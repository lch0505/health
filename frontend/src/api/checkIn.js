import request from './request'

export function checkIn(data) {
  return request.post('/user/check-in', data)
}

export function getTodayStatus() {
  return request.get('/user/check-in/today')
}

export function getCheckInList(params) {
  return request.get('/user/check-in/list', { params })
}

export function getCheckInDetail(id) {
  return request.get(`/user/check-in/${id}`)
}
