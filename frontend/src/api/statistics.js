import request from './request'

export function getDashboard() {
  return request.get('/user/statistics/dashboard')
}

export function getMonthlySummary(params) {
  return request.get('/user/statistics/monthly-summary', { params })
}

export function getStreakStats() {
  return request.get('/user/streak')
}
