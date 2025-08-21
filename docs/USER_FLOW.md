# Relai 플랫폼 사용자 시나리오 & 시스템 흐름 **상세 명세서**

본 문서는 Relai에서 사용자가 **회원가입 → 로그인 → 소설 생성 → 이어쓰기 → 투표 → 최다 득표작 반영**으로 이어지는 전 과정을, **수동 시나리오**와 **AI 보조 시나리오**
두 갈래 모두에 대해 **UI 동작, 프론트 코드 흐름, API 계약, 서버 계층(Controller/Service/Repository), DB 변화, 스케줄링 로직**까지 한 눈에 파악할 수 있도록 정리했습니다.

**작성자**: [왕택준](https://github.com/TJK98)

**문서 버전:** v1.0

**대상 독자:**
- **프론트엔드/백엔드 개발자**: 시스템 흐름 및 API 계약 이해, 구현/유지보수 참고
- **QA(테스터)**: 사용자 시나리오 기반 테스트 케이스 설계 참고
- **운영자/관리자**: 서비스 운영 중 발생할 수 있는 시나리오 파악 및 대응
- **신규 합류자(온보딩 멤버)**: Relai 플랫폼 전체 흐름을 빠르게 이해하기 위한 학습 자료

---

## 목차

1. [공통 사전 지식](#공통-사전-지식)
2. [데이터 모델 개요](#데이터-모델-개요)
3. [시나리오 A: 수동 흐름](#시나리오-a-수동-흐름)

    *
        1. 회원가입
    *
        2. 로그인
    *
        3. 새로운 소설 생성(수동)
    *
        4. 이어쓰기(수동 제안 등록)
    *
        5. 투표
    *
        6. 최다 득표작 업데이트(상태 확정 & 승격)
4. [시나리오 B: AI 보조 흐름](#시나리오-b-ai-보조-흐름)

    * 3) AI와 새로운 소설 생성
    * 4) AI와 이어쓰기 제안
5. [프론트엔드 라우팅 & 가드 동작](#프론트엔드-라우팅--가드-동작)
6. [엔드포인트 카탈로그(요약)](#엔드포인트-카탈로그요약)
7. [스케줄러 & 상태 머신](#스케줄러--상태-머신)
8. [운영/보안/품질 체크리스트](#운영보안품질-체크리스트)
9. [부록 A: 응답/요청 예시](#부록-a-응답요청-예시)
10. [부록 B: 자동 승격(ADOPTED→Chapter) 구현 스케치](#부록-b-자동-승격adoptedchapter-구현-스케치)
11. [부록 C: 환경 설정 요약](#부록-c-환경-설정-요약)
12. [부록 D: 텍스트 다이어그램](#부록-d-텍스트-다이어그램)

---

## 공통 사전 지식

* **인증**: JWT(Bearer). FE는 `localStorage.vibe_fiction_token`에 저장, 모든 보호된 API에 `Authorization: Bearer <token>` 포함.
* **프론트 공통 유틸**

    * `utils/token.js`: `saveToken/getToken/removeToken`
    * `utils/api.js`: `request()`(헤더 자동, `safeJson()`로 응답 안전 파싱)
    * `utils/ui.js`: 헤더 UI 토글, 로그인 모달 열기/닫기
    * `app.js`: 라우팅 가드, 페이지 모듈 동적 import
* **백엔드**

    * Spring Boot 3.5, Spring Security, JPA, QueryDSL
    * DB: MariaDB
    * JWT: `io.jsonwebtoken:jjwt-*`
    * AI: Google Gemini REST 호출, 상호작용 로그 `AiInteractionLogs` 저장
* **시간/마감**

    * 현재 테스트 기준: 최근 챕터 기준 **투표 마감 1분**(코드 상).
    * 운영 전환 시 실제 규칙(예: 3일, 자정 마감 등)로 교체 필요.

---

## 데이터 모델 개요

* `Users`(회원)
* `Novels`(소설) — `author: Users`
* `Genres`(장르, Enum `GenreType`)
* `NovelGenres`(소설:장르 N\:M)
* `Chapters`(회차) — `novel: Novels`, `author: Users`, (선택)`fromProposal: Proposals`
* `Proposals`(이어쓰기 제안) — `chapter: Chapters`, `proposer: Users`, `status: VOTING|ADOPTED|PENDING|REJECTED`,
  `voteDeadline`
* `Votes`(투표) — 유저가 제안에 1회 투표. 동일 챕터 내 중복 투표 금지.
* `AiInteractionLogs`(AI 호출 로그) — prompt/result, (선택)관련 제안/챕터 연결

---

## 시나리오 A: 수동 흐름

### 1) 회원가입 (Sign Up)

#### [UI]

* `/signup` 페이지에서 입력: 아이디/이메일/닉네임/비밀번호/생년월일.
* 실시간 검사(디바운스 500ms):

    * `GET /api/auth/check-username?loginId=...`
    * `GET /api/auth/check-email?email=...`
    * `GET /api/auth/check-nickname?nickname=...`
* 클라이언트 1차 정규식 → 성공 시 서버 중복 검사 메시지 반영.

#### [요청]

`POST /api/auth/signup`

```json
{
    "loginId": "alice_01",
    "email": "alice@example.com",
    "nickname": "앨리스",
    "password": "Abcdef12!",
    "birthDate": "2000-01-01"
}
```

#### [서버 처리]

* `SignUpServiceKO.signUp`

    * `UsersRepository.existsBy*` 중복 확인
    * 비밀번호 해시(`PasswordEncoder`)
    * `Users` 저장

#### [DB 변화]

* `users` 1행 추가

#### [응답/후속 UI]

* 성공 토스트 → `/`로 이동, 로그인 유도

---

### 2) 로그인 (Login)

#### [UI]

* `/` 헤더 로그인 → 모달 오픈
* 인증 필요한 페이지 접근 중이었다면 `localStorage.redirect_after_login`에 대상 URL 존재

#### [요청]

`POST /api/auth/login`

```json
{
    "loginIdOrEmail": "alice_01",
    "password": "Abcdef12!"
}
```

#### [서버 처리]

* `LoginServiceKO.authenticate`

    * loginId 우선, 없으면 email로 조회
    * `PasswordEncoder.matches`
    * `JwtProvider.generateToken(loginId, role)`

#### [응답/후속 UI]

* 응답의 `token`을 `localStorage.vibe_fiction_token`에 저장
* 헤더 UI 즉시 갱신(로그인/회원가입 → 마이페이지/로그아웃)
* `redirect_after_login` 있으면 그 URL로, 없으면 홈으로

---

### 3) 새로운 소설 생성(수동)

#### [UI]

* 홈의 “새 소설 쓰기” 클릭 → 라우트 가드

    * 미로그인: 로그인 모달 + 알림
    * 로그인: `/novels/create`
* 진입 시 장르 드롭다운:

    * `GET /api/genres`
    * `GenreController.getGenres → GenreService.getAllGenres`(Enum→DTO)

#### [입력/행동]

* 장르(최대 3), 제목, 시놉, 1화 본문 입력

#### [요청]

`POST /api/novels`

```json
{
    "title": "달 아래 검객",
    "synopsis": "달빛 아래에서만 강해지는 신입 검사의 이야기",
    "firstChapterTitle": "제1화. 달빛의 시험",
    "firstChapterContent": "…본문…",
    "genres": [
        "FANTASY",
        "ACTION"
    ],
    "visibility": "PUBLIC"
}
```

#### [서버 처리]

* `NovelServiceTj.create`

    1. 작성자 확인(인증 유저)
    2. 문자열 → `Genres.GenreType.valueOf()`
    3. `GenresRepository.findByNameIn(enumList)`

        * 갯수 불일치 시 4xx
    4. `Novels` 생성
    5. `Chapters`(1화) 생성 및 `novel.addChapter()`
    6. `NovelsRepository.save`(cascade로 1화 포함)

#### [DB 변화]

* `novels`, `chapters(1화)`, `novel_genres` 생성

#### [응답/후속 UI]

* `NovelCreateResponseTj { novelId, firstChapterId }`
* **투표 세션 시작 트리거(중요)**

    * FE: `POST /api/vote/start { novelId }`
    * 백엔드(구현 필요 시): 해당 소설의 **최신 챕터** 기준 `voteDeadline` 설정 등 초기화
* `/chapters?novelId={id}`로 이동

---

### 4) 이어쓰기(수동 제안 등록)

#### [UI: 소설 상세]

* `/chapters?novelId=...`

    * 병렬 로드:

        * `GET /api/novels/{novelId}`
        * `GET /api/chapters/{novelId}`
* “현재 진행 중인 이어쓰기” 버튼 → `/vote-page?novelId=...`

#### [UI: 투표 페이지 → 이어쓰기 진입]

* `/vote-page?novelId=...` 진입 시:

    * `GET /api/vote/novels/{novelId}/proposals`
    * 서비스 로직 핵심:

        * `lastChapter = chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)`
        * `proposals = proposalsRepository.findByChapter_ChapterIdOrderByVoteCountDesc(..., Pageable/Limit)`
        * `deadlineInfo.closingTime = getVotingDeadline(lastChapter)`
        * `latestChapterId = lastChapter.chapterId`
* FE는 `latestChapterId`로 이어쓰기 버튼 링크 구성
  → `/proposals/create?chapterId={latestChapterId}`

#### [UI: 이어쓰기 제안 페이지]

* `/proposals/create?chapterId=...`
* 제목/내용 작성(수동)

#### [요청]

`POST /api/chapters/{chapterId}/proposals`

```json
{
    "title": "달빛 검의 각성",
    "content": "…이어쓰기 본문…"
}
```

#### [서버 처리]

* `ProposalServiceTj.create`

    * proposer, chapter 조회
    * `Proposals.create` 후 저장
    * 응답에 `novelId` 포함(챕터→소설)

#### [DB 변화]

* `Proposals.create(...)` → `voteDeadline = LocalDateTime.now().plusDays(3)`
* "현재는 chapter.getCreatedAt() 기준으로 테스트용 1분 뒤(00:01:00)에 마감"
* 운영 전환 시 "3일 뒤 23:59:59" 같은 룰로 교체할 예정

#### [응답/후속 UI]

* 성공 → `/vote-page?novelId={novelId}`로 이동

---

### 5) 투표

#### [UI]

* 리스트(득표수/순위/카운트다운). 카드 클릭→모달→“투표하기”

#### [요청]

`POST /api/vote/do`

```json
{
    "proposalId": 123
}
```

헤더: `Authorization: Bearer <token>`

#### [서버 처리]

* `VoteServiceMj.createVote`

    * 사용자/제안/챕터 조회
    * 마감 검사(`now > voteDeadline` → 차단)
    * **중복 방지**

        * 같은 제안: `existsByUserAndProposal`
        * 같은 챕터의 다른 제안:
          `existsByUser_UserIdAndProposal_Chapter_ChapterId(userId, chapterId)`
    * **자기 제안 투표 금지**(proposer == user)
    * `Votes` 저장 + `proposal.incrementVoteCount()`

> 권장: 장기적으론 `voteCount`는 파생값으로 관리(조회 시 COUNT)하거나, DB 원자적 증가 사용 등 동시성 대비 필요.

#### [DB 변화]

* `votes` 1행 추가
* `proposal.voteCount` 증가

#### [응답/후속 UI]

* 성공 알림 → 새로고침 후 반영

---

### 6) 최다 득표작 업데이트(상태 확정 & 승격)

#### [트리거]

* **스케줄러**: `VoteScheduler.updateExpiredProposalStatus`
  (cron: `59 59 23 * *` → 매일 자정(23:59:59)에 실행)
    * 현재는 **테스트용으로 `@Scheduled(cron="5 * * * *")` (매 분 5초마다 실행)**
    * 운영 시 `@Scheduled(cron="59 59 23 * *")` (매일 자정 실행) 교체 필요
    * `proposalsRepository.findByVoteDeadlineBeforeAndStatus(now, VOTING)`
    * 만료된 제안 묶음별 최다 득표 계산

#### [상태 결정 규칙]

* 만료 대상 없음 → 종료
* 최다 득표 0이면서 후보 ≥1 → 전부 `PENDING`
* 최다 득표 동률(2개 이상) → 동률 `PENDING`, 나머지 `REJECTED`
* 최다 득표 1개 → 그 제안 `ADOPTED`, 나머지 `REJECTED`
* `saveAll` 일괄 업데이트

#### [승격(Chapter 반영)]

* **현재 코드 기준**: 스케줄러는 상태만 갱신
* 선택지

    1. **수동 승격**: 운영/작성자가 `ChapterServiceTj.create(..., fromProposalId=ADOPTED_ID)` 호출
    2. **자동 승격(추가 개발)**: 스케줄러가 ADOPTED 감지 시 `ChapterServiceTj.create()` 실행 (부록 B 참고)

---

## 시나리오 B: AI 보조 흐름

> 회원가입/로그인/투표/상태 확정은 A와 동일.
> 차이는 “초안 생성”과 “이어쓰기 제안 작성”에 AI 보조가 개입.

### 3) AI와 새로운 소설 생성

#### [UI]

* `/novels/create`
* 장르 ≥1 선택 + 시놉시스 입력 → **“AI 와 함께쓰기”** 클릭

#### [요청]

`POST /api/ai/novels/recommend`

```json
{
    "genre": "FANTASY",
    "synopsis": "달빛 아래에서만 강해지는 신입 검사의 이야기"
}
```

#### [서버 처리]

* `AiAssistServiceTj.recommendForNewNovel`

    * 프롬프트 구성(형식/제약/구분자 `---`)
    * `GeminiApiServiceImpl.generateContent(prompt)`
    * 결과 텍스트를 `\n---\n`로 분해:

        * 1줄: `novelTitle`
        * 2줄: `firstChapterTitle`
        * 3줄\~: `firstChapterContent`
    * `AiInteractionLogs` 저장

#### [DB 변화]

* `ai_interaction_logs` 1행 추가

#### [응답/후속 UI]

* 제목/1화 제목/본문 자동 채움(유저 편집 가능)
* **“소설 생성하기”** → `POST /api/novels`(A-3과 동일)
* 이후 `POST /api/vote/start` → `/chapters?novelId=...`

---

### 4) AI와 이어쓰기 제안

#### [UI]

* `/proposals/create?chapterId=...`
* **“AI 도움받기”** 클릭 → 간단 지시문 입력(예: “빌런이 약점을 간파하는 반전”)

#### [요청]

`POST /api/ai/chapters/{chapterId}/continue`

```json
{
    "instruction": "빌런이 검객의 약점을 눈치채는 반전"
}
```

#### [서버 처리]

* `AiAssistServiceTj.continueForChapter`

    * 기준 챕터까지 스토리 컨텍스트 문자열 구성
    * 프롬프트(형식/제약/구분자 `---`) + 사용자 instruction
    * Gemini 호출 → `\n---\n` 분해:

        * 1줄: `suggestedTitle`
        * 이후: `suggestedContent`
    * `AiInteractionLogs` 저장(선택적으로 제안과 연결)

#### [DB 변화]

* `ai_interaction_logs` 1행 추가

#### [응답/후속 UI]

* 폼 자동 채움 → 유저가 **완료**(제출)
  → `POST /api/chapters/{chapterId}/proposals` (A-4와 동일)
  → 이후 투표/마감/상태 결정 동일

---

## 프론트엔드 라우팅 & 가드 동작

* **라우트 매핑**(`PAGE_CONFIG`)

    * `/` → `home`
    * `/my-page`(인증 필요)
    * `/chapters`(쿼리 `novelId`)
    * `/novels/create`(인증 필요)
    * `/proposals/create`(쿼리 `chapterId`, 인증 필요)
    * `/vote-page`(쿼리 `novelId`, 인증 필요)
    * `/signup`

* **가드**(`app.js`)

    * 미정의 라우트 → `/`
    * `requiresAuth` && 토큰 없음 → 알림 + `/` + 로그인 모달 유도
    * 클릭 핸들러: 로고/회원가입/“새 소설 쓰기” 버튼(토큰 없으면 모달)

* **헤더 UI**

    * `updateHeaderUI()`가 토큰 존재 여부로 버튼 토글
    * 로그인 성공 시 `redirect_after_login` 처리

* **주요 페이지 동작**

    * **Home**: `/api/novels?page=&size=` 무한 스크롤, 카드 클릭→`/chapters?novelId=...`
    * **ChaptersPage**: 소설/챕터 병렬 로드, 모달 뷰어(이전/다음), “현재 진행 중인 이어쓰기”→`/vote-page?novelId=...`
    * **VotePage**: `/api/vote/novels/{novelId}/proposals` 로드 → `latestChapterId` 확보 → 이어쓰기 버튼 →
      `/proposals/create?chapterId=...`
      카운트다운(`deadlineInfo.closingTime`) 동작
    * **CreateProposal**: 쿼리 `chapterId` 필수. (선택)AI 도움, 제출 후 `/vote-page?novelId=...`
    * **CreateNovel**: 장르 로드→입력→(선택)AI→제출→(성공) `POST /api/vote/start`→`/chapters?novelId=...`
    * **MyPage**: `/api/my-page`(인증), 프로필 수정(이미지 5MB/확장자 검사, 비번 변경 시 현재 비번 필수)

---

## 엔드포인트 카탈로그(요약)

* **Auth**

    * `POST /api/auth/signup`
    * `POST /api/auth/login`
    * `GET  /api/auth/check-username?loginId=...`
    * `GET  /api/auth/check-email?email=...`
    * `GET  /api/auth/check-nickname?nickname=...`
* **Genres**

    * `GET  /api/genres`
* **Novels**

    * `GET  /api/novels?page=&size=` (홈)
    * `POST /api/novels`
    * `GET  /api/novels/{novelId}`
* **Chapters**

    * `GET  /api/chapters/{novelId}`
    * `POST /api/novels/{novelId}/chapters`(선택 기능: 작가가 직접 회차 등록)
* **Proposals**

    * `POST /api/chapters/{chapterId}/proposals`
    * `GET  /api/vote/novels/{novelId}/proposals`(투표 화면 데이터)
* **Votes**

    * `POST /api/vote/start`(소설 최신 챕터 기준 투표 세션 시작/마감시각 설정)
    * `POST /api/vote/do`(투표)
* **AI**

    * `POST /api/ai/novels/recommend`
    * `POST /api/ai/chapters/{chapterId}/continue`
* **My Page**

    * `GET  /api/my-page`
    * `POST /api/my-page`(프로필 수정: multipart/form-data)

---

## 스케줄러 & 상태 머신

* **VoteScheduler.updateExpiredProposalStatus** (cron `5 * * * * *`)

    * 조건: `voteDeadline < now` && `status = VOTING`
    * 집합별 최다 득표 계산 후 상태 일괄 업데이트

* **Proposal.status 상태 머신**

    * `VOTING` → (마감) →

        * 단독 최다 득표: `ADOPTED`
        * 동률 최다 득표: 동률 모두 `PENDING`(재투표 등 운영 정책에 따라 후속)
        * 전원 0표: 전원 `PENDING`
    * `ADOPTED` → (수동/자동) → `Chapters` 생성, (선택)`fromProposal` 연결
    * `REJECTED`/`PENDING`은 표시/보관 정책에 따름

---

## 운영/보안/품질 체크리스트

**운영**

* `POST /api/vote/start`에서 **voteDeadline** 반드시 설정(챕터/제안 기준 정책 합의)
* 마감 규칙 운영 값 적용(예: 3일 뒤 23:59:58, 타임존 명확화)
* 스케줄러 주기/오차(매 분 5초) 및 서버 시간 싱크 확인

**보안**

* `/api/genres`만 `permitAll`, 나머지 생성/수정/투표는 **JWT 필수**
* 투표: 동일 챕터 중복 투표/자기 투표 금지 서버 단에서 강제
* 파일 업로드: 확장자/크기 양단 검사, 저장 경로 화이트리스트

**성능**

* 장르 리스트 캐싱(서버/ETag/클라)
* N+1 방지: `@EntityGraph`(novel.author, novelGenres.genre)
* 투표 수 합산 전략 점검(실시간 COUNT vs 캐싱 필드 동기화)

**UX/에러 핸들링**

* `safeJson()`로 204/비JSON/깨진 JSON 방어
* 401/403 구분 안내, 리다이렉션/모달 UX 일관성
* 무한 스크롤: 빈 데이터/오류 시 반복 요청 방지

---

## 부록 A: 응답/요청 예시

### A-1. 장르 리스트

`GET /api/genres` → 200

```json
[
    {
        "code": "FANTASY",
        "description": "판타지"
    },
    {
        "code": "ACTION",
        "description": "액션"
    },
    ...
]
```

### A-2. 소설 생성

`POST /api/novels` → 201

```json
{
    "success": true,
    "message": "Novel created",
    "data": {
        "novelId": 42,
        "firstChapterId": 101
    }
}
```

### A-3. 이어쓰기 제안 생성

`POST /api/chapters/{chapterId}/proposals` → 201

```json
{
    "success": true,
    "message": "Proposal created",
    "data": {
        "proposalId": 555,
        "novelId": 42
    }
}
```

### A-4. 투표 화면 데이터

`GET /api/vote/novels/{novelId}/proposals` → 200

```json
{
    "success": true,
    "data": {
        "latestChapterId": 101,
        "deadlineInfo": {
            "closingTime": "2025-08-23 10:00:00"
        },
        "proposals": [
            {
                "proposalId": 555,
                "proposalTitle": "달빛 검의 각성",
                "proposalAuthor": "앨리스",
                "proposalContent": "…",
                "voteCount": 7,
                "novelName": "달 아래 검객"
            }
        ]
    }
}
```

### A-5. 투표

`POST /api/vote/do` → 200

```json
{
    "success": true,
    "message": "Vote accepted"
}
```

---

## 부록 B: 자동 승격(ADOPTED→Chapter) 구현 스케치

> 스케줄러에서 상태 결정 후, ADOPTED 제안을 자동으로 정식 회차로 승격.

```java
// 예: VoteScheduler.updateExpiredProposalStatus 내부 또는 후속 메서드
private void promoteAdoptedProposals(List<Proposals> processedProposals) {
    processedProposals.stream()
        .filter(p -> p.getStatus() == ADOPTED)
        .forEach(p -> {
            Chapters base = p.getChapter(); // 기준 챕터
            Novels novel = base.getNovel();
            Users author = p.getProposer(); // 혹은 novel.author 등 정책에 따라

            ChapterCreateRequestTj req = new ChapterCreateRequestTj(
                /* title */ p.getTitle(),
                /* content */ p.getContent()
            );

            Chapters created = chapterService.createFromProposal(
                novel.getNovelId(),
                author.getUserId(),
                req,
                p.getProposalId() // fromProposalId
            );

            // 필요 시 로그/알림
        });
}
```

* **정책 포인트**

    * 승격 회차의 **author**는 누구로 할지(원작자 vs 제안자 vs 공동저자)
    * `chapterNumber` 증가 정책(최신+1)
    * 회차 생성 후 다음 투표 세션 자동 시작 여부

---

## 부록 C: 환경 설정 요약

* **포트**: `server.port=9009`
* **DB**: MariaDB (`jdbc:mariadb://localhost:3306/ai-dea`, `root/mariadb`)
* **JPA**

    * DDL: `update`
    * Naming: `CamelCaseToUnderscoresNamingStrategy`
    * Dialect: `MariaDBDialect`
* **JWT**

    * `jwt.secret` (운영은 환경변수 권장)
    * `jwt.expiration=86400000`(24h)
* **Gemini**

    * URL: `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={apiKey}`
    * KEY: `${GEMINI_API_KEY:}`
* **파일 업로드**

    * `file.upload.location=${user.home}/aidea/uploads/`

---

## 부록 D: 텍스트 다이어그램

```
[회원가입]
  → POST /api/auth/signup
  → users(+)

[로그인]
  → POST /api/auth/login
  → JWT 발급 → FE 저장

[새 소설 생성(수동 또는 AI 초안)]
  → (선택) POST /api/ai/novels/recommend → AiInteractionLogs(+)
  → POST /api/novels → novels(+), chapters(1화)(+), novel_genres(+)
  → POST /api/vote/start (최신 챕터 기준 voteDeadline 설정)
  → /chapters?novelId=...

[이어쓰기 제안(수동 또는 AI 보조)]
  → /vote-page?novelId=... (latestChapterId/closingTime 로드)
  → /proposals/create?chapterId=latestChapterId
      (선택) POST /api/ai/chapters/{chapterId}/continue → AiInteractionLogs(+)
      POST /api/chapters/{chapterId}/proposals → proposals(+)

[투표]
  → POST /api/vote/do → votes(+), proposal.voteCount++

[마감 & 상태 확정(스케줄러)]
  → find VOTING where voteDeadline < now
  → 최다 득표 계산
  → 상태: ADOPTED | PENDING | REJECTED

[승격(선택: 자동/수동)]
  → (자동/수동) ChapterService.create(..., fromProposalId=ADOPTED)
  → chapters(+)
```

---
