import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { LoadingFallback } from '../components';

// 즉시 로드: 랜딩 및 핵심 페이지
import { StartPage } from '../features/start/StartPage';
import { Dashboard, CalendarExpandWidget } from '../features/dashboard';

// Lazy 로드: 나머지 페이지들
const CommentlePage = lazy(() =>
    import('../features/commentle/CommentlePage').then(m => ({ default: m.CommentlePage }))
);
const QuizPage = lazy(() =>
    import('../features/quiz/QuizPage').then(m => ({ default: m.QuizPage }))
);
const LoginPage = lazy(() =>
    import('../features/auth/index').then(m => ({ default: m.LoginPage }))
);
const SignupPage = lazy(() =>
    import('../features/auth/index').then(m => ({ default: m.SignupPage }))
);
const LoginCallbackPage = lazy(() =>
    import('../features/auth/index').then(m => ({ default: m.LoginCallbackPage }))
);
const RecruitmentPage = lazy(() =>
    import('../features/recruitment/RecruitmentPage').then(m => ({ default: m.RecruitmentPage }))
);
const StudyPage = lazy(() =>
    import('../features/study').then(m => ({ default: m.StudyPage }))
);
const StudyCreatePage = lazy(() =>
    import('../features/study').then(m => ({ default: m.StudyCreatePage }))
);
const StudyDetailPage = lazy(() =>
    import('../features/study').then(m => ({ default: m.StudyDetailPage }))
);
const StudyManagementPage = lazy(() =>
    import('../features/study').then(m => ({ default: m.StudyManagementPage }))
);
const TestSidebarPage = lazy(() =>
    import('../features/test/TestSidebarPage').then(m => ({ default: m.TestSidebarPage }))
);
const ProfilePage = lazy(() =>
    import('../features/profile').then(m => ({ default: m.ProfilePage }))
);
const MeetingHistoryPage = lazy(() =>
    import('../features/meeting').then(m => ({ default: m.MeetingHistoryPage }))
);
const MeetingDetailPage = lazy(() =>
    import('../features/meeting').then(m => ({ default: m.MeetingDetailPage }))
);
const MeetingRoomPage = lazy(() =>
    import('../features/meeting').then(m => ({ default: m.MeetingRoomPage }))
);

export const AppRouter = () => {
    return (
        <BrowserRouter>
            <Suspense fallback={<LoadingFallback />}>
                <Routes>
                    {/* 즉시 로드 페이지 */}
                    <Route path="/" element={<StartPage />} />
                    <Route path="/startpage" element={<StartPage />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/calendar-expand" element={<CalendarExpandWidget />} />

                    {/* Lazy 로드 페이지 */}
                    <Route path="/commentle" element={<CommentlePage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/login/callback" element={<LoginCallbackPage />} />
                    <Route path="/signup" element={<SignupPage />} />
                    <Route path="/quiz" element={<QuizPage />} />
                    <Route path="/study" element={<StudyPage />} />
                    <Route path="/study/create" element={<StudyCreatePage />} />
                    <Route path="/study/:id" element={<StudyDetailPage />} />
                    <Route path="/study/manage/:id" element={<StudyManagementPage />} />
                    <Route path="/study/:studyId/meetings" element={<MeetingHistoryPage />} />
                    <Route path="/study/:studyId/meetings/:meetingId" element={<MeetingDetailPage />} />
                    <Route path="/study/:studyId/meetings/:meetingId/room" element={<MeetingRoomPage />} />
                    <Route path="/recruitment" element={<RecruitmentPage />} />
                    <Route path="/profile" element={<ProfilePage />} />
                    <Route path="/test-sidebar" element={<TestSidebarPage />} />
                </Routes>
            </Suspense>
        </BrowserRouter>
    );
};
