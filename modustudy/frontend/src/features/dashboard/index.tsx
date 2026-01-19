import { ActivitySection, StatsSection, FeedsSection, QuizWidget, QuizWidget2, CalendarExpandWidget } from './components';
import './styles/Dashboard.css';
import { MainLayout } from '@/layouts/MainLayout';

export { CalendarExpandWidget };
export const Dashboard = () => {
    return (
        <MainLayout>
            <div className="dashboard-container">

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
        </MainLayout>
    );
};
