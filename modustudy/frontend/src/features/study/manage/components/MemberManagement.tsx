import React, { useState, useEffect } from 'react';
import { studyService } from '../../services/studyService';
import { StudyMember } from '../../mockData';
import { UserMinus, User, CheckCircle2 } from 'lucide-react';
import RoleBadge from '@/shared/components/RoleBadge';

interface MemberManagementProps {
    studyId: number;
    maxMembers: number;
}

const MemberManagement: React.FC<MemberManagementProps> = ({ studyId, maxMembers }) => {
    const [members, setMembers] = useState<StudyMember[]>([]);

    useEffect(() => {
        loadMembers();
    }, [studyId]);

    const loadMembers = () => {
        const data = studyService.getMembersByStudyId(studyId);
        setMembers(data);
    };

    const handleExpel = (userId: number, nickname: string) => {
        if (window.confirm(`${nickname} 멤버를 정말 강퇴하시겠습니까?`)) {
            const success = studyService.expelMember(studyId, userId);
            if (success) {
                setMembers(prev => prev.filter(m => m.userId !== userId));
            }
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
    };

    if (members.length === 0) {
        return (
            <div className="empty-state">
                <User size={48} />
                <p>현재 스터디에 참여 중인 멤버가 없습니다.</p>
            </div>
        );
    }

    return (
        <div className="member-management">
            <div className="section-header">
                <h3>스터디 멤버 ({members.length})</h3>
                <span className="info-badge">
                    <CheckCircle2 size={14} />
                    참여 정원 준수 중 ({members.length}/{maxMembers})
                </span>
            </div>

            <div className="member-table-container">
                <table className="member-table">
                    <thead>
                        <tr>
                            <th>닉네임</th>
                            <th>역할</th>
                            <th>가입일</th>
                            <th>출석률</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {members.map((member) => (
                            <tr key={member.id} className="member-row">
                                <td>
                                    <div className="member-info">
                                        <div className="member-avatar">{member.nickname.charAt(0)}</div>
                                        <span className="member-nickname">{member.nickname}</span>
                                    </div>
                                </td>
                                <td>
                                    <RoleBadge role={member.role} />
                                </td>
                                <td className="text-secondary">{formatDate(member.joinedAt)}</td>
                                <td>
                                    <div className="attendance-indicator">
                                        <div className="progress-bar-bg">
                                            <div
                                                className="progress-bar-fill"
                                                style={{ width: `${member.attendanceRate}%` }}
                                            ></div>
                                        </div>
                                        <span className="attendance-text">{member.attendanceRate}%</span>
                                    </div>
                                </td>
                                <td>
                                    {member.role !== 'LEADER' && (
                                        <button
                                            className="btn-icon-danger"
                                            onClick={() => handleExpel(member.userId, member.nickname)}
                                            title="강퇴하기"
                                        >
                                            <UserMinus size={18} />
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default MemberManagement;
