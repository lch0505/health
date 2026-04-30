import request from './request'

export function getUserList(params) {
  return request.get('/admin/users', { params })
}

export function getUserDetail(id) {
  return request.get(`/admin/users/${id}`)
}

export function createUser(data) {
  return request.post('/admin/users', data)
}

export function updateUser(id, data) {
  return request.put(`/admin/users/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/admin/users/${id}`)
}

export function updateUserStatus(id, status) {
  return request.put(`/admin/users/${id}/status?status=${status}`)
}

export function getCheckInList(params) {
  return request.get('/admin/check-in/list', { params })
}

export function getCheckInDetail(id) {
  return request.get(`/admin/check-in/${id}`)
}

export function getHealthRecordList(params) {
  return request.get('/admin/health-record/list', { params })
}

export function getHealthRecordDetail(id) {
  return request.get(`/admin/health-record/${id}`)
}
