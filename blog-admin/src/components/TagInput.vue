<template>
  <div>
    <!-- Tags display -->
    <div class="flex flex-wrap gap-2 mb-3">
      <span
        v-for="tag in tags"
        :key="tag.id || tag"
        class="inline-flex items-center gap-1 px-2 py-1 rounded bg-cyber-cyan/10 text-cyber-cyan text-sm"
      >
        {{ typeof tag === 'object' ? tag.name : tag }}
        <button @click="removeTag(tag)" class="hover:text-cyber-red">&times;</button>
      </span>
    </div>

    <!-- Input -->
    <input
      v-model="inputValue"
      @keydown.enter.prevent="addTag"
      @keydown.backspace="handleBackspace"
      class="input-cyber"
      placeholder="输入标签名后按回车添加"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  tags: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:tags'])

const inputValue = ref('')

function addTag() {
  const tag = inputValue.value.trim()
  if (!tag) return
  
  const currentTags = props.tags.map(t => typeof t === 'object' ? t.name : t)
  if (!currentTags.includes(tag)) {
    emit('update:tags', [...props.tags, tag])
  }
  inputValue.value = ''
}

function removeTag(tag) {
  const tagName = typeof tag === 'object' ? tag.name : tag
  emit('update:tags', props.tags.filter(t => (typeof t === 'object' ? t.name : t) !== tagName))
}

function handleBackspace() {
  if (inputValue.value === '' && props.tags.length > 0) {
    emit('update:tags', props.tags.slice(0, -1))
  }
}
</script>
