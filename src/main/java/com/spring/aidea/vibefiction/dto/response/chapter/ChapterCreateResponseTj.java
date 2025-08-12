package com.spring.aidea.vibefiction.dto.response.chapter;

import lombok.*;

/** [TJ] 회차 생성 응답 DTO */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChapterCreateResponseTj {
    private Long chapterId;
    private Integer chapterNumber;
}
