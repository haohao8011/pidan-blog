<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Category } from '@/types'
import {
  getAdminCategories,
  createCategory,
  updateCategory,
  deleteCategory,
} from '@/api/admin'
import { generateSlug } from '@/utils/slug'
import Button from '@/components/ui/Button.vue'
import Modal from '@/components/console/Modal.vue'
import { Plus, Pencil, Trash2 } from 'lucide-vue-next'

const categories = ref<Category[]>([])
const loading = ref(true)
const showModal = ref(false)
const editingCategory = ref<Category | null>(null)
const saving = ref(false)
const deletingId = ref<string | null>(null)

const form = ref({
  name: '',
  slug: '',
  description: '',
  sortOrder: 0,
})

async function fetchCategories() {
  loading.value = true
  try {
    const res = await getAdminCategories()
    categories.value = res.data.data
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingCategory.value = null
  form.value = { name: '', slug: '', description: '', sortOrder: 0 }
  showModal.value = true
}

function openEdit(cat: Category) {
  editingCategory.value = cat
  form.value = {
    name: cat.name,
    slug: cat.slug,
    description: cat.description || '',
    sortOrder: cat.sortOrder,
  }
  showModal.value = true
}

function handleNameInput() {
  if (!editingCategory.value) {
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
      description: form.value.description,
      sortOrder: form.value.sortOrder,
    }
    if (editingCategory.value) {
      await updateCategory(editingCategory.value.id, data)
      window.$toast?.success('分类已更新')
    } else {
      await createCategory(data)
      window.$toast?.success('分类已创建')
    }
    showModal.value = false
    fetchCategories()
  } catch (err) {
    window.$toast?.error(err instanceof Error ? err.message : '操作失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: string) {
  deletingId.value = id
  try {
    await deleteCategory(id)
    fetchCategories()
    window.$toast?.success('分类已删除')
  } catch {
    window.$toast?.error('删除失败')
  } finally {
    deletingId.value = null
  }
}

onMounted(fetchCategories)
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100">分类管理</h1>
      <Button @click="openCreate">
        <Plus class="w-4 h-4" />
        新建分类
      </Button>
    </div>

    <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 overflow-hidden">
      <div v-if="loading" class="py-20 text-center text-gray-500 dark:text-gray-400">加载中...</div>

      <div v-else-if="categories.length === 0" class="py-20 text-center text-gray-500 dark:text-gray-400">
        暂无分类
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-gray-200 dark:border-gray-700">
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">名称</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">别名</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400">描述</th>
              <th class="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:80px">排序</th>
              <th class="px-4 py-3 text-right text-xs font-semibold uppercase text-gray-500 dark:text-gray-400" style="width:120px">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr
              v-for="cat in categories"
              :key="cat.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-800/50"
            >
              <td class="px-4 py-3 font-medium text-gray-900 dark:text-gray-100">{{ cat.name }}</td>
              <td class="px-4 py-3 text-gray-500 dark:text-gray-400 font-mono text-xs">{{ cat.slug }}</td>
              <td class="px-4 py-3 text-gray-600 dark:text-gray-400 text-xs line-clamp-1">{{ cat.description || '-' }}</td>
              <td class="px-4 py-3 text-gray-500 dark:text-gray-400">{{ cat.sortOrder }}</td>
              <td class="px-4 py-3 text-right">
                <div class="flex items-center justify-end gap-1">
                  <button
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-primary-600 transition-colors"
                    title="编辑"
                    @click="openEdit(cat)"
                  >
                    <Pencil class="w-4 h-4" />
                  </button>
                  <button
                    class="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 text-gray-500 hover:text-red-600 transition-colors"
                    title="删除"
                    :disabled="deletingId === cat.id"
                    @click="handleDelete(cat.id)"
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
    <Modal :visible="showModal" :title="editingCategory ? '编辑分类' : '新建分类'" @close="showModal = false">
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
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">描述</label>
          <textarea
            v-model="form.description"
            rows="3"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">排序</label>
          <input
            v-model.number="form.sortOrder"
            type="number"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
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
