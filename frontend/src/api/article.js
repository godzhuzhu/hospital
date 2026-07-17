import request from './request'

export function getArticles(params) {
  return request.get('/articles', { params })
}

export function getArticleDetail(id) {
  return request.get(`/articles/${id}`)
}