export interface Comment {
  id: string
  nickname: string
  email?: string
  content: string
  approved: boolean
  postId: string
  postTitle?: string
  parentId?: string
  createdAt: string
  replies?: Comment[]
}

export interface CommentActionRequest {
  approved: boolean
}
