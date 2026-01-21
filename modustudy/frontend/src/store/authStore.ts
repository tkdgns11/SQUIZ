import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface User {
    id: string;
    name: string;
    nickname?: string;
    email: string;
    avatar?: string;
}

interface AuthState {
    isLoggedIn: boolean;
    user: User | null;
    isInitialized: boolean;
    login: (user: User) => void;
    logout: () => void;
    setInitialized: (val: boolean) => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            isLoggedIn: false,
            user: null,
            isInitialized: false,
            login: (user) => set({ isLoggedIn: true, user }),
            logout: () => {
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                set({ isLoggedIn: false, user: null });
            },
            setInitialized: (val) => set({ isInitialized: val }),
        }),
        {
            name: 'auth-storage', // localStorage 키 이름
            partialize: (state) => ({ isLoggedIn: state.isLoggedIn, user: state.user }), // 저장할 필드만 선택
        }
    )
);
