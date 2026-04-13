<template>
  <div class="min-h-screen">
    <!-- Hero Section -->
    <section class="relative h-80 flex items-center justify-center overflow-hidden cyber-grid-bg">
      <div class="absolute inset-0 bg-gradient-to-b from-cyber-dark/80 to-cyber-dark"></div>
      <div class="absolute top-20 left-1/4 w-72 h-72 bg-neon-blue/10 rounded-full blur-3xl"></div>
      <div class="absolute bottom-10 right-1/4 w-96 h-72 bg-neon-purple/10 rounded-full blur-3xl"></div>
      <div class="relative text-center px-4">
        <h1 class="text-5xl md:text-6xl font-bold mb-4 gradient-text">精选合集</h1>
        <p class="text-xl text-text-muted">系统性学习体验 · 深度内容聚合</p>
      </div>
    </section>

    <!-- Collections Grid -->
    <section class="max-w-7xl mx-auto px-4 py-16">
      <div v-if="loading" class="flex justify-center py-20">
        <div class="w-10 h-10 border-2 border-neon-blue border-t-transparent rounded-full animate-spin"></div>
      </div>

      <div v-else-if="collections.length === 0" class="text-center py-20">
        <div class="text-6xl mb-4">📚</div>
        <p class="text-text-muted text-xl">暂无合集</p>
        <router-link to="/" class="btn-cyber mt-6 inline-block">返回首页</router-link>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <router-link
          v-for="collection in collections"
          :key="collection.id"
          :to="`/collections/${collection.id}`"
          class="glass-card overflow-hidden transition-all duration-500 glass-card-hover group"
        >
          <div class="relative h-48 overflow-hidden">
            <img
              :src="collection.coverImage || `https://picsum.photos/seed/${collection.id}/800/400`"
              :alt="collection.name"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
            >
            <div class="absolute inset-0 bg-gradient-to-t from-cyber-card via-transparent to-transparent"></div>
            <div class="absolute top-4 right-4">
              <span class="tag-cyber">
                {{ collection.articleCount || collection.articles?.length || 0 }} 篇
              </span>
            </div>
          </div>
          <div class="p-6">
            <h3 class="text-xl font-semibold text-text-primary mb-3 group-hover:text-neon-blue transition-colors">
              {{ collection.name }}
            </h3>
            <p class="text-text-secondary text-sm line-clamp-3">
              {{ collection.description }}
            </p>
            <div class="mt-4 flex items-center text-text-muted text-sm">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              {{ formatDate(collection.createTime) }}
            </div>
          </div>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBlogStore } from '@/stores/blog'

const blogStore = useBlogStore()
const collections = ref([])
const loading = ref(true)

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

onMounted(async () => {
  try {
    await blogStore.fetchCollections()
    collections.value = blogStore.collections
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
