// 인증 도메인 관련 타입 정의
export interface UserDTO {
    id: number;
    email: string;
    name: string;
    nickname: string | null;
    profileImage: string | null;
    levelName: string;
    currentLevel: number;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    expiresIn: number;
    isNewUser: boolean;
    user: UserDTO;
    loginProvider: 'KAKAO' | 'NAVER' | 'GOOGLE';
}

export interface OAuth2CallbackRequest {
    code: string;
}

export interface AuthUrlResponse {
    authUrl: string;
}
