최초 작성자 : 윤상훈(0108)
최종 수정자 : 윤상훈(0108) 변경사유 :  

# ModuStudy - Spring Boot + JPA 개념 정리

> SSAFY 2학기 프로젝트를 위한 Spring Boot & JPA 기초 가이드

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [프로젝트 구조](#3-프로젝트-구조)
4. [JPA 기초](#4-jpa-기초)
5. [Entity 작성](#5-entity-작성)
6. [Repository](#6-repository)
7. [Service 계층](#7-service-계층)
8. [Controller & DTO](#8-controller--dto)
9. [연관관계 매핑](#9-연관관계-매핑)
10. [Spring Security + JWT](#10-spring-security--jwt)
11. [예외 처리](#11-예외-처리)
12. [자주 하는 실수 & 해결법](#12-자주-하는-실수--해결법)

---

## 1. 프로젝트 개요

### ModuStudy 백엔드 구조

```
클라이언트 요청
       │
       ▼
┌─────────────────┐
│   Controller    │  ← HTTP 요청/응답 처리
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    Service      │  ← 비즈니스 로직
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Repository    │  ← 데이터 접근 (JPA)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    Database     │  ← MySQL
└─────────────────┘
```

---

## 2. 기술 스택

| 기술 | 버전 | 용도 |
|------|------|------|
| Spring Boot | 3.2.x | 웹 프레임워크 |
| Spring Data JPA | - | ORM (DB 매핑) |
| Spring Security | - | 인증/인가 |
| MySQL | 8.0 | 데이터베이스 |
| Lombok | - | 보일러플레이트 제거 |
| JWT | 0.12.x | 토큰 인증 |

### build.gradle

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.ssafy'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-websocket'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'

    // Database
    runtimeOnly 'com.mysql:mysql-connector-j'

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/modustudy?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: ssafy
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update  # 개발: update, 운영: validate
    properties:
      hibernate:
        format_sql: true
        show_sql: true
        dialect: org.hibernate.dialect.MySQLDialect

  # 로깅 (SQL 파라미터 확인)
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

# JWT 설정
jwt:
  secret: your-256-bit-secret-key-here-minimum-32-characters
  access-token-validity: 3600000   # 1시간 (ms)
  refresh-token-validity: 604800000  # 7일 (ms)
```

---

## 3. 프로젝트 구조

```
src/main/java/com/ssafy/modustudy/
├── ModuStudyApplication.java        # 메인 클래스
│
├── config/                          # 설정
│   ├── SecurityConfig.java          # Spring Security 설정
│   ├── WebSocketConfig.java         # WebSocket 설정
│   ├── JpaConfig.java               # JPA Auditing 설정
│   └── CorsConfig.java              # CORS 설정
│
├── controller/                      # REST API
│   ├── AuthController.java
│   ├── UserController.java
│   ├── TeamController.java
│   ├── ChannelController.java
│   └── MessageController.java
│
├── service/                         # 비즈니스 로직
│   ├── AuthService.java
│   ├── UserService.java
│   ├── TeamService.java
│   ├── ChannelService.java
│   └── MessageService.java
│
├── repository/                      # JPA Repository
│   ├── UserRepository.java
│   ├── TeamRepository.java
│   ├── TeamMemberRepository.java
│   ├── ChannelRepository.java
│   └── MessageRepository.java
│
├── entity/                          # JPA Entity
│   ├── User.java
│   ├── Team.java
│   ├── TeamMember.java
│   ├── Channel.java
│   ├── Message.java
│   └── BaseTimeEntity.java          # 공통 시간 필드
│
├── dto/                             # Request/Response DTO
│   ├── request/
│   │   ├── SignupRequest.java
│   │   ├── LoginRequest.java
│   │   └── TeamCreateRequest.java
│   └── response/
│       ├── UserResponse.java
│       ├── TeamResponse.java
│       └── ApiResponse.java         # 공통 응답 형식
│
├── security/                        # JWT, 인증
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
│
└── exception/                       # 예외 처리
    ├── GlobalExceptionHandler.java
    ├── CustomException.java
    └── ErrorCode.java
```

---

## 4. JPA 기초

### 4.1 JPA란?

**JPA (Java Persistence API)**: 자바 객체와 데이터베이스 테이블을 매핑해주는 ORM 기술

```
기존 방식 (JDBC/MyBatis):
  SQL 직접 작성 → ResultSet → 객체 변환

JPA 방식:
  객체 조작 → JPA가 자동으로 SQL 생성 → DB 반영
```

### 4.2 핵심 개념

| 용어 | 설명 |
|------|------|
| Entity | DB 테이블과 매핑되는 자바 클래스 |
| Repository | 데이터 접근 인터페이스 (DAO 역할) |
| 영속성 컨텍스트 | Entity를 관리하는 JPA 내부 저장소 |
| JPQL | 객체 지향 쿼리 언어 (SQL과 유사) |

### 4.3 왜 JPA를 사용하나요?

```java
// MyBatis 방식 - SQL 직접 작성
@Select("SELECT * FROM users WHERE user_id = #{userId}")
User findById(Long userId);

// JPA 방식 - 메서드 이름으로 자동 생성
User findById(Long userId);  // JPA가 SQL 자동 생성!
```

**장점**:
- SQL 작성 최소화
- 객체 중심 개발
- DB 변경 시 코드 수정 최소화
- 생산성 향상

---

## 5. Entity 작성

### 5.1 기본 Entity 구조

```java
package com.ssafy.modustudy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity                          // JPA Entity 선언
@Table(name = "users")           // 테이블명 (생략 시 클래스명)
@Getter                          // Lombok: getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자
@AllArgsConstructor
@Builder                         // 빌더 패턴
public class User {

    @Id                          // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)  // ENUM → 문자열 저장
    @Column(nullable = false)
    private UserStatus status = UserStatus.OFFLINE;

    // ENUM 정의
    public enum UserStatus {
        ONLINE, OFFLINE, AWAY, DND
    }

    // 비즈니스 메서드 (setter 대신 사용)
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }
}
```

### 5.2 주요 어노테이션

| 어노테이션 | 설명 | 예시 |
|-----------|------|------|
| `@Entity` | JPA Entity 선언 | 클래스 레벨 |
| `@Table` | 테이블명 지정 | `@Table(name = "users")` |
| `@Id` | PK 지정 | 필드 레벨 |
| `@GeneratedValue` | PK 생성 전략 | `IDENTITY` = AUTO_INCREMENT |
| `@Column` | 컬럼 속성 | `nullable`, `unique`, `length` |
| `@Enumerated` | Enum 타입 매핑 | `EnumType.STRING` 권장 |
| `@Temporal` | 날짜 타입 (구버전) | Java 8+ 는 불필요 |
| `@Lob` | 대용량 데이터 | TEXT, BLOB |

### 5.3 BaseTimeEntity (공통 시간 필드)

```java
package com.ssafy.modustudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass  // 상속용 (테이블 생성 X)
@EntityListeners(AuditingEntityListener.class)  // Auditing 활성화
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**JpaConfig.java** (Auditing 활성화)

```java
package com.ssafy.modustudy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  // 이거 필수!
public class JpaConfig {
}
```

**Entity에서 상속**

```java
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {  // 상속
    // createdAt, updatedAt 자동 관리
}
```

### 5.4 ModuStudy 주요 Entity

#### Team Entity

```java
@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "invite_code", nullable = false, unique = true, length = 20)
    private String inviteCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // 연관관계 (양방향 시)
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Channel> channels = new ArrayList<>();

    // 비즈니스 메서드
    public void updateInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
```

#### Channel Entity

```java
@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Channel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Long channelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    public enum ChannelType {
        TEXT, VOICE
    }
}
```

#### Message Entity

```java
@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 메시지 수정
    public void updateContent(String content) {
        this.content = content;
    }
}
```

---

## 6. Repository

### 6.1 기본 Repository

```java
package com.ssafy.modustudy.repository;

import com.ssafy.modustudy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<Entity, PK타입>
public interface UserRepository extends JpaRepository<User, Long> {
    // 기본 CRUD 메서드 자동 제공:
    // save(), findById(), findAll(), delete(), count() ...
}
```

### 6.2 쿼리 메서드 (메서드 이름으로 쿼리 생성)

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT * FROM users WHERE email = ? AND status = ?
    Optional<User> findByEmailAndStatus(String email, User.UserStatus status);

    // SELECT * FROM users WHERE nickname LIKE '%keyword%'
    List<User> findByNicknameContaining(String keyword);

    // SELECT * FROM users WHERE status = ? ORDER BY created_at DESC
    List<User> findByStatusOrderByCreatedAtDesc(User.UserStatus status);

    // EXISTS
    boolean existsByEmail(String email);

    // COUNT
    long countByStatus(User.UserStatus status);
}
```

### 6.3 쿼리 메서드 키워드

| 키워드 | 예시 | SQL |
|--------|------|-----|
| `And` | findByEmailAndNickname | WHERE email = ? AND nickname = ? |
| `Or` | findByEmailOrNickname | WHERE email = ? OR nickname = ? |
| `Between` | findByCreatedAtBetween | WHERE created_at BETWEEN ? AND ? |
| `LessThan` | findByAgeLessThan | WHERE age < ? |
| `GreaterThan` | findByAgeGreaterThan | WHERE age > ? |
| `Like` | findByNicknameLike | WHERE nickname LIKE ? |
| `Containing` | findByNicknameContaining | WHERE nickname LIKE '%?%' |
| `In` | findByStatusIn | WHERE status IN (?, ?, ?) |
| `OrderBy` | findByStatusOrderByCreatedAtDesc | ORDER BY created_at DESC |
| `Top/First` | findTop5ByStatus | LIMIT 5 |

### 6.4 JPQL 커스텀 쿼리

```java
public interface TeamRepository extends JpaRepository<Team, Long> {

    // JPQL - 객체 기준 쿼리
    @Query("SELECT t FROM Team t WHERE t.owner.userId = :userId")
    List<Team> findByOwnerId(@Param("userId") Long userId);

    // JOIN FETCH - N+1 문제 해결
    @Query("SELECT t FROM Team t JOIN FETCH t.owner WHERE t.teamId = :teamId")
    Optional<Team> findByIdWithOwner(@Param("teamId") Long teamId);

    // 네이티브 쿼리 (SQL 직접 작성)
    @Query(value = "SELECT * FROM teams WHERE name LIKE %:keyword%", nativeQuery = true)
    List<Team> searchByName(@Param("keyword") String keyword);
}
```

### 6.5 페이징 & 정렬

```java
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 페이징
    Page<Message> findByChannelChannelId(Long channelId, Pageable pageable);

    // 사용
    // Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
    // Page<Message> messages = messageRepository.findByChannelChannelId(1L, pageable);
}
```

**Service에서 사용:**

```java
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Page<Message> getMessages(Long channelId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return messageRepository.findByChannelChannelId(channelId, pageable);
    }
}
```

---

## 7. Service 계층

### 7.1 기본 Service 구조

```java
package com.ssafy.modustudy.service;

import com.ssafy.modustudy.dto.request.TeamCreateRequest;
import com.ssafy.modustudy.dto.response.TeamResponse;
import com.ssafy.modustudy.entity.Team;
import com.ssafy.modustudy.entity.TeamMember;
import com.ssafy.modustudy.entity.User;
import com.ssafy.modustudy.exception.CustomException;
import com.ssafy.modustudy.exception.ErrorCode;
import com.ssafy.modustudy.repository.TeamMemberRepository;
import com.ssafy.modustudy.repository.TeamRepository;
import com.ssafy.modustudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor  // final 필드 생성자 자동 생성
@Transactional(readOnly = true)  // 기본: 읽기 전용 트랜잭션
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    // 팀 생성
    @Transactional  // 쓰기 작업: readOnly = false
    public TeamResponse createTeam(Long userId, TeamCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 초대 코드 생성
        String inviteCode = UUID.randomUUID().toString().substring(0, 8);

        // 팀 생성
        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .inviteCode(inviteCode)
                .owner(user)
                .build();

        Team savedTeam = teamRepository.save(team);

        // 팀장을 멤버로 추가
        TeamMember member = TeamMember.builder()
                .team(savedTeam)
                .user(user)
                .role(TeamMember.MemberRole.OWNER)
                .build();

        teamMemberRepository.save(member);

        return TeamResponse.from(savedTeam);
    }

    // 팀 목록 조회 (내가 속한 팀)
    public List<TeamResponse> getMyTeams(Long userId) {
        List<TeamMember> memberships = teamMemberRepository.findByUserUserId(userId);

        return memberships.stream()
                .map(tm -> TeamResponse.from(tm.getTeam()))
                .collect(Collectors.toList());
    }

    // 팀 상세 조회
    public TeamResponse getTeam(Long teamId) {
        Team team = teamRepository.findByIdWithOwner(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        return TeamResponse.from(team);
    }

    // 초대 코드로 팀 가입
    @Transactional
    public void joinTeam(Long userId, String inviteCode) {
        Team team = teamRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITE_CODE));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 멤버인지 확인
        if (teamMemberRepository.existsByTeamAndUser(team, user)) {
            throw new CustomException(ErrorCode.ALREADY_TEAM_MEMBER);
        }

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .role(TeamMember.MemberRole.MEMBER)
                .build();

        teamMemberRepository.save(member);
    }
}
```

### 7.2 트랜잭션 관리

```java
@Service
@Transactional(readOnly = true)  // 클래스 레벨: 기본 읽기 전용
public class TeamService {

    // 조회: readOnly = true (기본값 사용)
    public TeamResponse getTeam(Long teamId) { ... }

    // 생성/수정/삭제: readOnly = false
    @Transactional
    public TeamResponse createTeam(...) { ... }

    @Transactional
    public void updateTeam(...) { ... }

    @Transactional
    public void deleteTeam(...) { ... }
}
```

**트랜잭션 규칙:**
- `readOnly = true`: SELECT만 수행, 성능 최적화
- 쓰기 작업 시 반드시 `@Transactional` (readOnly = false)
- 예외 발생 시 자동 롤백 (RuntimeException)

---

## 8. Controller & DTO

### 8.1 Controller 구조

```java
package com.ssafy.modustudy.controller;

import com.ssafy.modustudy.dto.request.TeamCreateRequest;
import com.ssafy.modustudy.dto.response.ApiResponse;
import com.ssafy.modustudy.dto.response.TeamResponse;
import com.ssafy.modustudy.security.CustomUserDetails;
import com.ssafy.modustudy.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // 팀 생성
    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @AuthenticationPrincipal CustomUserDetails userDetails,  // 로그인 유저 정보
            @Valid @RequestBody TeamCreateRequest request
    ) {
        TeamResponse response = teamService.createTeam(userDetails.getUserId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("팀 생성 성공", response));
    }

    // 내 팀 목록
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getMyTeams(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<TeamResponse> teams = teamService.getMyTeams(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("팀 목록 조회 성공", teams));
    }

    // 팀 상세 조회
    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(
            @PathVariable Long teamId
    ) {
        TeamResponse team = teamService.getTeam(teamId);
        return ResponseEntity.ok(ApiResponse.success("팀 조회 성공", team));
    }

    // 초대 코드로 가입
    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<ApiResponse<Void>> joinTeam(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String inviteCode
    ) {
        teamService.joinTeam(userDetails.getUserId(), inviteCode);
        return ResponseEntity.ok(ApiResponse.success("팀 가입 성공", null));
    }
}
```

### 8.2 DTO (Data Transfer Object)

#### Request DTO

```java
package com.ssafy.modustudy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamCreateRequest {

    @NotBlank(message = "팀 이름은 필수입니다")
    @Size(min = 2, max = 100, message = "팀 이름은 2~100자여야 합니다")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;
}
```

#### Response DTO

```java
package com.ssafy.modustudy.dto.response;

import com.ssafy.modustudy.entity.Team;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamResponse {

    private Long teamId;
    private String name;
    private String description;
    private String inviteCode;
    private Long ownerId;
    private String ownerNickname;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 메서드
    public static TeamResponse from(Team team) {
        return TeamResponse.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .description(team.getDescription())
                .inviteCode(team.getInviteCode())
                .ownerId(team.getOwner().getUserId())
                .ownerNickname(team.getOwner().getNickname())
                .createdAt(team.getCreatedAt())
                .build();
    }
}
```

#### 공통 응답 DTO

```java
package com.ssafy.modustudy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
```

### 8.3 Validation 어노테이션

| 어노테이션 | 설명 |
|-----------|------|
| `@NotBlank` | null, "", " " 모두 불가 (문자열) |
| `@NotNull` | null 불가 |
| `@NotEmpty` | null, "" 불가 (컬렉션, 문자열) |
| `@Size(min, max)` | 길이 제한 |
| `@Email` | 이메일 형식 |
| `@Pattern` | 정규식 패턴 |
| `@Min`, `@Max` | 숫자 범위 |

```java
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{9,16}$",
        message = "비밀번호는 9~16자, 영문+숫자+특수문자 조합이어야 합니다"
    )
    private String password;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다")
    private String nickname;
}
```

---

## 9. 연관관계 매핑

### 9.1 연관관계 종류

| 관계 | 어노테이션 | 예시 |
|------|-----------|------|
| 1:1 | `@OneToOne` | User ↔ UserProfile |
| 1:N | `@OneToMany` | Team → Channel (하나의 팀에 여러 채널) |
| N:1 | `@ManyToOne` | Channel → Team (여러 채널이 하나의 팀에) |
| N:M | `@ManyToMany` | User ↔ Team (중간 테이블 필요) |

### 9.2 N:1 관계 (가장 많이 사용)

```java
@Entity
public class Channel {

    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩
    @JoinColumn(name = "team_id", nullable = false)  // FK 컬럼명
    private Team team;
}
```

**FetchType**:
- `LAZY` (권장): 실제 사용 시점에 쿼리 실행
- `EAGER`: 즉시 조인해서 가져옴 (N+1 문제 유발 가능)

### 9.3 1:N 양방향 관계

```java
// Team (1) ↔ Channel (N)

@Entity
public class Team {
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Channel> channels = new ArrayList<>();
}

@Entity
public class Channel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;  // 연관관계 주인 (FK 관리)
}
```

**mappedBy**: 연관관계 주인이 아님을 표시 (읽기 전용)

### 9.4 N:M 관계 → 중간 테이블로 분리

```java
// User ↔ Team (N:M) → TeamMember 중간 테이블

@Entity
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    public enum MemberRole {
        OWNER, ADMIN, MEMBER
    }
}
```

### 9.5 N+1 문제와 해결

**문제 상황:**
```java
// 팀 목록 조회 (1번 쿼리)
List<Team> teams = teamRepository.findAll();

// 각 팀의 owner 접근 (N번 쿼리 추가 발생!)
for (Team team : teams) {
    System.out.println(team.getOwner().getNickname());
}
// 총 1 + N 번의 쿼리 실행!
```

**해결: Fetch Join**
```java
@Query("SELECT t FROM Team t JOIN FETCH t.owner")
List<Team> findAllWithOwner();
// 1번의 조인 쿼리로 해결!
```

**해결: EntityGraph**
```java
@EntityGraph(attributePaths = {"owner"})
List<Team> findAll();
```

---

## 10. Spring Security + JWT

### 10.1 SecurityConfig

```java
package com.ssafy.modustudy.config;

import com.ssafy.modustudy.security.JwtAuthenticationFilter;
import com.ssafy.modustudy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT 사용)
            .csrf(csrf -> csrf.disable())

            // 세션 사용 안함 (JWT 사용)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 가능
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/ws/**").permitAll()  // WebSocket
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )

            // JWT 필터 추가
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
```

### 10.2 JwtTokenProvider

```java
package com.ssafy.modustudy.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private SecretKey key;

    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // 토큰에서 userId 추출
    public Long getUserId(String token) {
        return Long.parseLong(
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 인증 객체 생성
    public Authentication getAuthentication(String token) {
        Long userId = getUserId(token);
        UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
        return new UsernamePasswordAuthenticationToken(
            userDetails, "", userDetails.getAuthorities()
        );
    }

    // Request에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 10.3 JwtAuthenticationFilter

```java
package com.ssafy.modustudy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 토큰 유효성 검증 & 인증 정보 설정
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
```

### 10.4 CustomUserDetails

```java
package com.ssafy.modustudy.security;

import com.ssafy.modustudy.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    public Long getUserId() {
        return user.getUserId();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();  // 역할 기반 권한이 필요하면 추가
    }

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

### 10.5 AuthService

```java
package com.ssafy.modustudy.service;

import com.ssafy.modustudy.dto.request.LoginRequest;
import com.ssafy.modustudy.dto.request.SignupRequest;
import com.ssafy.modustudy.dto.response.TokenResponse;
import com.ssafy.modustudy.entity.User;
import com.ssafy.modustudy.exception.CustomException;
import com.ssafy.modustudy.exception.ErrorCode;
import com.ssafy.modustudy.repository.UserRepository;
import com.ssafy.modustudy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public void signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .status(User.UserStatus.OFFLINE)
                .build();

        userRepository.save(user);
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
```

---

## 11. 예외 처리

### 11.1 ErrorCode

```java
package com.ssafy.modustudy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 초대 코드입니다"),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),

    // 403 Forbidden
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다"),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다"),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다"),

    // 409 Conflict
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다"),
    ALREADY_TEAM_MEMBER(HttpStatus.CONFLICT, "이미 팀 멤버입니다"),

    // 500 Internal Server Error
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
```

### 11.2 CustomException

```java
package com.ssafy.modustudy.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

### 11.3 GlobalExceptionHandler

```java
package com.ssafy.modustudy.exception;

import com.ssafy.modustudy.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(
                    errorCode.getHttpStatus().value(),
                    errorCode.getMessage()
                ));
    }

    // Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(400, "입력값 검증 실패", errors));
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(500, "서버 오류가 발생했습니다"));
    }
}
```

---

## 12. 자주 하는 실수 & 해결법

### 12.1 LazyInitializationException

**문제**: 트랜잭션 밖에서 지연 로딩된 연관 엔티티 접근

```java
// Service에서 Team 조회 후 Controller에서 owner 접근
Team team = teamService.getTeam(1L);
team.getOwner().getNickname();  // LazyInitializationException!
```

**해결 1**: Fetch Join 사용
```java
@Query("SELECT t FROM Team t JOIN FETCH t.owner WHERE t.teamId = :teamId")
Optional<Team> findByIdWithOwner(@Param("teamId") Long teamId);
```

**해결 2**: DTO로 변환 (권장)
```java
// Service에서 DTO 변환 후 반환
public TeamResponse getTeam(Long teamId) {
    Team team = teamRepository.findByIdWithOwner(teamId).orElseThrow(...);
    return TeamResponse.from(team);  // 트랜잭션 내에서 변환
}
```

### 12.2 N+1 문제

**문제**: 연관 엔티티를 반복 조회

```java
List<Team> teams = teamRepository.findAll();  // 1번 쿼리
for (Team team : teams) {
    team.getOwner().getNickname();  // N번 쿼리 추가!
}
```

**해결**: Fetch Join 또는 EntityGraph
```java
@EntityGraph(attributePaths = {"owner"})
List<Team> findAll();
```

### 12.3 영속성 컨텍스트와 변경 감지

**JPA는 엔티티 변경을 자동 감지 (Dirty Checking)**

```java
@Transactional
public void updateNickname(Long userId, String nickname) {
    User user = userRepository.findById(userId).orElseThrow(...);
    user.updateNickname(nickname);  // setter 호출
    // save() 호출 불필요! 트랜잭션 종료 시 자동 UPDATE
}
```

### 12.4 양방향 연관관계 무한 루프

**문제**: JSON 직렬화 시 순환 참조

```java
// Team → Channel → Team → Channel → ... 무한 루프!
```

**해결 1**: @JsonIgnore
```java
@Entity
public class Channel {
    @ManyToOne
    @JsonIgnore  // 직렬화에서 제외
    private Team team;
}
```

**해결 2**: DTO 사용 (권장)
```java
// Entity를 직접 반환하지 않고 DTO로 변환
return TeamResponse.from(team);
```

### 12.5 save() 반환값 사용

**문제**: save() 후 원본 객체 사용

```java
Team team = Team.builder().name("스터디").build();
teamRepository.save(team);
System.out.println(team.getTeamId());  // null일 수 있음!
```

**해결**: save() 반환값 사용
```java
Team team = Team.builder().name("스터디").build();
Team savedTeam = teamRepository.save(team);  // 반환값 사용!
System.out.println(savedTeam.getTeamId());  // ID 있음
```

### 12.6 @Transactional 누락

**문제**: 쓰기 작업에 트랜잭션 없음

```java
// @Transactional 없으면 변경 감지 안됨!
public void updateUser(Long userId, String nickname) {
    User user = userRepository.findById(userId).orElseThrow(...);
    user.updateNickname(nickname);
    // 변경이 DB에 반영 안됨!
}
```

**해결**: @Transactional 추가
```java
@Transactional  // 필수!
public void updateUser(Long userId, String nickname) {
    User user = userRepository.findById(userId).orElseThrow(...);
    user.updateNickname(nickname);
    // 트랜잭션 종료 시 자동 UPDATE
}
```

---

## 부록: 유용한 팁

### A. 로깅 설정 (SQL 파라미터 확인)

```yaml
# application.yml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE  # 파라미터 값 출력
```

### B. 테스트 시 H2 인메모리 DB 사용

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### C. Querydsl 사용 (복잡한 동적 쿼리)

```java
// 복잡한 검색 조건
public List<Team> searchTeams(String keyword, TeamStatus status) {
    return queryFactory
        .selectFrom(team)
        .where(
            containsKeyword(keyword),
            eqStatus(status)
        )
        .fetch();
}

private BooleanExpression containsKeyword(String keyword) {
    return keyword != null ? team.name.contains(keyword) : null;
}
```

---

## 참고 자료

- [Spring Boot 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA 공식 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPA 쿼리 메서드 키워드](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)
- [Hibernate 공식 문서](https://hibernate.org/orm/documentation/)

---
