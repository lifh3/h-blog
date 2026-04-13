<template>
  <div class="overflow-x-auto">
    <table class="table-cyber">
      <thead>
        <tr>
          <th>标题</th>
          <th>分类</th>
          <th>合集</th>
          <th>状态</th>
          <th>时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="article in articles" :key="article.id">
          <td class="font-medium text-cyber-text">{{ article.title }}</td>
          <td>
            <span v-if="article.categoryName" class="badge badge-info">{{ article.categoryName }}</span>
            <span v-else class="text-cyber-muted">-</span>
          </td>
          <td>
            <span v-if="article.collectionName" class="badge badge-purple">{{ article.collectionName }}</span>
            <span v-else class="text-cyber-muted">-</span>
          </td>
          <td>
            <span :class="['badge', article.status === 1 ? 'badge-success' : 'badge-warning']">
              {{ article.status === 1 ? '已发布' : '草稿' }}
            </span>
          </td>
          <td class="text-cyber-muted">{{ formatDate(article.updateTime) }}</td>
          <td>
            <div class="flex gap-2">
              <button @click="$emit('preview', article)" class="text-cyber-yellow hover:underline text-sm">
                预览
              </button>
              <router-link :to="`/articles/${article.id}/edit`" class="text-cyber-cyan hover:underline text-sm">
                编辑
              </router-link>
              <button @click="$emit('delete', article.id)" class="text-cyber-red hover:underline text-sm">
                删除
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="articles.length === 0" class="text-center py-8 text-cyber-muted">
      暂无文章
    </div>
  </div>
</template>

<script setup>
defineProps({
  articles: { type: Array, default: () => [] }
})

defineEmits(['delete', 'preview'])

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>
