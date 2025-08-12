package com.spring.aidea.vibefiction.dto.request.chapter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** [TJ] 회차 생성 요청 DTO */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChapterCreateRequestTj {
    @NotBlank @Size(max = 60)
    private String title;

    @NotBlank @Size(max = 5000)
    private String content;
}
