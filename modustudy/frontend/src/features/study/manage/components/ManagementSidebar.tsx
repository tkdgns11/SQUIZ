import React from 'react';
import { Users, UserCheck, ClipboardCheck, ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface ManagementSidebarProps {
    studyId: number;
    activeTab: 'applicants' | 'members' | 'attendance';
    setActiveTab: (tab: 'applicants' | 'members' | 'attendance') => void;
}

const ManagementSidebar: React.FC<ManagementSidebarProps> = ({ studyId, activeTab, setActiveTab }) => {
    const navigate = useNavigate();

    const menuItems = [
        { id: 'applicants', label: '신청자 관리', icon: <UserCheck size={20} /> },
        { id: 'members', label: '멤버 관리', icon: <Users size={20} /> },
        { id: 'attendance', label: '출석 관리', icon: <ClipboardCheck size={20} /> },
    ];

    return (
        <aside className="management-sidebar">
            <button className="btn-back-to-study" onClick={() => navigate(`/study/${studyId}`)}>
                <ArrowLeft size={18} />
                <span>스터디로 돌아가기</span>
            </button>

            <div className="sidebar-menu">
                {menuItems.map((item) => (
                    <button
                        key={item.id}
                        className={`menu-item ${activeTab === item.id ? 'active' : ''}`}
                        onClick={() => setActiveTab(item.id as any)}
                    >
                        {item.icon}
                        <span>{item.label}</span>
                    </button>
                ))}
            </div>
        </aside>
    );
};

export default ManagementSidebar;
