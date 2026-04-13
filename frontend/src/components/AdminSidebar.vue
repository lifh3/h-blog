<template>
  <aside class="fixed left-0 top-0 bottom-0 w-64 bg-cyber-gray border-r border-cyber-border flex flex-col">
    <div class="h-16 flex items-center px-6 border-b border-cyber-border">
      <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center mr-3">
        <span class="text-white font-bold text-sm">T</span>
      </div>
      <span class="font-cyber text-lg font-bold gradient-text">Admin</span>
    </div>
    
    <nav class="flex-1 py-6 px-3">
      <router-link 
        v-for="item in menuItems" 
        :key="item.path"
        :to="item.path"
        class="menu-item"
        :class="{ 'active': isActive(item.path) }"
      >
        <span class="text-xl mb-1">{{ item.icon }}</span>
        <span>{{ item.name }}</span>
      </router-link>
    </nav>
    
    <div class="p-4 border-t border-cyber-border">
      <router-link to="/" target="_blank" class="flex items-center text-text-muted hover:text-neon-blue transition-colors text-sm">
        <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"/>
        </svg>
        查看博客
      </router-link>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const menuItems = [
  { path: '/admin', icon: '📊', name: '仪表盘' },
  { path: '/admin/articles', icon: '📝', name: '文章管理' },
  { path: '/admin/categories', icon: '📁', name: '分类管理' },
  { path: '/admin/tags', icon: '🏷️', name: '标签管理' },
  { path: '/admin/collections', icon: '📚', name: '合集管理' },
  { path: '/admin/comments', icon: '💬', name: '评论管理' },
]

function isActive(path) {
  if (path === '/admin') {
    return route.path === '/admin'
  }
  return route.path.startsWith(path)
}
</script>

<style scoped>
.menu-item {
  @apply flex items-center space-x-3 px-4 py-3 rounded-lg mb-1 text-text-secondary transition-all duration-300;
}

.menu-item:hover {
  @apply bg-white/5 text-text-primary;
}

.menu-item.active {
  @apply bg-neon-blue/10 text-neon-blue border-l-2 border-neon-blue;
}
</style>
