<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { SiteSetting } from '@/types'
import { getAdminSettings, updateSettings } from '@/api/admin'
import Button from '@/components/ui/Button.vue'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'
import { Save } from 'lucide-vue-next'

const settings = ref<SiteSetting>({})
const loading = ref(true)
const saving = ref(false)

// Form fields
const form = ref({
  'blog.title': '',
  'blog.description': '',
  'blog.keywords': '',
  'blog.avatar': '',
  'blog.footer': '',
  'social.github': '',
  'social.twitter': '',
})

onMounted(async () => {
  try {
    const res = await getAdminSettings()
    settings.value = res.data.data
    // Populate form
    for (const key of Object.keys(form.value)) {
      if (settings.value[key] !== undefined) {
        (form.value as Record<string, string>)[key] = settings.value[key]
      }
    }
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
})

async function handleSave() {
  saving.value = true
  try {
    await updateSettings(form.value)
    window.$toast?.success('设置已保存')
  } catch (err) {
    window.$toast?.error(err instanceof Error ? err.message : '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100">站点设置</h1>
      <Button :loading="saving" @click="handleSave">
        <Save class="w-4 h-4" />
        保存设置
      </Button>
    </div>

    <div v-if="loading" class="py-20">
      <LoadingSpinner size="lg" />
    </div>

    <div v-else class="space-y-6">
      <!-- Blog Settings -->
      <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">博客设置</h2>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">博客标题</label>
            <input
              v-model="form['blog.title']"
              type="text"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">博客描述</label>
            <textarea
              v-model="form['blog.description']"
              rows="3"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">关键词</label>
            <input
              v-model="form['blog.keywords']"
              type="text"
              placeholder="用逗号分隔"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">头像 URL</label>
            <input
              v-model="form['blog.avatar']"
              type="text"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">页脚文字</label>
            <input
              v-model="form['blog.footer']"
              type="text"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
        </div>
      </div>

      <!-- Social Settings -->
      <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">社交链接</h2>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">GitHub</label>
            <input
              v-model="form['social.github']"
              type="url"
              placeholder="https://github.com/username"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Twitter / X</label>
            <input
              v-model="form['social.twitter']"
              type="url"
              placeholder="https://twitter.com/username"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
        </div>
      </div>

      <!-- Save Button (bottom) -->
      <div class="flex justify-end">
        <Button :loading="saving" @click="handleSave">
          <Save class="w-4 h-4" />
          保存设置
        </Button>
      </div>
    </div>
  </div>
</template>
