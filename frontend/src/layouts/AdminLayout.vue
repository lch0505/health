<template>
  <el-container>
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon><HealthFilled /></el-icon>
        <span class="logo-text">管理后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/check-in">
          <el-icon><Calendar /></el-icon>
          <span>打卡记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/health-record">
          <el-icon><Document /></el-icon>
          <span>健康记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/vital-sign">
          <el-icon><TrendCharts /></el-icon>
          <span>体征数据</span>
        </el-menu-item>
        <el-menu-item index="/admin/diet-record">
          <el-icon><Bowl /></el-icon>
          <span>饮食记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/mood-record">
          <el-icon><ChatDotRound /></el-icon>
          <span>情绪记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/points">
          <el-icon><Coin /></el-icon>
          <span>积分管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/points-config">
          <el-icon><Setting /></el-icon>
          <span>积分规则配置</span>
        </el-menu-item>
        <el-menu-item index="/admin/achievement">
          <el-icon><Trophy /></el-icon>
          <span>勋章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/announcement">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="welcome">管理员：{{ nickname }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const nickname = computed(() => userStore.nickname)

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.aside {
  background-color: #304156;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #2b3a4a;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    
    .el-icon {
      margin-right: 10px;
      font-size: 24px;
      color: #409eff;
    }
    
    .logo-text {
      color: #fff;
    }
  }
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  margin-left: 220px;
  position: fixed;
  right: 0;
  left: 0;
  z-index: 100;
  
  .welcome {
    font-size: 14px;
    color: #606266;
  }
  
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    .username {
      margin-left: 10px;
      color: #606266;
    }
  }
}

.main {
  margin-left: 220px;
  margin-top: 60px;
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}
</style>
