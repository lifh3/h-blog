import { defineStore } from 'pinia'
import { ref } from 'vue'
import { publicApi } from '@/api'

export const useBlogStore = defineStore('blog', () => {
  const articles = ref([])
  const currentArticle = ref(null)
  const categories = ref([])
  const tags = ref([])
  const hotArticles = ref([])
  const relatedArticles = ref([])
  const stats = ref({ articles: 0, categories: 0, tags: 0, views: 0 })
  const collections = ref([])
  const currentCollection = ref(null)
  const loading = ref(false)

  function extract(res) {
    // API响应拦截器已经返回 res.data，所以直接返回即可
    return res.data ?? res
  }

  async function fetchArticles(params = {}) {
    loading.value = true
    try {
      const res = await publicApi.getArticles(params)
      const data = extract(res)
      articles.value = data?.records ?? data ?? []
    } finally {
      loading.value = false
    }
  }

  async function fetchArticlesByTag(name, params = {}) {
    loading.value = true
    try {
      const res = await publicApi.getTagArticles(name, params)
      const data = extract(res)
      articles.value = data?.records ?? data ?? []
    } finally {
      loading.value = false
    }
  }

  async function fetchArticle(id) {
    loading.value = true
    try {
      const res = await publicApi.getArticle(id)
      currentArticle.value = extract(res)
    } finally {
      loading.value = false
    }
  }

  async function fetchRelatedArticles(id) {
    const res = await publicApi.getRelatedArticles(id)
    relatedArticles.value = extract(res) ?? []
  }

  async function fetchCategories() {
    const res = await publicApi.getCategories()
    categories.value = extract(res) ?? []
  }

  async function fetchTags() {
    const res = await publicApi.getTags()
    tags.value = extract(res) ?? []
  }

  async function fetchHotArticles() {
    const res = await publicApi.getHotArticles()
    const data = extract(res)
    hotArticles.value = data?.records ?? data ?? []
  }

  async function fetchStats() {
    const res = await publicApi.getStats()
    stats.value = extract(res) ?? { articles: 0, categories: 0, tags: 0, views: 0 }
  }

  async function fetchArticlesByCategory(id, params = {}) {
    loading.value = true
    try {
      const res = await publicApi.getCategoryArticles(id, params)
      const data = extract(res)
      articles.value = data?.records ?? data ?? []
    } finally {
      loading.value = false
    }
  }

  async function fetchCollections() {
    const res = await publicApi.getCollections()
    collections.value = extract(res) ?? []
  }

  async function fetchCollection(id) {
    loading.value = true
    try {
      const res = await publicApi.getCollection(id)
      // API响应拦截器返回的是 axios res.data，所以 res 是后端完整响应 {code, data, message}
      // 后端返回格式: {code: 200, data: {series: {...}, articles: [...]}, message: "..."}
      const responseData = res.data || res
      
      if (responseData.series) {
        currentCollection.value = {
          ...responseData.series,
          articles: responseData.articles || []
        }
      } else {
        currentCollection.value = responseData
      }
    } finally {
      loading.value = false
    }
  }

  return {
    articles, currentArticle, categories, tags, hotArticles,
    relatedArticles, stats, loading, collections, currentCollection,
    fetchArticles, fetchArticle, fetchRelatedArticles,
    fetchCategories, fetchTags, fetchHotArticles, fetchStats,
    fetchArticlesByCategory, fetchArticlesByTag,
    fetchCollections, fetchCollection
  }
})
