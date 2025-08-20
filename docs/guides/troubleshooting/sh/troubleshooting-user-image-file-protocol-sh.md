# 트러블슈팅: 유저 이미지 수정 (`file://` 프로토콜 문제)

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼)에서 유저 프로필 이미지 업로드 후 화면에 이미지가 표시되지 않는 문제를 분석하고 해결한 사례를 기록한 기술 문서입니다.

**작성자:** 백승현

**작성일:** 2024년 8월 16일

---

## 1. 문제 현상

* 업로드 후 서버에 파일은 정상 저장됨.
* 그러나 클라이언트에서 이미지를 렌더링하지 못함.
* 브라우저 콘솔 에러:

  ```
  Not allowed to load local resource: file://C:/Users/user/aidea/uploads/profile_1_xxx.JPEG
  ```

---

## 2. 원인 분석

1. **브라우저 보안 정책**

   * 브라우저는 보안상 `file://` 프로토콜 접근을 차단.
   * 로컬 파일 직접 접근은 개인정보 유출 위험.

2. **서버 절대 경로 반환 오류**

   ```java
   return "C:/Users/user/aidea/uploads/" + filename; // ❌ file:// 경로 반환
   ```

3. **정적 리소스 매핑 미설정**

   * Spring Boot에서 `/uploads/**` 요청을 실제 파일 경로와 연결하지 않음.

---

## 3. 해결 과정

1. **application.yml 설정 추가**

   ```yaml
   file:
     upload:
       location: C:/Users/user/aidea/uploads
       web-path: /uploads
   ```

2. **WebMvcConfigurer로 매핑**

   ```java
   registry.addResourceHandler("/uploads/**")
           .addResourceLocations("file:C:/Users/user/aidea/uploads/");
   ```

3. **서비스 로직에서 웹 URL 반환**

   ```java
   return "/uploads/profile_1/xxx.jpg"; // ✅ file:// 대신 http URL 반환
   ```

4. **프론트엔드에서 file:// 제거**

   ```javascript
   profileImage.src = imageUrl; // 서버 제공 URL 그대로 사용
   ```

---

## 4. 결론 및 교훈

* **문제 원인**: file:// 경로 반환 + 브라우저 보안 정책.
* **해결책**: 서버는 웹 URL 반환, 프론트는 그대로 사용.
* **교훈**:

  1. 모든 리소스는 HTTP/HTTPS로 제공되어야 한다.
  2. 서버 절대경로는 직접 노출하지 않는다.
  3. file:// 프로토콜은 웹앱에서 절대 사용하지 않는다.

---

> 본 문서는 Vibe Fiction 프로젝트(Relai 플랫폼) 개발 과정에서 **유저 프로필 이미지 업로드 시 file:// 프로토콜 문제**를 해결한 사례를 기록한 트러블슈팅 문서입니다.
