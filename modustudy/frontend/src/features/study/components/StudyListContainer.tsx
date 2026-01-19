// 스터디 목록을 감싸는 컨테이너 컴포넌트 - 최대 너비와 중앙 정렬을 담당
import React from 'react';
import '../styles/StudyListContainer.css';

interface StudyListContainerProps {
    children: React.ReactNode;
    maxWidth?: string;
    className?: string;
}

const StudyListContainer: React.FC<StudyListContainerProps> = ({
    children,
    maxWidth = '1400px',
    className = '',
}) => {
    return (
        <div className={`page-container ${className}`} style={{ maxWidth }}>
            {children}
        </div>
    );
};

export default StudyListContainer;
