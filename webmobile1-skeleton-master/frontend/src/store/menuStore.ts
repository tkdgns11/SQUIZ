import { create } from 'zustand'

interface MenuItem {
  name: string
  path: string
  icon?: string
}

interface MenuState {
  menus: Record<string, MenuItem>
  activeIndex: number
  setActiveIndex: (index: number) => void
}

export const useMenuStore = create<MenuState>((set) => ({
  menus: {
    home: { name: '홈', path: '/', icon: 'ic-home' },
    history: { name: '히스토리', path: '/history', icon: 'ic-history' },
    logout: { name: '로그아웃', path: '/logout', icon: 'ic-logout' }
  },
  activeIndex: 0,
  setActiveIndex: (index) => set({ activeIndex: index })
}))
