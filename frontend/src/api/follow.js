import request from './request'

export const getMyFollowsApi = (params) => request.get('/follow/my', { params })
export const followApi = (type, id) => request.post('/follow/' + type + '/' + id)
export const unfollowApi = (type, id) => request.delete('/follow/' + type + '/' + id)
export const checkFollowApi = (params) => request.get('/follow/check', { params })
