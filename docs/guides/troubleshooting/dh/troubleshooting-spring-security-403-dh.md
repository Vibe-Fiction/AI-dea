# 트러블슈팅 가이드 – Spring Security 기본 차단 문제

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 **Spring Security 기본 차단 문제**를 해결하는 과정에서의 시도, 실패, 그리고 최종 의사결정을 정리한 트러블슈팅 사례입니다.

**작성자:** [고동현](https://github.com/rhehdgus8831)

**작성일:** 2025년 8월 11일

**문서 버전:** v1.0

---

## 1. 문제 현상

* **에러 메시지**: Postman 호출 시 `403 Forbidden` 반환
* **콜 스택/로그**: 서버 로그에는 별다른 예외 없음
* **발생 상황**: 회원가입(`/api/auth/signup`), 로그인(`/api/auth/login`) API 호출 시 컨트롤러 로직 실행 전 차단
* **배경**: Spring Security 의존성 추가 후 발생

---

## 2. 원인 분석

* Spring Security의 **기본 정책**은 모든 요청에 대해 인증을 요구하는 방식.
* 인증이 필요 없는 경로라도 `permitAll()`로 명시적으로 열어주지 않으면 접근 불가.
* 따라서 회원가입 및 로그인 API가 인증 단계 이전에 차단되어 `403 Forbidden`이 발생.

---

## 3. 해결 과정

### 초기 시도

* 컨트롤러 단에 `@PermitAll` 어노테이션 적용을 고려했으나,

    * 개별 엔드포인트마다 지정해야 하는 관리 비용이 큼
    * 유지보수 시 누락 가능성이 존재

### 최종 해결책

* **전역 보안 설정(SecurityFilterChain)에서 경로 단위로 허용** 처리

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/auth/**", "/api/novels/**").permitAll() // 인증 없이 접근 허용
            .anyRequest().authenticated() // 나머지는 인증 필요
        );
    return http.build();
}
```

---

## 4. 결론 및 배운 점

* Spring Security는 **보안 우선 기본값**을 제공하므로, 반드시 인증 예외 경로를 명확히 지정해야 함.
* 초기 설정 단계에서 **열린 API vs 인증이 필요한 API**를 명확히 구분하면 이후 유지보수 비용이 줄어듦.
* 보안 정책은 기능 구현보다 **선제적으로 설계**해야 안정성과 생산성을 동시에 확보할 수 있음.
