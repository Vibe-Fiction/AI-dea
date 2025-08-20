알# 트러블슈팅 가이드 – JSON 파싱 예외 미처리

본 문서는**Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼)에서 **JSON 파싱 예외 미처리 문제**를 해결하는 과정에서의 시도, 실패, 그리고 최종 의사결정을 정리한 트러블슈팅 사례입니다.

**작성자:** [고동현](https://github.com/rhehdgus8831)

**작성일:** 2025년 8월 15일

---

## 1. 문제 현상

* **에러 메시지**

  * `@Valid` 검증 실패 → 커스텀 JSON 에러 정상 반환
  * JSON 파싱 실패(`"2000-1-1"`) → 스프링 기본 에러 응답 노출
* **콜 스택/로그**: `HttpMessageNotReadableException` 발생
* **발생 상황**: DTO 바인딩 전 JSON 파싱 실패 시
* **배경**: `MethodArgumentNotValidException` 핸들러만 존재

---

## 2. 원인 분석

* JSON 파싱 오류는 **DTO 생성 전 단계**에서 발생.
* `@Valid` 검증 오류는 **DTO 생성 후 단계**에서 발생.
* 기존 핸들러는 후자만 처리 가능, 전자를 처리할 핸들러가 없어 기본 에러 반환됨.

---

## 3. 해결 과정

### 초기 시도

* `@Valid` 핸들러 확장을 시도했으나, 예외 발생 시점이 달라 적용 불가.

### 최종 해결책

```java
@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    ErrorResponse response = ErrorResponse.of(
        "잘못된 JSON 형식",
        List.of("날짜 형식이 올바르지 않습니다.")
    );
    return ResponseEntity.badRequest().body(response);
}
```

---

## 4. 결론 및 배운 점

* 예외는 **발생 레이어와 시점**이 다르므로 각각 맞춤 핸들러 필요.
* 응답 형식을 일관되게 맞추려면 파싱 오류까지 커스텀 핸들러에서 처리해야 함.

---

> 본 문서는 Vibe Fiction 프로젝트에서 **JSON 파싱 예외 미처리 문제**를 해결하는 과정에서의 시도, 실패, 그리고 최종 의사결정을 정리한 트러블슈팅 사례입니다.
