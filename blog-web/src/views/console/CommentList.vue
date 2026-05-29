<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Comment } from '@/types'
import { getAdminComments, approveComment, rejectComment, deleteComment } from '@/api/admin'
import { formatDateTime } from '@/utils/date'
import Badge from '@/components/ui/Badge.vue'
import Pagination from '@/components/ui/Pagination.vue'
import Modal from '@/components/console/Modal.vue'
import { Check, X, Trash2 } from 'lucide-vue-next'

const comments = ref<Comment[]>([])
const loading = ref(true)
const page = ref(0)
const totalElements = ref(0)
const pageSize = 15
const filterStatus = ref<'all' | 'pending' | 'approved'>('all')

const showDeleteModal = ref(false)
const deletingComment = ref<Comment | null>(null)
const deleting = ref(false)

async function fetchComments() {
  loading.value = true
  try {
    const params: Record<string, unknown> = { page: page.value, size: pageSize }
    if (filterStatus.value === 'pending') params.approved = false
    if (filterStatus.value === 'approved') params.approved = true

    const res = await getAdminComments(params as Parameters<typeof getAdminComments>[0])
    comments.value = res.data.data.content
    totalElements.value = res.data.data.totalElements
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
}

function onPageChange(newPage: number) {
  page.value = newPage
  fetchComments()
}

async function handleApprove(comment: Comment) {
  try {
    await approveComment(comment.id)
    fetchComments()
    window.$toast?.success('已通过')
  } catch {
    window.$toast?.error('操作失败')
  }
}

async function handleReject(comment: Comment) {
  try {
    await rejectComment(comment.id)
    fetchComments()
    window.$toast?.success('已拒绝')
  } catch {
    window.$toast?.error('操作失败')
  }
}

function confirmDelete(comment: Comment) {
  deletingComment.value = comment
  showDeleteModal.value = true
}

async function handleDelete() {
  if (!deletingComment.value) return
  deleting.value = true
  try {
    await deleteComment(deletingComment.value.id)
    showDeleteModal.value = false
    deletingComment.value = null
    fetchComments()
    window.$toast?.success('已删除')
  } catch {
    window.$toast?.error('删除失败')
  } finally {
    deleting.value = false
  }
}

onMounted(fetchComments)
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-6">评论管理</h1>

    <!-- Filter -->
    <div class="mb-4">
      <select
        v-model="filterStatus"
        class="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
        @change="fetchComments"
      >
        <option value="all">全部评论</option>
        <option value="pending">待审核</option>
        <option value="approved">已通过</option>
      </select>
    </div>

    <!-- Table -->
    <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 overflow-hidden">
      <div v-if="loading" class="py-20 text-center text-gray-500 dark:text-gray-400">加载中...</div>

      <div v-else-if="comments.length === 0" class="py-20 text-center text-gray-500 dark:text-gray-400">
        暂无评论
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-200 dark:border-gray-700">
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:120px">作者</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">内容</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:150px">文章</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:80px">状态</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:120px">日期</th>
              <th class="px-4 py-3 text-right text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:140px">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr
              v-for="comment in comments"
              :key="comment.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-800/50"
            >
              <td class="px-4 py-3">
                <div>
                  <p class="font-medium text-gray-900 dark:text-gray-100">{{ comment.nickname }}</p>
                  <p v-if="comment.email" class="text-xs text-gray-500 dark:text-gray-400">{{ comment.email }}</p>
                </div>
              </td>
              <td class="px-4 py-3">
                <p class="text-gray-700 dark:text-gray-300 line-clamp-2">{{ comment.content }}</p>
              </td>
              <td class="px-4 py-3 text-gray-600 dark:text-gray-400 text-xs">
                {{ comment.postTitle || '-' }}
              </td>
              <td class="px-4 py-3">
                <Badge :variant="comment.approved ? 'success' : 'warning'" size="sm">
                  {{ comment.approved ? '已通过' : '待审核' }}
                </Badge>
              </td>
              <td class="px-4 py-3 text-gray-500 dark:text-gray-400 text-xs">
                {{ formatDateTime(comment.createdAt) }}
              </td>
              <td class="px-4 py-3 text-right">
                <div class="flex items-center justify-end gap-1">
                  <button
                    v-if="!comment.approved"
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-green-600 transition-colors"
                    title="通过"
                    @click="handleApprove(comment)"
                  >
                    <Check class="w-4 h-4" />
                  </button>
                  <button
                    v-if="comment.approved"
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-yellow-600 transition-colors"
                    title="拒绝"
                    @click="handleReject(comment)"
                  >
                    <X class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-red-600 transition-colors"
                    title="删除"
                    @click="confirmDelete(comment)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

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
      <p class="text-gray-700 dark:text-gray-300">确定要删除此评论吗？此操作不可撤销。</p>
      <template #footer>
        <Button variant="secondary" @click="showDeleteModal = false">取消</Button>
        <Button variant="danger" :loading="deleting" @click="handleDelete">删除</Button>
      </template>
    </Modal>
  </div>
</template>

<script lang="ts">
import Button from '@/components/ui/Button.vue'
export default { components: { Button } }
</script>
