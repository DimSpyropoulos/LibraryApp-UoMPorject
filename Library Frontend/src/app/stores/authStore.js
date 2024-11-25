import { create } from 'zustand';
import { persist } from 'zustand/middleware';

const useAuthStore = create(persist((set) => ({
    token: null,
    user: null,
    setToken: (newToken) => {
        set({ token: newToken });
        localStorage.setItem('token', newToken); // Save token to localStorage
    },
    setUser: (userData) => set({ user: userData }),
    clearToken: () => {
        set({ token: null, user: null });
        localStorage.removeItem('token'); // Clear token from localStorage
    },
}), {
    name: 'auth-storage' // unique name for localStorage key
}));

export default useAuthStore;
