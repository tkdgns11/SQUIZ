// 헤더 + 사이드바 포함 기본 레이아웃

import { motion } from 'framer-motion';
import { Sidebar } from './components/Sidebar';
import { RightSideBar } from './components/RightSideBar';
import { useUIStore } from '@/store/uiStore';
import { useAuthStore } from '@/store/authStore';
import { SquizLogo } from '@/shared/components/SquizLogo';
import { Link } from 'react-router-dom';

interface MainLayoutProps {
    children: React.ReactNode;
}

export const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
    const { isSidebarOpen, toggleSidebar, activeRightTab } = useUIStore();
    const { isLoggedIn, user, logout } = useAuthStore();

    return (
        <div className="flex flex-col h-screen bg-study-bg overflow-hidden">
            {/* 헤더 - 상단 100% 너비 */}
            <header className="h-16 w-full bg-study-bg flex items-center justify-between px-6 flex-shrink-0 z-50">
                <div className="flex items-center gap-4">
                    {/* 사이드바 토글 버튼 */}
                    <button
                        onClick={toggleSidebar}
                        className="p-2 rounded-google hover:bg-study-blue/10 transition-colors"
                        aria-label="Toggle sidebar"
                    >
                        <span className="material-icons text-study-blue">
                            {isSidebarOpen ? 'menu_open' : 'menu'}
                        </span>
                    </button>

                    {/* 로고 영역 */}
                    <Link to="/dashboard" className="flex items-center">
                        <SquizLogo width={120} height={40} className="scale-125 origin-left" />
                    </Link>
                </div>

                {/* 우측 인증 영역 (친구, DM 버튼 삭제됨) */}
                <div className="flex items-center gap-3 pr-14 h-full">
                    <div className="w-px h-6 bg-study-blue/20 mx-1" />

                    {isLoggedIn ? (
                        <div className="flex items-center gap-3 ml-2">
                            <div className="flex items-center gap-2 p-1.5 px-3 bg-white/50 rounded-full border border-study-blue/10 backdrop-blur-sm">
                                <div className="w-7 h-7 rounded-full bg-gradient-to-br from-study-blue to-study-blue-dark flex items-center justify-center text-white text-xs font-bold overflow-hidden">
                                    {user?.avatar ? <img src={user.avatar} alt="P" className="w-full h-full object-cover" /> : user?.name.charAt(0)}
                                </div>
                                <span className="text-sm font-bold text-study-text-dark">{user?.name}님</span>
                            </div>
                            <button
                                onClick={logout}
                                className="p-2 px-4 bg-gray-100 hover:bg-gray-200 text-gray-600 rounded-pill text-xs font-bold transition-all hover:scale-105 active:scale-95"
                            >
                                로그아웃
                            </button>
                        </div>
                    ) : (
                        <Link
                            to="/login"
                            className="ml-2 p-2 px-5 bg-study-blue hover:bg-study-blue-dark text-white rounded-pill text-sm font-bold shadow-md shadow-study-blue/20 transition-all hover:scale-105 active:scale-95 no-underline"
                        >
                            로그인 / 회원가입
                        </Link>
                    )}
                </div>
            </header>

            {/* 사이드바 + 메인 콘텐츠 + 우측 사이드바 */}
            <div className="flex flex-1 overflow-hidden relative">
                {/* 왼쪽 사이드바 */}
                <Sidebar />

                {/* 메인 콘텐츠 영역 */}
                <motion.main
                    className="flex-1 flex flex-col overflow-hidden"
                    layout
                    transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                >
                    {/* 페이지 콘텐츠 */}
                    <div className={`flex-1 overflow-auto pt-2 pb-6 bg-study-bg transition-all duration-300 ${isSidebarOpen ? 'pl-6' : 'pl-0'
                        } ${activeRightTab ? 'pr-80' : 'pr-14'
                        }`}>
                        {children}
                    </div>
                </motion.main>

                {/* 우측 사이드바 (아이콘바 + 패널) */}
                <RightSideBar />
            </div>
        </div>
    );
};
