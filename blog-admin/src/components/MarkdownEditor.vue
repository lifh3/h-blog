<template>
  <div class="flex flex-col h-full">
    <!-- Toolbar -->
    <div class="flex flex-wrap gap-1 mb-3 p-2 glass-card">
      <button
        v-for="tool in tools"
        :key="tool.label"
        @click="tool.action"
        :title="tool.label"
        class="toolbar-btn"
        :class="{ 'text-cyber-cyan': tool.active }"
      >
        {{ tool.icon }}
      </button>
    </div>

    <!-- Editor & Preview -->
    <div class="flex-1 flex gap-4 min-h-0">
      <!-- Editor -->
      <div class="flex-1 flex flex-col">
        <div class="text-sm text-cyber-muted mb-2">编辑 (Markdown)</div>
        <textarea
          ref="textareaRef"
          :value="modelValue"
          @input="$emit('update:modelValue', $event.target.value)"
          class="flex-1 input-cyber resize-none font-mono text-sm leading-relaxed"
          placeholder="使用 Markdown 编写内容..."
        ></textarea>
      </div>

      <!-- Preview -->
      <div class="flex-1 flex flex-col">
        <div class="text-sm text-cyber-muted mb-2">预览</div>
        <div class="flex-1 glass-card p-4 overflow-auto prose prose-invert max-w-none" v-html="renderedContent"></div>
      </div>
    </div>

    <!-- Footer -->
    <div class="flex justify-between items-center mt-3 text-sm text-cyber-muted">
      <span>字数：{{ wordCount }}</span>
      <span class="text-xs">Markdown 格式</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  modelValue: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue'])
const textareaRef = ref(null)

marked.setOptions({
  breaks: true,
  gfm: true,
})

const wordCount = computed(() => {
  return props.modelValue.length
})

const renderedContent = computed(() => {
  if (!props.modelValue) return '<p class="text-cyber-muted">预览区域</p>'
  return marked(props.modelValue)
})

// 获取选中的文本
function getSelection() {
  const textarea = textareaRef.value
  return {
    start: textarea.selectionStart,
    end: textarea.selectionEnd,
    text: textarea.value.substring(textarea.selectionStart, textarea.selectionEnd)
  }
}

// 在指定位置插入文本
function insertText(before, after = '', placeholder = '') {
  const textarea = textareaRef.value
  const { start, end, text } = getSelection()
  const insertText = text || placeholder

  const newValue =
    textarea.value.substring(0, start) +
    before + insertText + after +
    textarea.value.substring(end)

  emit('update:modelValue', newValue)

  // 设置光标位置
  setTimeout(() => {
    textarea.focus()
    const newCursorPos = text ? start + before.length + insertText.length + after.length : start + before.length
    textarea.setSelectionRange(newCursorPos, newCursorPos)
  }, 0)
}

// 工具栏按钮配置
const tools = [
  {
    label: '标题1',
    icon: 'H1',
    action: () => insertText('# ', '', '标题')
  },
  {
    label: '标题2',
    icon: 'H2',
    action: () => insertText('## ', '', '标题')
  },
  {
    label: '标题3',
    icon: 'H3',
    action: () => insertText('### ', '', '标题')
  },
  {
    label: '加粗',
    icon: 'B',
    action: () => insertText('**', '**', '粗体文字')
  },
  {
    label: '斜体',
    icon: 'I',
    action: () => insertText('*', '*', '斜体文字')
  },
  {
    label: '删除线',
    icon: 'S',
    action: () => insertText('~~', '~~', '删除线文字')
  },
  {
    label: '引用',
    icon: '"',
    action: () => insertText('> ', '', '引用内容')
  },
  {
    label: '行内代码',
    icon: '</>',
    action: () => insertText('`', '`', '代码')
  },
  {
    label: '代码块',
    icon: '{ }',
    action: () => insertText('\n```\n', '\n```\n', '代码块内容')
  },
  {
    label: '无序列表',
    icon: '•',
    action: () => insertText('- ', '', '列表项')
  },
  {
    label: '有序列表',
    icon: '1.',
    action: () => insertText('1. ', '', '列表项')
  },
  {
    label: '链接',
    icon: '🔗',
    action: () => insertText('[', '](url)', '链接文字')
  },
  {
    label: '图片',
    icon: '🖼',
    action: () => insertText('![', '](image-url)', '图片描述')
  },
  {
    label: '水平线',
    icon: '—',
    action: () => insertText('\n---\n', '', '')
  }
]
</script>

<style scoped>
.toolbar-btn {
  @apply px-3 py-1.5 text-sm font-mono rounded transition-colors;
  @apply bg-[#1a2332]/80 text-cyber-muted;
  @apply hover:bg-[#1a2332] hover:text-cyber-text;
  @apply focus:outline-none focus:ring-1 focus:ring-cyber-cyan;
  min-width: 36px;
}

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
