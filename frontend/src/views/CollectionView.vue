<template>
  <div class="min-h-screen">
    <!-- Hero with Cover -->
    <section v-if="collection" class="relative h-96 flex items-end overflow-hidden">
      <img
        :src="collection.coverImage || `https://picsum.photos/seed/${collection.id}/1400/600`"
        :alt="collection.name"
        class="absolute inset-0 w-full h-full object-cover"
      >
      <div class="absolute inset-0 bg-gradient-to-t from-cyber-dark via-cyber-dark/60 to-transparent"></div>
      <div class="relative max-w-4xl mx-auto px-4 pb-12 w-full">
        <router-link to="/collections" class="inline-flex items-center text-text-muted hover:text-neon-blue transition-colors mb-6">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
          </svg>
          返回合集
        </router-link>
        <h1 class="text-4xl md:text-5xl font-bold text-text-primary mb-4">{{ collection.name }}</h1>
        <p class="text-text-secondary text-lg max-w-2xl">{{ collection.description }}</p>
      </div>
    </section>

    <!-- Articles List -->
    <section class="max-w-4xl mx-auto px-4 py-16">
      <div v-if="loading" class="flex justify-center py-20">
        <div class="w-10 h-10 border-2 border-neon-blue border-t-transparent rounded-full animate-spin"></div>
      </div>

      <div v-else-if="!collection" class="text-center py-20">
        <p class="text-text-muted text-xl">合集不存在</p>
        <router-link to="/collections" class="btn-cyber mt-6 inline-block">返回合集列表</router-link>
      </div>

      <div v-else>
        <h2 class="text-2xl font-semibold mb-8 flex items-center">
          <span class="w-8 h-1 bg-gradient-to-r from-neon-blue to-neon-purple rounded mr-3"></span>
          包含文章 ({{ sortedArticles.length }})
        </h2>

        <div v-if="sortedArticles.length === 0" class="text-center py-12">
          <p class="text-text-muted">该合集暂无文章</p>
        </div>

        <div v-else class="space-y-6">
          <router-link
            v-for="(article, index) in sortedArticles"
            :key="article.id"
            :to="`/article/${article.id}`"
            class="glass-card p-6 flex items-center gap-6 transition-all duration-300 glass-card-hover group"
          >
            <div class="flex-shrink-0 w-12 h-12 rounded-xl bg-gradient-to-br from-neon-blue/20 to-neon-purple/20 flex items-center justify-center text-neon-blue font-bold text-xl group-hover:from-neon-blue/40 group-hover:to-neon-purple/40 transition-all">
              {{ index + 1 }}
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="text-lg font-semibold text-text-primary group-hover:text-neon-blue transition-colors mb-1 truncate">
                {{ article.title }}
              </h3>
              <div class="flex items-center gap-4 text-text-muted text-sm">
                <span class="flex items-center">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                  </svg>
                  {{ formatDate(article.createTime) }}
                </span>
                <span class="flex items-center">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                  </svg>
                  {{ article.viewCount || 0 }} 阅读
                </span>
              </div>
            </div>
            <div v-if="article.coverImage" class="flex-shrink-0 w-24 h-16 rounded-lg overflow-hidden">
              <img :src="article.coverImage" :alt="article.title" class="w-full h-full object-cover">
            </div>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useBlogStore } from '@/stores/blog'

const route = useRoute()
const blogStore = useBlogStore()
const collection = ref(null)
const loading = ref(true)

const sortedArticles = computed(() => {
  if (!collection.value?.articles) return []
  return [...collection.value.articles].sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
})

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

onMounted(async () => {
  loading.value = true
  try {
    await blogStore.fetchCollection(route.params.id)
    collection.value = blogStore.currentCollection
  } finally {
    loading.value = false
  }
})
</script>
