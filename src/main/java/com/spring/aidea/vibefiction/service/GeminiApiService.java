package com.spring.aidea.vibefiction.service;

/**
 * 외부 생성형 AI 모델과의 통신을 위한 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 실제 구현 기술(Gemini, OpenAI 등)과 비즈니스 로직을 분리하는 역할을 합니다.
 * 이를 통해 향후 다른 AI 서비스로 교체하더라도, 이 인터페이스를 사용하는 서비스(e.g., AiAssistServiceTj)의
 * 코드 변경을 최소화할 수 있습니다. (OCP: 개방-폐쇄 원칙)
 *
 * @author 왕택준
 * @since 2025.08
 */
public interface GeminiApiService {

    /**
     * AI 모델에 프롬프트를 전송하고, 생성된 텍스트 응답을 받아옵니다.
     *
     * @param prompt AI에게 전달할 질문 또는 지시문
     * @return AI가 생성한 순수 텍스트 콘텐츠
     */
    String generateContent(String prompt);
}
