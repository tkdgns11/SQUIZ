최초 작성자 : 윤상훈 (0108)
최종 수정자 : 윤상훈 (0108) 변경사유 : 

# WebMobile1 Skeleton - Spring Boot 개념 정리 및 흐름

> SSAFY 2학기 프로젝트 프로젝트 코드 이해를 위한 Spring Boot 가이드

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [프로젝트 구조](#3-프로젝트-구조)
4. [핵심 Spring Boot 개념](#4-핵심-spring-boot-개념)
5. [계층형 아키텍처](#5-계층형-아키텍처)
6. [Spring Security & JWT](#6-spring-security--jwt)
7. [JPA & 데이터베이스](#7-jpa--데이터베이스)
8. [설정 파일 분석](#8-설정-파일-분석)
9. [전체 요청 흐름도](#9-전체-요청-흐름도)
10. [주요 파일별 역할](#10-주요-파일별-역할)

---

## 1. 프로젝트 개요

이 프로젝트는 **화상 회의 플랫폼**의 백엔드 기존으로, JWT 기반 인증 시스템이 구현되어 있습니다.

### 주요 특징
- **프레임워크**: Spring Boot 3.2.2 + Java 21
- **인증**: JWT (JSON Web Token) + Spring Security
- **ORM**: Spring Data JPA + QueryDSL
- **데이터베이스**: MySQL 8.0
- **API 문서**: Swagger/OpenAPI 3.0

---

## 2. 기술 스택

### build.gradle 의존성

```groovy
dependencies {
    // Spring Boot 핵심
    implementation 'org.springframework.boot:spring-boot-starter-web'      // REST API
    implementation 'org.springframework.boot:spring-boot-starter-security' // 보안
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa' // ORM
    implementation 'org.springframework.boot:spring-boot-starter-websocket' // 실시간 통신
    implementation 'org.springframework.boot:spring-boot-starter-validation' // 검증

    // 데이터베이스
    runtimeOnly 'com.mysql:mysql-connector-j'  // MySQL 드라이버

    // QueryDSL (타입 안전 쿼리)
    implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
    annotationProcessor 'com.querydsl:querydsl-apt:5.0.0:jakarta'

    // JWT 토큰
    implementation 'com.auth0:java-jwt:4.4.0'

    // API 문서화
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'

    // 개발 편의
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
}
```

| 라이브러리 | 용도 |
|-----------|------|
| `spring-boot-starter-web` | REST API, 내장 Tomcat 서버 |
| `spring-boot-starter-security` | 인증/인가 프레임워크 |
| `spring-boot-starter-data-jpa` | ORM (Hibernate) |
| `java-jwt` | JWT 생성/검증 |
| `querydsl-jpa` | 타입 안전 쿼리 빌더 |
| `springdoc-openapi` | Swagger UI 자동 생성 |
| `lombok` | 보일러플레이트 코드 제거 |

---

## 3. 프로젝트 구조

```
backend/src/main/java/com/ssafy/
├── GroupCallApplication.java          # 메인 클래스 (진입점)
│
├── api/                               # API 계층
│   ├── controller/                    # 컨트롤러 (요청 처리)
│   │   ├── AuthController.java        #   └── 로그인 API
│   │   └── UserController.java        #   └── 회원가입, 내정보 API
│   ├── service/                       # 서비스 (비즈니스 로직)
│   │   ├── UserService.java           #   └── 인터페이스
│   │   └── UserServiceImpl.java       #   └── 구현체
│   ├── request/                       # 요청 DTO
│   │   ├── UserLoginPostReq.java
│   │   └── UserRegisterPostReq.java
│   └── response/                      # 응답 DTO
│       ├── UserLoginPostRes.java
│       └── UserRes.java
│
├── db/                                # 데이터 계층
│   ├── entity/                        # JPA 엔티티
│   │   ├── BaseEntity.java            #   └── 공통 필드 (id, 생성/수정일)
│   │   └── User.java                  #   └── 사용자 엔티티
│   └── repository/                    # 저장소
│       ├── UserRepository.java        #   └── Spring Data JPA
│       └── UserRepositorySupport.java #   └── QueryDSL
│
├── config/                            # 설정 클래스
│   ├── SecurityConfig.java            # Spring Security 설정
│   ├── JpaConfig.java                 # JPA + JSON 설정
│   ├── PasswordEncoderConfig.java     # 비밀번호 암호화
│   ├── SwaggerConfig.java             # API 문서화
│   ├── WebMvcConfig.java              # CORS 설정
│   └── IndexController.java           # SPA 라우팅
│
└── common/                            # 공통 모듈
    ├── auth/                          # 인증 관련
    │   ├── JwtAuthenticationFilter.java  # JWT 필터
    │   ├── SsafyUserDetails.java         # UserDetails 구현
    │   └── SsafyUserDetailService.java   # UserDetailsService 구현
    ├── util/
    │   ├── JwtTokenUtil.java          # JWT 유틸리티
    │   └── ResponseBodyWriteUtil.java # 응답 유틸리티
    ├── model/response/
    │   └── BaseResponseBody.java      # 공통 응답 형식
    └── exception/handler/
        └── NotFoundHandler.java       # 404 예외 처리
```

---

## 4. 핵심 Spring Boot 개념

### 4.1 어노테이션 (Annotations)

Spring Boot는 어노테이션 기반으로 동작합니다.

#### 클래스 레벨 어노테이션

```java
// 메인 클래스
@SpringBootApplication  // 자동 설정 + 컴포넌트 스캔 + 설정 활성화
@EnableJpaAuditing      // JPA 감사(Auditing) 기능 활성화
public class GroupCallApplication { ... }

// 컨트롤러
@RestController         // @Controller + @ResponseBody
@RequestMapping("/api/v1/users")  // 기본 URL 경로
public class UserController { ... }

// 서비스
@Service("userService") // 서비스 빈 등록
public class UserServiceImpl implements UserService { ... }

// 저장소
@Repository            // DAO 빈 등록
public interface UserRepository extends JpaRepository<User, Long> { ... }

// 엔티티
@Entity                // JPA 엔티티
@Table(name = "users") // 테이블명 지정
public class User { ... }

// 설정
@Configuration         // 설정 클래스
@EnableWebSecurity     // Spring Security 활성화
public class SecurityConfig { ... }
```

#### 메서드/필드 레벨 어노테이션

```java
// HTTP 매핑
@GetMapping("/me")           // GET 요청 매핑
@PostMapping("/login")       // POST 요청 매핑
@PutMapping("/{id}")         // PUT 요청 매핑
@DeleteMapping("/{id}")      // DELETE 요청 매핑

// 파라미터 바인딩
@RequestBody UserLoginPostReq req   // JSON → 객체 변환
@PathVariable Long id               // URL 경로 변수
@RequestParam String name           // 쿼리 파라미터

// 의존성 주입
@Autowired                          // 자동 주입
@Value("${jwt.secret}")             // 설정값 주입

// JPA
@Id                                 // 기본키
@GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가
@Column(name = "user_id")           // 컬럼명 지정
@CreatedDate                        // 생성일 자동 설정
@LastModifiedDate                   // 수정일 자동 설정

// Lombok
@Getter @Setter                     // getter/setter 자동 생성
@NoArgsConstructor                  // 기본 생성자
@AllArgsConstructor                 // 전체 필드 생성자
```

---

### 4.2 의존성 주입 (Dependency Injection)

Spring이 객체를 생성하고 관리합니다.

```java
@Service
public class UserServiceImpl implements UserService {

    // 방법 1: 필드 주입 (비권장)
    @Autowired
    private UserRepository userRepository;

    // 방법 2: 생성자 주입 (권장)
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 방법 3: Lombok + 생성자 주입 (권장)
    @RequiredArgsConstructor
    public class UserServiceImpl {
        private final UserRepository userRepository;
    }
}
```

**장점**:
- 객체 생명주기 관리 자동화
- 테스트 시 Mock 객체 주입 용이
- 느슨한 결합 (Loose Coupling)

---

### 4.3 Bean과 Component

Spring이 관리하는 객체를 **Bean**이라고 합니다.

```java
// 방법 1: @Component 계열 어노테이션
@Component    // 일반 컴포넌트
@Service      // 서비스 계층
@Repository   // 데이터 접근 계층
@Controller   // 프레젠테이션 계층

// 방법 2: @Bean 메서드
@Configuration
public class AppConfig {

    @Bean  // 메서드 반환값을 Bean으로 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### 4.4 AOP (Aspect Oriented Programming)

횡단 관심사(Cross-cutting Concerns)를 분리합니다.

```
┌─────────────────────────────────────────────────────────┐
│                    요청 처리 흐름                        │
├─────────────────────────────────────────────────────────┤
│  요청 → [Filter] → [Security] → [Controller] → 응답     │
│              ↑           ↑            ↑                  │
│              └───────────┴────────────┘                  │
│                      AOP                                 │
│              (로깅, 보안, 트랜잭션)                       │
└─────────────────────────────────────────────────────────┘
```

**이 프로젝트의 AOP 적용**:
- `JwtAuthenticationFilter`: 모든 요청에 JWT 검증
- `@Transactional`: 서비스 메서드에 트랜잭션 적용
- `@ControllerAdvice`: 전역 예외 처리

---

## 5. 계층형 아키텍처

### 5.1 3-Tier 아키텍처

```
┌─────────────────────────────────────────────────────────────────┐
│                    Presentation Layer (표현 계층)                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  AuthController          UserController                  │   │
│  │  - POST /api/v1/auth/login   - POST /api/v1/users       │   │
│  │  - JWT 토큰 발급             - GET /api/v1/users/me      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Request DTO              Response DTO                   │   │
│  │  - UserLoginPostReq       - UserLoginPostRes            │   │
│  │  - UserRegisterPostReq    - UserRes                     │   │
│  └─────────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────┤
│                    Business Layer (비즈니스 계층)                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  UserService (Interface)                                 │   │
│  │  └── UserServiceImpl (구현체)                            │   │
│  │      - createUser(): 회원가입 로직                       │   │
│  │      - getUserByUserId(): 사용자 조회                    │   │
│  │      - 비밀번호 암호화, 비즈니스 규칙 적용                │   │
│  └─────────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────┤
│                    Data Access Layer (데이터 계층)               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  UserRepository (Spring Data JPA)                        │   │
│  │  - save(), findById(), findAll()                        │   │
│  │  - findByUserId() (커스텀 메서드)                        │   │
│  │                                                          │   │
│  │  UserRepositorySupport (QueryDSL)                        │   │
│  │  - 복잡한 동적 쿼리 처리                                 │   │
│  └─────────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────┤
│                    Domain Layer (도메인 계층)                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  BaseEntity                                              │   │
│  │  └── User Entity                                         │   │
│  │      - id, userId, password, name, department, position │   │
│  │      - createdAt, updatedAt (감사 필드)                  │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
                    ┌─────────────────┐
                    │    MySQL DB     │
                    │   ssafy_web_db  │
                    └─────────────────┘
```

---

### 5.2 Controller (컨트롤러)

HTTP 요청을 받아 서비스에 위임합니다.

```java
@Tag(name = "Auth", description = "인증 API")  // Swagger 그룹
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    @Operation(summary = "로그인")  // Swagger 설명
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    public ResponseEntity<UserLoginPostRes> login(
            @RequestBody UserLoginPostReq loginInfo) {

        String userId = loginInfo.getId();
        String password = loginInfo.getPassword();

        // 1. 사용자 조회
        User user = userService.getUserByUserId(userId);
        if (user == null) {
            return ResponseEntity.status(404)
                .body(UserLoginPostRes.of(404, "User not found", null));
        }

        // 2. 비밀번호 검증
        if (passwordEncoder.matches(password, user.getPassword())) {
            // 3. JWT 토큰 생성
            String accessToken = jwtTokenUtil.getToken(userId);
            return ResponseEntity.ok(
                UserLoginPostRes.of(200, "Success", accessToken));
        }

        return ResponseEntity.status(401)
            .body(UserLoginPostRes.of(401, "Invalid Password", null));
    }
}
```

**핵심 포인트**:
- `@RestController`: JSON 응답 자동 변환
- `@RequestBody`: JSON → 객체 역직렬화
- `ResponseEntity`: HTTP 상태 코드 + 바디 제어

---

### 5.3 Service (서비스)

비즈니스 로직을 처리합니다.

```java
// 인터페이스 (계약)
public interface UserService {
    User createUser(UserRegisterPostReq userRegisterInfo);
    User getUserByUserId(String userId);
}

// 구현체
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepositorySupport userRepositorySupport;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserRegisterPostReq userRegisterInfo) {
        User user = new User();
        user.setUserId(userRegisterInfo.getId());

        // 비밀번호 암호화 (BCrypt)
        user.setPassword(passwordEncoder.encode(userRegisterInfo.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User getUserByUserId(String userId) {
        // QueryDSL 사용
        return userRepositorySupport.findUserByUserId(userId).orElse(null);
    }
}
```

**핵심 포인트**:
- 인터페이스 분리: 느슨한 결합
- 비밀번호 암호화: BCrypt (salt + hash)
- 트랜잭션 관리: `@Transactional` 가능

---

### 5.4 Repository (저장소)

데이터 접근을 담당합니다.

```java
// Spring Data JPA - 메서드 이름으로 쿼리 자동 생성
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // SELECT * FROM users WHERE user_id = ?
    Optional<User> findByUserId(String userId);

    // SELECT * FROM users WHERE name = ? AND department = ?
    List<User> findByNameAndDepartment(String name, String department);

    // SELECT * FROM users WHERE name LIKE %?%
    List<User> findByNameContaining(String name);
}

// QueryDSL - 타입 안전 동적 쿼리
@Repository
public class UserRepositorySupport {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    QUser qUser = QUser.user;  // 자동 생성된 Q클래스

    public Optional<User> findUserByUserId(String userId) {
        User user = jpaQueryFactory
            .select(qUser)
            .from(qUser)
            .where(qUser.userId.eq(userId))  // 컴파일 타임 타입 체크
            .fetchOne();
        return Optional.ofNullable(user);
    }

    // 동적 쿼리 예시
    public List<User> searchUsers(String name, String dept) {
        BooleanBuilder builder = new BooleanBuilder();

        if (name != null) {
            builder.and(qUser.name.contains(name));
        }
        if (dept != null) {
            builder.and(qUser.department.eq(dept));
        }

        return jpaQueryFactory
            .selectFrom(qUser)
            .where(builder)
            .fetch();
    }
}
```

**Spring Data JPA vs QueryDSL**:

| 특성 | Spring Data JPA | QueryDSL |
|------|-----------------|----------|
| 단순 쿼리 | 매우 편리 | 코드량 많음 |
| 복잡한 쿼리 | 어려움 | 강력함 |
| 타입 안전성 | 런타임 체크 | 컴파일 타임 체크 |
| 동적 쿼리 | 제한적 | 매우 유연 |

---

### 5.5 Entity (엔티티)

데이터베이스 테이블과 매핑됩니다.

```java
// 공통 필드 (상속용)
@Getter
@MappedSuperclass  // 테이블 생성 안 함, 필드만 상속
@EntityListeners(AuditingEntityListener.class)  // 감사 기능
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Long id;

    @CreatedDate  // INSERT 시 자동 설정
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate  // INSERT/UPDATE 시 자동 설정
    private LocalDateTime updatedAt;
}

// 사용자 엔티티
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    private String userId;  // 로그인 ID

    @JsonIgnore  // JSON 응답에서 제외
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // 요청은 받음
    private String password;

    private String name;
    private String department;
    private String position;
}
```

**생성되는 테이블**:
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    password VARCHAR(255),
    name VARCHAR(255),
    department VARCHAR(255),
    position VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
```

---

### 5.6 DTO (Data Transfer Object)

계층 간 데이터 전송용 객체입니다.

```java
// 요청 DTO
@Getter
@Setter
@Schema(description = "유저 로그인 요청")
public class UserLoginPostReq {

    @Schema(description = "유저 ID", example = "ssafy_web")
    private String id;

    @Schema(description = "유저 Password", example = "your_password")
    private String password;
}

// 응답 DTO
@Getter
@Setter
@Schema(description = "유저 로그인 응답")
public class UserLoginPostRes extends BaseResponseBody {

    @Schema(description = "JWT 토큰")
    private String accessToken;

    // 정적 팩토리 메서드
    public static UserLoginPostRes of(Integer statusCode,
                                      String message,
                                      String accessToken) {
        UserLoginPostRes res = new UserLoginPostRes();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setAccessToken(accessToken);
        return res;
    }
}

// 공통 응답 형식
@Getter
@Setter
public class BaseResponseBody {
    private Integer statusCode;
    private String message;

    public static BaseResponseBody of(Integer statusCode, String message) {
        BaseResponseBody body = new BaseResponseBody();
        body.setStatusCode(statusCode);
        body.setMessage(message);
        return body;
    }
}
```

**DTO 사용 이유**:
- 엔티티 직접 노출 방지 (보안)
- API 버전별 다른 응답 구조
- 불필요한 필드 제외

---

## 6. Spring Security & JWT

### 6.1 Spring Security 흐름

```
┌─────────────────────────────────────────────────────────────────────┐
│                      HTTP 요청 처리 흐름                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [Client]                                                           │
│     │                                                               │
│     │ Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...                │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    Filter Chain                              │   │
│  │  ┌───────────────────────────────────────────────────────┐  │   │
│  │  │ 1. CorsFilter (CORS 처리)                              │  │   │
│  │  └───────────────────────────────────────────────────────┘  │   │
│  │                          ↓                                   │   │
│  │  ┌───────────────────────────────────────────────────────┐  │   │
│  │  │ 2. JwtAuthenticationFilter (JWT 검증)                  │  │   │
│  │  │    - Authorization 헤더에서 토큰 추출                   │  │   │
│  │  │    - 토큰 유효성 검증 (서명, 만료)                      │  │   │
│  │  │    - userId 추출 → User 조회                           │  │   │
│  │  │    - SecurityContext에 인증 정보 저장                   │  │   │
│  │  └───────────────────────────────────────────────────────┘  │   │
│  │                          ↓                                   │   │
│  │  ┌───────────────────────────────────────────────────────┐  │   │
│  │  │ 3. AuthorizationFilter (권한 검사)                     │  │   │
│  │  │    - 인증 필요 URL 확인                                │  │   │
│  │  │    - 인증 실패 시 401 반환                             │  │   │
│  │  └───────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                          ↓                                          │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    DispatcherServlet                         │   │
│  │                          ↓                                   │   │
│  │                    Controller                                │   │
│  │                          ↓                                   │
│  │                    Service                                   │   │
│  │                          ↓                                   │
│  │                    Repository                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

### 6.2 SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS 활성화
            .cors(Customizer.withDefaults())

            // CSRF 비활성화 (REST API는 불필요)
            .csrf(AbstractHttpConfigurer::disable)

            // 세션 사용 안 함 (Stateless)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 공개 API
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()  // 로그인
                .requestMatchers("/api/v1/users").permitAll()    // 회원가입

                // 나머지는 인증 필요
                .anyRequest().authenticated())

            // JWT 필터 등록 (UsernamePasswordAuthenticationFilter 이전에 실행)
            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**핵심 설정**:
| 설정 | 설명 |
|------|------|
| `cors()` | 프론트엔드 Cross-Origin 요청 허용 |
| `csrf().disable()` | REST API는 CSRF 보호 불필요 |
| `sessionCreationPolicy(STATELESS)` | 서버에 세션 저장 안 함 |
| `authorizeHttpRequests()` | URL별 접근 권한 |
| `addFilterBefore()` | 커스텀 필터 추가 |

---

### 6.3 JWT 토큰 구조

```
┌─────────────────────────────────────────────────────────────────────┐
│                        JWT Token 구조                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.                             │
│  eyJzdWIiOiJzc2FmeV93ZWIiLCJpYXQiOjE3MDQ2NzA0MDAsImV4cCI6MTcwNTk2NjQwMH0.│
│  qF3xKL2x...서명값                                                  │
│                                                                     │
│  ┌─────────────┐   ┌─────────────────────────┐   ┌──────────────┐  │
│  │   Header    │ . │        Payload          │ . │  Signature   │  │
│  │  (헤더)     │   │       (페이로드)         │   │   (서명)     │  │
│  ├─────────────┤   ├─────────────────────────┤   ├──────────────┤  │
│  │ {           │   │ {                       │   │ HMAC512(     │  │
│  │  "alg":     │   │  "sub": "ssafy_web",   │   │   header +   │  │
│  │   "HS512",  │   │  "iat": 1704670400,    │   │   payload,   │  │
│  │  "typ":     │   │  "exp": 1705966400     │   │   secret_key │  │
│  │   "JWT"     │   │ }                       │   │ )            │  │
│  │ }           │   │                         │   │              │  │
│  └─────────────┘   └─────────────────────────┘   └──────────────┘  │
│                                                                     │
│  ※ 각 부분은 Base64URL 인코딩됨                                      │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

Payload 클레임(Claims):
- sub (Subject): 사용자 ID ("ssafy_web")
- iat (Issued At): 토큰 발급 시간 (Unix timestamp)
- exp (Expiration): 토큰 만료 시간 (15일 후)
```

---

### 6.4 JwtTokenUtil.java

```java
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;  // 15일 (밀리초)

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // 토큰 생성
    public String getToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity);

        return JWT.create()
            .withSubject(userId)            // 사용자 ID
            .withIssuedAt(now)              // 발급 시간
            .withExpiresAt(expiry)          // 만료 시간
            .sign(Algorithm.HMAC512(secretKey.getBytes()));  // 서명
    }

    // 토큰에서 userId 추출
    public String getUserId(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey.getBytes()))
                .build();
            verifier.verify(token);  // 서명 검증 + 만료 확인
            return true;
        } catch (TokenExpiredException e) {
            log.error("토큰 만료됨");
        } catch (SignatureVerificationException e) {
            log.error("서명 검증 실패");
        } catch (JWTVerificationException e) {
            log.error("토큰 검증 실패");
        }
        return false;
    }
}
```

---

### 6.5 JwtAuthenticationFilter.java

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
        throws ServletException, IOException {

        // 1. 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 검증
        if (StringUtils.hasText(token) && jwtTokenUtil.validateToken(token)) {

            // 3. 토큰에서 userId 추출
            String userId = jwtTokenUtil.getUserId(token);

            // 4. DB에서 사용자 조회
            User user = userService.getUserByUserId(userId);

            if (user != null) {
                // 5. UserDetails 생성
                SsafyUserDetails userDetails = new SsafyUserDetails(user);

                // 6. Authentication 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,    // principal
                        null,           // credentials
                        userDetails.getAuthorities()  // authorities
                    );

                // 7. SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 8. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    // "Bearer eyJ..." → "eyJ..."
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtTokenUtil.HEADER_STRING);
        if (StringUtils.hasText(bearerToken) &&
            bearerToken.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
            return bearerToken.substring(7);  // "Bearer " 제거
        }
        return null;
    }
}
```

---

### 6.6 SsafyUserDetails.java

Spring Security의 UserDetails 인터페이스 구현입니다.

```java
public class SsafyUserDetails implements UserDetails {

    private final User user;  // JPA 엔티티

    public SsafyUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return user.getUserId();  // 로그인 ID
    }

    @Override
    public String getPassword() {
        return user.getPassword();  // 암호화된 비밀번호
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();  // 역할 없음 (추후 확장)
    }

    // 계정 상태 (모두 true로 설정)
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
```

**컨트롤러에서 사용**:
```java
@GetMapping("/me")
public ResponseEntity<UserRes> getUserInfo(Authentication authentication) {
    // authentication.getPrincipal() → SsafyUserDetails
    SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUsername();
    User user = userService.getUserByUserId(userId);
    return ResponseEntity.ok(UserRes.of(user));
}
```

---

## 7. JPA & 데이터베이스

### 7.1 JPA 기본 개념

```
┌─────────────────────────────────────────────────────────────────┐
│                    JPA (Java Persistence API)                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Java 객체 (Entity)  ←─────→  데이터베이스 테이블               │
│                                                                 │
│  @Entity                     CREATE TABLE users (              │
│  public class User {            id BIGINT PRIMARY KEY,         │
│      @Id                        user_id VARCHAR(255),          │
│      Long id;                   password VARCHAR(255),         │
│                                 created_at DATETIME            │
│      String userId;          );                                │
│      String password;                                          │
│  }                                                              │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │               ORM (Object-Relational Mapping)            │   │
│  │                                                          │   │
│  │  user.setPassword("new")  →  UPDATE users SET ...        │   │
│  │  repository.save(user)    →  INSERT INTO users ...       │   │
│  │  repository.findById(1)   →  SELECT * FROM users ...     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### 7.2 JPA 어노테이션

```java
@Entity                          // JPA 관리 엔티티
@Table(name = "users")           // 테이블명 지정

public class User {

    @Id                          // 기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;       // 컬럼명, NOT NULL, UNIQUE

    @Column(length = 255)
    private String password;     // VARCHAR(255)

    // 관계 매핑 (1:N 예시)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    // N:1 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
```

---

### 7.3 JPA Auditing (감사)

생성일/수정일을 자동으로 관리합니다.

```java
// 1. 메인 클래스에서 활성화
@SpringBootApplication
@EnableJpaAuditing  // 감사 기능 활성화
public class GroupCallApplication { ... }

// 2. 엔티티에 리스너 등록
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)  // 감사 리스너
public abstract class BaseEntity {

    @CreatedDate                    // INSERT 시 자동 설정
    @Column(updatable = false)      // UPDATE 시 변경 불가
    private LocalDateTime createdAt;

    @LastModifiedDate               // INSERT/UPDATE 시 자동 설정
    private LocalDateTime updatedAt;
}
```

**동작 원리**:
```
repository.save(new User())
    ↓
AuditingEntityListener 감지
    ↓
createdAt = LocalDateTime.now()
updatedAt = LocalDateTime.now()
    ↓
INSERT INTO users (..., created_at, updated_at) VALUES (..., NOW(), NOW())
```

---

### 7.4 Spring Data JPA 쿼리 메서드

메서드 이름으로 SQL을 자동 생성합니다.

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // 메서드 이름 → SQL 변환

    // findBy + 필드명
    Optional<User> findByUserId(String userId);
    // → SELECT * FROM users WHERE user_id = ?

    // And 조건
    List<User> findByNameAndDepartment(String name, String dept);
    // → SELECT * FROM users WHERE name = ? AND department = ?

    // Or 조건
    List<User> findByNameOrDepartment(String name, String dept);
    // → SELECT * FROM users WHERE name = ? OR department = ?

    // Like 검색
    List<User> findByNameContaining(String name);
    // → SELECT * FROM users WHERE name LIKE %?%

    // 정렬
    List<User> findByDepartmentOrderByNameAsc(String dept);
    // → SELECT * FROM users WHERE department = ? ORDER BY name ASC

    // 페이징
    Page<User> findByDepartment(String dept, Pageable pageable);
    // → SELECT * FROM users WHERE department = ? LIMIT ? OFFSET ?

    // 존재 여부
    boolean existsByUserId(String userId);
    // → SELECT COUNT(*) > 0 FROM users WHERE user_id = ?

    // 개수
    long countByDepartment(String dept);
    // → SELECT COUNT(*) FROM users WHERE department = ?

    // 삭제
    void deleteByUserId(String userId);
    // → DELETE FROM users WHERE user_id = ?
}
```

**키워드 조합**:
| 키워드 | 예시 | SQL |
|--------|------|-----|
| `And` | `findByNameAndAge` | `WHERE name = ? AND age = ?` |
| `Or` | `findByNameOrAge` | `WHERE name = ? OR age = ?` |
| `Is`, `Equals` | `findByName`, `findByNameIs` | `WHERE name = ?` |
| `Between` | `findByAgeBetween` | `WHERE age BETWEEN ? AND ?` |
| `LessThan` | `findByAgeLessThan` | `WHERE age < ?` |
| `GreaterThan` | `findByAgeGreaterThan` | `WHERE age > ?` |
| `Like` | `findByNameLike` | `WHERE name LIKE ?` |
| `Containing` | `findByNameContaining` | `WHERE name LIKE %?%` |
| `StartingWith` | `findByNameStartingWith` | `WHERE name LIKE ?%` |
| `EndingWith` | `findByNameEndingWith` | `WHERE name LIKE %?` |
| `OrderBy` | `findByOrderByAgeDesc` | `ORDER BY age DESC` |
| `Not` | `findByNameNot` | `WHERE name <> ?` |
| `In` | `findByAgeIn(List)` | `WHERE age IN (?, ?, ?)` |
| `IsNull` | `findByNameIsNull` | `WHERE name IS NULL` |
| `IsNotNull` | `findByNameIsNotNull` | `WHERE name IS NOT NULL` |

---

### 7.5 QueryDSL 동적 쿼리

복잡한 조건의 동적 쿼리를 타입 안전하게 작성합니다.

```java
@Repository
public class UserRepositorySupport {

    @Autowired
    private JPAQueryFactory queryFactory;

    QUser user = QUser.user;  // 자동 생성된 Q클래스

    // 단일 조회
    public Optional<User> findUserByUserId(String userId) {
        User result = queryFactory
            .selectFrom(user)
            .where(user.userId.eq(userId))
            .fetchOne();
        return Optional.ofNullable(result);
    }

    // 동적 검색 (검색 조건이 null이면 무시)
    public List<User> searchUsers(String name, String dept, String position) {
        BooleanBuilder builder = new BooleanBuilder();

        if (name != null) {
            builder.and(user.name.contains(name));
        }
        if (dept != null) {
            builder.and(user.department.eq(dept));
        }
        if (position != null) {
            builder.and(user.position.eq(position));
        }

        return queryFactory
            .selectFrom(user)
            .where(builder)
            .orderBy(user.createdAt.desc())
            .fetch();
    }

    // 페이징 + 정렬
    public Page<User> findUsersWithPaging(Pageable pageable) {
        List<User> content = queryFactory
            .selectFrom(user)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(user.id.desc())
            .fetch();

        long total = queryFactory
            .select(user.count())
            .from(user)
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
```

**QueryDSL 장점**:
- 컴파일 타임 타입 체크 (필드명 오타 방지)
- IDE 자동완성 지원
- 복잡한 조인, 서브쿼리 쉽게 작성
- 동적 쿼리 깔끔하게 처리

---

## 8. 설정 파일 분석

### 8.1 application.properties

```properties
# ===== 서버 설정 =====
server.port=8080                              # 서버 포트
server.servlet.context-path=/                 # 컨텍스트 경로
server.servlet.encoding.charset=UTF-8         # 인코딩

# ===== 데이터베이스 설정 =====
spring.datasource.url=jdbc:mysql://localhost:3306/ssafy_web_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=ssafy
spring.datasource.password=ssafy

# ===== JPA/Hibernate 설정 =====
spring.jpa.hibernate.ddl-auto=update          # 스키마 자동 업데이트
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true                      # SQL 로그 출력
spring.jpa.properties.hibernate.format_sql=true  # SQL 포맷팅

# ===== JWT 설정 =====
jwt.secret=dyAeHubOOc8KaOfYB6XEQoEj1QzRlVgtjNL8PYs1A1tymZvvqkcEU7L1imkKHeDa
jwt.access-token-validity=1296000000          # 15일 (밀리초)

# ===== Swagger 설정 =====
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs

# ===== 로깅 설정 =====
logging.level.com.ssafy=DEBUG                 # 애플리케이션 로그
logging.level.org.hibernate.SQL=DEBUG         # SQL 로그
```

**ddl-auto 옵션**:
| 옵션 | 설명 | 사용 환경 |
|------|------|-----------|
| `create` | 매번 테이블 재생성 | 테스트 |
| `create-drop` | 종료 시 테이블 삭제 | 테스트 |
| `update` | 변경사항만 반영 | 개발 |
| `validate` | 스키마 검증만 | 운영 |
| `none` | 아무것도 안 함 | 운영 |

---

### 8.2 JpaConfig.java (JSON 설정)

```java
@Configuration
public class JpaConfig {

    // QueryDSL 팩토리
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    // JSON 직렬화 설정
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule timeModule = new JavaTimeModule();

        // 날짜 형식 설정
        timeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        timeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return new ObjectMapper()
            // 빈 객체 직렬화 허용
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            // 날짜를 문자열로 (Unix timestamp 아님)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            // 알 수 없는 프로퍼티 무시
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(timeModule)
            .registerModule(new Jdk8Module());  // Optional 지원
    }
}
```

---

## 9. 전체 요청 흐름도

### 9.1 회원가입 흐름

```
┌─────────────────────────────────────────────────────────────────────┐
│                        회원가입 요청 흐름                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [Client]                                                           │
│     │                                                               │
│     │ POST /api/v1/users                                           │
│     │ Content-Type: application/json                               │
│     │ {                                                             │
│     │   "id": "ssafy_web",                                         │
│     │   "password": "mypassword123"                                │
│     │ }                                                             │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ SecurityConfig                                               │   │
│  │ - /api/v1/users는 permitAll() → 인증 없이 접근 가능          │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ UserController.register()                                    │   │
│  │                                                              │   │
│  │ @PostMapping                                                 │   │
│  │ public ResponseEntity<BaseResponseBody> register(            │   │
│  │     @RequestBody UserRegisterPostReq registerInfo) {         │   │
│  │                                                              │   │
│  │     // JSON → UserRegisterPostReq 자동 변환                  │   │
│  │     userService.createUser(registerInfo);                    │   │
│  │     return ResponseEntity.ok(BaseResponseBody.of(200, "OK"));│   │
│  │ }                                                            │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ UserServiceImpl.createUser()                                 │   │
│  │                                                              │   │
│  │ User user = new User();                                      │   │
│  │ user.setUserId(registerInfo.getId());                        │   │
│  │ user.setPassword(                                            │   │
│  │     passwordEncoder.encode(registerInfo.getPassword())       │   │
│  │ );  // BCrypt 암호화: "mypassword123" → "$2a$10$abc..."      │   │
│  │                                                              │   │
│  │ return userRepository.save(user);                            │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ UserRepository.save()                                        │   │
│  │                                                              │   │
│  │ JPA Auditing 동작:                                           │   │
│  │   - createdAt = NOW()                                        │   │
│  │   - updatedAt = NOW()                                        │   │
│  │                                                              │   │
│  │ INSERT INTO users (user_id, password, created_at, updated_at)│   │
│  │ VALUES ('ssafy_web', '$2a$10$abc...', NOW(), NOW())          │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     ▼                                                               │
│  [Client]                                                           │
│  ← 200 OK                                                          │
│  { "statusCode": 200, "message": "Success" }                       │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

### 9.2 로그인 흐름

```
┌─────────────────────────────────────────────────────────────────────┐
│                         로그인 요청 흐름                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [Client]                                                           │
│     │                                                               │
│     │ POST /api/v1/auth/login                                      │
│     │ { "id": "ssafy_web", "password": "mypassword123" }           │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ AuthController.login()                                       │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     │ 1. 사용자 조회                                               │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ UserService.getUserByUserId("ssafy_web")                     │   │
│  │     ↓                                                        │   │
│  │ UserRepositorySupport.findUserByUserId()                     │   │
│  │     ↓                                                        │   │
│  │ SELECT * FROM users WHERE user_id = 'ssafy_web'              │   │
│  │     ↓                                                        │   │
│  │ User { id=1, userId="ssafy_web", password="$2a$10$..." }     │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     │ 2. 비밀번호 검증                                             │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ PasswordEncoder.matches()                                    │   │
│  │                                                              │   │
│  │ 입력: "mypassword123"                                        │   │
│  │ DB:   "$2a$10$abc..."                                        │   │
│  │                                                              │   │
│  │ BCrypt.checkpw("mypassword123", "$2a$10$abc...") → true     │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     │ 3. JWT 토큰 생성                                             │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ JwtTokenUtil.getToken("ssafy_web")                           │   │
│  │                                                              │   │
│  │ JWT.create()                                                 │   │
│  │   .withSubject("ssafy_web")      // 사용자 ID                │   │
│  │   .withIssuedAt(now)             // 발급 시간                │   │
│  │   .withExpiresAt(now + 15일)     // 만료 시간                │   │
│  │   .sign(HMAC512(secretKey))      // 서명                     │   │
│  │                                                              │   │
│  │ → "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzc2FmeV93ZWIi..."        │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     ▼                                                               │
│  [Client]                                                           │
│  ← 200 OK                                                          │
│  {                                                                  │
│    "statusCode": 200,                                              │
│    "message": "Success",                                           │
│    "accessToken": "eyJhbGciOiJIUzUxMiJ9..."                       │
│  }                                                                  │
│                                                                     │
│  [Client]                                                           │
│  → 토큰을 localStorage에 저장                                       │
│  → 이후 요청 시 Authorization 헤더에 포함                           │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

### 9.3 인증된 요청 흐름 (내 정보 조회)

```
┌─────────────────────────────────────────────────────────────────────┐
│                      인증된 요청 흐름                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [Client]                                                           │
│     │                                                               │
│     │ GET /api/v1/users/me                                         │
│     │ Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...                │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ JwtAuthenticationFilter.doFilterInternal()                   │   │
│  │                                                              │   │
│  │ 1. 토큰 추출                                                 │   │
│  │    "Bearer eyJ..." → "eyJ..."                                │   │
│  │                                                              │   │
│  │ 2. 토큰 검증                                                 │   │
│  │    jwtTokenUtil.validateToken("eyJ...")                      │   │
│  │    - 서명 검증 (HMAC512)                                     │   │
│  │    - 만료 시간 확인                                          │   │
│  │    → true (유효함)                                           │   │
│  │                                                              │   │
│  │ 3. userId 추출                                               │   │
│  │    jwtTokenUtil.getUserId("eyJ...")                          │   │
│  │    → "ssafy_web"                                             │   │
│  │                                                              │   │
│  │ 4. 사용자 조회                                               │   │
│  │    userService.getUserByUserId("ssafy_web")                  │   │
│  │    → User 엔티티                                             │   │
│  │                                                              │   │
│  │ 5. SecurityContext 설정                                      │   │
│  │    SsafyUserDetails userDetails = new SsafyUserDetails(user);│   │
│  │    Authentication auth = new UsernamePassword...Token(...);  │   │
│  │    SecurityContextHolder.getContext().setAuthentication(auth);│   │
│  │                                                              │   │
│  │ 6. 다음 필터로 진행                                          │   │
│  │    filterChain.doFilter(request, response);                  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ SecurityConfig - AuthorizationFilter                         │   │
│  │                                                              │   │
│  │ /api/v1/users/me → anyRequest().authenticated()             │   │
│  │ SecurityContext에 Authentication 있음 → 통과                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ UserController.getUserInfo(Authentication authentication)    │   │
│  │                                                              │   │
│  │ // SecurityContext에서 자동 주입됨                           │   │
│  │ SsafyUserDetails userDetails =                               │   │
│  │     (SsafyUserDetails) authentication.getPrincipal();        │   │
│  │                                                              │   │
│  │ String userId = userDetails.getUsername();  // "ssafy_web"   │   │
│  │                                                              │   │
│  │ User user = userService.getUserByUserId(userId);             │   │
│  │                                                              │   │
│  │ return ResponseEntity.ok(UserRes.of(user));                  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│     │                                                               │
│     ▼                                                               │
│  [Client]                                                           │
│  ← 200 OK                                                          │
│  { "userId": "ssafy_web" }                                         │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 10. 주요 파일별 역할

### 10.1 진입점 및 설정

| 파일 | 역할 |
|------|------|
| `GroupCallApplication.java` | 메인 클래스, 앱 시작점 |
| `SecurityConfig.java` | Spring Security 설정 |
| `JpaConfig.java` | JPA + JSON 설정 |
| `WebMvcConfig.java` | CORS 설정 |
| `SwaggerConfig.java` | API 문서화 설정 |

### 10.2 API 계층

| 파일 | 역할 |
|------|------|
| `AuthController.java` | 로그인 API |
| `UserController.java` | 회원가입, 내 정보 API |
| `UserService.java` | 사용자 서비스 인터페이스 |
| `UserServiceImpl.java` | 사용자 서비스 구현 |

### 10.3 인증 관련

| 파일 | 역할 |
|------|------|
| `JwtTokenUtil.java` | JWT 생성/검증 |
| `JwtAuthenticationFilter.java` | 요청마다 JWT 검증 |
| `SsafyUserDetails.java` | UserDetails 구현 |
| `PasswordEncoderConfig.java` | BCrypt 설정 |

### 10.4 데이터 계층

| 파일 | 역할 |
|------|------|
| `BaseEntity.java` | 공통 필드 (id, 생성/수정일) |
| `User.java` | 사용자 엔티티 |
| `UserRepository.java` | Spring Data JPA |
| `UserRepositorySupport.java` | QueryDSL |

### 10.5 DTO

| 파일 | 역할 |
|------|------|
| `UserLoginPostReq.java` | 로그인 요청 |
| `UserRegisterPostReq.java` | 회원가입 요청 |
| `UserLoginPostRes.java` | 로그인 응답 (토큰 포함) |
| `UserRes.java` | 사용자 정보 응답 |
| `BaseResponseBody.java` | 공통 응답 형식 |

---

## 부록: API 명세

### A. 엔드포인트 목록

| 메서드 | URL | 인증 | 설명 |
|--------|-----|------|------|
| POST | `/api/v1/auth/login` | X | 로그인 |
| POST | `/api/v1/users` | X | 회원가입 |
| GET | `/api/v1/users/me` | O | 내 정보 조회 |
| GET | `/swagger-ui.html` | X | API 문서 |

### B. 요청/응답 예시

**로그인**:
```bash
# 요청
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"id": "ssafy_web", "password": "test1234"}'

# 응답 (성공)
{
  "statusCode": 200,
  "message": "Success",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**내 정보 조회**:
```bash
# 요청
curl -X GET http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# 응답
{
  "userId": "ssafy_web"
}
```

---

## 참고 자료

- [Spring Boot 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security 레퍼런스](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [QueryDSL 레퍼런스](http://querydsl.com/static/querydsl/latest/reference/html/)
- [JWT.io](https://jwt.io/) - JWT 디버거

---
