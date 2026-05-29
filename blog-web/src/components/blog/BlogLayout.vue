<script setup lang="ts">
import BlogHeader from '@/components/blog/BlogHeader.vue'
import BlogFooter from '@/components/blog/BlogFooter.vue'
</script>

<template>
  <div class="min-h-screen flex flex-col bg-white dark:bg-gray-950">
    <BlogHeader />
    <main class="flex-1 max-w-4xl mx-auto w-full px-4 sm:px-6 py-8">
      <slot />
    </main>
    <BlogFooter />
  </div>
</template>
