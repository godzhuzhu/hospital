import request from './request'

// 成员3接口 - 后续对接
export const getMyAppointmentsApi = (params) => request.get('/appointments/my', { params })
export const cancelAppointmentApi = (orderNo) => request.post(/appointments//cancel)
