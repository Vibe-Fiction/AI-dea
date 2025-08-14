package com.spring.aidea.vibefiction.dto.response.proposal;

import lombok.*;
import java.time.LocalDateTime;

/**
 * '이어쓰기 제안' 목록을 클라이언트에게 효율적으로 전달하기 위해 사용되는 요약 정보 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 제안의 전체 내용(content)을 제외한 핵심 메타데이터만을 포함하여,
 * 목록 조회 API의 응답 데이터 크기를 최소화하고 로딩 성능을 향상시키는 것을 목적으로 합니다.
 * 클라이언트는 이 정보를 사용하여 사용자에게 제안 목록을 보여주고, 투표나 상세 조회와 같은 상호작용을 유도할 수 있습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSummaryResponseTj {

    /**
     * 제안의 고유 식별자(Primary Key)입니다.
     * <p>
     * <b>[사용 예시]</b> 사용자가 목록에서 특정 제안을 클릭하여 상세 내용을 보거나 투표를 할 때,
     * 이 ID를 서버에 전송하여 대상을 지정하는 데 사용됩니다.
     */
    private Long proposalId;

    /**
     * 제안의 제목입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 목록에서 각 제안을 시각적으로 구분하는 주요 텍스트 정보입니다.
     */
    private String title;

    /**
     * 이 제안이 받은 총 투표 수입니다.
     * <p>
     * <b>[사용 예시]</b> 클라이언트는 이 값을 표시하여 어떤 제안이 가장 인기 있는지
     * 사용자에게 보여줄 수 있으며, '인기순' 정렬의 기준이 됩니다.
     */
    private Integer voteCount;

    /**
     * 제안의 현재 진행 상태를 나타내는 문자열입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 이 상태에 따라 클라이언트의 UI/UX가 달라질 수 있습니다.
     * <ul>
     *   <li>{@code VOTING}: 투표 진행 중. 사용자가 투표 가능.</li>
     *   <li>{@code ADOPTED}: 채택됨. 소설의 정식 내용으로 편입됨을 의미.</li>
     *   <li>{@code REJECTED}: 거절됨. 채택되지 않음.</li>
     * </ul>
     */
    private String status;

    /**
     * 이 제안이 AI에 의해 생성되었는지 여부를 나타내는 플래그입니다.
     * <p>
     * <b>[사용 예시]</b> 클라이언트는 이 값을 기반으로 'AI' 아이콘 등을 표시하여,
     * 제안의 출처가 AI임을 사용자에게 명확히 알려줄 수 있습니다.
     */
    private Boolean aiGenerated;

    /**
     * 제안이 시스템에 등록된 시각입니다.
     * <p>
     * <b>[사용 예시]</b> '최신순' 정렬의 기준이 되거나, "방금 전", "3일 전"과 같이
     * 상대 시간으로 표시하여 제안의 최신성을 나타내는 데 사용됩니다.
     */
    private LocalDateTime createdAt;
}
