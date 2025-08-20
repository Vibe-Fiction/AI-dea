# **Vibe-Fiction 프로젝트 클론 및 환경설정 가이드**

**작성자:** [왕택준](https://github.com/TJK98)

**문서 버전:** 1.0

본 문서는 Vibe-Fiction 프로젝트를 로컬 환경에 클론하여 정상적으로 개발 및 실행하기 위한 환경 설정 방법과 필수 유의사항을 안내합니다.

---

## **1. 사전 준비 사항**

프로젝트 설정을 시작하기 전에, 로컬 개발 환경에 아래 사항들이 먼저 준비되어야 합니다.

### **1-1. MariaDB 설치**

로컬 환경에 MariaDB가 설치되어 있어야 합니다. 설치되어 있지 않다면 아래 공식 사이트에서 다운로드하여 설치를 진행해 주세요.
*   **다운로드**: [MariaDB Server 다운로드](https://mariadb.org/download/)

### **1-2. 데이터베이스 생성**

MariaDB 설치 후, 프로젝트에서 사용할 데이터베이스를 생성해야 합니다.
GUI 툴(DBeaver, HeidiSQL 등)이나 터미널을 통해 MariaDB에 접속하여 아래 SQL 쿼리를 실행해 주세요.

```sql
CREATE DATABASE ai_dea
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
```
> **💡 중요:** 데이터베이스 이름은 반드시 **`ai_dea`** 로 생성해야 합니다. (UTF-8 문자셋 설정 권장)

---

## **2. 프로젝트 환경설정**

사전 준비가 완료되었다면, 이제 프로젝트 설정을 진행합니다.

### **2-1. 필수 설정 파일(`application-template.yml`) 생성**

프로젝트를 클론한 후, `src/main/resources/` 경로에 **`application-template.yml`** 파일을 **직접 생성**해야 합니다.

### **2-2. `.gitignore` 보안 설정**

생성한 `application-template.yml` 파일이 민감 정보를 포함하므로, GitHub에 유출되지 않도록 프로젝트 최상위 경로의 `.gitignore` 파일에 아래 내용을 **반드시 추가**해야 합니다.

```gitignore
# Application Secret Keys
/src/main/resources/application-template.yml
```

---

## **3. `application-template.yml` 예시 코드**

아래 내용을 위에서 생성한 `src/main/resources/application-template.yml` 파일에 붙여넣고, **`""`** 또는 **`"설명"`** 부분에 본인의 로컬 DB 정보와 발급받은 API 키를 정확하게 입력하세요.

```yaml
# =================================================================
# 이 파일에는 민감한 정보가 포함되므로 Git에 절대 올리지 마세요.
# =================================================================
# --- 민감 정보 ---
# Gemini API 설정 (절대 유출 금지)
google:
  gemini:
    api-key: "본인이 발급 받은 Gemini API KEY"
```

> ⚠️ **주의:**
> `application-template.yml` 파일에 입력된 API 키 등 민감 정보는 **절대 커밋하거나 외부에 공유해서는 안 됩니다.**

---

## **4. API 키 발급 안내**

프로젝트 실행에 필요한 API 키는 아래 공식 페이지에서 직접 발급받으실 수 있습니다.

*   **Google Gemini API**
    *   **링크**: [https://ai.google.dev/](https://ai.google.dev/)
    *   **설명**: Google AI Studio 또는 Google Cloud Platform에 가입하고 프로젝트를 생성한 후 API 키를 발급받을 수 있습니다.

---

## **5. 문의 및 지원**

> 궁금한 점은 언제든 GitHub Issue, 댓글, 혹은 메일로 문의 바랍니다.

*   **GitHub Repository**: [AI-dea](https://github.com/Vibe-Fiction/AI-dea)

*   **이메일**: [wtj1998@naver.com](mailto:wtj1998@naver.com)

---

> 본 문서를 반드시 참고하여 안전하게 프로젝트를 실행해 주세요.
