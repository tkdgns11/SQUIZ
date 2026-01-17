package com.ssafy.domain.quiz.service;

import com.ssafy.domain.gamification.entity.Badge;
import com.ssafy.domain.gamification.repository.BadgeRepository;
import com.ssafy.domain.quiz.dto.response.QuizCourseListItem;
import com.ssafy.domain.quiz.dto.response.QuizCourseListResponse;
import com.ssafy.domain.quiz.entity.QuizCourse;
import com.ssafy.domain.quiz.repository.QuizCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
