<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-cyber-text">编辑合集</h1>
      <button @click="handleSave" class="btn-cyber" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Settings -->
      <div class="lg:col-span-1 space-y-4">
        <div class="glass-card p-4">
          <h3 class="text-sm font-medium text-cyber-muted mb-3">合集设置</h3>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-cyber-muted mb-1">名称</label>
              <input v-model="form.name" type="text" class="input-cyber" placeholder="合集名称" />
            </div>
            <div>
              <label class="block text-sm text-cyber-muted mb-1">描述</label>
              <textarea v-model="form.description" class="input-cyber" rows="3" placeholder="合集描述"></textarea>
            </div>
            <div>
              <label class="block text-sm text-cyber-muted mb-1">封面图 URL</label>
              <input v-model="form.coverImage" type="text" class="input-cyber" placeholder="https://..." />
            </div>
            <div>
              <label class="block text-sm text-cyber-muted mb-1">排序</label>
              <input v-model.number="form.sort" type="number" class="input-cyber" placeholder="0" />
            </div>
          </div>
        </div>
      </div>

      <!-- Article Selection -->
      <div class="lg:col-span-2">
        <div class="glass-card p-4">
          <h3 class="text-sm font-medium text-cyber-muted mb-3">选择文章</h3>
          
          <div class="space-y-2 max-h-96 overflow-y-auto">
            <label
              v-for="article in availableArticles"
              :key="article.id"
              class="flex items-center gap-3 p-3 rounded-lg hover:bg-cyber-bg cursor-pointer transition-colors"
            >
              <input
                type="checkbox"
                :value="article.id"
                v-model="selectedArticleIds"
                class="w-4 h-4 rounded border-cyber-border text-cyber-cyan focus:ring-cyber-cyan"
              />
              <div class="flex-1">
                <div class="text-cyber-text font-medium">{{ article.title }}</div>
                <div class="text-xs text-cyber-muted">{{ formatDate(article.createTime) }}</div>
              </div>
            </label>
          </div>
          
          <div v-if="availableArticles.length === 0" class="text-center py-8 text-cyber-muted">
            暂无可选文章
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { collectionsAPI, articlesAPI } from '@/api'

const route = useRoute()
const router = useRouter()

const saving = ref(false)
const form = ref({ name: '', description: '', coverImage: '', sort: 0 })
const selectedArticleIds = ref([])
const availableArticles = ref([])

onMounted(async () => {
  await Promise.all([fetchCollection(), fetchArticles()])
})

async function fetchCollection() {
  try {
    const res = await collectionsAPI.get(route.params.id)
    // blog-admin API doesn't unwrap: res.data = {code, data: {series, articles}, message}
    const col = res.data.data || res.data
    form.value = {
      name: col.series?.name || col.name || '',
      description: col.series?.description || col.description || '',
      coverImage: col.series?.coverImage || col.coverImage || '',
      sort: col.series?.sort || col.sort || 0
    }
    selectedArticleIds.value = col.articles?.map(a => a.id) || []
  } catch (error) {
    console.error('Failed to fetch collection:', error)
  }
}

async function fetchArticles() {
  try {
    const res = await articlesAPI.list()
    // blog-admin API doesn't unwrap, structure: res.data = {code, data: {records: [...]}, message}
    availableArticles.value = res.data?.data?.records || []
  } catch (error) {
    console.error('Failed to fetch articles:', error)
  }
}

async function handleSave() {
  saving.value = true
  try {
    await collectionsAPI.update(route.params.id, form.value)
    
    // Update articles
    await collectionsAPI.addArticles(route.params.id, selectedArticleIds.value)
    
    router.push('/collections')
  } catch (error) {
    console.error('Failed to save collection:', error)
  } finally {
    saving.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>
