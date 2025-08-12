package com.spring.aidea.vibefiction.dto.request.aiInteractionLog;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** [TJ] 새 소설 보조 요청: 장르 + 시놉시스 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiRecommendNovelRequestTj {
    @NotBlank private String genre;
    @NotBlank private String synopsis;
}
