<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-cyber-text">文章管理</h1>
      <router-link to="/articles/new" class="btn-cyber">新建文章</router-link>
    </div>

    <div class="glass-card">
      <ArticleTable :articles="articles" @delete="handleDelete" @preview="handlePreview" />
    </div>

    <!-- Delete Confirm Modal -->
    <ConfirmModal
      :show="showDeleteModal"
      title="删除文章"
      message="确定要删除这篇文章吗？此操作不可撤销。"
      :loading="deleting"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />

    <!-- Preview Modal -->
    <Teleport to="body">
      <div v-if="showPreviewModal" class="fixed inset-0 z-50 flex items-center justify-center">
        <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="showPreviewModal = false"></div>
        <div class="relative glass-card p-6 w-full max-w-4xl mx-4 max-h-[90vh] overflow-hidden flex flex-col">
          <div class="flex justify-between items-start mb-4">
            <div>
              <h2 class="text-xl font-bold text-cyber-text">{{ previewArticle?.title }}</h2>
              <p class="text-sm text-cyber-muted mt-1">
                {{ previewArticle?.status === 1 ? '已发布' : '草稿' }} · {{ formatDate(previewArticle?.updateTime) }}
              </p>
            </div>
            <button @click="showPreviewModal = false" class="text-cyber-muted hover:text-cyber-text text-2xl">&times;</button>
          </div>
          <div class="flex-1 overflow-auto prose prose-invert max-w-none" v-html="renderedPreviewContent"></div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminArticlesAPI } from '@/api'
import ArticleTable from '@/components/ArticleTable.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import { marked } from 'marked'

const articles = ref([])
const showDeleteModal = ref(false)
const deleting = ref(false)
const currentDeleteId = ref(null)
const showPreviewModal = ref(false)
const previewArticle = ref(null)

marked.setOptions({
  breaks: true,
  gfm: true,
})

const renderedPreviewContent = computed(() => {
  if (!previewArticle.value?.content) return '<p class="text-cyber-muted">暂无内容</p>'
  return marked(previewArticle.value.content)
})

onMounted(async () => {
  await fetchArticles()
})

async function fetchArticles() {
  try {
    const res = await adminArticlesAPI.list()
    articles.value = res.data?.data?.records || []
  } catch (error) {
    console.error('Failed to fetch articles:', error)
  }
}

function handleDelete(id) {
  currentDeleteId.value = id
  showDeleteModal.value = true
}

function handlePreview(article) {
  previewArticle.value = article
  showPreviewModal.value = true
}

async function confirmDelete() {
  deleting.value = true
  try {
    await adminArticlesAPI.delete(currentDeleteId.value)
    await fetchArticles()
    showDeleteModal.value = false
  } catch (error) {
    console.error('Failed to delete article:', error)
  } finally {
    deleting.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.prose {
  color: #e8eef4;
  line-height: 1.7;
}
.prose :deep(h1) { font-size: 1.5rem; font-weight: bold; color: #e8eef4; margin-bottom: 1rem; }
.prose :deep(h2) { font-size: 1.25rem; font-weight: bold; color: #e8eef4; margin-bottom: 0.75rem; }
.prose :deep(h3) { font-size: 1.1rem; font-weight: bold; color: #a8b8c8; margin-bottom: 0.5rem; }
.prose :deep(p) { margin-bottom: 0.75rem; }
.prose :deep(ul), .prose :deep(ol) { padding-left: 1.5rem; margin-bottom: 0.75rem; }
.prose :deep(li) { margin-bottom: 0.25rem; }
.prose :deep(code) { background: #0f1419; color: #4a9eaf; padding: 0.15rem 0.4rem; border-radius: 4px; font-family: 'JetBrains Mono', monospace; font-size: 0.875rem; }
.prose :deep(pre) { background: #0f1419; padding: 1rem; border-radius: 8px; overflow-x: auto; margin-bottom: 1rem; }
.prose :deep(pre code) { background: none; padding: 0; }
.prose :deep(blockquote) { border-left: 3px solid #4a9eaf; padding-left: 1rem; color: #6a8090; margin: 1rem 0; }
.prose :deep(a) { color: #4a9eaf; text-decoration: underline; }
.prose :deep(strong) { color: #e8eef4; font-weight: bold; }
.prose :deep(hr) { border-color: #2d3a4f; margin: 1.5rem 0; }
.prose :deep(table) { width: 100%; border-collapse: collapse; margin-bottom: 1rem; }
.prose :deep(th), .prose :deep(td) { border: 1px solid #2d3a4f; padding: 0.5rem; text-align: left; }
.prose :deep(th) { background: #1a2332; font-weight: bold; }
</style>
