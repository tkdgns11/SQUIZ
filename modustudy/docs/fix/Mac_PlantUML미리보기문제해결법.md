최초 작성자 : 박지원(0110)
최종 수정자 : 박지원(0110) 변경 사유 : 
# PlantUML 미리보기 문제 해결 가이드

## 문제 상황

macOS 환경에서 VS Code로 PlantUML 파일(`.puml`)을 작성할 때, 미리보기가 제대로 표시되지 않는 문제가 발생했습니다.

- 간단한 테스트 코드는 미리보기가 되지만
- 복잡한 상태 다이어그램(state diagram)은 미리보기가 안 됨
- PlantUML Preview 창에 오류 아이콘만 표시됨

## 원인

PlantUML이 복잡한 다이어그램을 렌더링할 때 필요한 **Graphviz**가 시스템에 설치되어 있지 않았기 때문입니다.

### 왜 Graphviz가 필요한가?

- PlantUML은 다이어그램 레이아웃을 계산하기 위해 Graphviz 라이브러리를 사용합니다
- 특히 상태 다이어그램, 액티비티 다이어그램 등 복잡한 구조는 Graphviz 없이 렌더링이 불가능합니다
- VS Code 확장 프로그램만으로는 Graphviz가 포함되지 않습니다

### 왜 Windows에서는 확장만으로 작동했을까?

대부분의 PlantUML VS Code 확장은 기본적으로 **온라인 서버 렌더링** 방식을 사용합니다:

**온라인 서버 렌더링 (기본값)**
- PlantUML 코드를 공식 서버(`http://www.plantuml.com/plantuml`)에 전송
- 서버에서 렌더링된 이미지를 받아서 표시
- Graphviz가 로컬에 없어도 작동 (서버에 이미 설치되어 있음)
- **Windows에서는 이 방식이 정상 작동**

**macOS에서 문제가 발생한 이유**
- 네트워크 설정, 방화벽, 또는 회사 프록시 등으로 온라인 서버 접근이 제한되었을 가능성
- VS Code 확장 설정이 로컬 렌더링으로 되어 있었을 가능성
- macOS의 보안 정책으로 인한 네트워크 요청 차단

**로컬 렌더링의 장점**
- 인터넷 연결 없이도 작동
- 렌더링 속도가 빠름
- 회사 기밀 정보가 포함된 다이어그램도 안전하게 사용 가능
- 따라서 **macOS에서도 로컬 렌더링 환경 구축을 권장**

## 해결 방법

### 1. Homebrew로 필요한 패키지 설치

터미널에서 다음 명령어를 실행합니다:

```bash
# Graphviz 설치 (필수)
brew install graphviz

# PlantUML 설치 (선택사항, 로컬 렌더링 성능 향상)
brew install plantuml
```

### 2. VS Code 재시작

설치 후 VS Code를 완전히 종료하고 다시 시작합니다.

### 3. 설치 확인

터미널에서 Graphviz가 제대로 설치되었는지 확인합니다:

```bash
dot -V
```

PlantUML과 Graphviz 연결 상태 확인:

```bash
plantuml -testdot
```

## 추가 팁

### 캐시 문제가 있을 경우

만약 설치 후에도 이전 파일들의 미리보기가 안 된다면:

1. **VS Code 명령 팔레트** 열기: `Cmd + Shift + P` (Mac) / `Ctrl + Shift + P` (Windows)
2. `PlantUML: Clear Cache` 실행
3. 미리보기 창 닫고 다시 열기

### VS Code PlantUML 확장 설정 확인

VS Code에서 렌더링 방식을 확인하고 변경할 수 있습니다:

**설정 확인 방법:**
1. VS Code 설정 열기: `Cmd + ,` (Mac) / `Ctrl + ,` (Windows)
2. "plantuml render" 검색
3. 다음 옵션 중 하나 선택:
   - **PlantUMLServer**: 온라인 서버 사용 (인터넷 필요)
   - **Local**: 로컬 렌더링 (Graphviz + PlantUML 설치 필요)

`settings.json`에서 직접 설정:

```json
{
  // 로컬 렌더링 사용 (권장)
  "plantuml.render": "Local",
  
  // 또는 온라인 서버 사용
  // "plantuml.render": "PlantUMLServer",
  // "plantuml.server": "https://www.plantuml.com/plantuml"
}
```

**권장 설정:**
- 개인 프로젝트: 어느 방식이든 상관없음
- 회사/팀 프로젝트: **로컬 렌더링** 권장 (보안상 안전)
- 인터넷 없는 환경: **로컬 렌더링** 필수

## 참고사항

- 간단한 클래스 다이어그램은 Graphviz 없이도 렌더링될 수 있습니다
- 하지만 프로젝트에서 다양한 다이어그램을 사용할 예정이라면 처음부터 Graphviz를 설치하는 것을 권장합니다
- Windows 사용자는 Homebrew 대신 [Graphviz 공식 사이트](https://graphviz.org/download/)에서 설치 파일을 다운로드하거나 `choco install graphviz`를 사용할 수 있습니다

## 문제 해결 체크리스트

- [ ] Graphviz 설치 (`brew install graphviz`)
- [ ] VS Code 재시작
- [ ] 설치 확인 (`dot -V`)
- [ ] 캐시 클리어 (필요시)
- [ ] 미리보기 재시도


## 참고 블로그

https://unclean.tistory.com/57