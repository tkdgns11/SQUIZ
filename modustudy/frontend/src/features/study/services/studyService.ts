// Study Service - Mock 데이터를 사용한 스터디 관련 비즈니스 로직
import { mockStudies, mockRegions, mockUsers } from '../mockData';

export interface Study {
    id: number;
    leaderId: number;
    name: string;
    description: string;
    topic: string;
    format: string;
    studyType: string;
    meetingType: string;
    status: string;
    maxMembers: number;
    currentMembers: number;
    difficulty: string;
    scheduleDays: string;
    scheduleTime?: string;
    regionId?: number;
    locationDetail?: string;
    recruitEndDate?: string;
    region?: {
        id: number;
        name: string;
    };
    leader: {
        id: number;
        nickname: string;
        profileImage?: string;
        leaderRating: number;
        leaderReviewCount: number;
    };
    isBookmarked: boolean;
    createdAt: string;
}

export interface FilterOptions {
    status?: string[];
    topic?: string[];
    meetingType?: string[];
    difficulty?: string[];
    studyType?: string[];
    regionId?: number[];
    keyword?: string;
}

export interface SortOption {
    field: 'createdAt' | 'currentMembers' | 'recruitEndDate';
    order: 'asc' | 'desc';
}

class StudyService {
    // 전체 스터디 목록 조회
    getAllStudies(): Study[] {
        return [...mockStudies];
    }

    // 스터디 ID로 조회
    getStudyById(id: number): Study | undefined {
        return mockStudies.find((study: Study) => study.id === id);
    }

    // 필터링된 스터디 목록 조회
    getFilteredStudies(filters: FilterOptions): Study[] {
        let filtered = [...mockStudies];

        // 상태 필터
        if (filters.status && filters.status.length > 0) {
            filtered = filtered.filter((study: Study) => filters.status!.includes(study.status));
        }

        // 주제 필터
        if (filters.topic && filters.topic.length > 0) {
            filtered = filtered.filter((study: Study) => filters.topic!.includes(study.topic));
        }

        // 미팅 타입 필터
        if (filters.meetingType && filters.meetingType.length > 0) {
            filtered = filtered.filter((study: Study) =>
                filters.meetingType!.includes(study.meetingType)
            );
        }

        // 난이도 필터
        if (filters.difficulty && filters.difficulty.length > 0) {
            filtered = filtered.filter((study: Study) => filters.difficulty!.includes(study.difficulty));
        }

        // 스터디 타입 필터
        if (filters.studyType && filters.studyType.length > 0) {
            filtered = filtered.filter((study: Study) => filters.studyType!.includes(study.studyType));
        }

        // 지역 필터
        if (filters.regionId && filters.regionId.length > 0) {
            filtered = filtered.filter(
                (study: Study) => study.regionId && filters.regionId!.includes(study.regionId)
            );
        }

        // 키워드 검색
        if (filters.keyword && filters.keyword.trim()) {
            const keyword = filters.keyword.toLowerCase();
            filtered = filtered.filter(
                (study: Study) =>
                    study.name.toLowerCase().includes(keyword) ||
                    study.description.toLowerCase().includes(keyword) ||
                    study.topic.toLowerCase().includes(keyword)
            );
        }

        return filtered;
    }

    // 정렬
    sortStudies(studies: Study[], sortOption: SortOption): Study[] {
        const sorted = [...studies];

        sorted.sort((a: Study, b: Study) => {
            let aValue: any;
            let bValue: any;

            switch (sortOption.field) {
                case 'createdAt':
                    aValue = new Date(a.createdAt).getTime();
                    bValue = new Date(b.createdAt).getTime();
                    break;
                case 'currentMembers':
                    aValue = a.currentMembers;
                    bValue = b.currentMembers;
                    break;
                case 'recruitEndDate':
                    aValue = a.recruitEndDate ? new Date(a.recruitEndDate).getTime() : 0;
                    bValue = b.recruitEndDate ? new Date(b.recruitEndDate).getTime() : 0;
                    break;
                default:
                    return 0;
            }

            if (sortOption.order === 'asc') {
                return aValue - bValue;
            } else {
                return bValue - aValue;
            }
        });

        return sorted;
    }

    // 모집중인 스터디만 조회
    getRecruitingStudies(): Study[] {
        return mockStudies.filter((study: Study) => study.status === 'RECRUITING');
    }

    // 진행중인 스터디만 조회
    getInProgressStudies(): Study[] {
        return mockStudies.filter((study: Study) => study.status === 'IN_PROGRESS');
    }

    // 찜하기 토글 (실제로는 API 호출, 여기서는 로컬 상태만 변경)
    toggleBookmark(studyId: number): boolean {
        const study = mockStudies.find((s: Study) => s.id === studyId);
        if (study) {
            study.isBookmarked = !study.isBookmarked;
            return study.isBookmarked;
        }
        return false;
    }

    // 지역 목록 조회
    getRegions() {
        return [...mockRegions];
    }

    // 사용자 정보 조회
    getUserById(userId: number) {
        return mockUsers.find((user: any) => user.id === userId);
    }

    // 페이지네이션
    paginateStudies(studies: Study[], page: number, pageSize: number) {
        const startIndex = (page - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        const paginatedStudies = studies.slice(startIndex, endIndex);

        return {
            studies: paginatedStudies,
            totalCount: studies.length,
            totalPages: Math.ceil(studies.length / pageSize),
            currentPage: page,
            pageSize,
        };
    }

    // 스터디 신청 (API 시뮬레이션)
    async applyToStudy(studyId: number, message: string): Promise<{ success: boolean; message: string }> {
        console.log(`[StudyService] Applying to study ${studyId}: ${message}`);
        // 비동기 통신 시뮬레이션 (1.5초 대기)
        return new Promise((resolve) => {
            setTimeout(() => {
                // 시뮬레이션을 위해 10% 확률로 실패 발생
                const isError = Math.random() < 0.1;

                if (isError) {
                    resolve({
                        success: false,
                        message: '서버와의 통신 중 오류가 발생했습니다. 다시 시도해주세요.'
                    });
                } else {
                    resolve({
                        success: true,
                        message: '스터디 신청이 완료되었습니다! 스터디장의 승인을 기다려주세요.'
                    });
                }
            }, 1500);
        });
    }
}

// 싱글톤 인스턴스 export
export const studyService = new StudyService();
