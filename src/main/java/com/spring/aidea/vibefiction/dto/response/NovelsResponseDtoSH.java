package com.spring.aidea.vibefiction.dto.response;
import com.spring.aidea.vibefiction.entity.Genres;
import com.spring.aidea.vibefiction.entity.NovelGenres;
import com.spring.aidea.vibefiction.entity.Novels;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovelsResponseDtoSH {

    Long novelId; // 소설 ID
    String title; // 소설 제목
    String author; // 소설 작성자
    String authorName;
    String coverImageUrl; // 소설 썸네일 이미지 경로
    String synopsis; // 소설 시놉시스(줄거리)
    String status; // 공개 상태
    Long viewCount; // 조회수
    List<String> novelGenres; // 소설 분류
    LocalDateTime laseUpdatedAt; // 최종 수정일시
    // 좋아요 추가해야함

    public static NovelsResponseDtoSH from(Novels novels) {

        return NovelsResponseDtoSH.builder()
                .novelId(novels.getNovelId())
                .title(novels.getTitle())
                .author(String.valueOf(novels.getAuthor().getUserId()))
                .authorName(String.valueOf(novels.getAuthor().getNickname()))
                .coverImageUrl(novels.getCoverImageUrl())
                .synopsis(novels.getSynopsis())
                .status(novels.getStatus().toString())
                .viewCount(novels.getViewCount())
                .novelGenres(novels.getNovelGenres()
                        .stream()
                        .map(NovelGenres::getGenre)
                        .map(Genres::getName)
                        .toList()
                )
                .laseUpdatedAt(novels.getLastUpdatedAt())

                .build();


    }


}
