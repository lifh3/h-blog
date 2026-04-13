<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-cyber-text">合集管理</h1>
      <button @click="openModal()" class="btn-cyber">新建合集</button>
    </div>

    <div class="glass-card">
      <table class="table-cyber">
        <thead>
          <tr>
            <th>名称</th>
            <th>描述</th>
            <th>文章数</th>
            <th>排序</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="col in collections" :key="col.id">
            <td class="font-medium text-cyber-text">{{ col.name }}</td>
            <td class="text-cyber-muted max-w-xs truncate">{{ col.description || '-' }}</td>
            <td class="text-cyber-muted">{{ col.articleCount || 0 }}</td>
            <td class="text-cyber-muted">{{ col.sort || 0 }}</td>
            <td>
              <div class="flex gap-2">
                <router-link :to="`/collections/${col.id}/edit`" class="text-cyber-cyan hover:underline text-sm">编辑</router-link>
                <button @click="handleDelete(col.id)" class="text-cyber-red hover:underline text-sm">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="collections.length === 0" class="text-center py-8 text-cyber-muted">暂无合集</div>
    </div>

    <!-- Modal -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
        <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="showModal = false"></div>
        <div class="relative glass-card p-6 w-full max-w-md mx-4">
          <h3 class="text-lg font-bold text-cyber-text mb-4">{{ editingCollection ? '编辑合集' : '新建合集' }}</h3>
          
          <form @submit.prevent="handleSubmit" class="space-y-4">
            <div>
              <label class="block text-sm text-cyber-muted mb-1">名称</label>
              <input v-model="form.name" type="text" class="input-cyber" placeholder="合集名称" required />
            </div>
            <div>
              <label class="block text-sm text-cyber-muted mb-1">描述</label>
              <textarea v-model="form.description" class="input-cyber" rows="3" placeholder="合集描述"></textarea>
            </div>
            <div>
              <label class="block text-sm text-cyber-muted mb-1">排序</label>
              <input v-model.number="form.sort" type="number" class="input-cyber" placeholder="0" />
            </div>
            <div class="flex gap-3 justify-end">
              <button type="button" @click="showModal = false" class="btn-cyber-outline">取消</button>
              <button type="submit" class="btn-cyber" :disabled="submitting">{{ submitting ? '保存中...' : '保存' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <ConfirmModal
      :show="showDeleteModal"
      title="删除合集"
      message="确定要删除此合集吗？"
      :loading="deleting"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { collectionsAPI } from '@/api'
import ConfirmModal from '@/components/ConfirmModal.vue'

const collections = ref([])
const showModal = ref(false)
const showDeleteModal = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const editingCollection = ref(null)
const currentDeleteId = ref(null)

const form = ref({ name: '', description: '', sort: 0 })

onMounted(() => fetchCollections())

async function fetchCollections() {
  try {
    const res = await collectionsAPI.list()
    collections.value = res.data.data || []
  } catch (error) {
    console.error('Failed to fetch collections:', error)
  }
}

function openModal(col = null) {
  editingCollection.value = col
  if (col) {
    form.value = { name: col.name, description: col.description || '', sort: col.sort || 0 }
  } else {
    form.value = { name: '', description: '', sort: 0 }
  }
  showModal.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editingCollection.value) {
      await collectionsAPI.update(editingCollection.value.id, form.value)
    } else {
      await collectionsAPI.create(form.value)
    }
    showModal.value = false
    await fetchCollections()
  } catch (error) {
    console.error('Failed to save collection:', error)
  } finally {
    submitting.value = false
  }
}

function handleDelete(id) {
  currentDeleteId.value = id
  showDeleteModal.value = true
}

async function confirmDelete() {
  deleting.value = true
  try {
    await collectionsAPI.delete(currentDeleteId.value)
    await fetchCollections()
    showDeleteModal.value = false
  } catch (error) {
    console.error('Failed to delete collection:', error)
  } finally {
    deleting.value = false
  }
}
</script>
