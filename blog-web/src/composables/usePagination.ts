import { ref, reactive } from 'vue'
import type { PaginatedResponse } from '@/types'
import axios from 'axios'

export function usePagination<T>(fetchFn: (params: Record<string, unknown>) => Promise<{ data: { data: PaginatedResponse<T> } }>) {
  const data = ref<T[]>([]) as { value: T[] }
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pagination = reactive({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  })

  async function fetchData(extraParams: Record<string, unknown> = {}) {
    loading.value = true
    error.value = null
    try {
      const res = await fetchFn({
        page: pagination.page,
        size: pagination.size,
        ...extraParams,
      })
      const result = res.data.data
      data.value = result.content
      pagination.totalElements = result.totalElements
      pagination.totalPages = result.totalPages
    } catch (err) {
      if (axios.isAxiosError(err)) {
        error.value = err.message || '请求失败'
      } else if (err instanceof Error) {
        error.value = err.message
      } else {
        error.value = '未知错误'
      }
    } finally {
      loading.value = false
    }
  }

  function goToPage(page: number) {
    pagination.page = page
    fetchData()
  }

  function nextPage() {
    if (pagination.page < pagination.totalPages - 1) {
      goToPage(pagination.page + 1)
    }
  }

  function prevPage() {
    if (pagination.page > 0) {
      goToPage(pagination.page - 1)
    }
  }

  return {
    data,
    loading,
    error,
    pagination,
    fetchData,
    goToPage,
    nextPage,
    prevPage,
  }
}
