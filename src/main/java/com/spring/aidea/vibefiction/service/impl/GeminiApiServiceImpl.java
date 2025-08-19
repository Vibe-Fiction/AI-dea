package com.spring.aidea.vibefiction.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.spring.aidea.vibefiction.global.config.GeminiProperties;
import com.spring.aidea.vibefiction.service.GeminiApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * GeminiApiService 인터페이스의 RestTemplate 기반 구현 클래스입니다.
 * <p>
 * API 키를 URL 파라미터로 직접 전달하여 Gemini API와 HTTP 통신을 수행합니다.
 * 이 방식은 gcloud CLI 인증 없이, API 키만으로 간단하게 연동할 수 있는 장점이 있습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiApiServiceImpl implements GeminiApiService {

    private final RestTemplate restTemplate;
    private final GeminiProperties geminiProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateContent(String prompt) {
        // 1. HTTP 헤더 생성 (Content-Type: application/json)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. HTTP 요청 Body 생성 (Gemini API가 요구하는 JSON 형식)
        Map<String, Object> parts = Map.of("text", prompt);
        Map<String, Object> contents = Map.of("parts", Collections.singletonList(parts));
        Map<String, Object> requestBody = Map.of("contents", Collections.singletonList(contents));

        // 3. HttpEntity 객체로 헤더와 바디를 캡슐화
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 4. RestTemplate을 사용하여 POST 요청 전송 및 응답 받기
        try {
            log.info("Gemini API (RestTemplate) 호출 시작. URL: {}", geminiProperties.getUrl());
            // API URL과 API 키를 restTemplate.postForEntity에 직접 전달
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                geminiProperties.getUrl(),
                requestEntity,
                JsonNode.class,
                geminiProperties.getKey() // URL의 ?key={apiKey} 부분을 이 값으로 치환
            );

            // 5. JsonNode를 사용한 안전한 응답 파싱
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = response.getBody();
                // Gemini 응답 구조: candidates -> [0] -> content -> parts -> [0] -> text
                String generatedText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
                log.info("Gemini API로부터 성공적으로 응답을 받았습니다.");
                return generatedText;
            } else {
                log.error("Gemini API로부터 비정상 응답을 받았습니다. 상태 코드: {}", response.getStatusCode());
                throw new RuntimeException("AI 서비스로부터 비정상 응답을 받았습니다.");
            }

        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생", e);
            throw new RuntimeException("AI 서비스 호출에 실패했습니다.", e);
        }
    }
}
