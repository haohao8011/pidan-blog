<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import type { PostDetail as PostDetailType } from '@/types'
import { getPost, likePost } from '@/api/post'
import { formatDate } from '@/utils/date'
import CommentSection from '@/components/blog/CommentSection.vue'
import Badge from '@/components/ui/Badge.vue'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'
import { Heart, Eye, Calendar, Tag } from 'lucide-vue-next'

const route = useRoute()
const post = ref<PostDetailType | null>(null)
const loading = ref(true)
const liking = ref(false)

async function fetchPost() {
  loading.value = true
  try {
    const slug = route.params.slug as string
    const res = await getPost(slug)
    post.value = res.data.data
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

async function handleLike() {
  if (!post.value || liking.value) return
  liking.value = true
  try {
    const slug = route.params.slug as string
    const res = await likePost(slug)
    post.value.likeCount = res.data.data.likeCount
    window.$toast?.success('点赞成功')
  } catch {
    window.$toast?.error('点赞失败')
  } finally {
    liking.value = false
  }
}

onMounted(fetchPost)
</script>

<template>
  <div v-if="loading" class="py-20">
    <LoadingSpinner size="lg" />
  </div>

  <article v-else-if="post" class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 overflow-hidden">
    <div v-if="post.coverImage" class="aspect-[2/1] overflow-hidden">
      <img :src="post.coverImage" :alt="post.title" class="w-full h-full object-cover" />
    </div>

    <div class="p-6 sm:p-10">
      <h1 class="text-3xl sm:text-4xl font-bold text-gray-900 dark:text-gray-100 mb-4">
        {{ post.title }}
      </h1>

      <div class="flex flex-wrap items-center gap-4 text-sm text-gray-500 dark:text-gray-400 mb-6 pb-6 border-b border-gray-200 dark:border-gray-700">
        <span class="flex items-center gap-1.5">
          <Calendar class="w-4 h-4" />
          {{ formatDate(post.createdAt) }}
        </span>
        <span v-if="post.category" class="flex items-center gap-1.5">
          <router-link
            :to="`/categories/${post.category.slug}`"
            class="text-primary-600 dark:text-primary-400 hover:underline"
          >
            {{ post.category.name }}
          </router-link>
        </span>
        <span class="flex items-center gap-1.5">
          <Eye class="w-4 h-4" />
          {{ post.viewCount }} 次浏览
        </span>
      </div>

      <div v-if="post.tags && post.tags.length > 0" class="flex flex-wrap gap-2 mb-8">
        <router-link v-for="tag in post.tags" :key="tag.id" :to="`/tags/${tag.slug}`">
          <Badge variant="primary" size="md">
            <Tag class="w-3 h-3 mr-1" />
            {{ tag.name }}
          </Badge>
        </router-link>
      </div>

      <div class="prose" v-html="post.contentHtml" />

      <div class="mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
        <button
          :class="[
            'inline-flex items-center gap-2 px-6 py-3 rounded-xl text-sm font-medium transition-all',
            liking ? 'opacity-50 cursor-not-allowed' : 'hover:scale-105',
            'bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 hover:bg-red-100 dark:hover:bg-red-900/30',
          ]"
          :disabled="liking"
          @click="handleLike"
        >
          <Heart class="w-5 h-5" fill="currentColor" />
          <span>{{ post.likeCount }} 赞</span>
        </button>
      </div>
    </div>
  </article>

  <div v-else class="text-center py-20">
    <p class="text-2xl text-gray-400 mb-4">文章不存在</p>
    <router-link to="/" class="text-primary-600 hover:underline">返回首页</router-link>
  </div>

  <div v-if="post" class="mt-8 bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6 sm:p-10">
    <CommentSection
      :post-slug="(route.params.slug as string)"
      :comments="post.comments || []"
      @refresh="fetchPost"
    />
  </div>
</template>
