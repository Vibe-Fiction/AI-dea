<h1 align="center">Relai : AI 기반 소설 릴레이 플랫폼</h1>

<p align="center">
  <img src="src/main/resources/static/img/Relai-logo-small.png" alt="Relai 로고" width="140" />
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg"></a>
  <img src="https://img.shields.io/badge/Version-v1.0.0-6DB33F">
  <img src="https://img.shields.io/badge/Status-Active-success">
</p>

<p align="center"><b>“Relai - 소설계의 GitHub, 오픈소설(Open-Novel) 플랫폼”</b></p>
<p align="center">AI와 함께하는 협업 소설 창작 플랫폼 — GitHub처럼 이어 쓰고, 투표로 최고의 스토리를 완성합니다.</p>

---

## 목차

- [프로젝트 정보](#프로젝트-정보)
- [Getting Started](#getting-started)
- [프로젝트 비전](#프로젝트-비전)
    - [Relai가 해결하는 문제](#relai가-해결하는-문제)
    - [Relai의 철학](#relai의-철학)
    - [플랫폼 차별성](#플랫폼-차별성)
- [주요 기능](#주요-기능)
- [플랫폼 흐름](#플랫폼-흐름)
- [팀 구성](#팀-구성)
- [개발 기간](#개발-기간)
- [기술 스택](#기술-스택)
- [대표 문서](#대표-문서)
    - [전체 문서 폴더](#전체-문서-폴더)
    - [협업 규칙 & 기여 문서](#협업-규칙--기여-문서)
    - [라이선스](#라이선스)
- [향후 업데이트 계획](#향후-업데이트-계획)
    - [Phase 1 (v1.1.0) — 기능 개선](#phase-1-v110--기능-개선)
    - [Phase 2 (v1.2.0) — 기능 확장](#phase-2-v120--기능-확장)
    - [Phase 3 (v2.0.0) — 차세대 확장](#phase-3-v200--차세대-확장)
- [라이선스 및 문의](#라이선스-및-문의)

---

## 프로젝트 정보

| 항목           | 내용                       |
|--------------|--------------------------|
| **팀명**       | Ai-dea (에이아이-디어)         |
| **프로젝트명**    | Vibe Fiction (바이브 픽션)    |
| **플랫폼명**     | Relai (릴레이)              |
| **버전**       | v1.0.0                   |
| **Base URL** | `http://localhost:9009/` |

---

## Getting Started

- [설치 및 실행 가이드](docs/guides/SETUP_GUIDE.md)
- [API 스펙 (OpenAPI)](docs/openapi-relai.yml)
- [프로젝트 디렉토리 구조](docs/guides/STRUCTURE_GUIDE.md)

---

## 프로젝트 비전

Relai는 단순한 이어쓰기 플랫폼이 아니라, **소설 창작의 병목 현상**을 해결하고  
개인의 창작을 집단 협업으로 확장하는 새로운 패러다임을 제시합니다.

### Relai가 해결하는 문제

- **개인 창작의 한계**: 시간 부족, 창의적 소진, 연재 중단
- **품질 관리 부재**: 기존 릴레이 소설은 참여 품질이 제각각이고 완결성이 떨어짐
- **낮은 지속성**: 흥미 위주로 흐르다 보니 장기 프로젝트로 이어지지 못함

### Relai의 철학

* **오픈 소설(Open-Novel) 철학**:

  창작 과정 전체를 오픈 소스 개발 문화에 비유합니다. 이어쓰기는 PR, 투표는 리뷰, 채택은 Merge. 창작은 개인의 소유가 아니라 공동체의 기여를 통해 완성됩니다.

* **AI 파트너십 철학(Vibe Fiction)**:

  AI는 단순한 보조 도구가 아니라 창작의 협업 파트너입니다. 짧은 아이디어를 발전시키거나 장편 집필까지 지원하며, 이는 개발자가 AI와 함께 코드를 완성하는 **바이브 코딩(Vibe Coding)** 철학을 소설로 확장한 구현입니다.

* **집단 창작의 품질 철학**:

  누구나 제안할 수 있지만, 공동체의 합의(투표)를 통해 오직 하나의 공식 정사(Official Canon)만 채택됩니다. 즉흥적 재미에 그치지 않고, 완결성과 품질을 갖춘 작품을 지향합니다.

* **접근 권한 철학**:

  창작은 열린 공간이면서도 각자의 선택이 존중받아야 합니다. 공개, 비공개, 친구 공개 설정을 통해 사용자가 원하는 방식으로 창작 공간을 설계할 수 있습니다.

* **서사 빌드업 철학**:

  Relai의 서사는 단선적 이어쓰기를 넘어서, **시놉시스 → 챕터 → 릴레이**의 빌드업 구조를 따릅니다. 작은 아이디어가 체계적으로 확장되어 장편으로 성장할 수 있는 서사 생태계를 추구합니다.

### 플랫폼 차별성

Relai의 목표는 단순한 “이어쓰기 재미”가 아니라, **완성도 있는 협업 작품 창출**입니다.  
이를 위해 오픈 소설(Open-Novel) 철학과 AI 파트너십 철학(Vibe Fiction)을 결합하여 다음과 같은 차별성을 제공합니다.

| 구분         | 기존 릴레이 플랫폼      | **Relai**                        |
| ---------- | --------------- | -------------------------------- |
| **협업 모델**  | 단순 이어쓰기, 분기형 전개 | 아이디어 **PR → 투표 → Merge**         |
| **서사 구조**  | 짧은 문장/에피소드 나열   | **시놉시스 → 챕터 → 릴레이** 구조화          |
| **AI의 역할** | 없음 또는 단순 보조     | **맥락 이해 + 아이디어 제안 + 품질 개선**      |
| **운영 관리**  | 수동 관리, 기능 제한    | **자동화 정책** (투표 마감, 동률 처리, 휴면 관리) |
| **창작 접근성** | 즉흥적 재미 위주       | **가볍게 ↔ 본격 집필** 모두 지원 (AI 보조)    |

> Relai는 단순한 이어쓰기를 넘어, **완결성과 품질을 갖춘 협업 작품**을 만드는 데 집중합니다.

---

## 주요 기능

### 소설 창작
- 일반 창작 모드: 단독 작성
- AI 보조 모드: 인물·장르·흐름 입력 → AI 이어쓰기 제안
- 이어쓰기(PR) 제안 및 채택

### 릴레이 구조
- 투표 기반 다음 화 채택
- 투표 동률 시 원작자 우선권, 장기간 비활성화 제안 자동 처리 (예정)

### 자동화 정책
- 투표 마감 시 자동 처리 (최다 득표안 채택, 동률/무투표 시 원작자 선택권 제공)
- 30일 이상 미갱신 작품 자동 비공개 전환 (예정)

### 접근 권한 모델 (예정)
- 공개 / 비공개 / 친구 공개 지원
- 연재방 접근 비밀번호 설정

---

## 플랫폼 흐름

<p align="center">
  <img src="assets/img/platform-service-flow.gif" alt="Relai 서비스 흐름" width="80%"/>
</p>

> Relai의 창작 흐름은 **시놉시스(아이디어) → 챕터(큰 단위) → 릴레이(세부 이어쓰기)** 단계로 이어집니다.  
작은 아이디어가 차곡차곡 쌓여 장편 작품으로 성장하는 과정을 체험할 수 있습니다.

> Relai의 전체 사용자 흐름은 [프론트엔드 페이지 구성 문서](docs/frontend_page_structure.md)에서 더 자세히 확인할 수 있습니다.

---

## 팀 구성

| 역할 | 이름                                     | 주요 기술 및 담당 기능                                                                                   | 회고록 링크                                                                                                             |
| -- | -------------------------------------- |-------------------------------------------------------------------------------------------------| ------------------------------------------------------------------------------------------------------------------ |
| 팀장 | [송민재](https://github.com/songkey06)    | **Backend**: 투표 API, 로직 처리<br>**Frontend**: 투표 UI/UX<br>**Docs**: 회의록                | [송민재 회고록](https://github.com/songkey06/github-25050513/blob/main/Vibe-Fiction.txt)                                 |
| 팀원 | [고동현](https://github.com/rhehdgus8831) | **Backend**: JWT 인증·인가, 보안 정책<br>**Frontend**: 로그인/회원가입 UI/UX<br>**Docs**: ERD 명세, 발표 자료        | [고동현 회고록](https://github.com/rhehdgus8831/Project-SYNCUP-retrospective/blob/main/Ai_dea.md)                        |
| 팀원 | [백승현](https://github.com/Sirosho)      | **Backend**: API 구현, 데이터 검증<br>**Frontend**: 메인/마이/상세 페이지 UI/UX<br>**Docs**: API 명세, 발표 자료      | [백승현 회고록](https://github.com/Sirosho/study-with-me/blob/main/ai_dea.md)                                            |
| 팀원 | [왕택준](https://github.com/TJK98)        | **Backend**: AI 추천·이어쓰기 API, 장르 관리<br>**Frontend**: 소설 작성·제안 UI/UX<br>**Docs**: 정책/운영 문서, 규칙 관리 | [왕택준 회고록](https://velog.io/@wtj1998/Vibe-Fiction-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%ED%9A%8C%EA%B3%A0%EB%A1%9D) |

> Team Ai-dea의 팀 구성 및 역할은 [상세 문서](docs/planning/team_roles.md)에서 더 자세히 확인할 수 있습니다.

---

## 개발 기간

**2025.08.05 ~ 2025.08.21**

| **Phase**                          | **기간**         | **주요 내용**                               |
| ---------------------------------- | -------------- | --------------------------------------- |
| **Phase 1. Planning**              | 08.05 \~ 08.08 | 기획, 초기 설계, DB 모델링                       |
| **Phase 2. Development**           | 08.09 \~ 08.17 | 핵심 기능 개발, API 연동                        |
| **Phase 3. Frontend Build**        | 08.18 \~ 08.20 | 프론트엔드 구현 (Vanilla JS, API 연결, UI/UX 개선) |
| **Phase 4. Documentation**         | 08.20          | 문서 정리 및 발표 자료(PPT) 작성                   |
| **Phase 5. Integration & Testing** | 08.21 \~       | 통합, 테스트, 발표                             |
| **Phase 6. Deployment**            | 08.22 \~       | 서비스 배포 예정                               |


---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **언어** | ![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white) |
| **프레임워크** | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-6DB33F?logo=springboot&logoColor=white) |
| **프론트엔드** | ![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-ES6+-F7DF1E?logo=javascript&logoColor=black) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?logo=thymeleaf&logoColor=white) |
| **보안/인증** | ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-0.12.3-000000?logo=jsonwebtokens&logoColor=white) |
| **AI 연동** | ![Google Gemini](https://img.shields.io/badge/Google%20Gemini-4285F4?logo=google&logoColor=white) |
| **데이터베이스** | ![MariaDB](https://img.shields.io/badge/MariaDB-003545?logo=mariadb&logoColor=white) ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?logo=hibernate&logoColor=white) ![QueryDSL](https://img.shields.io/badge/QueryDSL-5.0.0-1C8D73) |
| **빌드/의존성 관리** | ![Gradle](https://img.shields.io/badge/Gradle-1.1.7-02303A?logo=gradle&logoColor=white) |
| **테스트** | ![JUnit5](https://img.shields.io/badge/JUnit5-25A162?logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/Mockito-000000?logo=mockito&logoColor=white) ![H2](https://img.shields.io/badge/H2%20Database-007396) |
| **편의 도구** | ![Lombok](https://img.shields.io/badge/Lombok-grey?logo=java&logoColor=white) ![DevTools](https://img.shields.io/badge/Spring%20DevTools-6DB33F) |
| **로깅** | ![Spring Logging](https://img.shields.io/badge/Logging-grey?logo=springboot&logoColor=white) |
| **파일 업로드** | `~/aidea/uploads/` |
| **협업 도구** | ![Git](https://img.shields.io/badge/Git-F05032?logo=git&logoColor=white) ![GitHub](https://img.shields.io/badge/GitHub-181717?logo=github&logoColor=white) ![Discord](https://img.shields.io/badge/Discord-5865F2?logo=discord&logoColor=white) |
| **개발 환경** | ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?logo=intellijidea&logoColor=white) ![Windows11](https://img.shields.io/badge/Windows%2011-0078D6?logo=windows11&logoColor=white) |
| **테스트 환경** | ![Chrome](https://img.shields.io/badge/Chrome-4285F4?logo=googlechrome&logoColor=white) ![Postman](https://img.shields.io/badge/Postman-FF6C37?logo=postman&logoColor=white) |

---

## 대표 문서

- [프로젝트 개요 및 기능](docs/planning/vibe-fiction-project-overview-and-features.md)
- [플랫폼 컨셉 정의서](docs/planning/relai-platform-overview.md)
- [플랫폼 시나리오 상세 명세서](docs/USER_FLOW.md)
- [시스템 아키텍처](docs/architecture/system_architecture.md)
- [API 스펙](docs/openapi-relai.yml)
- [릴레이 연재 정책 문서](docs/rules/relay_automation_rules.md)
- [커뮤니티 가이드라인](docs/rules/COMMUNITY_GUIDELINES.md)
- [저작권 및 지적재산권 정책](docs/rules/COPYRIGHT_POLICY.md)
- [AI 저작권 및 수익화 정책](docs/rules/AI_COPYRIGHT_COMPANY_POLICY.md)

### 전체 문서 폴더

- [Architecture 문서](docs/architecture)
- [Guides 문서](docs/guides)
  - [Troubleshooting 문서](docs/guides/troubleshooting)
- [회의록](docs/meeting-notes)
- [Planning 문서](docs/planning)
- [Presentations 문서](docs/presentations)
- [Rules 문서](docs/rules)
  - [Team rules 문서](docs/rules/team-rules)

### 협업 규칙 & 기여 문서

- [팀 규칙 허브](CONTRIBUTING.md)
- [GitHub PR/ISSUE 템플릿](.github)

### 라이선스

- [MIT License](LICENSE)

---

## 향후 업데이트 계획

### Phase 1 (v1.1.0) — 기능 개선

* **사용자 인증 및 계정 관리 강화**
    * 약관 동의 및 저작권/면책 조항 확인 (비동의 시 회원가입 불가)

* **릴레이 연재 정책 및 자동화 고도화**
    * 투표 기간: 새 회차 등록 기준 72시간
    * 자동 채택: 최다 득표안 자동 반영
    * 동률/무투표: 원작자 선택 기회 제공 (48시간 이내)
    * 제안 없음: 투표 기간 1회 연장 (+72시간)
    * 자기 투표 제한: 본인 제안에는 투표 불가

* **접근 범위 및 보안 확장**
    * 친구 전용 연재방(비공개 연재방)
    * 비밀번호 기반 수정 권한
    * 비공개 연재작 → 공개/릴레이 모드 전환 가능
    * 소설 상태 관리 (연재 중, 완결, 숨김)

---

### Phase 2 (v1.2.0) — 기능 확장

* **커뮤니티 기능**
    * 사용자 신고/제재, 차단, 트롤 대응 가이드
    * 유저 등급 시스템 (연재/채택 활동 기반)

* **콘텐츠 관리 및 시각화**
    * 성인/민감 콘텐츠 접근 제한
    * 표지 및 프로필 이미지 관리
    * 표지 수정 기능

* **사용성 개선**
    * 임시 저장 및 초안 기능
    * 알림 (좋아요, 댓글, 채택)
    * 감상 및 토론 댓글
    * 고급 정렬 (평점순, 댓글순, 즐겨찾기순)
    * 장르별 카테고리/필터
    * 즐겨찾기 및 평점 기능
    * 다크모드 UI
    * AI 보조 기능 온/오프 옵션

---

### Phase 3 (v2.0.0) — 차세대 확장

* 지능형 알림 시스템 (동률, 제안 없음, 비공개 전환 예정 등 상황별 알림)
* 완결 선언 및 완결작 보관소(스토리 아카이브)
* 외부 소설 백업/내보내기 API
* 전자책 변환 및 수익화(후원·광고)
* AI 기반 개인 맞춤 추천 기능
* 전체 회차 요약 기능 (AI 기반)
* AI 토큰 기반 구독 결제 시스템
* 모바일 앱(iOS/Android) — 창작, 투표 등 핵심 기능을 모바일에서도 지원

---

## 라이선스 및 문의

이 프로젝트는 MIT 라이선스 하에 공개되어 있습니다.  
자세한 내용은 [LICENSE](LICENSE) 파일을 참고하세요.

> 궁금한 점은 언제든 GitHub Issue, 댓글, 혹은 메일로 문의 바랍니다.

[AI-dea Repository](https://github.com/Vibe-Fiction/AI-dea)

**문의:** [wtj1998@naver.com](mailto:wtj1998@naver.com)

**작성자:** [왕택준](https://github.com/TJK98)
