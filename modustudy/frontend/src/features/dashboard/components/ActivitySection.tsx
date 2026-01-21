import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { scheduleStore, Schedule } from '../services/scheduleStore';
import './ActivitySection.css';

export const ActivitySection = () => {
    const navigate = useNavigate();
    const [currentDate] = useState(new Date(2026, 0, 18));
    const [schedules, setSchedules] = useState<Schedule[]>(scheduleStore.getState().schedules);
    const [selectedDate, setSelectedDate] = useState<string | null>(null); // 선택된 날짜
    const [windowWidth, setWindowWidth] = useState(window.innerWidth); // 화면 크기

    useEffect(() => {
        // 전역 스토어 구독하여 실시간 연동 및 로컬 스토리지 반영 확인
        const unsubscribe = scheduleStore.subscribe((state) => {
            setSchedules(state.schedules);
        });
        return () => {
            unsubscribe();
        };
    }, []);

    // 윈도우 리사이즈 감지
    useEffect(() => {
        const handleResize = () => setWindowWidth(window.innerWidth);
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    const getDaysInMonth = (year: number, month: number) => {
        const date = new Date(year, month, 1);
        const days = [];
        const firstDayIndex = date.getDay();
        for (let i = firstDayIndex; i > 0; i--) {
            const prevDate = new Date(year, month, 1 - i);
            days.push({ day: prevDate.getDate(), month: 'prev', fullDate: prevDate.toISOString().split('T')[0] });
        }
        while (date.getMonth() === month) {
            days.push({ day: date.getDate(), month: 'current', fullDate: new Date(date).toISOString().split('T')[0] });
            date.setDate(date.getDate() + 1);
        }
        return days;
    };

    const days = getDaysInMonth(currentDate.getFullYear(), currentDate.getMonth());
    const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

    const currentMonthStr = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}`;
    const monthlyScheduleCount = schedules.filter(s => s.date.startsWith(currentMonthStr)).length;

    const isCompactMode = windowWidth <= 1000; // 1000px 이하면 컴팩트 모드

    // 날짜 클릭 핸들러
    const handleDayClick = (dateStr: string) => {
        if (isCompactMode) {
            setSelectedDate(dateStr);
        }
    };

    const selectedSchedules = selectedDate ? schedules.filter(s => s.date === selectedDate) : [];

    return (
        <div className="activity-section">
            <div className="activity-combined-header">
                <div className="header-left">
                    {!isCompactMode && <h2>이번달 일정</h2>}
                    {!isCompactMode && (
                        <span className="current-month-badge">
                            {currentDate.getFullYear()}년 {currentDate.getMonth() + 1}월
                        </span>
                    )}
                </div>

                <div className="header-right">
                    <span className="monthly-count">
                        {isCompactMode ? `이번달 일정 ${monthlyScheduleCount}개` : `이번 달 일정 `}
                        {!isCompactMode && <strong>{monthlyScheduleCount}</strong>}
                        {!isCompactMode && '개'}
                    </span>
                    <button
                        className="maximize-btn-alt"
                        onClick={() => navigate('/calendar-expand')}
                        title="크게 보기"
                    >
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7" />
                        </svg>
                    </button>
                </div>
            </div>

            <div className="calendar-container">
                <div className="calendar-weekdays">
                    {weekDays.map((day, idx) => (
                        <div key={day} className={`weekday ${idx === 0 ? 'is-sun' : idx === 6 ? 'is-sat' : ''}`}>{day}</div>
                    ))}
                </div>

                <div className="calendar-grid">
                    {days.map((item, index) => {
                        const daySchedules = schedules.filter(s => s.date === item.fullDate);
                        const isToday = item.fullDate === '2026-01-18';
                        const dayOfWeek = new Date(item.fullDate).getDay();

                        const displaySchedules = daySchedules.slice(0, 2);
                        const remainingCount = daySchedules.length - 2;

                        return (
                            <div
                                key={index}
                                className={`calendar-day ${item.month !== 'current' ? 'other-month' : ''} ${isToday ? 'today' : ''} ${dayOfWeek === 0 ? 'is-sun' : dayOfWeek === 6 ? 'is-sat' : ''} ${isCompactMode ? 'compact-mode' : ''}`}
                                onClick={() => handleDayClick(item.fullDate)}
                                style={{ cursor: isCompactMode && daySchedules.length > 0 ? 'pointer' : 'default' }}
                            >
                                <span className="day-number">{item.day}</span>

                                {/* 700px 이하: 점으로만 표시 */}
                                {isCompactMode && daySchedules.length > 0 && (
                                    <div className="schedule-dots">
                                        {daySchedules.slice(0, 3).map((schedule, idx) => (
                                            <span key={idx} className={`schedule-dot ${schedule.type}`}></span>
                                        ))}
                                        {daySchedules.length > 3 && <span className="dot-more">+{daySchedules.length - 3}</span>}
                                    </div>
                                )}

                                {/* 700px 초과: 일정 텍스트 표시 */}
                                {!isCompactMode && (
                                    <div className="day-schedules">
                                        {displaySchedules.map(schedule => (
                                            <div
                                                key={schedule.id}
                                                className={`schedule-item ${schedule.type}`}
                                                title={schedule.title}
                                            >
                                                {schedule.title}
                                            </div>
                                        ))}
                                        {remainingCount > 0 && (
                                            <div className="more-schedules">
                                                +{remainingCount} more
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>
            </div>

            {/* 날짜 상세 모달 (컴팩트 모드 전용) */}
            {selectedDate && isCompactMode && (
                <div className="day-detail-overlay" onClick={() => setSelectedDate(null)}>
                    <div className="day-detail-modal" onClick={e => e.stopPropagation()}>
                        <header className="modal-header">
                            <h3>{selectedDate.split('-')[1]}월 {selectedDate.split('-')[2]}일 일정</h3>
                            <button className="close-btn" onClick={() => setSelectedDate(null)}>×</button>
                        </header>
                        <div className="modal-schedule-list">
                            {selectedSchedules.length > 0 ? (
                                selectedSchedules.map(s => (
                                    <div key={s.id} className={`modal-schedule-item ${s.type}`}>
                                        <div className="type-indicator"></div>
                                        <span>{s.title}</span>
                                    </div>
                                ))
                            ) : (
                                <p className="empty-state">등록된 일정이 없습니다.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};
