<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-semibold">评论管理</h2>
    </div>
    
    <div class="glass-card">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-cyber-border">
              <th class="text-left p-4 text-text-muted font-medium">评论内容</th>
              <th class="text-left p-4 text-text-muted font-medium">文章</th>
              <th class="text-left p-4 text-text-muted font-medium">用户</th>
              <th class="text-left p-4 text-text-muted font-medium">状态</th>
              <th class="text-left p-4 text-text-muted font-medium">时间</th>
              <th class="text-right p-4 text-text-muted font-medium">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="comment in comments" :key="comment.id" class="border-b border-cyber-border/50 hover:bg-white/5 transition-colors">
              <td class="p-4">
                <p class="text-text-primary max-w-xs truncate">{{ comment.content }}</p>
              </td>
              <td class="p-4">
                <router-link :to="`/article/${comment.article_id}`" target="_blank" class="text-text-secondary hover:text-neon-blue transition-colors">
                  {{ comment.article_title }}
                </router-link>
              </td>
              <td class="p-4">
                <div class="flex items-center space-x-2">
                  <span class="text-text-primary">{{ comment.nickname }}</span>
                  <span class="text-text-muted text-sm">({{ comment.email }})</span>
                </div>
              </td>
              <td class="p-4">
                <span :class="comment.is_show ? 'text-neon-green' : 'text-neon-pink'" class="text-sm">
                  {{ comment.is_show ? '已显示' : '已隐藏' }}
                </span>
              </td>
              <td class="p-4 text-text-muted text-sm">{{ formatDate(comment.createTime) }}</td>
              <td class="p-4 text-right">
                <button @click="toggleVisible(comment)" class="text-neon-blue hover:underline mr-4">
                  {{ comment.is_show ? '隐藏' : '显示' }}
                </button>
                <button @click="deleteComment(comment.id)" class="text-neon-pink hover:underline">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="!comments.length" class="text-center py-12 text-text-muted">
        暂无评论
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const comments = ref([])

async function fetchComments() {
  try {
    const res = await adminApi.getAdminComments()
    comments.value = res.list || res || []
  } catch (err) {
    console.error('Failed to fetch comments:', err)
  }
}

async function toggleVisible(comment) {
  try {
    await adminApi.updateComment(comment.id, { is_show: !comment.is_show })
    await fetchComments()
  } catch (err) {
    alert('操作失败')
  }
}

async function deleteComment(id) {
  if (!confirm('确定要删除这条评论吗？')) return
  try {
    await adminApi.deleteComment(id)
    await fetchComments()
  } catch (err) {
    alert('删除失败')
  }
}

function formatDate(date) {
  return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
}

onMounted(fetchComments)
</script>
