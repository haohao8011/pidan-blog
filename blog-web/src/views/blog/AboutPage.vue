<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getSettings } from '@/api/setting'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'

const settings = ref<Record<string, string>>({})
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getSettings()
    settings.value = res.data.data
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div v-if="loading" class="py-20">
    <LoadingSpinner size="lg" />
  </div>

  <div v-else class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-8 sm:p-12">
    <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-6">关于</h1>

    <div class="prose max-w-none text-gray-700 dark:text-gray-300">
      <p v-if="settings['blog.description']" class="text-lg leading-relaxed mb-4">
        {{ settings['blog.description'] }}
      </p>
      <p v-else class="text-lg leading-relaxed text-gray-500 dark:text-gray-400">
        这是一个使用 Vue 3 + Spring Boot 构建的博客系统。
      </p>

      <h2>技术栈</h2>
      <ul>
        <li>前端：Vue 3 + TypeScript + Tailwind CSS</li>
        <li>后端：Spring Boot + JPA + PostgreSQL</li>
        <li>编辑器：Tiptap (ProseMirror)</li>
        <li>部署：Docker Compose</li>
      </ul>

      <h2>功能特性</h2>
      <ul>
        <li>文章管理（Markdown / 富文本）</li>
        <li>分类与标签系统</li>
        <li>评论系统（需审核）</li>
        <li>归档时间线</li>
        <li>暗色模式</li>
        <li>响应式设计</li>
        <li>SEO 友好</li>
      </ul>
    </div>
  </div>
</template>
