import request from './request'

// 成员3接口 - 后续对接
export const getMyConsultsApi = (params) => request.get('/consults/my', { params })
export const cancelConsultApi = (orderNo) => request.post('/consults/' + orderNo + '/cancel')
