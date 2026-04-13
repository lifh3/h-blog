<template>
  <header class="h-14 bg-cyber-surface border-b border-cyber-border flex items-center justify-between px-6">
    <div class="text-lg font-medium text-cyber-text">
      {{ currentTitle }}
    </div>
    <div class="flex items-center gap-4">
      <span class="text-sm text-cyber-muted">{{ authStore.user?.username || 'Admin' }}</span>
      <button @click="handleLogout" class="btn-cyber-outline text-sm px-3 py-1">
        退出
      </button>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const pageTitles = {
  'Dashboard': '仪表盘',
  'ArticleList': '文章管理',
  'ArticleCreate': '新建文章',
  'ArticleEdit': '编辑文章',
  'CategoryList': '分类管理',
  'TagList': '标签管理',
  'CommentList': '评论管理',
  'CollectionList': '合集管理',
  'CollectionEdit': '编辑合集',
  'Profile': '个人设置'
}

const currentTitle = computed(() => {
  return pageTitles[route.name] || 'Blog Admin'
})

async function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
