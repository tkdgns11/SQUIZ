import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { scheduleStore, Schedule } from '../services/scheduleStore';
import './ActivitySection.css';

export const ActivitySection = () => {
    const navigate = useNavigate();
    const [currentDate] = useState(new Date(2026, 0, 18));
    const [schedules, setSchedules] = useState<Schedule[]>(scheduleStore.getState().schedules);

    useEffect(() => {
        // 전역 스토어 구독하여 실시간 연동 및 로컬 스토리지 반영 확인
        const unsubscribe = scheduleStore.subscribe((state) => {
            setSchedules(state.schedules);
        });
        return () => {
            unsubscribe();
        };
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

    return (
        <div className="activity-section">
            <div className="activity-combined-header">
                <div className="header-left">
                    <h2>Activity Logs</h2>
                    <span className="current-month-badge">
                        {currentDate.getFullYear()}년 {currentDate.getMonth() + 1}월
                    </span>
                </div>

                <div className="header-right">
                    <span className="monthly-count">
                        이번 달 일정 <strong>{monthlyScheduleCount}</strong>개
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
                    {weekDays.map(day => (
                        <div key={day} className="weekday">{day}</div>
                    ))}
                </div>

                <div className="calendar-grid">
                    {days.map((item, index) => {
                        const daySchedules = schedules.filter(s => s.date === item.fullDate);
                        const isToday = item.fullDate === '2026-01-18';

                        const displaySchedules = daySchedules.slice(0, 2);
                        const remainingCount = daySchedules.length - 2;

                        return (
                            <div
                                key={index}
                                className={`calendar-day ${item.month !== 'current' ? 'other-month' : ''} ${isToday ? 'today' : ''}`}
                            >
                                <span className="day-number">{item.day}</span>
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
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
};
