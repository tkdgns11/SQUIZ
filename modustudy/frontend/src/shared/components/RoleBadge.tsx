import React from 'react';
import { Shield, ShieldAlert, User } from 'lucide-react';
import './RoleBadge.css';

export type UserRole = 'LEADER' | 'SUB_LEADER' | 'MEMBER';

interface RoleBadgeProps {
    role: UserRole;
    className?: string;
}

const RoleBadge: React.FC<RoleBadgeProps> = ({ role, className = '' }) => {
    const getRoleConfig = () => {
        switch (role) {
            case 'LEADER':
                return {
                    label: '팀장',
                    icon: <Shield size={14} />,
                    class: 'role-leader'
                };
            case 'SUB_LEADER':
                return {
                    label: '부팀장',
                    icon: <ShieldAlert size={14} />,
                    class: 'role-sub-leader'
                };
            case 'MEMBER':
            default:
                return {
                    label: '팀원',
                    icon: <User size={14} />,
                    class: 'role-member'
                };
        }
    };

    const config = getRoleConfig();

    return (
        <span className={`role-badge ${config.class} ${className}`}>
            {config.icon}
            {config.label}
        </span>
    );
};

export default RoleBadge;
