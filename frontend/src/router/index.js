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
        path: 'monthly-summary',
        name: 'MonthlySummary',
        component: () => import('@/views/MonthlySummary.vue'),
        meta: { title: '月度总结' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }
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
