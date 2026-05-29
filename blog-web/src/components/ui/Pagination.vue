<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  total: number
  currentPage: number
  pageSize?: number
}

const props = withDefaults(defineProps<Props>(), {
  pageSize: 10,
})

const emit = defineEmits<{
  (e: 'change', page: number): void
}>()

const totalPages = computed(() => Math.ceil(props.total / props.pageSize))

function goTo(page: number) {
  if (page >= 0 && page < totalPages.value) {
    emit('change', page)
  }
}

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const current = props.currentPage
  const total = totalPages.value

  if (total <= 7) {
    for (let i = 0; i < total; i++) pages.push(i)
    return pages
  }

  pages.push(0)
  if (current > 3) pages.push('...')
  for (let i = Math.max(1, current - 1); i <= Math.min(total - 2, current + 1); i++) {
    pages.push(i)
  }
  if (current < total - 4) pages.push('...')
  pages.push(total - 1)

  return pages
})
</script>

<template>
  <nav v-if="totalPages > 1" class="flex items-center justify-center gap-1">
    <button
      class="px-3 py-2 text-sm rounded-lg transition-colors hover:bg-gray-100 dark:hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed"
      :disabled="currentPage === 0"
      @click="goTo(currentPage - 1)"
    >
      上一页
    </button>

    <template v-for="(page, index) in visiblePages" :key="index">
      <span
        v-if="page === '...'"
        class="px-2 py-2 text-sm text-gray-500"
      >
        ...
      </span>
      <button
        v-else
        :class="[
          'min-w-[36px] px-2 py-2 text-sm rounded-lg transition-colors',
          page === currentPage
            ? 'bg-primary-600 text-white'
            : 'hover:bg-gray-100 dark:hover:bg-gray-800',
        ]"
        @click="goTo(page as number)"
      >
        {{ (page as number) + 1 }}
      </button>
    </template>

    <button
      class="px-3 py-2 text-sm rounded-lg transition-colors hover:bg-gray-100 dark:hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed"
      :disabled="currentPage >= totalPages - 1"
      @click="goTo(currentPage + 1)"
    >
      下一页
    </button>
  </nav>
</template>
