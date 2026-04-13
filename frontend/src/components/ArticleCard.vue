<template>
  <router-link :to="`/article/${article.id}`" class="glass-card rounded-2xl overflow-hidden transition-all duration-500 glass-card-hover group">
    <div class="relative h-48 overflow-hidden">
      <img 
        :src="article.coverImage || 'https://picsum.photos/seed/' + article.id + '/800/400'" 
        :alt="article.title"
        class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
      >
      <div class="absolute inset-0 bg-gradient-to-t from-cyber-card via-transparent to-transparent"></div>
      <div class="absolute top-4 left-4 flex flex-wrap gap-2">
        <span v-if="article.collectionId" class="tag-cyber bg-neon-purple/20 border-neon-purple/50 text-neon-purple text-xs">
          📚 {{ article.collectionName || '合集' }}
        </span>
        <span v-if="article.categoryName" class="tag-cyber">{{ article.categoryName }}</span>
      </div>
    </div>
    
    <div class="p-5">
      <h3 class="text-lg font-semibold text-text-primary mb-3 line-clamp-2 group-hover:text-neon-blue transition-colors">
        {{ article.title }}
      </h3>
      <p class="text-text-secondary text-sm mb-4 line-clamp-2">
        {{ article.summary || article.content?.slice(0, 100) + '...' }}
      </p>
      
      <div class="flex items-center justify-between text-text-muted text-sm">
        <div class="flex items-center space-x-4">
          <span class="flex items-center">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
            </svg>
            {{ article.viewCount || 0 }}
          </span>
          <span>{{ formatDate(article.createTime) }}</span>
        </div>
      </div>
      
      <div v-if="article.tags" class="flex flex-wrap gap-2 mt-4">
        <span v-for="tag in article.tags.split(',').filter(t => t.trim()).slice(0, 3)" :key="tag" class="text-xs text-neon-purple">
          #{{ tag.trim() }}
        </span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
defineProps({
  article: {
    type: Object,
    required: true
  }
})

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
