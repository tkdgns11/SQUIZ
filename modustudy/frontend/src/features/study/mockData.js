// Mock 데이터 - 백엔드 API 연동 전까지 사용
// SQL 테스트 데이터를 JSON으로 변환

export const mockUsers = [
    {
        id: 1,
        email: 'leader1@ssafy.com',
        name: '김싸피',
        nickname: 'ssafy_kim',
        role: 'USER',
        isActive: true
    },
    {
        id: 2,
        email: 'leader2@ssafy.com',
        name: '이싸피',
        nickname: 'ssafy_lee',
        role: 'USER',
        isActive: true
    },
    {
        id: 3,
        email: 'leader3@ssafy.com',
        name: '박싸피',
        nickname: 'ssafy_park',
        role: 'USER',
        isActive: true
    },
    {
        id: 4,
        email: 'leader4@ssafy.com',
        name: '최싸피',
        nickname: 'ssafy_choi',
        role: 'USER',
        isActive: true
    }
];

export const mockRegions = [
    { id: 1, code: 'SEOUL', name: '서울', sortOrder: 1 },
    { id: 2, code: 'BUSAN', name: '부산', sortOrder: 2 },
    { id: 3, code: 'DAEJEON', name: '대전', sortOrder: 3 },
    { id: 4, code: 'GWANGJU', name: '광주', sortOrder: 4 },
    { id: 5, code: 'GUMI', name: '구미', sortOrder: 5 },
    { id: 6, code: 'DAEGU', name: '대구', sortOrder: 6 },
    { id: 7, code: 'INCHEON', name: '인천', sortOrder: 7 },
    { id: 8, code: 'ULSAN', name: '울산', sortOrder: 8 }
];

export const mockStudies = [
    {
        id: 1,
        leaderId: 1,
        name: '알고리즘 마스터',
        description: '백준 골드 문제 집중 풀이 스터디입니다',
        topic: '알고리즘',
        format: '문제풀이',
        studyType: 'PLANNED',
        meetingType: 'ONLINE',
        status: 'RECRUITING',
        isPublic: true,
        maxMembers: 6,
        currentMembers: 3,
        difficulty: 'INTERMEDIATE',
        scheduleDays: 'MON,WED,FRI',
        scheduleTime: '19:00:00',
        recruitStartDate: '2025-01-15',
        recruitEndDate: '2025-01-31',
        startDate: '2025-02-01',
        endDate: '2025-05-01',
        goal: '골드 티어 달성',
        textbook: '백준 온라인 저지',
        leader: {
            id: 1,
            nickname: 'ssafy_kim',
            profileImage: null,
            leaderRating: 4.5,
            leaderReviewCount: 10
        },
        isBookmarked: false,
        createdAt: '2025-01-15T10:00:00.000Z'
    },
    {
        id: 2,
        leaderId: 1,
        name: 'CS 기초 탄탄',
        description: '운영체제, 네트워크 완벽 정리',
        topic: 'CS',
        format: '독서',
        studyType: 'PLANNED',
        meetingType: 'OFFLINE',
        regionId: 1,
        locationDetail: '강남역 스터디카페',
        status: 'RECRUITING',
        isPublic: true,
        maxMembers: 4,
        currentMembers: 2,
        difficulty: 'BEGINNER',
        scheduleDays: 'TUE,THU',
        scheduleTime: '20:00:00',
        recruitStartDate: '2025-01-15',
        recruitEndDate: '2025-01-25',
        startDate: '2025-02-01',
        endDate: '2025-04-01',
        goal: 'CS 기본 개념 마스터',
        textbook: '운영체제와 정보기술의 원리',
        region: {
            id: 1,
            name: '서울'
        },
        leader: {
            id: 1,
            nickname: 'ssafy_kim',
            profileImage: null,
            leaderRating: 4.5,
            leaderReviewCount: 10
        },
        isBookmarked: false,
        createdAt: '2025-01-16T14:30:00.000Z'
    },
    {
        id: 3,
        leaderId: 2,
        name: '스프링 부트 실전',
        description: 'Spring Boot 프로젝트 기반 학습',
        topic: '백엔드',
        format: '프로젝트',
        studyType: 'PLANNED',
        meetingType: 'HYBRID',
        regionId: 1,
        locationDetail: '역삼역 근처',
        status: 'IN_PROGRESS',
        isPublic: true,
        maxMembers: 8,
        currentMembers: 6,
        difficulty: 'INTERMEDIATE',
        scheduleDays: 'SAT,SUN',
        scheduleTime: '14:00:00',
        startDate: '2025-01-01',
        endDate: '2025-03-31',
        goal: '실전 프로젝트 완성',
        region: {
            id: 1,
            name: '서울'
        },
        leader: {
            id: 2,
            nickname: 'ssafy_lee',
            profileImage: null,
            leaderRating: 4.8,
            leaderReviewCount: 15
        },
        isBookmarked: false,
        createdAt: '2025-01-17T09:15:00.000Z'
    },
    {
        id: 4,
        leaderId: 2,
        name: '주말 코테 특강',
        description: '이번 주말 집중 코딩테스트 준비',
        topic: '알고리즘',
        format: '문제풀이',
        studyType: 'LIGHTNING',
        meetingType: 'ONLINE',
        status: 'RECRUITING',
        isPublic: true,
        maxMembers: 10,
        currentMembers: 7,
        difficulty: 'ADVANCED',
        scheduleDays: 'SAT',
        recruitStartDate: '2025-01-18',
        recruitEndDate: '2025-01-20',
        startDate: '2025-01-21',
        endDate: '2025-01-21',
        leader: {
            id: 2,
            nickname: 'ssafy_lee',
            profileImage: null,
            leaderRating: 4.8,
            leaderReviewCount: 15
        },
        isBookmarked: false,
        createdAt: '2025-01-18T16:45:00.000Z'
    },
    {
        id: 5,
        leaderId: 3,
        name: 'React 정복하기',
        description: 'React 18 신기능 마스터',
        topic: '프론트엔드',
        format: '강의수강',
        studyType: 'PLANNED',
        meetingType: 'ONLINE',
        status: 'COMPLETED',
        isPublic: true,
        maxMembers: 5,
        currentMembers: 5,
        difficulty: 'INTERMEDIATE',
        scheduleDays: 'MON,WED',
        startDate: '2024-10-01',
        endDate: '2024-12-31',
        goal: 'React 18 완벽 이해',
        leader: {
            id: 3,
            nickname: 'ssafy_park',
            profileImage: null,
            leaderRating: 4.3,
            leaderReviewCount: 8
        },
        isBookmarked: false,
        createdAt: '2024-12-20T11:20:00.000Z'
    },
    {
        id: 6,
        leaderId: 3,
        name: '자바 기초부터',
        description: '자바 기본 문법 완벽 정리',
        topic: '프로그래밍 기초',
        format: '강의수강',
        studyType: 'PLANNED',
        meetingType: 'OFFLINE',
        regionId: 3,
        locationDetail: '유성구 카페',
        status: 'RECRUITING',
        isPublic: true,
        maxMembers: 6,
        currentMembers: 4,
        difficulty: 'BEGINNER',
        scheduleDays: 'MON,WED,FRI',
        scheduleTime: '19:30:00',
        recruitStartDate: '2025-01-18',
        recruitEndDate: '2025-02-05',
        startDate: '2025-02-10',
        endDate: '2025-04-10',
        region: {
            id: 3,
            name: '대전'
        },
        leader: {
            id: 3,
            nickname: 'ssafy_park',
            profileImage: null,
            leaderRating: 4.3,
            leaderReviewCount: 8
        },
        isBookmarked: false,
        createdAt: '2025-01-18T08:00:00.000Z'
    },
    {
        id: 7,
        leaderId: 4,
        name: 'SQL 완벽 정복',
        description: 'MySQL부터 PostgreSQL까지',
        topic: '데이터베이스',
        format: '문제풀이',
        studyType: 'PLANNED',
        meetingType: 'ONLINE',
        status: 'RECRUITING',
        isPublic: true,
        maxMembers: 10,
        currentMembers: 5,
        difficulty: 'INTERMEDIATE',
        scheduleDays: 'TUE,THU',
        scheduleTime: '21:00:00',
        recruitStartDate: '2025-01-18',
        recruitEndDate: '2025-02-01',
        startDate: '2025-02-05',
        endDate: '2025-04-30',
        goal: 'SQL 고급 쿼리 마스터',
        textbook: 'SQL 첫걸음',
        leader: {
            id: 4,
            nickname: 'ssafy_choi',
            profileImage: null,
            leaderRating: 4.6,
            leaderReviewCount: 12
        },
        isBookmarked: false,
        createdAt: '2025-01-18T13:30:00.000Z'
    },
    {
        id: 8,
        leaderId: 4,
        name: '파이썬 데이터 분석',
        description: 'Pandas, NumPy 활용하기',
        topic: '데이터 분석',
        format: '프로젝트',
        studyType: 'PLANNED',
        meetingType: 'ONLINE',
        status: 'IN_PROGRESS',
        isPublic: true,
        maxMembers: 8,
        currentMembers: 6,
        difficulty: 'ELEMENTARY',
        scheduleDays: 'SAT',
        scheduleTime: '10:00:00',
        startDate: '2025-01-10',
        endDate: '2025-03-10',
        goal: '데이터 분석 기초 완성',
        leader: {
            id: 4,
            nickname: 'ssafy_choi',
            profileImage: null,
            leaderRating: 4.6,
            leaderReviewCount: 12
        },
        isBookmarked: false,
        createdAt: '2025-01-10T07:00:00.000Z'
    },
    {
        id: 9,
        leaderId: 1,
        name: '매우매우매우매우매우 길어서 두 줄을 훌쩍 넘어가 버리는 스터디 제목 테스트입니다. 과연 제목은 두 줄에서 잘릴까요? 세 줄이 될까요?',
        description: '이것은 아주아주아주아주 긴 설명글입니다. 한 줄, 두 줄, 세 줄, 네 줄을 넘어서 계속해서 길어지는 텍스트가 카드의 레이아웃을 무너뜨리지 않고 CSS line-clamp 속성에 의해 두 줄에서 우아하게 잘리는지 확인하기 위한 테스트용 목업 데이터입니다. 더 이상 길어질 수 없을 만큼 길게 작성해 보겠습니다.',
        topic: '테스트',
        format: '테스트',
        studyType: 'PLANNED',
        meetingType: 'HYBRID',
        regionId: 1,
        locationDetail: '테스트 서버',
        status: 'RECRUITING',
        isPublic: true,
        maxMembers: 99,
        currentMembers: 1,
        difficulty: 'ADVANCED',
        scheduleDays: 'MON,TUE,WED,THU,FRI,SAT,SUN',
        scheduleTime: '00:00:00',
        recruitStartDate: '2025-01-01',
        recruitEndDate: '2025-12-31',
        startDate: '2025-01-01',
        endDate: '2025-12-31',
        goal: '오버플로우 완벽 차단',
        region: { id: 1, name: '서울' },
        leader: {
            id: 1,
            nickname: 'overflow_tester',
            profileImage: null,
            leaderRating: 5.0,
            leaderReviewCount: 999
        },
        isBookmarked: false,
        createdAt: '2025-01-19T00:00:00.000Z'
    }
];

// 유틸리티 함수들
export const getStudyById = (id) => {
    return mockStudies.find(study => study.id === id);
};

export const getStudiesByStatus = (status) => {
    return mockStudies.filter(study => study.status === status);
};

export const getRecruitingStudies = () => {
    return getStudiesByStatus('RECRUITING');
};

export const getStudiesByTopic = (topic) => {
    return mockStudies.filter(study => study.topic === topic);
};

export const searchStudies = (keyword) => {
    const lowerKeyword = keyword.toLowerCase();
    return mockStudies.filter(study =>
        study.name.toLowerCase().includes(lowerKeyword) ||
        study.description.toLowerCase().includes(lowerKeyword) ||
        study.topic.toLowerCase().includes(lowerKeyword)
    );
};

export const getRegionById = (id) => {
    return mockRegions.find(region => region.id === id);
};

export const getUserById = (id) => {
    return mockUsers.find(user => user.id === id);
};
