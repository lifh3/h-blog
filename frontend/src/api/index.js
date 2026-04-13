import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('admin_token')
      window.location.href = '/admin/login'
    }
    return Promise.reject(err)
  }
)

// Public APIs
export const publicApi = {
  getArticles: (params) => api.get('/articles', { params }),
  getArticle: (id) => api.get(`/articles/${id}`),
  getRelatedArticles: (id) => api.get(`/articles/${id}/related`),
  getCategories: () => api.get('/categories'),
  getCategory: (id) => api.get(`/categories/${id}`),
  getCategoryArticles: (id, params) => api.get(`/categories/${id}/articles`, { params }),
  getTags: () => api.get('/tags'),
  getTagArticles: (name, params) => api.get(`/tags/${name}/articles`, { params }),
  getHotArticles: () => api.get('/articles/hot'),
  getStats: () => api.get('/stats'),
  getAbout: () => api.get('/about'),
  getComments: (articleId) => api.get(`/articles/${articleId}/comments`),
  getCollections: () => api.get('/collections'),
  getCollection: (id) => api.get(`/collections/${id}`),
  addComment: (articleId, data) => api.post(`/articles/${articleId}/comments`, data),
}

// Admin APIs
export const adminApi = {
  login: (data) => api.post('/admin/login', data),
  getDashboard: () => api.get('/admin/dashboard'),
  getAdminArticles: (params) => api.get('/admin/articles', { params }),
  getAdminArticle: (id) => api.get(`/admin/articles/${id}`),
  createArticle: (data) => api.post('/admin/articles', data),
  updateArticle: (id, data) => api.put(`/admin/articles/${id}`, data),
  deleteArticle: (id) => api.delete(`/admin/articles/${id}`),
  getAdminCategories: () => api.get('/admin/categories'),
  createCategory: (data) => api.post('/admin/categories', data),
  updateCategory: (id, data) => api.put(`/admin/categories/${id}`, data),
  deleteCategory: (id) => api.delete(`/admin/categories/${id}`),
  getAdminTags: () => api.get('/admin/tags'),
  createTag: (data) => api.post('/admin/tags', data),
  updateTag: (id, data) => api.put(`/admin/tags/${id}`, data),
  deleteTag: (id) => api.delete(`/admin/tags/${id}`),
  getAdminComments: (params) => api.get('/admin/comments', { params }),
  updateComment: (id, data) => api.patch(`/admin/comments/${id}`, data),
  deleteComment: (id) => api.delete(`/admin/comments/${id}`),
  // 合集管理
  getAdminCollections: () => api.get('/collections'),
  getAdminCollection: (id) => api.get(`/collections/${id}`),
  createCollection: (data) => api.post('/collections', data),
  updateCollection: (id, data) => api.put(`/collections/${id}`, data),
  deleteCollection: (id) => api.delete(`/collections/${id}`),
}

export default api
