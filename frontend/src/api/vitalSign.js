import request from './request'

export function addVitalSign(data) {
  return request.post('/user/vital-sign', data)
}

export function updateVitalSign(id, data) {
  return request.put(`/user/vital-sign/${id}`, data)
}

export function deleteVitalSign(id) {
  return request.delete(`/user/vital-sign/${id}`)
}

export function getTodayVitalSign() {
  return request.get('/user/vital-sign/today')
}

export function getRecentVitalSigns(days) {
  return request.get(`/user/vital-sign/recent/${days}`)
}

export function getVitalSignList(params) {
  return request.get('/user/vital-sign/list', { params })
}

export function getVitalSignDetail(id) {
  return request.get(`/user/vital-sign/${id}`)
}
