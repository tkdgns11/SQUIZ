# Gradle Config Diff (backend vs docs/infra)

Scope:
- backend/build.gradle
- backend/gradle/wrapper/gradle-wrapper.properties
- backend/settings.gradle
- docs/infra/04-gradle-toolchain.md
- docs/infra/03-testing-guide.md (test dependency guidance)

Matches:
- Gradle Wrapper: 8.5 (`gradle-8.5-bin.zip`)
- Spring Boot plugin: 3.2.2
- Dependency management plugin: 1.1.4
- Java toolchain: 21
- Foojay toolchains resolver: 0.8.0

Differences:
- gradle.properties: not present in backend (no guidance in docs, but noted as a gap)
- Test deps:
  - backend includes H2 (`testImplementation` + `testRuntimeOnly`)
  - docs/testing guide suggests AssertJ + TestContainers (not present in backend)
- Extra plugins/deps in backend (not mentioned in docs):
  - co.uzzu.dotenv.gradle
  - spring-dotenv
  - jackson-databind
  - spring-boot-starter-oauth2-client
