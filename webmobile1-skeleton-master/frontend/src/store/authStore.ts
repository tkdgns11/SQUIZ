import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import axios from 'axios'

interface AuthState {
  token: string | null
  setToken: (token: string | null) => void
  login: (id: string, password: string) => Promise<void>
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      setToken: (token) => {
        set({ token })
        if (token) {
          axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
        } else {
          delete axios.defaults.headers.common['Authorization']
        }
      },
      login: async (id, password) => {
        const response = await axios.post('/api/v1/auth/login', { id, password })
        const accessToken = response.data.accessToken
        set({ token: accessToken })
        if (accessToken) {
          axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
        }
      },
      logout: () => {
        set({ token: null })
        delete axios.defaults.headers.common['Authorization']
      }
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ token: state.token })
    }
  )
)

// Initialize axios header from persisted token
const token = useAuthStore.getState().token
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}
