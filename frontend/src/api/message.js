import request from './request'

export const getMessagesApi = (params) => request.get('/messages', { params })
export const markReadApi = (id) => request.post('/messages/' + id + '/read')
