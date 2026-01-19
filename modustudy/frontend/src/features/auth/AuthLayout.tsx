import React from 'react';
import { Sparkles } from 'lucide-react';
import './AuthLayout.css';

interface AuthLayoutProps {
    children: React.ReactNode;
    pageState?: string;
}

const AuthLayout: React.FC<AuthLayoutProps> = ({ children, pageState = '' }) => {
    const [isLoaded, setIsLoaded] = React.useState(false);

    React.useEffect(() => {
        const timer = setTimeout(() => setIsLoaded(true), 100);
        return () => clearTimeout(timer);
    }, []);

    return (
        <div className={`auth-page ${pageState} ${isLoaded ? 'loaded' : ''}`}>
            <div className="auth-wrapper">
                {/* Left Side - Visual Branding */}
                <div className="auth-visual">
                    <div className="visual-content">
                        <div className="visual-logo-wrapper">
                            <div className="visual-logo">SQUIZ</div>
                        </div>
                        <h2>Manage Our Study</h2>
                        <p className="visual-description">
                            올인원 스터디 관리 플랫폼
                        </p>

                        <div className="feature-pills">
                            <div className="pill">
                                <Sparkles size={14} />
                                <span>AI 회의록 자동화</span>
                            </div>
                            <div className="pill">
                                <Sparkles size={14} />
                                <span>실시간 퀴즈 대결</span>
                            </div>
                            <div className="pill">
                                <Sparkles size={14} />
                                <span>학습 데이터 분석</span>
                            </div>
                        </div>
                    </div>
                    <div className="visual-background">
                        <div className="circle circle-1"></div>
                        <div className="circle circle-2"></div>
                        <div className="mesh-overlay"></div>
                    </div>
                </div>

                {/* Right Side - Form Container */}
                <div className="auth-form-container">
                    <div className="form-content">
                        {children}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AuthLayout;
