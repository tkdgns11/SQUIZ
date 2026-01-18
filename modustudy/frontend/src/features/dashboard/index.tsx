import { ActivitySection, StatsSection, FeedsSection, QuizWidget, QuizWidget2 } from './components';
import './styles/Dashboard.css';

export const Dashboard = () => {
    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h1>Dashboard</h1>
                <p>Welcome to ModuStudy!</p>
            </div>

            <div className="dashboard-grid">
                <ActivitySection />
                <StatsSection />
                <div className="quiz-daily">
                    <QuizWidget />
                </div>
                <div className="quiz-comp">
                    <QuizWidget2 />
                </div>
                <FeedsSection />
            </div>
        </div>
    );
};
