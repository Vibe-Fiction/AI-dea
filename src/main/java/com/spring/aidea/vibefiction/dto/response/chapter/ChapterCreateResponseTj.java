package com.spring.aidea.vibefiction.dto.response.chapter;

import lombok.*;

/**
 * 새로운 회차(Chapter) 생성이 성공적으로 완료되었을 때, 클라이언트에게 그 결과를 전달하는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 새로 생성된 회차의 고유 식별자(ID)와 소설 내 순서 정보를 제공하여,
 * 클라이언트가 후속 작업(예: 생성된 회차 페이지로 이동)을 수행할 수 있도록 돕습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterCreateResponseTj {

    /**
     * 새롭게 생성된 회차의 고유 식별자(Primary Key)입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 이 ID는 데이터베이스에서 회차를 유일하게 식별하는 값이며,
     * 향후 이 회차를 조회, 수정, 삭제하거나 관련 데이터를(예: 제안 목록) 요청할 때 사용되는 핵심 키입니다.
     */
    private Long chapterId;

    /**
     * 해당 소설 내에서 이 회차가 몇 번째인지를 나타내는 순서 번호입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 이 값은 서버에서 자동으로 계산되며, 보통 1부터 시작하여 순차적으로 증가합니다. (1-based index)
     * <p>
     * <b>[사용 예시]</b> 클라이언트는 이 값을 화면에 '3화', '15화'와 같이 표시하여
     * 독자가 소설의 진행 순서를 쉽게 파악하도록 할 수 있습니다.
     */
    private Integer chapterNumber;
}
