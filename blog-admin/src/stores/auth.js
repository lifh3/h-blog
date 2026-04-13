import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authAPI } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('admin_token', newToken)
  }

  function login(newToken, userData) {
    setToken(newToken)
    user.value = userData
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('admin_token')
  }

  async function fetchUser() {
    if (!token.value) return
    try {
      const res = await authAPI.profile()
      user.value = res.data.data
    } catch (error) {
      console.error('Failed to fetch user:', error)
      logout()
    }
  }

  return {
    token,
    user,
    isLoggedIn,
    login,
    logout,
    fetchUser,
    setToken
  }
})
