# Relai 플랫폼 사용자 시나리오 & 시스템 흐름 상세 명세서

본 문서는 Relai에서 사용자가  
**회원가입 → 로그인 → 소설 생성 → 이어쓰기 → 투표 → 최다 득표작 반영**으로 이어지는 전 과정을,  
**수동 시나리오**와 **AI 보조 시나리오** 두 갈래 모두에 대해  
**UI 동작, 프론트 코드 흐름, API 계약, 서버 계층(Controller/Service/Repository), DB 변화, 스케줄링 로직**까지  
한 눈에 파악할 수 있도록 정리했습니다.

**작성자**: [왕택준](https://github.com/TJK98)

**문서 버전**: v1.0

**대상 독자:**
- **프론트엔드 개발자 (FE)**: 사용자 흐름(UI/라우팅)과 API 연동 확인
- **백엔드 개발자 (BE)**: 컨트롤러/서비스/리포지토리 계층 동작 및 DB 연계 파악
- **QA/테스터**: 시나리오 기반 테스트 케이스 설계 및 자동화 대상 식별
- **운영자/관리자**: 투표/채택 자동화, 계정 상태 예외 처리 규칙 이해
- **신규 합류자**: Relai 시스템 전반 흐름 온보딩 자료로 활용

---

## 목차

1. [공통 사전 지식](#공통-사전-지식)
2. [데이터 모델 개요](#데이터-모델-개요)
3. [시나리오 A: 수동 흐름](#시나리오-a-수동-흐름)
4. [시나리오 B: AI 보조 흐름](#시나리오-b-ai-보조-흐름)
5. [프론트엔드 라우팅 & 가드 동작](#프론트엔드-라우팅--가드-동작)
6. [엔드포인트 카탈로그(요약)](#엔드포인트-카탈로그요약)
7. [스케줄러 & 상태 머신](#스케줄러--상태-머신)
8. [운영/보안/품질 체크리스트](#운영보안품질-체크리스트)
9. [부록 A: 응답/요청 예시](#부록-a-응답요청-예시)
10. [부록 B: 자동 승격 구현 스케치](#부록-b-자동-승격-구현-스케치)
11. [부록 C: 환경 설정 요약](#부록-c-환경-설정-요약)
12. [부록 D: 텍스트 다이어그램](#부록-d-텍스트-다이어그램)

---

## 공통 사전 지식

- JWT 인증, FE 유틸, BE 기술스택, 시간/마감 규칙

## 데이터 모델 개요

- Users, Novels, Genres, NovelGenres, Chapters, Proposals, Votes, AiInteractionLogs

---

## 시나리오 A: 수동 흐름

1. 회원가입
2. 로그인
3. 새로운 소설 생성 (수동)
4. 이어쓰기 (수동 제안 등록)
5. 투표
6. 최다 득표작 업데이트 (스케줄러)

---

## 시나리오 B: AI 보조 흐름

-
    3) AI와 새로운 소설 생성
-
    4) AI와 이어쓰기 제안

---

## 프론트엔드 라우팅 & 가드 동작

- 페이지 매핑, 토큰 가드, 헤더 UI 갱신, 무한스크롤/카운트다운 동작

---

## 엔드포인트 카탈로그(요약)

- Auth: `/api/auth/signup`, `/api/auth/login`, `/api/auth/check-*`
- Genres: `/api/genres`
- Novels: `/api/novels`, `/api/novels/{id}`
- Chapters: `/api/chapters/{novelId}`
- Proposals: `/api/chapters/{chapterId}/proposals`
- Votes: `/api/vote/start`, `/api/vote/do`, `/api/vote/novels/{novelId}/proposals`
- AI: `/api/ai/novels/recommend`, `/api/ai/chapters/{chapterId}/continue`
- My Page: `/api/my-page`

---

## 스케줄러 & 상태 머신

- VoteScheduler (cron 기반)
- Proposal.status 흐름: `VOTING → ADOPTED|PENDING|REJECTED`

---

## 운영/보안/품질 체크리스트

- 운영: 마감 규칙, voteDeadline 설정
- 보안: JWT 강제, 중복투표/자기투표 금지
- 성능: 캐싱, N+1 방지
- UX/에러: safeJson, 401/403 처리

---

## 부록 A: 응답/요청 예시

- 장르 조회, 소설 생성, 이어쓰기 제안, 투표 데이터, 투표 요청

## 부록 B: 자동 승격 구현 스케치

- ADOPTED → Chapter 자동 반영 코드

## 부록 C: 환경 설정 요약

- 포트, DB, JPA, JWT, Gemini API, 파일 업로드

## 부록 D: 텍스트 다이어그램

```
[회원가입] → POST /api/auth/signup
[로그인] → POST /api/auth/login
[소설 생성] → POST /api/novels
[이어쓰기 제안] → POST /api/chapters/{id}/proposals
[투표] → POST /api/vote/do
[마감] → VoteScheduler
```
