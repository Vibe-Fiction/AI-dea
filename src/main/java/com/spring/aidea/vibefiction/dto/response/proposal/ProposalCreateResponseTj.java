package com.spring.aidea.vibefiction.dto.response.proposal;

import lombok.*;

/**
 * 새로운 '이어쓰기 제안(Proposal)' 생성이 성공적으로 완료되었을 때, 클라이언트에게 그 결과를 전달하는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 새로 생성된 제안의 고유 식별자(ID)를 제공하여, 클라이언트가 방금 생성한 제안을
 * UI에 즉시 반영하거나 상세 보기 페이지로 이동하는 등 후속 작업을 수행할 수 있도록 돕습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalCreateResponseTj {

    /**
     * 새롭게 생성된 이어쓰기 제안의 고유 식별자(Primary Key)입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 이 ID는 데이터베이스에서 제안을 유일하게 식별하는 값이며,
     * 향후 이 제안에 대해 투표를 하거나, 상세 내용을 조회하는 등 모든 후속 작업의 기준이 되는 핵심 키입니다.
     */
    private Long proposalId;
    private Long novelId;
}
