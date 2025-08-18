package com.spring.aidea.vibefiction.service; // 또는 실제 파일의 패키지 경로

import com.spring.aidea.vibefiction.entity.Genres;
import com.spring.aidea.vibefiction.repository.GenresRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Boot 애플리케이션 시작 시점에 초기 데이터를 데이터베이스에 자동으로 삽입하는 클래스입니다.
 * <p>
 * 이 클래스는 {@link CommandLineRunner}를 구현하여, 애플리케이션이 완전히 구동된 후
 * {@code run} 메서드를 자동으로 실행합니다. 주로 애플리케이션 운영에 필수적인 마스터 데이터(e.g., 장르, 기본 역할)를
 * 검증하고, 누락된 경우 자동으로 추가하여 모든 개발 및 운영 환경에서 데이터의 일관성을 보장하는 역할을 합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GenresRepository genresRepository;

    /**
     * 애플리케이션 시작 시 실행되는 메인 로직입니다.
     * <p>
     * 현재 코드에 정의된 {@link com.spring.aidea.vibefiction.entity.Genres.GenreType} Enum의
     * 모든 상수가 데이터베이스의 {@code genres} 테이블에 존재하는지 확인하고,
     * 존재하지 않는 장르만 골라서 자동으로 {@code INSERT} 합니다.
     * 이를 통해, DB 데이터 상태와 상관없이 항상 최신 장르 목록과의 동기화를 보장합니다.
     *
     * @param args 애플리케이션 실행 시 전달된 커맨드 라인 인자
     * @throws Exception 실행 중 발생할 수 있는 예외
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("데이터 초기화 및 동기화 작업을 시작합니다...");

        // 1. 코드(Enum)에 정의된 모든 장르 목록('정답지')을 가져옵니다.
        Set<Genres.GenreType> expectedGenres = Arrays.stream(Genres.GenreType.values())
            .collect(Collectors.toSet());

        // 2. 데이터베이스에 현재 저장된 모든 장르 목록('현실')을 가져옵니다.
        Set<Genres.GenreType> existingGenres = genresRepository.findAll().stream()
            .map(Genres::getName)
            .collect(Collectors.toSet());

        // 3. '정답지'에는 있지만 '현실'에는 없는, 즉 '누락된' 장르만 필터링합니다.
        List<Genres> newGenresToSave = expectedGenres.stream()
            .filter(genreType -> !existingGenres.contains(genreType))
            .map(genreType -> Genres.builder().name(genreType).build())
            .toList();

        // 4. 누락된 장르가 있을 경우에만 DB에 저장합니다.
        if (!newGenresToSave.isEmpty()) {
            log.info("DB에 누락된 {}개의 기본 장르 데이터를 삽입합니다: {}", newGenresToSave.size(), newGenresToSave.stream().map(Genres::getName).toList());
            genresRepository.saveAll(newGenresToSave);
            log.info("누락된 장르 데이터 삽입 완료.");
        } else {
            log.info("모든 장르 데이터가 이미 DB에 존재하므로, 동기화를 건너뜁니다.");
        }

        log.info("데이터 초기화 및 동기화 작업이 완료되었습니다.");
    }
}
