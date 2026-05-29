import type { Category } from './category'
import type { Tag } from './tag'
import type { Comment } from './comment'

export interface Post {
  id: string
  title: string
  slug: string
  excerpt: string
  coverImage?: string
  viewCount: number
  likeCount: number
  published: boolean
  pinned: boolean
  createdAt: string
  updatedAt: string
  category: Category
  tags: Tag[]
}

export interface PostDetail extends Post {
  content: string
  contentHtml: string
  comments: Comment[]
}

export interface PostCreateRequest {
  title: string
  slug?: string
  content: string
  contentHtml: string
  excerpt?: string
  coverImage?: string
  categoryId?: string
  tagIds?: string[]
  published: boolean
  pinned: boolean
}

export interface PostUpdateRequest extends PostCreateRequest {
  id: string
}
