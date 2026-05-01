import request from './request'

export function getMessageBoardList(params) {
  return request.get('/message-board/list', { params })
}

export function getMessageBoardDetail(id) {
  return request.get(`/message-board/${id}`)
}

export function createMessageBoard(data) {
  return request.post('/message-board/add', data)
}

export function updateMessageBoard(data) {
  return request.put('/message-board/update', data)
}

export function deleteMessageBoard(id) {
  return request.delete(`/message-board/${id}`)
}
