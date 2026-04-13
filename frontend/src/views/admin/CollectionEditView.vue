<template>
  <div class="p-6">
    <div class="flex items-center gap-4 mb-6">
      <button @click="$router.back()" class="back-btn">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
        </svg>
        返回
      </button>
      <h1 class="text-2xl font-bold text-white">编辑合集</h1>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-10 h-10 border-2 border-neon-blue border-t-transparent rounded-full animate-spin"></div>
    </div>

    <div v-else-if="!collection" class="text-center py-20">
      <p class="text-text-muted text-xl mb-4">合集不存在</p>
      <router-link to="/admin/collections" class="btn-cyber inline-block">返回合集列表</router-link>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 左侧：合集信息 -->
      <div class="lg:col-span-1">
        <div class="glass-card rounded-xl border border-cyber-border p-6 sticky top-6">
          <h2 class="text-lg font-semibold text-white mb-4">合集信息</h2>
          
          <div v-if="collection.coverImage" class="mb-4 rounded-lg overflow-hidden">
            <img :src="collection.coverImage" :alt="collection.name" class="w-full h-40 object-cover">
          </div>
          
          <form @submit.prevent="handleUpdateBasic" class="space-y-4">
            <div>
              <label class="block text-sm text-text-secondary mb-2">名称</label>
              <input v-model="form.name" type="text" class="input-cyber w-full">
            </div>
            
            <div>
              <label class="block text-sm text-text-secondary mb-2">描述</label>
              <textarea v-model="form.description" rows="3" class="input-cyber w-full resize-none"></textarea>
            </div>
            
            <div>
              <label class="block text-sm text-text-secondary mb-2">封面图 URL</label>
              <input v-model="form.coverImage" type="url" class="input-cyber w-full">
            </div>
            
            <div>
              <label class="block text-sm text-text-secondary mb-2">排序</label>
              <input v-model.number="form.sort" type="number" class="input-cyber w-full">
            </div>
            
            <button type="submit" :disabled="savingBasic" class="btn-cyber w-full">
              {{ savingBasic ? '保存中...' : '保存修改' }}
            </button>
          </form>
        </div>
      </div>

      <!-- 右侧：文章列表 -->
      <div class="lg:col-span-2">
        <div class="glass-card rounded-xl border border-cyber-border p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-white">
              包含文章 ({{ articles.length }})
            </h2>
            <button @click="showAddArticleModal = true" class="btn-cyber-outline px-4 py-2 rounded-lg text-sm">
              添加文章
            </button>
          </div>

          <!-- 文章列表 -->
          <div class="space-y-3">
            <div v-for="(article, index) in articles" :key="article.id"
                 class="p-4 rounded-lg border border-cyber-border/50 bg-cyber-dark/30 hover:bg-white/5 transition-colors">
              <div class="flex items-start gap-4">
                <div class="flex-shrink-0 w-8 h-8 rounded-lg bg-neon-blue/20 flex items-center justify-center text-neon-blue font-bold text-sm">
                  {{ index + 1 }}
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between gap-4">
                    <div class="flex-1 min-w-0">
                      <h3 class="font-medium text-text-primary truncate">{{ article.title }}</h3>
                      <p class="text-sm text-text-muted mt-1">
                        {{ formatDate(article.createTime) }} · {{ article.viewCount || 0 }} 阅读
                      </p>
                    </div>
                    <div class="flex items-center gap-2 shrink-0">
                      <button @click="removeArticle(article.id)" class="text-neon-pink hover:underline text-sm">
                        移除
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="articles.length === 0" class="text-center py-12 text-text-muted">
              <div class="text-4xl mb-4">📚</div>
              <p>暂无文章，点击上方按钮添加</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加文章模态框 -->
    <div v-if="showAddArticleModal" 
         class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
         @click.self="showAddArticleModal = false">
      <div class="glass-card rounded-xl p-6 w-full max-w-2xl max-h-[80vh] overflow-hidden flex flex-col">
        <h2 class="text-xl font-bold text-white mb-4">添加文章</h2>
        
        <!-- 搜索框 -->
        <div class="mb-4">
          <input v-model="searchQuery" type="text" 
                 class="input-cyber w-full" 
                 placeholder="搜索文章标题..."
                 @input="filterArticles">
        </div>

        <!-- 可选文章列表 -->
        <div class="flex-1 overflow-y-auto space-y-2">
          <div v-for="article in filteredAvailableArticles" :key="article.id"
               @click="addArticle(article)"
               class="p-3 rounded-lg border border-cyber-border/50 hover:border-neon-cyan hover:bg-neon-cyan/10 cursor-pointer transition-colors">
            <h3 class="font-medium text-text-primary">{{ article.title }}</h3>
            <p class="text-sm text-text-muted mt-1">
              {{ formatDate(article.createTime) }} · {{ article.viewCount || 0 }} 阅读
            </p>
          </div>
          
          <div v-if="filteredAvailableArticles.length === 0" class="text-center py-8 text-text-muted">
            没有更多可选文章
          </div>
        </div>

        <button @click="showAddArticleModal = false" class="btn-cyber-outline mt-4">
          关闭
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { adminApi } from '@/api'

const route = useRoute()
const router = useRouter()

const collection = ref(null)
const articles = ref([])
const allArticles = ref([])
const loading = ref(true)
const savingBasic = ref(false)
const showAddArticleModal = ref(false)
const searchQuery = ref('')

const form = ref({
  name: '',
  description: '',
  coverImage: '',
  sort: 0
})

const filteredAvailableArticles = computed(() => {
  const query = searchQuery.value.toLowerCase()
  const articleIds = new Set(articles.value.map(a => a.id))
  return allArticles.value
    .filter(a => !articleIds.has(a.id))
    .filter(a => !query || a.title.toLowerCase().includes(query))
})

async function fetchData() {
  loading.value = true
  try {
    const collectionId = route.params.id
    
    // 获取合集详情
    const collectionRes = await adminApi.getAdminCollection(collectionId)
    console.log('Collection API response:', collectionRes)
    // API响应拦截器返回的是 axios res.data，也就是后端的完整响应 {code, data, message}
    // 后端返回格式: {code: 200, data: {series: {...}, articles: [...]}, message: "..."}
    const responseData = collectionRes.data || collectionRes
    console.log('Response data:', responseData)
    
    if (responseData.series) {
      collection.value = responseData.series
      articles.value = responseData.articles || []
      console.log('Articles in collection:', articles.value)
    } else {
      // 如果是直接返回合集对象
      collection.value = responseData
      articles.value = []
      console.log('No series found, using responseData directly')
    }
    
    form.value = {
      name: collection.value.name,
      description: collection.value.description || '',
      coverImage: collection.value.coverImage || '',
      sort: collection.value.sort || 0
    }
    
    // 获取所有文章供选择
    const allArticlesRes = await adminApi.getAdminArticles({ pageSize: 100 })
    console.log('All articles API response:', allArticlesRes)
    const allData = allArticlesRes.data || allArticlesRes
    console.log('All articles data:', allData)
    allArticles.value = allData.records || allData || []
    console.log('All articles array:', allArticles.value)
  } catch (err) {
    console.error('Failed to fetch collection:', err)
  } finally {
    loading.value = false
  }
}

async function handleUpdateBasic() {
  savingBasic.value = true
  try {
    await adminApi.updateCollection(route.params.id, form.value)
    collection.value = { ...collection.value, ...form.value }
    alert('保存成功')
  } catch (err) {
    alert('保存失败: ' + (err.message || err))
  } finally {
    savingBasic.value = false
  }
}

function addArticle(article) {
  articles.value.push(article)
  showAddArticleModal.value = false
  
  // 调用API更新合集中的文章（如果后端支持）
  // 这里可能需要调用一个专门的API来添加文章到合集
}

function removeArticle(articleId) {
  if (!confirm('确定要移除这篇文章吗？')) return
  articles.value = articles.value.filter(a => a.id !== articleId)
  
  // 调用API移除文章（如果后端支持）
  // 这里可能需要调用一个专门的API来从合集中移除文章
}

function filterArticles() {
  // 搜索已经在computed中处理
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { 
    year: 'numeric', month: 'short', day: 'numeric' 
  })
}

onMounted(() => {
  fetchData()
})
</script>
