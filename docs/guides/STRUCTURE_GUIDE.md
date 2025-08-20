# **Vibe Fiction (Relai) 프로젝트 구조 정의서**

**작성자**: [왕택준](https://github.com/TJK98)

**문서 버전:** 1.0

본 문서는 Vibe Fiction 프로젝트의 전체 디렉토리 구조와 각 폴더 및 주요 파일의 역할을 설명합니다. 프로젝트는 표준적인 Spring Boot 백엔드와 모듈화된 프론트엔드 구조를 따르며, 체계적인 문서 관리를 지향합니다.

---

## **1. 최상위 디렉토리 (Root Directory)**

프로젝트의 가장 바깥쪽에 위치하며, 빌드, 버전 관리, 프로젝트의 기본 정보를 담고 있습니다.

| 파일/폴더 | 역할 설명 |
| :--- | :--- |
| 📄 `build.gradle` | 프로젝트의 의존성(라이브러리), 플러그인, 빌드 방식을 정의하는 Gradle 스크립트입니다. |
| 📁 `src` | 애플리케이션의 핵심 소스코드가 위치하는 메인 디렉토리입니다. (상세 내용은 아래 참조) |
| 📁 `docs` | 프로젝트와 관련된 모든 기획, 설계, 가이드 문서를 체계적으로 관리합니다. (상세 내용은 아래 참조) |
| 📄 `README.md` | 프로젝트의 첫인상. 개요, 설치 방법, 기술 스택, 핵심 기능 등을 소개하는 문서입니다. |
| 📄 `CONTRIBUTING.md`| 프로젝트에 기여하는 방법, 팀의 협업 규칙(Git 워크플로우 등)을 안내하는 허브 문서입니다. |
| 📄 `.gitignore` | Git 버전 관리에서 제외할 파일 및 폴더(API 키, 빌드 결과물 등)를 지정합니다. |
| 📄 `LICENSE` | 프로젝트의 소스코드 라이선스(예: MIT, Apache 2.0)를 명시합니다. |
| 📁 `.github`                      | GitHub 전용 설정 및 템플릿 관리 |
---

## **2. 백엔드 소스코드 (`src/main/java`)**

Spring Boot 기반의 Java 애플리케이션 로직이 위치합니다.

| 패키지 | 역할 설명 |
| :--- | :--- |
| 📦 **`com.spring.aidea.vibefiction`** | 프로젝트의 기본 패키지입니다. |
| ┣ 📁 `controller` | 클라이언트의 HTTP 요청(Request)을 받아 처리하고 응답(Response)을 반환하는 API 엔드포인트입니다. |
| ┣ 📁 `dto` | **D**ata **T**ransfer **O**bject. 계층 간(Controller-Service, Service-Client) 데이터 전송을 위한 객체입니다. |
| ┃ ┣ 📁 `request` | 클라이언트로부터 받는 요청 데이터를 담는 DTO를 관리합니다. |
| ┃ ┗ 📁 `response` | 클라이언트에게 보낼 응답 데이터를 담는 DTO를 관리합니다. |
| ┣ 📁 `entity` | 데이터베이스 테이블과 1:1로 매핑되는 JPA 엔티티(Entity) 클래스를 관리합니다. |
| ┣ 📁 `global` | 프로젝트 전반에 걸쳐 사용되는 공통 설정 및 기능을 관리합니다. (Cross-cutting Concerns) |
| ┃ ┣ 📁 `config` | 보안(Spring Security), 데이터베이스(QueryDSL) 등 애플리케이션의 핵심 설정 클래스입니다. |
| ┃ ┣ 📁 `exception`| 서비스 전역에서 발생하는 예외를 처리하는 전역 예외 핸들러(Global Exception Handler)입니다. |
| ┃ ┗ 📁 `jwt` | JWT(JSON Web Token)의 생성, 검증, 필터링 등 인증/인가 관련 로직을 처리합니다. |
| ┣ 📁 `repository` | 데이터베이스에 접근하는 **D**ata **A**ccess **O**bject 계층입니다. JPA Repository 인터페이스를 정의합니다. |
| ┃ ┗ 📁 `impl` | QueryDSL을 사용하여 복잡하거나 동적인 쿼리를 직접 구현하는 클래스를 관리합니다. |
| ┣ 📁 `routes` | 비즈니스 로직 없이 단순 페이지 이동만을 담당하는 컨트롤러를 관리합니다. (예: `index.html` 렌더링) |
| ┗ 📁 `service` | 애플리케이션의 핵심 비즈니스 로직을 처리하는 계층입니다. |
|   ┗ 📁 `impl` | 서비스 인터페이스에 대한 실제 구현체를 관리합니다. (Interface-based-programming) |

---

## **3. 리소스 및 프론트엔드 (`src/main/resources`)**

설정 파일, 정적 리소스, HTML 템플릿 등 백엔드 로직 외의 모든 리소스를 관리합니다.

| 파일/폴더 | 역할 설명 |
| :--- | :--- |
| 📄 `application.yml` | DB 연결 정보, API 키 등 애플리케이션의 핵심 설정 파일입니다. **(Git 추적 제외 대상)** |
| 📄 `application-template.yml` | `application.yml`에 어떤 설정이 필요한지 알려주는 템플릿 파일입니다. |
| 📁 `static` | CSS, JavaScript, 이미지 등 브라우저가 직접 접근하는 정적(Static) 파일을 관리합니다. |
| ┃ ┣ 📁 `css` | 애플리케이션의 전체 스타일시트(CSS) 파일을 관리합니다. |
| ┃ ┣ 📁 `img` | 로고, 아이콘 등 이미지 파일을 관리합니다. |
| ┃ ┗ 📁 `js` | 프론트엔드 JavaScript 파일을 관리합니다. |
| ┃   ┣ 📁 `pages` | 각 페이지(메인, 마이페이지 등)별로 분리된 JavaScript 모듈을 관리합니다. |
| ┃   ┗ 📁 `utils` | API 호출, 인증 처리 등 여러 페이지에서 공통으로 사용되는 유틸리티 함수를 관리합니다. |
| ┗ 📁 `templates` | Thymeleaf를 사용한 서버 사이드 렌더링 HTML 파일을 관리합니다. |
|   ┗ 📁 `fragments`| 헤더, 푸터, 모달 등 여러 페이지에서 재사용되는 UI 조각(Fragment)을 관리합니다. |

---

## **4. 문서 (`docs`)**

프로젝트의 지적 자산을 체계적으로 보관하는 공간입니다.

| 파일/폴더 | 역할 설명 |
| :--- | :--- |
| 📄 `TERMS.md` | 서비스 이용 약관을 명시한 문서입니다. |
| 📄 `COMMUNITY_GUIDELINES.md`| 커뮤니티 활동 규칙 및 트롤 대응 정책을 안내하는 문서입니다. |
| 📁 `architecture` | API 명세, ERD 등 시스템의 기술적 설계 및 아키텍처 관련 문서를 관리합니다. |
| 📁 `guides` | 새로운 팀원을 위한 환경 설정, 주요 기능 사용법, 트러블슈팅 등 개발에 필요한 가이드를 제공합니다. |
| 📁 `meeting-notes`| 팀의 주요 의사결정 과정을 기록한 회의록을 날짜별로 관리합니다. |
| 📁 `planning` | 프로젝트의 초기 기획, 컨셉, 기능 정의 등 가장 근본적인 개요 문서를 관리합니다. |
| 📁 `rules` | 코딩 컨벤션, Git 워크플로우 등 팀 내부의 협업 규칙을 정의합니다. |

```
Ai-dea
📄 build.gradle              → Gradle 빌드 설정 파일
📄 settings.gradle           → 프로젝트 설정 파일 (멀티 모듈 구성 시 포함)
📄 gradlew                   → Gradle Wrapper 실행 스크립트 (Linux/Mac)
📄 gradlew.bat               → Gradle Wrapper 실행 스크립트 (Windows)
📄 .gitignore                → Git에 포함하지 않을 파일/폴더 설정
📄 README.md                 → 프로젝트 개요 및 실행 방법 문서
📄 LICENSE                   → 프로젝트 라이선스 명시
📄 CONTRIBUTING.md           → 기여 가이드라인
📁 .github                   → GitHub 전용 설정 및 템플릿 관리
 └── 📁 ISSUE_TEMPLATE             → 이슈 생성 시 사용하는 템플릿 모음
 │
 └── 📄 PULL_REQUEST_TEMPLATE.md   → PR(풀 리퀘스트) 작성 템플릿


📁 src                       → 애플리케이션 소스 코드
 └── 📁 main
      ├── 📁 java
      │    └── 📦 com.spring.aidea.vibefiction
      │         ├── 📁 controller      → API 요청을 처리하는 웹 계층
      │         ├── 📁 dto             → 요청/응답 데이터 전송 객체
      │         │    ├── 📁 request    → 클라이언트 요청 DTO
      │         │    └── 📁 response   → 서버 응답 DTO
      │         ├── 📁 entity          → JPA 엔티티 클래스 (DB 테이블 매핑)
      │         ├── 📁 global          → 전역적으로 사용하는 설정/공통 모듈
      │         │    ├── 📁 common     → 공통 유틸, 상수, 응답 포맷 등
      │         │    ├── 📁 config     → Spring 및 보안, JPA 등 설정 클래스
      │         │    ├── 📁 exception  → 예외 처리 클래스 및 핸들러
      │         │    └── 📁 jwt        → JWT 인증/인가 관련 클래스
      │         ├── 📁 repository      → DB 접근 계층 (JPA Repository 인터페이스)
      │         │    ├── 📁 custom     → 사용자 정의 쿼리 인터페이스
      │         │    └── 📁 impl       → 사용자 정의 쿼리 구현체
      │         ├── 📁 routes          → 라우트 정의 및 요청 경로 관리
      │         └── 📁 service         → 비즈니스 로직 계층
      │              └── 📁 impl       → 서비스 구현체
      │
      └── 📁 resources
           📄 application.yml           → 메인 환경설정 파일
           📄 application-template.yml  → API Key 등 민감정보 템플릿 (Git 미포함)
           📁 static
           │    ├── 📁 css             → 정적 CSS 파일
           │    ├── 📁 img             → 정적 이미지 파일
           │    └── 📁 js              → 정적 JS 파일
           │         ├── 📁 config     → 전역 설정 관련 JS
           │         ├── 📁 pages      → 페이지별 JS 모듈
           │         └── 📁 utils      → 공통 유틸 함수
           📁 templates
                └── 📁 fragments       → Thymeleaf 공용 HTML 조각 (헤더/푸터 등)

📁 docs                       → 프로젝트 문서
 ├── 📄 TERMS.md              → 서비스 이용약관
 ├── 📄 COMMUNITY_GUIDELINES.md → 커뮤니티 가이드라인
 ├── 📁 architecture          → 시스템 아키텍처 관련 문서
 ├── 📁 guides                → 가이드 문서
 │    └── 📁 troubleshooting  → 트러블슈팅 가이드
 ├── 📁 meeting-notes         → 회의록 정리
 ├── 📁 planning              → 프로젝트 개요 문서
 └── 📁 rules                 → 팀 규칙 문서 (코드 스타일, Git 워크플로우 등)
```
