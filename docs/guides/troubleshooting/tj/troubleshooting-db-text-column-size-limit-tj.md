# 트러블슈팅: DB 텍스트 컬럼 크기 부족 문제

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**에서 개발 중인 **Relai(릴레이) 플랫폼**에서 발생한 **DB 컬럼 크기 부족 문제**의 원인과 해결 과정을 정리한 기술 문서입니다.
AI 로그, AI 응답, 소설 본문 저장 시 각각 다른 예외 메시지가 발생했으나, 근본 원인은 모두 동일하게 **DB 컬럼 타입이 충분히 크지 않음**에 있었습니다.

**작성자:** [왕택준](https://github.com/TJK98)

**작성일:** 2025년 8월 17일

---

## 1. 문제 현상

### 1-1. AI 로그 저장 시 `Data too long` 예외

* **에러 메시지**

  ```text
  java.sql.SQLSyntaxErrorException: Data too long for column 'prompt' at row 1
  ```
* **발생 상황**: Gemini API 호출은 성공했으나, 프롬프트와 결과를 `ai_interaction_logs` 테이블에 저장하려는 시점에 실패.

---

### 1-2. Gemini 응답 저장 시 DB 크기 초과

* **에러 메시지**

  ```text
  Data too long for column 'prompt'
  ```
* **발생 상황**: AI 응답 텍스트가 길어 `VARCHAR(255)` 또는 기본 `TEXT` 컬럼으로는 수천\~수만 글자를 저장할 수 없었음.

---

### 1-3. 소설 본문 저장 시 `Incorrect string value` 예외

* **에러 메시지**

  ```text
  java.sql.SQLSyntaxErrorException: Incorrect string value: '...' for column chapters.content
  ```
* **발생 상황**: `POST /api/novels` 요청으로 소설 1화를 저장할 때 발생.
* **특징**: 인코딩 문제처럼 보이는 메시지가 출력되었으나, 실제 원인은 **컬럼 크기 부족**이었음.

---

## 2. 원인 분석

문제의 근본 원인은 **DB 컬럼 타입 제한**과 **JPA 기본 설정의 한계**에 있음.

1. **JPA 자동 생성 한계**

   * `@Lob`만 선언하면 기대한 `MEDIUMTEXT`나 `LONGTEXT`가 생성되지 않고, 기본적으로 작은 크기의 `VARCHAR(255)`가 생성됨.
2. **데이터 특성**

   * AI 프롬프트는 수천 자 이상일 수 있음.
   * Gemini AI 응답은 장문(수천\~수만 자)까지 반환 가능.
   * 소설 본문은 한글 텍스트가 포함되어 길이가 커지고, 에러 메시지는 모호하게 나타남.
3. **실질적 원인**

   * DB 컬럼 크기가 실제 데이터 길이를 감당하지 못해 `Data too long` 또는 `Incorrect string value` 예외 발생.

---

## 3. 해결 과정

### 3-1. DB 스키마 확장

```sql
ALTER TABLE ai_interaction_logs
    MODIFY COLUMN prompt MEDIUMTEXT,
    MODIFY COLUMN result MEDIUMTEXT;

ALTER TABLE chapters
    MODIFY COLUMN content MEDIUMTEXT;

ALTER TABLE proposals
    MODIFY COLUMN content MEDIUMTEXT;

ALTER TABLE novels
    MODIFY COLUMN synopsis TEXT;
```

### 3-2. 엔티티 코드 수정

```java
// AiInteractionLogs.java
@Column(name = "prompt", columnDefinition = "MEDIUMTEXT")
private String prompt;

@Column(name = "result", columnDefinition = "MEDIUMTEXT")
private String result;

// Chapters.java
@Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false)
private String content;

// Proposals.java
@Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false)
private String content;

// Novels.java
@Lob
@Column(name = "synopsis", columnDefinition = "TEXT")
private String synopsis;
```

### 3-3. 스키마 재생성 (필요 시)

* JPA `ddl-auto: update`는 기존 컬럼 타입 변경을 지원하지 않음.
* 초기 단계에서는 **테이블 Drop 후 재생성**이 더 확실한 해결책일 수 있음.

---

## 4. 결론 및 배운 점

* **문제 요약**: AI 프롬프트, AI 응답, 소설 본문 등 텍스트 데이터가 모두 DB 컬럼 크기 제한에 걸려 저장 실패.
* **해결 요약**: 작은 크기의 `VARCHAR`/`TEXT` 대신 `MEDIUMTEXT` 혹은 `LONGTEXT`로 확장.
* **교훈**:

  1. JPA `ddl-auto` 자동 생성만 신뢰하지 말고, 중요한 텍스트 컬럼은 직접 정의해야 한다.
  2. `Incorrect string value` 같은 모호한 메시지일지라도 **컬럼 크기 부족** 가능성을 우선 확인할 것.
  3. 외부 API 연동 시스템에서는 **데이터 최대 크기**를 미리 예측하고 설계해야 한다.

---

> 본 문서는 Vibe Fiction 프로젝트(Relai 플랫폼) 개발 과정에서 반복적으로 발생한 **DB 텍스트 컬럼 크기 부족 문제**를 하나로 통합 정리한 트러블슈팅 사례입니다.
