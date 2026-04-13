<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-cyber-text">分类管理</h1>
      <button @click="openModal()" class="btn-cyber">新建分类</button>
    </div>

    <div class="glass-card">
      <table class="table-cyber">
        <thead>
          <tr>
            <th>名称</th>
            <th>Slug</th>
            <th>描述</th>
            <th>文章数</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cat in categories" :key="cat.id">
            <td class="font-medium text-cyber-text">{{ cat.name }}</td>
            <td class="text-cyber-muted">{{ cat.alias || '-' }}</td>
            <td class="text-cyber-muted">{{ cat.description || '-' }}</td>
            <td class="text-cyber-muted">{{ cat.articleCount || 0 }}</td>
            <td>
              <div class="flex gap-2">
                <button @click="openModal(cat)" class="text-cyber-cyan hover:underline text-sm">编辑</button>
                <button @click="handleDelete(cat.id)" class="text-cyber-red hover:underline text-sm">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="categories.length === 0" class="text-center py-8 text-cyber-muted">暂无分类</div>
    </div>

    <!-- Modal -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
        <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="showModal = false"></div>
        <div class="relative glass-card p-6 w-full max-w-md mx-4">
          <h3 class="text-lg font-bold text-cyber-text mb-4">{{ editingCategory ? '编辑分类' : '新建分类' }}</h3>
          
          <form @submit.prevent="handleSubmit" class="space-y-4">
            <div>
              <label class="block text-sm text-cyber-muted mb-1">名称</label>
              <input v-model="form.name" type="text" class="input-cyber" placeholder="分类名称" required />
            </div>
            <div>
              <label class="block text-sm text-cyber-muted mb-1">别名</label>
              <input v-model="form.alias" type="text" class="input-cyber" placeholder="category-alias" />
            </div>
            <div>
              <label class="block text-sm text-cyber-muted mb-1">描述</label>
              <textarea v-model="form.description" class="input-cyber" rows="3" placeholder="分类描述"></textarea>
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
      title="删除分类"
      message="确定要删除此分类吗？"
      :loading="deleting"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { categoriesAPI } from '@/api'
import ConfirmModal from '@/components/ConfirmModal.vue'

const categories = ref([])
const showModal = ref(false)
const showDeleteModal = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const editingCategory = ref(null)
const currentDeleteId = ref(null)

const form = ref({ name: '', alias: '', description: '' })

onMounted(() => fetchCategories())

async function fetchCategories() {
  try {
    const res = await categoriesAPI.list()
    categories.value = res.data.data || []
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

function openModal(cat = null) {
  editingCategory.value = cat
  if (cat) {
    form.value = { name: cat.name, alias: cat.alias, description: cat.description || '' }
  } else {
    form.value = { name: '', alias: '', description: '' }
  }
  showModal.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editingCategory.value) {
      await categoriesAPI.update(editingCategory.value.id, form.value)
    } else {
      await categoriesAPI.create(form.value)
    }
    showModal.value = false
    await fetchCategories()
  } catch (error) {
    console.error('Failed to save category:', error)
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
    await categoriesAPI.delete(currentDeleteId.value)
    await fetchCategories()
    showDeleteModal.value = false
  } catch (error) {
    console.error('Failed to delete category:', error)
  } finally {
    deleting.value = false
  }
}
</script>
