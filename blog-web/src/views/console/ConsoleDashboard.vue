<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { DashboardStats } from '@/types'
import { getDashboard } from '@/api/admin'
import { formatDate } from '@/utils/date'
import Badge from '@/components/ui/Badge.vue'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'
import {
  FileText,
  Eye,
  Heart,
  MessageSquare,
  Clock,
  TrendingUp,
} from 'lucide-vue-next'

const stats = ref<DashboardStats | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getDashboard()
    stats.value = res.data.data
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
})

const statCards = [
  { key: 'totalPosts', label: '文章总数', icon: FileText, color: 'text-blue-600 bg-blue-50 dark:bg-blue-900/20 dark:text-blue-400' },
  { key: 'publishedPosts', label: '已发布', icon: TrendingUp, color: 'text-green-600 bg-green-50 dark:bg-green-900/20 dark:text-green-400' },
  { key: 'totalViews', label: '总浏览量', icon: Eye, color: 'text-purple-600 bg-purple-50 dark:bg-purple-900/20 dark:text-purple-400' },
  { key: 'totalLikes', label: '总点赞', icon: Heart, color: 'text-red-600 bg-red-50 dark:bg-red-900/20 dark:text-red-400' },
  { key: 'totalComments', label: '评论数', icon: MessageSquare, color: 'text-yellow-600 bg-yellow-50 dark:bg-yellow-900/20 dark:text-yellow-400' },
  { key: 'pendingComments', label: '待审核评论', icon: Clock, color: 'text-orange-600 bg-orange-50 dark:bg-orange-900/20 dark:text-orange-400' },
]
</script>

<template>
  <div>
    <div v-if="loading" class="py-20">
      <LoadingSpinner size="lg" />
    </div>

    <div v-else-if="stats" class="space-y-6">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="card in statCards"
          :key="card.key"
          class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-5"
        >
          <div class="flex items-center gap-4">
            <div :class="['p-3 rounded-lg', card.color]">
              <component :is="card.icon" class="w-5 h-5" />
            </div>
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400">{{ card.label }}</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">
                {{ (stats as Record<string, unknown>)[card.key] as number }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Posts -->
      <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800">
        <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100">最近文章</h3>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="border-b border-gray-200 dark:border-gray-700">
                <th class="px-6 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">标题</th>
                <th class="px-6 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">状态</th>
                <th class="px-6 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">浏览</th>
                <th class="px-6 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">点赞</th>
                <th class="px-6 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">日期</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
              <tr
                v-for="post in stats.recentPosts"
                :key="post.id"
                class="hover:bg-gray-50 dark:hover:bg-gray-800/50"
              >
                <td class="px-6 py-4">
                  <router-link
                    :to="`/console/posts/${post.id}/edit`"
                    class="font-medium text-gray-900 dark:text-gray-100 hover:text-primary-600 dark:hover:text-primary-400"
                  >
                    {{ post.title }}
                  </router-link>
                </td>
                <td class="px-6 py-4">
                  <Badge :variant="post.published ? 'success' : 'warning'" size="sm">
                    {{ post.published ? '已发布' : '草稿' }}
                  </Badge>
                </td>
                <td class="px-6 py-4 text-gray-600 dark:text-gray-400">{{ post.viewCount }}</td>
                <td class="px-6 py-4 text-gray-600 dark:text-gray-400">{{ post.likeCount }}</td>
                <td class="px-6 py-4 text-gray-500 dark:text-gray-400">{{ formatDate(post.createdAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
