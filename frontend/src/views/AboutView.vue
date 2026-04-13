<template>
  <div class="max-w-4xl mx-auto px-4 py-12">
    <div class="glass-card p-8 mb-8">
      <div class="flex flex-col md:flex-row items-center md:items-start gap-8">
        <div class="w-32 h-32 rounded-2xl bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center text-5xl shadow-neon-blue">
          🚀
        </div>
        <div class="flex-1 text-center md:text-left">
          <h1 class="text-3xl font-bold mb-2 gradient-text">欢迎来到 TechBlog</h1>
          <p class="text-text-secondary mb-4">
            全栈开发者 | 技术写作者 | 开源爱好者
          </p>
          <p class="text-text-muted leading-relaxed">
            热爱技术，专注于前沿科技探索。分享 Web 开发、移动端、人工智能等领域的实战经验与前沿资讯。
          </p>
          <div class="flex flex-wrap justify-center md:justify-start gap-3 mt-6">
            <a href="https://github.com" target="_blank" class="px-4 py-2 rounded-lg bg-white/5 hover:bg-white/10 transition-colors text-text-secondary hover:text-neon-blue">
              GitHub
            </a>
            <a href="mailto:hello@example.com" class="px-4 py-2 rounded-lg bg-white/5 hover:bg-white/10 transition-colors text-text-secondary hover:text-neon-blue">
              Email
            </a>
            <a href="https://twitter.com" target="_blank" class="px-4 py-2 rounded-lg bg-white/5 hover:bg-white/10 transition-colors text-text-secondary hover:text-neon-blue">
              Twitter
            </a>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
      <div class="glass-card p-6 text-center">
        <div class="text-3xl font-bold text-neon-blue mb-2">{{ stats.articles || 42 }}</div>
        <div class="text-text-muted text-sm">文章</div>
      </div>
      <div class="glass-card p-6 text-center">
        <div class="text-3xl font-bold text-neon-purple mb-2">{{ stats.categories || 8 }}</div>
        <div class="text-text-muted text-sm">分类</div>
      </div>
      <div class="glass-card p-6 text-center">
        <div class="text-3xl font-bold text-neon-green mb-2">{{ stats.tags || 156 }}</div>
        <div class="text-text-muted text-sm">标签</div>
      </div>
      <div class="glass-card p-6 text-center">
        <div class="text-3xl font-bold text-neon-yellow mb-2">{{ formatViews(stats.viewCount || 0) }}</div>
        <div class="text-text-muted text-sm">浏览</div>
      </div>
    </div>

    <div class="glass-card p-8">
      <h2 class="text-xl font-semibold mb-6 flex items-center">
        <span class="w-8 h-1 bg-gradient-to-r from-neon-blue to-neon-purple rounded mr-3"></span>
        技术栈
      </h2>
      <div class="flex flex-wrap gap-3">
        <span v-for="skill in skills" :key="skill" class="px-4 py-2 rounded-lg bg-white/5 border border-cyber-border text-text-secondary hover:border-neon-blue hover:text-neon-blue transition-colors">
          {{ skill }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBlogStore } from '@/stores/blog'

const blogStore = useBlogStore()
const stats = ref({})
const skills = [
  'Vue.js', 'React', 'Node.js', 'Python', 'Go', 
  'TypeScript', 'PostgreSQL', 'MongoDB', 'Docker',
  'Kubernetes', 'AWS', 'Redis', 'GraphQL', 'TailwindCSS'
]

function formatViews(num) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

onMounted(async () => {
  await blogStore.fetchStats()
  stats.value = blogStore.stats
})
</script>
