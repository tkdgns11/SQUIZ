export interface Schedule {
    id: number;
    date: string; // YYYY-MM-DD
    title: string;
    type: 'study' | 'project' | 'mentoring' | 'google';
}

export interface Goal {
    id: number;
    text: string;
    completed: boolean;
}

export interface Tag {
    id: number;
    text: string;
}

interface PlannerState {
    schedules: Schedule[];
    goals: Goal[];
    tags: Tag[];
    memo: string;
}

const STORAGE_KEY = 'modustudy_planner_data';

// 초기 기본 데이터
const DEFAULT_STATE: PlannerState = {
    schedules: [
        { id: 1, date: '2026-01-18', title: '알고리즘 스터디', type: 'study' },
        { id: 2, date: '2026-01-20', title: 'React 프로젝트 회의', type: 'project' },
        { id: 3, date: '2026-01-22', title: '멘토링 세션', type: 'mentoring' },
        { id: 4, date: '2026-01-25', title: 'TypeScript 심화 강의', type: 'study' },
        { id: 5, date: '2026-01-18', title: '개인 학습 시간', type: 'study' },
    ],
    goals: [
        { id: 1, text: '알고리즘 3문제 풀이', completed: false },
        { id: 2, text: 'React 공식문서 읽기', completed: true },
    ],
    tags: [
        { id: 1, text: 'React' },
        { id: 2, text: '공부기록' }
    ],
    memo: ''
};

// 로컬 스토리지에서 데이터 로드
const loadState = (): PlannerState => {
    try {
        const saved = localStorage.getItem(STORAGE_KEY);
        return saved ? JSON.parse(saved) : DEFAULT_STATE;
    } catch (e) {
        return DEFAULT_STATE;
    }
};

let state: PlannerState = loadState();

// 상태 저장 헬퍼
const saveState = () => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    listeners.forEach(l => l({ ...state }));
};

type Listener = (state: PlannerState) => void;
const listeners = new Set<Listener>();

export const scheduleStore = {
    getState: () => ({ ...state }),

    // 일정 관리
    addSchedule: (title: string, type: Schedule['type'], date?: string) => {
        const newEvent: Schedule = {
            id: Date.now(),
            title,
            date: date || new Date().toISOString().split('T')[0],
            type: type || 'study'
        };
        state.schedules = [...state.schedules, newEvent];
        saveState();
    },

    syncGoogleCalendar: () => {
        const googleEvents: Schedule[] = [
            { id: Date.now() + 1, date: '2026-01-15', title: '[G] 팀 테크 세미나', type: 'google' },
            { id: Date.now() + 2, date: '2026-01-27', title: '[G] 신규 프로젝트 온보딩', type: 'google' },
        ];
        state.schedules = [...state.schedules, ...googleEvents];
        saveState();
    },

    // 목표 관리
    addGoal: (text: string) => {
        state.goals = [...state.goals, { id: Date.now(), text, completed: false }];
        saveState();
    },

    toggleGoal: (id: number) => {
        state.goals = state.goals.map(g => g.id === id ? { ...g, completed: !g.completed } : g);
        saveState();
    },

    // 태그 관리
    addTag: (text: string) => {
        if (!state.tags.find(t => t.text === text)) {
            state.tags = [...state.tags, { id: Date.now(), text }];
            saveState();
        }
    },

    removeTag: (id: number) => {
        state.tags = state.tags.filter(t => t.id !== id);
        saveState();
    },

    // 메모 관리
    updateMemo: (memo: string) => {
        state.memo = memo;
        saveState();
    },

    subscribe: (listener: Listener) => {
        listeners.add(listener);
        return () => {
            listeners.delete(listener);
        };
    }
};
