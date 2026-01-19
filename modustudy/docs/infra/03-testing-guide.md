# 테스트 코드 작성 가이드

## 1. 개요

**모든 PR은 테스트를 통과해야 Merge 가능합니다.**

Jenkins CI에서 테스트 실패 시 배포가 중단됩니다.

```
PR 생성 → 테스트 실행 → 실패 시 Merge 불가
```

## 2. 테스트 디렉토리 구조

```
backend/src/test/java/com/ssafy/squiz/
├── unit/                          # 단위 테스트
│   ├── user/
│   │   ├── UserServiceTest.java
│   │   └── UserValidatorTest.java
│   ├── study/
│   │   └── StudyServiceTest.java
│   ├── quiz/
│   │   └── QuizServiceTest.java
│   └── channel/
│       └── ChannelServiceTest.java
│
├── integration/                   # 통합 테스트
│   ├── api/
│   │   ├── UserApiTest.java
│   │   ├── StudyApiTest.java
│   │   └── QuizApiTest.java
│   └── repository/
│       └── UserRepositoryTest.java
│
└── common/                        # 테스트 유틸
    ├── TestFixture.java
    └── IntegrationTestBase.java
```

## 3. 테스트 레벨

| 레벨 | 대상 | 의존성 | 속도 |
|-----|------|-------|------|
| Unit | Service, Util | Mock 사용 | 빠름 (~ms) |
| Integration | Repository, API | 실제 DB | 느림 (~s) |

## 4. 단위 테스트 작성

### 4.1 기본 구조

```java
package com.ssafy.squiz.unit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("정상적인 입력으로 회원가입 성공")
    void createUser_WithValidInput_ShouldReturnUserId() {
        // given
        UserCreateRequest request = new UserCreateRequest("test@test.com", "테스터");
        User savedUser = User.builder()
            .id(1L)
            .email("test@test.com")
            .nickname("테스터")
            .build();

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        Long userId = userService.createUser(request);

        // then
        assertThat(userId).isEqualTo(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시 예외 발생")
    void createUser_WithDuplicateEmail_ShouldThrowException() {
        // given
        UserCreateRequest request = new UserCreateRequest("existing@test.com", "테스터");
        given(userRepository.existsByEmail("existing@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessage("이미 존재하는 이메일입니다.");
    }
}
```

### 4.2 네이밍 컨벤션

```
메서드명_시나리오_예상결과

예시:
- createUser_WithValidInput_ShouldReturnUserId
- createUser_WithDuplicateEmail_ShouldThrowException
- findStudy_WhenNotExists_ShouldReturnEmpty
- updateQuiz_WithInvalidId_ShouldThrowNotFoundException
```

### 4.3 Given-When-Then 패턴

```java
@Test
void 테스트메서드() {
    // given - 테스트 준비 (Mock 설정, 테스트 데이터)
    given(repository.findById(1L)).willReturn(Optional.of(entity));

    // when - 테스트 실행
    Result result = service.doSomething(1L);

    // then - 결과 검증
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("expected");
}
```

## 5. 통합 테스트 작성

### 5.1 API 테스트

```java
package com.ssafy.squiz.integration.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("회원가입 API 성공")
    void createUser_Success() throws Exception {
        // given
        String request = """
            {
                "email": "newuser@test.com",
                "password": "password123",
                "nickname": "새유저"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.email").value("newuser@test.com"));
    }

    @Test
    @DisplayName("잘못된 이메일 형식으로 회원가입 실패")
    void createUser_InvalidEmail_Fail() throws Exception {
        // given
        String request = """
            {
                "email": "invalid-email",
                "password": "password123",
                "nickname": "새유저"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }
}
```

### 5.2 Repository 테스트

```java
package com.ssafy.squiz.integration.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail_Success() {
        // given
        User user = User.builder()
            .email("test@test.com")
            .nickname("테스터")
            .build();
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail("test@test.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("테스터");
    }
}
```

## 6. 도메인별 필수 테스트

### 6.1 최소 테스트 케이스

각 Service 클래스마다 최소한 다음 테스트가 필요합니다:

| 메서드 | 필수 테스트 |
|-------|-----------|
| create | 정상 생성, 유효성 검증 실패 |
| find | 정상 조회, 존재하지 않는 경우 |
| update | 정상 수정, 권한 없음 |
| delete | 정상 삭제, 권한 없음 |

### 6.2 도메인별 담당

| 도메인 | 테스트 패키지 |
|-------|-------------|
| 회원 | `unit/user/`, `integration/api/UserApiTest` |
| 스터디 | `unit/study/`, `integration/api/StudyApiTest` |
| 퀴즈 | `unit/quiz/`, `integration/api/QuizApiTest` |
| 채널 | `unit/channel/`, `integration/api/ChannelApiTest` |

## 7. 로컬에서 테스트 실행

### 전체 테스트

```bash
cd modustudy/backend
./gradlew test
```

### 특정 클래스만 테스트

```bash
./gradlew test --tests "com.ssafy.squiz.unit.user.UserServiceTest"
```

### 특정 패키지만 테스트

```bash
# 단위 테스트만
./gradlew test --tests "com.ssafy.squiz.unit.*"

# 통합 테스트만
./gradlew test --tests "com.ssafy.squiz.integration.*"
```

### 테스트 리포트 확인

테스트 실행 후 리포트 위치:

```
backend/build/reports/tests/test/index.html
```

## 8. 테스트 작성 규칙

### DO (해야 할 것)

- ✅ PR 전에 로컬에서 테스트 통과 확인
- ✅ 새 기능 추가 시 테스트 코드 함께 작성
- ✅ 버그 수정 시 해당 버그를 잡는 테스트 추가
- ✅ Given-When-Then 패턴 사용
- ✅ `@DisplayName`으로 테스트 설명 작성

### DON'T (하지 말 것)

- ❌ 테스트 없이 PR 올리기
- ❌ 다른 테스트에 의존하는 테스트 작성
- ❌ 실제 외부 API 호출하는 테스트
- ❌ 테스트 순서에 의존하는 코드

## 9. CI/CD에서의 테스트

### 파이프라인 흐름

```
Push → Jenkins 빌드 시작 → ./gradlew test → 실패 시 배포 중단
```

### 테스트 실패 시

1. Jenkins 빌드 로그에서 실패한 테스트 확인
2. 로컬에서 해당 테스트 실행
3. 수정 후 다시 Push

## 10. 테스트 의존성 (build.gradle)

```gradle
dependencies {
    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'

    // AssertJ (더 나은 assertion)
    testImplementation 'org.assertj:assertj-core:3.24.2'

    // TestContainers (통합 테스트용 DB)
    testImplementation 'org.testcontainers:junit-jupiter:1.19.0'
    testImplementation 'org.testcontainers:mysql:1.19.0'
}
```

## 11. 테스트 예시 템플릿

### Service 테스트 템플릿

```java
package com.ssafy.squiz.unit.{도메인};

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class {도메인}ServiceTest {

    @Mock
    private {도메인}Repository {도메인}Repository;

    @InjectMocks
    private {도메인}Service {도메인}Service;

    @Test
    @DisplayName("정상 생성")
    void create_Success() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("생성 실패 - 유효성 검증")
    void create_InvalidInput_Fail() {
        // given

        // when & then
    }
}
```

### API 테스트 템플릿

```java
package com.ssafy.squiz.integration.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class {도메인}ApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("생성 API 성공")
    void create_Success() throws Exception {
        // given
        String request = """
            {
                "field": "value"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/{도메인}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }
}
```
