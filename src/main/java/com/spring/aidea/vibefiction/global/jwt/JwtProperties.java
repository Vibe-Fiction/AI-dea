package com.spring.aidea.vibefiction.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 설정 파일 읽기 - application.yml 의 jwt.xx 값을 읽어오는 클래스입니다.
 * @author 고동현
 */
@Setter @Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private Long expiration;
}
