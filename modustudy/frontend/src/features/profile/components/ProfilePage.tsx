import { User, Mail, Calendar, Edit2 } from 'lucide-react';
import '../styles/ProfilePage.css';

export const ProfilePage = () => {
    // TODO: 실제 사용자 데이터는 API나 전역 상태에서 가져와야 함
    const userData = {
        name: '김싸피',
        email: 'ssafy@example.com',
        profileImage: null, // 프로필 이미지 URL
        joinDate: '2024.01.15',
        stats: {
            studyCount: 5,
            totalStudyTime: 127,
            quizScore: 85,
            attendance: 92,
        }
    };

    return (
        <div className="profile-page">
            <div className="profile-container">
                {/* 프로필 헤더 */}
                <div className="profile-header">
                    <div className="profile-avatar-wrapper">
                        {userData.profileImage ? (
                            <img
                                src={userData.profileImage}
                                alt="프로필 이미지"
                                className="profile-avatar"
                            />
                        ) : (
                            <div className="profile-avatar-placeholder">
                                <User size={48} />
                            </div>
                        )}
                        <button className="avatar-edit-btn" title="프로필 사진 변경">
                            <Edit2 size={16} />
                        </button>
                    </div>

                    <div className="profile-info">
                        <h1 className="profile-name">{userData.name}</h1>
                        <div className="profile-meta">
                            <span className="meta-item">
                                <Mail size={16} />
                                {userData.email}
                            </span>
                            <span className="meta-item">
                                <Calendar size={16} />
                                가입일: {userData.joinDate}
                            </span>
                        </div>
                    </div>

                    <button className="btn-edit-profile">
                        <Edit2 size={18} />
                        프로필 편집
                    </button>
                </div>

                {/* 학습 통계 - 8개 칩 (기존 4개 + Dashboard 4개) */}
                <div className="stats-section">
                    <h2 className="section-title">학습 통계</h2>
                    <div className="stats-grid-dashboard">
                        {/* 기존 통계 1: 참여 스터디 */}
                        <div className="stat-chip-dashboard">
                            <div className="stat-header-dashboard">
                                <span className="stat-icon-dashboard icon-study">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
                                    </svg>
                                </span>
                                <span className="stat-label-dashboard">참여 스터디</span>
                            </div>
                            <div className="stat-value-content-dashboard">
                                <span className="stat-value-dashboard">{userData.stats.studyCount}</span>
                                <span className="stat-unit-dashboard">개</span>
                            </div>
                        </div>

                        {/* 기존 통계 2: 총 학습 시간 */}
                        <div className="stat-chip-dashboard">
                            <div className="stat-header-dashboard">
                                <span className="stat-icon-dashboard icon-time">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
                                    </svg>
                                </span>
                                <span className="stat-label-dashboard">총 학습 시간</span>
                            </div>
                            <div className="stat-value-content-dashboard">
                                <span className="stat-value-dashboard">{userData.stats.totalStudyTime}</span>
                                <span className="stat-unit-dashboard">시간</span>
                            </div>
                        </div>

                        {/* 기존 통계 3: 평균 퀴즈 점수 */}
                        <div className="stat-chip-dashboard">
                            <div className="stat-header-dashboard">
                                <span className="stat-icon-dashboard icon-quiz">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                                    </svg>
                                </span>
                                <span className="stat-label-dashboard">평균 퀴즈 점수</span>
                            </div>
                            <div className="stat-value-content-dashboard">
                                <span className="stat-value-dashboard">{userData.stats.quizScore}</span>
                                <span className="stat-unit-dashboard">점</span>
                            </div>
                        </div>

                        {/* 기존 통계 4: 출석률 */}
                        <div className="stat-chip-dashboard">
                            <div className="stat-header-dashboard">
                                <span className="stat-icon-dashboard icon-attendance">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2" /><line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" /><line x1="3" y1="10" x2="21" y2="10" />
                                    </svg>
                                </span>
                                <span className="stat-label-dashboard">출석률</span>
                            </div>
                            <div className="stat-value-content-dashboard">
                                <span className="stat-value-dashboard">{userData.stats.attendance}</span>
                                <span className="stat-unit-dashboard">%</span>
                            </div>
                        </div>

                        {/* Dashboard 통계 1: 누적 열정 (불꽃) */}
                        <div className="stat-chip-dashboard">
                            <div className="stat-header-dashboard">
                                <span className="stat-icon-dashboard icon-passion">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z" />
                                    </svg>
                                </span>
                                <span className="stat-label-dashboard">누적 열정</span>
                            </div>
                            <div className="stat-value-content-dashboard">
                                <span className="stat-value-dashboard">2,450</span>
                                <span className="stat-unit-dashboard">FP</span>
                            </div>
                        </div>

                        {/* Dashboard 통계 2: 누적 출석수 */}
                        <div className="stat-chip-dashboard">
                            <div className="stat-header-dashboard">
                                <span className="stat-icon-dashboard icon-attendance">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" /><circle cx="9" cy="7" r="4" /><path d="M22 21v-2a4 4 0 0 0-3-3.87" /><path d="M16 3.13a4 4 0 0 1 0 7.75" />
                                    </svg>
                                </span>
                                <span className="stat-label-dashboard">누적 출석</span>
                            </div>
                            <div className="stat-value-content-dashboard">
                                <span className="stat-value-dashboard">42</span>
                                <span className="stat-unit-dashboard">일</span>
                            </div>
                        </div>

                        {/* Dashboard 통계 3: 누적 스터디 학습 시간 */}
                        <div className="stat-chip-dashboard">
                            <div className="stat-header-dashboard">
                                <span className="stat-icon-dashboard icon-study">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
                                    </svg>
                                </span>
                                <span className="stat-label-dashboard">스터디 학습</span>
                            </div>
                            <div className="stat-value-content-dashboard">
                                <span className="stat-value-dashboard">56</span>
                                <span className="stat-unit-dashboard">시간</span>
                            </div>
                        </div>

                        {/* Dashboard 통계 4: 퀴즈 푼 문제 누적 */}
                        <div className="stat-chip-dashboard">
                            <div className="stat-header-dashboard">
                                <span className="stat-icon-dashboard icon-time">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M9 11l3 3L22 4" /><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
                                    </svg>
                                </span>
                                <span className="stat-label-dashboard">퀴즈 해결</span>
                            </div>
                            <div className="stat-value-content-dashboard">
                                <span className="stat-value-dashboard">384</span>
                                <span className="stat-unit-dashboard">문제</span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* 내 스터디 활동 */}
                <div className="my-studies-section">
                    <h2 className="section-title">내 스터디 활동</h2>
                    <div className="studies-container">
                        <div className="study-activity-card">
                            <div className="study-activity-header">
                                <h3>알고리즘 스터디</h3>
                                <span className="study-status active">진행중</span>
                            </div>
                            <p className="study-activity-desc">매주 화, 목 오후 7시 | 온라인</p>
                            <div className="study-activity-stats">
                                <span>출석률: 95%</span>
                                <span>참여일: 42일</span>
                            </div>
                        </div>

                        <div className="study-activity-card">
                            <div className="study-activity-header">
                                <h3>React 프로젝트 스터디</h3>
                                <span className="study-status active">진행중</span>
                            </div>
                            <p className="study-activity-desc">매주 월, 수 오후 8시 | 오프라인</p>
                            <div className="study-activity-stats">
                                <span>출석률: 88%</span>
                                <span>참여일: 28일</span>
                            </div>
                        </div>

                        <div className="study-activity-card">
                            <div className="study-activity-header">
                                <h3>CS 기초 스터디</h3>
                                <span className="study-status completed">완료</span>
                            </div>
                            <p className="study-activity-desc">매주 토 오전 10시 | 온라인</p>
                            <div className="study-activity-stats">
                                <span>출석률: 100%</span>
                                <span>참여일: 60일</span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* 활동 잔디 (Activity Grass Graph) */}
                <div className="activity-grass-section">
                    <h2 className="section-title">
                        나의 활동 지수
                    </h2>
                    <div className="grass-container">
                        <div className="grass-track">
                            {Array.from({ length: 28 }).map((_, i) => (
                                <div
                                    key={i}
                                    className={`grass-node level-${Math.floor(Math.random() * 5)}`}
                                    title={`Activity level: ${Math.floor(Math.random() * 5)}`}
                                />
                            ))}
                        </div>
                        <p className="grass-description">
                            최근 한 달간의 스터디 참여 활동입니다.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};
