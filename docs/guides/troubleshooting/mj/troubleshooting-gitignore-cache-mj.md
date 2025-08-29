# 트러블슈팅 가이드 – Git `.gitignore` 미적용

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 발생한 **`.gitignore` 미적용 문제**에 대한 원인과 해결 과정을 정리한 기록입니다.

**작성자:** [송민재](https://github.com/songkey06)

**작성일:** 2025년 8월 20일

**문서 버전:** v1.0

---

### 문제점
`.gitignore` 파일에 `*.yml`을 추가했음에도 불구하고, 이미 추적되던 `.yml` 파일들이 Git에 계속 남아있었습니다.

### 원인
`.gitignore`는 이미 추적 중인(Staged) 파일에는 적용되지 않습니다. `.gitignore`를 수정하더라도, 변경 사항을 적용하려면 캐시를 삭제해야 합니다.

### 해결 방법
먼저 `.gitignore` 파일을 `*.yml`로 변경하고, 다음 명령어를 실행하여 Git 캐시를 삭제합니다.
```bash
git rm -r --cached .
git add .
git commit -m "gitignore update"
```

### 결론 및 교훈

* `.gitignore`는 **미추적 파일에만 동작**한다는 점을 반드시 기억해야 함.
* 민감정보 유출 방지를 위해서는 프로젝트 초기부터 **ignore 정책을 선제적으로 적용**하는 습관이 필요함.
