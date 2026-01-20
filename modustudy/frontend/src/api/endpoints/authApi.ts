import api from '../axios';
import { AuthResponse, AuthUrlResponse, OAuth2CallbackRequest } from '@/features/auth/types';

export const authApi = {
    /**
     * 카카오 로그인 URL 가져오기
     */
    getKakaoAuthUrl: async () => {
        const response = await api.get<any>('/api/v1/auth/oauth/kakao');
        // 백엔드에서 ApiResponse<AuthUrlResponse> 반환함
        return response.data.data as AuthUrlResponse;
    },

    /**
     * 카카오 로그인 콜백 처리 (인가 코드를 JWT로 교환)
     */
    handleKakaoCallback: async (code: string) => {
        const request: OAuth2CallbackRequest = { code };
        const response = await api.post<any>('/api/v1/auth/oauth/kakao/callback', request);
        // 백엔드에서 ApiResponse<AuthResponse> 반환함
        return response.data.data as AuthResponse;
    },
};
