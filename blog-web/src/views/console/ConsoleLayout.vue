<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import Sidebar from '@/components/console/Sidebar.vue'

const authStore = useAuthStore()
const sidebarCollapsed = ref(false)

onMounted(() => {
  authStore.checkAuth()
})

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-950">
    <Sidebar :collapsed="sidebarCollapsed" @toggle="toggleSidebar" />

    <div
      :class="[
        'transition-all duration-300 min-h-screen',
        sidebarCollapsed ? 'ml-16' : 'ml-60',
      ]"
    >
      <!-- Top Bar -->
      <header class="sticky top-0 z-20 h-16 bg-white/80 dark:bg-gray-900/80 backdrop-blur-lg border-b border-gray-200 dark:border-gray-800 flex items-center justify-between px-6">
        <div>
          <h2 class="text-lg font-semibold text-gray-900 dark:text-gray-100">
            管理后台
          </h2>
        </div>
        <div class="flex items-center gap-3">
          <span class="text-sm text-gray-600 dark:text-gray-400">
            {{ authStore.user?.displayName || authStore.user?.username || '管理员' }}
          </span>
          <div class="w-8 h-8 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-sm font-medium text-primary-700 dark:text-primary-300">
            {{ (authStore.user?.displayName || 'A').charAt(0).toUpperCase() }}
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <main class="p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>
