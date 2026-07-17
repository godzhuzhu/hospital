import request from './request'

export function getHospitals(params) {
  return request.get('/hospitals', { params })
}

export function getHospitalDetail(id) {
  return request.get(`/hospitals/${id}`)
}

export function getHospitalDepartments(id) {
  return request.get(`/hospitals/${id}/departments`)
}

export function getHospitalDoctors(id, params) {
  return request.get(`/hospitals/${id}/doctors`, { params })
}