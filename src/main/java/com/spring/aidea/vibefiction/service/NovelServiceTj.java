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

/**
 * 소설(Novel)의 생성 및 관리에 대한 핵심 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * 이 서비스는 여러 Repository와 도메인 객체를 조율(Orchestrating)하여,
 * 소설과 첫 회차를 하나의 트랜잭션으로 안전하게 생성하는 책임을 가집니다.
 * <p>
 * <b>[설계 원칙]</b>
 * 도메인 주도 설계(DDD)에 따라, 엔티티 생성과 관련된 핵심 로직(예: 유효성 검증, 초기값 설정)은
 * 각 도메인 엔티티의 정적 팩토리 메서드에 위임합니다. 서비스는 이러한 도메인 객체들을 사용하여
 * 전체 비즈니스 프로세스를 관리하는 'Transaction Script' 패턴의 역할을 수행합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Service
@RequiredArgsConstructor
public class NovelServiceTj {

    /** 생성된 소설과 회차를 영속화하기 위해 사용됩니다. (Cascade 설정으로 소설만 저장해도 회차가 함께 저장됩니다.) */
    private final NovelsRepository novelsRepository;
    /** 소설의 작성자(author)가 유효한 사용자인지 검증하고 엔티티를 조회하기 위해 사용됩니다. */
    private final UsersRepository usersRepository;
    /** 요청에 포함된 장르 ID들이 유효한지 검증하고, 소설과 연결할 장르 엔티티 목록을 조회하기 위해 사용됩니다. */
    private final GenresRepository genresRepository;

    /**
     * 신규 소설을 생성하고, 정책에 따라 첫 번째 회차(1화)를 함께 생성합니다.
     * <p>
     * 이 메서드는 하나의 트랜잭션으로 동작하여 데이터의 정합성을 보장합니다.
     * 엔티티 생성 책임은 각 도메인 클래스의 정적 팩토리 메서드로 위임하여 도메인의 응집도를 높입니다.
     * <p>
     * <b>[핵심 로직 흐름]</b>
     * <ol>
     *  <li>작성자({@link Users})와 장르({@link Genres}) 엔티티를 조회하여 유효성을 검증합니다.</li>
     *  <li>{@link Novels#create} 정적 팩토리 메서드를 호출하여 소설 엔티티를 생성합니다.</li>
     *  <li>{@link Chapters#create} 정적 팩토리 메서드를 호출하여 1화 엔티티를 생성합니다.</li>
     *  <li>{@link Novels#addChapter} 연관관계 편의 메서드를 통해 소설과 1화의 양방향 관계를 설정합니다.</li>
     *  <li>JPA의 영속성 전이(Cascade) 기능을 통해 소설만 저장해도 1화가 함께 저장되도록 합니다.</li>
     * </ol>
     *
     * @param authorId 소설 작성자의 사용자 ID (인증을 통해 획득).
     * @param req      소설 생성에 필요한 모든 정보를 담은 {@link NovelCreateRequestTj}.
     * @return         생성된 소설의 ID와 첫 회차의 ID를 담은 {@link NovelCreateResponseTj}.
     * @throws BusinessException {@code USER_NOT_FOUND}: 작성자를 찾을 수 없는 경우.
     *                           <br>{@code RESOURCE_NOT_FOUND}: 요청에 포함된 장르 ID 중 일부가 유효하지 않을 경우.
     */
    @Transactional
    public NovelCreateResponseTj create(Long authorId, NovelCreateRequestTj req) {
        // [1. 선행 조건 검증: 연관 엔티티 조회]
        Users author = usersRepository.findById(authorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Genres> genres = genresRepository.findAllById(req.getGenreIds());
        // [비즈니스 규칙] 요청된 장르 ID 개수와 DB에서 실제로 조회된 장르 엔티티 개수가 다르면, 유효하지 않은 ID가 포함된 것으로 간주
        if (genres.size() != req.getGenreIds().size()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "일부 장르를 찾을 수 없습니다.");
        }

        // [2. 도메인 객체 생성 (DDD)] 엔티티 생성 책임을 각 도메인의 정적 팩토리 메서드에 위임
        Novels novel = Novels.create(
                author,
                req.getTitle(),
                req.getSynopsis(),
                req.getVisibility(),
                genres
        );

        // [3. 비즈니스 규칙: 1화 동시 생성]
        // 소설 생성 시 1화는 필수 정책이며, 제안에서 생성된 것이 아니므로 fromProposal은 null로 전달
        Chapters firstChapter = Chapters.create(
                novel,
                author,
                req.getFirstChapterTitle(),
                req.getFirstChapterContent(),
                null
        );

        // [4. 연관관계 설정 (JPA Best Practice)] 연관관계 편의 메서드를 호출하여 양방향 관계의 일관성을 보장
        novel.addChapter(firstChapter);

        // [5. 영속화 (Cascade)] 소설을 저장합니다. Chapters는 Novels의 CascadeType.ALL(또는 PERSIST) 설정에 의해 자동으로 함께 영속화됩니다.
        novelsRepository.save(novel);

        // [6. 결과 반환] 클라이언트에게 생성된 리소스의 고유 ID들을 전달
        return new NovelCreateResponseTj(novel.getNovelId(), firstChapter.getChapterId());
    }
}
