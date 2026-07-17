import request from './request'

export function getDepartmentTree() {
  return request.get('/departments/tree')
}

export function getPrimaryDepartments() {
  return request.get('/departments/primary')
}

export function getDepartmentChildren(parentId) {
  return request.get(`/departments/${parentId}/children`)
}