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

export function getVitalSignList(params) {
  return request.get('/admin/vital-sign/list', { params })
}

export function getVitalSignDetail(id) {
  return request.get(`/admin/vital-sign/${id}`)
}

export function createVitalSign(userId, data) {
  return request.post('/admin/vital-sign?userId=' + userId, data)
}

export function updateVitalSign(id, data) {
  return request.put(`/admin/vital-sign/${id}`, data)
}

export function deleteVitalSign(id) {
  return request.delete(`/admin/vital-sign/${id}`)
}

export function getDietRecordList(params) {
  return request.get('/admin/diet-record/list', { params })
}

export function getDietRecordDetail(id) {
  return request.get(`/admin/diet-record/${id}`)
}

export function createDietRecord(userId, data) {
  return request.post('/admin/diet-record?userId=' + userId, data)
}

export function updateDietRecord(id, data) {
  return request.put(`/admin/diet-record/${id}`, data)
}

export function deleteDietRecord(id) {
  return request.delete(`/admin/diet-record/${id}`)
}

export function getMoodRecordList(params) {
  return request.get('/admin/mood-record/list', { params })
}

export function getMoodRecordDetail(id) {
  return request.get(`/admin/mood-record/${id}`)
}

export function createMoodRecord(userId, data) {
  return request.post('/admin/mood-record?userId=' + userId, data)
}

export function updateMoodRecord(id, data) {
  return request.put(`/admin/mood-record/${id}`, data)
}

export function deleteMoodRecord(id) {
  return request.delete(`/admin/mood-record/${id}`)
}
