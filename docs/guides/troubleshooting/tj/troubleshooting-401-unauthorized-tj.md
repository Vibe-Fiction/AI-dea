# 트러블슈팅: Spring Security 인증 우회 URL 등록

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 Postman 등을 통한 API 테스트 시 `401 Unauthorized` 오류가 발생하는 문제의 원인과 해결 과정을 정리한 기술 문서입니다.

**작성자:** [왕택준](https://github.com/TJK98)

**작성일:** 2025년 8월 11일

**문서 버전:** v1.0

---

## 1. 문제 현상

- **에러 메시지**:
  ```
  401 Unauthorized
  ```
- **발생 상황**:
  - Postman에서 API 요청을 보낼 때 **로그인(인증) 절차 없이** 호출한 경우.
  - 예: `/api/novels` GET 요청 시.
- **프로젝트 설정**:
  - Spring Security가 모든 API 요청을 기본적으로 **인증 필요** 상태로 설정.
  - 로그인 토큰(JWT/세션) 미포함 시 요청이 차단됨.

---

## 2. 원인 분석

1. **Spring Security 전역 인증 활성화**
   - 현재 `SecurityConfig`에서 모든 요청에 대해 인증 절차를 거치도록 설정되어 있음.

2. **테스트 시 인증 토큰 부재**
   - Postman이나 브라우저에서 직접 호출 시, 로그인 토큰이 없으므로 Spring Security가 즉시 `401` 응답.

3. **예외 URL 미등록**
   - 특정 API를 인증 없이 호출하려면 `permitAll()` 설정이 필요하지만, 해당 URL이 등록되지 않은 상태.

---

## 3. 해결 과정

### 3-1. 임시 해결책: 인증 우회 URL 등록
`SecurityConfig` 클래스에 **permitAllURLs** 배열을 두고, 인증 없이 접근할 수 있는 URL을 추가.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 인증을 우회할 URL 목록
    String[] permitAllURLs = {
        "/api/novels/**" // novels API 전부 인증 없이 접근 가능
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(permitAllURLs).permitAll() // 배열에 등록된 URL은 모두 허용
                .anyRequest().authenticated() // 나머지는 인증 필요
            )
            .csrf(csrf -> csrf.disable()); // 테스트 편의상 CSRF 비활성화
        return http.build();
    }
}
```

- **적용 후 효과**:
  - `permitAllURLs` 배열에 등록된 패턴(`"/api/novels/**"`)에 매칭되는 모든 요청은 인증 절차 없이 호출 가능.
  - Postman에서 토큰 없이 테스트 가능.

---

## 4. 결론 및 배운 점

- **문제**: 전역 인증이 활성화된 상태에서 테스트를 위해 인증을 우회할 URL이 필요했음.
- **해결**: `SecurityConfig`에 **예외 URL 패턴**을 배열로 관리하여, 필요한 API만 인증을 우회하도록 설정.
- **배운 점**:
  1. 개발·테스트 환경에서는 생산성을 위해 인증 우회 경로를 두는 것이 유용하지만,
  2. 운영 환경에서는 반드시 제거하거나, 접근 권한을 엄격히 제한해야 함.
  3. URL 예외 목록을 **배열 변수로 관리**하면, 각 개발자가 손쉽게 추가·수정 가능.
