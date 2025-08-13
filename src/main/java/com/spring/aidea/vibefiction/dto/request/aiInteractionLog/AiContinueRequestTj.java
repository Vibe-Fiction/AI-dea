package com.spring.aidea.vibefiction.dto.request.aiInteractionLog;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * AI를 통해 소설의 다음 내용을 이어쓰도록 요청할 때 사용되는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 사용자가 AI에게 원하는 스토리 전개 방향이나 특정 요구사항을 전달하는
 * '지시문(instruction)'을 담는 역할을 합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiContinueRequestTj {

    /**
     * AI가 이어쓰기 내용을 생성할 때 참고해야 할 지시문 또는 가이드라인입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 이 필드는 사용자가 AI에게 원하는 바를 명확히 전달하기 위한
     * 핵심 정보이므로 비어 있을 수 없습니다.
     * <p>
     * <b>[예시]</b> "주인공이 동굴에서 고대의 유물을 발견하는 장면을 묘사해줘."
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "AI에게 전달할 지시문은 비어 있을 수 없습니다.")
    private String instruction;
}
