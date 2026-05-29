import type { Post } from './post'

export interface DashboardStats {
  totalPosts: number
  publishedPosts: number
  draftPosts: number
  totalComments: number
  pendingComments: number
  totalViews: number
  totalLikes: number
  recentPosts: Post[]
}

export interface ArchiveGroup {
  year: number
  month: number
  posts: Post[]
}
