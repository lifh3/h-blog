<template>
  <aside class="w-56 h-screen bg-cyber-surface border-r border-cyber-border flex flex-col">
    <!-- Logo -->
    <div class="p-4 border-b border-cyber-border">
      <h1 class="text-xl font-bold gradient-text">Blog Admin</h1>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 py-4">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="flex items-center px-4 py-3 mx-2 rounded-lg transition-all duration-200"
        :class="isActive(item.path) ? 'bg-cyber-cyan/10 text-cyber-cyan' : 'text-cyber-muted hover:text-cyber-text hover:bg-cyber-bg'"
      >
        <span class="text-lg mr-3">{{ item.icon }}</span>
        <span class="font-medium">{{ item.label }}</span>
      </router-link>
    </nav>

    <!-- Footer -->
    <div class="p-4 border-t border-cyber-border text-cyber-muted text-xs text-center">
      v1.0.0
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const menuItems = [
  { path: '/', label: 'Dashboard', icon: '📊' },
  { path: '/articles', label: '文章', icon: '📝' },
  { path: '/categories', label: '分类', icon: '📁' },
  { path: '/tags', label: '标签', icon: '🏷️' },
  { path: '/comments', label: '评论', icon: '💬' },
  { path: '/collections', label: '合集', icon: '📚' },
  { path: '/profile', label: '设置', icon: '⚙️' },
  { path: '/ai-publish', label: 'AI发布', icon: '🤖' },
  { path: '/scheduler', label: '定时任务', icon: '⏰' }
]

function isActive(path) {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}
</script>
