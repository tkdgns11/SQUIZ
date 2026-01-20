import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import StudyListContainer from './StudyListContainer';
import StudyCardContent from './StudyCardContent';
import StudyFilter, { FilterState } from './StudyFilter';
import { studyService, Study, SortOption } from '../services/studyService';
import { MainLayout } from '@/layouts/MainLayout';
import './styles/StudyPage.css';

const StudyPage: React.FC = () => {
    const navigate = useNavigate();
    const [filteredStudies, setFilteredStudies] = useState<Study[]>([]);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [filters, setFilters] = useState<FilterState>({
        status: [],
        topic: [],
        meetingType: [],
        difficulty: [],
        studyType: [],
        regionId: [],
    });
    const [sortOption, setSortOption] = useState<SortOption>({
        field: 'createdAt',
        order: 'desc',
    });
    const [currentPage, setCurrentPage] = useState(1);
    const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid'); // 뷰 모드 상태
    const pageSize = 12;

    // 초기 데이터 로드
    useEffect(() => {
        const allStudies = studyService.getAllStudies();
        setFilteredStudies(allStudies);
    }, []);

    // 필터 및 검색 적용
    useEffect(() => {
        let result = studyService.getFilteredStudies({
            ...filters,
            keyword: searchKeyword,
        });

        // 정렬 적용
        result = studyService.sortStudies(result, sortOption);

        setFilteredStudies(result);
        setCurrentPage(1); // 필터 변경 시 첫 페이지로
    }, [filters, searchKeyword, sortOption]);

    // 필터 변경 핸들러
    const handleFilterChange = (newFilters: FilterState) => {
        setFilters(newFilters);
    };

    // 검색 핸들러
    const handleSearch = (keyword: string) => {
        setSearchKeyword(keyword);
    };

    // 정렬 변경 핸들러
    const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const value = e.target.value;
        switch (value) {
            case 'latest':
                setSortOption({ field: 'createdAt', order: 'desc' });
                break;
            case 'oldest':
                setSortOption({ field: 'createdAt', order: 'asc' });
                break;
            case 'popular':
                setSortOption({ field: 'currentMembers', order: 'desc' });
                break;
            case 'deadline':
                setSortOption({ field: 'recruitEndDate', order: 'asc' });
                break;
            default:
                setSortOption({ field: 'createdAt', order: 'desc' });
        }
    };

    // 찜하기 토글 핸들러
    const handleBookmarkToggle = (studyId: number) => {
        studyService.toggleBookmark(studyId);
        // 상태 업데이트
        setFilteredStudies((prev) =>
            prev.map((study) =>
                study.id === studyId ? { ...study, isBookmarked: !study.isBookmarked } : study
            )
        );
    };

    // 스터디 클릭 핸들러
    const handleStudyClick = (studyId: number) => {
        navigate(`/study/${studyId}`);
    };

    // 페이지네이션
    const paginatedData = studyService.paginateStudies(filteredStudies, currentPage, pageSize);

    return (
        <MainLayout>
            <StudyListContainer>
                <div className="study-page">
                    <div className="study-page-info">
                        <div className="difficulty-legend">
                            <span className="legend-item beginner">초급</span>
                            <span className="legend-item intermediate">중급</span>
                            <span className="legend-item advanced">고급</span>
                        </div>
                        <p className="study-count">
                            총 <strong>{filteredStudies.length}</strong>개의 스터디
                        </p>
                    </div>

                    {/* 필터 및 검색 */}
                    <StudyFilter onFilterChange={handleFilterChange} onSearch={handleSearch} />

                    {/* 통합 컨트롤 바 */}
                    <div className="study-controls">
                        <div className="view-toggle">
                            <button
                                className={`view-btn ${viewMode === 'grid' ? 'active' : ''}`}
                                onClick={() => setViewMode('grid')}
                                title="그리드 뷰"
                            >
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                                    <rect x="3" y="3" width="7" height="7" rx="1" />
                                    <rect x="14" y="3" width="7" height="7" rx="1" />
                                    <rect x="3" y="14" width="7" height="7" rx="1" />
                                    <rect x="14" y="14" width="7" height="7" rx="1" />
                                </svg>
                            </button>
                            <button
                                className={`view-btn ${viewMode === 'list' ? 'active' : ''}`}
                                onClick={() => setViewMode('list')}
                                title="리스트 뷰"
                            >
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                                    <line x1="8" y1="6" x2="21" y2="6" />
                                    <line x1="8" y1="12" x2="21" y2="12" />
                                    <line x1="8" y1="18" x2="21" y2="18" />
                                    <rect x="3" y="5" width="2" height="2" rx="0.5" />
                                    <rect x="3" y="11" width="2" height="2" rx="0.5" />
                                    <rect x="3" y="17" width="2" height="2" rx="0.5" />
                                </svg>
                            </button>
                        </div>

                        <div className="meeting-type-group">
                            <div className="meeting-type-tabs">
                                <button
                                    className={`tab-btn ${filters.meetingType.length === 0 ? 'active' : ''}`}
                                    onClick={() => handleFilterChange({ ...filters, meetingType: [] })}
                                >
                                    무관
                                </button>
                                <button
                                    className={`tab-btn ${filters.meetingType.includes('ONLINE') ? 'active' : ''}`}
                                    onClick={() => handleFilterChange({ ...filters, meetingType: ['ONLINE'] })}
                                >
                                    온라인
                                </button>
                                <button
                                    className={`tab-btn ${filters.meetingType.includes('OFFLINE') ? 'active' : ''}`}
                                    onClick={() => handleFilterChange({ ...filters, meetingType: ['OFFLINE'] })}
                                >
                                    오프라인
                                </button>
                                <button
                                    className={`tab-btn ${filters.meetingType.includes('HYBRID') ? 'active' : ''}`}
                                    onClick={() => handleFilterChange({ ...filters, meetingType: ['HYBRID'] })}
                                >
                                    혼합
                                </button>
                            </div>
                            <select
                                className="meeting-type-select"
                                value={filters.meetingType.length === 0 ? 'ALL' : filters.meetingType[0]}
                                onChange={(e) => {
                                    const val = e.target.value;
                                    handleFilterChange({
                                        ...filters,
                                        meetingType: val === 'ALL' ? [] : [val]
                                    });
                                }}
                            >
                                <option value="ALL">방식 무관</option>
                                <option value="ONLINE">온라인</option>
                                <option value="OFFLINE">오프라인</option>
                                <option value="HYBRID">혼합</option>
                            </select>
                        </div>

                        <div className="sort-wrapper">
                            <select
                                className="sort-select"
                                value={
                                    sortOption.field === 'createdAt' && sortOption.order === 'desc' ? 'latest' :
                                        sortOption.field === 'createdAt' && sortOption.order === 'asc' ? 'oldest' :
                                            sortOption.field === 'currentMembers' ? 'popular' :
                                                sortOption.field === 'recruitEndDate' ? 'deadline' : 'latest'
                                }
                                onChange={handleSortChange}
                            >
                                <option value="latest">최신순</option>
                                <option value="oldest">오래된순</option>
                                <option value="popular">인기순</option>
                                <option value="deadline">마감임박순</option>
                            </select>
                        </div>
                    </div>

                    {/* 스터디 목록 */}
                    {paginatedData.studies.length > 0 ? (
                        <>
                            <div className={`study-grid ${viewMode === 'list' ? 'list-view' : ''}`}>
                                {paginatedData.studies.map((study) => (
                                    <StudyCardContent
                                        key={study.id}
                                        study={study}
                                        onBookmarkToggle={handleBookmarkToggle}
                                        onClick={handleStudyClick}
                                    />
                                ))}
                            </div>

                            {/* 페이지네이션 */}
                            {paginatedData.totalPages > 1 && (
                                <div className="pagination">
                                    <button
                                        className="pagination-btn arrow"
                                        disabled={currentPage === 1}
                                        onClick={() => setCurrentPage((prev) => prev - 1)}
                                        title="이전 페이지"
                                    >
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="m15 18-6-6 6-6" />
                                        </svg>
                                    </button>

                                    <div className="pagination-numbers">
                                        {Array.from({ length: paginatedData.totalPages }, (_, i) => i + 1).map((pageNum) => (
                                            <button
                                                key={pageNum}
                                                className={`pagination-number ${currentPage === pageNum ? 'active' : ''}`}
                                                onClick={() => setCurrentPage(pageNum)}
                                            >
                                                {pageNum}
                                            </button>
                                        ))}
                                    </div>

                                    <button
                                        className="pagination-btn arrow"
                                        disabled={currentPage === paginatedData.totalPages}
                                        onClick={() => setCurrentPage((prev) => prev + 1)}
                                        title="다음 페이지"
                                    >
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                            <path d="m9 18 6-6-6-6" />
                                        </svg>
                                    </button>
                                </div>
                            )}
                        </>
                    ) : (
                        <div className="empty-state">
                            <p>검색 결과가 없습니다.</p>
                            <p className="empty-state-subtitle">다른 검색어나 필터를 시도해보세요.</p>
                        </div>
                    )}
                </div>
            </StudyListContainer>
        </MainLayout>
    );
};

export default StudyPage;
