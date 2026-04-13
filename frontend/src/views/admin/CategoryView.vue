<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-semibold">分类管理</h2>
      <button @click="showModal = true" class="btn-cyber">
        新建分类
      </button>
    </div>
    
    <div class="glass-card">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-cyber-border">
              <th class="text-left p-4 text-text-muted font-medium">名称</th>
              <th class="text-left p-4 text-text-muted font-medium">描述</th>
              <th class="text-left p-4 text-text-muted font-medium">文章数</th>
              <th class="text-left p-4 text-text-muted font-medium">排序</th>
              <th class="text-right p-4 text-text-muted font-medium">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cat in categories" :key="cat.id" class="border-b border-cyber-border/50 hover:bg-white/5 transition-colors">
              <td class="p-4 text-text-primary font-medium">{{ cat.name }}</td>
              <td class="p-4 text-text-secondary">{{ cat.description || '-' }}</td>
              <td class="p-4 text-text-secondary">{{ cat.article_count || 0 }}</td>
              <td class="p-4 text-text-muted">{{ cat.sort || 0 }}</td>
              <td class="p-4 text-right">
                <button @click="editCategory(cat)" class="text-neon-blue hover:underline mr-4">编辑</button>
                <button @click="deleteCategory(cat.id)" class="text-neon-pink hover:underline">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="!categories.length" class="text-center py-12 text-text-muted">
        暂无分类
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeModal">
      <div class="glass-card p-6 w-full max-w-md">
        <h3 class="text-lg font-semibold mb-4">{{ editingCategory ? '编辑分类' : '新建分类' }}</h3>
        <form @submit.prevent="saveCategory">
          <div class="space-y-4">
            <div>
              <label class="block text-text-secondary text-sm mb-2">名称</label>
              <input v-model="form.name" type="text" class="input-cyber" required>
            </div>
            <div>
              <label class="block text-text-secondary text-sm mb-2">描述</label>
              <input v-model="form.description" type="text" class="input-cyber">
            </div>
            <div>
              <label class="block text-text-secondary text-sm mb-2">排序</label>
              <input v-model.number="form.sort" type="number" class="input-cyber">
            </div>
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

const categories = ref([])
const showModal = ref(false)
const editingCategory = ref(null)
const form = ref({ name: '', description: '', sort: 0 })

async function fetchCategories() {
  try {
    const res = await adminApi.getAdminCategories()
    categories.value = res.list || res || []
  } catch (err) {
    console.error('Failed to fetch categories:', err)
  }
}

function editCategory(cat) {
  editingCategory.value = cat
  form.value = { ...cat }
  showModal.value = true
}

async function saveCategory() {
  try {
    if (editingCategory.value) {
      await adminApi.updateCategory(editingCategory.value.id, form.value)
    } else {
      await adminApi.createCategory(form.value)
    }
    closeModal()
    await fetchCategories()
  } catch (err) {
    alert('保存失败')
  }
}

async function deleteCategory(id) {
  if (!confirm('确定要删除这个分类吗？')) return
  try {
    await adminApi.deleteCategory(id)
    await fetchCategories()
  } catch (err) {
    alert('删除失败')
  }
}

function closeModal() {
  showModal.value = false
  editingCategory.value = null
  form.value = { name: '', description: '', sort: 0 }
}

onMounted(fetchCategories)
</script>
