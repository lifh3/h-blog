<template>
  <aside class="space-y-6">
    <div class="glass-card p-6">
      <h3 class="text-lg font-semibold mb-4 flex items-center">
        <span class="w-8 h-1 bg-gradient-to-r from-neon-blue to-neon-purple rounded mr-3"></span>
        关于博客
      </h3>
      <div class="flex items-center space-x-4 mb-4">
        <div class="w-16 h-16 rounded-xl bg-gradient-to-br from-neon-blue to-neon-purple flex items-center justify-center text-2xl">
          🚀
        </div>
        <div>
          <h4 class="font-semibold text-text-primary">TechBlog</h4>
          <p class="text-text-muted text-sm">探索科技前沿</p>
        </div>
      </div>
      <p class="text-text-secondary text-sm">
        分享前沿技术、编程心得与创意灵感，记录代码的艺术与科技的魅力。
      </p>
    </div>

    <div class="glass-card p-6">
      <h3 class="text-lg font-semibold mb-4 flex items-center">
        <span class="w-8 h-1 bg-gradient-to-r from-neon-purple to-neon-pink rounded mr-3"></span>
        分类
      </h3>
      <div class="space-y-2">
        <router-link 
          v-for="cat in categories" 
          :key="cat.id" 
          :to="`/category/${cat.id}`"
          class="flex items-center justify-between p-2 rounded-lg hover:bg-white/5 transition-colors group"
        >
          <span class="text-text-secondary group-hover:text-neon-blue transition-colors">{{ cat.name }}</span>
          <span class="text-text-muted text-sm">{{ cat.articleCount || 0 }}</span>
        </router-link>
      </div>
    </div>

    <div class="glass-card p-6">
      <h3 class="text-lg font-semibold mb-4 flex items-center">
        <span class="w-8 h-1 bg-gradient-to-r from-neon-green to-neon-yellow rounded mr-3"></span>
        热门文章
      </h3>
      <div class="space-y-3">
        <router-link 
          v-for="(article, idx) in hotArticles" 
          :key="article.id" 
          :to="`/article/${article.id}`"
          class="flex items-start space-x-3 group"
        >
          <span class="flex-shrink-0 w-6 h-6 rounded bg-white/10 text-text-muted text-sm flex items-center justify-center group-hover:bg-neon-blue/20 group-hover:text-neon-blue transition-colors">
            {{ idx + 1 }}
          </span>
          <span class="text-text-secondary text-sm line-clamp-2 group-hover:text-neon-blue transition-colors">
            {{ article.title }}
          </span>
        </router-link>
      </div>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  categories: { type: Array, default: () => [] },
  hotArticles: { type: Array, default: () => [] }
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
