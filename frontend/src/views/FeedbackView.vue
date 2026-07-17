<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-body">
      <AppSidebar />
      <div class="main-content">
        <h2 class="page-title">意见反馈</h2>

        <div class="feedback-form">
          <div class="form-item">
            <label>反馈类型</label>
            <select v-model="form.feedbackType">
              <option value="">请选择</option>
              <option :value="1">系统问题</option>
              <option :value="2">服务问题</option>
              <option :value="3">医生问题</option>
              <option :value="4">其他问题</option>
            </select>
          </div>
          <div class="form-item">
            <label>反馈内容</label>
            <textarea v-model="form.content" placeholder="请详细描述您的问题或建议..." rows="5"></textarea>
          </div>
          <button class="btn-primary" @click="submitFeedback" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交反馈' }}
          </button>
        </div>

        <h3 class="section-title">我的反馈记录</h3>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="feedbacks.length === 0" class="empty">暂无反馈记录</div>
        <div v-else class="feedback-list">
          <div v-for="item in feedbacks" :key="item.id" class="feedback-card">
            <div class="fb-header">
              <span class="fb-type">{{ typeMap[item.feedbackType] || '其他' }}</span>
              <span class="fb-status" :class="'fb-status-' + item.status">{{ item.status === 1 ? '待处理' : '已处理' }}</span>
              <span class="fb-time">{{ item.createTime }}</span>
            </div>
            <p class="fb-content">{{ item.content }}</p>
            <p v-if="item.replyContent" class="fb-reply">回复：{{ item.replyContent }}</p>
          </div>
        </div>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import AppSidebar from '@/components/AppSidebar.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getMyFeedbacksApi, submitFeedbackApi } from '@/api/feedback'

const loading = ref(true)
const feedbacks = ref([])
const submitting = ref(false)
const form = reactive({ feedbackType: '', content: '' })

const typeMap = { 1: '系统问题', 2: '服务问题', 3: '医生问题', 4: '其他问题' }

onMounted(() => fetchFeedbacks())

async function fetchFeedbacks() {
  loading.value = true
  try {
    const res = await getMyFeedbacksApi({ page: 1, pageSize: 50 })
    if (res.data) {
      feedbacks.value = res.data.records || []
    }
  } catch (_) { feedbacks.value = [] }
  finally { loading.value = false }
}

async function submitFeedback() {
  if (!form.feedbackType || !form.content) {
    alert('请填写完整信息'); return
  }
  submitting.value = true
  try {
    await submitFeedbackApi({ feedbackType: form.feedbackType, content: form.content })
    form.feedbackType = ''
    form.content = ''
    alert('提交成功')
    fetchFeedbacks()
  } catch (_) { alert('提交失败') }
  finally { submitting.value = false }
}
</script>

<style scoped>
.page-wrapper { min-height: 100vh; display: flex; flex-direction: column; }
.page-body { max-width: 1200px; margin: 20px auto; padding: 0 20px; display: flex; gap: 20px; flex: 1; width: 100%; }
.main-content { flex: 1; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); padding: 30px; }
.page-title { font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 2px solid #1e88e5; }
.section-title { font-size: 16px; margin: 30px 0 16px; color: #333; }
.loading, .empty { text-align: center; padding: 40px; color: #999; }

.feedback-form { background: #f5f7fa; border-radius: 8px; padding: 20px; margin-bottom: 10px; }
.form-item { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.form-item label { font-size: 13px; color: #666; }
.form-item select, .form-item textarea { padding: 8px 12px; border: 1px solid #e8e8e8; border-radius: 4px; font-size: 14px; width: 100%; }
.form-item select:focus, .form-item textarea:focus { border-color: #1e88e5; }
.form-item textarea { resize: vertical; }
.btn-primary { padding: 10px 32px; background: #1e88e5; color: #fff; border-radius: 4px; font-size: 14px; border: none; cursor: pointer; }
.btn-primary:hover { background: #1565c0; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.feedback-list { display: flex; flex-direction: column; gap: 12px; }
.feedback-card { background: #f5f7fa; border-radius: 8px; padding: 16px 20px; }
.fb-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.fb-type { padding: 2px 8px; background: #e3f2fd; color: #1e88e5; border-radius: 4px; font-size: 12px; }
.fb-status { font-size: 12px; padding: 2px 8px; border-radius: 4px; }
.fb-status-1 { background: #fff3e0; color: #ff9800; }
.fb-status-2 { background: #e8f5e9; color: #4caf50; }
.fb-time { color: #999; font-size: 12px; margin-left: auto; }
.fb-content { font-size: 14px; color: #333; line-height: 1.5; }
.fb-reply { font-size: 13px; color: #1e88e5; margin-top: 8px; padding-top: 8px; border-top: 1px solid #e8e8e8; }
</style>
