import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/layout',
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: '/dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: '/order',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: '/product',
        component: () => import('@/views/product/index.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: '/platform',
        component: () => import('@/views/platform/index.vue'),
        meta: { title: '平台配置' }
      },
      {
        path: '/inventory',
        component: () => import('@/views/inventory/index.vue'),
        meta: { title: '库存管理' }
      },
      {
        path: '/warehouse',
        component: () => import('@/views/warehouse/index.vue'),
        meta: { title: '仓库管理' }
      },
      {
        path: '/finance',
        component: () => import('@/views/finance/index.vue'),
        meta: { title: '财务管理' }
      },
      {
        path: '/user',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
