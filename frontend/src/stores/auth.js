import { defineStore } from 'pinia'

const STORAGE_KEY = 'kyoo-mall-auth'

function loadFromStorage() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || {}
  } catch {
    return {}
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: loadFromStorage().token || '',
    user: loadFromStorage().user || null,
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
  },
  actions: {
    setAuth({ token, userId, username, nickname }) {
      this.token = token
      this.user = { userId, username, nickname }
      localStorage.setItem(STORAGE_KEY, JSON.stringify({ token: this.token, user: this.user }))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(STORAGE_KEY)
    },
  },
})
