<template>
  <div>
    <h1 class="text-2xl font-bold text-cyber-text mb-6">个人设置</h1>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Profile Info -->
      <div class="glass-card p-6">
        <h2 class="text-lg font-bold text-cyber-text mb-4">基本信息</h2>
        
        <form @submit.prevent="handleUpdateProfile" class="space-y-4">
          <div>
            <label class="block text-sm text-cyber-muted mb-1">用户名</label>
            <input v-model="profileForm.username" type="text" class="input-cyber" placeholder="用户名" disabled />
          </div>
          <div>
            <label class="block text-sm text-cyber-muted mb-1">昵称</label>
            <input v-model="profileForm.nickname" type="text" class="input-cyber" placeholder="昵称" />
          </div>
          <div>
            <label class="block text-sm text-cyber-muted mb-1">邮箱</label>
            <input v-model="profileForm.email" type="email" class="input-cyber" placeholder="邮箱" />
          </div>
          <button type="submit" class="btn-cyber" :disabled="updating">
            {{ updating ? '保存中...' : '保存修改' }}
          </button>
        </form>
      </div>

      <!-- Change Password -->
      <div class="glass-card p-6">
        <h2 class="text-lg font-bold text-cyber-text mb-4">修改密码</h2>
        
        <form @submit.prevent="handleChangePassword" class="space-y-4">
          <div>
            <label class="block text-sm text-cyber-muted mb-1">当前密码</label>
            <input v-model="passwordForm.currentPassword" type="password" class="input-cyber" placeholder="当前密码" required />
          </div>
          <div>
            <label class="block text-sm text-cyber-muted mb-1">新密码</label>
            <input v-model="passwordForm.newPassword" type="password" class="input-cyber" placeholder="新密码" required />
          </div>
          <div>
            <label class="block text-sm text-cyber-muted mb-1">确认新密码</label>
            <input v-model="passwordForm.confirmPassword" type="password" class="input-cyber" placeholder="确认新密码" required />
          </div>
          <div v-if="passwordError" class="text-cyber-red text-sm">{{ passwordError }}</div>
          <button type="submit" class="btn-cyber" :disabled="changingPassword">
            {{ changingPassword ? '修改中...' : '修改密码' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { usersAPI } from '@/api'

const authStore = useAuthStore()

const profileForm = ref({ username: '', nickname: '', email: '' })
const passwordForm = ref({ currentPassword: '', newPassword: '', confirmPassword: '' })
const updating = ref(false)
const changingPassword = ref(false)
const passwordError = ref('')

onMounted(async () => {
  try {
    const res = await usersAPI.profile()
    const user = res.data.data
    profileForm.value = {
      username: user.username || '',
      nickname: user.nickname || '',
      email: user.email || ''
    }
  } catch (error) {
    console.error('Failed to fetch profile:', error)
  }
})

async function handleUpdateProfile() {
  updating.value = true
  try {
    await usersAPI.updateProfile(profileForm.value)
    await authStore.fetchUser()
  } catch (error) {
    console.error('Failed to update profile:', error)
  } finally {
    updating.value = false
  }
}

async function handleChangePassword() {
  passwordError.value = ''
  
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordError.value = '两次输入的密码不一致'
    return
  }
  
  changingPassword.value = true
  try {
    await usersAPI.changePassword(passwordForm.value)
    passwordForm.value = { currentPassword: '', newPassword: '', confirmPassword: '' }
  } catch (error) {
    passwordError.value = error.response?.data?.message || '修改失败'
  } finally {
    changingPassword.value = false
  }
}
</script>
