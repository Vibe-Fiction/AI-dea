package com.spring.aidea.vibefiction.dto.request.aiInteractionLog;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** [TJ] 이어쓰기 보조 요청: 사용자 요구사항 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiContinueRequestTj {
    @NotBlank private String instruction;
}
