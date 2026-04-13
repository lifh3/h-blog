import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/HomeView.vue') },
      { path: 'article/:id', name: 'Article', component: () => import('@/views/ArticleView.vue') },
      { path: 'category/:id', name: 'Category', component: () => import('@/views/CategoryView.vue') },
      { path: 'tag/:name', name: 'Tag', component: () => import('@/views/TagView.vue') },
      { path: 'collections', name: 'Collections', component: () => import('@/views/CollectionsView.vue') },
      { path: 'collections/:id', name: 'Collection', component: () => import('@/views/CollectionView.vue') },
      { path: 'archives', name: 'Archives', component: () => import('@/views/ArchivesView.vue') },
      { path: 'links', name: 'Links', component: () => import('@/views/LinksView.vue') },
      { path: 'about', name: 'About', component: () => import('@/views/AboutView.vue') },
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/LoginView.vue')
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('@/views/admin/DashboardView.vue') },
      { path: 'articles', name: 'AdminArticles', component: () => import('@/views/admin/ArticleListView.vue') },
      { path: 'articles/new', name: 'AdminArticleNew', component: () => import('@/views/admin/ArticleEditView.vue') },
      { path: 'articles/:id/edit', name: 'AdminArticleEdit', component: () => import('@/views/admin/ArticleEditView.vue') },
      { path: 'categories', name: 'AdminCategories', component: () => import('@/views/admin/CategoryView.vue') },
      { path: 'tags', name: 'AdminTags', component: () => import('@/views/admin/TagView.vue') },
      { path: 'comments', name: 'AdminComments', component: () => import('@/views/admin/CommentView.vue') },
      { path: 'collections', name: 'AdminCollections', component: () => import('@/views/admin/CollectionView.vue') },
      { path: 'collections/:id/edit', name: 'AdminCollectionEdit', component: () => import('@/views/admin/CollectionEditView.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/admin/login')
  } else if (to.path === '/admin/login' && authStore.isAuthenticated) {
    next('/admin')
  } else {
    next()
  }
})

export default router
