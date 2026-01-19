import React from 'react';
import PageContainer from './components/StudyListContainer'; // 재사용 가능한 컨테이너

const StudyCreatePage: React.FC = () => {
    return (
        <PageContainer>
            <div style={{
                padding: '40px 20px',
                textAlign: 'center',
                backgroundColor: 'var(--color-surface)',
                borderRadius: 'var(--radius-lg)',
                border: '1px solid var(--color-border)',
                marginTop: '20px'
            }}>
                <h1 style={{ fontSize: '24px', marginBottom: '16px', color: 'var(--color-text-primary)' }}>
                    스터디 생성 페이지
                </h1>
                <p style={{ color: 'var(--color-text-secondary)', fontSize: '16px' }}>
                    스터디 생성 기능은 현재 준비 중입니다. 🚧
                </p>
                <div style={{ marginTop: '30px' }}>
                    <button
                        onClick={() => window.history.back()}
                        style={{
                            padding: '10px 20px',
                            backgroundColor: 'var(--color-primary)',
                            color: 'white',
                            border: 'none',
                            borderRadius: 'var(--radius-md)',
                            cursor: 'pointer',
                            fontWeight: '600'
                        }}
                    >
                        돌아가기
                    </button>
                </div>
            </div>
        </PageContainer>
    );
};

export default StudyCreatePage;
