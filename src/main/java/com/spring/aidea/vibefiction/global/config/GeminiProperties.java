package com.spring.aidea.vibefiction.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * {@code application.yml} 파일의 {@code gemini.api} 하위 설정값들을
 * Java 객체로 안전하게 바인딩하기 위한 클래스입니다.
 * <p>
 * {@code @ConfigurationProperties} 어노테이션을 통해 Spring Boot가 자동으로
 * 해당 경로의 프로퍼티들을 이 클래스의 필드에 주입해줍니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gemini.api")
@Validated
public class GeminiProperties {

    /**
     * Gemini API를 호출할 때 사용되는 API 키입니다.
     * <p>
     * <b>[보안]</b> 이 값은 민감 정보이므로, {@code application-template.yml} 또는
     * 환경 변수를 통해 주입받아야 합니다.
     */
    private String key;

    /**
     * Gemini API를 호출하기 위한 전체 Endpoint URL입니다.
     * RestTemplate에서 사용되며, 경로 변수({apiKey})를 포함할 수 있습니다.
     */
    private String url;
}
