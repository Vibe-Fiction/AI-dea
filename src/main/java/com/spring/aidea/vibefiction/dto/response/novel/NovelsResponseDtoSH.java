package com.spring.aidea.vibefiction.dto.response.novel;

import com.spring.aidea.vibefiction.entity.Genres;
import com.spring.aidea.vibefiction.entity.NovelGenres;
import com.spring.aidea.vibefiction.entity.Novels;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 소설 목록 또는 상세 정보 조회 시 사용될 데이터 전송 객체(DTO)입니다.
 *
 * @author SH (Original Author)
 * @author 왕택준 (Refactored by)
 * @since 2025.08
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovelsResponseDtoSH {

    /** 소설의 고유 식별자(ID) */
    private Long novelId;

    /** 소설의 제목 */
    private String title;

    /** 소설 원작자의 고유 식별자(ID) */
    private Long authorId;

    /** 소설 원작자의 닉네임 */
    private String authorName;

    /** 소설 표지 이미지의 URL */
    private String coverImageUrl;

    /** 소설의 시놉시스 (줄거리 요약) */
    private String synopsis;

    /** 소설의 현재 연재 상태 (e.g., "ONGOING", "COMPLETED") */
    private String status;

    /** 소설의 총 조회수 */
    private Long viewCount;

    /** 소설에 부여된 장르 목록 (Enum 상수명, e.g., "FANTASY", "ROMANCE") */
    private List<String> genres;

    /** 소설이 마지막으로 업데이트된 일시 */
    private LocalDateTime lastUpdatedAt;


    /**
     * Novels 엔티티를 클라이언트에 전달할 DTO 형태로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param novels 변환할 원본 Novels 엔티티 객체
     * @return 변환된 NovelsResponseDtoSH 객체
     */
    public static NovelsResponseDtoSH from(Novels novels) {

        return NovelsResponseDtoSH.builder()
            .novelId(novels.getNovelId())
            .title(novels.getTitle())
            .authorId(novels.getAuthor().getUserId())
            .authorName(novels.getAuthor().getNickname())
            .coverImageUrl(novels.getCoverImageUrl())
            .synopsis(novels.getSynopsis())
            .status(novels.getStatus().name())
            .viewCount(novels.getViewCount())
            .genres(novels.getNovelGenres()
                .stream()
                .map(NovelGenres::getGenre)
                .map(Genres::getName)
                .map(Genres.GenreType::getDescription) // 한글 설명을 가져오도록 변경
                .collect(Collectors.toList())
            )
            .lastUpdatedAt(novels.getLastUpdatedAt())
            .build();
    }
}
