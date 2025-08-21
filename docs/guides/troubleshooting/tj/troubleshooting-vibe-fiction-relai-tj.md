# 트러블슈팅 리포트: Vibe Fiction (Relai 플랫폼) 개발 중 발생한 주요 문제

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 발생한 다양한 문제를 기록한 종합 트러블슈팅 리포트입니다.
UI 폼 구조, 인증 토큰, API 응답, JS 모듈 경로 문제까지 총 4건의 사례를 다루며, 각 문제의 증상·원인·해결 과정을 체계적으로 정리했습니다.

**작성자:** [왕택준](https://github.com/TJK98)

**작성일:** 2025년 8월 17일

**문서 버전:** v1.0

---

## 챕터 1. UI 폼 구조 문제

### 1. 문제 현상

* **에러 증상**: “소설 내용 (1화)” 섹션에 1화 제목 입력란이 표시되지 않음.
* **발생 배경**: JS 코드에서 `chapterTitleInput`을 참조했으나, DOM에 해당 ID가 존재하지 않아 `null` 참조 발생.

### 2. 원인 분석

* HTML 구조상 `textarea`만 존재하고, 별도의 제목 `input` 요소가 없음.
* JS에서 참조하는 ID와 실제 DOM 요소 불일치.

### 3. 해결 과정

* **HTML 수정**: 1화 제목 입력란 추가.

  ```html
  <div class="form-group">
    <label for="chapter-title">1화 제목</label>
    <input type="text" id="chapter-title" name="chapterTitle" required>
  </div>
  ```
* **JS 수정**: 올바른 ID 참조.

  ```js
  const chapterTitleInput = document.getElementById('chapter-title');
  ```

### 4. 결론 및 배운 점

* DOM 요소와 JS 코드의 ID/Name은 반드시 일치해야 한다.
* 프론트엔드 개발 시, **UI 구조와 스크립트 의존성 검증 절차**가 필요하다.

---

## 챕터 2. Token 불일치 문제

### 1. 문제 현상

* **에러 메시지**:

  ```
  403 Forbidden
  ```
* **상황**: 프론트엔드에서 API 호출 시 무조건 인증 실패.

### 2. 원인 분석

* 서버는 `X-VibeFiction-Token` 헤더를 요구.
* 프론트엔드는 `assertToken`이라는 잘못된 이름으로 헤더를 전송.
* 토큰 키 이름 불일치로 인해 Security Filter가 인증을 통과하지 못함.

### 3. 해결 과정

* **프론트엔드 수정**:

  ```js
  const headers = {
    "Content-Type": "application/json",
    "X-VibeFiction-Token": localStorage.getItem("vibeFictionToken")
  };
  ```
* **백엔드 임시 완화**: 개발 단계에서 `/api/novels/**`, `/api/ai/**`를 `.permitAll()`로 설정하여 테스트 가능하게 함.

### 4. 결론 및 배운 점

* API 문서에서 **헤더 키 이름을 정확히 통일**해야 한다.
* 운영 환경에서는 반드시 `Authorization: Bearer` 표준을 따르는 것이 유지보수에 유리하다.

---

## 챕터 3. API 응답 구조 불일치 문제

### 1. 문제 현상

* **증상**: 프론트엔드에서 소설 제목/본문이 화면에 표시되지 않고 `undefined` 값 출력.

### 2. 원인 분석

* 백엔드 응답 구조:

  ```json
  {
    "success": true,
    "message": "ok",
    "data": { "novelTitle": "..." }
  }
  ```
* 프론트엔드 기대 구조: `response.data.novelTitle`
* 실제 접근 경로: `response.data.data.novelTitle`

### 3. 해결 과정

* 프론트엔드 코드 수정:

  ```js
  const resultData = response.data; // ApiResponse.data 추출
  novelTitleInput.value = resultData.novelTitle;
  ```

### 4. 결론 및 배운 점

* 프론트-백엔드 간 DTO/응답 스펙을 반드시 문서화하고 **통일된 Contract**를 유지해야 한다.
* 데이터 접근 경로 변경 시, 사전에 명세 공유와 테스트가 필요하다.

---

## 챕터 4. JS 모듈 경로 문제

### 1. 문제 현상

* **에러 로그**:

  ```
  create-novel.js:9 GET http://localhost:9009/js/utils/api.js net::ERR_SOCKET_NOT_CONNECTED
  ```
* **발생 배경**: 서버에서 JS 모듈을 찾지 못해 import 실패.

### 2. 원인 분석

* `create-novel.js` 내부에서 `/utils/api.js`를 절대경로로 import.
* 실제 정적 리소스 경로는 `static/js/utils/api.js` 하위에 존재.

### 3. 해결 과정

* 상대경로 기반 import로 수정.

  ```js
  import { getGenres, recommendNovelApi, createNovelApi } from '../utils/api.js';
  ```

### 4. 결론 및 배운 점

* 정적 리소스는 **Spring Boot의 `/resources/static/` 구조**와 일치해야 한다.
* 배포 환경까지 고려할 때, **상대경로 import**가 안전하다.

---

# 종합 결론

Relai 플랫폼 개발 과정에서 발생한 네 가지 문제는 모두 \*\*작은 불일치(폼 구조, 헤더 이름, 응답 경로, 모듈 경로)\*\*에서 비롯되었습니다.
이들은 각각 UI, 인증, API 계약, 정적 리소스 관리 영역에서 발생했으나, 공통적으로 **사소한 불일치가 전체 기능 실패로 직결**된다는 점에서 교훈을 줍니다.

* **공통 교훈**

  1. 프론트-백엔드 계약(Contract)과 명세는 반드시 문서화하고 공유할 것.
  2. 보안 토큰·헤더·API 경로는 일관성 있게 관리할 것.
  3. DOM과 스크립트, 정적 자원과 빌드 경로 간의 매칭을 꼼꼼히 점검할 것.
