import React from 'react';
import { Heart, Users, MapPin, Calendar, Clock, Award } from 'lucide-react';
import '../styles/StudyCardContent.css';

interface StudyCardContentProps {
    study: {
        id: number;
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
    };
    onBookmarkToggle?: (studyId: number) => void;
    onClick?: (studyId: number) => void;
}

const StudyCardContent: React.FC<StudyCardContentProps> = ({ study, onBookmarkToggle, onClick }) => {
    // 상태별 색상 클래스
    const getStatusClass = (status: string) => {
        switch (status) {
            case 'RECRUITING':
                return 'status-recruiting';
            case 'IN_PROGRESS':
                return 'status-in-progress';
            case 'COMPLETED':
                return 'status-completed';
            default:
                return '';
        }
    };

    // 상태별 텍스트
    const getStatusText = (status: string) => {
        switch (status) {
            case 'RECRUITING':
                return '모집중';
            case 'IN_PROGRESS':
                return '진행중';
            case 'COMPLETED':
                return '완료';
            default:
                return status;
        }
    };

    // 난이도별 색상 클래스
    const getDifficultyClass = (difficulty: string) => {
        switch (difficulty) {
            case 'BEGINNER':
            case 'ELEMENTARY':
                return 'difficulty-beginner';
            case 'INTERMEDIATE':
                return 'difficulty-intermediate';
            case 'ADVANCED':
                return 'difficulty-advanced';
            default:
                return '';
        }
    };

    // 난이도 텍스트
    const getDifficultyText = (difficulty: string) => {
        switch (difficulty) {
            case 'BEGINNER':
            case 'ELEMENTARY':
                return '초급';
            case 'INTERMEDIATE':
                return '중급';
            case 'ADVANCED':
                return '고급';
            default:
                return difficulty;
        }
    };

    // 미팅 타입 색상 클래스
    const getMeetingTypeClass = (meetingType: string) => {
        switch (meetingType) {
            case 'ONLINE':
                return 'meeting-online';
            case 'OFFLINE':
                return 'meeting-offline';
            case 'HYBRID':
                return 'meeting-hybrid';
            default:
                return '';
        }
    };

    // 미팅 타입 텍스트
    const getMeetingTypeText = (meetingType: string) => {
        switch (meetingType) {
            case 'ONLINE':
                return '온라인';
            case 'OFFLINE':
                return '오프라인';
            case 'HYBRID':
                return '혼합';
            default:
                return meetingType;
        }
    };

    // 요일 포맷팅
    const formatDays = (days: string) => {
        const dayMap: { [key: string]: string } = {
            MON: '월',
            TUE: '화',
            WED: '수',
            THU: '목',
            FRI: '금',
            SAT: '토',
            SUN: '일',
        };
        return days
            .split(',')
            .map((day) => dayMap[day.trim()] || day)
            .join(', ');
    };

    // D-day 계산
    const getDday = (endDate?: string) => {
        if (!endDate) return null;
        const today = new Date();
        const end = new Date(endDate);
        const diff = Math.ceil((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
        if (diff < 0) return null;
        return diff;
    };

    const dday = getDday(study.recruitEndDate);

    return (
        <div className="study-card" onClick={() => onClick?.(study.id)}>
            {/* 헤더 */}
            <div className="study-card-header">
                <div className="study-card-badges">
                    <span className={`badge badge-status ${getStatusClass(study.status)}`}>
                        {getStatusText(study.status)}
                    </span>
                    {dday !== null && dday <= 7 && (
                        <span className="badge badge-dday">D-{dday}</span>
                    )}
                    {study.studyType === 'LIGHTNING' && (
                        <span className="badge badge-lightning">⚡ 번개</span>
                    )}
                </div>
                <button
                    className={`bookmark-btn ${study.isBookmarked ? 'bookmarked' : ''}`}
                    onClick={(e) => {
                        e.stopPropagation();
                        onBookmarkToggle?.(study.id);
                    }}
                >
                    <Heart size={20} fill={study.isBookmarked ? 'currentColor' : 'none'} />
                </button>
            </div>

            {/* 제목 및 설명 */}
            <div className="study-card-content">
                <h3 className="study-card-title">{study.name}</h3>
                <p className="study-card-description">{study.description}</p>

                {/* 태그 */}
                <div className="study-card-tags">
                    <span className="tag tag-topic">{study.topic}</span>
                    <span className={`tag tag-difficulty ${getDifficultyClass(study.difficulty)}`}>
                        {getDifficultyText(study.difficulty)}
                    </span>
                    <span className={`tag tag-meeting ${getMeetingTypeClass(study.meetingType)}`}>
                        {getMeetingTypeText(study.meetingType)}
                    </span>
                </div>

                {/* 정보 */}
                <div className="study-card-info">
                    <div className="info-item">
                        <Users size={16} />
                        <span>
                            {study.currentMembers}/{study.maxMembers}명
                        </span>
                    </div>

                    {study.meetingType !== 'ONLINE' && study.region && (
                        <div className="info-item">
                            <MapPin size={16} />
                            <span>{study.region.name}</span>
                        </div>
                    )}

                    <div className="info-item">
                        <Calendar size={16} />
                        <span>{formatDays(study.scheduleDays)}</span>
                    </div>

                    {study.scheduleTime && (
                        <div className="info-item">
                            <Clock size={16} />
                            <span>{study.scheduleTime.substring(0, 5)}</span>
                        </div>
                    )}
                </div>

                {/* 스터디장 정보 */}
                <div className="study-card-leader">
                    <div className="leader-avatar">
                        {study.leader.profileImage ? (
                            <img src={study.leader.profileImage} alt={study.leader.nickname} />
                        ) : (
                            <div className="leader-avatar-placeholder">
                                {study.leader.nickname.charAt(0)}
                            </div>
                        )}
                    </div>
                    <div className="leader-info">
                        <span className="leader-name">{study.leader.nickname}</span>
                        <div className="leader-rating">
                            <Award size={14} />
                            <span>
                                {study.leader.leaderRating.toFixed(1)} ({study.leader.leaderReviewCount})
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StudyCardContent;
