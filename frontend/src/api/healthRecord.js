import request from './request'

export function addHealthRecord(data) {
  return request.post('/user/health-record', data)
}

export function updateHealthRecord(id, data) {
  return request.put(`/user/health-record/${id}`, data)
}

export function deleteHealthRecord(id) {
  return request.delete(`/user/health-record/${id}`)
}

export function getTodayRecords() {
  return request.get('/user/health-record/today')
}

export function getHealthRecordList(params) {
  return request.get('/user/health-record/list', { params })
}

export function getHealthRecordDetail(id) {
  return request.get(`/user/health-record/${id}`)
}
