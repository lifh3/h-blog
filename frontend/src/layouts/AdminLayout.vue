<template>
  <div class="flex min-h-screen bg-cyber-dark">
    <AdminSidebar />
    <div class="flex-1 ml-64">
      <header class="h-16 bg-cyber-gray border-b border-cyber-border flex items-center justify-between px-6">
        <h2 class="text-xl font-semibold text-text-primary">管理后台</h2>
        <button @click="handleLogout" class="px-4 py-2 rounded-lg text-sm text-text-secondary hover:text-neon-blue transition-colors">
          退出登录
        </button>
      </header>
      <main class="p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import AdminSidebar from '@/components/AdminSidebar.vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

function handleLogout() {
  authStore.logout()
  router.push('/admin/login')
}
</script>
