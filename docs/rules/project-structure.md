# 프로젝트 구조 규칙 (Vibe Fiction)

> [↩ 규칙 허브로 돌아가기](../../CONTRIBUTING.md)

본 문서는 **Team Ai-dea**가 개발하는 **Vibe Fiction** 프로젝트에서 사용하는 디렉토리 구조, 클래스 분리 등 일관된 개발 구조를 정의합니다.

작성자: [왕택준](https://github.com/TJK98)

---

## 1. 디렉토리 구조: 계층형 아키텍처 (Layered Architecture)

우리 프로젝트는 전통적인 **계층형 아키텍처**를 채택합니다. 이는 각 계층의 역할을 명확히 분리하여 코드의 재사용성과 유지보수성을 높이는 구조입니다.

### 1.1 패키지 구성 원칙

모든 소스 코드는 역할에 따라 `controller`, `service`, `dto`, `entity`, `repository`, `global` 등의 패키지로 나누어 관리합니다.

- **`controller`**: HTTP 요청을 받아 비즈니스 로직(Service)으로 연결하는 역할 (API 엔드포인트)
- **`service`**: 핵심 비즈니스 로직을 처리하는 역할
- **`dto`**: 계층 간 데이터 전송을 위한 객체 (Data Transfer Object)
- **`entity`**: 데이터베이스 테이블과 매핑되는 JPA 엔티티
- **`repository`**: 데이터베이스에 접근하는 JPA Repository 인터페이스
- **`global`**: 전역적으로 사용되는 설정, 예외 처리, 유틸리티 등

### 1.2 전체 구조 예시

```
Ai-dea
📄 build.gradle  
📄 settings.gradle  
📄 gradlew  
📄 gradlew.bat  
📄 .gitignore  
📄 README.md  
📄 LICENSE  
📄 CONTRIBUTING.md  

📁 src
 └── 📁 main
      ├── 📁 java
      │    └── 📦 com.spring.aidea.vibefiction
      │         ├── 📁 controller
      │         ├── 📁 dto
      │         │    ├── 📁 request
      │         │    └── 📁 response
      │         ├── 📁 entity
      │         ├── 📁 global
      │         │    ├── 📁 common
      │         │    ├── 📁 config
      │         │    ├── 📁 exception
      │         │    └── 📁 jwt
      │         ├── 📁 repository
      │         │    ├── 📁 custom
      │         │    └── 📁 impl
      │         ├── 📁 routes
      │         └── 📁 service
      │              └── 📁 impl
      │
      └── 📁 resources
           📄 application.yml  
           📄 application-template.yml  
           📁 static
           │    ├── 📁 css
           │    ├── 📁 img
           │    └── 📁 js
           │         ├── 📁 config
           │         ├── 📁 pages
           │         └── 📁 utils
           📁 templates
                └── 📁 fragments

📁 docs
 ├── 📄 TERMS.md  
 ├── 📄 COMMUNITY_GUIDELINES.md  
 ├── 📁 architecture
 │
 ├── 📁 guides
 │    └── 📁 troubleshooting
 │
 ├── 📁 meeting-notes
 │
 ├── 📁 overview 
 │
 └── 📁 rules
```

> ❗ **핵심 작업 원칙**:
>
> *   **새로운 기능을 만들 때**: 해당 기능에 필요한 `Controller`, `Service`, `DTO` 등을 각각의 역할에 맞는 패키지에 추가합니다.
> *   **파일 이름**: `도메인 + 역할` 형식으로 명명하여 가독성을 높입니다. (예: `UserController`, `NovelService`)

---

## 2. 테스트 디렉토리 구조: 개인별 패키지 관리

테스트 코드는 충돌을 최소화하고 각자의 작업 공간을 명확히 하기 위해 **개인별 초성(이니셜) 패키지** 내에서 작성합니다.

### 2.1 구성 원칙

- `src/test/java/com/vibefiction` 경로 아래에 **자신의 이니셜로 된 패키지를 생성**합니다.
- 해당 패키지 안에서 자유롭게 테스트 코드를 작성합니다. 이렇게 하면 다른 팀원의 테스트 코드와 파일명이 겹치거나 충돌할 일이 없습니다.

### 2.2 구조 예시

```
📁 src/test/java/com/vibefiction
│
├── 📁 tj                     # 왕택준 개발자 테스트 공간
│   ├── 📄 UserServiceTestTj.java
│   └── 📄 NovelControllerTestTj.java
│
├── 📁 sh                     # 백승현 개발자 테스트 공간
│   └── ...
│
├── 📁 mj                     # 송민재 개발자 테스트 공간
│   └── ...
│
├── 📁 dh                     # 고동현 개발자 테스트 공간
│   └── ...
│
├── 📄 AiDeaApplicationTests.java  # 공용 통합 테스트 (필요 시 사용)
```

> 🔗 **개인 식별용 네이밍 규칙**에 대한 자세한 내용은 **[Git 워크플로우 규칙 문서](./Git-Workflow.md)**를 참고하세요.

> 🔗 **테스트 코드 작성 스타일**에 대한 자세한 내용은 **[테스트 코드 규칙 문서](./Test-Code-Convention.md)**를 참고하세요.
