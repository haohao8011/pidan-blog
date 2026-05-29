<script setup lang="ts">
import { computed } from 'vue'
import type { Post } from '@/types'
import { formatDate } from '@/utils/date'
import Badge from '@/components/ui/Badge.vue'

const props = defineProps<{
  post: Post
}>()

const excerpt = computed(() => {
  if (props.post.excerpt) return props.post.excerpt
  return props.post.title
})
</script>

<template>
  <router-link
    :to="`/posts/${post.slug}`"
    class="group block bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 overflow-hidden hover:shadow-lg dark:hover:shadow-primary-900/10 transition-all duration-300"
  >
    <!-- Cover Image -->
    <div v-if="post.coverImage" class="aspect-[16/9] overflow-hidden bg-gray-100 dark:bg-gray-800">
      <img
        :src="post.coverImage"
        :alt="post.title"
        class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
        loading="lazy"
      />
    </div>

    <div class="p-5">
      <!-- Pinned badge -->
      <div v-if="post.pinned" class="mb-2">
        <Badge variant="primary" size="sm">置顶</Badge>
      </div>

      <!-- Title -->
      <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors line-clamp-2">
        {{ post.title }}
      </h2>

      <!-- Excerpt -->
      <p class="text-gray-600 dark:text-gray-400 text-sm leading-relaxed mb-4 line-clamp-3">
        {{ excerpt }}
      </p>

      <!-- Meta -->
      <div class="flex flex-wrap items-center gap-3 text-xs text-gray-500 dark:text-gray-400">
        <span>{{ formatDate(post.createdAt) }}</span>
        <span v-if="post.category" class="text-primary-600 dark:text-primary-400">
          {{ post.category.name }}
        </span>
        <div v-if="post.tags && post.tags.length > 0" class="flex gap-1.5">
          <Badge v-for="tag in post.tags.slice(0, 3)" :key="tag.id" size="sm">
            {{ tag.name }}
          </Badge>
        </div>
        <span class="ml-auto flex items-center gap-1">
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
          </svg>
          {{ post.viewCount }}
        </span>
        <span class="flex items-center gap-1">
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
          </svg>
          {{ post.likeCount }}
        </span>
      </div>
    </div>
  </router-link>
</template>
