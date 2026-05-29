<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<template>
  <footer class="border-t border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-900/50">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 py-8">
      <div class="flex flex-col sm:flex-row items-center justify-between gap-4">
        <p class="text-sm text-gray-500 dark:text-gray-400">
          &copy; {{ year }} PiDan Blog. Built with Vue 3 & Spring Boot.
        </p>
        <div class="flex items-center gap-4 text-sm text-gray-500 dark:text-gray-400">
          <router-link
            to="/"
            class="hover:text-primary-600 dark:hover:text-primary-400 transition-colors"
          >
            首页
          </router-link>
          <router-link
            to="/archive"
            class="hover:text-primary-600 dark:hover:text-primary-400 transition-colors"
          >
            归档
          </router-link>
          <router-link
            to="/about"
            class="hover:text-primary-600 dark:hover:text-primary-400 transition-colors"
          >
            关于
          </router-link>
        </div>
      </div>
    </div>
  </footer>
</template>
