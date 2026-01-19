import React, { useState } from 'react';
import { Search, SlidersHorizontal, X } from 'lucide-react';
import '../styles/StudyFilter.css';

interface StudyFilterProps {
    onFilterChange: (filters: FilterState) => void;
    onSearch: (keyword: string) => void;
}

export interface FilterState {
    status: string[];
    topic: string[];
    meetingType: string[];
    difficulty: string[];
    studyType: string[];
    regionId: number[];
}

const StudyFilter: React.FC<StudyFilterProps> = ({ onFilterChange, onSearch }) => {
    const [searchKeyword, setSearchKeyword] = useState('');
    const [showFilters, setShowFilters] = useState(false);
    const [filters, setFilters] = useState<FilterState>({
        status: [],
        topic: [],
        meetingType: [],
        difficulty: [],
        studyType: [],
        regionId: [],
    });

    // 필터 옵션 정의
    const filterOptions = {
        status: [
            { value: 'RECRUITING', label: '모집중' },
            { value: 'IN_PROGRESS', label: '진행중' },
            { value: 'COMPLETED', label: '완료' },
        ],
        topic: [
            { value: '알고리즘', label: '알고리즘' },
            { value: 'CS', label: 'CS' },
            { value: '백엔드', label: '백엔드' },
            { value: '프론트엔드', label: '프론트엔드' },
            { value: '데이터베이스', label: '데이터베이스' },
            { value: '데이터 분석', label: '데이터 분석' },
            { value: '프로그래밍 기초', label: '프로그래밍 기초' },
        ],
        meetingType: [
            { value: 'ONLINE', label: '온라인' },
            { value: 'OFFLINE', label: '오프라인' },
            { value: 'HYBRID', label: '혼합' },
        ],
        difficulty: [
            { value: 'BEGINNER', label: '초급' },
            { value: 'INTERMEDIATE', label: '중급' },
            { value: 'ADVANCED', label: '고급' },
        ],
        studyType: [
            { value: 'PLANNED', label: '정규' },
            { value: 'LIGHTNING', label: '번개' },
        ],
    };

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchKeyword(e.target.value);
    };

    const handleSearchSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSearch(searchKeyword);
    };

    const handleFilterToggle = (category: keyof FilterState, value: string | number) => {
        setFilters((prev) => {
            const categoryFilters = prev[category] as any[];
            const newFilters = categoryFilters.includes(value)
                ? categoryFilters.filter((v) => v !== value)
                : [...categoryFilters, value];

            const updatedFilters = {
                ...prev,
                [category]: newFilters,
            };

            onFilterChange(updatedFilters);
            return updatedFilters;
        });
    };

    const clearFilters = () => {
        const emptyFilters: FilterState = {
            status: [],
            topic: [],
            meetingType: [],
            difficulty: [],
            studyType: [],
            regionId: [],
        };
        setFilters(emptyFilters);
        onFilterChange(emptyFilters);
    };

    const getActiveFilterCount = () => {
        return Object.values(filters).reduce((acc, curr) => acc + curr.length, 0);
    };

    return (
        <div className="study-filter">
            {/* 검색 및 필터 버튼 행 */}
            <div className="filter-row">
                {/* 검색바 */}
                <form className="search-bar" onSubmit={handleSearchSubmit}>
                    <Search size={20} className="search-icon" />
                    <input
                        type="text"
                        placeholder="스터디 검색..."
                        value={searchKeyword}
                        onChange={handleSearchChange}
                        className="search-input"
                    />
                    {searchKeyword && (
                        <button
                            type="button"
                            className="clear-search-btn"
                            onClick={() => {
                                setSearchKeyword('');
                                onSearch('');
                            }}
                        >
                            <X size={18} />
                        </button>
                    )}
                </form>

                {/* 필터 토글 버튼 */}
                <button className="filter-toggle-btn" onClick={() => setShowFilters(!showFilters)}>
                    <SlidersHorizontal size={20} />
                    <span>필터</span>
                    {getActiveFilterCount() > 0 && (
                        <span className="filter-count">{getActiveFilterCount()}</span>
                    )}
                </button>
            </div>

            {/* 필터 패널 */}
            {showFilters && (
                <div className="filter-panel">
                    <div className="filter-header">
                        <h3>필터</h3>
                        {getActiveFilterCount() > 0 && (
                            <button className="clear-filters-btn" onClick={clearFilters}>
                                초기화
                            </button>
                        )}
                    </div>

                    <div className="filter-sections">
                        {/* 모집 상태 */}
                        <div className="filter-section">
                            <h4 className="filter-section-title">모집 상태</h4>
                            <div className="filter-options">
                                {filterOptions.status.map((option) => (
                                    <label key={option.value} className="filter-option">
                                        <input
                                            type="checkbox"
                                            checked={filters.status.includes(option.value)}
                                            onChange={() => handleFilterToggle('status', option.value)}
                                        />
                                        <span>{option.label}</span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        {/* 주제 */}
                        <div className="filter-section">
                            <h4 className="filter-section-title">주제</h4>
                            <div className="filter-options">
                                {filterOptions.topic.map((option) => (
                                    <label key={option.value} className="filter-option">
                                        <input
                                            type="checkbox"
                                            checked={filters.topic.includes(option.value)}
                                            onChange={() => handleFilterToggle('topic', option.value)}
                                        />
                                        <span>{option.label}</span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        {/* 미팅 타입 */}
                        <div className="filter-section">
                            <h4 className="filter-section-title">미팅 방식</h4>
                            <div className="filter-options">
                                {filterOptions.meetingType.map((option) => (
                                    <label key={option.value} className="filter-option">
                                        <input
                                            type="checkbox"
                                            checked={filters.meetingType.includes(option.value)}
                                            onChange={() => handleFilterToggle('meetingType', option.value)}
                                        />
                                        <span>{option.label}</span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        {/* 난이도 */}
                        <div className="filter-section">
                            <h4 className="filter-section-title">난이도</h4>
                            <div className="filter-options">
                                {filterOptions.difficulty.map((option) => (
                                    <label key={option.value} className="filter-option">
                                        <input
                                            type="checkbox"
                                            checked={filters.difficulty.includes(option.value)}
                                            onChange={() => handleFilterToggle('difficulty', option.value)}
                                        />
                                        <span>{option.label}</span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        {/* 스터디 타입 */}
                        <div className="filter-section">
                            <h4 className="filter-section-title">스터디 타입</h4>
                            <div className="filter-options">
                                {filterOptions.studyType.map((option) => (
                                    <label key={option.value} className="filter-option">
                                        <input
                                            type="checkbox"
                                            checked={filters.studyType.includes(option.value)}
                                            onChange={() => handleFilterToggle('studyType', option.value)}
                                        />
                                        <span>{option.label}</span>
                                    </label>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default StudyFilter;
