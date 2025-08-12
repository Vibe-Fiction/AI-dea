package com.spring.aidea.vibefiction.dto.request.proposal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** [TJ] 이어쓰기 제안 생성 요청 DTO */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProposalCreateRequestTj {
    @NotBlank @Size(max = 60)
    private String title;

    @NotBlank @Size(max = 5000)
    private String content;

    // (선택) AI 추천을 반영하여 만든 제안인 경우 연결할 로그 ID
    private Long aiLogId;
}
