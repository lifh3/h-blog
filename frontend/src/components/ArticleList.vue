<template>
  <div class="grid grid-cols-1 md:grid-cols-2 gap-6" id="articles">
    <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
    <div v-if="articles.length === 0 && !loading" class="col-span-2 text-center py-20">
      <div class="text-6xl mb-4">📭</div>
      <p class="text-text-muted text-lg">暂无文章</p>
    </div>
  </div>
  
  <div v-if="loading" class="flex justify-center py-12">
    <div class="w-8 h-8 border-2 border-neon-blue border-t-transparent rounded-full animate-spin"></div>
  </div>
  
  <div v-if="hasMore && !loading" class="flex justify-center mt-8">
    <button @click="loadMore" class="btn-cyber">
      加载更多
    </button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import ArticleCard from './ArticleCard.vue'

const props = defineProps({
  articles: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  hasMore: { type: Boolean, default: false }
})

const emit = defineEmits(['load-more'])

function loadMore() {
  emit('load-more')
}
</script>
