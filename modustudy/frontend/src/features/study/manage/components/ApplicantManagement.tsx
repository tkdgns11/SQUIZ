import React, { useState, useEffect } from 'react';
import { studyService } from '../../services/studyService';
import { Applicant } from '../../mockData';
import { Check, X, Clock, MessageSquare } from 'lucide-react';

interface ApplicantManagementProps {
    studyId: number;
}

const ApplicantManagement: React.FC<ApplicantManagementProps> = ({ studyId }) => {
    const [applicants, setApplicants] = useState<Applicant[]>([]);

    useEffect(() => {
        const data = studyService.getApplicantsByStudyId(studyId);
        setApplicants(data);
    }, [studyId]);

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
    };

    const handleAction = (applicantId: number, status: 'APPROVED' | 'REJECTED') => {
        const success = studyService.updateApplicantStatus(applicantId, status);
        if (success) {
            setApplicants(prev => prev.map(app =>
                app.id === applicantId ? { ...app, status } : app
            ));
        }
    };

    if (applicants.length === 0) {
        return (
            <div className="empty-state">
                <Clock size={48} />
                <p>아직 신청자가 없습니다.</p>
            </div>
        );
    }

    return (
        <div className="applicant-management">
            <h3>신청자 목록 ({applicants.length})</h3>
            <div className="applicant-list">
                {applicants.map((app) => (
                    <div key={app.id} className="applicant-card">
                        <div className="app-header">
                            <div className="app-user-info">
                                <div className="app-avatar">{app.nickname.charAt(0)}</div>
                                <div>
                                    <h4 className="app-nickname">{app.nickname}</h4>
                                    <span className="app-date">{formatDate(app.createdAt)} 신청</span>
                                </div>
                            </div>
                            <div className={`app-status status-${app.status.toLowerCase()}`}>
                                {app.status === 'PENDING' ? '대기 중' : app.status === 'APPROVED' ? '승인됨' : '거절됨'}
                            </div>
                        </div>

                        <div className="app-message">
                            <MessageSquare size={16} />
                            <p>{app.message}</p>
                        </div>

                        {app.status === 'PENDING' && (
                            <div className="app-actions">
                                <button
                                    className="btn-approve"
                                    onClick={() => handleAction(app.id, 'APPROVED')}
                                >
                                    <Check size={18} />
                                    <span>승인</span>
                                </button>
                                <button
                                    className="btn-reject"
                                    onClick={() => handleAction(app.id, 'REJECTED')}
                                >
                                    <X size={18} />
                                    <span>거절</span>
                                </button>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ApplicantManagement;
