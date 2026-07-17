<template>
  <div class="pagination" v-if="total > 0">
    <span class="pagination-info">共 {{ total }} 条，第 {{ current }} / {{ totalPages }} 页</span>
    <div class="pagination-btns">
      <button :disabled="current <= 1" @click="go(1)">首页</button>
      <button :disabled="current <= 1" @click="go(current - 1)">上一页</button>
      <button
        v-for="p in visiblePages"
        :key="p"
        :class="{ active: p === current }"
        @click="go(p)"
      >{{ p }}</button>
      <button :disabled="current >= totalPages" @click="go(current + 1)">下一页</button>
      <button :disabled="current >= totalPages" @click="go(totalPages)">末页</button>
    </div>
    <span class="pagination-jump">
      跳至
      <input
        type="number"
        v-model.number="jumpPage"
        :max="totalPages"
        :min="1"
        @keyup.enter="jump"
      />
      页
    </span>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  total: { type: Number, default: 0 },
  current: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 }
})

const emit = defineEmits(['change'])

const jumpPage = ref(1)

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))

const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const cur = props.current
  let start = Math.max(1, cur - 2)
  let end = Math.min(total, cur + 2)
  if (end - start < 4) {
    if (start === 1) end = Math.min(total, start + 4)
    else start = Math.max(1, end - 4)
  }
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

function go(page) {
  if (page >= 1 && page <= totalPages.value && page !== props.current) {
    emit('change', page)
  }
}

function jump() {
  if (jumpPage.value >= 1 && jumpPage.value <= totalPages.value) {
    go(jumpPage.value)
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  padding: 20px 0;
  font-size: 14px;
  color: #666;
}

.pagination-info {
  margin-right: 12px;
}

.pagination-btns {
  display: flex;
  gap: 4px;
}

.pagination-btns button {
  min-width: 36px;
  height: 36px;
  padding: 0 8px;
  border: 1px solid #ddd;
  border-radius: var(--radius, 4px);
  background: #fff;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btns button:hover:not(:disabled) {
  border-color: var(--primary, #409eff);
  color: var(--primary, #409eff);
}

.pagination-btns button.active {
  background: var(--primary, #409eff);
  border-color: var(--primary, #409eff);
  color: #fff;
}

.pagination-btns button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-jump {
  margin-left: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-jump input {
  width: 48px;
  height: 30px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: var(--radius, 4px);
  outline: none;
}

.pagination-jump input:focus {
  border-color: var(--primary, #409eff);
}
</style>