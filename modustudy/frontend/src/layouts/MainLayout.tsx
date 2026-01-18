// 헤더 + 사이드바 포함 기본 레이아웃

import { motion } from 'framer-motion';
import { Sidebar } from './components/Sidebar';
import { useUIStore } from '@/store/uiStore';

interface MainLayoutProps {
    children: React.ReactNode;
}

export const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
    const { isSidebarOpen, toggleSidebar } = useUIStore();

    return (
        <div className="flex h-screen bg-study-bg overflow-hidden">
            {/* 사이드바 */}
            <Sidebar />

            {/* 메인 콘텐츠 영역 */}
            <motion.main
                className="flex-1 flex flex-col overflow-hidden"
                layout
                transition={{ type: 'spring', stiffness: 300, damping: 30 }}
            >
                {/* 헤더 */}
                <header className="h-16 bg-study-bg flex items-center px-6 gap-4">
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

                    {/* 헤더 타이틀 */}
                    <h1 className="text-xl font-bold text-study-text">
                        SQUIZ Dashboard
                    </h1>
                </header>

                {/* 페이지 콘텐츠 */}
                <div className="flex-1 overflow-auto p-6 bg-study-bg">
                    {children}
                </div>
            </motion.main>
        </div>
    );
};
