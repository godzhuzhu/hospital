<template>
  <div class="my-consult-page">
    <AppHeader />
    <div class="page-body">
      <AppSidebar />
      <div class="main">
        <h2>我的咨询</h2>
        <div class="tabs">
          <button v-for="t in tabs" :key="t.value" :class="{ active: status === t.value }" @click="switchTab(t.value)">{{ t.label }}</button>
        </div>
        <div class="order-list">
          <div class="order-card" v-for="o in orders" :key="o.id">
            <div class="order-header">
              <span class="order-no">订单号：{{ o.orderNo }}</span>
              <span class="status" :class="'status-' + o.status">{{ statusMap[o.status] || '未知' }}</span>
            </div>
            <div class="order-body">
              <p>医生：{{ o.doctorName }}</p>
              <p>医院：{{ o.hospitalName }}</p>
              <p>就诊人：{{ o.familyMemberName }}</p>
              <p class="fee">¥{{ o.amount }}</p>
            </div>
            <div class="order-actions" v-if="o.status === 0">
              <button class="btn-primary" @click="$router.push(`/consult/pay/${o.orderNo}`)">去支付</button>
            </div>
          </div>
        </div>
        <div class="empty" v-if="orders.length === 0">暂无咨询记录</div>
        <Pagination :total="total" :current="page" :pageSize="pageSize" @change="handlePage" />
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import AppSidebar from '@/components/AppSidebar.vue'
import Pagination from '@/components/Pagination.vue'
import { getMyConsults } from '@/api/consult'

const orders = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const status = ref('')
const tabs = [
  { label: '全部', value: '' },
  { label: '待支付', value: '0' },
  { label: '进行中', value: '1' },
  { label: '已完成', value: '2' }
]
const statusMap = { 0: '待支付', 1: '进行中', 2: '已完成' }

onMounted(fetchData)

async function fetchData() {
  try {
    const res = await getMyConsults({ page: page.value, pageSize: pageSize.value, status: status.value })
    const d = res.data.data || res.data
    orders.value = d.records || []
    total.value = d.total || 0
  } catch (e) { console.error('加载咨询列表失败', e) }
}

function switchTab(v) { status.value = v; page.value = 1; fetchData() }
function handlePage(p) { page.value = p; fetchData() }
</script>

<style scoped>
.my-consult-page { min-height: 100vh; background: var(--bg); }
.page-body { display: flex; gap: 24px; }
.main { flex: 1; }
.main h2 { font-size: 20px; margin-bottom: 16px; }
.tabs { display: flex; gap: 8px; margin-bottom: 16px; }
.tabs button { padding: 6px 16px; background: var(--bg-white); border: 1px solid var(--border); border-radius: 4px; font-size: 13px; cursor: pointer; }
.tabs button.active { background: var(--primary); color: #fff; border-color: var(--primary); }
.order-list { display: flex; flex-direction: column; gap: 12px; }
.order-card { padding: 16px; background: var(--bg-white); border-radius: var(--radius); box-shadow: var(--shadow); }
.order-header { display: flex; justify-content: space-between; margin-bottom: 10px; padding-bottom: 8px; border-bottom: 1px solid var(--border); }
.order-no { font-size: 13px; color: var(--text-muted); }
.status-0 { color: #e53935; }
.status-1 { color: var(--primary); }
.status-2 { color: #4caf50; }
.order-body p { font-size: 13px; color: var(--text-light); padding: 2px 0; }
.fee { color: #e53935; font-weight: 600; font-size: 15px !important; }
.order-actions { margin-top: 10px; }
.empty { text-align: center; padding: 40px; color: var(--text-muted); }
@media (max-width: 768px) { .page-body { flex-direction: column; } }
</style>