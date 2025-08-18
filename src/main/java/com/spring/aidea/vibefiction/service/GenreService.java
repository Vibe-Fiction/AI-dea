package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.genre.GenreResponse;
import com.spring.aidea.vibefiction.repository.GenresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 장르(Genre) 정보와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenresRepository genresRepository;

    /**
     * 데이터베이스에 저장된 모든 장르 정보를 조회하여 DTO 리스트로 변환 후 반환합니다.
     * <p>
     * 이 메서드는 읽기 전용 트랜잭션으로 동작하여 성능을 최적화합니다.
     *
     * @return 클라이언트에 전달될 {@link GenreResponse} DTO의 리스트.
     */
    @Transactional(readOnly = true)
    public List<GenreResponse> getAllGenres() {
        return genresRepository.findAll().stream()
            .map(genre -> GenreResponse.fromEnum(genre.getName()))
            .toList();
    }
}
