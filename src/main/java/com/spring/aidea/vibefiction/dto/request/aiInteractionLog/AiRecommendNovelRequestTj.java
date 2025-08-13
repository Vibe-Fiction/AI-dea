package com.spring.aidea.vibefiction.dto.request.aiInteractionLog;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * AI를 통해 새로운 소설을 추천받기 위해 사용되는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 사용자가 원하는 소설의 장르(genre)와 핵심 줄거리(synopsis)를 AI에 전달하여,
 * 이를 기반으로 새로운 소설의 제목과 첫 회차의 초안을 생성하도록 요청하는 데 사용됩니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecommendNovelRequestTj {

    /**
     * 추천받고 싶은 소설의 장르입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> AI가 추천 결과물의 분위기와 특성을 결정하는 핵심 요소이므로
     * 비어 있을 수 없습니다.
     * <p>
     * <b>[예시]</b> "로맨스 판타지", "현대 퓨전 무협"
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "소설 장르는 비어 있을 수 없습니다.")
    private String genre;

    /**
     * 추천받고 싶은 소설의 핵심 줄거리(시놉시스)입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> AI가 이 내용을 바탕으로 구체적인 스토리와 제목을 구상하므로
     * 비어 있을 수 없습니다.
     * <p>
     * <b>[예시]</b> "평범한 회사원이 퇴근길에 이세계로 소환되어 전설의 검을 찾는 이야기"
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "소설 시놉시스는 비어 있을 수 없습니다.")
    private String synopsis;
}
