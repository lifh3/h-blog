<template>
  <div class="min-h-screen">
    <section class="relative h-72 flex items-center justify-center overflow-hidden cyber-grid-bg">
      <div class="absolute inset-0 bg-gradient-to-b from-cyber-dark/80 to-cyber-dark"></div>
      <div class="relative text-center px-4">
        <h1 class="text-5xl font-bold mb-4 gradient-text">文章归档</h1>
        <p class="text-text-muted">按时间线整理的所有文章</p>
      </div>
    </section>
    <section class="max-w-4xl mx-auto px-4 py-16">
      <div v-if="loading" class="flex justify-center py-20">
        <div class="w-10 h-10 border-2 border-neon-blue border-t-transparent rounded-full animate-spin"></div>
      </div>
      <div v-else-if="groupedArticles.length === 0" class="text-center py-20">
        <p class="text-text-muted text-xl">暂无归档</p>
      </div>
      <div v-else class="space-y-12">
        <div v-for="group in groupedArticles" :key="group.year">
          <h2 class="text-2xl font-bold text-text-primary mb-6 flex items-center">
            <span class="w-8 h-1 bg-gradient-to-r from-neon-blue to-neon-purple rounded mr-3"></span>
            {{ group.year }}
          </h2>
          <div class="space-y-4">
            <router-link
              v-for="article in group.articles"
              :key="article.id"
              :to="`/article/${article.id}`"
              class="glass-card p-5 flex items-center gap-4 transition-all duration-300 glass-card-hover group"
            >
              <span class="text-text-muted text-sm w-16 flex-shrink-0">{{ formatMonth(article.createTime) }}</span>
              <span class="text-text-primary group-hover:text-neon-blue transition-colors">{{ article.title }}</span>
              <span v-if="article.categoryName" class="tag-cyber ml-auto text-xs">{{ article.categoryName }}</span>
            </router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useBlogStore } from '@/stores/blog'

const blogStore = useBlogStore()
const allArticles = ref([])
const loading = ref(true)

const groupedArticles = computed(() => {
  const groups = {}
  allArticles.value.forEach(article => {
    const year = new Date(article.createTime).getFullYear()
    if (!groups[year]) groups[year] = []
    groups[year].push(article)
  })
  return Object.keys(groups)
    .sort((a, b) => b - a)
    .map(year => ({ year: parseInt(year), articles: groups[year] }))
})

function formatMonth(date) {
  return new Date(date).toLocaleDateString('zh-CN', { month: '2-digit' })
}

onMounted(async () => {
  try {
    await blogStore.fetchArticles({ limit: 1000 })
    allArticles.value = blogStore.articles
  } finally {
    loading.value = false
  }
})
</script>
