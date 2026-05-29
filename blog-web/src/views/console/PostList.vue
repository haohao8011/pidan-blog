<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Post } from '@/types'
import { getAdminPosts, deletePost } from '@/api/admin'
import { formatDate } from '@/utils/date'
import Button from '@/components/ui/Button.vue'
import Badge from '@/components/ui/Badge.vue'
import Pagination from '@/components/ui/Pagination.vue'
import Modal from '@/components/console/Modal.vue'
import { Plus, Pencil, Trash2, Search } from 'lucide-vue-next'

const posts = ref<Post[]>([])
const loading = ref(true)
const page = ref(0)
const totalElements = ref(0)
const pageSize = 15
const searchKeyword = ref('')
const filterStatus = ref<'all' | 'published' | 'draft'>('all')

// Delete modal
const showDeleteModal = ref(false)
const deletingPost = ref<Post | null>(null)
const deleting = ref(false)

async function fetchPosts() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      page: page.value,
      size: pageSize,
    }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (filterStatus.value === 'published') params.published = true
    if (filterStatus.value === 'draft') params.published = false

    const res = await getAdminPosts(params as { page?: number; size?: number; keyword?: string; published?: boolean })
    posts.value = res.data.data.content
    totalElements.value = res.data.data.totalElements
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
}

function onPageChange(newPage: number) {
  page.value = newPage
  fetchPosts()
}

function handleSearch() {
  page.value = 0
  fetchPosts()
}

function confirmDelete(post: Post) {
  deletingPost.value = post
  showDeleteModal.value = true
}

async function handleDelete() {
  if (!deletingPost.value) return
  deleting.value = true
  try {
    await deletePost(deletingPost.value.id)
    showDeleteModal.value = false
    deletingPost.value = null
    fetchPosts()
    window.$toast?.success('删除成功')
  } catch {
    window.$toast?.error('删除失败')
  } finally {
    deleting.value = false
  }
}

onMounted(fetchPosts)
</script>

<template>
  <div>
    <!-- Header -->
    <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100">文章管理</h1>
      <router-link to="/console/posts/new">
        <Button>
          <Plus class="w-4 h-4" />
          新建文章
        </Button>
      </router-link>
    </div>

    <!-- Filters -->
    <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 mb-4 p-4">
      <div class="flex flex-col sm:flex-row gap-3">
        <div class="flex-1 relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索文章..."
            class="w-full pl-9 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            @keyup.enter="handleSearch"
          />
        </div>
        <select
          v-model="filterStatus"
          class="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          @change="handleSearch"
        >
          <option value="all">全部状态</option>
          <option value="published">已发布</option>
          <option value="draft">草稿</option>
        </select>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 overflow-hidden">
      <div v-if="loading" class="py-20 text-center text-gray-500 dark:text-gray-400">
        <div class="inline-flex items-center gap-2">
          <svg class="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          加载中...
        </div>
      </div>

      <div v-else-if="posts.length === 0" class="py-20 text-center text-gray-500 dark:text-gray-400">
        暂无文章
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-200 dark:border-gray-700">
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">标题</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:100px">状态</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:100px">分类</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:80px">浏览</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:80px">点赞</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:120px">日期</th>
              <th class="px-4 py-3 text-right text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:120px">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr
              v-for="post in posts"
              :key="post.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-800/50"
            >
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <Badge v-if="post.pinned" variant="danger" size="sm">置顶</Badge>
                  <span class="font-medium text-gray-900 dark:text-gray-100 truncate max-w-[300px]">
                    {{ post.title }}
                  </span>
                </div>
              </td>
              <td class="px-4 py-3">
                <Badge :variant="post.published ? 'success' : 'warning'" size="sm">
                  {{ post.published ? '已发布' : '草稿' }}
                </Badge>
              </td>
              <td class="px-4 py-3 text-gray-600 dark:text-gray-400">
                {{ post.category?.name || '-' }}
              </td>
              <td class="px-4 py-3 text-gray-600 dark:text-gray-400">{{ post.viewCount }}</td>
              <td class="px-4 py-3 text-gray-600 dark:text-gray-400">{{ post.likeCount }}</td>
              <td class="px-4 py-3 text-gray-500 dark:text-gray-400">{{ formatDate(post.createdAt) }}</td>
              <td class="px-4 py-3 text-right">
                <div class="flex items-center justify-end gap-1">
                  <router-link
                    :to="`/console/posts/${post.id}/edit`"
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-primary-600 transition-colors"
                    title="编辑"
                  >
                    <Pencil class="w-4 h-4" />
                  </router-link>
                  <button
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-red-600 transition-colors"
                    title="删除"
                    @click="confirmDelete(post)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalElements > pageSize" class="border-t border-gray-200 dark:border-gray-700 py-4">
        <Pagination
          :total="totalElements"
          :current-page="page"
          :page-size="pageSize"
          @change="onPageChange"
        />
      </div>
    </div>

    <!-- Delete Modal -->
    <Modal :visible="showDeleteModal" title="确认删除" @close="showDeleteModal = false">
      <p class="text-gray-700 dark:text-gray-300">
        确定要删除文章「<span class="font-semibold">{{ deletingPost?.title }}</span>」吗？此操作不可撤销。
      </p>
      <template #footer>
        <Button variant="secondary" @click="showDeleteModal = false">取消</Button>
        <Button variant="danger" :loading="deleting" @click="handleDelete">删除</Button>
      </template>
    </Modal>
  </div>
</template>
