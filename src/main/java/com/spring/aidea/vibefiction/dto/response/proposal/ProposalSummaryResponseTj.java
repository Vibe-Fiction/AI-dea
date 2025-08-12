package com.spring.aidea.vibefiction.dto.response.proposal;

import lombok.*;
import java.time.LocalDateTime;

/** [TJ] 이어쓰기 제안 목록 응답용 요약 DTO */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProposalSummaryResponseTj {
    private Long proposalId;
    private String title;
    private Integer voteCount;
    private String status;           // "VOTING", "ADOPTED", ...
    private Boolean aiGenerated;
    private LocalDateTime createdAt;
}
