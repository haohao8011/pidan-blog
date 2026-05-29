import api from './index'
import type { Category, ApiResponse } from '@/types'

export function getCategories() {
  return api.get<ApiResponse<Category[]>>('/categories')
}
