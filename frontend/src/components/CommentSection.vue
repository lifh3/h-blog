<template>
  <div class="comment-section mt-12">
    <div class="flex items-center gap-3 mb-8">
      <h3 class="text-2xl font-bold text-text-primary">评论</h3>
      <span class="px-3 py-1 rounded-full bg-cyber-accent/20 text-cyber-accent text-sm font-medium">{{ comments.length }}</span>
    </div>

    <!-- Comment Form -->
    <form @submit.prevent="submitComment" class="comment-form mb-10 p-6 rounded-2xl bg-cyber-surface/80 border border-cyber-border/50 backdrop-blur-sm">
      <div class="flex gap-4">
        <div class="w-10 h-10 rounded-full bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center text-white font-bold text-sm flex-shrink-0">
          {{ userAvatar }}
        </div>
        <div class="flex-1">
          <textarea
            v-model="form.content"
            placeholder="写下你的评论... (支持 Markdown 格式)"
            rows="4"
            class="w-full px-4 py-3 rounded-xl bg-cyber-dark/60 border border-cyber-border/50 text-text-primary placeholder-text-muted/50 focus:outline-none focus:border-cyber-accent/50 focus:ring-2 focus:ring-cyber-accent/20 transition-all resize-none"
          ></textarea>
          <div class="flex items-center justify-between mt-4">
            <div v-if="isLoggedIn" class="flex items-center gap-2 text-sm text-text-muted">
              <svg class="w-4 h-4 text-neon-green" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>以 <strong class="text-neon-blue">{{ blogAuthorName }}</strong> 身份评论</span>
            </div>
            <div v-else class="text-sm text-text-muted">
              博主回复会显示认证标识
            </div>
            <button
              type="submit"
              :disabled="submitting || !form.content"
              class="ml-4 px-6 py-2 rounded-xl bg-gradient-to-r from-neon-blue to-neon-purple text-white font-medium hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed flex-shrink-0"
            >
              {{ submitting ? '发布中...' : '发表评论' }}
            </button>
          </div>
        </div>
      </div>
    </form>

    <!-- Comments List -->
    <div v-if="comments.length > 0" class="comments-list space-y-6">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <!-- Main Comment -->
        <div class="comment-main flex gap-4">
          <div class="w-10 h-10 rounded-full bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center text-white font-bold text-sm flex-shrink-0">
            {{ getAvatarLetter(comment.nickname || comment.userId ? '博主' : '游') }}
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-3 mb-2">
              <span class="font-semibold text-text-primary">{{ comment.nickname || '匿名用户' }}</span>
              <span v-if="comment.userId" class="px-2 py-0.5 rounded-full bg-neon-green/20 text-neon-green text-xs font-medium">博主</span>
              <span class="text-text-muted text-sm">{{ timeAgo(comment.createTime) || formatDate(comment.createTime) }}</span>
            </div>
            <div class="text-text-secondary leading-relaxed whitespace-pre-wrap" v-html="renderMarkdown(comment.content)"></div>
            <div class="flex items-center gap-4 mt-3">
              <button
                @click="toggleLike(comment)"
                class="flex items-center gap-1.5 text-sm transition-colors"
                :class="comment.liked ? 'text-red-400' : 'text-text-muted hover:text-red-400'"
              >
                <svg class="w-4 h-4" :fill="comment.liked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                <span>{{ comment.likes || 0 }}</span>
              </button>
              <button
                @click="toggleReplyForm(comment.id)"
                class="flex items-center gap-1.5 text-sm text-text-muted hover:text-cyber-accent transition-colors"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
                </svg>
                <span>回复</span>
              </button>
            </div>

            <!-- Reply Form -->
            <div v-if="replyingTo === comment.id" class="reply-form mt-4 p-4 rounded-xl bg-cyber-dark/40 border border-cyber-border/30">
              <textarea
                v-model="replyContent"
                :placeholder="`回复 @${comment.nickname || '匿名用户'}...`"
                rows="3"
                class="w-full px-4 py-3 rounded-xl bg-cyber-dark/60 border border-cyber-border/50 text-text-primary placeholder-text-muted/50 focus:outline-none focus:border-cyber-accent/50 focus:ring-2 focus:ring-cyber-accent/20 transition-all resize-none text-sm"
              ></textarea>
              <div class="flex items-center justify-end gap-3 mt-3">
                <button
                  @click="cancelReply"
                  class="px-4 py-2 rounded-lg text-text-muted hover:text-text-primary transition-colors text-sm"
                >
                  取消
                </button>
                <button
                  @click="submitReply(comment.id)"
                  :disabled="!replyContent"
                  class="px-5 py-2 rounded-lg bg-cyber-accent/20 text-cyber-accent hover:bg-cyber-accent/30 transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
                >
                  发布回复
                </button>
              </div>
            </div>

            <!-- Nested Replies -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies-list mt-6 pl-6 border-l-2 border-cyber-border/30 space-y-4">
              <div
                v-for="reply in comment.replies"
                :key="reply.id"
                class="reply-item flex gap-3"
              >
                <div class="w-8 h-8 rounded-full bg-gradient-to-br from-neon-purple to-neon-pink flex items-center justify-center text-white font-bold text-xs flex-shrink-0">
                  {{ getAvatarLetter(reply.nickname || reply.userId ? '博主' : '游') }}
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-1">
                    <span class="font-medium text-text-primary text-sm">{{ reply.nickname || '匿名用户' }}</span>
                    <span v-if="reply.userId" class="px-2 py-0.5 rounded-full bg-neon-green/20 text-neon-green text-xs font-medium">博主</span>
                    <span v-if="reply.replyTo" class="text-cyber-accent text-xs">@{{ reply.replyTo }}</span>
                    <span class="text-text-muted text-xs">{{ timeAgo(reply.createTime) || formatDate(reply.createTime) }}</span>
                  </div>
                  <div class="text-text-secondary text-sm leading-relaxed whitespace-pre-wrap" v-html="renderMarkdown(reply.content)"></div>
                  <div class="flex items-center gap-3 mt-2">
                    <button
                      @click="toggleLike(reply)"
                      class="flex items-center gap-1 text-xs transition-colors"
                      :class="reply.liked ? 'text-red-400' : 'text-text-muted hover:text-red-400'"
                    >
                      <svg class="w-3.5 h-3.5" :fill="reply.liked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                      </svg>
                      <span>{{ reply.likes || 0 }}</span>
                    </button>
                    <button
                      @click="toggleReplyForm(comment.id, reply.nickname)"
                      class="text-xs text-text-muted hover:text-cyber-accent transition-colors"
                    >
                      回复
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-12">
      <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-cyber-surface/50 flex items-center justify-center">
        <svg class="w-8 h-8 text-text-muted" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
      </div>
      <p class="text-text-muted">暂无评论，来发表第一条评论吧</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { publicApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import MarkdownIt from 'markdown-it'

const props = defineProps({
  articleId: { type: [Number, String], required: true }
})

const authStore = useAuthStore()

const md = new MarkdownIt({
  html: false,
  linkify: true,
  typographer: true
})

const comments = ref([])
const submitting = ref(false)
const replyingTo = ref(null)
const replyContent = ref('')
const replyToNickname = ref('')
const form = ref({ content: '' })

const isLoggedIn = computed(() => authStore.isAuthenticated)
const blogAuthorName = computed(() => authStore.userInfo?.nickname || authStore.userInfo?.username || '博主')

const userAvatar = computed(() => {
  if (isLoggedIn.value) {
    return blogAuthorName.value.charAt(0).toUpperCase()
  }
  return '游'
})

function getAvatarLetter(name) {
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}

function timeAgo(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return ''
  const now = new Date()
  const seconds = Math.floor((now - date) / 1000)

  if (seconds < 60) return '刚刚'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}天前`
  const months = Math.floor(days / 30)
  if (months < 12) return `${months}个月前`
  const years = Math.floor(months / 12)
  return `${years}年前`
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return ''
  return date.toLocaleString('zh-CN')
}

function renderMarkdown(content) {
  if (!content) return ''
  return md.render(content)
}

async function fetchComments() {
  try {
    const res = await publicApi.getComments(props.articleId)
    const allComments = res.data || []
    // Organize comments into parent-replies structure
    const parentComments = allComments.filter(c => !c.parentId || c.parentId === 0)
    parentComments.forEach(parent => {
      parent.replies = allComments.filter(c => c.parentId === parent.id)
      parent.liked = false
      parent.replies.forEach(r => r.liked = false)
    })
    comments.value = parentComments
  } catch (err) {
    console.error('Failed to fetch comments:', err)
  }
}

async function submitComment() {
  if (!form.value.content) return
  submitting.value = true
  try {
    const payload = {
      content: form.value.content,
      nickname: isLoggedIn.value ? blogAuthorName.value : '匿名用户'
    }
    await publicApi.addComment(props.articleId, payload)
    form.value = { content: '' }
    await fetchComments()
  } catch (err) {
    alert('评论失败，请重试')
  } finally {
    submitting.value = false
  }
}

function toggleReplyForm(commentId, replyToNick = '') {
  if (replyingTo.value === commentId) {
    replyingTo.value = null
    replyContent.value = ''
    replyToNickname.value = ''
  } else {
    replyingTo.value = commentId
    replyContent.value = ''
    replyToNickname.value = replyToNick
  }
}

function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
  replyToNickname.value = ''
}

async function submitReply(parentId) {
  if (!replyContent.value) return
  submitting.value = true
  try {
    const payload = {
      content: replyContent.value,
      nickname: isLoggedIn.value ? blogAuthorName.value : '匿名用户',
      replyTo: replyToNickname.value || undefined,
      parentId: parentId
    }
    await publicApi.addComment(props.articleId, payload)
    cancelReply()
    await fetchComments()
  } catch (err) {
    alert('回复失败，请重试')
  } finally {
    submitting.value = false
  }
}

function toggleLike(comment) {
  comment.liked = !comment.liked
  comment.likes = (comment.likes || 0) + (comment.liked ? 1 : -1)
}

onMounted(() => fetchComments())
</script>

<style scoped>
.comment-section {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.comment-form {
  transition: border-color 0.2s, box-shadow 0.2s;
}

.comment-form:focus-within {
  border-color: rgba(74, 158, 175, 0.3);
  box-shadow: 0 0 20px rgba(74, 158, 175, 0.1);
}

textarea:focus {
  box-shadow: none;
}
</style>
