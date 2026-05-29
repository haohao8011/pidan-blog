import api from './index'
import type { ArchiveGroup, ApiResponse } from '@/types'

export function getArchive() {
  return api.get<ApiResponse<ArchiveGroup[]>>('/archive')
}
