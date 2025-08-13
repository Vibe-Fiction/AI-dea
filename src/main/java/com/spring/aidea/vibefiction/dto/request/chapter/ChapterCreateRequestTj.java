package com.spring.aidea.vibefiction.dto.request.chapter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 특정 소설에 새로운 회차(Chapter)를 등록하기 위해 사용되는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 소설의 작가가 새로운 회차를 발행할 때 필요한
 * 회차의 제목(title)과 본문 내용(content) 정보를 담는 역할을 합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterCreateRequestTj {

    /**
     * 새로 생성할 회차의 제목입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 회차 목록에서 독자에게 노출되는 중요한 정보이며,
     * 최대 60자까지 입력할 수 있습니다.
     *
     * @see jakarta.validation.constraints.NotBlank
     * @see jakarta.validation.constraints.Size
     */
    @NotBlank(message = "회차 제목은 비어 있을 수 없습니다.")
    @Size(max = 60, message = "회차 제목은 60자를 초과할 수 없습니다.")
    private String title;

    /**
     * 새로 생성할 회차의 본문 내용입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 회차의 핵심적인 스토리 내용을 담고 있으며,
     * 데이터베이스 제약 조건에 따라 최대 5000자까지 저장할 수 있습니다.
     *
     * @see jakarta.validation.constraints.NotBlank
     * @see jakarta.validation.constraints.Size
     */
    @NotBlank(message = "회차 내용은 비어 있을 수 없습니다.")
    @Size(max = 5000, message = "회차 내용은 5000자를 초과할 수 없습니다.")
    private String content;
}
