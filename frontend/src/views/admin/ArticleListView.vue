<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-semibold">文章管理</h2>
      <router-link to="/admin/articles/new" class="btn-cyber">
        新建文章
      </router-link>
    </div>
    
    <div class="glass-card">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-cyber-border">
              <th class="text-left p-4 text-text-muted font-medium">标题</th>
              <th class="text-left p-4 text-text-muted font-medium">分类</th>
              <th class="text-left p-4 text-text-muted font-medium">状态</th>
              <th class="text-left p-4 text-text-muted font-medium">浏览</th>
              <th class="text-left p-4 text-text-muted font-medium">时间</th>
              <th class="text-right p-4 text-text-muted font-medium">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="article in articles" :key="article.id" class="border-b border-cyber-border/50 hover:bg-white/5 transition-colors">
              <td class="p-4">
                <router-link :to="`/article/${article.id}`" target="_blank" class="text-text-primary hover:text-neon-blue transition-colors">
                  {{ article.title }}
                </router-link>
              </td>
              <td class="p-4 text-text-secondary">{{ article.categoryName }}</td>
              <td class="p-4">
                <span :class="article.status === 1 ? 'text-neon-green' : 'text-neon-yellow'" class="text-sm">
                  {{ article.status === 1 ? '已发布' : '草稿' }}
                </span>
              </td>
              <td class="p-4 text-text-secondary">{{ article.viewCount || 0 }}</td>
              <td class="p-4 text-text-muted text-sm">{{ formatDate(article.createTime) }}</td>
              <td class="p-4 text-right">
                <router-link :to="`/admin/articles/${article.id}/edit`" class="text-neon-blue hover:underline mr-4">编辑</router-link>
                <button @click="deleteArticle(article.id)" class="text-neon-pink hover:underline">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="!articles.length" class="text-center py-12 text-text-muted">
        暂无文章
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const articles = ref([])

async function fetchArticles() {
  try {
    const res = await adminApi.getAdminArticles()
    articles.value = res.list || res || []
  } catch (err) {
    console.error('Failed to fetch articles:', err)
  }
}

async function deleteArticle(id) {
  if (!confirm('确定要删除这篇文章吗？')) return
  try {
    await adminApi.deleteArticle(id)
    await fetchArticles()
  } catch (err) {
    alert('删除失败')
  }
}

function formatDate(date) {
  return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
}

onMounted(fetchArticles)
</script>
