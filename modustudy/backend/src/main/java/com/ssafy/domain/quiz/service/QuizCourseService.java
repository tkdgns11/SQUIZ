package com.ssafy.domain.quiz.service;

import com.ssafy.domain.gamification.entity.Badge;
import com.ssafy.domain.gamification.repository.BadgeRepository;
import com.ssafy.domain.quiz.dto.response.BadgeInfo;
import com.ssafy.domain.quiz.dto.response.QuizCourseDetailResponse;
import com.ssafy.domain.quiz.dto.response.QuizCourseListItem;
import com.ssafy.domain.quiz.dto.response.QuizCourseListResponse;
import com.ssafy.domain.quiz.dto.response.SectionSummary;
import com.ssafy.domain.quiz.entity.QuizCourse;
import com.ssafy.domain.quiz.entity.QuizCourseSection;
import com.ssafy.domain.quiz.repository.QuizCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 퀴즈 코스 조회 비즈니스 로직.
 *
 * 호출자: {@link com.ssafy.domain.quiz.controller.QuizCourseController}
 *
 * 책임:
 * - 활성 코스 목록 조회
 * - 코스에 연결된 배지 코드로 배지 이름 조합
 *
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizCourseService {

    private final QuizCourseRepository quizCourseRepository;
    private final BadgeRepository badgeRepository;

    /**
     * 활성 코스 목록을 반환한다.
     *
     * 정렬 기준: sortOrder 오름차순, 동일 시 id 오름차순.
     * 배지 코드는 코스에 저장된 값을 사용하고, 배지명은 Badge 테이블에서 조회해 합성한다.
     *
     * @return 코스 목록 응답 DTO
     */
    public QuizCourseListResponse getCourseList() {
        List<QuizCourse> courses = quizCourseRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc();
        List<String> badgeCodes = courses.stream()
                .map(QuizCourse::getBadgeCode)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, String> badgeNameByCode = badgeCodes.isEmpty()
                ? Map.of()
                : badgeRepository.findByCodeIn(badgeCodes).stream()
                    .collect(Collectors.toMap(Badge::getCode, Badge::getName, (first, ignored) -> first));

        List<QuizCourseListItem> items = courses.stream()
                .map(course -> new QuizCourseListItem(
                        course.getId(),
                        course.getCode(),
                        course.getName(),
                        course.getDescription(),
                        course.getTotalSections(),
                        course.getBadgeCode(),
                        course.getBadgeCode() == null ? null : badgeNameByCode.get(course.getBadgeCode())
                ))
                .toList();

        return new QuizCourseListResponse(items);
    }

    /**
     * 코스 상세 정보를 반환한다.
     *
     * 코스 정보 + 섹션 목록 + 배지 정보를 조합한다.
     *
     * @param courseId 코스 ID
     * @return 코스 상세 응답 DTO
     */
    public QuizCourseDetailResponse getCourseDetail(Long courseId) {
        // 코스 + 섹션을 fetch join으로 조회 findByIdWithSections
        // 코스가 없으면 404로 응답 .orElseThrow
        // TODO: 이 부분 공통 error 포멧 사용할 수 있도록 핸들러 추가하고 교체하기
        QuizCourse course = quizCourseRepository.findByIdWithSections(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz course not found"));

        // 비활성 코스는 공개하지 않으므로 404로 처리
        // TODO: 이 부분도 공통 error 포멧 사용할 수 있도록 핸들러 추가하고 교체하기
        if (!Boolean.TRUE.equals(course.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz course not found");
        }

        BadgeInfo badgeInfo = null; // 응답에 넣을 배지 정보를 준비(기본값은 null).
        String badgeCode = course.getBadgeCode(); // 코스에 매핑된 배지 코드가 있는지 확인

        // 배지 코드가 있으면 배지 테이블에서 상세 조회
        if (badgeCode != null) {
            // code/name/description으로 DTO 생성 .map(람다)
            // 배지가 없으면 code만 넣고 나머지는 null로 둔다 .orElseGet(람다)
            badgeInfo = badgeRepository.findByCode(badgeCode)
                    .map(badge -> new BadgeInfo(badge.getCode(), badge.getName(), badge.getDescription()))
                    .orElseGet(() -> new BadgeInfo(badgeCode, null, null));
        }

        // 섹션 엔티티 목록을 응답용 요약 DTO 목록으로 변환
            // 섹션 하나를 SectionSummary로 매핑 map()
            // List로 수집 .toList()
        List<SectionSummary> sections = course.getSections().stream()
                .map(section -> toSectionSummary(section))
                .toList();

        // 최종 응답 DTO를 생성해 반환
        return new QuizCourseDetailResponse(
                course.getId(),
                course.getCode(),
                course.getName(),
                course.getDescription(),
                course.getTotalSections(),
                badgeInfo,
                sections
        );
    }

    private SectionSummary toSectionSummary(QuizCourseSection section) {
        return new SectionSummary(
                section.getSectionNumber(),
                section.getName(),
                section.getDescription(),
                section.getTotalQuestions(),
                section.getPassScore()
        );
    }
}
