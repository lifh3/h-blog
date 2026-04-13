<template>
  <div class="max-w-4xl mx-auto px-4 py-12">
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-10 h-10 border-2 border-neon-blue border-t-transparent rounded-full animate-spin"></div>
    </div>
    
    <article v-else-if="article" class="animate-fade-in">
      <div class="relative h-80 rounded-2xl overflow-hidden mb-8">
        <img 
          :src="article.coverImage || 'https://picsum.photos/seed/' + article.id + '/1200/400'" 
          :alt="article.title"
          class="w-full h-full object-cover"
        >
        <div class="absolute inset-0 bg-gradient-to-t from-cyber-dark via-cyber-dark/50 to-transparent"></div>
      </div>
      
      <header class="mb-8 -mt-32 relative z-10 px-4">
        <div class="flex flex-wrap gap-2 mb-4">
          <router-link v-if="article.collectionId" :to="`/collections/${article.collectionId}`" class="tag-cyber bg-neon-purple/20 border-neon-purple/50 text-neon-purple hover:bg-neon-purple/30">
            📚 {{ article.collectionName || '所属合集' }}
          </router-link>
          <router-link v-if="article.categoryId" :to="`/category/${article.categoryId}`" class="tag-cyber">
            {{ article.categoryName || '未分类' }}
          </router-link>
          <span v-for="tag in (article.tags || '').split(',').filter(t => t.trim())" :key="tag" class="tag-cyber text-neon-purple">
            {{ tag.trim() }}
          </span>
        </div>
        <h1 class="text-4xl font-bold mb-4 text-text-primary">{{ article.title }}</h1>
        <div class="flex items-center space-x-6 text-text-muted">
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
      </header>
      
      <div class="glow-line mb-8"></div>
      
      <div class="prose-custom" v-html="renderedContent"></div>
      
      <CommentSection :article-id="article.id" />
      
      <section v-if="relatedArticles.length" class="mt-12">
        <h3 class="text-xl font-semibold mb-6 flex items-center">
          <span class="w-8 h-1 bg-gradient-to-r from-neon-blue to-neon-purple rounded mr-3"></span>
          相关推荐
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <ArticleCard v-for="a in relatedArticles" :key="a.id" :article="a" />
        </div>
      </section>
    </article>
    
    <div v-else class="text-center py-20">
      <p class="text-text-muted text-xl">文章不存在</p>
      <router-link to="/" class="btn-cyber mt-6 inline-block">返回首页</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useBlogStore } from '@/stores/blog'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import CommentSection from '@/components/CommentSection.vue'
import ArticleCard from '@/components/ArticleCard.vue'

const route = useRoute()
const blogStore = useBlogStore()

const article = ref(null)
const relatedArticles = ref([])
const loading = ref(true)

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><div class="copy-btn" onclick="navigator.clipboard.writeText(this.parentElement.innerText)">📋</div><code>' + hljs.highlight(str, { language: lang, ignoreIllegals: true }).value + '</code></pre>'
      } catch (__) {}
    }
    return '<pre class="hljs"><div class="copy-btn" onclick="navigator.clipboard.writeText(this.parentElement.innerText)">📋</div><code>' + md.utils.escapeHtml(str) + '</code></pre>'
  }
})

const renderedContent = computed(() => {
  return article.value ? md.render(article.value.content || '') : ''
})

function formatDate(date) {
  return new Date(date).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

onMounted(async () => {
  loading.value = true
  try {
    await blogStore.fetchArticle(route.params.id)
    article.value = blogStore.currentArticle
    await blogStore.fetchRelatedArticles(route.params.id)
    relatedArticles.value = blogStore.relatedArticles
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.prose-custom {
  @apply text-text-secondary leading-relaxed;
}

.prose-custom :deep(h1),
.prose-custom :deep(h2),
.prose-custom :deep(h3),
.prose-custom :deep(h4) {
  @apply text-text-primary font-semibold mt-8 mb-4;
}

.prose-custom :deep(h2) { @apply text-2xl border-b border-cyber-border pb-2; }
.prose-custom :deep(h3) { @apply text-xl; }

.prose-custom :deep(p) { @apply mb-6; }

.prose-custom :deep(a) { @apply text-neon-blue hover:underline; }

.prose-custom :deep(code) {
  @apply px-1.5 py-0.5 rounded bg-white/10 text-neon-green font-tech text-sm;
}

.prose-custom :deep(pre) {
  @apply relative mb-6 rounded-xl overflow-hidden;
}

.prose-custom :deep(pre .copy-btn) {
  @apply absolute top-2 right-2 px-2 py-1 text-sm cursor-pointer opacity-50 hover:opacity-100;
}

.prose-custom :deep(pre code) {
  @apply p-4 bg-transparent block;
}

.prose-custom :deep(blockquote) {
  @apply border-l-4 border-neon-purple pl-6 my-6 italic text-text-muted;
}

.prose-custom :deep(ul), .prose-custom :deep(ol) { @apply pl-6 mb-6; }
.prose-custom :deep(li) { @apply mb-2; }
.prose-custom :deep(img) { @apply rounded-xl max-w-full h-auto my-6; }
.prose-custom :deep(table) { @apply w-full border-collapse my-6; }
.prose-custom :deep(th), .prose-custom :deep(td) { @apply border border-cyber-border px-4 py-2; }
.prose-custom :deep(th) { @apply bg-cyber-gray text-text-primary; }
</style>
