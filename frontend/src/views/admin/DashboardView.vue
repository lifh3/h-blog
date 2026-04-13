<template>
  <div class="space-y-6">
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="glass-card p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-text-muted text-sm">文章总数</p>
            <p class="text-3xl font-bold text-neon-blue mt-1">{{ stats.articles || 0 }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-neon-blue/20 flex items-center justify-center text-2xl">📝</div>
        </div>
      </div>
      
      <div class="glass-card p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-text-muted text-sm">评论总数</p>
            <p class="text-3xl font-bold text-neon-purple mt-1">{{ stats.comments || 0 }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-neon-purple/20 flex items-center justify-center text-2xl">💬</div>
        </div>
      </div>
      
      <div class="glass-card p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-text-muted text-sm">总浏览量</p>
            <p class="text-3xl font-bold text-neon-green mt-1">{{ stats.viewCount || 0 }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-neon-green/20 flex items-center justify-center text-2xl">👁️</div>
        </div>
      </div>
      
      <div class="glass-card p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-text-muted text-sm">用户总数</p>
            <p class="text-3xl font-bold text-neon-yellow mt-1">{{ stats.users || 0 }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-neon-yellow/20 flex items-center justify-center text-2xl">👤</div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="glass-card p-6">
        <h3 class="text-lg font-semibold mb-4">最近评论</h3>
        <div class="space-y-4">
          <div v-for="comment in recentComments" :key="comment.id" class="flex items-start space-x-3 p-3 rounded-lg bg-white/5">
            <div class="w-8 h-8 rounded-full bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center text-white text-sm font-semibold">
              {{ comment.nickname?.charAt(0).toUpperCase() }}
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-text-primary text-sm truncate">{{ comment.content }}</p>
              <p class="text-text-muted text-xs mt-1">{{ comment.articleTitle }} · {{ formatDate(comment.createTime) }}</p>
            </div>
          </div>
          <div v-if="!recentComments.length" class="text-center py-8 text-text-muted">
            暂无评论
          </div>
        </div>
      </div>
      
      <div class="glass-card p-6">
        <h3 class="text-lg font-semibold mb-4">热门文章</h3>
        <div class="space-y-4">
          <div v-for="(article, idx) in hotArticles" :key="article.id" class="flex items-center space-x-3 p-3 rounded-lg bg-white/5">
            <span class="w-6 h-6 rounded bg-white/10 text-text-muted text-sm flex items-center justify-center">{{ idx + 1 }}</span>
            <span class="flex-1 text-text-secondary text-sm truncate">{{ article.title }}</span>
            <span class="text-neon-blue text-sm">{{ article.viewCount || 0 }}</span>
          </div>
          <div v-if="!hotArticles.length" class="text-center py-8 text-text-muted">
            暂无文章
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const stats = ref({})
const recentComments = ref([])
const hotArticles = ref([])

async function fetchDashboard() {
  try {
    const res = await adminApi.getDashboard()
    stats.value = res.stats || {}
    recentComments.value = res.recent_comments || []
    hotArticles.value = res.hot_articles || []
  } catch (err) {
    console.error('Failed to fetch dashboard:', err)
  }
}

function formatDate(date) {
  return new Date(date).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

onMounted(fetchDashboard)
</script>
