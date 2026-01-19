# Gradle Toolchain 설정 가이드

## 1. 개요

Gradle Toolchain은 빌드 서버(Jenkins 등)에 특정 Java 버전이 설치되어 있지 않아도 Gradle이 자동으로 필요한 JDK를 다운로드하여 사용하는 기능입니다.

### 왜 Toolchain을 사용하나?

| 기존 방식 | Toolchain 방식 |
|----------|---------------|
| 서버에 Java 21 설치 필요 | 서버 환경 무관 |
| 관리자 권한 필요 | 권한 불필요 |
| 다른 프로젝트에 영향 가능 | 프로젝트별 독립적 |

## 2. 설정 방법

### 2.1 build.gradle 설정

```gradle
// 기존 방식 (서버에 Java 21 필요)
java {
    sourceCompatibility = '21'
}

// Toolchain 방식 (자동 다운로드)
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

### 2.2 현재 프로젝트 설정

`modustudy/backend/build.gradle`:

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.2'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'co.uzzu.dotenv.gradle' version '2.0.0'
}

group = 'com.ssafy'
version = '1.0-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

## 3. 동작 원리

```
빌드 시작
    ↓
Gradle이 Java 21 필요 확인
    ↓
로컬에 Java 21 있음? ──Yes──→ 해당 JDK 사용
    │
    No
    ↓
자동으로 Java 21 다운로드 (Adoptium/Eclipse Temurin)
    ↓
다운로드된 JDK로 빌드 실행
    ↓
다음 빌드부터는 캐시된 JDK 사용
```

## 4. JDK 다운로드 위치

Gradle이 자동 다운로드한 JDK는 다음 위치에 캐시됩니다:

| OS | 경로 |
|----|------|
| Linux | `~/.gradle/jdks/` |
| macOS | `~/.gradle/jdks/` |
| Windows | `%USERPROFILE%\.gradle\jdks\` |

## 5. 벤더 지정 (선택)

특정 JDK 벤더를 지정할 수도 있습니다:

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM  // Eclipse Temurin
    }
}
```

사용 가능한 벤더:
- `ADOPTIUM` (Eclipse Temurin) - 기본값
- `AMAZON` (Amazon Corretto)
- `AZUL` (Azul Zulu)
- `ORACLE`
- `SAP`
- `IBM`

## 6. 로컬 개발 환경

### 로컬에 Java 21이 있는 경우
- Gradle이 로컬 JDK를 자동 감지하여 사용
- 추가 다운로드 없음

### 로컬에 Java 21이 없는 경우
- 첫 빌드 시 자동 다운로드 (약 200MB)
- 이후 빌드에서는 캐시 사용

## 7. Jenkins CI/CD에서의 동작

Jenkins에서 `./gradlew test` 또는 `./gradlew build` 실행 시:

1. Jenkins 서버에 Java 21이 없어도 빌드 가능
2. 첫 빌드 시 JDK 다운로드로 시간이 조금 더 걸림
3. 이후 빌드는 캐시된 JDK 사용으로 빠름

### Jenkinsfile 예시

```groovy
stage('Backend Test') {
    steps {
        dir('modustudy/backend') {
            sh '''
                chmod +x gradlew
                ./gradlew test --no-daemon
            '''
            // Java 버전 지정 불필요 - Toolchain이 자동 처리
        }
    }
}
```

## 8. 트러블슈팅

### 8.1 다운로드 실패 시

네트워크 문제로 JDK 다운로드 실패 시:

```bash
# 수동으로 Gradle 캐시 정리
rm -rf ~/.gradle/jdks/

# 다시 빌드
./gradlew build
```

### 8.2 특정 JDK 강제 사용

로컬에 설치된 특정 JDK를 강제로 사용하려면:

```bash
./gradlew build -Porg.gradle.java.installations.paths=/path/to/jdk21
```

### 8.3 Toolchain 정보 확인

현재 사용 중인 Toolchain 정보 확인:

```bash
./gradlew -q javaToolchains
```

## 9. 참고 자료

- [Gradle Toolchains 공식 문서](https://docs.gradle.org/current/userguide/toolchains.html)
- [Eclipse Temurin (Adoptium)](https://adoptium.net/)
