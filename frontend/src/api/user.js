import request from './request'

export const getProfileApi = () => request.get('/user/profile')
export const updateProfileApi = (data) => request.put('/user/profile', data)
export const getProfile = getProfileApi
export const updateProfile = updateProfileApi
export const uploadAvatarApi = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
