import { useNavigate } from 'react-router-dom';
import './QuizWidget.css';

export const QuizWidget2 = () => {
    const navigate = useNavigate();

    return (
        <div className="quiz-action-widget" onClick={() => navigate('/quiz')}>
            <div className="quiz-action-content">
                <div className="action-info">
                    <div className="title-wrapper">
                        <h3>온라인 퀴즈 대회</h3>
                        <span className="live-badge">LIVE</span>
                    </div>
                    <p>실시간 퀴즈 배틀</p>
                </div>

                {/* 하단 투명 화살표 버튼 */}
                <div className="widget-action-footer">
                    <div className="arrow-circle">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                            <polyline points="9 18 15 12 9 6" />
                        </svg>
                    </div>
                </div>
            </div>
        </div>
    );
};
