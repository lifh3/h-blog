<template>
  <div class="max-w-7xl mx-auto px-4 py-12">
    <div class="glass-card p-8 mb-8">
      <div class="flex items-center space-x-4">
        <div class="w-16 h-16 rounded-xl bg-gradient-to-br from-neon-purple to-neon-pink flex items-center justify-center text-2xl">
          📁
        </div>
        <div>
          <h1 class="text-2xl font-bold text-text-primary">{{ categoryName }}</h1>
          <p class="text-text-muted">共 {{ totalCount }} 篇文章</p>
        </div>
      </div>
    </div>

    <!-- 筛选和排序控制 -->
    <div class="glass-card p-6 mb-8">
      <div class="flex flex-wrap gap-4 items-center justify-between">
        <div class="flex flex-wrap gap-3 items-center">
          <!-- 分类筛选 -->
          <div class="relative">
            <button @click="showCategoryFilter = !showCategoryFilter" class="filter-btn">
              <span>分类</span>
              <span class="text-neon-accent">{{ selectedCategoryName }}</span>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
              </svg>
            </button>
            <div v-if="showCategoryFilter" class="absolute top-full left-0 mt-2 w-48 glass-card border border-cyber-border rounded-xl shadow-xl z-10 p-2 max-h-64 overflow-y-auto">
              <div @click="selectCategory(null)" class="px-4 py-2 rounded-lg text-sm hover:bg-cyber-accent/10 cursor-pointer" :class="{ 'text-cyber-accent': !currentCategoryId }">全部</div>
              <div v-for="cat in categories" :key="cat.id" 
                @click="selectCategory(cat.id, cat.name)"
                class="px-4 py-2 rounded-lg text-sm hover:bg-cyber-accent/10 cursor-pointer"
                :class="{ 'text-cyber-accent': currentCategoryId === cat.id }">
                {{ cat.name }}
              </div>
            </div>
          </div>

          <!-- 标签筛选 -->
          <div class="relative">
            <button @click="showTagFilter = !showTagFilter" class="filter-btn">
              <span>标签</span>
              <span class="text-neon-accent">{{ selectedTagName }}</span>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
              </svg>
            </button>
            <div v-if="showTagFilter" class="absolute top-full left-0 mt-2 w-48 glass-card border border-cyber-border rounded-xl shadow-xl z-10 p-2 max-h-64 overflow-y-auto">
              <div @click="selectTag(null)" class="px-4 py-2 rounded-lg text-sm hover:bg-cyber-accent/10 cursor-pointer" :class="{ 'text-cyber-accent': !currentTag }">全部</div>
              <div v-for="tag in tags" :key="tag.id" 
                @click="selectTag(tag.name)"
                class="px-4 py-2 rounded-lg text-sm hover:bg-cyber-accent/10 cursor-pointer"
                :class="{ 'text-cyber-accent': currentTag === tag.name }">
                #{{ tag.name }}
              </div>
            </div>
          </div>
        </div>

        <!-- 时间排序 -->
        <div class="flex gap-2">
          <button @click="setSort('latest')" class="sort-btn" :class="{ 'active': sortBy === 'latest' }">最新</button>
          <button @click="setSort('earliest')" class="sort-btn" :class="{ 'active': sortBy === 'earliest' }">最早</button>
        </div>
      </div>
    </div>
    
    <div class="flex flex-col lg:flex-row gap-8">
      <div class="flex-1">
        <ArticleList 
          :articles="articles" 
          :loading="loading"
          :has-more="hasMore"
          @load-more="loadMore"
        />
      </div>
      <div class="lg:w-80">
        <Sidebar :categories="categories" :hot-articles="hotArticles" />
        <div class="mt-6">
          <TagCloud :tags="tags" @tag-click="handleTagClick" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBlogStore } from '@/stores/blog'
import ArticleList from '@/components/ArticleList.vue'
import Sidebar from '@/components/Sidebar.vue'
import TagCloud from '@/components/TagCloud.vue'

const route = useRoute()
const router = useRouter()
const blogStore = useBlogStore()

const articles = ref([])
const categories = ref([])
const tags = ref([])
const hotArticles = ref([])
const categoryName = ref('')
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)
const totalCount = ref(0)

// 筛选和排序状态
const currentCategoryId = ref(route.params.id)
const currentTag = ref(null)
const sortBy = ref('latest')
const showCategoryFilter = ref(false)
const showTagFilter = ref(false)

const selectedCategoryName = computed(() => {
  if (!currentCategoryId.value) return '全部'
  const cat = categories.value.find(c => c.id === currentCategoryId.value)
  return cat?.name || '全部'
})

const selectedTagName = computed(() => {
  return currentTag.value ? `#${currentTag.value}` : '全部'
})

async function fetchData(resetPage = true) {
  if (resetPage) {
    page.value = 1
    articles.value = []
  }
  
  loading.value = true
  try {
    let res
    const params = { 
      page: page.value, 
      pageSize: 10,
      sort: sortBy.value 
    }
    
    if (currentTag.value) {
      // 按标签筛选
      res = await blogStore.fetchArticlesByTag(currentTag.value, params)
      articles.value = resetPage ? blogStore.articles : [...articles.value, ...blogStore.articles]
    } else if (currentCategoryId.value) {
      // 按分类筛选
      res = await blogStore.fetchArticlesByCategory(currentCategoryId.value, params)
      articles.value = resetPage ? blogStore.articles : [...articles.value, ...blogStore.articles]
    } else {
      // 获取所有文章
      res = await blogStore.fetchArticles(params)
      articles.value = resetPage ? blogStore.articles : [...articles.value, ...blogStore.articles]
    }
    
    hasMore.value = blogStore.articles.length >= 10
    totalCount.value = blogStore.articles.length
    
    // 更新分类名称
    if (currentCategoryId.value) {
      const cat = categories.value.find(c => c.id === currentCategoryId.value)
      categoryName.value = cat?.name || '分类'
    } else {
      categoryName.value = '全部分类'
    }
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  page.value++
  await fetchData(false)
}

function selectCategory(id, name) {
  currentCategoryId.value = id
  showCategoryFilter.value = false
  categoryName.value = name || '全部分类'
  router.push(id ? `/category/${id}` : '/')
  fetchData()
}

function selectTag(tagName) {
  currentTag.value = tagName
  showTagFilter.value = false
  fetchData()
}

function setSort(sort) {
  sortBy.value = sort
  fetchData()
}

function handleTagClick(tagName) {
  selectTag(tagName)
}

onMounted(async () => {
  await Promise.all([
    blogStore.fetchCategories().then(() => { categories.value = blogStore.categories }),
    blogStore.fetchTags().then(() => { tags.value = blogStore.tags }),
    blogStore.fetchHotArticles().then(() => { hotArticles.value = blogStore.hotArticles }),
  ])
  
  // 设置当前分类ID
  if (route.params.id) {
    currentCategoryId.value = parseInt(route.params.id)
    const cat = categories.value.find(c => c.id === currentCategoryId.value)
    categoryName.value = cat?.name || '分类'
  }
  
  await fetchData()
})

watch(() => route.params.id, (newId) => {
  if (newId) {
    currentCategoryId.value = parseInt(newId)
    const cat = categories.value.find(c => c.id === currentCategoryId.value)
    categoryName.value = cat?.name || '分类'
  } else {
    currentCategoryId.value = null
    categoryName.value = '全部分类'
  }
  currentTag.value = null
  fetchData()
})
</script>

<style scoped>
.filter-btn {
  @apply px-4 py-2 rounded-lg text-sm flex items-center gap-2;
  background: rgba(74, 158, 175, 0.1);
  border: 1px solid rgba(74, 158, 175, 0.3);
  color: #a8b8c8;
  transition: all 0.2s;
}

.filter-btn:hover {
  background: rgba(74, 158, 175, 0.2);
  border-color: rgba(74, 158, 175, 0.5);
}

.sort-btn {
  @apply px-4 py-2 rounded-lg text-sm;
  background: transparent;
  border: 1px solid #2d3a4f;
  color: #a8b8c8;
  transition: all 0.2s;
}

.sort-btn:hover {
  border-color: #4a9eaf;
  color: #4a9eaf;
}

.sort-btn.active {
  background: rgba(74, 158, 175, 0.2);
  border-color: #4a9eaf;
  color: #4a9eaf;
}
</style>
