import request from './request'

export const getMyReviewsApi = (params) => request.get('/reviews/my', { params })
export const addReviewApi = (data) => request.post('/reviews', data)
