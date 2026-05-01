import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true, requiresUser: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'check-in',
        name: 'CheckIn',
        component: () => import('@/views/CheckIn.vue'),
        meta: { title: '打卡' }
      },
      {
        path: 'health-record',
        name: 'HealthRecord',
        component: () => import('@/views/HealthRecord.vue'),
        meta: { title: '健康记录' }
      },
      {
        path: 'vital-sign',
        name: 'VitalSign',
        component: () => import('@/views/VitalSign.vue'),
        meta: { title: '体征数据' }
      },
      {
        path: 'diet-record',
        name: 'DietRecord',
        component: () => import('@/views/DietRecord.vue'),
        meta: { title: '饮食记录' }
      },
      {
        path: 'mood-record',
        name: 'MoodRecord',
        component: () => import('@/views/MoodRecord.vue'),
        meta: { title: '情绪管理' }
      },
      {
        path: 'monthly-summary',
        name: 'MonthlySummary',
        component: () => import('@/views/MonthlySummary.vue'),
        meta: { title: '月度总结' }
      },
      {
        path: 'points',
        name: 'PointsCenter',
        component: () => import('@/views/PointsCenter.vue'),
        meta: { title: '积分中心' }
      },
      {
        path: 'achievement',
        name: 'AchievementWall',
        component: () => import('@/views/AchievementWall.vue'),
        meta: { title: '勋章墙' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'announcement',
        name: 'Announcement',
        component: () => import('@/views/Announcement.vue'),
        meta: { title: '公告通知' }
      },
      {
        path: 'message-board',
        name: 'MessageBoard',
        component: () => import('@/views/MessageBoard.vue'),
        meta: { title: '留言板' }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/users',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'check-in',
        name: 'CheckInManagement',
        component: () => import('@/views/admin/CheckInManagement.vue'),
        meta: { title: '打卡记录管理' }
      },
      {
        path: 'health-record',
        name: 'HealthRecordManagement',
        component: () => import('@/views/admin/HealthRecordManagement.vue'),
        meta: { title: '健康记录管理' }
      },
      {
        path: 'vital-sign',
        name: 'VitalSignManagement',
        component: () => import('@/views/admin/VitalSignManagement.vue'),
        meta: { title: '体征数据管理' }
      },
      {
        path: 'diet-record',
        name: 'DietRecordManagement',
        component: () => import('@/views/admin/DietRecordManagement.vue'),
        meta: { title: '饮食记录管理' }
      },
      {
        path: 'mood-record',
        name: 'MoodRecordManagement',
        component: () => import('@/views/admin/MoodRecordManagement.vue'),
        meta: { title: '情绪记录管理' }
      },
      {
        path: 'points',
        name: 'PointsManagement',
        component: () => import('@/views/admin/PointsManagement.vue'),
        meta: { title: '积分管理' }
      },
      {
        path: 'achievement',
        name: 'AchievementManagement',
        component: () => import('@/views/admin/AchievementManagement.vue'),
        meta: { title: '勋章管理' }
      },
      {
        path: 'points-config',
        name: 'PointsConfigManagement',
        component: () => import('@/views/admin/PointsConfigManagement.vue'),
        meta: { title: '积分规则配置' }
      },
      {
        path: 'announcement',
        name: 'AnnouncementManagement',
        component: () => import('@/views/admin/AnnouncementManagement.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'message-board',
        name: 'MessageBoardManagement',
        component: () => import('@/views/admin/MessageBoardManagement.vue'),
        meta: { title: '留言板管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 健康习惯打卡系统` : '健康习惯打卡系统'

  const userStore = useUserStore()
  const isAuthenticated = userStore.isAuthenticated
  const isAdmin = userStore.isAdmin

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !isAdmin) {
    if (isAuthenticated) {
      next('/dashboard')
    } else {
      next('/login')
    }
  } else if (to.meta.requiresUser && isAdmin) {
    next('/admin/users')
  } else if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
    if (isAdmin) {
      next('/admin/users')
    } else {
      next('/dashboard')
    }
  } else if (to.path === '/') {
    if (isAuthenticated) {
      if (isAdmin) {
        next('/admin/users')
      } else {
        next('/dashboard')
      }
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router
