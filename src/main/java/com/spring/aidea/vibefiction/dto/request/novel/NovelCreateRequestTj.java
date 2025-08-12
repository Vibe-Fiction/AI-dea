package com.spring.aidea.vibefiction.dto.request.novel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** [TJ] 새 소설 생성 요청 DTO */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NovelCreateRequestTj {
    @NotBlank @Size(max = 50)
    private String title;

    @Size(max = 2000)
    private String synopsis;

    // 선택: 1화 동시 생성 (AI 추천 결과를 그대로 사용할 때)
    @Size(max = 60)
    private String firstChapterTitle;

    @Size(max = 5000)
    private String firstChapterContent;
}
