# OpenPDF 적용 안내

## OpenPDF란?
OpenPDF는 Java에서 PDF 문서를 생성/편집할 수 있는 오픈소스 라이브러리입니다. iText(초기 버전) 계열 API와 유사한 방식으로 문서를 작성할 수 있어 기존 예제를 활용하기 쉽고, 비교적 간단한 문서 출력에 적합합니다.

## 이 프로젝트에 적용한 내용

### 1) 의존성 추가
- 위치: `backend/build.gradle`
- 추가 항목:
```
implementation 'com.github.librepdf:openpdf:1.3.39'
```

### 2) PDF 내보내기 서비스 구현
- 위치: `backend/src/main/java/com/ssafy/domain/meeting/service/MeetingService.java`
- 주요 흐름:
  1. `exportMeetingMarkdown(...)`로 미팅 요약/참가자 요약/액션아이템/트랜스크립트를 Markdown 문자열로 구성
  2. `exportMeetingPdf(...)`에서 OpenPDF의 `Document`와 `PdfWriter`로 PDF 바이트 생성
  3. Markdown 라인 단위로 제목/섹션/본문을 구분해 `Paragraph`로 렌더링

### 3) API 응답 처리
- 위치: `backend/src/main/java/com/ssafy/domain/meeting/controller/MeetingController.java`
- 엔드포인트:
  - Markdown: `GET /api/v1/studies/{studyId}/meetings/{meetingId}/export?format=MARKDOWN`
  - PDF: `GET /api/v1/studies/{studyId}/meetings/{meetingId}/export?format=PDF`
- 응답 헤더:
  - Markdown: `Content-Type: text/markdown`, `Content-Disposition: attachment; filename=meeting-{id}.md`
  - PDF: `Content-Type: application/pdf`, `Content-Disposition: attachment; filename=meeting-{id}.pdf`

### 4) 한글 폰트 설정
- 위치: `backend/src/main/resources/application.properties`
- 추가 설정:
```
meeting.pdf.font-path=C:/Windows/Fonts/malgun.ttf
```
- 이유: 기본 폰트(Helvetica)는 한글이 깨질 수 있어, 한글 지원 폰트를 지정하도록 구성

## 현재 사용 방법
1) 미팅 종료 후 요약/액션아이템/참가자 요약이 저장된 상태에서,
2) 내보내기 API 호출

예시:
- Markdown
```
GET /api/v1/studies/1/meetings/10/export?format=MARKDOWN
```
- PDF
```
GET /api/v1/studies/1/meetings/10/export?format=PDF
```

## 앞으로 추가하면 좋은 개선사항

### 1) 폰트 리소스 내장
- 현재는 OS 경로를 직접 지정
- 개선: `src/main/resources/fonts/`에 폰트 파일 포함 후 classpath 로드
- 장점: 배포 환경(OS)에 영향받지 않음

### 2) Markdown 스타일 정밀 렌더링
- 현재는 라인 단위 텍스트 렌더링
- 개선: 마크다운 파서(예: flexmark) + HTML 변환 후 PDF 렌더링
- 장점: 헤더, 리스트, 굵게/기울임 등의 스타일 보존

### 3) PDF 템플릿/브랜딩
- 로고/헤더/푸터/페이지 번호 등 추가
- 미팅 메타데이터(스터디명, 회의 유형) 강조 가능

### 4) Export 기록 저장
- PDF/Markdown 파일을 저장소(S3 등)에 업로드 후
- `meeting_export` 같은 테이블로 다운로드 URL 관리

### 5) 접근 제어/감사 로그
- 누가 어떤 미팅을 언제 내보냈는지 기록
- 민감한 회의 데이터의 접근 이력 관리 가능
