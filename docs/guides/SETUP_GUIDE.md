# **Vibe-Fiction 프로젝트 클론 및 환경설정 가이드**

**작성자:** [왕택준](https://github.com/TJK98)

**문서 버전:** 1.0

본 문서는 Vibe-Fiction 프로젝트를 로컬 환경에 클론하여 정상적으로 개발 및 실행하기 위한 환경 설정 방법과 필수 유의사항을 안내합니다.

---

## **1. 사전 준비 사항**

프로젝트 설정을 시작하기 전에, 로컬 개발 환경에 아래 사항들이 먼저 준비되어야 합니다.

### **1-1. MariaDB 설치**

[MariaDB Server 다운로드](https://mariadb.org/download/)

### **1-2. 데이터베이스 생성**

```sql
CREATE DATABASE ai_dea
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```

> 💡 DB 이름은 반드시 `ai_dea` 로 생성해야 합니다.

---

## **2. 프로젝트 클론하기**

레포지토리 클론

[Aidea Repository 바로가기](https://github.com/BeFit-crew/BeFit)

아래 명령어를 통해 프로젝트를 로컬 환경에 클론하세요:

```bash
git clone https://github.com/Vibe-Fiction/AI-dea.git
cd AI-dea
```

Gradle Wrapper가 포함되어 있으므로, 별도의 Gradle 설치 없이 빌드 및 실행이 가능합니다.

---

## **3. 프로젝트 환경설정**

### **3-1. 설정 파일(`application-template.yml`) 생성**

경로: `src/main/resources/`

### **3-2. `.gitignore` 보안 설정**

```gitignore
/src/main/resources/application-template.yml
```

---

## **4. `application-template.yml` 예시**

```yaml
google:
  gemini:
    api-key: "본인이 발급 받은 Gemini API KEY"
```

---

## **5. 실행 방법**

Gradle Wrapper를 통해 서버를 실행할 수 있습니다.

```bash
./gradlew bootRun   # Mac/Linux
gradlew.bat bootRun # Windows
```

기본 실행 포트는 `8080`입니다.

---

## **6. API 키 발급 안내**

[Google Gemini API](https://ai.google.dev/)

---

## **7. 문의 및 지원**

* GitHub: [AI-dea](https://github.com/Vibe-Fiction/AI-dea)
* 이메일: [wtj1998@naver.com](mailto:wtj1998@naver.com)

---

> 본 문서를 반드시 참고하여 안전하게 프로젝트를 실행해 주세요.
