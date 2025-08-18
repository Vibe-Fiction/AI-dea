package com.spring.aidea.vibefiction.global.config;


import com.spring.aidea.vibefiction.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스입니다.
 *
 * @author 고동현
 * <p>
 * 주석으로 설명이 달린 코드부분에
 * .requestMatchers("각자URL").permitAll() 을 추가해야 Spring security의 검증을 거치지 않고
 * postman 테스트가 가능합니다. 토큰 인증과 연동하기 전의 작업단계에서는 이부분에 작업중인 URL을
 * 추가해서 JWT 인증을 비활성화 하면 됩니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 검증 제외 할 api URL
    String[] apiURLs = {
        "/api/novels/**",
        "/api/genres",
        "/api/chapters/{chapterId}/proposals",
        "/api/auth/**",
        "/api/auth/signup",
        "/api/auth/login",
    };
    // 검증 제외 할 정적소스 (html,css,image,js) URL
    String[] wedPagesURLs = {
        "/",
        "/css/**",
        "/js/**",
        "/chapters",
        "/vote",
        "/novel",
        "/proposal",
        "/my-page",
        "/signup",
        "/img/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configure(http))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            //
            .authorizeHttpRequests(authorize -> authorize
                // 로그인 로직완성 후 토큰로직 연결되면 밑의 코드는 지워야함
                .requestMatchers(apiURLs)
                .permitAll()

                .requestMatchers(wedPagesURLs)
                .permitAll()
                // 다른 모든 요청은 인증 필요
                .anyRequest()
                .authenticated()
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        ;
        return http.build();
    }


}
