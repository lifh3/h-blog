<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-cyber-text">{{ isEdit ? '编辑文章' : '新建文章' }}</h1>
      <div class="flex gap-3">
        <button @click="handleSave(false)" class="btn-cyber-outline" :disabled="saving">
          保存草稿
        </button>
        <button @click="handleSave(true)" class="btn-cyber" :disabled="saving">
          {{ saving ? '保存中...' : '发布' }}
        </button>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-6">
      <!-- Editor -->
      <div class="col-span-2 glass-card p-4" style="height: calc(100vh - 200px);">
        <MarkdownEditor v-model="form.content" />
      </div>

      <!-- Settings -->
      <div class="space-y-4">
        <div class="glass-card p-4">
          <h3 class="text-sm font-medium text-cyber-muted mb-3">文章设置</h3>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-cyber-muted mb-1">标题</label>
              <input v-model="form.title" type="text" class="input-cyber" placeholder="文章标题" />
            </div>

            <div>
              <label class="block text-sm text-cyber-muted mb-1">分类</label>
              <select v-model="form.categoryId" class="input-cyber">
                <option value="">请选择分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
              </select>
            </div>

            <div>
              <label class="block text-sm text-cyber-muted mb-1">标签</label>
              <TagInput v-model="form.tags" />
            </div>

            <div>
              <label class="block text-sm text-cyber-muted mb-1">合集</label>
              <select v-model="form.collectionId" class="input-cyber">
                <option value="">不添加到合集</option>
                <option v-for="col in collections" :key="col.id" :value="col.id">{{ col.name }}</option>
              </select>
            </div>

            <div>
              <label class="block text-sm text-cyber-muted mb-1">封面图 URL</label>
              <input v-model="form.coverImage" type="text" class="input-cyber" placeholder="https://..." />
            </div>

            <div>
              <label class="block text-sm text-cyber-muted mb-1">状态</label>
              <div class="flex items-center gap-2">
                <input v-model="form.published" type="checkbox" id="published" class="w-4 h-4" />
                <label for="published" class="text-sm text-cyber-text">立即发布</label>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MarkdownEditor from '@/components/MarkdownEditor.vue'
import TagInput from '@/components/TagInput.vue'
import { articlesAPI, adminArticlesAPI, categoriesAPI, collectionsAPI } from '@/api'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const saving = ref(false)

const form = ref({
  title: '',
  content: '',
  categoryId: '',
  tags: [],
  collectionId: '',
  coverImage: '',
  published: false
})

const categories = ref([])
const collections = ref([])

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchCollections()])
  
  if (isEdit.value) {
    await fetchArticle()
  }
})

async function fetchCategories() {
  try {
    const res = await categoriesAPI.list()
    categories.value = res.data.data || []
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

async function fetchCollections() {
  try {
    const res = await collectionsAPI.list()
    collections.value = res.data.data || []
  } catch (error) {
    console.error('Failed to fetch collections:', error)
  }
}

async function fetchArticle() {
  try {
    const res = await adminArticlesAPI.get(route.params.id)
    const article = res.data.data
    form.value = {
      title: article.title,
      content: article.content,
      categoryId: article.categoryId || '',
      tags: article.tags || [],
      collectionId: article.collectionId || '',
      coverImage: article.coverImage || '',
      published: article.status === 1
    }
  } catch (error) {
    console.error('Failed to fetch article:', error)
  }
}

async function handleSave(publish = false) {
  saving.value = true
  try {
    const data = {
      ...form.value,
      status: (publish || form.value.published) ? 1 : 0
    }

    if (isEdit.value) {
      await adminArticlesAPI.update(route.params.id, data)
    } else {
      await articlesAPI.create(data)
    }
    router.push('/articles')
  } catch (error) {
    console.error('Failed to save article:', error)
  } finally {
    saving.value = false
  }
}
</script>
