<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { ArchiveGroup } from '@/types'
import { getArchive } from '@/api/archive'
import { formatDate } from '@/utils/date'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'

const archives = ref<ArchiveGroup[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getArchive()
    archives.value = res.data.data
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
})

const monthNames = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
</script>

<template>
  <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-8">归档</h1>

  <div v-if="loading" class="py-20">
    <LoadingSpinner size="lg" />
  </div>

  <div v-else-if="archives.length === 0" class="text-center py-20 text-gray-500 dark:text-gray-400">
    暂无文章
  </div>

  <div v-else class="space-y-8">
    <div v-for="group in archives" :key="`${group.year}-${group.month}`">
      <div class="flex items-center gap-3 mb-4">
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100">
          {{ group.year }}年{{ monthNames[group.month - 1] }}
        </h2>
        <span class="text-sm text-gray-400 dark:text-gray-500">
          ({{ group.posts.length }} 篇)
        </span>
        <div class="flex-1 h-px bg-gray-200 dark:bg-gray-700" />
      </div>

      <div class="relative pl-6 border-l-2 border-gray-200 dark:border-gray-700 space-y-4">
        <div v-for="post in group.posts" :key="post.id" class="relative">
          <div class="absolute -left-[29px] top-1.5 w-3 h-3 rounded-full bg-primary-500 border-2 border-white dark:border-gray-950" />
          <router-link :to="`/posts/${post.slug}`" class="block group">
            <span class="text-xs text-gray-400 dark:text-gray-500">
              {{ formatDate(post.createdAt) }}
            </span>
            <h3 class="text-sm font-medium text-gray-800 dark:text-gray-200 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors mt-1">
              {{ post.title }}
            </h3>
            <div v-if="post.category" class="text-xs text-gray-400 dark:text-gray-500 mt-0.5">
              {{ post.category.name }}
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>
