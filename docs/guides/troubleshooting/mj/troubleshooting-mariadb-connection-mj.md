# 트러블슈팅 가이드 – 서버 통신 오류 (MariaDB)

본 문서는 Vibe Fiction 프로젝트 개발 중 발생한 **MariaDB 서버 통신 오류**에 대한 원인과 해결 과정을 정리한 기록입니다.

**작성자:** [송민재](https://github.com/songkey06)

**작성일:** 2025년 8월 20일

---

### 문제점
서버 통신이 연결되지 않고 Tomcat이 시작되지 못했습니다. 로그를 확인해보니 MariaDB에 필요한 데이터베이스가 존재하지 않는 것이 원인이었습니다.

### 원인
`ai-dea` 데이터베이스가 MariaDB에 생성되지 않았습니다.

### 해결 방법
데이터베이스를 수동으로 생성하고 사용하도록 설정합니다.
```sql
CREATE DATABASE `ai-dea`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;
USE `ai-dea`;
```

### 결론 및 교훈

* 서버가 정상적으로 실행되지 않을 때는 **DB 존재 여부**부터 확인해야 함.
* 초기 세팅 시 `schema.sql` 또는 `init.sql`을 활용하면 재현 가능한 환경 구성이 가능함.

> 본 문서는 Vibe Fiction 프로젝트 개발 중 발생한 \*\*서버 통신 오류 (MariaDB)\*\*에 대한 트러블슈팅 기록입니다.
