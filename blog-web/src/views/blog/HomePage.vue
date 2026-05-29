<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import type { Post } from '@/types'
import { getPosts } from '@/api/post'
import PostCard from '@/components/blog/PostCard.vue'
import Pagination from '@/components/ui/Pagination.vue'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'

const posts = ref<Post[]>([])
const loading = ref(true)
const page = ref(0)
const totalElements = ref(0)
const totalPages = ref(0)
const pageSize = 10

async function fetchPosts() {
  loading.value = true
  try {
    const res = await getPosts({ page: page.value, size: pageSize })
    const data = res.data.data
    posts.value = data.content
    totalElements.value = data.totalElements
    totalPages.value = data.totalPages
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
}

function onPageChange(newPage: number) {
  page.value = newPage
  fetchPosts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const pinnedPosts = ref<Post[]>([])
const regularPosts = ref<Post[]>([])

onMounted(async () => {
  await fetchPosts()
  pinnedPosts.value = posts.value.filter((p) => p.pinned)
  regularPosts.value = posts.value.filter((p) => !p.pinned)
})

watch(posts, (val) => {
  pinnedPosts.value = val.filter((p) => p.pinned)
  regularPosts.value = val.filter((p) => !p.pinned)
})
</script>

<template>
  <div v-if="loading" class="py-20">
    <LoadingSpinner size="lg" />
  </div>

  <template v-else>
    <section v-if="pinnedPosts.length > 0" class="mb-8">
      <h2 class="text-sm font-semibold text-primary-600 dark:text-primary-400 uppercase tracking-wider mb-4">
        置顶文章
      </h2>
      <div class="grid gap-4">
        <PostCard v-for="post in pinnedPosts" :key="post.id" :post="post" />
      </div>
    </section>

    <section>
      <div class="grid gap-4">
        <PostCard v-for="post in regularPosts" :key="post.id" :post="post" />
      </div>
    </section>

    <div v-if="posts.length === 0" class="text-center py-20">
      <div class="text-gray-400 dark:text-gray-500 text-6xl mb-4">
        <svg class="w-16 h-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
        </svg>
      </div>
      <p class="text-gray-500 dark:text-gray-400">暂无文章</p>
    </div>

    <div v-if="totalPages > 1" class="mt-8">
      <Pagination
        :total="totalElements"
        :current-page="page"
        :page-size="pageSize"
        @change="onPageChange"
      />
    </div>
  </template>
</template>
