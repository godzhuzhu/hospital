<template>
  <span class="rate-star" :class="{ readonly: readonly }">
    <span
      v-for="i in 5"
      :key="i"
      class="star"
      :class="starClass(i)"
      @click="handleClick(i)"
      @mouseenter="handleHover(i)"
      @mouseleave="handleLeave"
    >
      {{ starChar(i) }}
    </span>
    <span v-if="showText" class="rate-text">{{ displayValue }}分</span>
  </span>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  modelValue: { type: Number, default: 0 },
  readonly: { type: Boolean, default: false },
  showText: { type: Boolean, default: false },
  size: { type: String, default: '18px' }
})

const emit = defineEmits(['update:modelValue'])

const hoverValue = ref(0)

const displayValue = computed(() => {
  return hoverValue.value || props.modelValue
})

function starClass(i) {
  const val = hoverValue.value || props.modelValue
  if (i <= Math.floor(val)) return 'full'
  if (i - 0.5 <= val) return 'half'
  return 'empty'
}

function starChar(i) {
  const val = hoverValue.value || props.modelValue
  if (i <= Math.floor(val)) return '★'
  if (i - 0.5 <= val) return '★'
  return '★'
}

function handleClick(i) {
  if (props.readonly) return
  emit('update:modelValue', i)
}

function handleHover(i) {
  if (props.readonly) return
  hoverValue.value = i
}

function handleLeave() {
  hoverValue.value = 0
}
</script>

<style scoped>
.rate-star {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: v-bind(size);
  line-height: 1;
}

.rate-star:not(.readonly) .star {
  cursor: pointer;
}

.star {
  transition: color 0.15s;
}

.star.full {
  color: #f7ba2a;
}

.star.half {
  color: #f7ba2a;
  opacity: 0.6;
}

.star.empty {
  color: #c0c4cc;
}

.rate-text {
  margin-left: 6px;
  font-size: 14px;
  color: #f7ba2a;
  font-weight: 600;
}
</style>