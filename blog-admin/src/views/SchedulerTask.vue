<template>
  <div class="p-6 space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-white">⏰ 定时任务</h1>
        <p class="text-cyber-muted mt-1">配置自动发文工作流的调度计划</p>
      </div>
    </div>

    <!-- 调度开关 -->
    <div class="glass-card p-6 rounded-xl border border-cyber-border">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-lg font-semibold text-white">🔔 定时任务开关</h2>
          <p class="text-cyber-muted text-sm mt-1">关闭后定时任务将不会自动执行，但可以手动触发</p>
        </div>
        <button @click="toggleScheduler"
          :class="['relative w-14 h-7 rounded-full transition-all duration-300', scheduler.enabled ? 'bg-green-5' : 'bg-dark-3']">
          <span
            :class="['absolute top-1 w-5 h-5 bg-white rounded-full transition-all duration-300',
                     scheduler.enabled ? 'left-8' : 'left-1']">
          </span>
        </button>
      </div>
    </div>

    <!-- 执行配置 -->
    <div class="glass-card p-6 rounded-xl border border-cyber-border">
      <h2 class="text-lg font-semibold text-white mb-4">⚙️ 执行配置</h2>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Cron 表达式 -->
        <div>
          <label class="block text-sm text-cyber-muted mb-2">Cron 执行时间</label>
          <input v-model="scheduler.cron" @blur="updateCron"
            class="w-full bg-dark-3 border border-cyber-border rounded-lg px-4 py-2.5 text-white font-mono text-sm focus:border-cyber-cyan outline-none"
            placeholder="0 0 9 * * ?" />
          <p class="text-cyber-muted text-xs mt-2">格式：秒 分 时 日 月 周（例：0 0 9 * * ? = 每天9点）</p>
        </div>

        <!-- 自动发布 -->
        <div>
          <label class="block text-sm text-cyber-muted mb-2">文章发布策略</label>
          <div class="flex items-center space-x-4 mt-1">
            <button @click="scheduler.autoPublish = false; updateConfig()"
              :class="['flex-1 py-2.5 rounded-lg text-sm font-medium border transition-all',
                       !scheduler.autoPublish ? 'bg-cyber-cyan/15 border-cyber-cyan text-cyber-cyan' : 'border-cyber-border text-cyber-muted hover:border-cyber-cyan']">
              📝 仅生成草稿
            </button>
            <button @click="scheduler.autoPublish = true; updateConfig()"
              :class="['flex-1 py-2.5 rounded-lg text-sm font-medium border transition-all',
                       scheduler.autoPublish ? 'bg-cyber-cyan/15 border-cyber-cyan text-cyber-cyan' : 'border-cyber-border text-cyber-muted hover:border-cyber-cyan']">
              ✅ 直接发布
            </button>
          </div>
        </div>
      </div>

      <!-- 常用定时预设 -->
      <div class="mt-5">
        <label class="block text-sm text-cyber-muted mb-2">常用预设</label>
        <div class="flex flex-wrap gap-2">
          <button v-for="preset in cronPresets" :key="preset.label"
            @click="scheduler.cron = preset.cron; updateCron()"
            class="px-3 py-1.5 rounded-lg text-sm border border-cyber-border text-cyber-muted hover:border-cyber-cyan hover:text-cyber-cyan transition-all">
            {{ preset.label }}
          </button>
        </div>
      </div>
    </div>

    <!-- 手动触发 -->
    <div class="glass-card p-6 rounded-xl border border-cyber-border">
      <h2 class="text-lg font-semibold text-white mb-4">🚀 手动执行</h2>
      <p class="text-cyber-muted text-sm mb-4">立即执行一次完整的自动发文工作流（获取热点 → AI 生成文章 → 保存）</p>
      <button @click="runNow" :disabled="running"
        class="btn-cyber px-6 py-2.5 rounded-lg font-medium disabled:opacity-40">
        {{ running ? '⚡ 执行中...' : '🚀 立即执行工作流' }}
      </button>

      <!-- 执行结果 -->
      <div v-if="runResult" class="mt-4 bg-dark-3 rounded-lg p-4">
        <div class="text-sm" :class="runResult.type === 'success' ? 'text-green-4' : 'text-red-4'">
          {{ runResult.msg }}
        </div>
      </div>
    </div>

    <!-- 执行日志 -->
    <div class="glass-card p-6 rounded-xl border border-cyber-border">
      <h2 class="text-lg font-semibold text-white mb-3">📋 最近执行日志</h2>
      <div class="bg-dark-2 rounded-lg p-4 font-mono text-xs text-cyber-muted max-h-48 overflow-y-auto space-y-1">
        <div v-for="(log, i) in logs" :key="i"
          :class="log.type === 'error' ? 'text-red-4' : log.type === 'success' ? 'text-green-4' : 'text-cyber-muted'">
          [{{ log.time }}] {{ log.msg }}
        </div>
        <div v-if="logs.length === 0" class="text-cyber-muted">暂无日志</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'

const scheduler = ref({ enabled: true, cron: '0 0 9 * * ?', autoPublish: false })
const running = ref(false)
const runResult = ref(null)
const logs = ref([])

const cronPresets = [
  { label: '每天 9:00', cron: '0 0 9 * * ?' },
  { label: '每天 12:00', cron: '0 0 12 * * ?' },
  { label: '每天 18:00', cron: '0 0 18 * * ?' },
  { label: '每天 21:00', cron: '0 0 21 * * ?' },
  { label: '每6小时', cron: '0 0 */6 * * ?' },
  { label: '每12小时', cron: '0 0 */12 * * ?' },
  { label: '每周一', cron: '0 0 9 ? * MON' },
  { label: '每月1号', cron: '0 0 9 1 * ?' },
]

function addLog(msg, type = 'info') {
  const now = new Date().toLocaleTimeString('zh-CN')
  logs.value.unshift({ time: now, msg, type })
  if (logs.value.length > 50) logs.value.pop()
}

async function fetchConfig() {
  try {
    const res = await api.get('/admin/ai/schedule-config')
    if (res.data) {
      scheduler.value = { ...res.data }
    }
  } catch (e) { /* ignore */ }
}

async function updateConfig() {
  try {
    await api.put('/admin/ai/schedule-config', scheduler.value)
    addLog('配置已保存', 'success')
  } catch (e) {
    addLog('配置保存失败: ' + (e.message || e), 'error')
  }
}

async function updateCron() {
  await updateConfig()
}

async function toggleScheduler() {
  scheduler.value.enabled = !scheduler.value.enabled
  await updateConfig()
}

async function runNow() {
  running.value = true
  runResult.value = null
  addLog('开始执行工作流...')
  try {
    const res = await api.post('/admin/ai/publish-now', {})
    const data = res.data || {}
    runResult.value = { type: 'success', msg: `完成：${data.message || data.title || '执行成功'}` }
    addLog(`工作流完成：${data.message || ''}`, 'success')
  } catch (e) {
    const msg = e.response?.data?.message || e.message || '执行失败'
    runResult.value = { type: 'error', msg }
    addLog('工作流执行失败: ' + msg, 'error')
  }
  running.value = false
}

onMounted(() => {
  fetchConfig()
})
</script>
