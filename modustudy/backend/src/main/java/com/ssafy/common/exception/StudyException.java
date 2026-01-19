package com.ssafy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 스터디 관련 예외 클래스
 */
public class StudyException {

    /**
     * 스터디를 찾을 수 없음 (404)
     */
    public static class StudyNotFoundException extends BusinessException {
        public StudyNotFoundException(Long studyId) {
            super(HttpStatus.NOT_FOUND, "STUDY_NOT_FOUND",
                    "존재하지 않는 스터디입니다: " + studyId);
        }
    }

    /**
     * 권한 없음 (403)
     */
    public static class NotStudyLeaderException extends BusinessException {
        public NotStudyLeaderException() {
            super(HttpStatus.FORBIDDEN, "NOT_STUDY_LEADER",
                    "스터디장만 수정/삭제할 수 있습니다");
        }

        public NotStudyLeaderException(String message) {
            super(HttpStatus.FORBIDDEN, "NOT_STUDY_LEADER", message);
        }
    }

    /**
     * 잘못된 요청 (400)
     */
    public static class InvalidStudyRequestException extends BusinessException {
        public InvalidStudyRequestException(String message) {
            super(HttpStatus.BAD_REQUEST, "INVALID_STUDY_REQUEST", message);
        }
    }

    /**
     * 상태 전환 불가 (400)
     */
    public static class InvalidStatusTransitionException extends BusinessException {
        public InvalidStatusTransitionException(String message) {
            super(HttpStatus.BAD_REQUEST, "INVALID_STATUS_TRANSITION", message);
        }
    }

    /**
     * 삭제 불가 (400)
     */
    public static class CannotDeleteStudyException extends BusinessException {
        public CannotDeleteStudyException() {
            super(HttpStatus.BAD_REQUEST, "CANNOT_DELETE_ACTIVE_STUDY",
                    "진행 중이거나 완료된 스터디는 삭제할 수 없습니다");
        }
    }

    /**
     * 모집 연장 불가 (400)
     */
    public static class MaxExtensionReachedException extends BusinessException {
        public MaxExtensionReachedException() {
            super(HttpStatus.BAD_REQUEST, "MAX_EXTENSION_REACHED",
                    "모집 기간은 최대 1회만 연장 가능합니다");
        }
    }

    /**
     * 모집 중이 아님 (400)
     */
    public static class NotRecruitingException extends BusinessException {
        public NotRecruitingException() {
            super(HttpStatus.BAD_REQUEST, "NOT_RECRUITING",
                    "모집 중인 스터디만 기간을 연장할 수 있습니다");
        }
    }
}