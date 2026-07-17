<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-body">
      <AppSidebar />
      <div class="main-content">
        <div class="header-row">
          <h2 class="page-title">就诊人管理</h2>
          <button class="btn-primary" @click="openAdd">新增就诊人</button>
        </div>

        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="list.length === 0" class="empty">暂无就诊人，点击上方按钮添加</div>
        <div v-else class="member-grid">
          <div v-for="item in list" :key="item.id" class="member-card">
            <div class="card-info">
              <h4>{{ item.name }}</h4>
              <p><span class="tag">{{ relationMap[item.relation] || item.relation }}</span></p>
              <p class="phone">{{ item.phone }}</p>
              <p class="id-card">{{ maskIdCard(item.idCard) }}</p>
            </div>
            <div class="card-actions">
              <button class="btn-edit" @click="openEdit(item)">编辑</button>
              <button class="btn-delete" @click="handleDelete(item.id)">删除</button>
            </div>
          </div>
        </div>

        <!-- 弹窗 -->
        <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
          <div class="modal">
            <h3>{{ isEdit ? '编辑就诊人' : '新增就诊人' }}</h3>
            <div class="form-grid">
              <div class="form-item">
                <label>姓名 *</label>
                <input v-model="form.name" placeholder="请输入姓名" />
              </div>
              <div class="form-item">
                <label>关系 *</label>
                <select v-model="form.relation">
                  <option value="">请选择</option>
                  <option value="本人">本人</option>
                  <option value="配偶">配偶</option>
                  <option value="父母">父母</option>
                  <option value="子女">子女</option>
                  <option value="兄弟姐妹">兄弟姐妹</option>
                  <option value="其他">其他</option>
                </select>
              </div>
              <div class="form-item">
                <label>手机号 *</label>
                <input v-model="form.phone" placeholder="请输入手机号" />
              </div>
              <div class="form-item">
                <label>身份证号</label>
                <input v-model="form.idCard" placeholder="请输入身份证号" />
              </div>
              <div class="form-item">
                <label>性别</label>
                <select v-model.number="form.gender">
                  <option :value="null">请选择</option>
                  <option :value="1">男</option>
                  <option :value="2">女</option>
                </select>
              </div>
              <div class="form-item">
                <label>年龄</label>
                <input v-model.number="form.age" type="number" placeholder="请输入年龄" />
              </div>
            </div>
            <div class="modal-btns">
              <button class="btn-primary" @click="handleSave" :disabled="saving">
                {{ saving ? '保存中...' : '保存' }}
              </button>
              <button class="btn-cancel" @click="closeModal">取消</button>
            </div>
          </div>
        </div>

        <!-- 删除确认 -->
        <div v-if="showDelete" class="modal-overlay" @click.self="showDelete = false">
          <div class="modal mini">
            <h3>确认删除</h3>
            <p>确定要删除该就诊人吗？</p>
            <div class="modal-btns">
              <button class="btn-delete" @click="confirmDelete">确认删除</button>
              <button class="btn-cancel" @click="showDelete = false">取消</button>
            </div>
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
import { getFamilyMembersApi, addFamilyMemberApi, updateFamilyMemberApi, deleteFamilyMemberApi } from '@/api/familyMember'

const loading = ref(true)
const list = ref([])
const showModal = ref(false)
const showDelete = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const deleteId = ref(null)

const form = reactive({ name: '', relation: '', phone: '', idCard: '', gender: null, age: null })
const editId = ref(null)

const relationMap = { '本人': '本人', '配偶': '配偶', '父母': '父母', '子女': '子女', '兄弟姐妹': '兄弟姐妹', '其他': '其他' }

onMounted(async () => {
  await fetchList()
  loading.value = false
})

async function fetchList() {
  try {
    const res = await getFamilyMembersApi()
    list.value = res.data || []
  } catch (_) { list.value = [] }
}

function maskIdCard(idCard) {
  if (!idCard) return ''
  return idCard.replace(/(\d{6})\d{8,}(\d{4})/, '****')
}

function openAdd() {
  resetForm()
  isEdit.value = false
  showModal.value = true
}

function openEdit(item) {
  isEdit.value = true
  editId.value = item.id
  form.name = item.name || ''
  form.relation = item.relation || ''
  form.phone = item.phone || ''
  form.idCard = item.idCard || ''
  form.gender = item.gender
  form.age = item.age
  showModal.value = true
}

function resetForm() {
  form.name = ''; form.relation = ''; form.phone = ''
  form.idCard = ''; form.gender = null; form.age = null
  editId.value = null
}

function closeModal() {
  showModal.value = false
  resetForm()
}

async function handleSave() {
  if (!form.name || !form.relation || !form.phone) {
    alert('请填写必填项'); return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateFamilyMemberApi(editId.value, { ...form })
    } else {
      await addFamilyMemberApi({ ...form })
    }
    closeModal()
    await fetchList()
    alert('保存成功')
  } catch (_) { alert('保存失败') }
  finally { saving.value = false }
}

function handleDelete(id) {
  deleteId.value = id
  showDelete.value = true
}

async function confirmDelete() {
  try {
    await deleteFamilyMemberApi(deleteId.value)
    showDelete.value = false
    await fetchList()
    alert('删除成功')
  } catch (_) { alert('删除失败') }
}
</script>

<style scoped>
.page-wrapper { min-height: 100vh; display: flex; flex-direction: column; }
.page-body { max-width: 1200px; margin: 20px auto; padding: 0 20px; display: flex; gap: 20px; flex: 1; width: 100%; }
.main-content { flex: 1; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); padding: 30px; }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 20px; padding-bottom: 12px; border-bottom: 2px solid #1e88e5; flex: 1; margin: 0; }
.loading, .empty { text-align: center; padding: 60px; color: #999; font-size: 15px; }

.member-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 16px; }
.member-card { background: #f5f7fa; border-radius: 8px; padding: 20px; display: flex; justify-content: space-between; align-items: center; }
.card-info h4 { font-size: 16px; margin-bottom: 6px; }
.card-info .tag { display: inline-block; background: #e3f2fd; color: #1e88e5; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.card-info .phone { color: #666; font-size: 13px; margin-top: 4px; }
.card-info .id-card { color: #999; font-size: 12px; }
.card-actions { display: flex; flex-direction: column; gap: 8px; }
.btn-edit, .btn-delete { padding: 6px 16px; border-radius: 4px; font-size: 13px; border: none; cursor: pointer; }
.btn-edit { background: #1e88e5; color: #fff; }
.btn-edit:hover { background: #1565c0; }
.btn-delete { background: #ff5252; color: #fff; }
.btn-delete:hover { background: #d32f2f; }

.btn-primary { padding: 8px 20px; background: #1e88e5; color: #fff; border-radius: 4px; font-size: 14px; border: none; cursor: pointer; }
.btn-primary:hover { background: #1565c0; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel { padding: 8px 20px; background: #f5f5f5; color: #666; border-radius: 4px; font-size: 14px; border: 1px solid #e8e8e8; cursor: pointer; }

.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: #fff; border-radius: 8px; padding: 30px; width: 480px; max-height: 80vh; overflow-y: auto; }
.modal.mini { width: 380px; }
.modal h3 { font-size: 18px; margin-bottom: 20px; }
.modal p { color: #666; margin-bottom: 20px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 24px; }
.form-item { display: flex; flex-direction: column; gap: 4px; }
.form-item label { font-size: 13px; color: #666; }
.form-item input, .form-item select { padding: 8px 12px; border: 1px solid #e8e8e8; border-radius: 4px; font-size: 14px; }
.form-item input:focus, .form-item select:focus { border-color: #1e88e5; }
.modal-btns { display: flex; gap: 12px; justify-content: flex-end; }
</style>
