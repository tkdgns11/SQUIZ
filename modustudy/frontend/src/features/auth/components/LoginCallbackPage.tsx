import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authApi } from '@/api/endpoints/authApi';
import { useAuthStore } from '@/store/authStore';
import AuthLayout from './AuthLayout';
import { Loader2 } from 'lucide-react';

export const LoginCallbackPage = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const login = useAuthStore((state) => state.login);

    useEffect(() => {
        const handleCallback = async () => {
            const code = searchParams.get('code');

            if (!code) {
                console.error('No authorization code found in URL');
                navigate('/login', { replace: true });
                return;
            }

            try {
                const data = await authApi.handleKakaoCallback(code);

                // localStorage에 토큰 저장 (api/axios.ts에서 사용됨)
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);

                // authStore 업데이트
                login({
                    id: String(data.user.id),
                    name: data.user.name,
                    email: data.user.email,
                    avatar: data.user.profileImage || undefined
                });

                // 대시보드로 리다이렉트
                navigate('/dashboard', { replace: true });
            } catch (error) {
                console.error('Login callback error:', error);
                alert('로그인 처리 중 오류가 발생했습니다.');
                navigate('/login', { replace: true });
            }
        };

        handleCallback();
    }, [searchParams, navigate, login]);

    return (
        <AuthLayout hideBranding>
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                minHeight: '300px',
                textAlign: 'center'
            }}>
                <Loader2 className="animate-spin" size={48} color="var(--color-primary)" />
                <h3 style={{ marginTop: '1.5rem', fontWeight: 700 }}>로그인 중입니다</h3>
                <p style={{ color: '#64748b', marginTop: '0.5rem' }}>잠시만 기다려 주세요...</p>
            </div>
        </AuthLayout>
    );
};
