import request from './request'

export function getAnnouncementList(params) {
  return request.get('/announcement/list', { params })
}

export function getAnnouncementLatest(limit) {
  return request.get('/announcement/latest', { params: { limit } })
}

export function getAnnouncementDetail(id) {
  return request.get(`/announcement/${id}`)
}
