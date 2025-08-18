package com.spring.aidea.vibefiction.dto.request.proposal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 소설의 특정 회차에 대한 새로운 '이어쓰기 제안'을 등록하기 위해 사용되는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 독자가 다음 이야기에 대한 자신의 아이디어를 제안할 때 필요한
 * 제안의 제목(title)과 내용(content)을 담는 역할을 합니다. AI의 도움을 받아 제안을 작성한 경우,
 * 관련 AI 상호작용 로그 ID(aiLogId)를 함께 전달하여 출처를 명시할 수 있습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalCreateRequestTj {

    /**
     * 새로 생성할 이어쓰기 제안의 제목입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 제안 목록에서 다른 제안들과 구분되는 식별자 역할을 합니다.
     * 비어 있을 수 없으며, 최대 60자까지 입력 가능합니다.
     */
    @NotBlank(message = "제안 제목은 비어 있을 수 없습니다.")
    @Size(max = 60, message = "제안 제목은 60자를 초과할 수 없습니다.")
    private String title;

    /**
     * 새로 생성할 이어쓰기 제안의 본문 내용입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 제안의 핵심적인 아이디어를 담고 있으며, 비어 있을 수 없습니다.
     * 최대 5000자까지 입력 가능합니다.
     */
    @NotBlank(message = "제안 내용은 비어 있을 수 없습니다.")
    @Size(max = 5000, message = "제안 내용은 5000자를 초과할 수 없습니다.")
    private String content;

    /**
     * 이 제안이 AI의 추천을 기반으로 작성되었을 경우, 해당 AI 상호작용 로그의 고유 ID입니다. (선택 사항)
     * <p>
     * <b>[비즈니스 규칙]</b> AI가 생성한 내용을 사용자가 그대로 또는 수정하여 제안으로 등록하는 경우,
     * 해당 내용의 출처를 추적하기 위해 사용됩니다. 사용자가 직접 작성한 경우에는 null 입니다.
     *
     * @see com.spring.aidea.vibefiction.entity.AiInteractionLog
     */
    private Long aiLogId;
}
