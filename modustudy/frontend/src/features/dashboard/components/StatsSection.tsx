import './StatsSection.css';

interface StatChipProps {
    label: string;
    value: string | number;
    unit: string;
    icon: React.ReactNode;
    colorClass: string;
}

const StatChip = ({ label, value, unit, icon, colorClass }: StatChipProps) => (
    <div className="stat-chip">
        <div className="stat-header">
            <span className={`stat-icon ${colorClass}`}>
                {icon}
            </span>
            <span className="stat-label">{label}</span>
        </div>
        <div className="stat-value-content">
            <span className="stat-value">{value}</span>
            <span className="stat-unit">{unit}</span>
        </div>
    </div>
);

export const StatsSection = () => {
    return (
        <div className="stats-section">
            <h2>Study Statistics</h2>

            <div className="stats-grid">
                {/* 누적 열정 (불꽃) */}
                <StatChip
                    label="누적 열정"
                    value="2,450"
                    unit="FP"
                    colorClass="icon-passion"
                    icon={
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z" />
                        </svg>
                    }
                />

                {/* 누적 출석수 */}
                <StatChip
                    label="누적 출석"
                    value="42"
                    unit="일"
                    colorClass="icon-attendance"
                    icon={
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" /><circle cx="9" cy="7" r="4" /><path d="M22 21v-2a4 4 0 0 0-3-3.87" /><path d="M16 3.13a4 4 0 0 1 0 7.75" />
                        </svg>
                    }
                />

                {/* 누적 스터디 학습 시간 */}
                <StatChip
                    label="스터디 학습"
                    value="56"
                    unit="시간"
                    colorClass="icon-study"
                    icon={
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
                        </svg>
                    }
                />

                {/* 퀴즈 푼 문제 누적 */}
                <StatChip
                    label="퀴즈 해결"
                    value="384"
                    unit="문제"
                    colorClass="icon-time"
                    icon={
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M9 11l3 3L22 4" /><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
                        </svg>
                    }
                />
            </div>
        </div>
    );
};
