import request from './request'

export function addMoodRecord(data) {
  return request.post('/user/mood-record', data)
}

export function updateMoodRecord(id, data) {
  return request.put(`/user/mood-record/${id}`, data)
}

export function deleteMoodRecord(id) {
  return request.delete(`/user/mood-record/${id}`)
}

export function getTodayMoodRecord() {
  return request.get('/user/mood-record/today')
}

export function getRecentMoodRecords(days) {
  return request.get(`/user/mood-record/recent/${days}`)
}

export function getMoodRecordList(params) {
  return request.get('/user/mood-record/list', { params })
}

export function getMoodRecordDetail(id) {
  return request.get(`/user/mood-record/${id}`)
}
