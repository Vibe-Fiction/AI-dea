# API 명세서

**작성자**: [백승현](https://github.com/Sirosho)

| 항목       | 내용                          |
|----------|-----------------------------|
| 팀명       | Ai-dea                      |
| 프로젝트명    | VibeFiction                 |
| 플랫폼명     | Relai                       |
| 작성일      | 2025-08-20                  |
| 버전       | v1.0.0                      |
| Base URL | `http://localhost:9009/api` |
| 인증 방식    | JWT Bearer Token            |
| 응답 형식    | JSON                        |

---

## API 카테고리
- [회원 인증 관리](#apiauth---회원-인증-관리)
- [소설 관리](#apinovels---소설-관리)
- [챕터 관리](#apinovelsnovelidchapters---챕터-관리)
- [챕터 조회](#apichapters---챕터-조회)
- [제안 관리](#apichapterschapteridproposals---제안-관리)
- [작가 선택](#apichapterschapteridselect-winner---작가-선택)
- [투표 관리](#apivote---투표-관리)
- [AI 기능](#apia---ai-기능)
- [장르 관리](#apigenres---장르-관리)
- [사용자 프로필](#apimy-page---사용자-프로필)
- [오류 처리](#http-상태-코드-및-오류-처리)

---

## `/api/auth` - 회원 인증 관리

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **POST** | `/api/auth/signup` | 회원가입 | ❌ |
| **POST** | `/api/auth/login` | 로그인 | ❌ |
| **GET** | `/api/auth/check-username` | 사용자명 중복 체크 | ❌ |
| **GET** | `/api/auth/check-email` | 이메일 중복 체크 | ❌ |
| **GET** | `/api/auth/check-nickname` | 닉네임 중복 체크 | ❌ |

### POST `/api/auth/signup`
**요청**
```json
{
  "loginId": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "nickname": "테스트유저"
}
```

**응답 200**
```json
{
  "success": true,
  "message": "회원가입이 성공적으로 완료되었습니다.",
  "data": {
    "userId": 1,
    "loginId": "testuser",
    "email": "test@example.com",
    "nickname": "테스트유저"
  }
}
```

---

### POST `/api/auth/login`
**요청**
```json
{
  "loginIdOrEmail": "testuser",
  "password": "password123"
}
```

**응답 200**
```json
{
  "success": true,
  "message": "로그인이 완료되었습니다.",
  "data": {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "eyJhbGciOi...",
    "user": {
      "userId": 1,
      "loginId": "testuser",
      "nickname": "테스트유저"
    }
  }
}
```

---

### GET `/api/auth/check-username?loginId={loginId}`
**응답 200**:
```json
{
  "success": true,
  "message": "사용 가능한 사용자명입니다.",
  "data": false  // false: 사용가능, true: 중복
}
```

---

## `/api/novels` - 소설 관리

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **POST** | `/api/novels` | 새 소설 생성 | ✅ |
| **GET** | `/api/novels` | 소설 목록 조회 (페이지네이션) | ❌ |
| **GET** | `/api/novels/{novelId}` | 특정 소설 상세 조회 | ❌ |

### POST `/api/novels`
**요청**:
```json
{
  "title": "마법학교 에테르",
  "synopsis": "평범한 소년이 마법학교에 입학하면서 벌어지는 모험 이야기",
  "genre": "FANTASY",
  "firstChapter": {
    "title": "입학 통지서",
    "content": "내 이름이 적힌 붉은 봉랍의 편지. 그것은 마법학교 에테르의 입학 통지서였다."
  }
}
```

**응답 201**:
```json
{
  "success": true,
  "message": "새로운 소설이 성공적으로 생성되었습니다.",
  "data": {
    "novelId": 1,
    "title": "마법학교 에테르",
    "synopsis": "평범한 소년이 마법학교에 입학하면서 벌어지는 모험 이야기",
    "genre": "FANTASY",
    "authorId": 1,
    "authorNickname": "이야기꾼조씨",
    "createdAt": "2025-08-20T10:00:00"
  }
}
```

### GET `/api/novels?page=0&size=8`
**응답 200**:
```json
[
  {
    "novelId": 1,
    "title": "마법학교 에테르",
    "synopsis": "평범한 소년이 마법학교에 입학하면서 벌어지는 모험 이야기",
    "genre": "FANTASY",
    "authorNickname": "이야기꾼조씨",
    "createdAt": "2025-08-20T10:00:00",
    "chapterCount": 5,
    "viewCount": 1524,
    "status": "ONGOING"
  }
]
```

### GET `/api/novels/{novelId}`
**응답 200**:
```json
{
  "novelId": 1,
  "title": "마법학교 에테르",
  "synopsis": "평범한 소년이 마법학교에 입학하면서 벌어지는 모험 이야기",
  "genre": "FANTASY",
  "authorNickname": "이야기꾼조씨",
  "createdAt": "2025-08-20T10:00:00",
  "viewCount": 1524,
  "status": "ONGOING",
  "chapters": [
    {
      "chapterId": 1,
      "chapterNumber": 1,
      "title": "입학 통지서",
      "authorNickname": "이야기꾼조씨",
      "createdAt": "2025-08-20T10:00:00"
    }
  ]
}
```

---

## `/api/novels/{novelId}/chapters` - 챕터 관리

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **POST** | `/api/novels/{novelId}/chapters` | 새 챕터 생성 | ✅ |

### POST `/api/novels/{novelId}/chapters`
**요청**:
```json
{
  "title": "마법의 시작",
  "content": "학교 문을 통과하는 순간, 나는 완전히 다른 세계에 발을 들여놓았다는 것을 깨달았다."
}
```

**응답 201**:
```json
{
  "success": true,
  "message": "새로운 회차가 등록되었습니다.",
  "data": {
    "chapterId": 2,
    "novelId": 1,
    "chapterNumber": 2,
    "title": "마법의 시작",
    "content": "학교 문을 통과하는 순간, 나는 완전히 다른 세계에 발을 들여놓았다는 것을 깨달았다.",
    "authorId": 2,
    "fromProposalId": 1,
    "createdAt": "2025-08-20T11:00:00"
  }
}
```

---

## `/api/chapters` - 챕터 조회

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **GET** | `/api/chapters/{novelId}` | 소설별 챕터 목록 조회 | ❌ |

### GET `/api/chapters/{novelId}`
**응답 200**:
```json
[
  {
    "chapterId": 1,
    "novelId": 1,
    "chapterNumber": 1,
    "title": "입학 통지서",
    "content": "내 이름이 적힌 붉은 봉랍의 편지. 그것은 마법학교 에테르의 입학 통지서였다.",
    "author": "이야기꾼조씨",
    "createdAt": "2025-08-20T10:00:00"
  },
  {
    "chapterId": 2,
    "novelId": 1,
    "chapterNumber": 2,
    "title": "마법의 시작",
    "content": "학교 문을 통과하는 순간, 나는 완전히 다른 세계에 발을 들여놓았다는 것을 깨달았다.",
    "author": "판타지러버",
    "createdAt": "2025-08-20T11:00:00"
  }
]
```

---

## `/api/chapters/{chapterId}/proposals` - 제안 관리

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **POST** | `/api/chapters/{chapterId}/proposals` | 이어쓰기 제안 생성 | ✅ |
| **GET** | `/api/chapters/{chapterId}/proposals` | 제안 목록 조회 | ❌ |

### POST `/api/chapters/{chapterId}/proposals`
**요청**:
```json
{
  "title": "신비한 도서관 발견",
  "content": "복도를 걸어가던 주인공은 지도에 없는 비밀 도서관을 발견하게 된다. 그곳에는 금지된 마법서들이 숨겨져 있었는데...",
  "aiLogId": 123  // AI 생성 시에만 포함
}
```

**응답 201**:
```json
{
  "success": true,
  "message": "새로운 제안이 등록되었습니다.",
  "data": {
    "proposalId": 1,
    "chapterId": 1,
    "proposerId": 2,
    "title": "신비한 도서관 발견",
    "content": "복도를 걸어가던 주인공은 지도에 없는 비밀 도서관을 발견하게 된다...",
    "voteDeadline": "2025-08-23T10:00:00",
    "aiGenerated": true,
    "createdAt": "2025-08-20T12:00:00"
  }
}
```

### GET `/api/chapters/{chapterId}/proposals`
**응답 200**:
```json
{
  "success": true,
  "message": "제안 목록 조회 성공",
  "data": [
    {
      "proposalId": 1,
      "title": "신비한 도서관 발견",
      "proposerNickname": "판타지러버",
      "voteCount": 15,
      "aiGenerated": true,
      "status": "VOTING",
      "createdAt": "2025-08-20T12:00:00"
    },
    {
      "proposalId": 2,
      "title": "마법 수업 첫날",
      "proposerNickname": "스토리텔러",
      "voteCount": 8,
      "aiGenerated": false,
      "status": "VOTING",
      "createdAt": "2025-08-20T13:00:00"
    }
  ]
}
```

---

## `/api/chapters/{chapterId}/select-winner` - 작가 선택

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **POST** | `/api/chapters/{chapterId}/select-winner` | 동점 시 작가가 최종 선택 | ✅ |

### POST `/api/chapters/{chapterId}/select-winner`
**요청**:
```json
{
  "selectedProposalId": 2
}
```

**응답 200**:
```json
{
  "success": true,
  "message": "다음 회차가 성공적으로 결정되었습니다.",
  "data": {
    "selectedProposalId": 2,
    "nextChapterId": 3
  }
}
```

---

## `/api/vote` - 투표 관리

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **GET** | `/api/vote/novels/{novelId}/proposals` | 소설별 투표 제안 목록 | ❌ |
| **POST** | `/api/vote/do` | 투표하기 | ✅ |

### GET `/api/vote/novels/{novelId}/proposals?page=0&size=6`
**응답 200**:
```json
{
  "success": true,
  "message": "투표 제안 목록을 성공적으로 조회했습니다.",
  "data": {
    "proposals": [
      {
        "proposalId": 1,
        "title": "신비한 도서관 발견",
        "content": "복도를 걸어가던 주인공은 지도에 없는 비밀 도서관을 발견하게 된다...",
        "proposerNickname": "판타지러버",
        "voteCount": 15,
        "aiGenerated": true,
        "voteDeadline": "2025-08-23T10:00:00",
        "createdAt": "2025-08-20T12:00:00"
      }
    ],
    "votingClosed": false,
    "currentChapterAuthor": "이야기꾼조씨",
    "hasNextPage": true
  }
}
```

### POST `/api/vote/do`
**요청**:
```json
{
  "proposalId": 1
}
```

**응답 200**:
```json
"투표가 성공적으로 완료되었습니다."
```

**오류 응답**:
- `409 Conflict`: "이미 투표에 참여했습니다."
- `403 Forbidden`: "투표 기간이 마감되었습니다."
- `400 Bad Request`: "유효하지 않은 제안 ID입니다."

---

## `/api/ai` - AI 기능

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **POST** | `/api/ai/novels/recommend` | AI 소설 추천 생성 | ✅ |
| **POST** | `/api/ai/chapters/{chapterId}/continue` | AI 이어쓰기 추천 | ✅ |

### POST `/api/ai/novels/recommend`
**요청**:
```json
{
  "genre": "FANTASY",
  "synopsis": "마법학교를 배경으로 한 성장 이야기"
}
```

**응답 200**:
```json
{
  "success": true,
  "message": "AI 소설 추천 성공",
  "data": {
    "recommendedTitle": "마법학교 에테르의 신입생",
    "recommendedContent": "붉은 봉랍으로 봉인된 편지가 도착했을 때, 나는 이것이 내 인생을 완전히 바꿀 순간이라는 것을 알지 못했다. 마법학교 에테르의 입학 통지서. 평범한 소년이었던 나에게 온 이 편지는...",
    "aiLogId": 456
  }
}
```

### POST `/api/ai/chapters/{chapterId}/continue`
**요청**:
```json
{
  "guideline": "주인공이 새로운 친구를 만나는 장면을 포함해주세요"
}
```

**응답 200**:
```json
{
  "success": true,
  "message": "AI 이어쓰기 추천 성공",
  "data": {
    "recommendedTitle": "운명적인 만남",
    "recommendedContent": "식당에서 혼자 앉아있던 나에게 한 소녀가 다가왔다. '혹시 새로 온 신입생이야?' 그녀의 미소는 마법처럼 따뜻했다...",
    "aiLogId": 789
  }
}
```

---

## `/api/genres` - 장르 관리

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **GET** | `/api/genres` | 전체 장르 목록 조회 | ❌ |

### GET `/api/genres`
**응답 200**:
```json
[
  {
    "code": "FANTASY",
    "description": "판타지"
  },
  {
    "code": "ROMANCE",
    "description": "로맨스"
  },
  {
    "code": "MYSTERY",
    "description": "미스터리"
  },
  {
    "code": "SCIENCE_FICTION",
    "description": "SF"
  },
  {
    "code": "HORROR",
    "description": "공포"
  },
  {
    "code": "DRAMA",
    "description": "드라마"
  }
]
```

---

## `/api/my-page` - 사용자 프로필

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| **GET** | `/api/my-page` | 마이페이지 정보 조회 | ✅ |
| **POST** | `/api/my-page` | 프로필 정보 수정 | ✅ |

### GET `/api/my-page`
**응답 200**:
```json
{
  "user": {
    "userId": 1,
    "loginId": "testuser",
    "nickname": "테스트유저",
    "email": "test@example.com",
    "profileImageUrl": "https://example.com/profile/testuser.jpg",
    "createdAt": "2025-08-20T10:00:00"
  },
  "novels": [
    {
      "novelId": 1,
      "title": "마법학교 에테르",
      "genre": "FANTASY",
      "chapterCount": 5,
      "viewCount": 1524,
      "status": "ONGOING",
      "createdAt": "2025-08-20T10:00:00"
    }
  ],
  "statistics": {
    "totalNovels": 1,
    "totalChapters": 3,
    "totalProposals": 7,
    "totalVotes": 25
  }
}
```

### POST `/api/my-page`
**Content-Type**: `multipart/form-data`

**요청 파라미터**:
- `nickname` (선택): 새로운 닉네임
- `email` (선택): 새로운 이메일
- `password` (선택): 새로운 비밀번호
- `currentPassword` (비밀번호 변경 시 필수): 현재 비밀번호
- `profileImage` (선택): 프로필 이미지 파일

**응답**: `200 OK` (응답 본문 없음)

---

## HTTP 상태 코드 및 오류 처리

### 성공 응답
- `200 OK`: 성공적인 조회/수정
- `201 Created`: 리소스 생성 성공

### 클라이언트 오류 (4xx)

#### 400 Bad Request
- `INVALID_INPUT`: 입력값이 올바르지 않습니다
- `BUSINESS_ERROR`: 비즈니스 로직 오류가 발생했습니다
- `VALIDATION_ERROR`: 유효성 검사에 실패했습니다
- `INVALID_DATE_FORMAT`: 날짜 형식이 올바르지 않습니다 (yyyy-MM-dd 형식 사용)
- `FILE_SIZE_EXCEEDED`: 파일 크기가 제한을 초과했습니다
- `DATA_INTEGRITY_VIOLATION`: 데이터 무결성 제약 조건을 위반했습니다

#### 401 Unauthorized
- `UNAUTHORIZED`: 인증이 필요합니다
- `INVALID_PASSWORD`: 비밀번호가 올바르지 않습니다

#### 403 Forbidden
- `FORBIDDEN`: 접근 권한이 없습니다

#### 404 Not Found
- `RESOURCE_NOT_FOUND`: 요청한 리소스를 찾을 수 없습니다
- `USER_NOT_FOUND`: 사용자를 찾을 수 없습니다
- `NOVEL_NOT_FOUND`: 소설을 찾을 수 없습니다
- `CHAPTER_NOT_FOUND`: 챕터리스트 조회에 실패하였습니다

#### 409 Conflict
- `DUPLICATE_RESOURCE`: 이미 존재하는 리소스입니다
- `DUPLICATE_USERNAME`: 이미 사용 중인 사용자명입니다
- `DUPLICATE_EMAIL`: 이미 사용 중인 이메일입니다

### 서버 오류 (5xx)
- `500 Internal Server Error`
    - `INTERNAL_SERVER_ERROR`: 서버 내부 오류가 발생했습니다

### 오류 응답 형식

#### 일반 오류 응답
```json
{
  "timestamp": "2025-08-20T10:30:00",
  "status": 404,
  "error": "USER_NOT_FOUND",
  "detail": "사용자를 찾을 수 없습니다",
  "path": "/api/my-page"
}
```

#### 유효성 검증 오류 응답
```json
{
  "timestamp": "2025-08-20T10:30:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "validationErrors": [
    {
      "field": "email",
      "message": "올바른 이메일 형식이 아닙니다",
      "rejectedValue": "invalid-email"
    },
    {
      "field": "birthDate",
      "message": "날짜 형식이 올바르지 않습니다. yyyy-MM-dd 형식으로 입력해주세요.",
      "rejectedValue": "2000-1-1"
    }
  ]
}
```

#### 날짜 형식 오류 예시
```json
{
  "timestamp": "2025-08-20T10:30:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "validationErrors": [
    {
      "field": "birthDate",
      "message": "날짜 형식이 올바르지 않습니다. yyyy-MM-dd 형식으로 입력해주세요.",
      "rejectedValue": "2000-1-1"
    }
  ]
}
```

### 주요 비즈니스 예외 시나리오

#### 투표 관련 오류
- **중복 투표**: `409 Conflict` - "이미 투표에 참여했습니다."
- **투표 마감**: `403 Forbidden` - "투표 기간이 마감되었습니다."
- **잘못된 제안**: `400 Bad Request` - "유효하지 않은 제안 ID입니다."

#### 인증 관련 오류
- **로그인 실패**: `401 Unauthorized` - "비밀번호가 올바르지 않습니다."
- **사용자 없음**: `404 Not Found` - "사용자를 찾을 수 없습니다."

#### 중복 데이터 오류
- **사용자명 중복**: `409 Conflict` - "이미 사용 중인 사용자명입니다."
- **이메일 중복**: `409 Conflict` - "이미 사용 중인 이메일입니다."

#### 파일 업로드 오류
- **파일 크기 초과**: `400 Bad Request` - "파일 크기가 제한을 초과했습니다."
- **지원하지 않는 형식**: `400 Bad Request` - "지원하지 않는 파일 형식입니다."

---


## 인증 및 보안

### JWT 토큰 사용
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 보안 고려사항
- 모든 비밀번호는 bcrypt로 해싱
- 파일 업로드 시 확장자 및 크기 제한
- SQL Injection 방지를 위한 PreparedStatement 사용
- XSS 방지를 위한 입력값 검증

---

## RESTful 설계 원칙

### 리소스 중심 URL 설계
- `/api/novels` - 소설 컬렉션
- `/api/novels/{id}` - 특정 소설
- `/api/chapters/{id}/proposals` - 특정 챕터의 제안들

### HTTP 메서드 활용
- `GET`: 조회
- `POST`: 생성
- `PUT/PATCH`: 수정 (현재 미구현)
- `DELETE`: 삭제 (현재 미구현)

### 적절한 HTTP 상태 코드 사용
- 201 Created: 리소스 생성
- 200 OK: 조회/수정 성공
- 404 Not Found: 리소스 없음

---

## 페이지네이션

### 쿼리 파라미터
- `page`: 페이지 번호 (0부터 시작)
- `size`: 페이지 크기 (기본값 설정됨)

### 예시
```
GET /api/novels?page=0&size=8
GET /api/vote/novels/1/proposals?page=0&size=6
```

---
