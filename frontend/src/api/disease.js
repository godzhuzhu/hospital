import request from './request'

export function getDiseases(params) {
  return request.get('/diseases', { params })
}

export function getDiseaseDetail(id) {
  return request.get(`/diseases/${id}`)
}