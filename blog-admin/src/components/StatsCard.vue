<template>
  <div class="glass-card p-6 flex items-center gap-4">
    <div class="w-12 h-12 rounded-xl flex items-center justify-center text-2xl" :style="{ background: iconBg }">
      {{ icon }}
    </div>
    <div>
      <div class="text-2xl font-bold text-cyber-text">{{ value }}</div>
      <div class="text-sm text-cyber-muted">{{ label }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  icon: { type: String, default: '📊' },
  value: { type: [String, Number], default: 0 },
  label: { type: String, default: '' },
  color: { type: String, default: 'cyan' }
})

const iconBg = computed(() => {
  const colors = {
    cyan: 'rgba(88, 166, 255, 0.15)',
    purple: 'rgba(163, 113, 247, 0.15)',
    green: 'rgba(63, 185, 80, 0.15)',
    yellow: 'rgba(210, 153, 34, 0.15)'
  }
  return colors[props.color] || colors.cyan
})
</script>
