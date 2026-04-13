<template>
  <Teleport to="body">
    <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center">
      <!-- Overlay -->
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="$emit('cancel')"></div>
      
      <!-- Modal -->
      <div class="relative glass-card p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-bold text-cyber-text mb-2">{{ title }}</h3>
        <p class="text-cyber-muted mb-6">{{ message }}</p>
        
        <div class="flex gap-3 justify-end">
          <button @click="$emit('cancel')" class="btn-cyber-outline" :disabled="loading">
            取消
          </button>
          <button @click="$emit('confirm')" class="btn-cyber" :disabled="loading">
            <span v-if="loading">处理中...</span>
            <span v-else>确认</span>
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
defineProps({
  show: { type: Boolean, default: false },
  title: { type: String, default: '确认操作' },
  message: { type: String, default: '确定要执行此操作吗？' },
  loading: { type: Boolean, default: false }
})

defineEmits(['confirm', 'cancel'])
</script>
