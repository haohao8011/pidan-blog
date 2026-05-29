import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '@/types'
import { login as apiLogin, logout as apiLogout, getProfile } from '@/api/admin'

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(false)
  const user = ref<User | null>(null)

  async function checkAuth() {
    try {
      const res = await getProfile()
      user.value = res.data.data
      isLoggedIn.value = true
    } catch {
      user.value = null
      isLoggedIn.value = false
    }
  }

  async function login(username: string, password: string) {
    const res = await apiLogin({ username, password })
    user.value = res.data.data.user
    isLoggedIn.value = true
    return res.data.data
  }

  async function logout() {
    try {
      await apiLogout()
    } finally {
      user.value = null
      isLoggedIn.value = false
    }
  }

  function clearAuth() {
    user.value = null
    isLoggedIn.value = false
  }

  return { isLoggedIn, user, login, logout, checkAuth, clearAuth }
})
