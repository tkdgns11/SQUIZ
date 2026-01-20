import { useEffect, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authApi } from '@/api/endpoints/authApi';
import { useAuthStore } from '@/store/authStore';
import AuthLayout from './AuthLayout';
import { Loader2 } from 'lucide-react';

export const LoginCallbackPage = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const login = useAuthStore((state) => state.login);

    // 중복 요청 방지를 위한 ref (React StrictMode에서 useEffect 두 번 실행 대응)
    const isProcessingRef = useRef(false);

    useEffect(() => {
        const handleCallback = async () => {
            const code = searchParams.get('code');

            // 인가 코드가 없으면 로그인 페이지로
            if (!code) {
                console.error('No authorization code found in URL');
                navigate('/login', { replace: true });
                return;
            }

            // 이미 처리 중이면 중복 요청 방지
            if (isProcessingRef.current) {
                console.log('[INFO] 이미 로그인 처리 중입니다.');
                return;
            }
            isProcessingRef.current = true;

            try {
                console.log('[INFO] 카카오 로그인 처리 시작');
                const data = await authApi.handleKakaoCallback(code);

                // localStorage에 토큰 저장
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);

                // authStore 업데이트
                login({
                    id: String(data.user.id),
                    name: data.user.name,
                    email: data.user.email,
                    avatar: data.user.profileImage || undefined
                });

                console.log('[INFO] 로그인 성공!');
                // 대시보드로 리다이렉트
                navigate('/dashboard', { replace: true });
            } catch (error) {
                console.error('Login callback error:', error);

                // 에러 발생 시 플래그 리셋 (재시도 가능하도록)
                isProcessingRef.current = false;

                alert('로그인 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
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
