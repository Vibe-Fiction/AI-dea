# 트러블슈팅: 무한스크롤 무한루프

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼)에서 무한스크롤 구현 시, 데이터가 없을 때 GET 요청이 무한히 반복되는 문제를 분석하고 해결한 사례를 기록한 기술 문서입니다.

**작성자:** 백승현

**작성일:** 2024년 8월 19일

---

## 1. 문제 현상

* 등록된 소설이 없을 때:

  * `[]` 응답 시 `hasMoreData` 상태 업데이트 실패.
  * `finally` 블록에서 자동 로드가 반복 실행.
  * `scrollHeight <= innerHeight` 조건이 계속 true → 무한 루프.

---

## 2. 원인 분석

1. **빈 배열 처리 누락**

   * novels.length === 0일 때 `return`이 상태 반영보다 먼저 실행.
2. **finally 블록 문제**

   * 오류나 빈 배열 상황에도 자동 로드 실행.
3. **데이터 검증 부재**

   * 배열 형식이 아닐 때도 무한 실행.

---

## 3. 해결 과정

1. **빈 배열 처리 보완**

   ```javascript
   if (novels.length === 0) {
       state.hasMoreData = false;
       return;
   }
   ```

2. **데이터 형식 검증 추가**

   ```javascript
   if (!Array.isArray(novels)) {
       state.hasMoreData = false;
       return;
   }
   ```

3. **오류 발생 시 상태 업데이트**

   ```javascript
   catch (error) {
       state.hasMoreData = false;
   }
   ```

4. **실제 데이터 존재 조건 추가**

   ```javascript
   if (state.totalLoadedNovels > 0) {
       // 추가 로드 허용
   }
   ```

---

## 4. 결론 및 교훈

* **문제 원인**: 무한스크롤 상태 관리 부재.
* **해결책**: 모든 분기에서 `state.hasMoreData`와 `state.isLoading` 상태를 올바르게 업데이트.
* **교훈**:

  1. 무한스크롤 구현 시 예외 상황(빈 배열, 오류)까지 고려해야 한다.
  2. 상태 플래그는 항상 일관성 있게 업데이트해야 한다.
  3. 디버깅 시 Network + Console + 상태 로그를 함께 확인하는 습관이 필요하다.

---

> 본 문서는 Vibe Fiction 프로젝트(Relai 플랫폼) 개발 과정에서 **무한스크롤 무한루프 문제**를 해결한 사례를 기록한 트러블슈팅 문서입니다.
