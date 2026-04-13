<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-semibold">标签管理</h2>
      <button @click="showModal = true" class="btn-cyber">
        新建标签
      </button>
    </div>
    
    <div class="glass-card">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-cyber-border">
              <th class="text-left p-4 text-text-muted font-medium">名称</th>
              <th class="text-left p-4 text-text-muted font-medium">文章数</th>
              <th class="text-right p-4 text-text-muted font-medium">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="tag in tags" :key="tag.id" class="border-b border-cyber-border/50 hover:bg-white/5 transition-colors">
              <td class="p-4">
                <span class="text-neon-purple">#</span>
                <span class="text-text-primary font-medium ml-1">{{ tag.name }}</span>
              </td>
              <td class="p-4 text-text-secondary">{{ tag.article_count || 0 }}</td>
              <td class="p-4 text-right">
                <button @click="editTag(tag)" class="text-neon-blue hover:underline mr-4">编辑</button>
                <button @click="deleteTag(tag.id)" class="text-neon-pink hover:underline">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="!tags.length" class="text-center py-12 text-text-muted">
        暂无标签
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeModal">
      <div class="glass-card p-6 w-full max-w-md">
        <h3 class="text-lg font-semibold mb-4">{{ editingTag ? '编辑标签' : '新建标签' }}</h3>
        <form @submit.prevent="saveTag">
          <div class="mb-4">
            <label class="block text-text-secondary text-sm mb-2">名称</label>
            <input v-model="form.name" type="text" class="input-cyber" required>
          </div>
          <div class="flex justify-end space-x-4 mt-6">
            <button type="button" @click="closeModal" class="px-4 py-2 text-text-secondary hover:text-text-primary transition-colors">取消</button>
            <button type="submit" class="btn-cyber">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const tags = ref([])
const showModal = ref(false)
const editingTag = ref(null)
const form = ref({ name: '' })

async function fetchTags() {
  try {
    const res = await adminApi.getAdminTags()
    tags.value = res.list || res || []
  } catch (err) {
    console.error('Failed to fetch tags:', err)
  }
}

function editTag(tag) {
  editingTag.value = tag
  form.value = { name: tag.name }
  showModal.value = true
}

async function saveTag() {
  try {
    if (editingTag.value) {
      await adminApi.updateTag(editingTag.value.id, form.value)
    } else {
      await adminApi.createTag(form.value)
    }
    closeModal()
    await fetchTags()
  } catch (err) {
    alert('保存失败')
  }
}

async function deleteTag(id) {
  if (!confirm('确定要删除这个标签吗？')) return
  try {
    await adminApi.deleteTag(id)
    await fetchTags()
  } catch (err) {
    alert('删除失败')
  }
}

function closeModal() {
  showModal.value = false
  editingTag.value = null
  form.value = { name: '' }
}

onMounted(fetchTags)
</script>
