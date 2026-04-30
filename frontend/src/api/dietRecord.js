import request from './request'

export function addDietRecord(data) {
  return request.post('/user/diet-record', data)
}

export function updateDietRecord(id, data) {
  return request.put(`/user/diet-record/${id}`, data)
}

export function deleteDietRecord(id) {
  return request.delete(`/user/diet-record/${id}`)
}

export function getTodayDietRecords() {
  return request.get('/user/diet-record/today')
}

export function getDietRecordList(params) {
  return request.get('/user/diet-record/list', { params })
}

export function getDietRecordDetail(id) {
  return request.get(`/user/diet-record/${id}`)
}
