import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  async function login(username, password) {
    try {
      const res = await axios.post('/api/admin/login', { username, password })
      token.value = res.data.token
      userInfo.value = res.data.user
      localStorage.setItem('admin_token', token.value)
      axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
      return true
    } catch (err) {
      throw err
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
    delete axios.defaults.headers.common['Authorization']
  }

  function initAuth() {
    if (token.value) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
    }
  }

  return { token, userInfo, isAuthenticated, login, logout, initAuth }
})
