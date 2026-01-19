import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { StartPage } from '../features/start/StartPage';
import { Dashboard, CalendarExpandWidget } from '../features/dashboard';
import { CommentlePage } from '../features/commentle/CommentlePage';
import { QuizPage } from '../features/quiz/QuizPage';
import { LoginPage, SignupPage } from '../features/auth/index';
import { RecruitmentPage } from '../features/recruitment/RecruitmentPage';
import { StudyPage, StudyCreatePage } from '../features/study';
import { TestSidebarPage } from '../features/test/TestSidebarPage';

export const AppRouter = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/startpage" element={<StartPage />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/calendar-expand" element={<CalendarExpandWidget />} />
                <Route path="/commentle" element={<CommentlePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/quiz" element={<QuizPage />} />
                <Route path="/study" element={<StudyPage />} />
                <Route path="/study/create" element={<StudyCreatePage />} />
                <Route path="/recruitment" element={<RecruitmentPage />} />
                <Route path="/test-sidebar" element={<TestSidebarPage />} />
                <Route path="/" element={
                    <div style={{ textAlign: "center", marginTop: "50px" }}>
                        <h1>Home</h1>
                        <a href="/startpage" className="text-study-blue hover:underline">Go to Start Page</a>
                        <br />
                        <a href="/dashboard" className="text-study-blue hover:underline">Go to Dashboard</a>
                        <br />
                        <a href="/test-sidebar" className="text-study-blue hover:underline">Test Sidebar</a>
                        <br />
                        <a href="/calendar-expand" className="text-study-blue hover:underline">Calendar (Expanded)</a>
                    </div>
                } />
            </Routes>
        </BrowserRouter>
    );
};
