export interface Tag {
  id: string
  name: string
  slug: string
  postCount?: number
}

export interface TagCreateRequest {
  name: string
  slug?: string
}
