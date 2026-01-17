-- 1. 퀴즈 카테고리 초기 데이터 (최상위)
INSERT INTO `quiz_category` (`parent_id`, `code`, `name`, `description`, `depth`, `sort_order`) VALUES
(NULL, 'DEV_IT', '개발/IT', 'IT 개발 전반', 0, 1);

-- 2. 대분류 (개발/IT 하위)
INSERT INTO `quiz_category` (`parent_id`, `code`, `name`, `description`, `depth`, `sort_order`) VALUES
(1, 'CS_BASIC', 'CS 기초', 'Computer Science 기초 개념', 1, 1),
(1, 'FRONTEND', '프론트엔드', '웹 프론트엔드 기술', 1, 2),
(1, 'BACKEND', '백엔드', '서버 및 백엔드 기술', 1, 3),
(1, 'INFRA_DEVOPS', '인프라/DevOps', '인프라 및 배포', 1, 4),
(1, 'AI_ML', 'AI/ML', '인공지능 및 머신러닝', 1, 5),
(1, 'MOBILE', '모바일', '모바일 앱 개발', 1, 6);

-- 3. CS 기초 중분류
INSERT INTO `quiz_category` (`parent_id`, `code`, `name`, `depth`, `sort_order`) VALUES
(2, 'DATA_STRUCTURE', '자료구조', 2, 1),
(2, 'ALGORITHM', '알고리즘 이론', 2, 2),
(2, 'OS', '운영체제', 2, 3),
(2, 'NETWORK', '네트워크', 2, 4),
(2, 'DATABASE', '데이터베이스', 2, 5),
(2, 'COMPUTER_ARCH', '컴퓨터구조', 2, 6),
(2, 'DESIGN_PATTERN', '디자인패턴', 2, 7),
(2, 'SYSTEM_DESIGN', '시스템 설계', 2, 8);

-- 4. 프론트엔드 중분류
INSERT INTO `quiz_category` (`parent_id`, `code`, `name`, `depth`, `sort_order`) VALUES
(3, 'HTML_CSS', 'HTML/CSS', 2, 1),
(3, 'JAVASCRIPT', 'JavaScript', 2, 2),
(3, 'TYPESCRIPT', 'TypeScript', 2, 3),
(3, 'REACT', 'React', 2, 4),
(3, 'VUE', 'Vue', 2, 5),
(3, 'NEXTJS', 'Next.js', 2, 6),
(3, 'WEB_PERF', '웹 접근성/성능', 2, 7);

-- 5. 백엔드 중분류
INSERT INTO `quiz_category` (`parent_id`, `code`, `name`, `depth`, `sort_order`) VALUES
(4, 'JAVA_SPRING', 'Java/Spring', 2, 1),
(4, 'PYTHON_DJANGO', 'Python/Django', 2, 2),
(4, 'PYTHON_FASTAPI', 'Python/FastAPI', 2, 3),
(4, 'NODEJS_EXPRESS', 'Node.js/Express', 2, 4),
(4, 'GO', 'Go', 2, 5),
(4, 'KOTLIN', 'Kotlin', 2, 6),
(4, 'API_DESIGN', 'API 설계', 2, 7);

-- 6. 인프라/DevOps 중분류
INSERT INTO `quiz_category` (`parent_id`, `code`, `name`, `depth`, `sort_order`) VALUES
(5, 'DOCKER', 'Docker', 2, 1),
(5, 'KUBERNETES', 'Kubernetes', 2, 2),
(5, 'CICD', 'CI/CD', 2, 3),
(5, 'AWS', 'AWS', 2, 4),
(5, 'GCP', 'GCP', 2, 5),
(5, 'LINUX', 'Linux', 2, 6),
(5, 'MONITORING', '모니터링', 2, 7);

-- 7. AI/ML 중분류
INSERT INTO `quiz_category` (`parent_id`, `code`, `name`, `depth`, `sort_order`) VALUES
(6, 'ML_BASIC', '머신러닝 기초', 2, 1),
(6, 'DEEP_LEARNING', '딥러닝', 2, 2),
(6, 'NLP', 'NLP', 2, 3),
(6, 'COMPUTER_VISION', '컴퓨터 비전', 2, 4),
(6, 'MLOPS', 'MLOps', 2, 5),
(6, 'PAPER_REVIEW', '논문 리뷰', 2, 6);

-- 8. 모바일 중분류
INSERT INTO `quiz_category` (`parent_id`, `code`, `name`, `depth`, `sort_order`) VALUES
(7, 'ANDROID_KOTLIN', 'Android (Kotlin)', 2, 1),
(7, 'ANDROID_JAVA', 'Android (Java)', 2, 2),
(7, 'IOS_SWIFT', 'iOS (Swift)', 2, 3),
(7, 'FLUTTER', 'Flutter', 2, 4),
(7, 'REACT_NATIVE', 'React Native', 2, 5);

-- 9. 샘플 문제 (자료구조 - 배열)
INSERT INTO `quiz_question_pool` 
(`category_id`, `question_text`, `question_type`, `explanation`, `difficulty`, `tags`, `created_by`) VALUES
(8, '배열(Array)의 시간복잡도가 O(1)인 연산은?', 'MULTIPLE_CHOICE',
'배열은 인덱스를 통한 직접 접근이 O(1)입니다. 삽입/삭제는 최악의 경우 O(n)입니다.',
'EASY', '["배열", "시간복잡도", "자료구조"]', 1);

INSERT INTO `quiz_question_pool_option`
(`question_pool_id`, `option_label`, `option_text`, `is_correct`, `sort_order`) VALUES
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 8 AND `question_text` = '배열(Array)의 시간복잡도가 O(1)인 연산은?'), 'A', '인덱스 접근', TRUE, 1),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 8 AND `question_text` = '배열(Array)의 시간복잡도가 O(1)인 연산은?'), 'B', '삽입', FALSE, 2),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 8 AND `question_text` = '배열(Array)의 시간복잡도가 O(1)인 연산은?'), 'C', '삭제', FALSE, 3),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 8 AND `question_text` = '배열(Array)의 시간복잡도가 O(1)인 연산은?'), 'D', '검색', FALSE, 4);

-- 10. 샘플 문제 (알고리즘 - 정렬)
INSERT INTO `quiz_question_pool` 
(`category_id`, `question_text`, `question_type`, `explanation`, `difficulty`, `tags`, `created_by`) VALUES
(9, '평균 시간복잡도가 O(n log n)인 정렬 알고리즘은?', 'MULTIPLE_CHOICE',
'병합 정렬(Merge Sort)은 분할 정복 방식으로 평균/최악 모두 O(n log n)입니다.',
'MEDIUM', '["정렬", "알고리즘", "시간복잡도"]', 1);

INSERT INTO `quiz_question_pool_option`
(`question_pool_id`, `option_label`, `option_text`, `is_correct`, `sort_order`) VALUES
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 9 AND `question_text` = '평균 시간복잡도가 O(n log n)인 정렬 알고리즘은?'), 'A', '버블 정렬', FALSE, 1),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 9 AND `question_text` = '평균 시간복잡도가 O(n log n)인 정렬 알고리즘은?'), 'B', '선택 정렬', FALSE, 2),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 9 AND `question_text` = '평균 시간복잡도가 O(n log n)인 정렬 알고리즘은?'), 'C', '병합 정렬', TRUE, 3),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 9 AND `question_text` = '평균 시간복잡도가 O(n log n)인 정렬 알고리즘은?'), 'D', '삽입 정렬', FALSE, 4);

-- 11. 샘플 문제 (React - Hooks)
INSERT INTO `quiz_question_pool` 
(`category_id`, `question_text`, `question_type`, `explanation`, `difficulty`, `tags`, `created_by`) VALUES
(18, 'React에서 사이드 이펙트를 처리하는 Hook은?', 'MULTIPLE_CHOICE',
'useEffect는 컴포넌트의 사이드 이펙트(API 호출, DOM 조작 등)를 처리합니다.',
'EASY', '["React", "Hooks", "useEffect"]', 1);

INSERT INTO `quiz_question_pool_option`
(`question_pool_id`, `option_label`, `option_text`, `is_correct`, `sort_order`) VALUES
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 18 AND `question_text` = 'React에서 사이드 이펙트를 처리하는 Hook은?'), 'A', 'useState', FALSE, 1),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 18 AND `question_text` = 'React에서 사이드 이펙트를 처리하는 Hook은?'), 'B', 'useEffect', TRUE, 2),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 18 AND `question_text` = 'React에서 사이드 이펙트를 처리하는 Hook은?'), 'C', 'useContext', FALSE, 3),
((SELECT `id` FROM `quiz_question_pool` WHERE `category_id` = 18 AND `question_text` = 'React에서 사이드 이펙트를 처리하는 Hook은?'), 'D', 'useReducer', FALSE, 4);
