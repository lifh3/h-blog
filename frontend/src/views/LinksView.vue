<template>
  <div class="min-h-screen">
    <section class="relative h-72 flex items-center justify-center overflow-hidden cyber-grid-bg">
      <div class="absolute inset-0 bg-gradient-to-b from-cyber-dark/80 to-cyber-dark"></div>
      <div class="relative text-center px-4">
        <h1 class="text-5xl font-bold mb-4 gradient-text">友情链接</h1>
        <p class="text-text-muted">交换链接 · 共同成长</p>
      </div>
    </section>

    <section class="max-w-5xl mx-auto px-4 py-16">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <a
          v-for="link in links"
          :key="link.id"
          :href="link.url"
          target="_blank"
          class="glass-card p-6 transition-all duration-500 glass-card-hover group"
        >
          <div class="flex items-center gap-4 mb-3">
            <img v-if="link.avatar" :src="link.avatar" class="w-10 h-10 rounded-full object-cover" :alt="link.name">
            <div v-else class="w-10 h-10 rounded-full bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center text-white font-bold">
              {{ link.name.charAt(0) }}
            </div>
            <div>
              <h3 class="text-text-primary font-semibold group-hover:text-neon-blue transition-colors">{{ link.name }}</h3>
              <p class="text-text-muted text-xs">{{ link.description }}</p>
            </div>
          </div>
        </a>
      </div>

      <div class="mt-16 glass-card p-8 text-center">
        <h2 class="text-2xl font-bold text-text-primary mb-4">申请友链</h2>
        <p class="text-text-secondary mb-6">欢迎交换友链，请在下方留言或通过其他方式联系博主。</p>
        <div class="text-left space-y-3 text-sm text-text-muted">
          <p>• 网站名称：TechBlog</p>
          <p>• 网站地址：https://techblog.example.com</p>
          <p>• 网站描述：分享技术与生活</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const links = ref([
  { id: 1, name: 'Vue.js', url: 'https://vuejs.org', description: '渐进式 JavaScript 框架', avatar: 'https://vuejs.org/logo.svg' },
  { id: 2, name: 'Vite', url: 'https://vitejs.dev', description: '下一代前端构建工具', avatar: 'https://vitejs.dev/logo.svg' },
  { id: 3, name: 'TailwindCSS', url: 'https://tailwindcss.com', description: '原子化 CSS 框架', avatar: '' },
])
</script>
