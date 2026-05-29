export interface Category {
  id: string
  name: string
  slug: string
  description?: string
  sortOrder: number
  postCount?: number
}

export interface CategoryCreateRequest {
  name: string
  slug?: string
  description?: string
  sortOrder?: number
}
