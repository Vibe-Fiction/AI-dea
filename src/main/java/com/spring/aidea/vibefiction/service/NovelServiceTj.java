package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.novel.NovelCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.novel.NovelCreateResponseTj;
import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Genres;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.GenresRepository;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 소설(Novel)의 생성 및 관리에 대한 핵심 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Service
@RequiredArgsConstructor
public class NovelServiceTj {

    private final NovelsRepository novelsRepository;
    private final UsersRepository usersRepository;
    private final GenresRepository genresRepository;

    /**
     * [리팩토링] 신규 소설을 생성하고, 정책에 따라 첫 번째 회차(1화)를 함께 생성합니다.
     *
     * <p><b>[리팩토링 핵심 변경 사항]</b>
     * 장르 처리 방식이 변경되었습니다. 클라이언트로부터 Genre Enum의 이름(문자열) 목록을 받아,
     * 이를 {@link com.spring.aidea.vibefiction.entity.Genres.GenreType} Enum 목록으로 변환합니다.
     * 이후, 변환된 Enum 목록을 사용하여 DB에서 {@link Genres} 엔티티를 조회한 뒤 소설을 생성합니다.
     *
     * @param authorId 소설 작성자의 사용자 ID (인증을 통해 획득).
     * @param req      소설 생성에 필요한 모든 정보를 담은 {@link NovelCreateRequestTj}.
     * @return         생성된 소설의 ID와 첫 회차의 ID를 담은 {@link NovelCreateResponseTj}.
     * @throws BusinessException {@code USER_NOT_FOUND}: 작성자를 찾을 수 없는 경우.
     *                           <br>{@code INVALID_INPUT}: 요청에 포함된 장르 이름이 유효하지 않은 Genre Enum 상수일 경우.
     *                           <br>{@code RESOURCE_NOT_FOUND}: 요청된 장르가 DB에 존재하지 않을 경우.
     */
    @Transactional
    public NovelCreateResponseTj create(Long authorId, NovelCreateRequestTj req) {

        // [1. 선행 조건 검증: 연관 엔티티 조회]
        Users author = usersRepository.findById(authorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        /*
         * [리팩토링-AS-IS] 기존 장르 조회 로직 (ID 기반)
         * @author 왕택준
         *
         * [주석 처리 이유]
         * DTO의 입력 방식이 genre ID(Integer) 목록에서 genre 이름(String) 목록으로 변경됨에 따라,
         * ID로 직접 조회하던 이 로직은 더 이상 유효하지 않습니다.
         * 아래의 TO-BE 로직에서는 문자열을 Enum으로 변환하고, 변환된 Enum을 사용하여 이름으로 조회합니다.
         *
        List<Genres> genres = genresRepository.findAllById(req.getGenreIds());
        if (genres.size() != req.getGenreIds().size()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "일부 장르를 찾을 수 없습니다.");
        }
        */

        // [리팩토링-TO-BE] 새로운 장르 조회 로직 (Enum 이름 기반)
        // [2-1. 데이터 변환 및 검증: 장르 문자열을 GenreType Enum으로]
        List<Genres.GenreType> genreTypes;
        try {
            genreTypes = req.getGenres().stream()
                    .map(String::toUpperCase)
                    .map(Genres.GenreType::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            // Genres.GenreType.valueOf()는 존재하지 않는 Enum 상수를 변환하려 할 때 이 예외를 발생시킵니다.
            throw new BusinessException(ErrorCode.INVALID_INPUT, "유효하지 않은 장르가 포함되어 있습니다.");
        }

        // [2-2. DB 조회: 변환된 Enum으로 Genres 엔티티 목록 조회]
        List<Genres> genres = genresRepository.findByNameIn(genreTypes);
        // [비즈니스 규칙] 요청된 장르 개수와 DB에서 실제로 조회된 장르 엔티티 개수가 다르면,
        // Enum에는 정의되어 있으나 DB에는 아직 등록되지 않은 장르가 포함된 것으로 간주
        if (genres.size() != genreTypes.size()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "일부 장르는 아직 등록되지 않았습니다.");
        }

        // [3. 도메인 객체 생성 (DDD)]
        Novels novel = Novels.create(
                author,
                req.getTitle(),
                req.getSynopsis(),
                req.getVisibility(),
                genres
        );

        // [4. 비즈니스 규칙: 1화 동시 생성]
        Chapters firstChapter = Chapters.create(
                novel,
                author,
                req.getFirstChapterTitle(),
                req.getFirstChapterContent(),
                null
        );

        // [5. 연관관계 설정 (JPA Best Practice)]
        novel.addChapter(firstChapter);

        // [6. 영속화 (Cascade)]
        novelsRepository.save(novel);

        // [7. 결과 반환]
        return new NovelCreateResponseTj(novel.getNovelId(), firstChapter.getChapterId());
    }
}
