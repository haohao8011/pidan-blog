<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import type { Post } from '@/types'
import { getPosts } from '@/api/post'
import PostCard from '@/components/blog/PostCard.vue'
import Pagination from '@/components/ui/Pagination.vue'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'

const route = useRoute()
const posts = ref<Post[]>([])
const loading = ref(true)
const page = ref(0)
const totalElements = ref(0)
const totalPages = ref(0)
const pageSize = 10

async function fetchPosts() {
  loading.value = true
  try {
    const slug = route.params.slug as string
    const res = await getPosts({ page: page.value, size: pageSize, tag: slug })
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

onMounted(fetchPosts)
watch(() => route.params.slug, fetchPosts)
</script>

<template>
  <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">
    标签文章
  </h1>
  <p class="text-gray-500 dark:text-gray-400 mb-8">
    # {{ route.params.slug }}
  </p>

  <div v-if="loading" class="py-20">
    <LoadingSpinner size="lg" />
  </div>

  <template v-else>
    <div class="grid gap-4">
      <PostCard v-for="post in posts" :key="post.id" :post="post" />
    </div>

    <div v-if="posts.length === 0" class="text-center py-20 text-gray-500 dark:text-gray-400">
      该标签下暂无文章
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
