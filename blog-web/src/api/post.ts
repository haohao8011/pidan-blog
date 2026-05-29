import api from './index'
import type { Post, PostDetail, PaginatedResponse, ApiResponse } from '@/types'

export interface PostQueryParams {
  page?: number
  size?: number
  category?: string
  tag?: string
  keyword?: string
}

export function getPosts(params: PostQueryParams = {}) {
  return api.get<ApiResponse<PaginatedResponse<Post>>>('/posts', { params })
}

export function getPost(slug: string) {
  return api.get<ApiResponse<PostDetail>>(`/posts/${slug}`)
}

export function likePost(slug: string) {
  return api.post<ApiResponse<{ likeCount: number }>>(`/posts/${slug}/like`)
}

export function submitComment(slug: string, data: { nickname: string; email?: string; content: string; parentId?: string }) {
  return api.post<ApiResponse<unknown>>(`/posts/${slug}/comments`, data)
}
