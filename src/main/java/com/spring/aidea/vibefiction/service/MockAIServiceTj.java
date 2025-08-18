/*
package com.spring.aidea.vibefiction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

*/
/**
 * 외부 AI API와의 실제 통신을 모방(imitating)하는 모의(Mock) 서비스 클래스입니다.
 *
 * 이 클래스는 개발 및 테스트 단계에서 외부 API의 가용성, 네트워크 지연, 비용 문제에 구애받지 않고
 * 항상 예측 가능한 테스트 데이터를 반환하도록 설계되었습니다. 이를 통해 AI 연관 비즈니스 로직을
 * 안정적이고 독립적으로 검증할 수 있습니다.
 * <p>
 * <b>[설계]</b>
 * 의존성 주입(DI)을 통해 {@link AiAssistServiceTj}에서 이 Mock 서비스를 실제 AI 서비스 구현체로
 * 손쉽게 교체할 수 있도록 인터페이스 기반으로 설계하는 것을 고려할 수 있습니다.
 *
 * @author 왕택준
 * @since 2025.08
 *//*

@Service
public class MockAIServiceTj {

    */
/** 모의 응답 데이터(Map)를 실제 AI 응답과 동일한 JSON 문자열 형식으로 직렬화하기 위해 사용됩니다. *//*

    private final ObjectMapper objectMapper = new ObjectMapper();

    */
/**
     * '새 소설 추천' 시나리오를 위한 예측 가능한 모의 응답 데이터를 생성합니다.
     * <p>
     * <b>[모의 동작]</b>
     * 이 메서드는 전달받은 {@code prompt}의 내용과 상관없이 항상 동일한, 미리 정의된
     * 소설 추천 데이터를 JSON 문자열 형태로 반환합니다. 프롬프트 내용은 디버깅 목적으로 콘솔에 출력됩니다.
     *
     * @param prompt {@link AiAssistServiceTj}로부터 전달받은 AI 요청 프롬프트. (현재 구현에서는 내용에 영향을 주지 않음)
     * @return 하드코딩된 소설 추천 정보가 담긴 JSON 형식의 응답 문자열.
     *//*

    public String getNovelRecommendation(String prompt) {
        // [디버깅용] 실제 AI 연동 시, 이 프롬프트가 잘 생성되는지 확인하기 위한 로그
        System.out.println("--- [Mock AI TJ] 새 소설 추천 프롬프트 ---");
        System.out.println(prompt);
        System.out.println("--------------------------------------");

        // [모의 데이터] AI가 생성했을 법한 가상의 응답 데이터를 정의
        String dummyContent = """
                어둠이 내려앉은 도시, 주인공 '레이'는 자신의 손에서 뿜어져 나오는 푸른 빛을 보며 경악했다. 이것은 시작에 불과했다...
                """;

        Map<String, String> dummyResponse = Map.of(
                "novelTitle", "AI가 추천하는: 운명의 그림자",
                "firstChapterTitle", "제1화: 깨어난 힘",
                "firstChapterContent", dummyContent.stripIndent()
        );

        try {
            return objectMapper.writeValueAsString(dummyResponse);
        } catch (JsonProcessingException e) {
            // [예외 처리] Mock 서비스에서는 테스트 흐름이 끊기지 않도록 빈 JSON 객체를 반환. 실제 서비스에서는 더 정교한 예외 처리가 필요.
            return "{}";
        }
    }

    */
/**
     * '이어쓰기 제안' 시나리오를 위한 예측 가능한 모의 응답 데이터를 생성합니다.
     * <p>
     * <b>[모의 동작]</b>
     * 이 메서드는 전달받은 {@code prompt}의 내용과 상관없이 항상 동일한, 미리 정의된
     * 이어쓰기 제안 데이터를 JSON 문자열 형태로 반환합니다. 프롬프트 내용은 디버깅 목적으로 콘솔에 출력됩니다.
     *
     * @param prompt {@link AiAssistServiceTj}로부터 전달받은 AI 요청 프롬프트. (현재 구현에서는 내용에 영향을 주지 않음)
     * @return 하드코딩된 이어쓰기 제안 정보가 담긴 JSON 형식의 응답 문자열.
     *//*

    public String getContinueProposal(String prompt) {
        // [디버깅용] 실제 AI 연동 시, 이 프롬프트가 잘 생성되는지 확인하기 위한 로그
        System.out.println("--- [Mock AI TJ] 이어쓰기 제안 프롬프트 ---");
        System.out.println(prompt);
        System.out.println("----------------------------------------");

        // [모의 데이터] AI가 생성했을 법한 가상의 응답 데이터를 정의
        String dummyContent = """
                레이는 어둠 속에서 들려오는 목소리에 귀를 기울였다. '도움이 필요한가, 젊은이?' 그곳에는 한쪽 눈에 안대를 한 노인이 서 있었다. 그의 등장은 새로운 파란을 예고했다.
                """;

        Map<String, String> dummyResponse = Map.of(
                "suggestedTitle", "AI가 제안하는: 새로운 만남",
                "suggestedContent", dummyContent.stripIndent()
        );

        try {
            return objectMapper.writeValueAsString(dummyResponse);
        } catch (JsonProcessingException e) {
            // [예외 처리] Mock 서비스에서는 테스트 흐름이 끊기지 않도록 빈 JSON 객체를 반환.
            return "{}";
        }
    }
}
*/
