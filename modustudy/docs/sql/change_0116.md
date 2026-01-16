# ERD.sql 변경 요약 (공유용)

본 문서는 최초 `ERD.sql` 대비 변경된 스키마를 팀 공유 목적으로 정리한 문서입니다.  
기준: `docs/sql/ERD.sql` (최신 반영본)

## 변경 배경
- `ModuStudy.sql`에만 있던 테이블을 ERD 기준에 맞게 통합
- `erd_migration_sql.sql`의 "스키마 변경사항"을 ERD.sql에 흡수
- 초기 데이터(INSERT)는 포함하지 않음

## 변경 요약
### 1) 사용자/인증 관련 추가
- `profile` 테이블 추가
  - 목적: 사용자 프로필 상세 정보 저장
  - 주요 컬럼 의미:
    - `user_id`: 사용자 FK (1:1 관계, UNIQUE)
    - `profile_image_url`: 프로필 이미지 URL
    - `profile_image_source`: 이미지 출처(KAKAO/GOOGLE/NAVER/UPLOAD)
    - `bio`: 자기소개
    - `social_links`, `tech`, `favorite`: JSON 형태의 링크/기술/선호 저장
- `refresh_token` 테이블 추가
  - 목적: 로그인 세션 유지(리프레시 토큰 관리)
  - 주요 컬럼 의미:
    - `user_id`: 사용자 FK
    - `token`: 리프레시 토큰 값(UNIQUE)
    - `expires_at`: 만료 시각
    - `is_revoked`: 폐기 여부

### 2) 게이미피케이션/리워드 관련 추가
- `level_config` 테이블 추가
  - 목적: 레벨별 요구 경험치/메타데이터 관리
  - 주요 컬럼 의미:
    - `level`: 레벨 번호(UNIQUE)
    - `required_exp`: 해당 레벨 필요 경험치
    - `level_icon_url`, `level_color`: UI 표시용 메타
- `reward_policy` 테이블 추가
  - 목적: 행동별 보상 정책 정의
  - 주요 컬럼 의미:
    - `action_type`: 보상 대상 행동 타입
    - `exp_amount`, `point_amount`: 지급 경험치/포인트
    - `daily_limit`: 일일 제한
- `exp_transaction` 테이블 추가
  - 목적: 경험치 증감 이력 관리
  - 주요 컬럼 의미:
    - `user_id`: 대상 사용자
    - `exp_type`: 사유(행동 타입)
    - `reference_type`, `reference_id`: 연관 엔티티 타입/ID
    - `balance_after`: 처리 후 잔액
- `point_transaction` 테이블 추가
  - 목적: 포인트 증감 이력 관리
  - 주요 컬럼 의미:
    - `transaction_type`: EARN/SPEND
    - `point_type`: 사유(행동 타입)
    - `reference_type`, `reference_id`: 연관 엔티티 타입/ID
    - `balance_after`: 처리 후 잔액
- `hint_cost` 테이블 추가
  - 목적: 힌트/아이템 비용 정책 관리
  - 주요 컬럼 의미:
    - `hint_type`: 힌트 타입
    - `cost_points`: 소모 포인트
    - `is_active`: 활성 여부

### 3) 퀴즈 기능 확장 (erd_migration_sql 반영)
#### 3-1. 신규 테이블 추가
- `quiz_category`
  - 목적: 퀴즈 카테고리 계층 구조
  - `parent_id`로 트리 구조 구성
- `quiz_question_pool`
  - 목적: 공용 문제 저장소 (코스/대회 공통)
  - `category_id`: 카테고리 FK
  - `created_by`: 문제 출제자 FK
  - `options`: 객관식 보기 객체 배열(JSON)
  - `correct_answer`: 정답 보기 번호 배열(JSON, 필수)
  - `question_type`: 객관식 단일/복수 정답 및 주관식 구분
- `quiz_question_pool_option`
  - 목적: 객관식 보기 및 정답 여부 저장
- `quiz_contest_state`
  - 목적: 실시간 대회 진행 상태 저장
- `quiz_contest_chat`
  - 목적: 대회 채팅 기록
  - `participant_id`: 참가자 FK
- `quiz_practice_stats`
  - 목적: 사용자별 카테고리 학습 통계
- `quiz_practice_record`
  - 목적: 연습 세션 단위 기록
- `quiz_practice_answer`
  - 목적: 연습 문제 단위 답안 기록

#### 3-2. 기존 테이블 컬럼 확장/보강
- `quiz_contest`
  - `category_id`: 대회 카테고리 (선택)
  - `contest_type`: PUBLIC/STUDY
  - `study_id`: 스터디 전용 대회 연결
- `quiz_question`
  - `question_pool_id`: 공용 문제 풀 참조
- `quiz_participant`
  - `last_answer_time`: 동점자 처리용
  - 인덱스 추가: `idx_contest_score`, `idx_participant_user`
- `quiz_answer`
  - `user_answer`: 사용자 선택 보기 번호 배열(JSON)
  - `time_taken_seconds`: 답변 소요 시간
- `user_badge`
  - `badge_type`, `reference_type`, `reference_id`, `rank` 추가
  - 인덱스 추가: `idx_user_badge_type`

## 외래키/연관관계 요약
### 사용자 관련
- `profile.user_id` -> `user.id` (1:1)
- `refresh_token.user_id` -> `user.id`
- `exp_transaction.user_id` -> `user.id`
- `point_transaction.user_id` -> `user.id`

### 퀴즈 관련
- `quiz_category.parent_id` -> `quiz_category.id`
- `quiz_question_pool.category_id` -> `quiz_category.id`
- `quiz_question_pool.created_by` -> `user.id`
- `quiz_question_pool_option.question_pool_id` -> `quiz_question_pool.id`
- `quiz_contest_state.contest_id` -> `quiz_contest.id`
- `quiz_contest_state.current_question_pool_id` -> `quiz_question_pool.id`
- `quiz_contest_chat.contest_id` -> `quiz_contest.id`
- `quiz_contest_chat.user_id` -> `user.id`
- `quiz_contest_chat.participant_id` -> `quiz_participant.id`
- `quiz_practice_stats.user_id` -> `user.id`
- `quiz_practice_stats.category_id` -> `quiz_category.id`
- `quiz_practice_record.user_id` -> `user.id`
- `quiz_practice_record.category_id` -> `quiz_category.id`
- `quiz_practice_answer.practice_record_id` -> `quiz_practice_record.id`
- `quiz_practice_answer.question_pool_id` -> `quiz_question_pool.id`
- `quiz_contest.category_id` -> `quiz_category.id`
- `quiz_contest.study_id` -> `study.id`
- `quiz_question.question_pool_id` -> `quiz_question_pool.id`

## 참고
- 초기 데이터(INSERT)는 ERD.sql에 포함하지 않았습니다.
- 실제 운영 반영 시, 필요하면 `erd_migration_sql.sql`의 초기 데이터 섹션을 별도 실행합니다.
