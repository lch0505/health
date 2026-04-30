import request from './request'

export function getAchievementWall() {
  return request.get('/user/achievement/wall')
}

export function getMyAchievements() {
  return request.get('/user/achievement/my')
}

export function markAchievementAsRead(id) {
  return request.post(`/user/achievement/read/${id}`)
}
