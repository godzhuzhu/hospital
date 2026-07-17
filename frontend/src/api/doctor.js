import request from './request'

export function getDoctors(params) {
  return request.get('/doctors', { params })
}

export function getDoctorDetail(id) {
  return request.get(`/doctors/${id}`)
}

export function getDoctorSchedules(id, params) {
  return request.get(`/doctors/${id}/schedules`, { params })
}

export function getDoctorReviews(id, params) {
  return request.get(`/doctors/${id}/reviews`, { params })
}