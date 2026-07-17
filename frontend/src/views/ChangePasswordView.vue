<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-body">
      <AppSidebar />
      <div class="main-content">
        <h2 class="page-title">修改密码</h2>
        <form @submit.prevent="handleSubmit" class="password-form">
          <div class="form-group">
            <label>原密码</label>
            <input v-model="form.oldPassword" type="password" placeholder="请输入原密码" />
          </div>
          <div class="form-group">
            <label>新密码</label>
            <input v-model="form.newPassword" type="password" placeholder="6-20位新密码" />
          </div>
          <div class="form-group">
            <label>确认新密码</label>
            <input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" />
          </div>
          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? '提交中...' : '确认修改' }}
          </button>
        </form>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { changePasswordApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import AppHeader from '@/components/AppHeader.vue'
import AppSidebar from '@/components/AppSidebar.vue'
import AppFooter from '@/components/AppFooter.vue'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const loading = ref(false)

async function handleSubmit() {
  if (form.newPassword.length < 6) return alert('新密码至少6位')
  if (form.newPassword !== form.confirmPassword) return alert('两次新密码不一致')

  loading.value = true
  try {
    await changePasswordApi({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword,
      confirmPassword: form.confirmPassword,
    })
    alert('密码修改成功，请重新登录')
    auth.logout()
    router.push('/login?redirect=/')
  } catch (_) {
    // 拦截器已提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-wrapper { min-height: 100vh; display: flex; flex-direction: column; }
.page-body { max-width: 1200px; margin: 20px auto; padding: 0 20px; display: flex; gap: 20px; flex: 1; width: 100%; }
.main-content { flex: 1; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); padding: 30px; }
.page-title { font-size: 20px; margin-bottom: 24px; padding-bottom: 12px; border-bottom: 2px solid #1e88e5; }

.password-form { max-width: 420px; margin: 0 auto; padding-top: 10px; }
.form-group { margin-bottom: 18px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 13px; color: #666; }
.form-group input { width: 100%; padding: 10px 12px; font-size: 14px; border: 1px solid #e8e8e8; border-radius: 4px; transition: border-color 0.2s; }
.form-group input:focus { border-color: #1e88e5; }

.btn-submit { width: 100%; padding: 12px; background: #1e88e5; color: #fff; border: none; border-radius: 4px; font-size: 15px; cursor: pointer; transition: background 0.2s; }
.btn-submit:hover { background: #1565c0; }
.btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
