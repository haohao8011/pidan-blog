<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Tag } from '@/types'
import {
  getAdminTags,
  createTag,
  updateTag,
  deleteTag,
} from '@/api/admin'
import { generateSlug } from '@/utils/slug'
import Button from '@/components/ui/Button.vue'
import Modal from '@/components/console/Modal.vue'
import { Plus, Pencil, Trash2 } from 'lucide-vue-next'

const tags = ref<Tag[]>([])
const loading = ref(true)
const showModal = ref(false)
const editingTag = ref<Tag | null>(null)
const saving = ref(false)
const deletingId = ref<string | null>(null)

const form = ref({
  name: '',
  slug: '',
})

async function fetchTags() {
  loading.value = true
  try {
    const res = await getAdminTags()
    tags.value = res.data.data
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingTag.value = null
  form.value = { name: '', slug: '' }
  showModal.value = true
}

function openEdit(tag: Tag) {
  editingTag.value = tag
  form.value = { name: tag.name, slug: tag.slug }
  showModal.value = true
}

function handleNameInput() {
  if (!editingTag.value) {
    form.value.slug = generateSlug(form.value.name)
  }
}

async function handleSave() {
  if (!form.value.name.trim()) return
  saving.value = true
  try {
    const data = {
      name: form.value.name.trim(),
      slug: form.value.slug || generateSlug(form.value.name),
    }
    if (editingTag.value) {
      await updateTag(editingTag.value.id, data)
      window.$toast?.success('标签已更新')
    } else {
      await createTag(data)
      window.$toast?.success('标签已创建')
    }
    showModal.value = false
    fetchTags()
  } catch (err) {
    window.$toast?.error(err instanceof Error ? err.message : '操作失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: string) {
  deletingId.value = id
  try {
    await deleteTag(id)
    fetchTags()
    window.$toast?.success('标签已删除')
  } catch {
    window.$toast?.error('删除失败')
  } finally {
    deletingId.value = null
  }
}

onMounted(fetchTags)
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100">标签管理</h1>
      <Button @click="openCreate">
        <Plus class="w-4 h-4" />
        新建标签
      </Button>
    </div>

    <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 overflow-hidden">
      <div v-if="loading" class="py-20 text-center text-gray-500 dark:text-gray-400">加载中...</div>

      <div v-else-if="tags.length === 0" class="py-20 text-center text-gray-500 dark:text-gray-400">
        暂无标签
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-200 dark:border-gray-700">
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">名称</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">别名</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:80px">文章数</th>
              <th class="px-4 py-3 text-right text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:120px">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr
              v-for="tag in tags"
              :key="tag.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-800/50"
            >
              <td class="px-4 py-3 font-medium text-gray-900 dark:text-gray-100">{{ tag.name }}</td>
              <td class="px-4 py-3 text-gray-500 dark:text-gray-400 font-mono text-xs">{{ tag.slug }}</td>
              <td class="px-4 py-3 text-gray-500 dark:text-gray-400">{{ tag.postCount ?? 0 }}</td>
              <td class="px-4 py-3 text-right">
                <div class="flex items-center justify-end gap-1">
                  <button
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-primary-600 transition-colors"
                    title="编辑"
                    @click="openEdit(tag)"
                  >
                    <Pencil class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-red-600 transition-colors"
                    title="删除"
                    :disabled="deletingId === tag.id"
                    @click="handleDelete(tag.id)"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <Modal :visible="showModal" :title="editingTag ? '编辑标签' : '新建标签'" @close="showModal = false">
      <form class="space-y-4" @submit.prevent="handleSave">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">名称</label>
          <input
            v-model="form.name"
            type="text"
            required
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            @input="handleNameInput"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">别名 (Slug)</label>
          <input
            v-model="form.slug"
            type="text"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm font-mono focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>
      </form>
      <template #footer>
        <Button variant="secondary" @click="showModal = false">取消</Button>
        <Button :loading="saving" @click="handleSave">保存</Button>
      </template>
    </Modal>
  </div>
</template>
