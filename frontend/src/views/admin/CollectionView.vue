<template>
  <div class="p-6">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-white">合集管理</h1>
      <button @click="showCreateModal = true" class="btn-cyber">
        新建合集
      </button>
    </div>

    <!-- 合集列表 -->
    <div class="glass-card rounded-xl border border-cyber-border overflow-hidden">
      <table class="w-full">
        <thead>
          <tr class="border-b border-cyber-border bg-cyber-dark/50">
            <th class="p-4 text-left text-sm font-medium text-text-muted">名称</th>
            <th class="p-4 text-left text-sm font-medium text-text-muted">描述</th>
            <th class="p-4 text-left text-sm font-medium text-text-muted">文章数</th>
            <th class="p-4 text-left text-sm font-medium text-text-muted">排序</th>
            <th class="p-4 text-left text-sm font-medium text-text-muted">创建时间</th>
            <th class="p-4 text-right text-sm font-medium text-text-muted">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="collection in collections" :key="collection.id" 
              class="border-b border-cyber-border/30 hover:bg-white/5 transition-colors">
            <td class="p-4">
              <div class="flex items-center gap-3">
                <div v-if="collection.coverImage" class="w-12 h-12 rounded-lg overflow-hidden flex-shrink-0">
                  <img :src="collection.coverImage" :alt="collection.name" class="w-full h-full object-cover">
                </div>
                <div v-else class="w-12 h-12 rounded-lg bg-gradient-to-br from-neon-purple/20 to-neon-pink/20 flex items-center justify-center text-xl">
                  📚
                </div>
                <span class="font-medium text-text-primary">{{ collection.name }}</span>
              </div>
            </td>
            <td class="p-4 text-sm text-text-secondary max-w-xs truncate">
              {{ collection.description || '暂无描述' }}
            </td>
            <td class="p-4 text-sm text-text-secondary">
              {{ collection.articleCount || 0 }}
            </td>
            <td class="p-4 text-sm text-text-secondary">
              {{ collection.sort || 0 }}
            </td>
            <td class="p-4 text-sm text-text-muted">
              {{ formatDate(collection.createTime) }}
            </td>
            <td class="p-4 text-right">
              <button @click="goToEdit(collection.id)" class="text-neon-blue hover:underline mr-4">编辑</button>
              <button @click="handleDelete(collection)" class="text-neon-pink hover:underline">删除</button>
            </td>
          </tr>
          <tr v-if="collections.length === 0">
            <td colspan="6" class="p-8 text-center text-text-muted">
              暂无合集，点击上方按钮创建第一个合集
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 创建/编辑模态框 -->
    <div v-if="showCreateModal || editingCollection" 
         class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
         @click.self="closeModal">
      <div class="glass-card rounded-xl p-6 w-full max-w-md">
        <h2 class="text-xl font-bold text-white mb-4">
          {{ editingCollection ? '编辑合集' : '新建合集' }}
        </h2>
        
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label class="block text-sm text-text-secondary mb-2">名称 *</label>
            <input v-model="form.name" type="text" required 
                   class="input-cyber w-full" placeholder="输入合集名称">
          </div>
          
          <div>
            <label class="block text-sm text-text-secondary mb-2">描述</label>
            <textarea v-model="form.description" rows="3"
                      class="input-cyber w-full resize-none" 
                      placeholder="输入合集描述"></textarea>
          </div>
          
          <div>
            <label class="block text-sm text-text-secondary mb-2">封面图 URL</label>
            <input v-model="form.coverImage" type="url" 
                   class="input-cyber w-full" placeholder="https://...">
          </div>
          
          <div>
            <label class="block text-sm text-text-secondary mb-2">排序</label>
            <input v-model.number="form.sort" type="number" 
                   class="input-cyber w-full" placeholder="0">
          </div>
          
          <div class="flex gap-3 pt-4">
            <button type="submit" :disabled="submitting" class="btn-cyber flex-1">
              {{ submitting ? '保存中...' : '保存' }}
            </button>
            <button type="button" @click="closeModal" class="btn-cyber-outline flex-1">
              取消
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'

const router = useRouter()
const collections = ref([])
const showCreateModal = ref(false)
const editingCollection = ref(null)
const submitting = ref(false)

const form = ref({
  name: '',
  description: '',
  coverImage: '',
  sort: 0
})

async function fetchCollections() {
  try {
    const res = await adminApi.getAdminCollections()
    collections.value = res.list || res || []
  } catch (err) {
    console.error('Failed to fetch collections:', err)
  }
}

function goToEdit(id) {
  router.push(`/admin/collections/${id}/edit`)
}

function handleDelete(collection) {
  if (!confirm(`确定要删除合集 "${collection.name}" 吗？`)) return
  
  adminApi.deleteCollection(collection.id)
    .then(() => {
      collections.value = collections.value.filter(c => c.id !== collection.id)
    })
    .catch(err => {
      alert('删除失败: ' + (err.message || err))
    })
}

function openEditModal(collection) {
  editingCollection.value = collection
  form.value = {
    name: collection.name,
    description: collection.description || '',
    coverImage: collection.coverImage || '',
    sort: collection.sort || 0
  }
}

function closeModal() {
  showCreateModal.value = false
  editingCollection.value = null
  form.value = {
    name: '',
    description: '',
    coverImage: '',
    sort: 0
  }
}

function handleSubmit() {
  submitting.value = true
  
  const promise = editingCollection.value
    ? adminApi.updateCollection(editingCollection.value.id, form.value)
    : adminApi.createCollection(form.value)
  
  promise
    .then(() => {
      fetchCollections()
      closeModal()
    })
    .catch(err => {
      alert((editingCollection.value ? '更新' : '创建') + '失败: ' + (err.message || err))
    })
    .finally(() => {
      submitting.value = false
    })
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { 
    year: 'numeric', month: 'short', day: 'numeric' 
  })
}

onMounted(() => {
  fetchCollections()
})
</script>
