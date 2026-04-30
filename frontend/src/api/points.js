import request from './request'

export function getPointsSummary() {
  return request.get('/user/points/summary')
}

export function getPointsRecordList(params) {
  return request.get('/user/points/record/list', { params })
}
