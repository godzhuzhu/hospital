import request from './request'

export function getHomeIndex() {
  return request.get('/home/index')
}