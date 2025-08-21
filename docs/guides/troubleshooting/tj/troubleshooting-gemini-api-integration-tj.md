# 트러블슈팅: Gemini AI API 연동 과정에서 발생한 주요 문제들

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 Google **Gemini AI API**를 연동하는 과정에서 발생했던 다양한 문제의 원인과 해결 과정을 정리한 기술 문서입니다.
HTTP 요청 처리 오류부터 DB 스키마 충돌까지 여러 유형의 장애를 다루며, 향후 유사 상황에서 시행착오를 줄이는 것을 목표로 합니다.

**작성자:** [왕택준](https://github.com/TJK98)

**작성일:** 2025년 8월 14일

**문서 버전:** v1.0

---

## 1. 문제 현상

### 1-1. `415 Unsupported Media Type`

* **에러 메시지**

  ```
  Content-Type 'text/plain;charset=UTF-8' is not supported
  ```
* **상황**: Postman으로 `POST /api/ai/novels/recommend` 호출 시 발생.
* **특징**: 서버는 `application/json`을 기대했으나 요청이 기본값인 `text/plain`으로 전송됨.

---

### 1-2. `503 Service Unavailable`

* **에러 메시지**

  ```
  The model is overloaded. Please try again later.
  ```
* **상황**: Gemini API 호출 시 Google 서버가 일시적으로 과부하 상태.
* **특징**: 우리 코드/설정 문제가 아닌 **외부 서버 부하** 문제.

---

### 1-3. `Data too long for column 'prompt'` (DB 예외)

* **에러 메시지**

  ```
  java.sql.SQLSyntaxErrorException: Data too long for column 'prompt' at row 1
  ```
* **상황**: `AiAssistServiceTj`가 Gemini 호출 로그를 DB(`ai_interaction_logs`)에 저장하는 과정에서 발생.
* **특징**: 프롬프트 길이가 DB 컬럼(`VARCHAR`, `TEXT`) 크기를 초과.

---

## 2. 원인 분석

1. **클라이언트 설정 문제**: Postman에서 `Content-Type`을 지정하지 않아 `415` 발생.
2. **외부 서버 상태 문제**: Google Gemini 서버 자체의 부하로 인해 `503` 반환.
3. **DB 스키마 한계**: JPA 기본 매핑(`VARCHAR(255)`/`TEXT`)으로는 AI 프롬프트/응답의 장문 텍스트 저장 불가.

---

## 3. 해결 과정

### 3-1. `415 Unsupported Media Type` 해결

* Postman 요청 Body 타입을 **JSON**으로 명시 설정.
* 자동으로 `Content-Type: application/json` 헤더가 포함되어 정상 동작.

---

### 3-2. `503 Service Unavailable` 해결

* 코드 수정 불필요.
* 일정 시간 대기 후 재시도 시 정상 응답.
* 추후를 대비해 **재시도 로직(backoff)** 고려.

---

### 3-3. DB 스키마 문제 해결

1. **DB 컬럼 확장**

   ```sql
   ALTER TABLE ai_interaction_logs
       MODIFY COLUMN prompt MEDIUMTEXT,
       MODIFY COLUMN result MEDIUMTEXT;
   ```
2. **엔티티 코드 반영**

   ```java
   @Column(name = "prompt", columnDefinition = "MEDIUMTEXT")
   private String prompt;

   @Column(name = "result", columnDefinition = "MEDIUMTEXT")
   private String result;
   ```

* `MEDIUMTEXT`로 변경하여 장문 텍스트까지 안정적으로 저장 가능.

---

## 4. 결론 및 배운 점

* **통신/데이터 이중 고려**

  * API 호출 성공만으로 끝이 아니라, 그 결과 데이터(길이·형식)가 우리 DB/서비스에서 수용 가능한지도 검증해야 함.
* **에러 코드 해석 중요성**

  * `415`, `503`, `500(DB)` 등 에러 코드는 각각 **클라이언트 설정**, **외부 서버 상태**, **내부 스키마 문제**라는 전혀 다른 원인을 가리킴.
* **방어적 설계 필요성**

  * AI 응답이 항상 JSON이라 가정하지 않고, `extractJsonFromString` 같은 파싱 보호 로직을 둔 것이 안정성을 높였음.
