import AuthLayout from './AuthLayout';

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
            </form>

            <p style={{ marginTop: '2rem', textAlign: 'center', fontSize: '0.9rem', color: 'var(--color-text-secondary)' }}>
                계정이 없으신가요? <a href="/signup" className="text-study-blue font-bold hover:underline">회원가입</a>
            </p>
        </AuthLayout>
    );
};
