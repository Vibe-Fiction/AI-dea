# Relai 플랫폼 - 시스템 아키텍처

본 문서는 Relai 플랫폼의 **전체 시스템 아키텍처**를 정의합니다.
플랫폼의 핵심 구성 요소(프론트엔드, 백엔드, 데이터베이스, AI 서비스, 외부 연동 등)와 그 상호작용 방식을 설명합니다.

**작성자**: [왕택준](https://github.com/TJK98)

**문서 버전**: v1.0

**대상 독자:**

* **프론트엔드 개발자**: API 호출 및 UI 흐름 이해
* **백엔드 개발자**: 서비스/도메인/DB 계층 아키텍처 파악
* **운영자/PM**: 시스템 구성과 데이터 흐름 이해
* **QA/테스터**: 아키텍처 기반 시나리오 테스트 설계
* **투자자/신규 합류자**: 플랫폼의 기술 구조 한눈에 이해

---

## 1. 전체 아키텍처 개요

Relai는 **웹 기반 AI 협업 소설 플랫폼**으로, 주요 아키텍처는 다음과 같이 구성됩니다.

* **Frontend (React/JS, Vite)**
  사용자 인터페이스, 소설 열람/투표/작성/AI 보조 요청 처리

* **Backend (Spring Boot)**
  REST API 제공, 비즈니스 로직, 인증/인가, 상태 머신 처리

* **Database (MariaDB / MySQL)**
  사용자, 소설, 회차, 제안, 투표, 로그, AI 상호작용 데이터 저장

* **AI Service (Gemini API)**

  * AI 소설 생성 (신규 소설 초안, 이어쓰기 초안)
  * 요약 생성 (Chapter Summary → STORY\_SUMMARY\_POLICY.md 참조)
  * AI 로그 저장 및 후처리

* **Scheduler**
  투표 마감, 자동 숨김(HIDDEN), 자동 완결(COMPLETED) 처리

* **Infra / Cloud** (추후 확장)
  Docker 기반 컨테이너, CI/CD (GitHub Actions), 클라우드 배포(AWS/EKS) 고려

---

## 2. 아키텍처 다이어그램 (Mermaid)

```mermaid
flowchart TD

    subgraph User[사용자]
        A[웹 브라우저\nFrontend (React/Vite)]
    end

    subgraph Frontend[Frontend Layer]
        A -->|REST API 호출| B[Backend API (Spring Boot)]
    end

    subgraph Backend[Backend Layer]
        B --> C[Service Layer]
        C --> D[Repository Layer]
        D --> E[(Database MariaDB)]
        C --> F[Scheduler\n(투표 마감, 자동완결)]
        C --> G[AI Service Adapter]
    end

    subgraph AIService[AI Service Layer]
        G --> H[Gemini API]
        H --> G
    end

    subgraph Data[Data Assets]
        E -->|저장| I[소설/회차/투표/로그 데이터]
        G -->|저장| J[AI Interaction Logs]
        G -->|생성| K[Story Summaries]
    end

    subgraph External[외부 연동]
        H -.-> L[OpenAI/Gemini 모델]
    end

    User <--> Frontend
```

---

## 3. 주요 데이터 흐름

1. **회원가입/로그인**

   * FE → BE → DB (`Users`, JWT 기반 인증)

2. **소설 생성**

   * FE 입력값 or AI 추천 → BE → DB(`Novels`, `Chapters`)

3. **이어쓰기 제안**

   * FE 직접 작성 or AI 제안 → BE → DB(`Proposals`)

4. **투표 & 채택**

   * FE 투표 → BE → DB(`Votes`)
   * Scheduler: 마감 후 상태 전환 (`ADOPTED`, `REJECTED`, `HIDDEN`)

5. **AI 상호작용**

   * Prompt 생성 (AiAssistServiceTj) → Gemini API 호출 → 응답 저장 (`AiInteractionLogs`)

---

## 4. 운영 고려 사항

* **보안**: JWT 기반 인증/인가, Spring Security Role 기반 정책
* **데이터 관리**: 소설/제안/투표/로그 분리 저장, 자동 요약 테이블 활용
* **성능 최적화**: Chapter 요약 캐싱, AI 호출 최소화
* **확장성**: 모놀리식(Spring Boot) → 마이크로서비스 전환 고려 가능

---

## 5. 추후 업데이트 계획

* **AI 요약 테이블 자동화** (챕터 증가 시 Context 축소)
* **멀티 모델 지원** (Gemini 외 Claude, GPT 등)
* **분산 캐싱 (Redis)** 도입
* **실시간 알림 시스템 (WebSocket)** 추가
* **클라우드 네이티브 배포** (AWS/EKS, GCP Kubernetes)
