package com.spring.aidea.vibefiction.dto.response.aiInteractionLog;

import lombok.*;

/**
 * AI의 '이어쓰기' 생성 결과를 클라이언트에게 전달하는 데이터 전송 객체(DTO)입니다.
 *
 * 사용자의 이어쓰기 요청에 따라 AI가 생성한 제안 제목과 내용을 담고 있습니다.
 * 클라이언트는 이 응답 데이터를 사용자에게 보여주고, 사용자는 이를 기반으로
 * 새로운 '이어쓰기 제안(Proposal)'을 생성할 수 있습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiContinueResponseTj {

    /**
     * 이번 AI 상호작용에 대한 고유 로그 ID입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 사용자가 이 AI 추천 내용을 '이어쓰기 제안'으로 등록할 경우,
     * 어떤 AI 요청으로부터 생성되었는지를 추적하기 위한 핵심 키(key)가 됩니다.
     *
     * @see com.spring.aidea.vibefiction.dto.request.proposal.ProposalCreateRequestTj
     * @see com.spring.aidea.vibefiction.entity.AiInteractionLog
     */
    private Long logId;

    /**
     * AI가 생성한 이어쓰기 내용의 추천 제목입니다.
     * <p>
     * <b>[사용 예시]</b> 클라이언트에서는 이 값을 제안 제목 입력 필드의
     * 기본값으로 사용자에게 보여줄 수 있습니다.
     */
    private String suggestedTitle;

    /**
     * AI가 생성한 이어쓰기 내용의 본문입니다.
     * <p>
     * <b>[핵심 데이터]</b> 사용자의 지시문을 바탕으로 생성된 실제 스토리 내용입니다.
     */
    private String suggestedContent;
}
