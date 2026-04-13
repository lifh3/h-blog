<template>
  <div class="min-h-screen flex items-center justify-center bg-cyber-bg">
    <div class="glass-card p-8 w-full max-w-md">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold gradient-text mb-2">Blog Admin</h1>
        <p class="text-cyber-muted">管理后台登录</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-sm text-cyber-muted mb-1">用户名</label>
          <input
            v-model="form.username"
            type="text"
            class="input-cyber"
            placeholder="请输入用户名"
            required
          />
        </div>

        <div>
          <label class="block text-sm text-cyber-muted mb-1">密码</label>
          <input
            v-model="form.password"
            type="password"
            class="input-cyber"
            placeholder="请输入密码"
            required
          />
        </div>

        <div v-if="error" class="text-cyber-red text-sm">{{ error }}</div>

        <button type="submit" class="btn-cyber w-full" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { authAPI } from '@/api'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true
  error.value = ''
  
  try {
    const res = await authAPI.login(form.value)
    const { token, user } = res.data.data
    authStore.login(token, user)
    router.push('/')
  } catch (err) {
    error.value = err.response?.data?.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>
