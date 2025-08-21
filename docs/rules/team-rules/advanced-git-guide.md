# 고급 Git 가이드

> [↩ 전체 규칙으로 돌아가기](../../../CONTRIBUTING.md)

본 문서는 **기본 워크플로우**(workflow.md)와 별도로, 숙련자 또는 특정 상황에서만 필요한 Git 심화 작업 방법을 설명합니다.

**작성자:** [왕택준](https://github.com/TJK98)

**문서 버전:** v1.0

**대상 독자:**
- **숙련 개발자**: 기본 Git 사용법을 넘어 심화 기능을 활용해야 하는 경우
- **팀 리더/관리자**: 충돌 해결, 브랜치 정리, 배포 관련 고급 Git 작업 수행
- **신규 합류자 중 고급 사용자**: 팀 내 Git 활용을 빠르게 심화 수준으로 익히고 싶은 경우

---

## 1. Rebase 활용 팁

### 1.1 Rebase란?

- `git rebase`는 커밋 기록을 **더 깔끔하고 직선형으로 유지**하기 위해 사용합니다.
- 협업 시 불필요한 병합 커밋(Merge Commit)을 줄이는 데 유용합니다.

### 1.2 기본 명령어

#### **방법 1: checkout 사용 (기존 방식)**

```bash
# 현재 브랜치를 최신 dev 브랜치에 맞춰 재정렬
git checkout feature/기능명
git fetch origin
git rebase origin/dev
```

#### **방법 2: switch 사용 (최신 권장 방식)**

```bash
# 현재 브랜치를 최신 dev 브랜치에 맞춰 재정렬
git switch feature/기능명
git fetch origin
git rebase origin/dev
```

### 1.3 주의사항

- **절대 이미 원격에 푸시한 브랜치에서 강제 푸시(\`--force\`)를 하지 말 것**
  → 다른 팀원의 작업 기록이 유실될 위험이 있습니다.
- Rebase는 **내 로컬 브랜치에서만** 안전하게 수행하세요.

---

## 2. Hotfix 흐름

### 2.1 개념

- Hotfix 브랜치는 **배포 중 발견된 긴급 버그를 즉시 수정**하기 위해 사용합니다.
- main 브랜치에서 직접 분기하여 수정 후, main과 dev에 모두 반영합니다.

### 2.2 작업 흐름

#### **방법 1: checkout 사용**

```bash
# 1. main에서 hotfix 브랜치 생성
git checkout main
git pull origin main
git checkout -b hotfix/버그명
```

#### **방법 2: switch 사용**

```bash
# 1. main에서 hotfix 브랜치 생성
git switch main
git pull origin main
git switch -c hotfix/버그명
```

```bash
# 2. 수정 작업 후 커밋 & 푸시
git add .
git commit -m "hotfix: 버그 설명"
git push origin hotfix/버그명
```

```bash
# 3. PR 생성 → main & dev 병합
#    main: 즉시 배포
#    dev: 코드 동기화
```

### 2.3 병합 순서

1. main에 병합 후, 바로 배포
2. dev 브랜치에도 동일한 변경사항 병합
3. hotfix 브랜치 삭제

---

## 3. 권장 사항

- Rebase와 Hotfix는 **PR 리뷰를 거친 후** 진행하세요.
- 팀원 간 사전 합의 없이 Rebase로 커밋 기록을 변경하지 마세요.
- Hotfix는 반드시 버그 재현 및 수정 내역을 명확히 남기세요.

### Squash and Merge 시 메시지 팁

- 제목: **PR 제목 그대로 사용** → `[타입]: 상세 내용 (#이슈번호)`
- 본문: PR의 `작업 내용 요약`에서 핵심 변경 2~3줄 bullet만 이관
- 기본 플로우는 [Git 워크플로우 규칙](workflow.md) 참고
