<template>
  <div class="md-editor" :class="{ 'fullscreen': isFullscreen }">
    <!-- Toolbar -->
    <div class="editor-toolbar">
      <div class="toolbar-left">
        <button @click="insertFormat('bold')" title="加粗 (Ctrl+B)" class="toolbar-btn">
          <span class="font-bold">B</span>
        </button>
        <button @click="insertFormat('italic')" title="斜体 (Ctrl+I)" class="toolbar-btn italic-btn">
          <span class="italic">I</span>
        </button>
        <button @click="insertFormat('strikethrough')" title="删除线" class="toolbar-btn">
          <span class="line-through">S</span>
        </button>

        <div class="toolbar-divider"></div>

        <button @click="insertFormat('h1')" title="标题1" class="toolbar-btn text-xs">H1</button>
        <button @click="insertFormat('h2')" title="标题2" class="toolbar-btn text-xs">H2</button>
        <button @click="insertFormat('h3')" title="标题3" class="toolbar-btn text-xs">H3</button>

        <div class="toolbar-divider"></div>

        <button @click="insertFormat('quote')" title="引用" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
          </svg>
        </button>
        <button @click="insertFormat('code')" title="行内代码" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4"/>
          </svg>
        </button>
        <button @click="insertFormat('codeblock')" title="代码块" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 7v10c0 2 1 3 3 3h10c2 0 3-1 3-3V7c0-2-1-3-3-3H7c-2 0-3 1-3 3z"/>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 11l2 2-2 2m4-4l2 2-2 2"/>
          </svg>
        </button>

        <div class="toolbar-divider"></div>

        <button @click="insertFormat('ul')" title="无序列表" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
          </svg>
        </button>
        <button @click="insertFormat('ol')" title="有序列表" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 20l4-16m2 16l4-16M3 8h18M3 16h18"/>
          </svg>
        </button>
        <button @click="insertFormat('todo')" title="任务列表" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
          </svg>
        </button>

        <div class="toolbar-divider"></div>

        <button @click="insertFormat('link')" title="链接" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1"/>
          </svg>
        </button>
        <button @click="insertFormat('image')" title="图片" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
        </button>
        <button @click="insertFormat('table')" title="表格" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M3 14h18M10 3v18M14 3v18M3 6v12a2 2 0 002 2h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2z"/>
          </svg>
        </button>
        <button @click="insertFormat('hr')" title="水平线" class="toolbar-btn">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14"/>
          </svg>
        </button>
      </div>

      <div class="toolbar-right">
        <button @click="togglePreview" :class="['toolbar-btn', { active: showPreview }]" title="预览">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
          </svg>
        </button>
        <button @click="toggleFullscreen" :class="['toolbar-btn', { active: isFullscreen }]" title="全屏编辑">
          <svg v-if="!isFullscreen" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4"/>
          </svg>
          <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Editor Area -->
    <div class="editor-container" :class="{ 'with-preview': showPreview }">
      <div class="editor-pane">
        <div class="pane-header">
          <span class="text-text-muted text-xs">Markdown</span>
        </div>
        <textarea
          ref="textareaRef"
          :value="modelValue"
          @input="handleInput"
          @keydown="handleKeydown"
          class="editor-textarea"
          placeholder="在这里编写 Markdown 内容...

支持的功能：
• # 标题
• **加粗** *斜体* ~~删除线~~
• > 引用
• `代码` 或 ``` 代码块
• 列表和任务列表
• [链接](url) 和 ![图片](url)
• 表格

快捷键：
Ctrl+B 加粗 | Ctrl+I 斜体 | Ctrl+K 链接"
        ></textarea>
      </div>

      <div v-if="showPreview" class="preview-pane">
        <div class="pane-header">
          <span class="text-text-muted text-xs">预览</span>
        </div>
        <div class="preview-content prose-custom" v-html="renderedContent"></div>
      </div>
    </div>

    <!-- Word Count -->
    <div class="editor-footer">
      <span class="text-text-muted text-xs">{{ wordCount }} 字 | {{ lineCount }} 行</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const props = defineProps({
  modelValue: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue'])

const textareaRef = ref(null)
const isFullscreen = ref(false)
const showPreview = ref(true)

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><div class="copy-btn" onclick="navigator.clipboard.writeText(this.parentElement.innerText)">📋</div><code>' + hljs.highlight(str, { language: lang, ignoreIllegals: true }).value + '</code></pre>'
      } catch (__) {}
    }
    return '<pre class="hljs"><div class="copy-btn" onclick="navigator.clipboard.writeText(this.parentElement.innerText)">📋</div><code>' + md.utils.escapeHtml(str) + '</code></pre>'
  }
})

const renderedContent = computed(() => {
  return md.render(props.modelValue || '')
})

const wordCount = computed(() => {
  const text = props.modelValue || ''
  return text.replace(/\s/g, '').length
})

const lineCount = computed(() => {
  const text = props.modelValue || ''
  return text ? text.split('\n').length : 0
})

function handleInput(e) {
  emit('update:modelValue', e.target.value)
}

function handleKeydown(e) {
  // Handle Tab key
  if (e.key === 'Tab') {
    e.preventDefault()
    const start = e.target.selectionStart
    const end = e.target.selectionEnd
    const value = e.target.value
    const newValue = value.substring(0, start) + '  ' + value.substring(end)
    emit('update:modelValue', newValue)
    // Set cursor position after tab
    setTimeout(() => {
      e.target.selectionStart = e.target.selectionEnd = start + 2
    }, 0)
    return
  }

  // Ctrl+B for bold
  if (e.ctrlKey && e.key === 'b') {
    e.preventDefault()
    insertFormat('bold')
    return
  }

  // Ctrl+I for italic
  if (e.ctrlKey && e.key === 'i') {
    e.preventDefault()
    insertFormat('italic')
    return
  }

  // Ctrl+K for link
  if (e.ctrlKey && e.key === 'k') {
    e.preventDefault()
    insertFormat('link')
    return
  }
}

function togglePreview() {
  showPreview.value = !showPreview.value
}

function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
}

function getTextarea() {
  return textareaRef.value
}

function insertFormat(type) {
  const textarea = getTextarea()
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = textarea.value
  const selectedText = text.substring(start, end)

  let before = ''
  let after = ''
  let newText = ''

  switch (type) {
    case 'bold':
      before = '**'
      after = '**'
      newText = `${before}${selectedText || '粗体文本'}${after}`
      break
    case 'italic':
      before = '*'
      after = '*'
      newText = `${before}${selectedText || '斜体文本'}${after}`
      break
    case 'strikethrough':
      before = '~~'
      after = '~~'
      newText = `${before}${selectedText || '删除线文本'}${after}`
      break
    case 'h1':
      newText = `# ${selectedText || '一级标题'}`
      break
    case 'h2':
      newText = `## ${selectedText || '二级标题'}`
      break
    case 'h3':
      newText = `### ${selectedText || '三级标题'}`
      break
    case 'quote':
      newText = `> ${selectedText || '引用文本'}`
      break
    case 'code':
      before = '`'
      after = '`'
      newText = `${before}${selectedText || '代码'}${after}`
      break
    case 'codeblock':
      newText = `\`\`\`\n${selectedText || '代码块'}\n\`\`\``
      break
    case 'ul':
      newText = `- ${selectedText || '列表项'}`
      break
    case 'ol':
      newText = `1. ${selectedText || '列表项'}`
      break
    case 'todo':
      newText = `- [ ] ${selectedText || '任务'}`
      break
    case 'link':
      if (selectedText) {
        newText = `[${selectedText}](url)`
      } else {
        newText = `[链接文本](url)`
      }
      break
    case 'image':
      if (selectedText) {
        newText = `![${selectedText}](图片URL)`
      } else {
        newText = `![图片描述](图片URL)`
      }
      break
    case 'table':
      newText = `| 列1 | 列2 | 列3 |
| --- | --- | --- |
| 内容 | 内容 | 内容 |`
      break
    case 'hr':
      newText = `\n---\n`
      break
    default:
      return
  }

  const newValue = text.substring(0, start) + newText + text.substring(end)
  emit('update:modelValue', newValue)

  // Set cursor position
  setTimeout(() => {
    if (selectedText) {
      // If text was selected, select the whole inserted text
      textarea.selectionStart = start
      textarea.selectionEnd = start + newText.length
    } else {
      // Position cursor between markers or at end
      const cursorPos = start + newText.length
      textarea.selectionStart = textarea.selectionEnd = cursorPos
    }
    textarea.focus()
  }, 0)
}
</script>

<style scoped>
.md-editor {
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(74, 158, 175, 0.3);
  border-radius: 12px;
  overflow: hidden;
  background: rgba(15, 15, 35, 0.8);
  transition: all 0.3s ease;
}

.md-editor.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  border-radius: 0;
  border: none;
}

/* Toolbar */
.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(30, 30, 50, 0.9);
  border-bottom: 1px solid rgba(74, 158, 175, 0.2);
  flex-wrap: wrap;
  gap: 8px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
}

.toolbar-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  color: rgba(255, 255, 255, 0.7);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.toolbar-btn:hover {
  background: rgba(74, 158, 175, 0.2);
  color: #4a9eaf;
}

.toolbar-btn.active {
  background: rgba(74, 158, 175, 0.3);
  color: #4a9eaf;
}

.toolbar-divider {
  width: 1px;
  height: 20px;
  background: rgba(74, 158, 175, 0.2);
  margin: 0 4px;
}

/* Editor Container */
.editor-container {
  display: flex;
  flex: 1;
  min-height: 500px;
}

.editor-container.with-preview .editor-pane {
  width: 50%;
}

.editor-container.with-preview .preview-pane {
  width: 50%;
}

.editor-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(74, 158, 175, 0.2);
}

.pane-header {
  padding: 8px 16px;
  background: rgba(30, 30, 50, 0.5);
  border-bottom: 1px solid rgba(74, 158, 175, 0.1);
}

.editor-textarea {
  flex: 1;
  width: 100%;
  padding: 20px;
  background: transparent;
  color: #e0e0e0;
  font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.7;
  outline: none;
  resize: none;
  border: none;
}

.editor-textarea::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

/* Preview Pane */
.preview-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: rgba(20, 20, 40, 0.5);
}

.preview-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
}

/* Footer */
.editor-footer {
  padding: 8px 16px;
  background: rgba(30, 30, 50, 0.5);
  border-top: 1px solid rgba(74, 158, 175, 0.1);
  display: flex;
  justify-content: flex-end;
}

/* Prose Styles */
.prose-custom {
  @apply text-gray-300 leading-relaxed;
}

.prose-custom :deep(h1),
.prose-custom :deep(h2),
.prose-custom :deep(h3),
.prose-custom :deep(h4) {
  @apply text-white font-semibold mt-6 mb-4;
}

.prose-custom :deep(h1) { @apply text-2xl border-b border-cyan-500/30 pb-2; }
.prose-custom :deep(h2) { @apply text-xl; }
.prose-custom :deep(h3) { @apply text-lg; }

.prose-custom :deep(p) {
  @apply mb-4 leading-relaxed;
}

.prose-custom :deep(a) {
  @apply text-cyan-400 hover:underline;
}

.prose-custom :deep(code) {
  @apply px-1.5 py-0.5 rounded bg-white/10 text-emerald-400 font-mono text-sm;
}

.prose-custom :deep(pre) {
  @apply relative mb-4 rounded-lg overflow-hidden bg-black/30;
}

.prose-custom :deep(pre .copy-btn) {
  @apply absolute top-2 right-2 px-2 py-1 text-sm cursor-pointer opacity-50 hover:opacity-100 rounded bg-white/10;
}

.prose-custom :deep(pre code) {
  @apply p-4 bg-transparent block text-sm;
}

.prose-custom :deep(blockquote) {
  @apply border-l-4 border-purple-500 pl-4 my-4 italic text-gray-400;
}

.prose-custom :deep(ul),
.prose-custom :deep(ol) {
  @apply mb-4 pl-6;
}

.prose-custom :deep(li) {
  @apply mb-2;
}

.prose-custom :deep(img) {
  @apply rounded-lg max-w-full h-auto my-4;
}

.prose-custom :deep(table) {
  @apply w-full border-collapse my-4;
}

.prose-custom :deep(th),
.prose-custom :deep(td) {
  @apply border border-cyan-500/30 px-4 py-2 text-left;
}

.prose-custom :deep(th) {
  @apply bg-cyan-500/10 text-white;
}

.prose-custom :deep(hr) {
  @apply border-cyan-500/30 my-6;
}

.prose-custom :deep(input[type="checkbox"]) {
  @apply mr-2 accent-cyan-500;
}

/* Fullscreen adjustments */
.md-editor.fullscreen .editor-container {
  min-height: calc(100vh - 120px);
}

.md-editor.fullscreen .editor-textarea {
  font-size: 16px;
  padding: 32px;
}

.md-editor.fullscreen .preview-content {
  padding: 32px;
}
</style>
