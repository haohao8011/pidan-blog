<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  LayoutDashboard,
  FileText,
  MessageSquare,
  FolderOpen,
  Tags,
  Settings,
  LogOut,
  ChevronLeft,
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const props = defineProps<{
  collapsed: boolean
}>()

const emit = defineEmits<{
  (e: 'toggle'): void
}>()

const navItems = [
  { path: '/console', label: '仪表盘', icon: LayoutDashboard, exact: true },
  { path: '/console/posts', label: '文章', icon: FileText, exact: false },
  { path: '/console/comments', label: '评论', icon: MessageSquare, exact: false },
  { path: '/console/categories', label: '分类', icon: FolderOpen, exact: false },
  { path: '/console/tags', label: '标签', icon: Tags, exact: false },
  { path: '/console/settings', label: '设置', icon: Settings, exact: false },
]

function isActive(item: { path: string; exact: boolean }) {
  if (item.exact) {
    return route.path === item.path
  }
  return route.path.startsWith(item.path)
}

async function handleLogout() {
  await authStore.logout()
  router.push('/setup')
}
</script>

<template>
  <aside
    :class="[
      'fixed left-0 top-0 h-full bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800 z-30 transition-all duration-300 flex flex-col',
      collapsed ? 'w-16' : 'w-60',
    ]"
  >
    <!-- Header -->
    <div class="h-16 flex items-center justify-between px-4 border-b border-gray-200 dark:border-gray-800">
      <span v-if="!collapsed" class="text-lg font-bold text-primary-600 dark:text-primary-400 truncate">
        管理后台
      </span>
      <button
        class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
        @click="emit('toggle')"
      >
        <ChevronLeft
          :class="[
            'w-5 h-5 text-gray-500 transition-transform',
            collapsed ? 'rotate-180' : '',
          ]"
        />
      </button>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 py-4 overflow-y-auto">
      <ul class="space-y-1 px-2">
        <li v-for="item in navItems" :key="item.path">
          <router-link
            :to="item.path"
            :class="[
              'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors',
              isActive(item)
                ? 'bg-primary-50 text-primary-700 dark:bg-primary-900/30 dark:text-primary-400'
                : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
              collapsed ? 'justify-center' : '',
            ]"
            :title="collapsed ? item.label : ''"
          >
            <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
            <span v-if="!collapsed">{{ item.label }}</span>
          </router-link>
        </li>
      </ul>
    </nav>

    <!-- Footer -->
    <div class="p-2 border-t border-gray-200 dark:border-gray-800">
      <router-link
        to="/"
        :class="[
          'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors',
          collapsed ? 'justify-center' : '',
        ]"
        :title="collapsed ? '返回博客' : ''"
      >
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
        </svg>
        <span v-if="!collapsed">返回博客</span>
      </router-link>
      <button
        :class="[
          'w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-red-600 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20 transition-colors',
          collapsed ? 'justify-center' : '',
        ]"
        :title="collapsed ? '退出登录' : ''"
        @click="handleLogout"
      >
        <LogOut class="w-5 h-5 flex-shrink-0" />
        <span v-if="!collapsed">退出登录</span>
      </button>
    </div>
  </aside>
</template>
