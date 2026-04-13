<template>
  <nav class="fixed top-0 left-0 right-0 z-50 h-16 glass-card border-b border-cyber-border">
    <div class="max-w-7xl mx-auto px-4 h-full flex items-center justify-between">
      <router-link to="/" class="flex items-center space-x-3">
        <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center">
          <span class="text-white font-bold text-lg">T</span>
        </div>
        <span class="font-cyber text-xl font-bold gradient-text">TechBlog</span>
      </router-link>

      <div class="hidden md:flex items-center space-x-8">
        <router-link to="/" class="nav-link" :class="{ 'active': $route.path === '/' }">首页</router-link>
        
        <!-- 分类下拉菜单 -->
        <div class="relative" @mouseenter="showCategories = true" @mouseleave="showCategories = false">
          <button class="nav-link flex items-center gap-1">
            分类
            <svg class="w-4 h-4 transition-transform" :class="{ 'rotate-180': showCategories }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
            </svg>
          </button>
          
          <transition name="fade">
            <div v-if="showCategories" class="absolute top-full left-0 mt-2 w-56 glass-card border border-cyber-border rounded-xl shadow-xl overflow-hidden">
              <div class="p-2">
                <div v-for="category in categories" :key="category.id" 
                  @click="goToCategory(category.id)"
                  class="px-4 py-2.5 rounded-lg text-sm text-text-secondary hover:bg-cyber-accent/10 hover:text-cyber-accent cursor-pointer transition-colors flex items-center justify-between group">
                  <span>{{ category.name }}</span>
                  <span class="text-xs text-text-muted group-hover:text-cyber-accent">{{ category.articleCount || 0 }}</span>
                </div>
                <div v-if="categories.length === 0" class="px-4 py-3 text-sm text-text-muted text-center">
                  暂无分类
                </div>
              </div>
            </div>
          </transition>
        </div>
        
        <router-link to="/collections" class="nav-link" :class="{ 'active': $route.path.startsWith('/collections') }">合集</router-link>
        <router-link to="/archives" class="nav-link" :class="{ 'active': $route.path === '/archives' }">归档</router-link>
        <router-link to="/links" class="nav-link" :class="{ 'active': $route.path === '/links' }">友链</router-link>
        <router-link to="/about" class="nav-link" :class="{ 'active': $route.path === '/about' }">关于</router-link>
      </div>

      <div class="flex items-center space-x-4">
        <div class="relative">
          <input 
            v-model="searchQuery"
            @keyup.enter="handleSearch"
            type="text" 
            placeholder="搜索文章..."
            class="input-cyber w-48 pl-10 pr-4 py-2 text-sm"
          >
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-text-muted" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
          </svg>
        </div>
      </div>
    </div>
  </nav>
  <div class="h-16"></div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { publicApi } from '@/api'

const searchQuery = ref('')
const router = useRouter()
const showCategories = ref(false)
const categories = ref([])

async function fetchCategories() {
  try {
    const res = await publicApi.getCategories()
    categories.value = res.data?.data || res.data || []
  } catch (err) {
    console.error('Failed to fetch categories:', err)
  }
}

function goToCategory(categoryId) {
  showCategories.value = false
  router.push(`/category/${categoryId}`)
}

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ path: '/', query: { search: searchQuery.value } })
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.nav-link {
  color: #a8b8c8;
  transition: color 0.3s;
}
.nav-link:hover,
.nav-link.active {
  color: #4a9eaf;
}
.nav-link.active {
  text-shadow: 0 0 8px rgba(74,158,175,0.5);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
