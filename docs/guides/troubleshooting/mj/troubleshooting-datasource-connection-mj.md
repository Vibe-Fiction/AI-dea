# 트러블슈팅 가이드 – DataSource 연결 오류

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 **DataSource 연결 오류**에 대한 원인과 해결 과정을 정리한 기록입니다.

**작성자:** [송민재](https://github.com/songkey06)

**작성일:** 2025년 8월 20일

**문서 버전:** v1.0

---

### 문제점
'url' 속성이 지정되지 않아 DataSource를 구성하지 못했습니다.

### 원인
`spring.config.import` 설정에 있는 `application-template.yml` 파일이 데이터베이스 연결 정보를 덮어쓰거나 올바르게 로드하지 못했기 때문입니다.

### 해결 방법
`spring.config.import: optional:classpath:application-template.yml` 설정을 주석 처리하여, 이 파일이 로딩되지 않도록 합니다.

### 결론 및 교훈

* **환경 설정 파일 충돌**은 작은 오타나 중복 참조에서 비롯될 수 있음.
* 민감 정보와 환경 변수를 분리할 때는 **우선순위**와 **적용 범위**를 반드시 점검해야 함.
