import AuthLayout from './AuthLayout';
import { authApi } from '@/api/endpoints/authApi';

export const LoginPage = () => {
    return (
        <AuthLayout>
            <div className="form-header">
                <h3>로그인</h3>
                <p>SQUIZ와 함께 스마트한 학습을 시작하세요</p>
            </div>

            <form className="auth-form" onSubmit={(e) => e.preventDefault()}>
                <div className="input-group">
                    <label>이메일</label>
                    <input type="email" placeholder="example@ssafy.com" />
                </div>

                <div className="input-group">
                    <label>비밀번호</label>
                    <input type="password" placeholder="••••••••" />
                </div>

                <div className="flex-between" style={{ marginBottom: '1.5rem', fontSize: '0.9rem' }}>
                    <label className="flex items-center gap-2 cursor-pointer">
                        <input type="checkbox" className="rounded-sm border-study-blue/30" />
                        <span className="text-study-text/70">로그인 유지</span>
                    </label>
                    <a href="#" className="text-study-blue hover:underline">비밀번호 찾기</a>
                </div>

                <button type="submit" className="btn-primary">
                    로그인
                </button>

                <div className="auth-divider">
                    <span>또는</span>
                </div>

                {/* 소셜 로그인 버튼 그룹 - 동그란 아이콘 버튼 */}
                <div className="social-login-circle-group">
                    {/* 카카오 로그인 버튼 */}
                    <button
                        type="button"
                        className="social-circle-btn kakao"
                        onClick={async () => {
                            try {
                                const { authUrl } = await authApi.getKakaoAuthUrl();
                                window.location.href = authUrl;
                            } catch (error) {
                                console.error('Failed to get Kakao Auth URL:', error);
                                alert('카카오 로그인 페이지를 불러오는데 실패했습니다.');
                            }
                        }}
                        title="카카오 로그인"
                    >
                        <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
                            <path d="M12 3C6.48 3 2 6.58 2 11c0 2.8 1.86 5.26 4.64 6.68-.15.56-.52 2.02-.6 2.33-.09.38.14.42.29.31.12-.09 1.94-1.32 2.73-1.86.56.08 1.13.12 1.94.12 5.52 0 10-3.58 10-8 0-4.42-4.48-8-10-8z" />
                        </svg>
                    </button>

                    {/* 구글 로그인 버튼 */}
                    <button
                        type="button"
                        className="social-circle-btn google"
                        onClick={() => {
                            // TODO: 구글 로그인 구현
                            alert('구글 로그인은 준비 중입니다.');
                        }}
                        title="구글 로그인"
                    >
                        <svg viewBox="0 0 24 24" width="20" height="20">
                            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
                            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
                        </svg>
                    </button>

                    {/* 네이버 로그인 버튼 */}
                    <button
                        type="button"
                        className="social-circle-btn naver"
                        onClick={() => {
                            // TODO: 네이버 로그인 구현
                            alert('네이버 로그인은 준비 중입니다.');
                        }}
                        title="네이버 로그인"
                    >
                        <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                            <path d="M16.273 12.845L7.376 0H0v24h7.727V11.155L16.624 24H24V0h-7.727v12.845z" />
                        </svg>
                    </button>
                </div>
            </form>

            <p style={{ marginTop: '2rem', textAlign: 'center', fontSize: '0.9rem', color: 'var(--color-text-secondary)' }}>
                계정이 없으신가요? <a href="/signup" className="text-study-blue font-bold hover:underline">회원가입</a>
            </p>
        </AuthLayout>
    );
};
