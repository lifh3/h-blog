<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-cyber-text">评论管理</h1>
    </div>

    <!-- 搜索面板 -->
    <div class="glass-card rounded-xl border border-cyber-border p-5 mb-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-sm font-medium text-gray-300 flex items-center gap-2">
          <span>🔍</span> 搜索筛选
        </h3>
        <button @click="showSearch = !showSearch" class="text-xs text-cyber-muted hover:text-cyber-cyan transition-colors">
          {{ showSearch ? '收起' : '展开' }}
        </button>
      </div>

      <div v-if="showSearch" class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm text-gray-300 mb-2">文章ID</label>
          <input v-model="searchForm.articleId" type="number"
            class="w-full bg-cyber-surface border border-cyber-border rounded-lg px-3 py-2 text-sm text-gray-200 focus:border-cyber-cyan focus:outline-none placeholder-gray-500"
            placeholder="输入文章ID">
        </div>
        <div>
          <label class="block text-sm text-gray-300 mb-2">开始时间</label>
          <input v-model="searchForm.startDate" type="datetime-local"
            class="w-full bg-cyber-surface border border-cyber-border rounded-lg px-3 py-2 text-sm text-gray-200 focus:border-cyber-cyan focus:outline-none">
        </div>
        <div>
          <label class="block text-sm text-gray-300 mb-2">结束时间</label>
          <input v-model="searchForm.endDate" type="datetime-local"
            class="w-full bg-cyber-surface border border-cyber-border rounded-lg px-3 py-2 text-sm text-gray-200 focus:border-cyber-cyan focus:outline-none">
        </div>
        <div class="md:col-span-3 flex gap-2">
          <button @click="handleSearch" class="btn-cyber-outline px-4 py-2 rounded-lg text-sm">搜索</button>
          <button @click="resetSearch" class="btn-cyber-outline px-4 py-2 rounded-lg text-sm">重置</button>
        </div>
      </div>
    </div>

    <div class="glass-card">
      <table class="table-cyber">
        <thead>
          <tr>
            <th>内容</th>
            <th>文章</th>
            <th>用户</th>
            <th>时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in comments" :key="item.id">
            <td class="max-w-xs truncate">{{ item.content }}</td>
            <td class="text-cyber-muted">{{ item.articleId }}</td>
            <td class="text-cyber-muted">{{ item.nickname || '匿名' }}</td>
            <td class="text-cyber-muted">{{ formatDate(item.createTime) }}</td>
            <td>
              <span :class="['badge', item.status === 1 ? 'badge-success' : 'badge-warning']">
                {{ item.status === 1 ? '已审核' : '待审核' }}
              </span>
            </td>
            <td>
              <div class="flex gap-2">
                <button @click="openPreview(item)" class="text-cyber-cyan hover:underline text-sm">预览</button>
                <button v-if="item.status !== 1" @click="handleApprove(item.id)" class="text-cyber-green hover:underline text-sm">通过</button>
                <button @click="handleDelete(item.id)" class="text-cyber-red hover:underline text-sm">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="comments.length === 0" class="text-center py-8 text-cyber-muted">暂无评论</div>
    </div>

    <ConfirmModal
      :show="showDeleteModal"
      title="删除评论"
      message="确定要删除此评论吗？"
      :loading="deleting"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />

    <!-- 预览弹窗 -->
    <div v-if="showPreviewModal" class="fixed inset-0 bg-black/70 flex items-center justify-center z-50" @click.self="closePreview">
      <div class="glass-card rounded-xl border border-cyber-border w-full max-w-4xl max-h-[90vh] overflow-hidden flex flex-col">
        <div class="flex items-center justify-between p-5 border-b border-cyber-border">
          <h3 class="text-lg font-semibold text-gray-100">评论预览</h3>
          <button @click="closePreview" class="text-gray-400 hover:text-white">✕</button>
        </div>
        <div class="overflow-y-auto p-5 flex-1">
          <!-- 当前评论 -->
          <div class="mb-6">
            <div class="text-xs text-cyber-muted mb-2">当前评论 #{{ selectedCommentId }}</div>
            <div class="bg-cyber-bg rounded-lg p-4 border-l-2 border-cyber-cyan">
              <div class="flex items-center gap-2 mb-2">
                <span class="text-sm font-medium text-gray-200">{{ currentComment?.nickname || '匿名' }}</span>
                <span class="text-xs text-cyber-muted">{{ formatDate(currentComment?.createTime) }}</span>
                <span v-if="currentComment?.replyTo" class="text-xs text-cyber-muted">→ 回复 {{ currentComment.replyTo }}</span>
              </div>
              <div class="text-gray-200">{{ currentComment?.content }}</div>
            </div>
          </div>

          <!-- 文章信息 -->
          <div class="mb-6">
            <div class="text-xs text-cyber-muted mb-2">所属文章</div>
            <div class="text-sm text-gray-300">
              文章ID: <span class="text-cyber-cyan">{{ currentComment?.articleId }}</span>
            </div>
          </div>

          <!-- 同文章其他评论 -->
          <div>
            <div class="text-xs text-cyber-muted mb-2">该文章的全部评论 ({{ articleComments.length }} 条)</div>
            <div class="space-y-3">
              <div v-for="c in articleComments" :key="c.id"
                :class="['rounded-lg p-3 border-l-2 cursor-pointer transition-colors', c.id === selectedCommentId ? 'bg-cyber-cyan/10 border-cyber-cyan' : 'bg-cyber-bg border-cyber-border hover:border-cyber-cyan/50']"
                @click="selectedCommentId = c.id">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-sm font-medium text-gray-200">{{ c.nickname || '匿名' }}</span>
                  <span class="text-xs text-cyber-muted">{{ formatDate(c.createTime) }}</span>
                  <span v-if="c.replyTo" class="text-xs text-cyber-muted">→ 回复 {{ c.replyTo }}</span>
                  <span v-if="c.id === selectedCommentId" class="text-xs text-cyber-cyan font-medium">← 当前</span>
                </div>
                <div :class="['text-sm', c.id === selectedCommentId ? 'text-gray-200' : 'text-gray-400']">{{ c.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { commentsAPI } from '@/api'
import ConfirmModal from '@/components/ConfirmModal.vue'

const comments = ref([])
const showDeleteModal = ref(false)
const deleting = ref(false)
const currentDeleteId = ref(null)

// 搜索相关
const showSearch = ref(false)
const searchForm = ref({
  articleId: null,
  startDate: '',
  endDate: ''
})

// 预览相关
const showPreviewModal = ref(false)
const selectedCommentId = ref(null)
const articleComments = ref([])

// 计算当前选中的评论
const currentComment = computed(() => {
  if (!selectedCommentId.value || articleComments.value.length === 0) return null
  return articleComments.value.find(c => c.id === selectedCommentId.value) || null
})

onMounted(() => fetchComments())

async function fetchComments(params = {}) {
  try {
    const res = params && Object.keys(params).length > 0
      ? await commentsAPI.search(params)
      : await commentsAPI.list()
    comments.value = res.data.data || []
  } catch (error) {
    console.error('Failed to fetch comments:', error)
  }
}

async function handleSearch() {
  const params = {}
  if (searchForm.value.articleId) {
    params.articleId = searchForm.value.articleId
  }
  if (searchForm.value.startDate) {
    params.startDate = new Date(searchForm.value.startDate).toISOString()
  }
  if (searchForm.value.endDate) {
    params.endDate = new Date(searchForm.value.endDate).toISOString()
  }
  await fetchComments(params)
}

function resetSearch() {
  searchForm.value = { articleId: null, startDate: '', endDate: '' }
  fetchComments()
}

async function openPreview(comment) {
  selectedCommentId.value = comment.id
  showPreviewModal.value = true
  // 获取同文章的所有评论
  try {
    const res = await commentsAPI.getByArticle(comment.articleId)
    articleComments.value = res.data.data || []
  } catch (error) {
    console.error('Failed to fetch article comments:', error)
    articleComments.value = []
  }
}

function closePreview() {
  showPreviewModal.value = false
  selectedCommentId.value = null
  articleComments.value = []
}

async function handleApprove(id) {
  try {
    await commentsAPI.approve(id)
    await fetchComments()
  } catch (error) {
    console.error('Failed to approve comment:', error)
  }
}

function handleDelete(id) {
  currentDeleteId.value = id
  showDeleteModal.value = true
}

async function confirmDelete() {
  deleting.value = true
  try {
    await commentsAPI.delete(currentDeleteId.value)
    await fetchComments()
    showDeleteModal.value = false
  } catch (error) {
    console.error('Failed to delete comment:', error)
  } finally {
    deleting.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>
