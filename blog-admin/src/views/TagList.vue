<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-cyber-text">标签管理</h1>
      <button @click="openModal()" class="btn-cyber">新建标签</button>
    </div>

    <div class="glass-card">
      <table class="table-cyber">
        <thead>
          <tr>
            <th>名称</th>
            <th>ID</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tag in tags" :key="tag.id">
            <td class="font-medium text-cyber-text">{{ tag.name }}</td>
            <td class="text-cyber-muted">{{ tag.id }}</td>
            <td>
              <div class="flex gap-2">
                <button @click="openModal(tag)" class="text-cyber-cyan hover:underline text-sm">编辑</button>
                <button @click="handleDelete(tag.id)" class="text-cyber-red hover:underline text-sm">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="tags.length === 0" class="text-center py-8 text-cyber-muted">暂无标签</div>
    </div>

    <!-- Modal -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
        <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="showModal = false"></div>
        <div class="relative glass-card p-6 w-full max-w-md mx-4">
          <h3 class="text-lg font-bold text-cyber-text mb-4">{{ editingTag ? '编辑标签' : '新建标签' }}</h3>
          
          <form @submit.prevent="handleSubmit" class="space-y-4">
            <div>
              <label class="block text-sm text-cyber-muted mb-1">名称</label>
              <input v-model="form.name" type="text" class="input-cyber" placeholder="标签名称" required />
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
      title="删除标签"
      message="确定要删除此标签吗？"
      :loading="deleting"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { tagsAPI } from '@/api'
import ConfirmModal from '@/components/ConfirmModal.vue'

const tags = ref([])
const showModal = ref(false)
const showDeleteModal = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const editingTag = ref(null)
const currentDeleteId = ref(null)

const form = ref({ name: '' })

onMounted(() => fetchTags())

async function fetchTags() {
  try {
    const res = await tagsAPI.list()
    tags.value = res.data.data || []
  } catch (error) {
    console.error('Failed to fetch tags:', error)
  }
}

function openModal(tag = null) {
  editingTag.value = tag
  if (tag) {
    form.value = { name: tag.name }
  } else {
    form.value = { name: '' }
  }
  showModal.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editingTag.value) {
      await tagsAPI.update(editingTag.value.id, form.value)
    } else {
      await tagsAPI.create(form.value)
    }
    showModal.value = false
    await fetchTags()
  } catch (error) {
    console.error('Failed to save tag:', error)
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
    await tagsAPI.delete(currentDeleteId.value)
    await fetchTags()
    showDeleteModal.value = false
  } catch (error) {
    console.error('Failed to delete tag:', error)
  } finally {
    deleting.value = false
  }
}
</script>
