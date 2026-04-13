<template>
  <div class="max-w-7xl mx-auto px-4 py-12">
    <div class="glass-card p-8 mb-8">
      <div class="flex items-center space-x-4">
        <div class="w-16 h-16 rounded-xl bg-gradient-to-br from-neon-green to-neon-yellow flex items-center justify-center text-2xl">
          🏷️
        </div>
        <div>
          <h1 class="text-2xl font-bold text-text-primary"># {{ tagName }}</h1>
          <p class="text-text-muted">共 {{ articles.length }} 篇文章</p>
        </div>
      </div>
    </div>
    
    <div class="flex flex-col lg:flex-row gap-8">
      <div class="flex-1">
        <ArticleList 
          :articles="articles" 
          :loading="loading"
          :has-more="hasMore"
          @load-more="loadMore"
        />
      </div>
      <div class="lg:w-80">
        <Sidebar :categories="categories" :hot-articles="hotArticles" />
        <div class="mt-6">
          <TagCloud :tags="tags" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useBlogStore } from '@/stores/blog'
import ArticleList from '@/components/ArticleList.vue'
import Sidebar from '@/components/Sidebar.vue'
import TagCloud from '@/components/TagCloud.vue'

const route = useRoute()
const blogStore = useBlogStore()

const articles = ref([])
const categories = ref([])
const tags = ref([])
const hotArticles = ref([])
const tagName = ref('')
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

async function fetchData() {
  loading.value = true
  tagName.value = route.params.name
  try {
    await blogStore.fetchArticlesByTag(route.params.name, { page: 1 })
    articles.value = blogStore.articles
    hasMore.value = blogStore.articles.length >= 10
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  page.value++
  loading.value = true
  try {
    await blogStore.fetchArticlesByTag(route.params.name, { page: page.value })
    articles.value = [...articles.value, ...blogStore.articles]
    hasMore.value = blogStore.articles.length >= 10
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    fetchData(),
    blogStore.fetchCategories().then(() => { categories.value = blogStore.categories }),
    blogStore.fetchTags().then(() => { tags.value = blogStore.tags }),
    blogStore.fetchHotArticles().then(() => { hotArticles.value = blogStore.hotArticles }),
  ])
})

watch(() => route.params.name, fetchData)
</script>
