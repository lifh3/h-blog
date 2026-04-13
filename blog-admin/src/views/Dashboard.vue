<template>
  <div>
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-cyber-text mb-2">欢迎回来</h1>
      <p class="text-cyber-muted">这是您的博客数据概览</p>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
      <StatsCard icon="📝" :value="stats.articles || 0" label="文章总数" color="cyan" />
      <StatsCard icon="💬" :value="stats.comments || 0" label="评论总数" color="purple" />
      <StatsCard icon="📁" :value="stats.categories || 0" label="分类总数" color="green" />
      <StatsCard icon="🏷️" :value="stats.tags || 0" label="标签总数" color="yellow" />
    </div>

    <!-- Quick Actions -->
    <div class="glass-card p-6">
      <h2 class="text-lg font-bold text-cyber-text mb-4">快速操作</h2>
      <div class="flex flex-wrap gap-3">
        <router-link to="/articles/new" class="btn-cyber">写文章</router-link>
        <router-link to="/categories" class="btn-cyber-outline">管理分类</router-link>
        <router-link to="/tags" class="btn-cyber-outline">管理标签</router-link>
        <router-link to="/comments" class="btn-cyber-outline">审核评论</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import StatsCard from '@/components/StatsCard.vue'
import { statsAPI } from '@/api'

const stats = ref({})

onMounted(async () => {
  try {
    const res = await statsAPI.get()
    stats.value = res.data.data
  } catch (error) {
    console.error('Failed to fetch stats:', error)
  }
})
</script>
