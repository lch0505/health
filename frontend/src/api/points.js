import request from './request'

export function getPointsSummary() {
  return request.get('/user/points/summary')
}

export function getPointsRecordList(params) {
  return request.get('/user/points/record/list', { params })
}

export function getPointsConfigList() {
  return request.get('/points-config/list')
}

export function getPointsConfigDetail(pointsType) {
  return request.get(`/points-config/${pointsType}`)
}
