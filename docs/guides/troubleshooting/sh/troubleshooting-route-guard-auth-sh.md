# 트러블슈팅: 라우트 가드 미적용 문제

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼)에서 로그인 없이 URL 직접 접근 시 API 요청이 실패하고 빈 페이지가 표시되는 문제를 분석하고, \*\*라우트 가드(Route Guard)\*\*를 도입해 해결한 과정을 정리한 기술 문서입니다.

**작성자:** 백승현

**작성일:** 2024년 8월 19일

---

## 1. 문제 현상

* 정상 플로우: 로그인 후 버튼 클릭 → 페이지 진입.
* 비정상 플로우: URL 직접 접근 시 레이아웃은 보이지만 데이터 API 호출 실패.
* 마이페이지 예시:

  * ✅ 헤더, 네비게이션 표시
  * ❌ API 401 Unauthorized
  * ❌ 빈 페이지

---

## 2. 원인 분석

1. **동적 라우트 처리 복잡성**

   * `/vote-page/:id` 하나만 필요한데, 정규식 기반 범용 로직으로 과도하게 복잡.

2. **라우트 가드 실행 시점 문제**

   * 페이지 모듈 로드 후 실행되어, 보호된 페이지가 잠깐 보이는 현상.

---

## 3. 해결 과정

1. **동적 라우트 단순화**

   ```javascript
   if (path.startsWith('/vote-page/')) {
       return PAGE_CONFIG['/vote-page/:id'] || null;
   }
   ```

2. **가드 실행 순서 조정**

   ```javascript
   function main() {
       const currentPage = getCurrentPage();
       if (!routeGuard(currentPage)) return; // ✅ 가장 먼저 실행
       // 이후 초기화
   }
   ```

3. **로그아웃 처리 보완**

   ```javascript
   if (authRequiredPaths.includes(currentPath)) {
       window.location.href = '/';
   }
   ```

---

## 4. 결론 및 교훈

* **문제 원인**: URL 직접 접근 시 토큰 검증 로직 미적용.
* **해결책**: 라우트 가드를 최우선 실행, 동적 라우트 단순화.
* **교훈**:

  1. 인증 검증은 초기화 이전에 실행해야 한다.
  2. KISS 원칙 — 불필요한 범용 코드보다 프로젝트 맞춤형 단순 로직.
  3. 라우트 가드 없이 보안은 보장할 수 없다.

---

> 본 문서는 Vibe Fiction 프로젝트(Relai 플랫폼) 개발 과정에서 **라우트 가드 미적용 문제**를 해결한 사례를 기록한 트러블슈팅 문서입니다.
