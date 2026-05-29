import api from './index'
import type { SiteSetting, ApiResponse } from '@/types'

export function getSettings() {
  return api.get<ApiResponse<SiteSetting>>('/settings')
}
