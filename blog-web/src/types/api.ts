import type { User } from './user'

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  user: User
}

export interface SetupAdminRequest {
  username: string
  password: string
  displayName: string
  blogTitle: string
  blogDescription: string
}

export interface SetupStatus {
  initialized: boolean
}

export interface SiteSetting {
  [key: string]: string
}

export interface DatabaseTestRequest {
  port: number
  username: string
  password: string
  database: string
}
