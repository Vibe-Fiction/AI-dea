# 트러블슈팅 가이드 – 커스텀 예외 처리 실패

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 발생한 **커스텀 예외 처리 실패 문제**를 해결하는 과정에서의 시도, 실패, 그리고 최종 의사결정을 정리한 트러블슈팅 사례입니다.

**작성자:** [고동현](https://github.com/rhehdgus8831)

**작성일:** 2025년 8월 15일

**문서 버전:** v1.0

---

## 1. 문제 상황

### 1.1 발생한 문제

* Postman 응답이 커스텀 JSON 에러가 아닌 기본 **500 Internal Server Error** 반환됨
* 콘솔 로그에는 `BusinessException` 발생 로그 확인됨
* 중복 아이디 회원가입 시 다음 코드 실행:

  ```java
  throw new BusinessException("에러 메시지");
  ```
* 예외 처리기가 존재했으나 정상 동작하지 않음

### 1.2 문제 원인

* `BusinessException` 생성자에 문자열만 전달 → `ErrorCode` 필드가 `null` 상태
* 예외 처리기 내부에서 다음 코드 실행:

  ```java
  exception.getErrorCode().getStatus()
  ```

  → `NullPointerException` 발생
* 결국 커스텀 에러 처리 실패, 서버 기본 응답(500)으로 대체됨

---

## 2. 증상 확인

* **로그 예시**

  ```
  java.lang.NullPointerException: Cannot invoke "ErrorCode.getStatus()" because "exception.getErrorCode()" is null
  ```
* Postman 응답

  ```json
  {
    "timestamp": "2025-08-15T10:32:11.321+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/api/auth/signup"
  }
  ```

---

## 3. 해결 방법

### 잘못된 코드

```java
throw new BusinessException("이미 사용 중인 아이디입니다.");
```

### 수정된 코드

```java
throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
```

### 해결 포인트

1. 예외는 **항상 ErrorCode 기반으로 생성**하도록 강제
2. 예외 처리기에서는 `ErrorCode`의 `status`, `message`를 안정적으로 참조 가능
3. 코드 레벨에서 Enum 기반 통일 → `NullPointerException` 방지

---

## 4. 예방 방법

1. **BusinessException 생성자 제한**

   ```java
   public class BusinessException extends RuntimeException {
       private final ErrorCode errorCode;

       public BusinessException(ErrorCode errorCode) {
           super(errorCode.getMessage());
           this.errorCode = errorCode;
       }

       public ErrorCode getErrorCode() {
           return errorCode;
       }
   }
   ```

   → 문자열 전달 생성자 제거 (컴파일 단계에서 강제)

2. **테스트 케이스 추가**

    * 중복 회원가입 시 반드시 `ErrorCode.DUPLICATE_USERNAME` 반환하는지 확인
    * 예외 메시지, HTTP 상태코드 검증

3. **컨벤션 문서화**

    * "커스텀 예외는 반드시 `ErrorCode` Enum 기반으로 생성한다"를 팀 규칙에 포함

---

## 5. 핵심 교훈

1. 커스텀 예외는 **일관된 생성 규칙**이 필요하다.
2. `Enum` 기반 관리가 없으면 런타임 NPE 가능성이 크다.
3. 예외 핸들링은 **개발자가 의도한 형태로 사용자에게 전달**되어야 한다.
