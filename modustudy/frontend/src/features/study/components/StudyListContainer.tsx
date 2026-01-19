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
