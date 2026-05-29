import api from './index'
import type { Tag, ApiResponse } from '@/types'

export function getTags() {
  return api.get<ApiResponse<Tag[]>>('/tags')
}
