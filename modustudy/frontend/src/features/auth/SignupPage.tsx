import AuthLayout from './AuthLayout';

export const SignupPage = () => {
    return (
        <AuthLayout>
            <div className="form-header">
                <h3>회원가입</h3>
                <p>SQUIZ 스터디 팀의 일원이 되어보세요</p>
            </div>

            <form className="auth-form" onSubmit={(e) => e.preventDefault()}>
                <div className="input-group">
                    <label>이름</label>
                    <input type="text" placeholder="성함을 입력해주세요" />
                </div>

                <div className="input-group">
                    <label>이메일</label>
                    <input type="email" placeholder="example@ssafy.com" />
                </div>

                <div className="input-group">
                    <label>비밀번호</label>
                    <input type="password" placeholder="8자 이상 입력해주세요" />
                </div>

                <div className="input-group">
                    <label>비밀번호 확인</label>
                    <input type="password" placeholder="비밀번호를 다시 입력해주세요" />
                </div>

                <button type="submit" className="btn-primary" style={{ marginTop: '1rem' }}>
                    가입하기
                </button>
            </form>

            <p style={{ marginTop: '2rem', textAlign: 'center', fontSize: '0.9rem', color: 'var(--color-text-secondary)' }}>
                이미 계정이 있으신가요? <a href="/login" className="text-study-blue font-bold hover:underline">로그인</a>
            </p>
        </AuthLayout>
    );
};
