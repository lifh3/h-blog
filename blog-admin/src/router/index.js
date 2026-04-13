import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'articles',
        name: 'ArticleList',
        component: () => import('@/views/ArticleList.vue')
      },
      {
        path: 'articles/new',
        name: 'ArticleCreate',
        component: () => import('@/views/ArticleEdit.vue')
      },
      {
        path: 'articles/:id/edit',
        name: 'ArticleEdit',
        component: () => import('@/views/ArticleEdit.vue')
      },
      {
        path: 'categories',
        name: 'CategoryList',
        component: () => import('@/views/CategoryList.vue')
      },
      {
        path: 'tags',
        name: 'TagList',
        component: () => import('@/views/TagList.vue')
      },
      {
        path: 'comments',
        name: 'CommentList',
        component: () => import('@/views/CommentList.vue')
      },
      {
        path: 'collections',
        name: 'CollectionList',
        component: () => import('@/views/CollectionList.vue')
      },
      {
        path: 'collections/:id/edit',
        name: 'CollectionEdit',
        component: () => import('@/views/CollectionEdit.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      },
      {
        path: 'ai-publish',
        name: 'AIPublish',
        component: () => import('@/views/AIPublish.vue')
      },
      {
        path: 'scheduler',
        name: 'SchedulerTask',
        component: () => import('@/views/SchedulerTask.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && authStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
