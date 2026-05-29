import api from './index'
import type {
  ApiResponse,
  PaginatedResponse,
  Post,
  PostDetail,
  Category,
  Tag,
  Comment,
  SiteSetting,
  DashboardStats,
  Media,
  User,
  LoginRequest,
  LoginResponse,
  PostCreateRequest,
  CategoryCreateRequest,
  TagCreateRequest,
  SetupStatus,
  DatabaseTestRequest,
  SetupAdminRequest,
} from '@/types'

// ===== Auth =====
export function login(data: LoginRequest) {
  return api.post<ApiResponse<LoginResponse>>('/admin/auth/login', data)
}

export function logout() {
  return api.post<ApiResponse<null>>('/admin/auth/logout')
}

export function getProfile() {
  return api.get<ApiResponse<User>>('/admin/profile')
}

export function updateProfile(data: { displayName?: string; avatar?: string }) {
  return api.put<ApiResponse<User>>('/admin/profile', data)
}

// ===== Dashboard =====
export function getDashboard() {
  return api.get<ApiResponse<DashboardStats>>('/admin/dashboard')
}

// ===== Posts =====
export interface AdminPostQueryParams {
  page?: number
  size?: number
  keyword?: string
  published?: boolean
  categoryId?: string
}

export function getAdminPosts(params: AdminPostQueryParams = {}) {
  return api.get<ApiResponse<PaginatedResponse<Post>>>('/admin/posts', { params })
}

export function getAdminPost(id: string) {
  return api.get<ApiResponse<PostDetail>>(`/admin/posts/${id}`)
}

export function createPost(data: PostCreateRequest) {
  return api.post<ApiResponse<Post>>('/admin/posts', data)
}

export function updatePost(id: string, data: PostCreateRequest) {
  return api.put<ApiResponse<Post>>(`/admin/posts/${id}`, data)
}

export function deletePost(id: string) {
  return api.delete<ApiResponse<null>>(`/admin/posts/${id}`)
}

// ===== Categories =====
export function getAdminCategories() {
  return api.get<ApiResponse<Category[]>>('/admin/categories')
}

export function createCategory(data: CategoryCreateRequest) {
  return api.post<ApiResponse<Category>>('/admin/categories', data)
}

export function updateCategory(id: string, data: CategoryCreateRequest) {
  return api.put<ApiResponse<Category>>(`/admin/categories/${id}`, data)
}

export function deleteCategory(id: string) {
  return api.delete<ApiResponse<null>>(`/admin/categories/${id}`)
}

// ===== Tags =====
export function getAdminTags() {
  return api.get<ApiResponse<Tag[]>>('/admin/tags')
}

export function createTag(data: TagCreateRequest) {
  return api.post<ApiResponse<Tag>>('/admin/tags', data)
}

export function updateTag(id: string, data: TagCreateRequest) {
  return api.put<ApiResponse<Tag>>(`/admin/tags/${id}`, data)
}

export function deleteTag(id: string) {
  return api.delete<ApiResponse<null>>(`/admin/tags/${id}`)
}

// ===== Comments =====
export interface AdminCommentQueryParams {
  page?: number
  size?: number
  approved?: boolean
  postId?: string
}

export function getAdminComments(params: AdminCommentQueryParams = {}) {
  return api.get<ApiResponse<PaginatedResponse<Comment>>>('/admin/comments', { params })
}

export function approveComment(id: string) {
  return api.put<ApiResponse<Comment>>(`/admin/comments/${id}/approve`)
}

export function rejectComment(id: string) {
  return api.put<ApiResponse<Comment>>(`/admin/comments/${id}/reject`)
}

export function deleteComment(id: string) {
  return api.delete<ApiResponse<null>>(`/admin/comments/${id}`)
}

// ===== Settings =====
export function getAdminSettings() {
  return api.get<ApiResponse<SiteSetting>>('/admin/settings')
}

export function updateSettings(data: SiteSetting) {
  return api.put<ApiResponse<SiteSetting>>('/admin/settings', data)
}

// ===== Media =====
export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post<ApiResponse<Media>>('/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function getMediaList(params: { page?: number; size?: number } = {}) {
  return api.get<ApiResponse<PaginatedResponse<Media>>>('/admin/media', { params })
}

export function deleteMedia(id: string) {
  return api.delete<ApiResponse<null>>(`/admin/media/${id}`)
}

// ===== Setup =====
export function getSetupStatus() {
  return api.get<ApiResponse<SetupStatus>>('/setup/status')
}

export function testDatabase(data: DatabaseTestRequest) {
  return api.post<ApiResponse<boolean>>('/setup', data)
}

export function setupAdmin(data: SetupAdminRequest) {
  return api.post<ApiResponse<null>>('/setup/admin', data)
}
