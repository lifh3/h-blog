<template>
  <div class="glass-card p-6">
    <h3 class="text-lg font-semibold mb-4 flex items-center">
      <span class="w-8 h-1 bg-gradient-to-r from-neon-yellow to-neon-pink rounded mr-3"></span>
      标签云
    </h3>
    <div class="flex flex-wrap gap-2">
      <button
        v-for="tag in tags" 
        :key="tag.id" 
        @click="handleTagClick(tag.name)"
        :style="{ fontSize: getSize(tag.count) + 'rem' }"
        class="tag-item transition-all duration-300"
      >
        #{{ tag.name }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  tags: { type: Array, default: () => [] }
})

const emit = defineEmits(['tag-click'])

const maxCount = computed(() => {
  return Math.max(...props.tags.map(t => t.count || 1), 1)
})

function getSize(count) {
  const minSize = 0.8
  const maxSize = 1.4
  const ratio = (count || 1) / maxCount.value
  return minSize + (maxSize - minSize) * ratio
}

function handleTagClick(tagName) {
  emit('tag-click', tagName)
}
</script>

<style scoped>
.tag-item {
  @apply px-3 py-1 rounded-full text-neon-purple cursor-pointer;
  background: rgba(191, 90, 242, 0.1);
  border: 1px solid rgba(191, 90, 242, 0.3);
}

.tag-item:hover {
  background: rgba(191, 90, 242, 0.2);
  box-shadow: 0 0 15px rgba(191, 90, 242, 0.4);
  transform: scale(1.1);
}
</style>
