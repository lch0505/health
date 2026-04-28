<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <el-icon size="32" color="#409eff"><HealthFilled /></el-icon>
        <div class="title-text">健康习惯打卡系统</div>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="tips">
          <span>没有账号？</span>
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
        <div class="demo-tips">
          <el-text type="info">演示账号：admin / admin123（管理员）</el-text>
          <br />
          <el-text type="info">演示账号：user / user123（普通用户）</el-text>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  const success = await userStore.handleLogin(loginForm)
  loading.value = false

  if (success) {
    router.push('/dashboard')
  }
}
</script>

<style scoped lang="scss">
.login-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  
  .title-text {
    margin-top: 10px;
    font-size: 24px;
    color: #333;
    font-weight: bold;
  }
}

.tips {
  text-align: center;
  font-size: 14px;
  color: #909399;
  
  .link {
    color: #409eff;
    text-decoration: none;
  }
}

.demo-tips {
  margin-top: 20px;
  text-align: center;
  font-size: 12px;
  line-height: 1.8;
}
</style>
