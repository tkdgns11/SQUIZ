-- =============================================
-- ModuStudy ERD (DDL)
-- ERDCloud import용
-- =============================================

-- =============================================
-- 1. 사용자/인증
-- =============================================

CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255),                      -- 비밀번호 (nullable, 소셜만 사용 시 NULL)
    `name` VARCHAR(50),
    `nickname` VARCHAR(50) NOT NULL UNIQUE,
    `profile_image` VARCHAR(500),
    `role` ENUM('USER', 'ADMIN') DEFAULT 'USER',
    `is_active` BOOLEAN DEFAULT TRUE,
    `is_online` BOOLEAN DEFAULT FALSE,            -- 접속 상태
    `last_seen_at` TIMESTAMP,                     -- 마지막 접속 시간
    `is_searchable` BOOLEAN DEFAULT TRUE,         -- 친구 검색 허용 여부
    `leader_rating` FLOAT DEFAULT 0.0,            -- 스터디장 평점 (캐싱)
    `leader_review_count` INT DEFAULT 0,          -- 스터디장 평가 수 (캐싱)
    `last_login_at` TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 사용자 소셜 계정 연동
CREATE TABLE `user_social_account` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `provider` ENUM('GOOGLE', 'KAKAO', 'NAVER') NOT NULL,
    `provider_user_id` VARCHAR(100) NOT NULL,     -- 소셜 서비스에서의 사용자 ID
    `email` VARCHAR(100),                         -- 소셜 계정 이메일
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_provider_user` (`provider`, `provider_user_id`)
);

-- 로그인 이력
CREATE TABLE `login_history` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `provider` ENUM('GOOGLE', 'KAKAO', 'NAVER', 'EMAIL') NOT NULL,  -- 로그인 방식
    `login_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `ip_address` VARCHAR(45),
    `device_info` VARCHAR(200),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

CREATE TABLE `user_schedule` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `day_of_week` ENUM('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN') NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `is_available` BOOLEAN DEFAULT TRUE,         -- TRUE: 가능, FALSE: 불가능
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- 사용자 소속 인증 (매핑 테이블)
CREATE TABLE `user_organization` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `org_type` VARCHAR(50) NOT NULL,             -- SSAFY, NBC, WTC, UNIVERSITY 등
    `org_data` JSON NOT NULL,                    -- 타입별 상세 정보 (기수, 캠퍼스, retireYn 등)
    `verified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 최초 인증 시점
    `last_checked_at` TIMESTAMP,                 -- 마지막 상태 체크 시점 (스터디 개설/가입 시)
    `is_active` BOOLEAN DEFAULT TRUE,            -- 인증 유효 여부 (퇴소 시 FALSE)
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- =============================================
-- 1-1. 친구/DM
-- =============================================

-- 친구 관계
CREATE TABLE `friendship` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `requester_id` BIGINT NOT NULL,              -- 친구 요청자
    `addressee_id` BIGINT NOT NULL,              -- 친구 요청 대상
    `status` ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'BLOCKED') DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`requester_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`addressee_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_friendship` (`requester_id`, `addressee_id`)
);

-- DM (1:1 메시지)
CREATE TABLE `direct_message` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `sender_id` BIGINT NOT NULL,
    `receiver_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `message_type` ENUM('TEXT', 'IMAGE', 'FILE') DEFAULT 'TEXT',
    `file_url` VARCHAR(500),
    `is_read` BOOLEAN DEFAULT FALSE,
    `is_deleted_by_sender` BOOLEAN DEFAULT FALSE,
    `is_deleted_by_receiver` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`sender_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`receiver_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- =============================================
-- 2. 스터디
-- =============================================

-- 지역 테이블
CREATE TABLE `region` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `code` VARCHAR(20) NOT NULL UNIQUE,          -- SEOUL, BUSAN 등
    `name` VARCHAR(50) NOT NULL,                 -- 서울, 부산 등
    `sort_order` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 지역 초기 데이터
-- INSERT INTO region (code, name, sort_order) VALUES
-- ('SEOUL', '서울', 1), ('BUSAN', '부산', 2), ('DAEGU', '대구', 3),
-- ('INCHEON', '인천', 4), ('GWANGJU', '광주', 5), ('DAEJEON', '대전', 6),
-- ('ULSAN', '울산', 7), ('SEJONG', '세종', 8), ('GYEONGGI', '경기', 9),
-- ('GANGWON', '강원', 10), ('CHUNGBUK', '충북', 11), ('CHUNGNAM', '충남', 12),
-- ('JEONBUK', '전북', 13), ('JEONNAM', '전남', 14), ('GYEONGBUK', '경북', 15),
-- ('GYEONGNAM', '경남', 16), ('JEJU', '제주', 17);

CREATE TABLE `study` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `leader_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `topic` VARCHAR(50) NOT NULL,                -- 알고리즘/CS/자격증/프로젝트 등
    `format` VARCHAR(50),                        -- 문제풀이/독서/강의수강/프로젝트
    `study_type` ENUM('PLANNED', 'LIGHTNING') NOT NULL,  -- 계획/번개
    `meeting_type` ENUM('ONLINE', 'OFFLINE', 'HYBRID') DEFAULT 'ONLINE',  -- 진행 방식
    `region_id` BIGINT,                          -- 오프라인/혼합 시 지역
    `location_detail` VARCHAR(200),              -- 상세 장소 (ex: 강남역 근처)
    `schedule_summary` VARCHAR(100),             -- 일정 요약 (ex: 매주 월/수 19:00)
    `schedule_days` VARCHAR(50),                 -- 요일 (ex: MON,WED)
    `schedule_time` TIME,                        -- 시작 시간
    `max_members` INT DEFAULT 10,
    `is_public` BOOLEAN DEFAULT TRUE,            -- 공개/비공개
    `status` ENUM('DRAFT', 'SCHEDULED', 'RECRUITING', 'RECRUIT_CLOSED', 'PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'DRAFT',
    `penalty_policy` ENUM('STRICT', 'NORMAL', 'LENIENT', 'RATIO', 'NONE') DEFAULT 'NORMAL',
    `start_date` DATE,
    `end_date` DATE,
    `total_sessions` INT,                        -- 총 회차
    `recruit_start_date` DATE,
    `recruit_end_date` DATE,
    `extension_count` INT DEFAULT 0,             -- 모집 연장 횟수 (최대 1회)
    `textbook` VARCHAR(500),                     -- 사용 교재/자료
    `goal` VARCHAR(500),                         -- 스터디 목표
    `difficulty` ENUM('BEGINNER', 'ELEMENTARY', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'INTERMEDIATE',  -- 난이도
    `prerequisites` TEXT,                        -- 사전 지식
    `process_detail` TEXT,                       -- 진행 방식 상세
    `target_org_type` VARCHAR(50),               -- 대상 소속 타입 (SSAFY, NBC, WTC 등, NULL이면 누구나)
    `target_org_criteria` JSON,                  -- 대상 조건 ({"generation": 14} 등, NULL이면 해당 소속 전체)
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`leader_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`region_id`) REFERENCES `region`(`id`)
);

-- 스터디 모집글 템플릿 (사용자 저장용)
CREATE TABLE `study_template` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT,                            -- NULL이면 시스템 템플릿
    `name` VARCHAR(100) NOT NULL,                -- 템플릿 이름
    `is_system` BOOLEAN DEFAULT FALSE,           -- 시스템 기본 템플릿 여부
    `template_type` VARCHAR(50),                 -- ALGORITHM, CS, INTERVIEW, PROJECT, CERTIFICATE, READING
    `topic` VARCHAR(50),
    `format` VARCHAR(50),
    `meeting_type` ENUM('ONLINE', 'OFFLINE', 'HYBRID'),
    `description` TEXT,
    `textbook` VARCHAR(500),
    `goal` VARCHAR(500),
    `difficulty` ENUM('BEGINNER', 'ELEMENTARY', 'INTERMEDIATE', 'ADVANCED'),
    `prerequisites` TEXT,
    `process_detail` TEXT,
    `penalty_policy` ENUM('STRICT', 'NORMAL', 'LENIENT', 'RATIO', 'NONE'),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- 스터디 모집글 댓글
CREATE TABLE `study_comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `parent_id` BIGINT,                          -- 대댓글인 경우 부모 댓글 ID
    `content` TEXT NOT NULL,
    `image_url` VARCHAR(500),                    -- 댓글 첨부 이미지 URL
    `is_deleted` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`parent_id`) REFERENCES `study_comment`(`id`) ON DELETE CASCADE
);

CREATE TABLE `study_member` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `role` ENUM('LEADER', 'MEMBER') DEFAULT 'MEMBER',
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED', 'LEFT', 'KICKED') DEFAULT 'PENDING',
    `joined_at` TIMESTAMP,
    `left_at` TIMESTAMP,
    `is_probation` BOOLEAN DEFAULT TRUE,         -- 수습기간 여부
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_study_user` (`study_id`, `user_id`)
);

CREATE TABLE `study_session` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `session_number` INT NOT NULL,               -- 회차 번호
    `title` VARCHAR(200),
    `description` TEXT,
    `scheduled_at` TIMESTAMP NOT NULL,           -- 예정 일시
    `duration_minutes` INT DEFAULT 60,
    `location` VARCHAR(200),                     -- 장소 (온라인/오프라인 주소)
    `is_online` BOOLEAN DEFAULT TRUE,
    `status` ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    `completed_at` TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_study_session` (`study_id`, `session_number`)
);

CREATE TABLE `study_bookmark` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `study_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_study` (`user_id`, `study_id`)
);

CREATE TABLE `study_application` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `message` TEXT,                              -- 지원 메시지
    `matching_score` DECIMAL(5,2),               -- AI 매칭 점수
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    `rejected_reason` VARCHAR(500),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `processed_at` TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE TABLE `study_leader_review` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `reviewer_id` BIGINT NOT NULL,               -- 평가자
    `leader_id` BIGINT NOT NULL,                 -- 평가 대상 (스터디장)
    `rating` DECIMAL(2,1) NOT NULL,              -- 1.0 ~ 5.0
    `comment` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`reviewer_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`leader_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_study_reviewer` (`study_id`, `reviewer_id`)
);

-- =============================================
-- 3. 채널/채팅
-- =============================================

CREATE TABLE `channel` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `type` ENUM('TEXT', 'VOICE') NOT NULL,
    `voice_room_type` ENUM('DISCUSSION', 'MEETING'),  -- 음성방 타입 (상시토론/미팅)
    `description` VARCHAR(500),
    `is_default` BOOLEAN DEFAULT FALSE,          -- 기본 채널 여부
    `is_temporary` BOOLEAN DEFAULT FALSE,        -- 임시 채널 여부 (인원 부족 시 논의용, 텍스트만)
    `sort_order` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE
);

CREATE TABLE `message` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `channel_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `message_type` ENUM('TEXT', 'IMAGE', 'FILE', 'SYSTEM') DEFAULT 'TEXT',
    `file_url` VARCHAR(500),
    `is_deleted` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`channel_id`) REFERENCES `channel`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

-- =============================================
-- 4. 미팅
-- =============================================

CREATE TABLE `meeting` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `session_id` BIGINT,                         -- 연결된 스터디 세션
    `channel_id` BIGINT,                         -- 음성 채널
    `title` VARCHAR(200),
    `started_at` TIMESTAMP,
    `ended_at` TIMESTAMP,
    `duration_seconds` INT,
    `participant_count` INT DEFAULT 0,
    `status` ENUM('WAITING', 'IN_PROGRESS', 'ENDED') DEFAULT 'WAITING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`session_id`) REFERENCES `study_session`(`id`),
    FOREIGN KEY (`channel_id`) REFERENCES `channel`(`id`)
);

CREATE TABLE `meeting_participant` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `joined_at` TIMESTAMP NOT NULL,
    `left_at` TIMESTAMP,
    `is_muted` BOOLEAN DEFAULT FALSE,
    `is_camera_on` BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (`meeting_id`) REFERENCES `meeting`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE TABLE `meeting_transcript` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,                     -- STT 결과
    `timestamp_seconds` INT NOT NULL,            -- 미팅 시작 기준 초
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`meeting_id`) REFERENCES `meeting`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE TABLE `meeting_summary` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id` BIGINT NOT NULL UNIQUE,
    `summary` TEXT,                              -- AI 요약
    `action_items` JSON,                         -- 액션 아이템 목록
    `keywords` JSON,                             -- 키워드 목록
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`meeting_id`) REFERENCES `meeting`(`id`) ON DELETE CASCADE
);

CREATE TABLE `meeting_photo` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id` BIGINT NOT NULL,
    `image_url` VARCHAR(500) NOT NULL,
    `captured_at` TIMESTAMP NOT NULL,
    `is_selected` BOOLEAN DEFAULT FALSE,         -- 보고서용 선택 여부
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`meeting_id`) REFERENCES `meeting`(`id`) ON DELETE CASCADE
);

-- =============================================
-- 5. 출석
-- =============================================

CREATE TABLE `attendance` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `session_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `check_type` ENUM('BLE', 'SELF', 'AUTO') DEFAULT 'SELF',
    `status` ENUM('PRESENT', 'LATE', 'ABSENT', 'EXCUSED') DEFAULT 'ABSENT',
    `checked_at` TIMESTAMP,
    `checked_by` BIGINT,                         -- BLE 체크 시 스터디장 ID
    `excuse_reason` TEXT,
    `excuse_status` ENUM('PENDING', 'APPROVED', 'REJECTED'),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`session_id`) REFERENCES `study_session`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`checked_by`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_session_user` (`session_id`, `user_id`)
);

-- 세션별 개인 메모 (오프라인 스터디용)
CREATE TABLE `session_memo` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `session_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` VARCHAR(500) NOT NULL,             -- 한 줄 메모
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`session_id`) REFERENCES `study_session`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_session_user_memo` (`session_id`, `user_id`)
);

-- =============================================
-- 6. 퀴즈
-- =============================================

CREATE TABLE `quiz_contest` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL,
    `description` TEXT,
    `created_by` BIGINT NOT NULL,                -- 관리자
    `status` ENUM('DRAFT', 'SCHEDULED', 'IN_PROGRESS', 'ENDED') DEFAULT 'DRAFT',
    `scheduled_at` TIMESTAMP,
    `started_at` TIMESTAMP,
    `ended_at` TIMESTAMP,
    `time_limit_seconds` INT DEFAULT 30,         -- 문제당 제한 시간
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `quiz_question` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `contest_id` BIGINT NOT NULL,
    `question_number` INT NOT NULL,
    `question_text` TEXT NOT NULL,
    `question_type` ENUM('MULTIPLE_CHOICE', 'SHORT_ANSWER') DEFAULT 'MULTIPLE_CHOICE',
    `options` JSON,                              -- 객관식 보기
    `correct_answer` VARCHAR(500) NOT NULL,
    `explanation` TEXT,                          -- 해설
    `points` INT DEFAULT 10,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`contest_id`) REFERENCES `quiz_contest`(`id`) ON DELETE CASCADE
);

CREATE TABLE `quiz_participant` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `contest_id` BIGINT NOT NULL,
    `user_id` BIGINT,                            -- NULL이면 비로그인
    `nickname` VARCHAR(50) NOT NULL,             -- 표시 닉네임
    `total_score` INT DEFAULT 0,
    `correct_count` INT DEFAULT 0,
    `rank` INT,
    `joined_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`contest_id`) REFERENCES `quiz_contest`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE TABLE `quiz_answer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `participant_id` BIGINT NOT NULL,
    `question_id` BIGINT NOT NULL,
    `answer` VARCHAR(500),
    `is_correct` BOOLEAN,
    `score` INT DEFAULT 0,
    `answered_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`participant_id`) REFERENCES `quiz_participant`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`question_id`) REFERENCES `quiz_question`(`id`) ON DELETE CASCADE
);

-- 팀 내 복습 퀴즈
CREATE TABLE `study_quiz` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `session_id` BIGINT,                         -- 연결된 세션 (선택)
    `title` VARCHAR(200) NOT NULL,
    `source_type` ENUM('MEETING', 'MATERIAL', 'MANUAL') NOT NULL,  -- 생성 소스
    `source_id` BIGINT,
    `status` ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`session_id`) REFERENCES `study_session`(`id`)
);

CREATE TABLE `study_quiz_question` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `quiz_id` BIGINT NOT NULL,
    `question_text` TEXT NOT NULL,
    `question_type` ENUM('MULTIPLE_CHOICE', 'SHORT_ANSWER') DEFAULT 'MULTIPLE_CHOICE',
    `options` JSON,
    `correct_answer` VARCHAR(500) NOT NULL,
    `explanation` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`quiz_id`) REFERENCES `study_quiz`(`id`) ON DELETE CASCADE
);

CREATE TABLE `study_quiz_attempt` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `quiz_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `score` INT DEFAULT 0,
    `total_questions` INT,
    `correct_count` INT,
    `completed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`quiz_id`) REFERENCES `study_quiz`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE TABLE `wrong_answer_note` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `question_id` BIGINT NOT NULL,               -- study_quiz_question ID
    `user_answer` VARCHAR(500),
    `review_count` INT DEFAULT 0,
    `last_reviewed_at` TIMESTAMP,
    `is_mastered` BOOLEAN DEFAULT FALSE,         -- 완전 숙지 여부
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`question_id`) REFERENCES `study_quiz_question`(`id`) ON DELETE CASCADE
);

-- 세션형 퀴즈 (코스 기반 학습)
CREATE TABLE `quiz_course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL UNIQUE,          -- JAVA, PYTHON, CS_BASIC 등
    `name` VARCHAR(100) NOT NULL,                -- Java 마스터, Python 기초 등
    `description` TEXT,
    `icon` VARCHAR(10),                          -- ☕, 🐍 등
    `badge_code` VARCHAR(50),                    -- 완료 시 부여할 뱃지 코드
    `total_sections` INT DEFAULT 0,
    `is_active` BOOLEAN DEFAULT TRUE,
    `sort_order` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `quiz_course_section` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL,
    `section_number` INT NOT NULL,               -- 순서
    `name` VARCHAR(100) NOT NULL,                -- 기본 문법, 객체지향 등
    `description` TEXT,
    `total_questions` INT DEFAULT 0,
    `pass_score` INT DEFAULT 70,                 -- 통과 점수 (%)
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`course_id`) REFERENCES `quiz_course`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_course_section` (`course_id`, `section_number`)
);

CREATE TABLE `quiz_course_question` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `section_id` BIGINT NOT NULL,
    `question_number` INT NOT NULL,
    `question_text` TEXT NOT NULL,
    `question_type` ENUM('MULTIPLE_CHOICE', 'SHORT_ANSWER') DEFAULT 'MULTIPLE_CHOICE',
    `options` JSON,                              -- 객관식 보기
    `correct_answer` VARCHAR(500) NOT NULL,
    `explanation` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`section_id`) REFERENCES `quiz_course_section`(`id`) ON DELETE CASCADE
);

-- 사용자별 코스 진행 상황
CREATE TABLE `user_course_progress` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `current_section` INT DEFAULT 1,             -- 현재 진행 중인 섹션
    `completed_sections` INT DEFAULT 0,          -- 완료한 섹션 수
    `is_completed` BOOLEAN DEFAULT FALSE,        -- 코스 완료 여부
    `completed_at` TIMESTAMP,
    `started_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`course_id`) REFERENCES `quiz_course`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_course` (`user_id`, `course_id`)
);

-- 사용자별 섹션 시도 기록
CREATE TABLE `user_section_attempt` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `section_id` BIGINT NOT NULL,
    `score` INT DEFAULT 0,                       -- 점수 (%)
    `correct_count` INT DEFAULT 0,
    `total_questions` INT DEFAULT 0,
    `is_passed` BOOLEAN DEFAULT FALSE,           -- 통과 여부
    `attempted_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`section_id`) REFERENCES `quiz_course_section`(`id`) ON DELETE CASCADE
);

-- =============================================
-- 7. 게이미피케이션
-- =============================================

CREATE TABLE `daily_contribution` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `contribution_date` DATE NOT NULL,
    `has_activity` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_date` (`user_id`, `contribution_date`)
);

CREATE TABLE `contribution_detail` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `contribution_date` DATE NOT NULL,
    `activity_type` ENUM('STUDY_ATTENDANCE', 'QUIZ_CONTEST') NOT NULL,
    `reference_id` BIGINT NOT NULL,
    `reference_name` VARCHAR(200) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

CREATE TABLE `user_stats` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL UNIQUE,
    `level` INT DEFAULT 1,
    `level_name` VARCHAR(50) DEFAULT '새싹',
    `total_activity_days` INT DEFAULT 0,
    `current_streak` INT DEFAULT 0,
    `max_streak` INT DEFAULT 0,
    `last_activity_date` DATE,
    `total_studies_joined` INT DEFAULT 0,
    `total_studies_led` INT DEFAULT 0,
    `total_chat_count` INT DEFAULT 0,
    `total_quiz_count` INT DEFAULT 0,
    `total_materials_uploaded` INT DEFAULT 0,
    `total_retrospectives` INT DEFAULT 0,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- 뱃지 정의 (영구, 긍정적 - 한번 획득하면 유지)
CREATE TABLE `badge` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL UNIQUE,          -- FIRST_ACTIVITY, STREAK_7 등
    `name` VARCHAR(100) NOT NULL,                -- 첫 발걸음, 일주일 연속 등
    `description` VARCHAR(200) NOT NULL,
    `icon` VARCHAR(10),
    `category` ENUM('ACTIVITY', 'STREAK', 'STUDY', 'ATTENDANCE', 'PARTICIPATION', 'QUIZ', 'MASTER', 'SPECIAL') NOT NULL,
    `condition_type` VARCHAR(50) NOT NULL,
    `condition_value` INT NOT NULL,
    `sort_order` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 사용자 뱃지 (획득 기록)
CREATE TABLE `user_badge` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `badge_id` BIGINT NOT NULL,
    `earned_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`badge_id`) REFERENCES `badge`(`id`),
    UNIQUE KEY `uk_user_badge` (`user_id`, `badge_id`)
);

-- 패널티 정의 (일시적, 부정적 - 해소 가능)
CREATE TABLE `penalty` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL UNIQUE,          -- THREE_DAY_QUIT, GHOST 등
    `name` VARCHAR(100) NOT NULL,                -- 작심삼일, 유령회원 등
    `description` VARCHAR(200) NOT NULL,
    `icon` VARCHAR(10),
    `grant_condition` VARCHAR(200),              -- 부여 조건 설명
    `removal_condition` VARCHAR(200),            -- 해소 조건 설명
    `removal_required` INT,                      -- 해소에 필요한 횟수
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 사용자 패널티 (일시적, 해소 가능)
CREATE TABLE `user_penalty` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `penalty_id` BIGINT NOT NULL,
    `study_id` BIGINT,                           -- 스터디별 패널티인 경우
    `is_active` BOOLEAN DEFAULT TRUE,
    `removal_progress` INT DEFAULT 0,            -- 해소 진행률 (출석 횟수 등)
    `granted_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `removed_at` TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`penalty_id`) REFERENCES `penalty`(`id`),
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE SET NULL
);

-- =============================================
-- 8. 자료실
-- =============================================

CREATE TABLE `material` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `uploader_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `description` TEXT,
    `material_type` ENUM('LINK', 'FILE', 'IMAGE', 'VIDEO') NOT NULL,
    `url` VARCHAR(500),
    `file_path` VARCHAR(500),
    `file_size` BIGINT,
    `week_number` INT,                           -- 커리큘럼 주차
    `view_count` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`uploader_id`) REFERENCES `user`(`id`)
);

CREATE TABLE `material_comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `material_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`material_id`) REFERENCES `material`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE TABLE `curriculum` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `week_number` INT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `description` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_study_week` (`study_id`, `week_number`)
);

CREATE TABLE `progress` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `curriculum_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `is_completed` BOOLEAN DEFAULT FALSE,
    `completed_at` TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`curriculum_id`) REFERENCES `curriculum`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_curriculum_user` (`curriculum_id`, `user_id`)
);

-- =============================================
-- 9. 회고
-- =============================================

CREATE TABLE `retrospective` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `session_id` BIGINT,
    `title` VARCHAR(200) NOT NULL,
    `retrospective_type` ENUM('KPT', 'FREE') DEFAULT 'KPT',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`session_id`) REFERENCES `study_session`(`id`)
);

CREATE TABLE `retrospective_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `retrospective_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `category` ENUM('KEEP', 'PROBLEM', 'TRY') NOT NULL,  -- KPT
    `content` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`retrospective_id`) REFERENCES `retrospective`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

-- =============================================
-- 10. 알림
-- =============================================

CREATE TABLE `notification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `type` ENUM('CHAT', 'SCHEDULE', 'ATTENDANCE', 'STUDY_UPDATE', 'QUIZ', 'SYSTEM') NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT,
    `reference_type` VARCHAR(50),                -- study, meeting, quiz_contest 등
    `reference_id` BIGINT,
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

CREATE TABLE `notification_setting` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `notification_type` VARCHAR(50) NOT NULL,
    `is_enabled` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_type` (`user_id`, `notification_type`)
);

CREATE TABLE `fcm_token` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `token` VARCHAR(500) NOT NULL,
    `device_type` ENUM('ANDROID', 'IOS', 'WEB') NOT NULL,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- =============================================
-- 11. SSAFY 보고서
-- =============================================

CREATE TABLE `report` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `report_type` ENUM('APPLICATION', 'MONTHLY_RESULT') NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `content` JSON,                              -- 보고서 내용 (자동 생성)
    `report_month` DATE,                         -- 월별 보고서인 경우
    `status` ENUM('DRAFT', 'SUBMITTED') DEFAULT 'DRAFT',
    `created_by` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`created_by`) REFERENCES `user`(`id`)
);

-- =============================================
-- 12. AI 피드백
-- =============================================

CREATE TABLE `ai_feedback` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `feature_type` VARCHAR(50) NOT NULL,         -- summary, action_item, quiz, etc.
    `reference_id` BIGINT,
    `feedback` ENUM('POSITIVE', 'NEGATIVE') NOT NULL,
    `comment` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

-- =============================================
-- 13. 데일리
-- =============================================

CREATE TABLE `daily_report` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `study_id` BIGINT NOT NULL,
    `report_date` DATE NOT NULL,
    `summary` TEXT,                              -- AI 생성 요약
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`study_id`) REFERENCES `study`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_study_date` (`study_id`, `report_date`)
);

CREATE TABLE `daily_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `daily_report_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `category` ENUM('YESTERDAY', 'TODAY', 'BLOCKER') NOT NULL,  -- 어제 한 일/오늘 할 일/블로커
    `content` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`daily_report_id`) REFERENCES `daily_report`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);
