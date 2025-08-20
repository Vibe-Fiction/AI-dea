# Relai 플랫폼 API & 시퀀스 다이어그램 문서

**작성자**: [왕택준](https://github.com/TJK98)

**문서 버전**: 1.0

**대상 독자**: FE/BE 개발자, QA, 운영자

본 문서는 Relai 플랫폼의 **전체 사용자 시나리오 흐름**을

* **Mermaid 시퀀스 다이어그램**
* **OpenAPI 3.0 (Swagger) API 스펙**

두 가지 형식으로 제공합니다.

---

## 1. Mermaid 시퀀스 다이어그램

### A. 수동 흐름

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant FE as Frontend
    participant AUTH as Auth API
    participant GEN as Genre API
    participant NOV as Novels API
    participant CHP as Chapters API
    participant PROP as Proposals API
    participant VOTE as Vote API
    participant SCHED as VoteScheduler

    U->>AUTH: POST /api/auth/signup
    U->>AUTH: POST /api/auth/login → JWT
    U->>GEN: GET /api/genres
    U->>NOV: POST /api/novels
    FE->>VOTE: POST /api/vote/start
    U->>CHP: GET /api/chapters/{novelId}
    U->>PROP: POST /api/chapters/{chapterId}/proposals
    U->>VOTE: POST /api/vote/do {proposalId}
    SCHED->>VOTE: cron 실행 → 상태 업데이트 (ADOPTED/PENDING/REJECTED)
```

---

### B. AI 보조 흐름

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant FE as Frontend
    participant AI as AI Assist API
    participant GEN as Genre API
    participant NOV as Novels API
    participant PROP as Proposals API
    participant VOTE as Vote API

    U->>GEN: GET /api/genres
    U->>AI: POST /api/ai/novels/recommend
    AI-->>FE: novelTitle + chapter 초안
    U->>NOV: POST /api/novels
    FE->>VOTE: POST /api/vote/start

    U->>AI: POST /api/ai/chapters/{chapterId}/continue {instruction}
    AI-->>FE: suggestedTitle + content
    U->>PROP: POST /api/chapters/{chapterId}/proposals
    U->>VOTE: POST /api/vote/do {proposalId}
```
