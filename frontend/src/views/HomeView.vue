<template>
  <div>
    <HeroSection />
    
    <div class="max-w-7xl mx-auto px-4 py-16">
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useBlogStore } from '@/stores/blog'
import HeroSection from '@/components/HeroSection.vue'
import ArticleList from '@/components/ArticleList.vue'
import Sidebar from '@/components/Sidebar.vue'
import TagCloud from '@/components/TagCloud.vue'

const route = useRoute()
const blogStore = useBlogStore()

const articles = ref([])
const categories = ref([])
const tags = ref([])
const hotArticles = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

async function fetchData() {
  loading.value = true
  try {
    const search = route.query.search
    if (search) {
      await blogStore.fetchArticles({ search, page: 1 })
    } else {
      await blogStore.fetchArticles({ page: 1 })
    }
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
    const search = route.query.search
    const params = { page: page.value, ...(search ? { search } : {}) }
    await blogStore.fetchArticles(params)
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

watch(() => route.query.search, fetchData)
</script>
