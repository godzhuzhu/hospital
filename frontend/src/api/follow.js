import request from './request'

export const getMyFollowsApi = (params) => request.get('/follow/my', { params })
export const followApi = (type, id) => request.post(/follow//)
export const unfollowApi = (type, id) => request.delete(/follow//)
export const checkFollowApi = (params) => request.get('/follow/check', { params })
