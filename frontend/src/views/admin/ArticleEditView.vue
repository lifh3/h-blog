<template>
  <div class="article-edit" :class="{ 'immersive-mode': isImmersive }">
    <!-- Header Bar -->
    <div class="edit-header">
      <div class="header-left">
        <button @click="goBack" class="back-btn">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
          </svg>
          返回
        </button>
        <h2 class="text-lg font-semibold text-text-primary">{{ isEdit ? '编辑文章' : '新建文章' }}</h2>
      </div>
      <div class="header-right">
        <button @click="toggleImmersive" class="immersive-btn" :title="isImmersive ? '退出沉浸' : '沉浸编辑'">
          <svg v-if="!isImmersive" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4"/>
          </svg>
          <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
        <button @click="saveDraft" class="action-btn secondary">
          保存草稿
        </button>
        <button @click="publish" class="action-btn primary">
          发布
        </button>
      </div>
    </div>

    <!-- Main Content -->
    <div class="edit-content">
      <!-- Title Input -->
      <div class="title-section">
        <input
          v-model="form.title"
          type="text"
          class="title-input"
          placeholder="文章标题..."
        >
      </div>

      <!-- Editor Area -->
      <div class="editor-section">
        <MarkdownEditor v-model="form.content" />
      </div>
    </div>

    <!-- Sidebar (collapsible) -->
    <div class="edit-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <button @click="sidebarCollapsed = !sidebarCollapsed" class="sidebar-toggle">
        <svg class="w-4 h-4" :class="{ 'rotate-180': sidebarCollapsed }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
        </svg>
      </button>

      <div v-if="!sidebarCollapsed" class="sidebar-content">
        <h3 class="text-lg font-semibold mb-4 text-text-primary">文章设置</h3>

        <div class="settings-group">
          <label class="settings-label">分类</label>
          <select v-model="form.categoryId" class="settings-select">
            <option value="">选择分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
        </div>

        <div class="settings-group">
          <label class="settings-label">标签</label>
          <input
            v-model="form.tags"
            type="text"
            class="settings-input"
            placeholder="用逗号分隔，如: AI, 技术, 教程"
          >
        </div>

        <div class="settings-group">
          <label class="settings-label">封面图 URL</label>
          <input
            v-model="form.coverImage"
            type="text"
            class="settings-input"
            placeholder="https://..."
          >
          <div v-if="form.coverImage" class="cover-preview">
            <img :src="form.coverImage" alt="封面预览" @error="form.coverImage = ''">
          </div>
        </div>

        <div class="settings-group">
          <label class="settings-label">摘要</label>
          <textarea
            v-model="form.summary"
            rows="4"
            class="settings-textarea"
            placeholder="文章摘要，会显示在列表页..."
          ></textarea>
        </div>

        <div class="settings-group">
          <label class="settings-label">状态</label>
          <div class="status-badges">
            <span class="status-badge" :class="{ active: form.status === 1 }">已发布</span>
            <span class="status-badge" :class="{ active: form.status === 0 }">草稿</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { adminApi } from '@/api'
import MarkdownEditor from '@/components/MarkdownEditor.vue'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const isImmersive = ref(false)
const sidebarCollapsed = ref(false)
const categories = ref([])

const form = ref({
  title: '',
  content: '',
  categoryId: '',
  tags: '',
  coverImage: '',
  summary: '',
  status: 0
})

// Watch for immersive mode to collapse sidebar
watch(isImmersive, (val) => {
  if (val) sidebarCollapsed.value = true
})

async function fetchCategories() {
  try {
    const res = await adminApi.getAdminCategories()
    categories.value = res.list || res || []
  } catch (err) {
    console.error('Failed to fetch categories:', err)
  }
}

async function fetchArticle() {
  if (!isEdit.value) return
  try {
    const res = await adminApi.getAdminArticle(route.params.id)
    form.value = {
      ...res,
      tags: (res.tags || []).join(', ')
    }
  } catch (err) {
    console.error('Failed to fetch article:', err)
  }
}

async function saveDraft() {
  form.value.status = 0
  await save()
}

async function publish() {
  form.value.status = 1
  await save()
}

async function save() {
  if (!form.value.title) {
    alert('请输入文章标题')
    return
  }
  if (!form.value.content) {
    alert('请输入文章内容')
    return
  }

  try {
    const data = {
      ...form.value,
      tags: form.value.tags ? form.value.tags.split(',').map(t => t.trim()).filter(Boolean) : []
    }

    if (isEdit.value) {
      await adminApi.updateArticle(route.params.id, data)
    } else {
      await adminApi.createArticle(data)
    }
    router.push('/admin/articles')
  } catch (err) {
    alert('保存失败: ' + (err.response?.data?.message || err.message))
  }
}

function goBack() {
  router.push('/admin/articles')
}

function toggleImmersive() {
  isImmersive.value = !isImmersive.value
}

onMounted(async () => {
  await fetchCategories()
  await fetchArticle()
})
</script>

<style scoped>
.article-edit {
  display: grid;
  grid-template-columns: 1fr 320px;
  grid-template-rows: auto 1fr;
  gap: 0;
  height: calc(100vh - 64px);
  transition: all 0.3s ease;
}

.article-edit.immersive-mode {
  grid-template-columns: 1fr;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background: #0a0a1a;
  height: 100vh;
  padding: 20px;
}

.article-edit.immersive-mode .edit-sidebar {
  display: none;
}

/* Header */
.edit-header {
  grid-column: 1 / -1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: rgba(20, 20, 40, 0.8);
  border-bottom: 1px solid rgba(74, 158, 175, 0.2);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.7);
  background: transparent;
  border: 1px solid rgba(74, 158, 175, 0.3);
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  background: rgba(74, 158, 175, 0.1);
  color: #4a9eaf;
  border-color: #4a9eaf;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.immersive-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.7);
  background: transparent;
  border: 1px solid rgba(74, 158, 175, 0.3);
  cursor: pointer;
  transition: all 0.2s;
}

.immersive-btn:hover {
  background: rgba(74, 158, 175, 0.1);
  color: #4a9eaf;
}

.action-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.secondary {
  background: transparent;
  border: 1px solid rgba(74, 158, 175, 0.3);
  color: rgba(255, 255, 255, 0.8);
}

.action-btn.secondary:hover {
  background: rgba(74, 158, 175, 0.1);
  border-color: #4a9eaf;
}

.action-btn.primary {
  background: linear-gradient(135deg, #4a9eaf, #7c3aed);
  border: none;
  color: white;
}

.action-btn.primary:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

/* Main Content */
.edit-content {
  display: flex;
  flex-direction: column;
  padding: 24px;
  overflow: auto;
}

.title-section {
  margin-bottom: 20px;
}

.title-input {
  width: 100%;
  font-size: 32px;
  font-weight: 700;
  background: transparent;
  border: none;
  outline: none;
  color: #ffffff;
  padding: 12px 0;
  border-bottom: 2px solid transparent;
  transition: border-color 0.2s;
}

.title-input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.title-input:focus {
  border-bottom-color: rgba(74, 158, 175, 0.5);
}

.editor-section {
  flex: 1;
  min-height: 500px;
}

/* Sidebar */
.edit-sidebar {
  position: relative;
  background: rgba(20, 20, 40, 0.6);
  border-left: 1px solid rgba(74, 158, 175, 0.2);
  padding: 24px;
  overflow-y: auto;
  transition: all 0.3s;
}

.edit-sidebar.collapsed {
  padding: 24px 12px;
  width: 48px;
}

.sidebar-toggle {
  position: absolute;
  top: 50%;
  left: -12px;
  transform: translateY(-50%);
  width: 24px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(30, 30, 60, 0.9);
  border: 1px solid rgba(74, 158, 175, 0.3);
  border-radius: 6px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.2s;
}

.sidebar-toggle:hover {
  background: rgba(74, 158, 175, 0.2);
  color: #4a9eaf;
}

.sidebar-toggle .rotate-180 {
  transform: rotate(180deg);
}

.sidebar-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.settings-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.settings-label {
  font-size: 12px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.6);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.settings-select,
.settings-input,
.settings-textarea {
  padding: 10px 14px;
  border-radius: 8px;
  background: rgba(15, 15, 35, 0.8);
  border: 1px solid rgba(74, 158, 175, 0.2);
  color: #ffffff;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.settings-select:focus,
.settings-input:focus,
.settings-textarea:focus {
  border-color: rgba(74, 158, 175, 0.5);
  box-shadow: 0 0 0 3px rgba(74, 158, 175, 0.1);
}

.settings-textarea {
  resize: none;
  line-height: 1.6;
}

.cover-preview {
  margin-top: 8px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(74, 158, 175, 0.2);
}

.cover-preview img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.status-badges {
  display: flex;
  gap: 8px;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  background: rgba(15, 15, 35, 0.8);
  border: 1px solid rgba(74, 158, 175, 0.2);
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.2s;
}

.status-badge.active {
  background: rgba(74, 158, 175, 0.2);
  border-color: #4a9eaf;
  color: #4a9eaf;
}
</style>
