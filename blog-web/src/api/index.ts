import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        const authStore = useAuthStore()
        authStore.clearAuth()
        const isPublicRoute = ['/', '/posts', '/categories', '/tags', '/archive', '/about', '/setup']
          .some(path => window.location.pathname.startsWith(path))
        if (!isPublicRoute) {
          window.location.href = '/console/login'
        }
      }
      const message = data?.message || '请求失败'
      return Promise.reject(new Error(message))
    }
    return Promise.reject(new Error('网络连接失败'))
  }
)

export default api
