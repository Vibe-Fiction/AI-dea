# 트러블슈팅: Git 리버트 후 머지 문제

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 Git 브랜치 작업에서 발생한 **리버트 후 재머지 실패 문제**를 분석하고 해결한 사례를 기록한 기술 문서입니다.

**작성일:** 2025년 8월 19일

**문서 버전:** v1.0

---

## 1. 문제 현상

* **발생 상황**:

  1. `feature` 브랜치 변경사항을 `dev`에 머지 (커밋 A)
  2. API 키 노출 등의 이유로 `dev`에서 해당 커밋을 **리버트** (커밋 B)
  3. 수정 후 다시 `feature` → `dev` 머지 시도
     → **변경사항이 반영되지 않음**

* **에러 로그 예시**:

  ```bash
  git merge feature
  # Already up to date.

  git cherry-pick <commit-hash>
  # The previous cherry-pick is now empty, possibly due to conflict resolution.
  ```

* **증상 요약**:
  Git이 동일한 변경사항을 "이미 적용된 것으로 간주"하고 머지를 무시.

---

## 2. 원인 분석

1. **Git 히스토리 구조**

   * 커밋 A(머지)와 커밋 B(리버트)가 모두 `dev` 히스토리에 존재.
   * 이후 동일한 커밋을 다시 합치려 하면 Git은 "이미 처리된 변경"으로 판단.

2. **머지 베이스 문제**

   * Git은 두 브랜치의 공통 조상을 찾을 때 리버트된 커밋을 포함한 상태에서 비교.
   * 결과적으로 새로운 변경사항이 없는 것으로 계산.

---

## 3. 해결 과정

### 3-1. 방법 1: 하드 리셋 (많은 변경사항이 있을 때 유용)

**조건**: 팀원과 협의 필수, 백업 브랜치 확보 필요.

1. 백업 브랜치 생성

   ```bash
   git checkout dev
   git branch backup/dev-$(date +%Y%m%d-%H%M%S)
   ```
2. 되돌릴 커밋 확인

   ```bash
   git log --oneline -10
   git log --graph --oneline --all -10
   ```
3. 하드 리셋 실행

   ```bash
   git reset --hard <원하는_커밋_해시>
   ```
4. 원격 반영

   ```bash
   git push origin dev --force-with-lease
   ```

---

### 3-2. 방법 2: 리버트의 리버트

리버트 커밋(B)을 다시 리버트 → 원래 변경사항 복구.

```bash
git revert <리버트_커밋_해시>
git push origin dev
```

---

### 3-3. 방법 3: 새로운 커밋으로 재적용

1. feature 브랜치를 기준으로 새로운 브랜치 생성.
2. revert된 변경사항을 다시 적용.
3. 새로운 커밋으로 dev에 머지.

```bash
git checkout dev
git checkout feature -- .
git add .
git commit -m "Re-apply feature changes with fixes"
git push origin dev
```

---

## 4. 주의사항

* **하드 리셋**: 협업 환경에서는 반드시 동의 후 진행.
* **강제 푸시**:

  ```bash
  # 위험
  git push origin dev --force
  # 안전
  git push origin dev --force-with-lease
  ```

---

## 5. 예방 방법

1. **리버트 대신 수정 커밋 사용**

   * 민감 정보가 노출된 경우 리버트보다 **수정 커밋**으로 문제 해결.
2. **Feature 플래그 활용**

   * 기능 토글로 위험한 변경사항을 제어.
3. **Pre-commit 훅으로 민감 정보 차단**

   ```bash
   # .git/hooks/pre-commit
   if git diff --cached | grep -i "api.key\|password\|secret"; then
     echo "Warning: Potential sensitive information detected!"
     exit 1
   fi
   ```

---

## 6. 결론 및 배운 점

* **문제 원인**: Git은 리버트된 커밋을 다시 머지하려 할 때 중복으로 인식하여 무시.
* **해결책**:

  * 하드 리셋
  * 리버트의 리버트
  * 새로운 커밋으로 재적용
* **교훈**:

  1. 불필요한 리버트는 피하고, 가능하면 수정 커밋으로 처리.
  2. 협업 환경에서는 `reset --hard` 및 강제 푸시 사용 시 반드시 사전 협의.
  3. 민감정보 노출은 사전 차단(Feature flag, pre-commit 훅)으로 예방.
