import request from './request'

export function globalSearch(params) {
  return request.get('/search/global', { params })
}