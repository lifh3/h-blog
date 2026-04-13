<template>
  <div class="min-h-screen bg-cyber-dark cyber-grid-bg flex items-center justify-center p-4">
    <div class="w-full max-w-md">
      <div class="text-center mb-8">
        <div class="w-20 h-20 rounded-2xl bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center mx-auto mb-4 shadow-neon-blue">
          <span class="text-white font-bold text-3xl">T</span>
        </div>
        <h1 class="text-3xl font-cyber font-bold gradient-text">管理登录</h1>
        <p class="text-text-muted mt-2">TechBlog 后台管理系统</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="glass-card p-8">
        <div class="mb-6">
          <label class="block text-text-secondary text-sm mb-2">用户名</label>
          <input 
            v-model="form.username"
            type="text" 
            class="input-cyber"
            placeholder="请输入用户名"
            required
          >
        </div>
        <div class="mb-6">
          <label class="block text-text-secondary text-sm mb-2">密码</label>
          <input 
            v-model="form.password"
            type="password" 
            class="input-cyber"
            placeholder="请输入密码"
            required
          >
        </div>
        
        <div v-if="error" class="mb-4 p-3 rounded-lg bg-neon-pink/20 text-neon-pink text-sm">
          {{ error }}
        </div>
        
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

const router = useRouter()
const authStore = useAuthStore()

const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    await authStore.login(form.value.username, form.value.password)
    router.push('/admin')
  } catch (err) {
    error.value = '用户名或密码错误'
  } finally {
    loading.value = false
  }
}
</script>
