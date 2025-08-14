package com.spring.aidea.vibefiction.dto.response.novel;

import lombok.*;

/**
 * 새로운 소설 작품 생성이 성공적으로 완료되었을 때, 클라이언트에게 그 결과를 전달하는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 새로 생성된 소설의 고유 식별자(ID)를 제공하여 클라이언트가 후속 작업(예: 생성된 소설 상세 페이지로 이동)을
 * 수행할 수 있도록 돕습니다. 요청 시 첫 회차 정보가 함께 제공되었다면, 해당 회차의 ID도 포함됩니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovelCreateResponseTj {

    /**
     * 새롭게 생성된 소설의 고유 식별자(Primary Key)입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 이 ID는 데이터베이스에서 소설을 유일하게 식별하는 값이며,
     * 향후 이 소설을 조회하거나 회차를 추가하는 등 모든 후속 작업의 기준이 되는 핵심 키입니다.
     */
    private Long novelId;

    /**
     * 소설 생성과 동시에 첫 번째 회차(1화)가 함께 생성된 경우, 해당 회차의 고유 식별자입니다. (선택적 반환)
     * <p>
     * <b>[비즈니스 규칙]</b> 소설 생성 요청({@link com.spring.aidea.vibefiction.dto.request.novel.NovelCreateRequestTj})에
     * 1화 정보가 포함되지 않았다면 이 필드는 {@code null}이 됩니다.
     * <p>
     * <b>[사용 예시]</b> 클라이언트는 이 ID를 사용하여, 소설 생성 직후 독자를 바로 1화 읽기 페이지로 안내할 수 있습니다.
     */
    private Long firstChapterId;
}
