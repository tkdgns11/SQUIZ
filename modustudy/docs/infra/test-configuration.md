# 테스트 환경 설정 가이드

## 개요

CI/CD 파이프라인에서 테스트를 MySQL 데이터베이스로 실행하기 위한 설정입니다.
H2 인메모리 DB 대신 실제 MySQL을 사용하여 운영 환경과 동일한 조건에서 테스트합니다.

## 변경 사항

### 1. 테스트용 application.properties 생성

**파일 위치**: `src/test/resources/application.properties`

```properties
# Database 설정 (환경변수로 오버라이드 가능)
spring.datasource.url=${TEST_DB_URL:jdbc:mysql://localhost:3306/ssafy_test_db?...}
spring.datasource.username=${TEST_DB_USERNAME:ssafy}
spring.datasource.password=${TEST_DB_PASSWORD:ssafy}

# JPA - 테스트 시 테이블 자동 생성
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=create
```

### 2. 테스트 클래스 Security 설정

Spring Security 인증을 우회하기 위해 테스트 클래스에 `@WithMockUser` 추가:

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testuser", roles = {"USER"})
class StudyControllerTest {
    // ...
}
```

**적용된 테스트 클래스**:
- `StudyControllerTest`
- `ApplicationControllerTest`
- `LeaderControllerTest`

### 3. 삭제된 파일

- `src/test/resources/application-test.properties` - 프로필 충돌 방지를 위해 삭제

## 환경별 설정

### 로컬 개발 환경

기본값 사용 (별도 설정 불필요):
- DB URL: `localhost:3306/ssafy_test_db`
- Username: `ssafy`
- Password: `ssafy`

**테스트 DB 생성**:
```bash
mysql -u ssafy -pssafy -e "CREATE DATABASE IF NOT EXISTS ssafy_test_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

**테스트 실행**:
```bash
./gradlew test
```

### EC2 CI 환경

`/etc/environment` 또는 GitLab CI 변수에 설정:

```bash
# /etc/environment
TEST_DB_URL=jdbc:mysql://localhost:3306/squiz_test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false
TEST_DB_USERNAME=ssafy
TEST_DB_PASSWORD=ssafy
```

**테스트 DB 생성** (EC2 MySQL 컨테이너):
```bash
docker exec -it mysql mysql -u ssafy -pssafy -e "CREATE DATABASE IF NOT EXISTS squiz_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

## 트러블슈팅

### 1. 테이블 생성 안 됨

**증상**: `Table 'xxx' doesn't exist` 에러

**해결**:
- `spring.jpa.hibernate.ddl-auto=create` 확인
- `spring.jpa.generate-ddl=true` 확인
- MySQL dialect 자동 감지 (명시적 설정 불필요)

### 2. 403 Forbidden 에러

**증상**: 테스트에서 API 호출 시 403 에러

**해결**:
- 테스트 클래스에 `@WithMockUser` 추가
- import: `org.springframework.security.test.context.support.WithMockUser`

### 3. 환경변수 인식 안 됨

**증상**: `PropertyPlaceholderHelper` 에러

**해결**:
- 환경변수에 기본값 설정: `${VAR_NAME:defaultValue}`
- build.gradle에 `spring-dotenv` 의존성 추가

## 의존성

```groovy
// build.gradle
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'
testImplementation 'me.paulschwarz:spring-dotenv:4.0.0'
```
