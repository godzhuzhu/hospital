import request from './request'

export const getMyFeedbacksApi = (params) => request.get('/feedbacks/my', { params })
export const submitFeedbackApi = (data) => request.post('/feedbacks', data)
