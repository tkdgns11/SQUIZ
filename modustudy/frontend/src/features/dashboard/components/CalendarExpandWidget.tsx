import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { scheduleStore, Schedule, Goal, Tag } from '../services/scheduleStore';
import './CalendarExpandWidget.css';
import './ActivitySection.css';

export const CalendarExpandWidget = () => {
    const navigate = useNavigate();
    const [isAdding, setIsAdding] = useState(false);

    // 전역 스토어 상태 연동
    const [plannerState, setPlannerState] = useState(scheduleStore.getState());
    const { schedules, goals, tags, memo } = plannerState;

    const [newTitle, setNewTitle] = useState('');
    const [newGoal, setNewGoal] = useState('');
    const [tagInput, setTagInput] = useState('');
    const [selectedDate, setSelectedDate] = useState<string | null>(null);

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const [currentDate] = useState(new Date(2026, 0, 18));

    useEffect(() => {
        // 전역 스토어 구독
        const unsubscribe = scheduleStore.subscribe((newState) => {
            setPlannerState(newState);
        });
        return () => {
            unsubscribe();
        };
    }, []);

    const handleAddSchedule = () => {
        if (!newTitle.trim()) return;
        scheduleStore.addSchedule(newTitle);
        setNewTitle('');
        setIsAdding(false);
    };

    const handleAddGoal = (e: React.FormEvent) => {
        e.preventDefault();
        if (!newGoal.trim()) return;
        scheduleStore.addGoal(newGoal);
        setNewGoal('');
    };

    const handleAddTag = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && tagInput.trim()) {
            scheduleStore.addTag(tagInput.trim());
            setTagInput('');
        }
    };

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

    const calendarDays = getDaysInMonth(currentDate.getFullYear(), currentDate.getMonth());
    const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    const selectedSchedules = selectedDate ? schedules.filter(s => s.date === selectedDate) : [];

    return (
        <div className="planner-page">
            <header className="planner-header">
                <div className="header-left">
                    <button className="back-btn-transparent" onClick={() => navigate(-1)} title="대시보드로 돌아가기">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M19 12H5M12 19l-7-7 7-7" />
                        </svg>
                    </button>
                    <div className="planner-date-display">
                        <span className="year-label">{currentDate.getFullYear()}</span>
                        <span className="month-label">{currentDate.getMonth() + 1}월</span>
                    </div>
                </div>

                <div className="header-right action-group">
                    <button className="planner-action-btn primary" onClick={() => setIsAdding(!isAdding)}>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                            <line x1="12" y1="5" x2="12" y2="19"></line>
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                        </svg>
                        <span>일정 추가</span>
                    </button>
                    <button className="planner-action-btn secondary" onClick={() => scheduleStore.syncGoogleCalendar()}>
                        <img src="https://www.gstatic.com/images/branding/product/1x/calendar_2020q4_48dp.png" alt="G" />
                        <span>Google 연동</span>
                    </button>
                </div>
            </header>

            <main className="planner-content-grid">
                {/* 왼쪽 2: 달력 */}
                <section className="planner-calendar-area">
                    {isAdding && (
                        <div className="inline-add-form animate-slide-down">
                            <input
                                type="text"
                                placeholder="어떤 일정을 추가할까요?"
                                value={newTitle}
                                onChange={(e) => setNewTitle(e.target.value)}
                                autoFocus
                                onKeyDown={(e) => e.key === 'Enter' && handleAddSchedule()}
                            />
                            <div className="form-actions">
                                <button className="confirm-pill" onClick={handleAddSchedule}>저장</button>
                            </div>
                        </div>
                    )}
                    <div className="expanded-calendar-wrapper">
                        <div className="calendar-weekdays">
                            {weekDays.map(day => <div key={day} className="weekday large">{day}</div>)}
                        </div>
                        <div className="calendar-grid large">
                            {calendarDays.map((item, index) => {
                                const daySchedules = schedules.filter(s => s.date === item.fullDate);
                                const isToday = item.fullDate === '2026-01-18';

                                return (
                                    <div
                                        key={index}
                                        className={`calendar-day large ${item.month !== 'current' ? 'other-month' : ''} ${isToday ? 'today' : ''}`}
                                        onClick={() => setSelectedDate(item.fullDate)}
                                    >
                                        <span className="day-number">{item.day}</span>
                                        <div className="day-schedules">
                                            {daySchedules.map(schedule => (
                                                <div key={schedule.id} className={`schedule-item ${schedule.type} large`}>
                                                    {schedule.title}
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                </section>

                {/* 오른쪽 1: 학습 목표 및 메모 */}
                <aside className="planner-sidebar-area">
                    <div className="sidebar-widget dark-stone-texture">
                        <div className="goals-header">
                            <h3>오늘의 학습 목표</h3>
                            <span className="goal-date">01.18 Sun</span>
                        </div>

                        <form className="goal-input-form-dark" onSubmit={handleAddGoal}>
                            <input
                                type="text"
                                placeholder="새로운 목표 추가..."
                                value={newGoal}
                                onChange={(e) => setNewGoal(e.target.value)}
                            />
                            <button type="submit">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                                    <line x1="12" y1="5" x2="12" y2="19"></line>
                                    <line x1="5" y1="12" x2="19" y2="12"></line>
                                </svg>
                            </button>
                        </form>

                        <div className="goals-list-dark scroll-hide">
                            {goals.map(goal => (
                                <div
                                    key={goal.id}
                                    className={`goal-item-dark ${goal.completed ? 'completed' : ''}`}
                                    onClick={() => scheduleStore.toggleGoal(goal.id)}
                                >
                                    <div className="goal-checkbox-dark">
                                        {goal.completed && <span>✓</span>}
                                    </div>
                                    <span className="goal-text">{goal.text}</span>
                                </div>
                            ))}
                        </div>

                        <div className="goals-footer-dark">
                            <div className="progress-info">
                                <p>{goals.filter(g => g.completed).length} / {goals.length} Done</p>
                                <span>{goals.length > 0 ? Math.round((goals.filter(g => g.completed).length / goals.length) * 100) : 0}%</span>
                            </div>
                            <div className="progress-bar-dark">
                                <div
                                    className="progress-fill-dark"
                                    style={{ width: `${goals.length > 0 ? (goals.filter(g => g.completed).length / goals.length) * 100 : 0}%` }}
                                ></div>
                            </div>
                        </div>
                    </div>

                    <div className="sidebar-widget dark-stone-texture memo-area">
                        <div className="memo-header">
                            <h3>학습 메모</h3>
                            <div className="tag-list">
                                {tags.map(tag => (
                                    <span key={tag.id} className="tag-badge">
                                        #{tag.text}
                                        <button onClick={(e) => { e.stopPropagation(); scheduleStore.removeTag(tag.id); }}>×</button>
                                    </span>
                                ))}
                                <input
                                    className="tag-input"
                                    placeholder="#태그"
                                    value={tagInput}
                                    onChange={(e) => setTagInput(e.target.value)}
                                    onKeyDown={handleAddTag}
                                />
                            </div>
                        </div>
                        <textarea
                            placeholder="오늘의 인사이트를 기록하세요..."
                            value={memo}
                            onChange={(e) => scheduleStore.updateMemo(e.target.value)}
                        ></textarea>
                    </div>
                </aside>
            </main>

            {/* 일자 클릭 모달 */}
            {selectedDate && (
                <div className="day-select-overlay" onClick={() => setSelectedDate(null)}>
                    <div className="day-select-modal animate-modal-pop" onClick={e => e.stopPropagation()}>
                        <header className="modal-header-compact">
                            <div className="date-info">
                                <h2>{selectedDate.split('-')[1]}월 {selectedDate.split('-')[2]}일</h2>
                                <span>전체 일정 목록</span>
                            </div>
                            <button className="close-btn" onClick={() => setSelectedDate(null)}>×</button>
                        </header>
                        <div className="modal-schedule-list scroll-hide" style={{ maxHeight: '300px', overflowY: 'auto' }}>
                            {selectedSchedules.length > 0 ? (
                                selectedSchedules.map(s => (
                                    <div key={s.id} className={`modal-schedule-item ${s.type}`}>
                                        <div className="type-indicator"></div>
                                        <div className="item-content">
                                            <span className="item-title">{s.title}</span>
                                            <span className="item-type">{s.type.toUpperCase()}</span>
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <div className="empty-modal-state" style={{ textAlign: 'center', padding: '2rem', opacity: 0.5 }}>
                                    <p>등록된 일정이 없습니다.</p>
                                </div>
                            )}
                        </div>
                        <button className="modal-add-btn" onClick={() => { setSelectedDate(null); setIsAdding(true); }}>
                            + 새 일정 추가하기
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};
