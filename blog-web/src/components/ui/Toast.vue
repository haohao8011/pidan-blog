<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'

interface ToastItem {
  id: number
  message: string
  type: 'success' | 'error' | 'info'
}

const toasts = ref<ToastItem[]>([])
let nextId = 0

function addToast(message: string, type: ToastItem['type'] = 'info', duration = 3000) {
  const id = nextId++
  toasts.value.push({ id, message, type })
  setTimeout(() => {
    removeToast(id)
  }, duration)
}

function removeToast(id: number) {
  toasts.value = toasts.value.filter((t) => t.id !== id)
}

function success(message: string, duration?: number) {
  addToast(message, 'success', duration)
}

function error(message: string, duration?: number) {
  addToast(message, 'error', duration)
}

function info(message: string, duration?: number) {
  addToast(message, 'info', duration)
}

// Global toast instance
const toastInstance = { success, error, info }

// Make available globally
declare global {
  interface Window {
    $toast: typeof toastInstance
  }
}

onMounted(() => {
  window.$toast = toastInstance
})

defineExpose({ success, error, info })

const typeColors: Record<string, string> = {
  success: 'bg-green-500',
  error: 'bg-red-500',
  info: 'bg-primary-500',
}
</script>

<template>
  <div class="fixed top-4 right-4 z-50 flex flex-col gap-2 pointer-events-none">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        :class="[
          'pointer-events-auto px-4 py-3 rounded-lg shadow-lg text-white text-sm flex items-center gap-2 min-w-[200px] max-w-[400px]',
          typeColors[toast.type],
        ]"
      >
        <span class="flex-1">{{ toast.message }}</span>
        <button
          class="text-white/80 hover:text-white"
          @click="removeToast(toast.id)"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>
