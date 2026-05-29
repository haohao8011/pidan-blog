<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Category } from '@/types'
import { getCategories } from '@/api/category'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'
import { FolderOpen } from 'lucide-vue-next'

const categories = ref<Category[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getCategories()
    categories.value = res.data.data
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-8">分类</h1>

  <div v-if="loading" class="py-20">
    <LoadingSpinner size="lg" />
  </div>

  <div v-else-if="categories.length === 0" class="text-center py-20 text-gray-500 dark:text-gray-400">
    暂无分类
  </div>

  <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
    <router-link
      v-for="cat in categories"
      :key="cat.id"
      :to="`/categories/${cat.slug}`"
      class="group bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6 hover:shadow-lg dark:hover:shadow-primary-900/10 transition-all"
    >
      <div class="flex items-start justify-between">
        <div class="flex items-center gap-3">
          <div class="p-2.5 rounded-lg bg-primary-50 dark:bg-primary-900/20 text-primary-600 dark:text-primary-400 group-hover:bg-primary-100 dark:group-hover:bg-primary-900/30 transition-colors">
            <FolderOpen class="w-5 h-5" />
          </div>
          <div>
            <h3 class="font-semibold text-gray-900 dark:text-gray-100 group-hover:text-primary-600 dark:group-hover:text-primary-400 transition-colors">
              {{ cat.name }}
            </h3>
            <p v-if="cat.description" class="text-sm text-gray-500 dark:text-gray-400 mt-1 line-clamp-2">
              {{ cat.description }}
            </p>
          </div>
        </div>
        <span v-if="cat.postCount !== undefined" class="text-sm text-gray-400 dark:text-gray-500">
          {{ cat.postCount }} 篇
        </span>
      </div>
    </router-link>
  </div>
</template>
