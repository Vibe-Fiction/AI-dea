# 코드 스타일 가이드

> [↩ 규칙 허브로 돌아가기](../../../CONTRIBUTING.md)

본 문서는 **Team Ai-dea**의 Vibe Fiction 프로젝트에서 코드의 가독성, 일관성, 유지보수성을 높이기 위해 적용하는 스타일 규칙을 정의합니다.

**작성자**: [왕택준](https://github.com/TJK98)

---

## 1. 네이밍 규칙

### 1.1 개인 작업 식별용 네이밍

Java 및 Spring 기반 개발에서는 동일한 서비스/DTO/예외명 등이 충돌할 수 있으므로, 프로젝트 초기 단계에서는 **클래스/메서드/테스트 이름 뒤에 이니셜을 붙여 개인 작업을 구분합니다.**

| 항목             | 예시 |
|------------------|------|
| 컨트롤러 메서드  | `createUserByTj()` |
| DTO 클래스명     | `NovelCreateRequestDtoMj` |
| 테스트 클래스명  | `UserServiceTestSh` |
| 커스텀 예외      | `DuplicateEmailExceptionKo` |
| 유틸 클래스      | `FileUtilDh` |

- 공통 컴포넌트는 이니셜을 생략할 수 있습니다.
- **실제 배포 전에는 네이밍을 통합하거나 모듈 단위로 리팩토링할 예정입니다.**

#### 팀원 이니셜 규칙

| 이름       | 이니셜 |
|------------|--------|
| 왕택준     | `tj`   |
| 백승현     | `sh`   |
| 고동현     | `ko`   |
| 김대현     | `dh`   |
| 송민재     | `mj`   |

### 1.2 이니셜 네이밍 규칙의 장기적 관점과 대안

'이니셜 네이밍' 규칙은 프로젝트 초기 단계에서 발생하는 **Git Merge Conflict(병합 충돌)을 최소화**하고, 각자의 작업 범위를 명확히 구분하기 위한 **전략적인 선택**입니다.

하지만 프로젝트가 성숙해짐에 따라 다음과 같은 유지보수 문제를 야기할 수 있습니다.

- **가독성 저하**: `NovelService` 안에 `createNovelByMj()`, `createNovelByTj()` 등 유사한 목적의 메서드가 여러 개 존재하여 어떤 것이 최종본인지 파악하기
  어렵습니다.
- **기술 부채**: "배포 전 리팩토링" 약속이 지켜지지 않을 경우, 불필요한 코드가 그대로 남아 유지보수 비용을 증가시킬 수 있습니다.

#### 장기적인 목표: 표준 네이밍과 충돌 해결 문화

우리 팀의 장기적인 목표는 **'이니셜 네이밍'을 점진적으로 줄여나가고, 표준적인 네이밍 컨벤션을 사용하는 것**입니다. 이를 위해 다음과 같은 문화를 지향합니다.

1. **사전 논의**: 같은 파일을 수정해야 할 경우, 작업을 시작하기 전에 관련 팀원과 **어떤 부분을 어떻게 수정할지 미리 논의**합니다.
2. **적극적인 브랜치 관리**: 각자 자신의 `feature` 브랜치에서 작업하고, `dev` 브랜치의 최신 변경사항을 자주 `pull` 또는 `rebase` 하여 병합 충돌을 조기에 발견하고 해결합니다.
3. **충돌 해결은 필수 역량**: Git 병합 충돌은 협업의 자연스러운 과정입니다. 충돌 해결을 두려워하지 않고, 팀원과 함께 소통하며 해결하는 것을 원칙으로 합니다.

> ❗ **핵심 원칙**: 이니셜 네이밍은 **충돌 회피**를 위한 수단일 뿐, **충돌 해결 능력**을 기르는 것이 더 중요합니다. 기능이 안정화되면 과감히 이니셜을 제거하고 코드를 통합하는 리팩토링을 진행합니다.

---

## 2. 자동화된 코드 스타일 관리 (`.editorconfig`)

우리 프로젝트는 모든 팀원이 동일한 코드 스타일을 유지하도록 강제하기 위해 `.editorconfig` 파일을 사용합니다. 이 파일은 IntelliJ, VSCode 등 대부분의 에디터에서 자동으로 인식되며, 파일을
저장할 때마다 설정된 규칙에 맞게 코드를 포맷팅해줍니다.

이를 통해 들여쓰기, 줄바꿈, 공백 등 사소한 스타일 차이로 인한 불필요한 Git 변경(diff)을 방지하고 코드 리뷰 시 로직에만 집중할 수 있습니다.

> 🔗 우리 프로젝트의 구체적인 포맷팅 규칙은 프로젝트 최상위 디렉토리(root)에 위치한 **`.editorconfig`** 파일에 모두 정의되어 있습니다. 각 설정값에 대한 자세한 설명은 해당 파일 내의 주석을
통해 직접 확인해주시기 바랍니다.

---

## 3. 코드 주석 규칙

### 3.1 주석의 목적 및 작성 원칙

- 협업자, 리뷰어, 미래의 자신이 코드를 빠르게 이해할 수 있도록 돕습니다.
- 단순히 “무엇을 한다”가 아니라, **“왜 그렇게 했는가 (비즈니스적 의도)”** 를 설명하는 데 중점을 둡니다.
- `i++ // i를 1 증가`와 같이 **당연한 코드에 불필요한 주석은 작성하지 않습니다.**
- **전원 한국어 주석** 사용을 원칙으로 합니다.

### 3.2 주석 작성 위치

| 위치       | 설명                                                      |
|------------|-----------------------------------------------------------|
| 메서드 상단 | 기능 개요, 주요 로직 흐름, 입력/출력, 예외 처리 등        |
| 코드 라인 옆 | 비즈니스 목적, 복잡한 조건 분기, 코드의 의도 설명         |
| `TODO` / `FIXME` | 향후 개선이 필요한 작업, 현재 문제가 있는 코드 등 |

### 3.3 주석 형식 및 작성 방식

#### 작성자 이니셜 표기

- 각자 작성한 코드나 주요 변경점에는 이니셜 표기를 권장합니다.
- **예시**: `// [TJ] 사용자 로그인 처리 (JWT 발급 포함)`

#### 한 줄 설명 + 비즈니스 의미

- 코드의 기술적인 동작뿐만 아니라, 비즈니스 관점에서 왜 필요한지를 설명합니다.
- **예시**:
  ```java
  // 댓글 채택 시, 더 이상 투표가 진행되지 않도록 스케줄링된 투표를 마감 처리
  proposal.setStatus(Status.ADOPTED);
  voteScheduler.cancelVoting(proposal.getId());
  ```

#### 조건문, 분기 설명

- 복잡한 조건 분기의 이유나 비즈니스 규칙을 설명합니다.
- **예시**:
  ```java
  // 친구 공개 설정인 경우, 게시글 열람 전 사용자 간 친구 관계 확인
  if (novel.getVisibility() == Visibility.FRIENDS) {
      ...
  }
  ```

#### TODO / FIXME 주석

- **`TODO`**: 당장 처리하지는 않지만 향후 개선이 필요한 작업에 사용합니다. **관련 GitHub 이슈 번호를 `(#이슈번호)` 형식으로 반드시 함께 기재**하여 추적을 용이하게 합니다.
- **`FIXME`**: 코드에 문제가 있어 반드시 수정해야 하지만, 일단 커밋해야 할 때 사용합니다.
- **예시**:
  ```java
  // TODO(#15): 장르 필터 기능 추가 예정 (프론트와 협의 필요)
  // FIXME: 비로그인 사용자의 접근 제한 로직이 누락되어 있음 (긴급 수정 필요)
  ```
  > 💡 **Tip**: IntelliJ, VSCode 등 대부분의 IDE는 `TODO(#이슈번호)` 형식의 주석에서 이슈 번호를 클릭하면 해당 GitHub 이슈 페이지로 바로 이동하는 기능을 지원합니다.

### 3.4 Javadoc 스타일 주석 (선택)

- Javadoc은 필수 사항은 아니지만, API 문서 자동화가 필요하거나 복잡한 공개 메서드에 대해서는 아래 예시와 같이 사용을 고려할 수 있습니다.

```java
/**
 * 사용자의 소설을 생성하는 메서드입니다.
 *
 * 이 메서드는 사용자의 ID와 소설 제목을 받아 새로운 소설 엔티티를 생성하고
 * 데이터베이스에 저장한 후, 생성된 소설의 고유 ID를 반환합니다.
 *
 * @param userId 생성할 사용자의 ID
 * @param novelTitle 생성할 소설의 제목
 * @return 생성된 소설의 고유 ID (Long)
 * @throws IllegalArgumentException userId나 novelTitle이 유효하지 않을 경우
 *
 * @author 왕택준
 * @since 2025.08
 */
public Long createNovel(Long userId, String novelTitle) {
    // ...
}
```

---

## 4. 테스트 코드 스타일

### 4.1 테스트 파일 및 클래스 규칙

| 항목         | 규칙                                                  |
|--------------|-------------------------------------------------------|
| 파일명       | `UserServiceTestTj.java` 형식 (이니셜 포함)           |
| 클래스 명    | `@SpringBootTest` 또는 슬라이스 테스트(`@WebMvcTest`, `@DataJpaTest`) 어노테이션 명확히 명시 |
| 클래스 책임  | 한 클래스는 하나의 서비스/리포지토리 테스트 책임만 가집니다. |

### 4.2 메서드 작성 규칙

- **네이밍 및 어노테이션**:
    - `@Test` 어노테이션은 필수입니다.
    - `@DisplayName("테스트의 목적")` 어노테이션을 사용하여 테스트 목적을 명확히 한글로 기술합니다.
- **구조는 `given / when / then` 고정**:
    - 주석 또는 공백으로 각 구간을 명확히 구분합니다.
    - 코드와 주석으로 기대 동작을 함께 명시합니다.

### 4.3 테스트 유형별 스타일

| 테스트 종류           | 설명                                                                 |
|-----------------------|----------------------------------------------------------------------|
| **단위 테스트**       | 외부 의존성(DB, 외부 API 등) 없는 메서드 단독 테스트                   |
| **통합 테스트**       | DB, 네트워크, 외부 API 등 실제 자원 연동 테스트                      |
| **슬라이스 테스트**   | `@DataJpaTest`, `@WebMvcTest` 등 특정 레이어 단위만 테스트           |

#### 공통 원칙

- 불필요한 `@Autowired` 지양 → `@MockBean` 또는 생성자 주입을 우선적으로 고려합니다.
- DB를 사용하는 테스트는 `@Transactional` 또는 `@Rollback` 사용을 고려하여 테스트 간 독립성을 보장합니다.
- Mock 객체 사용 시 `given → willReturn`을 통한 흐름을 명확하게 설정합니다.

### 4.4 기타 권장 사항

- 테스트 커버리지를 높이되, **핵심 비즈니스 로직 중심 테스트를 우선**합니다.
- 단순 getter/setter, DTO 생성 등은 테스트를 생략할 수 있습니다.
- 테스트 실패 시 에러 메시지가 명확히 나오도록 `assertThat(...).as("실패 시 출력할 설명")` 사용을 권장합니다.

### 4.5 예시 코드

#### 성공 케이스

```java

@SpringBootTest
@Transactional
class UserServiceTestTj {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("[성공] 회원가입 - 정상적인 정보로 회원가입 시 DB에 저장된다")
    void registerUser_Success() {
        // given - 어떤 데이터가 주어졌을 때
        UserRegistrationRequest request = new UserRegistrationRequest("test@example.com", "password123", "테스트유저");

        // when - 어떤 행위를 하면
        Long userId = userService.registerUserByTj(request);

        // then - 어떤 결과가 나와야 한다
        User foundUser = userRepository.findById(userId)
            .orElseThrow(() -> new AssertionError("유저를 찾을 수 없습니다."));

        assertThat(foundUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(foundUser.getNickname()).isEqualTo(request.getNickname());
    }
}
```

#### 실패 케이스

```java

@Test
@DisplayName("[실패] 회원가입 - 중복된 이메일로 가입 시 예외가 발생한다")
void registerUser_Fail_WithDuplicateEmail() {
    // given
    userRepository.save(new User("test@example.com", "password123", "기존유저"));
    UserRegistrationRequest request = new UserRegistrationRequest("test@example.com", "new_password", "신규유저");

    // when & then
    assertThrows(DuplicateEmailException.class, () -> {
        userService.registerUserByTj(request);
    }, "중복된 이메일이므로 DuplicateEmailException이 발생해야 합니다.");
}
}
```

> 🔗 테스트 폴더 구조(초성/이니셜 규칙)는 **[프로젝트 구조 규칙](project-structure.md)** 을 참고하세요.
