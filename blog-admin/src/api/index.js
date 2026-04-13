import axios from 'axios'
import { createPinia } from 'pinia'
import router from '@/router'

const api = axios.create({
  baseURL: '/api',
  timeout: 600000,  // 10分钟
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor - handle 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

// Auth APIs
export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  logout: () => api.post('/auth/logout'),
  register: (data) => api.post('/auth/register'),
  profile: () => api.get('/users/profile'),
  updateProfile: (data) => api.patch('/users/profile', data),
  changePassword: (data) => api.post('/users/change-password', data)
}

// Article APIs (前台公开接口)
export const articlesAPI = {
  list: (params) => api.get('/articles', { params }),
  get: (id) => api.get(`/articles/${id}`),
  create: (data) => api.post('/articles', data),
  update: (id, data) => api.put(`/articles/${id}`, data),
  delete: (id) => api.delete(`/articles/${id}`),
  publish: (id) => api.post(`/articles/${id}/publish`),
  unpublish: (id) => api.post(`/articles/${id}/unpublish`)
}

// Admin Article APIs (管理端，含草稿)
export const adminArticlesAPI = {
  list: (params) => api.get('/admin/articles', { params }),
  get: (id) => api.get(`/admin/articles/${id}`),
  update: (id, data) => api.put(`/admin/articles/${id}`, data),
  publish: (id) => api.post(`/admin/articles/${id}/publish`),
  unpublish: (id) => api.post(`/admin/articles/${id}/unpublish`),
  delete: (id) => api.delete(`/admin/articles/${id}`)
}

// Category APIs
export const categoriesAPI = {
  list: (params) => api.get('/categories', { params }),
  get: (id) => api.get(`/categories/${id}`),
  create: (data) => api.post('/categories', data),
  update: (id, data) => api.put(`/categories/${id}`, data),
  delete: (id) => api.delete(`/categories/${id}`)
}

// Tag APIs
export const tagsAPI = {
  list: (params) => api.get('/tags', { params }),
  get: (id) => api.get(`/tags/${id}`),
  create: (data) => api.post('/tags', data),
  update: (id, data) => api.put(`/tags/${id}`, data),
  delete: (id) => api.delete(`/tags/${id}`)
}

// Comment APIs
export const commentsAPI = {
  list: (params) => api.get('/comments', { params }),
  search: (params) => api.get('/comments/search', { params }),
  getByArticle: (articleId) => api.get(`/comments/article/${articleId}`),
  approve: (id) => api.post(`/comments/${id}/approve`),
  reject: (id) => api.post(`/comments/${id}/reject`),
  delete: (id) => api.delete(`/comments/${id}`)
}

// Collection APIs
export const collectionsAPI = {
  list: (params) => api.get('/collections', { params }),
  get: (id) => api.get(`/collections/${id}`),
  create: (data) => api.post('/collections', data),
  update: (id, data) => api.put(`/collections/${id}`, data),
  delete: (id) => api.delete(`/collections/${id}`),
  addArticles: (id, articleIds) => api.post(`/collections/${id}/articles`, { articleIds }),
  removeArticle: (id, articleId) => api.delete(`/collections/${id}/articles/${articleId}`)
}

// Stats API
export const statsAPI = {
  get: () => api.get('/stats')
}

// Users API
export const usersAPI = {
  profile: () => api.get('/users/profile'),
  updateProfile: (data) => api.patch('/users/profile', data),
  changePassword: (data) => api.post('/users/change-password', data)
}

// Public APIs (no auth required)
export const publicApi = {
  stats: () => api.get('/stats'),
  articles: (params) => api.get('/articles', { params }),
  categories: (params) => api.get('/categories', { params }),
  tags: (params) => api.get('/tags', { params }),
  commentsByArticle: (articleId, params) => api.get(`/articles/${articleId}/comments`, { params }),
  collections: (params) => api.get('/collections', { params })
}

export default api
