# 개념 ERD 명세서

본 문서는 Relai 플랫폼의 데이터 모델을 **개념적 수준(Conceptual ERD)** 에서 정의하여, 서비스 전반의 구조를 이해하기 위한 기준 자료입니다.

**작성자:** [고동현](https://github.com/rhehdgus8831)

**문서 버전:** v1.0

**대상 독자:**
- **백엔드 개발자**: 엔티티 설계와 관계를 코드(Entity, Repository) 구현 시 참고
- **DBA**: 데이터베이스 논리/물리 모델링 전 사전 검토용
- **기획자/PM**: 서비스 전반의 데이터 흐름 및 주요 개체 이해
- **신규 합류자**: 시스템 핵심 데이터 구조를 빠르게 익히기 위한 온보딩 자료

---

## 주요 개체 (Entities)

### User
- **PK**: user_id
- **Attributes**: username, email, password, created_at
- **설명**: 플랫폼 사용자 기본 정보

### Novel
- **PK**: novel_id
- **Attributes**: title, synopsis, cover_image_url, created_at, status, visibility, author_id
- **설명**: 사용자가 작성한 소설의 메타데이터

### Chapter
- **PK**: chapter_id
- **Attributes**: novel_id (FK), title, content, created_at, order
- **설명**: 각 소설의 회차(챕터) 정보

### Proposal
- **PK**: proposal_id
- **Attributes**: chapter_id (FK), user_id (FK), content, created_at, vote_count
- **설명**: 이어쓰기 제안 정보

### Vote
- **PK**: vote_id
- **Attributes**: proposal_id (FK), user_id (FK), created_at
- **설명**: 제안에 대한 투표 기록

### AI Interaction Log
- **PK**: log_id
- **Attributes**: user_id (FK), novel_id (FK), request, response, created_at
- **설명**: AI 보조 기능 사용 내역 기록

---

## 관계 (Relationships)

- User ↔ Novel (1:N)
- Novel ↔ Chapter (1:N)
- Chapter ↔ Proposal (1:N)
- Proposal ↔ Vote (1:N)
- User ↔ Proposal (1:N)
- User ↔ Vote (1:N)

---

## 속성 (Attributes) 정리

| 엔티티 | 주요 속성 | 설명 |
|--------|-----------|------|
| User | username, email, password | 사용자 계정 정보 |
| Novel | title, synopsis, status, visibility | 소설 기본 정보 |
| Chapter | title, content, order | 소설의 각 회차 |
| Proposal | content, vote_count | 이어쓰기 제안 |
| Vote | user_id, created_at | 투표 기록 |
| AI Interaction Log | request, response | AI API 요청/응답 기록 |

---

## 설계 원칙

- 정규화된 구조 (3NF 기준) 유지
- 데이터 무결성 보장 (FK 제약조건 사용)
- 확장성 고려 (추후 AI 로그 분리, 통계용 테이블 추가 가능)

---

## 향후 확장 고려사항

- 투표 기간 및 상태 관리 테이블 추가 필요성 검토
- 제안 상태(Enum) 관리 컬럼 도입 가능성
- 로그 데이터 축적 시 별도 아카이브 DB 고려
