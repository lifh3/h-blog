<template>
  <div class="p-6 space-y-6">

    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-white">AI智能发布</h1>
        <p class="text-cyber-muted mt-1">热点追踪 | 一键生成 | 本地项目分析</p>
      </div>
      <button @click="fetchAll" :disabled="loadingTopics" class="btn-cyber-outline px-4 py-2 rounded-lg text-sm">
        {{ loadingTopics ? '加载中...' : '刷新话题' }}
      </button>
    </div>

    <!-- 设置面板 -->
    <div class="glass-card rounded-xl border border-cyber-border p-5">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-base font-semibold text-gray-100 flex items-center gap-2">
          <span>⚙️</span> AI 发布设置
        </h3>
        <button @click="showSettings = !showSettings" class="text-xs text-cyber-muted hover:text-cyber-cyan transition-colors">
          {{ showSettings ? '收起' : '展开' }}
        </button>
      </div>

      <div v-if="showSettings" class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- AI 发布设置 -->
        <div class="space-y-4">
          <h4 class="text-sm font-medium text-gray-300 border-b border-cyber-border pb-2">AI 发布设置</h4>
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm text-gray-300">自动发布</div>
              <div class="text-xs text-gray-500">生成文章后自动发布，无需手动确认</div>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input v-model="settings.aiPublish.autoPublish" type="checkbox" class="sr-only peer">
              <div class="w-11 h-6 bg-dark-3 rounded-full peer peer-checked:bg-cyber-cyan/30 peer-checked:after:translate-x-full after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-cyber-muted peer-checked:after:bg-cyber-cyan after:rounded-full after:h-5 after:w-5 after:transition-all"></div>
            </label>
          </div>
          <div>
            <label class="block text-sm text-gray-300 mb-2">默认分类</label>
            <select v-model="settings.aiPublish.defaultCategoryId" class="w-full bg-cyber-surface border border-cyber-border rounded-lg px-3 py-2 text-sm text-gray-200 focus:border-cyber-cyan focus:outline-none">
              <option value="">由AI自动选择</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <button @click="saveAiPublishSettings" :disabled="savingSettings" class="btn-cyber-outline px-4 py-2 rounded-lg text-sm">
            {{ savingSettings ? '保存中...' : '保存设置' }}
          </button>
        </div>

        <!-- 定时任务设置 -->
        <div class="space-y-4">
          <h4 class="text-sm font-medium text-gray-300 border-b border-cyber-border pb-2">定时任务设置</h4>
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm text-gray-300">启用定时任务</div>
              <div class="text-xs text-gray-500">每天自动生成并发布文章</div>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input v-model="settings.scheduler.enabled" type="checkbox" class="sr-only peer">
              <div class="w-11 h-6 bg-dark-3 rounded-full peer peer-checked:bg-cyber-cyan/30 peer-checked:after:translate-x-full after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-cyber-muted peer-checked:after:bg-cyber-cyan after:rounded-full after:h-5 after:w-5 after:transition-all"></div>
            </label>
          </div>
          <div>
            <label class="block text-sm text-gray-300 mb-2">执行时间 (Cron)</label>
            <input v-model="settings.scheduler.cron" type="text"
              class="w-full bg-cyber-surface border border-cyber-border rounded-lg px-3 py-2 text-sm text-gray-200 focus:border-cyber-cyan focus:outline-none font-mono placeholder-gray-500"
              placeholder="0 0 9 * * ?">
            <div class="text-xs text-gray-500 mt-1">示例: 0 0 9 * * ? = 每天9点执行</div>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm text-gray-300">自动发布</div>
              <div class="text-xs text-gray-500">定时任务生成文章后自动发布</div>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input v-model="settings.scheduler.autoPublish" type="checkbox" class="sr-only peer">
              <div class="w-11 h-6 bg-dark-3 rounded-full peer peer-checked:bg-cyber-cyan/30 peer-checked:after:translate-x-full after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-cyber-muted peer-checked:after:bg-cyber-cyan after:rounded-full after:h-5 after:w-5 after:transition-all"></div>
            </label>
          </div>
          <button @click="saveSchedulerSettings" :disabled="savingSettings" class="btn-cyber-outline px-4 py-2 rounded-lg text-sm">
            {{ savingSettings ? '保存中...' : '保存定时任务设置' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Four Entry Points -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">

      <!-- Entry 1: 总结今日热点 -->
      <button @click="generateSummary" :disabled="loading"
        class="glass-card rounded-xl border border-cyber-border p-6 text-left hover:border-cyber-cyan/60 transition-all disabled:opacity-50 group">
        <div class="text-4xl mb-3">📰</div>
        <div class="text-lg font-bold text-gray-100 mb-1">今日热点综述</div>
        <div class="text-sm text-gray-400">AI将所有热点话题<br />汇总为一篇综合文章</div>
        <div v-if="loading && activeMode === 'summary'" class="mt-3 text-xs text-cyber-cyan animate-pulse">
          正在生成综述文章...
        </div>
      </button>

      <!-- Entry 2: By Direction -->
      <div class="glass-card rounded-xl border border-cyber-border p-6 min-h-[220px]">
        <div class="text-4xl mb-3">🎯</div>
        <div class="text-lg font-bold text-gray-100 mb-1">按方向生成</div>
        <div class="text-sm text-gray-400 mb-3">选择一个平台方向</div>
        <div class="flex flex-wrap gap-1.5">
          <button v-for="p in PLATFORMS" :key="p.id" @click="pickDirection(p)" :disabled="loading" :class="['px-2 py-1 rounded text-xs border transition-all',
            selectedDirection === p.id
              ? 'bg-cyber-cyan/20 border-cyber-cyan text-cyber-cyan'
              : 'bg-dark-3 border-cyber-border text-cyber-muted hover:border-cyber-cyan']">
            {{ p.icon }} {{ p.label }}
          </button>
        </div>
        <!-- Topic list for selected direction -->
        <div v-if="selectedDirection && directionTopics.length > 0" class="mt-3 space-y-1.5">
          <div v-for="topic in directionTopics" :key="topic" @click="generateFromTopic(topic)" :disabled="loading"
            class="bg-dark-3 border border-cyber-border rounded-lg px-3 py-2 cursor-pointer hover:border-cyber-cyan/60 transition-all text-sm text-white flex items-center justify-between group disabled:opacity-50">
            <span class="truncate flex-1 mr-2">{{ String(topic).substring(0, 25) }}{{ String(topic).length > 25 ? '...'
              : '' }}</span>
            <span v-if="loading && activeTopic === String(topic)"
              class="text-xs text-cyber-cyan animate-pulse shrink-0">写作中</span>
            <span v-else class="text-cyber-cyan text-xs opacity-0 group-hover:opacity-100 shrink-0">写入</span>
          </div>
        </div>
        <div v-if="loading && activeMode === 'direction'" class="mt-2 text-xs text-cyber-cyan animate-pulse">
          AI正在写作，请稍候...
        </div>
      </div>

      <!-- Entry 3: 自定义话题 -->
      <div class="glass-card rounded-xl border border-cyber-border p-6">
        <div class="text-4xl mb-3">💬</div>
        <div class="text-lg font-bold text-gray-100 mb-1">自定义话题</div>
        <div class="text-sm text-gray-400 mb-3">输入任意话题让AI生成文章</div>
        <input v-model="customTopic" @keyup.enter="generateCustom" placeholder="例如：AI Agent发展趋势"
          class="w-full bg-dark-3 border border-cyber-border rounded-lg px-3 py-2 text-sm text-white placeholder-gray-600 focus:border-cyber-cyan focus:outline-none mb-2" />
        <button @click="generateCustom" :disabled="loading || !customTopic.trim()"
          class="w-full btn-cyber-outline py-2 rounded-lg text-sm disabled:opacity-50">
          {{ loading && activeMode === 'custom' ? '生成中...' : '生成文章' }}
        </button>
      </div>

      <!-- Entry 4: 本地项目分析 -->
      <div class="glass-card rounded-xl border border-cyber-border p-6">
        <div class="text-4xl mb-3">📁</div>
        <div class="text-lg font-bold text-gray-100 mb-1">本地项目分析</div>
        <div class="text-sm text-gray-400 mb-3">选择项目目录，AI分析生成文章</div>

        <!-- Directory Selector -->
        <div class="relative">
          <input
            ref="fileInputRef"
            type="file"
            webkitdirectory
            directory
            multiple
            @change="handleDirectorySelect"
            class="hidden"
          />
          <button
            @click="triggerFileSelect"
            :disabled="loading && activeMode !== 'local'"
            class="w-full btn-cyber-outline py-2 rounded-lg text-sm flex items-center justify-center gap-2"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"/>
            </svg>
            选择项目目录
          </button>
        </div>

        <!-- Selected Files Preview -->
        <div v-if="selectedFiles.length > 0" class="mt-3 max-h-24 overflow-y-auto space-y-1">
          <div class="flex items-center justify-between text-xs">
            <span class="text-cyber-cyan">{{ selectedFiles.length }} 个文件已选择</span>
            <button @click="clearFiles" class="text-gray-500 hover:text-red-400">清除</button>
          </div>
          <div v-for="(file, idx) in selectedFiles.slice(0, 5)" :key="idx" class="text-xs text-gray-500 truncate">
            {{ getFileIcon(file.name) }} {{ file.relativePath || file.name }}
          </div>
          <div v-if="selectedFiles.length > 5" class="text-xs text-gray-600">
            ... 还有 {{ selectedFiles.length - 5 }} 个文件
          </div>
        </div>

        <!-- Instruction Input -->
        <textarea
          v-model="localProjectPrompt"
          @keyup.enter.ctrl="generateFromLocalProject"
          placeholder="输入分析要求，如：分析这个项目的技术架构、主要功能和亮点..."
          rows="2"
          class="w-full bg-dark-3 border border-cyber-border rounded-lg px-3 py-2 text-sm text-white placeholder-gray-600 focus:border-cyber-cyan focus:outline-none mt-3 resize-none"
        ></textarea>

        <button
          @click="generateFromLocalProject"
          :disabled="loading || selectedFiles.length === 0 || !localProjectPrompt.trim()"
          class="w-full btn-cyber mt-2 py-2 rounded-lg text-sm disabled:opacity-50"
        >
          {{ loading && activeMode === 'local' ? '分析中...' : '分析并生成' }}
        </button>
      </div>
    </div>

    <!-- All Hot Topics + Batch Generate -->
    <div class="glass-card rounded-xl border border-cyber-border p-5">
      <div class="flex items-center justify-between mb-3">
        <div>
          <h3 class="text-base font-semibold text-gray-100">全部热点话题</h3>
          <p class="text-xs text-gray-500 mt-0.5">共 {{ topics.length }} 个话题，批量生成文章</p>
        </div>
        <span v-if="loadingTopics" class="animate-pulse text-xs text-cyber-cyan">加载中...</span>
        <button v-if="!loadingTopics" @click="batchGenerate" :disabled="loadingBatch || topics.length === 0"
          class="btn-cyber-outline px-3 py-1.5 rounded-lg text-xs">
          批量生成全部
        </button>
      </div>
      <!-- Batch generate progress bar -->
      <div v-if="loadingBatch" class="mb-3">
        <div class="flex items-center gap-2 text-xs text-cyber-cyan mb-1">
          <span class="animate-spin">等待</span>
          <span>AI正在为每个话题生成文章... ({{ batchProgress }}/{{ batchTotal }})</span>
        </div>
        <div class="w-full bg-dark-3 rounded-full h-1.5">
          <div class="bg-cyber-cyan h-1.5 rounded-full transition-all"
            :style="{ width: batchTotal > 0 ? `${(batchProgress / batchTotal * 100)}%` : '0%' }"></div>
        </div>
      </div>
      <!-- Batch results summary -->
      <div v-if="batchResults.length > 0" class="mb-3 space-y-2">
        <div class="flex gap-3 text-xs">
          <span class="text-green-400">成功 {{batchResults.filter(a => a.status === 0 || a.status === 1).length}}
            篇</span>
          <span class="text-yellow-400">草稿 {{batchResults.filter(a => a.status === 0).length}}</span>
          <span class="text-red-400">失败 {{batchResults.filter(a => a.status === -1).length}}</span>
        </div>
      </div>
      <!-- Topic chips -->
      <div class="flex flex-wrap gap-2 max-h-[200px] overflow-y-auto">
        <span v-for="(topic, idx) in topics" :key="idx" @click="generateFromTopic(topic)" class="topic-chip">
          {{ String(topic).substring(0, 18) }}{{ String(topic).length > 18 ? '...' : '' }}
        </span>
        <span v-if="topics.length === 0 && !loadingTopics" class="text-gray-600 text-xs">暂无可用话题</span>
      </div>
    </div>

    <!-- Article 预览 Area -->
    <div v-if="currentArticle" ref="previewRef"
      class="glass-card rounded-xl border border-cyber-border p-5 scroll-mt-4">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-2 flex-wrap">
          <span v-if="currentArticle.topic === 'Today Hot Summary'"
            class="badge bg-cyber-cyan/20 text-cyber-cyan border border-cyber-cyan/40 text-xs px-2 py-0.5 rounded">热点综述</span>
          <span v-if="currentArticle.isLocalProject"
            class="badge bg-purple-500/20 text-purple-400 border border-purple-500/40 text-xs px-2 py-0.5 rounded">本地项目</span>
          <h2 class="text-lg font-bold text-gray-100 truncate">{{ currentArticle.title }}</h2>
          <span class="text-gray-500 text-sm">{{ currentArticle.topic }}</span>
          <span :class="currentArticle.status === 1 ? 'badge-success' : 'badge-warning'" class="badge">
            {{ currentArticle.status === 1 ? '已发布' : '草稿' }}
          </span>
          <span class="text-gray-600 text-xs">{{ formatTime(currentArticle.createTime) }}</span>
          <!-- AI推荐的分类和标签 -->
          <div v-if="currentArticle.categoryName || currentArticle.tagNames?.length" class="flex items-center gap-2 mt-2 w-full">
            <span v-if="currentArticle.categoryName" class="text-xs text-green-400">
              📁 分类: {{ currentArticle.categoryName }}
            </span>
            <span v-if="currentArticle.tagNames?.length" class="text-xs text-yellow-400">
              🏷️ 标签: {{ currentArticle.tagNames.join(', ') }}
            </span>
            <span v-if="currentArticle.recommendReason" class="text-xs text-gray-500 italic">
              ({{ currentArticle.recommendReason }})
            </span>
          </div>
        </div>
        <div class="flex items-center gap-2 shrink-0">
          <router-link v-if="currentArticle.articleId" :to="'/article-edit/' + currentArticle.articleId"
            class="text-xs text-cyber-muted hover:text-cyber-cyan transition-colors">
            编辑
          </router-link>
          <button v-if="currentArticle.status === 0" @click="publishArticle"
            class="btn-cyber bg-cyber-cyan/20 border border-cyber-cyan text-cyber-cyan hover:bg-cyber-cyan/30 px-8 py-3 rounded-lg font-bold text-sm shadow-lg shadow-cyber-cyan/20">
            立即发布
          </button>
          <button v-else @click="unpublishArticle"
            class="btn-cyber bg-yellow-500/20 border border-yellow-500/50 text-yellow-400 hover:bg-yellow-500/30 px-4 py-2 rounded-lg text-sm">
            取消发布
          </button>
        </div>
      </div>
      <!-- Article content -->
      <div class="article-content text-sm leading-relaxed text-gray-300 border-t border-cyber-border pt-4 mt-4"
        v-html="renderMarkdown(currentArticle.content)"></div>
    </div>

    <!-- Recent Articles List -->
    <div v-if="recentArticles.length > 0" class="glass-card rounded-xl border border-cyber-border p-5">
      <h3 class="text-sm font-semibold text-gray-100 mb-3">最近生成</h3>
      <div class="space-y-2">
        <div v-for="article in recentArticles" :key="article.articleId" @click="currentArticle = article"
          :class="['p-3 rounded-lg border cursor-pointer transition-all', currentArticle && currentArticle.articleId === article.articleId ? 'border-cyber-cyan bg-cyber-cyan/5' : 'border-cyber-border bg-dark-3/50 hover:border-cyber-cyan/50']">
          <div class="flex items-center justify-between">
            <div class="flex-1 min-w-0">
              <h4 class="text-sm font-medium text-white truncate">{{ article.title }}</h4>
              <p class="text-cyber-muted text-xs mt-0.5 truncate">{{ article.summary }}</p>
              <div class="flex items-center gap-2 mt-1">
                <span :class="article.status === 1 ? 'text-green-400' : 'text-yellow-400'" class="text-xs">
                  {{ article.status === 1 ? '已发布' : '草稿' }}
                </span>
                <span class="text-gray-600 text-xs">{{ formatTime(article.createTime) }}</span>
              </div>
            </div>
            <div class="flex items-center gap-2 shrink-0 ml-2">
              <button v-if="article.status === 0" @click.stop="publishBatchOne(article)"
                class="text-xs text-cyber-cyan hover:text-white transition-colors">发布</button>
              <button v-else @click.stop="unpublishBatchOne(article)"
                class="text-xs text-yellow-400 hover:text-white transition-colors">取消发布</button>
              <button @click.stop="previewBatchArticle(article)"
                class="text-xs text-gray-400 hover:text-white transition-colors">预览</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 📋 操作日志 -->
    <div class="glass-card rounded-xl border border-cyber-border p-4">
      <div class="flex items-center justify-between mb-2">
        <h3 class="text-sm font-semibold text-gray-100">操作日志</h3>
        <button @click="logs = []" class="text-xs text-gray-600 hover:text-gray-400">清除</button>
      </div>
      <div class="space-y-1 max-h-[200px] overflow-y-auto font-mono text-xs">
        <div v-for="(log, idx) in logs" :key="idx"
          :class="['flex items-start gap-2', log.type === 'error' ? 'text-red-400' : log.type === 'success' ? 'text-green-400' : 'text-gray-500']">
          <span class="shrink-0">[{{ log.time }}]</span>
          <span class="flex-1">{{ log.text }}</span>
        </div>
        <div v-if="logs.length === 0" class="text-gray-700">暂无日志</div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { marked } from 'marked'
import api, { adminArticlesAPI } from '@/api'

// 平台列表（写死，不依赖后端）
const PLATFORMS = [
  { id: 'weibo', label: '微博', icon: '📮' },
  { id: 'zhihu', label: '知乎', icon: '💬' },
  { id: 'baidu', label: '百度', icon: '🔍' },
  { id: 'bilibili', label: 'B站', icon: '📺' },
  { id: 'douyin', label: '抖音', icon: '🎵' },
  { id: 'wallstreetcn', label: '财经', icon: '📈' },
  { id: 'toutiao', label: '头条', icon: '📰' },
  { id: 'thepaper', label: '澎湃', icon: '🗞️' },
]

const loading = ref(false)
const loadingTopics = ref(false)
const topics = ref([])
const customTopic = ref('')
const selectedDirection = ref('')
const selectedDirectionLabel = ref('')
const directionTopics = ref([])
const currentArticle = ref(null)
const recentArticles = ref([])
const logs = ref([])
const previewRef = ref(null)
const previewHighlight = ref(false)
// 批量生成状态
const batchResults = ref([])
const loadingBatch = ref(false)
const batchProgress = ref(0)
const batchTotal = ref(0)
// 当前正在生成的状态（用于显示哪个在生成中）
const activeMode = ref('')
const activeTopic = ref('')

// 本地项目相关
const fileInputRef = ref(null)
const selectedFiles = ref([])
const localProjectPrompt = ref('')

// 设置相关
const showSettings = ref(true)
const savingSettings = ref(false)
const categories = ref([])
const settings = ref({
  aiPublish: {
    autoPublish: false,
    defaultCategoryId: ''
  },
  scheduler: {
    enabled: true,
    cron: '0 0 9 * * ?',
    autoPublish: false,
    categoryId: ''
  }
})

marked.setOptions({ breaks: true, gfm: true })

const renderedContent = computed(() => {
  if (!currentArticle.value?.content) return '<p class="text-cyber-muted">无内容</p>'
  return marked(currentArticle.value.content)
})

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function addLog(msg, type = 'info') {
  const now = new Date().toLocaleTimeString('zh-CN')
  logs.value.unshift({ time: now, msg, type })
  if (logs.value.length > 50) logs.value.pop()
}
// 生成完成后滚动到预览区域
async function scrollToPreview() {
  await nextTick()
  if (previewRef.value) {
    previewRef.value.scrollIntoView({ behavior: 'smooth', block: 'start' })
    previewHighlight.value = true
    setTimeout(() => { previewHighlight.value = false }, 2000)
  }
}

async function fetchAll() {
  loadingTopics.value = true
  await fetchTopics()
  loadingTopics.value = false
}

async function fetchTopics() {
  try {
    const res = await api.get('/admin/ai/topics')
    const data = res?.data
    topics.value = Array.isArray(data) ? data : []
    addLog(`获取到 ${topics.value.length} 条全网热点`, 'success')
  } catch (e) {
    addLog('获取热点话题失败: ' + (e.message || e), 'error')
    topics.value = []
  }
}

// 点击方向芯片
async function pickDirection(platform) {
  if (selectedDirection.value === platform.id) {
    selectedDirection.value = ''
    selectedDirectionLabel.value = ''
    directionTopics.value = []
    return
  }
  selectedDirection.value = platform.id
  selectedDirectionLabel.value = platform.label
  loadingTopics.value = true
  try {
    const res = await api.get('/admin/ai/direction-topics?direction=' + platform.id)
    const data = res?.data
    directionTopics.value = Array.isArray(data) ? data : []
    addLog(`加载 ${platform.label} 热点 ${directionTopics.value.length} 条`, 'success')
  } catch (e) {
    directionTopics.value = []
    addLog(`${platform.label} 加载失败`, 'error')
  }
  loadingTopics.value = false
}

// 点击话题 ? 直接生成
async function generateFromTopic(topic) {
  // 确保 topic 是字符串（防御后端数据源可能返回数组的情况）
  const topicStr = Array.isArray(topic) ? String(topic[0]) : String(topic || '')
  if (!topicStr || loading.value) return
  loading.value = true
  activeMode.value = 'direction'
  activeTopic.value = topicStr
  addLog(`正在生成: ${topicStr}`)
  try {
    const res = await api.post('/admin/ai/generate', { topic: topicStr })
    const article = res.data.data
    currentArticle.value = article
    recentArticles.value = [article, ...recentArticles.value.filter(a => a.articleId !== article.articleId)]
    addLog(`生成成功：${article.title}`, 'success')
    await scrollToPreview()
  } catch (e) {
    addLog('生成失败: ' + (e.message || e), 'error')
  } finally {
    loading.value = false
    activeMode.value = ''
    activeTopic.value = ''
  }
}
// 总结今日热点（两步：先摘要每个话题，再汇总成长文）
async function generateSummary() {
  if (loading.value) return
  loading.value = true
  activeMode.value = 'summary'
  addLog('正在生成热点综述文章...')
  try {
    const res = await api.post('/admin/ai/summary')
    const data = res.data.data || {}
    currentArticle.value = data
    recentArticles.value = [data, ...recentArticles.value.filter(a => a.articleId !== data.articleId)]
    addLog(`综述文章生成成功：${data.title}`, 'success')
    await scrollToPreview()
  } catch (e) {
    addLog('生成失败: ' + (e.message || e), 'error')
  } finally {
    loading.value = false
    activeMode.value = ''
  }
}
// 批量生成：为每个热点生成一篇独立文章
async function batchGenerate() {
  if (loadingBatch.value || topics.value.length === 0) return
  loadingBatch.value = true
  batchProgress.value = 0
  batchTotal.value = topics.value.length
  batchResults.value = []
  addLog(`开始批量生成 ${topics.value.length} 篇文章...`)

  // 后端并行生成，前端轮询进度（模拟进度，实际由后端返回完成结果）
  // 由于后端是同步并行执行，直接等结果
  try {
    const res = await api.post('/admin/ai/batch-generate', {
      topics: topics.value,
      maxCount: topics.value.length
    })
    batchResults.value = Array.isArray(res?.data?.data) ? res.data.data : []
    const successCount = batchResults.value.filter(a => a.status === 0 || a.status === 1).length
    const failCount = batchResults.value.filter(a => a.status === -1).length
    addLog(`批量生成完成 ${successCount} 篇成功，${failCount} 篇失败`, failCount > 0 ? 'error' : 'success')
    batchProgress.value = batchTotal.value
  } catch (e) {
    addLog('批量生成失败: ' + (e.message || e), 'error')
  } finally {
    loadingBatch.value = false
  }
}

// 预览批量生成的文章
async function previewBatchArticle(article) {
  if (article.status === -1) {
    addLog(`生成失败: ${article.topic}`, 'error')
    return
  }
  currentArticle.value = article
  await scrollToPreview()
}
// 发布批量生成的文章
async function publishBatchOne(article) {
  if (article.status === -1 || !article.articleId || article.articleId < 0) return
  try {
    await adminArticlesAPI.publish(article.articleId)
    article.status = 1
    addLog(`已发布：${article.title}`, 'success')
  } catch (e) {
    addLog('发布失败: ' + (e.message || e), 'error')
  }
}
// 撤回批量生成的文章
async function unpublishBatchOne(article) {
  if (!article.articleId || article.articleId < 0) return
  try {
    await adminArticlesAPI.unpublish(article.articleId)
    article.status = 0
    addLog(`已撤回：${article.title}`, 'success')
  } catch (e) {
    addLog('撤回失败: ' + (e.message || e), 'error')
  }
}
// 自定义话题生成
async function generateCustom() {
  const topic = customTopic.value.trim()
  if (!topic || loading.value) return
  loading.value = true
  activeMode.value = 'custom'
  addLog(`正在生成: ${topic}`)
  try {
    const res = await api.post('/admin/ai/generate', { topic })
    const article = res.data.data
    currentArticle.value = article
    recentArticles.value = [article, ...recentArticles.value.filter(a => a.articleId !== article.articleId)]
    addLog(`生成成功：${article.title}`, 'success')
    customTopic.value = ''
    await scrollToPreview()
  } catch (e) {
    addLog('生成失败: ' + (e.message || e), 'error')
  } finally {
    loading.value = false
    activeMode.value = ''
  }
}

// ========== 本地项目分析相关 ==========

function triggerFileSelect() {
  fileInputRef.value?.click()
}

function handleDirectorySelect(event) {
  const files = Array.from(event.target.files || [])
  selectedFiles.value = files.map(file => ({
    name: file.name,
    path: file.webkitRelativePath || file.name,
    relativePath: file.webkitRelativePath,
    size: file.size,
    type: file.type,
    content: null
  }))
  addLog(`已选择 ${files.length} 个文件`, 'success')
}

function clearFiles() {
  selectedFiles.value = []
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
  localProjectPrompt.value = ''
}

function getFileIcon(filename) {
  const ext = filename.split('.').pop()?.toLowerCase()
  const iconMap = {
    'js': '📜', 'jsx': '⚛️', 'ts': '📘', 'tsx': '⚛️',
    'vue': '💚', 'svelte': '🔥',
    'py': '🐍', 'java': '☕', 'go': '🔵', 'rs': '🦀',
    'html': '🌐', 'css': '🎨', 'scss': '🎨', 'less': '🎨',
    'json': '📋', 'yaml': '⚙️', 'yml': '⚙️', 'xml': '📰',
    'md': '📝', 'txt': '📄', 'sql': '🗃️',
    'png': '🖼️', 'jpg': '🖼️', 'jpeg': '🖼️', 'gif': '🖼️', 'svg': '🖼️',
    'zip': '📦', 'tar': '📦', 'gz': '📦',
    'pdf': '📕', 'doc': '📘', 'docx': '📘',
    'gitignore': '🔧', 'env': '🔐', 'log': '📜'
  }
  return iconMap[ext] || '📄'
}

async function readFileContent(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result)
    reader.onerror = reject
    reader.readAsText(file)
  })
}

async function generateFromLocalProject() {
  if (selectedFiles.value.length === 0 || !localProjectPrompt.value.trim() || loading.value) return

  loading.value = true
  activeMode.value = 'local'
  addLog(`正在读取 ${selectedFiles.value.length} 个文件...`)

  try {
    // 读取所有文件内容
    const fileDataList = []
    const fileReadPromises = selectedFiles.value.slice(0, 50).map(async (fileInfo) => {
      try {
        // 找到实际的 File 对象
        const file = fileInputRef.value?.files?.length > 0
          ? Array.from(fileInputRef.value.files).find(f =>
              f.name === fileInfo.name && (f.webkitRelativePath === fileInfo.relativePath || f.name === fileInfo.name)
            )
          : null

        if (file) {
          const content = await readFileContent(file)
          return {
            name: fileInfo.name,
            path: fileInfo.path,
            content: content
          }
        }
        return null
      } catch (e) {
        console.warn(`Failed to read file: ${fileInfo.name}`, e)
        return null
      }
    })

    const results = await Promise.allSettled(fileReadPromises)
    results.forEach(result => {
      if (result.status === 'fulfilled' && result.value) {
        fileDataList.push(result.value)
      }
    })

    addLog(`已读取 ${fileDataList.length} 个文件，准备发送给AI分析...`)

    if (fileDataList.length === 0) {
      addLog('未能读取任何文件内容', 'error')
      loading.value = false
      activeMode.value = ''
      return
    }

    // 发送文件内容给后端
    addLog(`正在分析项目内容...`)
    const res = await api.post('/admin/ai/analyze-local', {
      files: fileDataList,
      prompt: localProjectPrompt.value.trim()
    })

    const article = res.data.data
    currentArticle.value = {
      ...article,
      isLocalProject: true,
      topic: '本地项目分析'
    }
    recentArticles.value = [currentArticle.value, ...recentArticles.value.filter(a => a.articleId !== article.articleId)]
    addLog(`项目分析完成：${article.title}`, 'success')
    await scrollToPreview()

  } catch (e) {
    addLog('项目分析失败: ' + (e.message || e), 'error')
  } finally {
    loading.value = false
    activeMode.value = ''
  }
}

// 预览历史文章
async function previewArticle(article) {
  try {
    const res = await adminArticlesAPI.get(article.articleId)
    currentArticle.value = {
      ...article,
      content: res.data?.content || article.content || article.summary,
      createTime: res.data?.createTime || article.createTime
    }
    await scrollToPreview()
  } catch (e) {
    currentArticle.value = article
    await scrollToPreview()
  }
}

// 发布
async function publishArticle() {
  if (!currentArticle.value) return
  try {
    await adminArticlesAPI.publish(currentArticle.value.articleId)
    currentArticle.value.status = 1
    const item = recentArticles.value.find(a => a.articleId === currentArticle.value.articleId)
    if (item) item.status = 1
    addLog(`已发布：${currentArticle.value.title}`, 'success')
  } catch (e) {
    addLog('发布失败: ' + (e.message || e), 'error')
  }
}

// 撤回
async function unpublishArticle() {
  if (!currentArticle.value) return
  try {
    await adminArticlesAPI.unpublish(currentArticle.value.articleId)
    currentArticle.value.status = 0
    const item = recentArticles.value.find(a => a.articleId === currentArticle.value.articleId)
    if (item) item.status = 0
    addLog(`已撤回：${currentArticle.value.title}`, 'success')
  } catch (e) {
    addLog('撤回失败: ' + (e.message || e), 'error')
  }
}

async function publishOne(id) {
  try {
    await adminArticlesAPI.publish(id)
    const item = recentArticles.value.find(a => a.articleId === id)
    if (item) item.status = 1
    if (currentArticle.value?.articleId === id) currentArticle.value.status = 1
    addLog('已发布', 'success')
  } catch (e) {
    addLog('发布失败: ' + (e.message || e), 'error')
  }
}

async function unpublishOne(id) {
  try {
    await adminArticlesAPI.unpublish(id)
    const item = recentArticles.value.find(a => a.articleId === id)
    if (item) item.status = 0
    if (currentArticle.value?.articleId === id) currentArticle.value.status = 0
    addLog('已撤回', 'success')
  } catch (e) {
    addLog('撤回失败: ' + (e.message || e), 'error')
  }
}

onMounted(async () => {
  await fetchAll()
  await fetchSettings()
  await fetchCategories()
  try {
    const res = await adminArticlesAPI.list({ page: 1, pageSize: 20 })
    recentArticles.value = (res.data?.records || []).map(a => ({
      articleId: a.id,
      title: a.title,
      summary: a.summary,
      content: a.content,
      topic: a.summary?.substring(0, 30) || '',
      status: a.status,
      createTime: a.createTime
    }))
  } catch (e) { /* ignore */ }
})

// 获取设置
async function fetchSettings() {
  try {
    const res = await api.get('/admin/ai-settings')
    const data = res.data
    if (data.aiPublish) {
      settings.value.aiPublish.autoPublish = data.aiPublish.autoPublish || false
      settings.value.aiPublish.defaultCategoryId = data.aiPublish.defaultCategoryId || ''
    }
    if (data.scheduler) {
      settings.value.scheduler.enabled = data.scheduler.enabled !== false
      settings.value.scheduler.cron = data.scheduler.cron || '0 0 9 * * ?'
      settings.value.scheduler.autoPublish = data.scheduler.autoPublish || false
      settings.value.scheduler.categoryId = data.scheduler.categoryId || ''
    }
  } catch (e) {
    console.error('Failed to fetch settings:', e)
  }
}

// 获取分类列表
async function fetchCategories() {
  try {
    const res = await api.get('/admin/categories')
    categories.value = res.data || []
  } catch (e) {
    console.error('Failed to fetch categories:', e)
  }
}

// 保存 AI 发布设置
async function saveAiPublishSettings() {
  savingSettings.value = true
  try {
    await api.put('/admin/ai-settings/ai-publish', {
      autoPublish: settings.value.aiPublish.autoPublish,
      defaultCategoryId: settings.value.aiPublish.defaultCategoryId || null
    })
    addLog('AI发布设置已保存', 'success')
  } catch (e) {
    addLog('保存失败: ' + (e.message || e), 'error')
  } finally {
    savingSettings.value = false
  }
}

// 保存定时任务设置
async function saveSchedulerSettings() {
  savingSettings.value = true
  try {
    await api.put('/admin/ai-settings/scheduler', {
      enabled: settings.value.scheduler.enabled,
      cron: settings.value.scheduler.cron,
      autoPublish: settings.value.scheduler.autoPublish,
      categoryId: settings.value.scheduler.categoryId || null
    })
    addLog('定时任务设置已保存', 'success')
  } catch (e) {
    addLog('保存失败: ' + (e.message || e), 'error')
  } finally {
    savingSettings.value = false
  }
}
</script>
