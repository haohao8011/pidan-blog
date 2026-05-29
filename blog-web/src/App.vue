<script setup lang="ts">
import { watch } from 'vue'
import { useDarkModeStore } from '@/stores/darkMode'

const darkModeStore = useDarkModeStore()

watch(
  () => darkModeStore.isDark,
  (isDark) => {
    if (isDark) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  },
  { immediate: true }
)
</script>

<template>
  <router-view />
</template>
