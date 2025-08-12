package com.spring.aidea.vibefiction.dto.response.aiInteractionLog;

import lombok.*;

/** [TJ] 이어쓰기 보조 응답 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiContinueResponseTj {
    private Long logId;
    private String suggestedTitle;
    private String suggestedContent;
}
