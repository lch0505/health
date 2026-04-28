<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>个人信息</span>
        </div>
      </template>
      <el-form :model="userForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="userForm.role === 'admin' ? 'danger' : 'primary'">
            {{ userForm.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="不修改请留空"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="userForm.confirmPassword"
            type="password"
            placeholder="不修改请留空"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleUpdate">
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUserInfo } from '@/api/auth'

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const userForm = reactive({
  username: '',
  role: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (userForm.password && value !== userForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  nickname: [{ max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }],
  password: [{ min: 6, max: 20, message: '密码长度需在6-20个字符之间', trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 200) {
      userForm.username = res.data.username
      userForm.role = res.data.role
      userForm.nickname = res.data.nickname || ''
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

const handleUpdate = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const data = {
      nickname: userForm.nickname
    }
    if (userForm.password) {
      data.password = userForm.password
    }

    const res = await updateUserInfo(data)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      userStore.fetchUserInfo()
      userForm.password = ''
      userForm.confirmPassword = ''
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (error) {
    ElMessage.error('修改失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (userStore.userInfo) {
    userForm.username = userStore.userInfo.username
    userForm.role = userStore.userInfo.role
    userForm.nickname = userStore.userInfo.nickname || ''
  } else {
    fetchUserInfo()
  }
})
</script>
