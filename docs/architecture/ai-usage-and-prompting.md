# Relai AI 활용 및 프롬프트 설계 문서

본 문서는 Relai에서 AI를 호출할 때 **어떤 역할/규칙/제약을 부여**하고, **무슨 데이터를 입력으로 주며**, **어떤 형식으로 응답을 받아 파싱**하여 **UI에 뿌리는지**를 기술합니다.
FE/BE/운영/QA가 공통 이해로 유지·개선할 수 있도록 작성되었습니다.

**작성자**: [왕택준](https://github.com/TJK98)

**문서 버전**: v1.0

**대상 독자:**

* **백엔드/프론트엔드 개발자**: AI 호출 로직, 프롬프트 구조, 파싱 방식 이해 및 유지보수
* **QA 담당자**: 출력 형식 검증, 예외 케이스 테스트, 품질 기준 정의
* **기획/운영자**: AI가 어떤 규칙·제약으로 동작하는지 이해하여 서비스 기획/운영 반영
* **신규 합류자**: Relai의 AI 활용 구조와 프롬프트 설계를 빠르게 이해하기 위한 온보딩 자료

---

## 1) 아키텍처 개요

* 진입점: `AiAssistServiceTj`

  * **신규 소설 추천**: `recommendForNewNovel(userId, AiRecommendNovelRequestTj)`

    * 입력: `genre`, `synopsis`
    * 출력: `novelTitle`, `firstChapterTitle`, `firstChapterContent`, `logId`
  * **이어쓰기 제안**: `continueForChapter(userId, chapterId, AiContinueRequestTj)`

    * 입력: `chapterId`, `instruction`
    * 컨텍스트: **소설 기본 정보(제목/장르/시놉시스) + 모든 챕터 내용(1화\~현재)**
      → `buildFullStoryContext()`가 구성
    * 출력: `suggestedTitle`, `suggestedContent`, `logId`
* 공통: AI 호출 전/후를 **`AiInteractionLogs`** 로 영구 저장 (prompt/result 등)

---

## 2) AI 역할(Role)·규칙(Rules)·제약(Constraints)

### A. 역할(Role) 선언

* **신규 소설 추천**: “창의적인 웹소설 작가 AI”
* **이어쓰기 제안**: “기존 회차를 정확히 이어 쓰는 전문 보조 작가 AI”

> 목적을 명확히 선언해 모델의 톤·출력 목표를 고정합니다.

### B. 지시사항(Instructions)

대표 예시:

1. **문맥 유지**: 기존 설정(인물/능력/톤/세계관)을 바꾸지 않기
2. **열린 결말**: 다음 라운드 이어쓰기가 가능하도록 오픈엔디드로 마무리
3. **사용자 요구 반영**: `instruction`은 핵심 사건으로 자연스럽게 녹이되 충돌 금지
4. **형식 준수**: *JSON 금지*, 구분자(`---`)를 포함한 **텍스트 포맷** 필수
5. **길이 제한 준수**: 제목/본문 최대 길이 등 엄격히 제한

### C. 제약(Constraints)

* `novelTitle` ≤ 50자
* `firstChapterTitle` ≤ 60자
* `firstChapterContent` 200\~5000자
* `suggestedTitle` ≤ 60자
* `suggestedContent` ≤ 5000자
  (길이 기준은 운영/UX 정책과 토큰 예산에 맞게 변경 가능)

---

## 3) 입력 데이터 구성(Input Contract)

### 신규 소설 추천 (`/api/ai/novels/recommend`)

* **필수 입력**: `genre`, `synopsis`
* **프롬프트 포함 정보**

  * 역할/지시/제약/출력형식(고정)
  * 사용자 입력: 장르, 시놉시스

### 이어쓰기 제안 (`/api/ai/chapters/{chapterId}/continue`)

* **필수 입력**: `chapterId`, `instruction`
* **프롬프트 포함 정보**

  * 역할/지시/제약/출력형식(고정)
  * **소설 기본 정보**: 제목, 장르(설명), 시놉시스
  * **컨텍스트**: `buildFullStoryContext()`가 생성한 **모든 챕터 텍스트**
  * 사용자 입력: `instruction` (요구사항)

> 현재 구현은 “특정 회차까지”라는 주석과 달리 **전체 챕터**를 포함합니다. 필요 시 `<= baseChapter.chapterNumber` 필터로 수정 권장.

---

## 4) 출력 형식(Output Contract) & 파싱

### 통일된 텍스트 포맷(구분자 방식)

JSON 대신 **텍스트 + 구분자(`---`)** 를 사용합니다.
이유: 다양한 모델 버전에서 JSON 파싱 실패율이 높고, UI 바인딩이 단순함.

#### 신규 소설 추천 (3분할)

```
<novelTitle>
---
<firstChapterTitle>
---
<firstChapterContent...>
```

파싱: `split("\n---\n", 3)`

#### 이어쓰기 제안 (2분할)

```
<suggestedTitle>
---
<suggestedContent...>
```

파싱: `split("\n---\n", 2)`

> **견고성 권장**: 운영에서는 `(?m)^\s*---\s*$` 같은 정규식 분할을 써 CRLF/공백/양끝 개행 변동에도 안전하게 처리하세요.

---

## 5) 컨텍스트 구성 로직(buildFullStoryContext)

```java
private String buildFullStoryContext(Chapters baseChapter) {
    List<Chapters> allChapters = baseChapter.getNovel().getChapters();
    return allChapters.stream()
        .sorted((c1, c2) -> c1.getChapterNumber().compareTo(c2.getChapterNumber()))
        .map(c -> String.format("제%d화: %s\n%s",
             c.getChapterNumber(), c.getTitle(), c.getContent()))
        .collect(Collectors.joining("\n\n---\n\n"));
}
```

* **현재 설계**: **해당 소설의 모든 회차**를 “제N화: 제목 + 본문” 형태로 연결 (장면 구분을 위해 `---` 삽입)
* **개선 제안**

  1. **토큰 절약**: *요약 테이블* 기반 컨텍스트(“회차별 요약 + 최근 n화 원문”)로 전환
  2. **필터**: `where chapterNumber <= baseChapter.chapterNumber`
  3. **길이 제한 가드**: 토큰 추정치 기준 상한 초과 시 뒤쪽부터 줄여서 콘텍스트 구성

---

## 6) 로깅 & 관측

모든 호출은 `AiInteractionLogs`에 저장:

* `type`: `NOVEL_CREATION` / `PROPOSAL_GENERATION`
* `prompt`: 실제 보낸 프롬프트 전문
* `result`: 원문 응답
* `user`, *(선택)* `basedOnChapter`
* `createdAt` 등

활용:

* 장애/품질 이슈 재현, 프롬프트 A/B 실험, 비용 추적, 악성 입력 추적

---

## 7) 예외 처리 & 품질 가드

* **출력 파싱 실패**: 로그로 원문 남기고 `RuntimeException`(현재), 운영에선 사용자 친화 메시지 + 재시도 버튼 권장
* **빈 응답/제약 위반**: 길이/금칙어 검사 후 재요청 또는 “수정 제안” UX
* **안전 필터**: 혐오/성적/저작권 침해 고위험 키워드 블록리스트(사전/사후) 적용 권장
* **레이트 리밋/백오프**: 429/5xx 시 지수 백오프 재시도(최대 n회)

---

## 8) API 연계 요약

* `POST /api/ai/novels/recommend`
  입력: `{ genre, synopsis }` → 출력: `{ novelTitle, firstChapterTitle, firstChapterContent, aiLogId }`
* `POST /api/ai/chapters/{chapterId}/continue`
  입력: `{ instruction }` → 출력: `{ suggestedTitle, suggestedContent, aiLogId }`

해당 결과는 **폼 자동 채움** → 사용자가 편집 → 제출 시 **정식 엔드포인트** 호출

* `POST /api/novels`
* `POST /api/chapters/{chapterId}/proposals`

---

## 9) 성능/비용 최적화 로드맵

1. **요약 캐시 테이블 도입(권장)**

   * 테이블: `chapter_summaries` (chapter\_id, summary\_text, tokens, updated\_at)
   * 생성/갱신: 챕터 생성 시 즉시 요약 + 이후 편집 시 재생성
   * 프롬프트 입력: “최근 n화 원문 + 그 이전은 요약”
2. **토큰 가드**

   * 입력 토큰 추정 → 상한 초과 시 요약/축약 루트로 자동 전환
3. **프롬프트 템플릿 버전 관리**

   * `prompt_version`을 `AiInteractionLogs`에 추가 기록 → 롤백/실험 용이
4. **장르/금칙어 가드**

   * 입력 정규화(예: BL/GL → boy-love/girl-love 라벨)와 금칙어 치환

---

## 10) 프롬프트 원문 (현행)

### A. 신규 소설 추천 템플릿 (발췌)

```
## ROLE & GOAL
당신은 Vibe Fiction 플랫폼을 위한 창의적인 웹소설 작가 AI입니다 ...

## INSTRUCTIONS
1. Analyze Input
2. Generate Creatively
3. Leave Open-ended
4. Adhere to Constraints
5. Format Output (JSON 금지, 구분자 사용)

## INPUT DATA
- 장르: "<GENRE>"
- 시놉시스: "<SYNOPSIS>"

## CONSTRAINTS
(생략)

## OUTPUT FORMAT (TEXT ONLY, USE SEPARATOR)
<제목>
---
<1화 제목>
---
<1화 내용>
```

### B. 이어쓰기 제안 템플릿 (발췌)

```
## ROLE & GOAL
당신은 ... 전문 웹소설 AI 어시스턴트입니다 ...

## INSTRUCTIONS
1. Strictly Continue the Story ...
(생략)

## CONTEXT
### 소설 기본 정보:
- 제목: "<TITLE>", 장르: [<GENRES>], 시놉시스: "<SYNOPSIS>"
### 이전 회차 전체 내용:
<buildFullStoryContext(...) 결과>

## USER REQUEST
- 다음 이야기 요구사항: "<INSTRUCTION>"

## CONSTRAINTS
(생략)

## OUTPUT FORMAT
<제목>
---
<내용>
```

---

## 11) 테스트 관점(샘플 케이스)

* **구분자 누락**: 응답에 `---`가 없을 때 예외/재시도
* **CRLF 변형**: `\r\n---\r\n` 대응 정규식 분리
* **과도한 길이**: 5000자 초과 시 잘림/재생성
* **설정 위반**: 등장인물/설정 훼손 여부 간단 룰체커
* **토큰 초과 상황**: 요약 루트 정상 전환

---

## 12) 향후 변경에 강한 포인트

* **프롬프트 문자열 템플릿화** → 하드코딩을 피하고, 버전 관리·실험·롤백에 유연하도록 설계
* **요약 테이블 도입** → 장기 연재 소설에서도 안정적인 속도/비용 구조 보장
* **출력 파서 분리** → 유닛 테스트 가능하도록 독립 모듈화하여 견고성 확보
* **출력 파싱 실패 시 재시도 로직** → 현재는 1회 재시도 없이 곧바로 실패 처리됨.  
  향후에는 최대 3회까지 자동 재시도 후에도 실패하면 사용자에게 친화적인 오류 메시지를 제공하고,
  운영 로그에 원문 응답을 남겨 추후 분석 및 개선이 가능하도록 설계
