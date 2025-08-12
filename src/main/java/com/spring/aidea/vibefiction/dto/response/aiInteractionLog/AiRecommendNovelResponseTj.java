package com.spring.aidea.vibefiction.dto.response.aiInteractionLog;

import lombok.*;

/** [TJ] 새 소설 보조 응답 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiRecommendNovelResponseTj {
    private Long logId;                // AI_INTERACTION_LOGS.log_id
    private String novelTitle;
    private String firstChapterTitle;
    private String firstChapterContent;
}
