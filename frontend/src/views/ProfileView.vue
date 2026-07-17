<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-body">
      <AppSidebar />
      <div class="main-content">
        <h2 class="page-title">个人信息</h2>
        
        <div v-if="loading" class="loading">加载中...</div>

        <template v-else>
          <!-- 查看模式 -->
          <div v-if="!editing" class="profile-card">
            <div class="avatar-section">
              <img :src="profile.avatar || 'img/default-avatar.png'" class="avatar" alt="头像" />
              <label class="avatar-upload-btn">
                更换头像
                <input type="file" accept="image/*" @change="handleAvatarChange" hidden />
              </label>
            </div>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">用户名</span>
                <span class="value">{{ profile.username }}</span>
              </div>
              <div class="info-item">
                <span class="label">手机号</span>
                <span class="value">{{ profile.phone }}</span>
              </div>
              <div class="info-item">
                <span class="label">邮箱</span>
                <span class="value">{{ profile.email || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="label">真实姓名</span>
                <span class="value">{{ profile.realName || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="label">性别</span>
                <span class="value">{{ genderText }}</span>
              </div>
              <div class="info-item">
                <span class="label">生日</span>
                <span class="value">{{ profile.birthday || '未设置' }}</span>
              </div>
            </div>
            <div class="btn-group">
              <button class="btn-primary" @click="startEdit">编辑资料</button>
              <router-link to="/change-password" class="btn-link">修改密码</router-link>
            </div>
          </div>

          <!-- 编辑模式 -->
          <div v-else class="profile-card">
            <div class="avatar-section">
              <img :src="profile.avatar || 'img/default-avatar.png'" class="avatar" alt="头像" />
              <label class="avatar-upload-btn">
                更换头像
                <input type="file" accept="image/*" @change="handleAvatarChange" hidden />
              </label>
            </div>
            <div class="form-grid">
              <div class="form-item">
                <label>邮箱</label>
                <input v-model="form.email" placeholder="请输入邮箱" />
              </div>
              <div class="form-item">
                <label>真实姓名</label>
                <input v-model="form.realName" placeholder="请输入真实姓名" />
              </div>
              <div class="form-item">
                <label>性别</label>
                <select v-model="form.gender">
                  <option value="">请选择</option>
                  <option :value="1">男</option>
                  <option :value="2">女</option>
                </select>
              </div>
              <div class="form-item">
                <label>生日</label>
                <input type="date" v-model="form.birthday" />
              </div>
            </div>
            <div class="btn-group">
              <button class="btn-primary" @click="saveProfile" :disabled="saving">
                {{ saving ? '保存中...' : '保存' }}
              </button>
              <button class="btn-cancel" @click="cancelEdit">取消</button>
            </div>
          </div>
        </template>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import AppSidebar from '@/components/AppSidebar.vue'
import AppFooter from '@/components/AppFooter.vue'
import { getProfileApi, updateProfileApi, uploadAvatarApi } from '@/api/user'

const loading = ref(true)
const editing = ref(false)
const saving = ref(false)
const profile = reactive({
  username: '', phone: '', email: '', realName: '', gender: null, birthday: '', avatar: ''
})
const form = reactive({
  email: '', realName: '', gender: null, birthday: ''
})

const genderText = computed(() => {
  if (profile.gender === 1) return '男'
  if (profile.gender === 2) return '女'
  return '未设置'
})

onMounted(async () => {
  try {
    const res = await getProfileApi()
    Object.assign(profile, res.data)
  } catch (_) { /* 静默处理 */ }
  finally { loading.value = false }
})

function startEdit() {
  form.email = profile.email || ''
  form.realName = profile.realName || ''
  form.gender = profile.gender
  form.birthday = profile.birthday || ''
  editing.value = true
}

function cancelEdit() {
  editing.value = false
}

async function saveProfile() {
  saving.value = true
  try {
    const data = {
      email: form.email, realName: form.realName,
      gender: form.gender != null ? String(form.gender) : null,
      birthday: form.birthday || null
    }
    await updateProfileApi(data)
    profile.email = form.email
    profile.realName = form.realName
    profile.gender = form.gender
    profile.birthday = form.birthday
    editing.value = false
    alert('保存成功')
  } catch (_) { alert('保存失败') }
  finally { saving.value = false }
}

async function handleAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await uploadAvatarApi(file)
    profile.avatar = res.data.avatar
    alert('头像上传成功')
  } catch (_) { alert('上传失败') }
}
</script>

<style scoped>
.page-wrapper { min-height: 100vh; display: flex; flex-direction: column; }
.page-body { max-width: 1200px; margin: 20px auto; padding: 0 20px; display: flex; gap: 20px; flex: 1; width: 100%; }
.main-content { flex: 1; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); padding: 30px; }
.page-title { font-size: 20px; margin-bottom: 24px; padding-bottom: 12px; border-bottom: 2px solid #1e88e5; }
.loading { text-align: center; padding: 40px; color: #999; }

.profile-card { max-width: 600px; margin: 0 auto; }
.avatar-section { text-align: center; margin-bottom: 30px; }
.avatar { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 3px solid #1e88e5; }
.avatar-upload-btn { display: inline-block; margin-top: 10px; color: #1e88e5; cursor: pointer; font-size: 13px; }
.avatar-upload-btn:hover { text-decoration: underline; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 30px; }
.info-item { padding: 12px; background: #f5f7fa; border-radius: 6px; }
.info-item .label { display: block; font-size: 12px; color: #999; margin-bottom: 4px; }
.info-item .value { font-size: 15px; color: #333; }

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 30px; }
.form-item { display: flex; flex-direction: column; gap: 6px; }
.form-item label { font-size: 13px; color: #666; }
.form-item input, .form-item select { padding: 8px 12px; border: 1px solid #e8e8e8; border-radius: 4px; font-size: 14px; }
.form-item input:focus, .form-item select:focus { border-color: #1e88e5; }

.btn-group { display: flex; gap: 12px; justify-content: center; }
.btn-primary { padding: 10px 32px; background: #1e88e5; color: #fff; border-radius: 4px; font-size: 14px; border: none; cursor: pointer; }
.btn-primary:hover { background: #1565c0; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel { padding: 10px 32px; background: #f5f5f5; color: #666; border-radius: 4px; font-size: 14px; border: 1px solid #e8e8e8; cursor: pointer; }
.btn-link { color: #1e88e5; text-decoration: none; padding: 10px 32px; border: 1px solid #1e88e5; border-radius: 4px; font-size: 14px; }
.btn-link:hover { background: #e3f2fd; }
</style>
