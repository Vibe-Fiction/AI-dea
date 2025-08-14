package com.spring.aidea.vibefiction.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 API와의 HTTP 통신을 위해 RestTemplate을 Spring Bean으로 등록하는 설정 클래스입니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate 객체를 생성하여 Bean으로 등록합니다.
     * 이 Bean은 프로젝트 내의 다른 서비스(@Service)에서 의존성 주입(@Autowired 또는 생성자 주입)을 통해
     * 간편하게 재사용될 수 있습니다.
     *
     * @return 프로젝트 전역에서 사용할 RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
