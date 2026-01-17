package com.ssafy.domain.study.repository;

import com.ssafy.domain.study.entity.Difficulty;
import com.ssafy.domain.study.entity.MeetingType;
import com.ssafy.domain.study.entity.Status;
import com.ssafy.domain.study.entity.StudyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 스터디 검색/필터 조건
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySearchCondition {

    // 키워드 검색 (이름 + 설명)
    private String keyword;

    // 주제 (알고리즘, CS, 자격증 등)
    private String topic;

    // 진행 포맷 (문제풀이, 독서, 강의수강 등)
    private String format;

    // 스터디 타입 (계획/번개)
    private StudyType studyType;

    // 진행 방식 (온라인, 오프라인, 혼합)
    private MeetingType meetingType;

    // 스터디 상태 (모집중, 진행중 등)
    private Status status;

    // 지역 ID (오프라인/혼합인 경우)
    private Long regionId;

    // 난이도
    private Difficulty difficulty;

    // 공개 여부
    private Boolean isPublic;

    // 대상 소속 타입 (SSAFY, NBC 등)
    private String targetOrgType;

    // 요일 필터 (ex: MON,WED)
    private String scheduleDays;

    // 최대 인원 이하
    private Integer maxMembersLessThan;
}