import request from './request'

export const getFamilyMembersApi = () => request.get('/family-members')
export const addFamilyMemberApi = (data) => request.post('/family-members', data)
export const updateFamilyMemberApi = (id, data) => request.put('/family-members/' + id, data)
export const deleteFamilyMemberApi = (id) => request.delete('/family-members/' + id)
