import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { StartPage } from '../features/start/StartPage';
import { Dashboard, CalendarExpandWidget } from '../features/dashboard';
import { CommentlePage } from '../features/commentle/CommentlePage';
import { QuizPage } from '../features/quiz/QuizPage';
import { LoginPage, SignupPage, LoginCallbackPage } from '../features/auth/index';
import { RecruitmentPage } from '../features/recruitment/RecruitmentPage';
import { StudyPage, StudyCreatePage, StudyDetailPage, StudyManagementPage } from '../features/study';
import { TestSidebarPage } from '../features/test/TestSidebarPage';
import { ProfilePage } from '../features/profile';

export const AppRouter = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/startpage" element={<StartPage />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/calendar-expand" element={<CalendarExpandWidget />} />
                <Route path="/commentle" element={<CommentlePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/login/callback" element={<LoginCallbackPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/quiz" element={<QuizPage />} />
                <Route path="/study" element={<StudyPage />} />
                <Route path="/study/create" element={<StudyCreatePage />} />
                <Route path="/study/:id" element={<StudyDetailPage />} />
                <Route path="/study/manage/:id" element={<StudyManagementPage />} />
                <Route path="/recruitment" element={<RecruitmentPage />} />
                <Route path="/profile" element={<ProfilePage />} />
                <Route path="/test-sidebar" element={<TestSidebarPage />} />
                <Route path="/" element={<StartPage />} />
            </Routes>
        </BrowserRouter>
    );
};
