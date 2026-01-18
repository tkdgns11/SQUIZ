import React, { useState } from 'react';
import './ActivitySection.css';

// 일정 데이터 인터페이스
interface Schedule {
    id: number;
    date: string; // YYYY-MM-DD
    title: string;
    type: 'study' | 'project' | 'mentoring';
}

// Mock 일정 데이터 (사용자 할당 일정)
const mockSchedules: Schedule[] = [
    { id: 1, date: '2026-01-18', title: '알고리즘 스터디', type: 'study' },
    { id: 2, date: '2026-01-20', title: 'React 프로젝트 회의', type: 'project' },
    { id: 3, date: '2026-01-22', title: '멘토링 세션', type: 'mentoring' },
    { id: 4, date: '2026-01-25', title: 'TypeScript 심화 강의', type: 'study' },
    { id: 5, date: '2026-01-18', title: '개인 학습 시간', type: 'study' },
];

export const ActivitySection = () => {
    const [currentDate, setCurrentDate] = useState(new Date(2026, 0, 18)); // 2026년 1월로 고정 (Mock)

    // 캘린더 날짜 계산 로직
    const getDaysInMonth = (year: number, month: number) => {
        const date = new Date(year, month, 1);
        const days = [];
        // 시작 요일 맞추기 (이전 달 날짜들)
        const firstDayIndex = date.getDay();
        for (let i = firstDayIndex; i > 0; i--) {
            const prevDate = new Date(year, month, 1 - i);
            days.push({ day: prevDate.getDate(), month: 'prev', fullDate: prevDate.toISOString().split('T')[0] });
        }
        // 이번 달 날짜들
        while (date.getMonth() === month) {
            days.push({ day: date.getDate(), month: 'current', fullDate: new Date(date).toISOString().split('T')[0] });
            date.setDate(date.getDate() + 1);
        }
        return days;
    };

    const days = getDaysInMonth(currentDate.getFullYear(), currentDate.getMonth());
    const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

    // 이번 달 총 일정 개수 계산 (Mock 데이터 기반)
    const currentMonthStr = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}`;
    const monthlyScheduleCount = mockSchedules.filter(s => s.date.startsWith(currentMonthStr)).length;

    return (
        <div className="activity-section">
            <div className="activity-header-wrapper">
                <h2>Activity Logs</h2>
                <span className="monthly-count">
                    이번 달 일정 <strong>{monthlyScheduleCount}</strong>개
                </span>
            </div>

            <div className="calendar-container">
                {/* 캘린더 헤더 */}
                <div className="calendar-header">
                    <span className="current-month">
                        {currentDate.toLocaleString('default', { month: 'long' })} {currentDate.getFullYear()}
                    </span>
                    <div className="calendar-nav">
                        <button className="nav-btn">&lt;</button>
                        <button className="nav-btn">&gt;</button>
                    </div>
                </div>

                {/* 요일 */}
                <div className="calendar-weekdays">
                    {weekDays.map(day => (
                        <div key={day} className="weekday">{day}</div>
                    ))}
                </div>

                {/* 날짜 그리드 */}
                <div className="calendar-grid">
                    {days.map((item, index) => {
                        const daySchedules = mockSchedules.filter(s => s.date === item.fullDate);
                        const isToday = item.fullDate === '2026-01-18'; // Mock Today

                        // 최대 2개까지만 표시하고 나머지는 "+N개 더보기"로 처리
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
