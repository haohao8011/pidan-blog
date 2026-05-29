<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Tag } from '@/types'
import { getTags } from '@/api/tag'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'

const tags = ref<Tag[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getTags()
    tags.value = res.data.data
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-8">标签</h1>

  <div v-if="loading" class="py-20">
    <LoadingSpinner size="lg" />
  </div>

  <div v-else-if="tags.length === 0" class="text-center py-20 text-gray-500 dark:text-gray-400">
    暂无标签
  </div>

  <div v-else class="flex flex-wrap gap-3">
    <router-link
      v-for="tag in tags"
      :key="tag.id"
      :to="`/tags/${tag.slug}`"
      :class="[
        'inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all hover:scale-105',
        'bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 text-gray-700 dark:text-gray-300',
        'hover:border-primary-300 dark:hover:border-primary-700 hover:text-primary-600 dark:hover:text-primary-400',
        'hover:shadow-md dark:hover:shadow-primary-900/10',
      ]"
    >
      <span># {{ tag.name }}</span>
      <span v-if="tag.postCount !== undefined" class="text-xs text-gray-400 dark:text-gray-500">
        ({{ tag.postCount }})
      </span>
    </router-link>
  </div>
</template>
