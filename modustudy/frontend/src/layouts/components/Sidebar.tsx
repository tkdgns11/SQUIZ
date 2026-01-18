import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useUIStore } from '@/store/uiStore';
import { QuizIcon, StudyIcon, DashboardIcon, CalendarIcon } from '@/shared/components/icons/SidebarIcons';

export const Sidebar = () => {
    const { isSidebarOpen } = useUIStore();

    return (
        <motion.aside
            className="h-screen bg-study-bg flex flex-col"
            initial={{ width: 280 }}
            animate={{ width: isSidebarOpen ? 280 : 64 }}
            transition={{ type: 'spring', stiffness: 300, damping: 30 }}
        >
            {/* 메뉴 아이템들 */}
            <nav className="flex-1 p-4 space-y-2">
                {/* 퀴즈 메뉴 */}
                <SidebarItem
                    icon={<QuizIcon />}
                    label="퀴즈"
                    isOpen={isSidebarOpen}
                    badge={5}
                    badgeColor="study-teal"
                />

                {/* 스터디 메뉴 */}
                <SidebarItem
                    icon={<StudyIcon />}
                    label="스터디"
                    isOpen={isSidebarOpen}
                    showDot
                    dotColor="study-green"
                />

                {/* 대시보드 */}
                <SidebarItem
                    icon={<DashboardIcon />}
                    label="대시보드"
                    isOpen={isSidebarOpen}
                />

                {/* 캘린더 */}
                <SidebarItem
                    icon={<CalendarIcon />}
                    label="캘린더"
                    isOpen={isSidebarOpen}
                />
            </nav>
        </motion.aside>
    );
};

// 사이드바 아이템 컴포넌트
interface SidebarItemProps {
    icon: React.ReactNode;
    label: string;
    isOpen: boolean;
    badge?: number;
    badgeColor?: string;
    showDot?: boolean;
    dotColor?: string;
}

const SidebarItem: React.FC<SidebarItemProps> = ({
    icon,
    label,
    isOpen,
    badge,
    badgeColor = 'study-blue',
    showDot,
    dotColor = 'study-green',
}) => {
    const [isHovered, setIsHovered] = useState(false);

    return (
        <motion.button
            className="w-full flex items-center gap-3 p-3 rounded-google hover:bg-study-blue/10 transition-colors relative group"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onHoverStart={() => setIsHovered(true)}
            onHoverEnd={() => setIsHovered(false)}
        >
            {/* 라벨 */}
            {isOpen && (
                <motion.span
                    className="text-study-text font-medium flex-1 text-left"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                >
                    {label}
                </motion.span>
            )}

            {/* 호버 시 오른쪽에 나타나는 아이콘 */}
            {isOpen && (
                <motion.div
                    className="text-study-blue/40"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: isHovered ? 1 : 0 }}
                    transition={{ duration: 0.2 }}
                >
                    {icon}
                </motion.div>
            )}

            {/* 배지 */}
            {isOpen && badge !== undefined && !isHovered && (
                <motion.span
                    className={`px-2 py-0.5 rounded-full text-xs font-semibold text-white bg-${badgeColor}`}
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                >
                    {badge}
                </motion.span>
            )}

            {/* 활성 상태 점 */}
            {showDot && !isHovered && (
                <motion.div
                    className={`absolute right-2 top-2 w-2 h-2 rounded-full bg-${dotColor}`}
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                />
            )}
        </motion.button>
    );
};
