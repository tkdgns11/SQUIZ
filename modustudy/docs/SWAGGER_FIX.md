최초 작성자 : 윤상훈(0107)
최종 수정자 : 윤상훈(0107) 변경사유 :  

# Swagger UI 접속 오류 해결

## 문제 현상

`http://localhost:8080/swagger-ui.html` 접속 시 404 에러 발생

## 원인 분석

### 1. 정적 리소스 매핑 비활성화

`application.properties`에서 SPA 지원을 위해 정적 리소스 자동 매핑이 비활성화되어 있음:

```properties
spring.web.resources.add-mappings=false
```

이로 인해 springdoc-openapi가 자동으로 등록하는 Swagger UI 리소스 핸들러가 작동하지 않음.

### 2. NotFoundHandler의 잘못된 404 처리

`NotFoundHandler.java`에서 `/swagger`로 시작하는 모든 요청을 404로 반환하도록 설정되어 있음:

```java
// NotFoundHandler.java:50-52
if(url.startsWith("/swagger") ||
   url.startsWith("/actuator") ||
   url.startsWith("/webjars/") ||
   ...
```

**동작 흐름:**
1. `/swagger-ui.html` 요청 들어옴
2. `spring.web.resources.add-mappings=false`로 인해 정적 리소스 핸들러 없음
3. `NoHandlerFoundException` 발생
4. `NotFoundHandler`가 예외를 catch
5. URL이 `/swagger`로 시작하므로 404 반환

### 3. webjars 리소스 경로 오류

`WebMvcConfig.java`에서 webjars 리소스 경로가 잘못 설정되어 있음:

```java
// 잘못된 설정
registry.addResourceHandler("/webjars/**")
        .addResourceLocations("/webjars/");

// 올바른 설정
registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
```

## 해결 방법

### 1. WebMvcConfig.java 수정

Swagger UI 리소스 핸들러를 명시적으로 추가:

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // ... 기존 코드 ...

    // Swagger UI 및 webjars 리소스 핸들러 추가
    registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");

    // ... 기존 코드 ...
}
```

### 2. NotFoundHandler.java 수정

Swagger 관련 경로를 404 처리 목록에서 제거:

```java
// 변경 전
if(url.startsWith("/assets/") ||
   url.startsWith("/css/") ||
   // ...
   url.startsWith("/upload/") ||
   url.startsWith("/swagger") ||    // 삭제
   url.startsWith("/actuator") ||   // 삭제
   url.startsWith("/webjars/") ||   // 삭제
   url.equals("/favicon.ico")) {

// 변경 후
if(url.startsWith("/assets/") ||
   url.startsWith("/css/") ||
   // ...
   url.startsWith("/upload/") ||
   url.equals("/favicon.ico")) {
```

## 수정 후 접속 URL

| 용도 | URL |
|------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/api-docs` |
| OpenAPI YAML | `http://localhost:8080/api-docs.yaml` |

> **참고**: springdoc-openapi v2.x에서는 `/swagger-ui.html` 접속 시 자동으로 `/swagger-ui/index.html`로 리다이렉트됩니다.

## 관련 설정 파일

- `src/main/resources/application.properties` - springdoc 설정
- `src/main/java/com/ssafy/config/WebMvcConfig.java` - 리소스 핸들러
- `src/main/java/com/ssafy/config/SwaggerConfig.java` - OpenAPI 설정
- `src/main/java/com/ssafy/config/SecurityConfig.java` - 보안 설정 (Swagger 경로 허용)
- `src/main/java/com/ssafy/common/exception/handler/NotFoundHandler.java` - SPA 404 처리
