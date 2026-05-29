<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import Link from '@tiptap/extension-link'
import Placeholder from '@tiptap/extension-placeholder'
import type { Category, Tag } from '@/types'
import {
  getAdminPost,
  createPost,
  updatePost,
  getAdminCategories,
  getAdminTags,
} from '@/api/admin'
import Button from '@/components/ui/Button.vue'
import { generateSlug } from '@/utils/slug'
import {
  Bold,
  Italic,
  Strikethrough,
  Heading1,
  Heading2,
  Heading3,
  Code,
  List,
  ListOrdered,
  Quote,
  Minus,
  Link as LinkIcon,
  Image as ImageIcon,
  Undo,
  Redo,
  Eye,
  CodeSquare,
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const saving = ref(false)
const showPreview = ref(false)

const form = ref({
  title: '',
  slug: '',
  content: '',
  contentHtml: '',
  excerpt: '',
  coverImage: '',
  categoryId: '',
  tags: [] as string[],
  tagsInput: '',
  published: false,
  pinned: false,
})

const categories = ref<Category[]>([])
const availableTags = ref<Tag[]>([])

// Tiptap editor
const editor = useEditor({
  extensions: [
    StarterKit,
    Image.configure({ inline: true }),
    Link.configure({ openOnClick: false }),
    Placeholder.configure({
      placeholder: '开始写作...',
    }),
  ],
  content: '',
  onUpdate: ({ editor: e }) => {
    form.value.content = e.getText()
    form.value.contentHtml = e.getHTML()
  },
})

function handleTitleInput() {
  if (!isEdit.value && !form.value.slug) {
    form.value.slug = generateSlug(form.value.title)
  }
}

function handleTagsInput() {
  form.value.tags = form.value.tagsInput
    .split(',')
    .map((t) => t.trim())
    .filter(Boolean)
}

async function fetchPost() {
  if (!isEdit.value) return
  try {
    const res = await getAdminPost(route.params.id as string)
    const post = res.data.data
    form.value.title = post.title
    form.value.slug = post.slug
    form.value.content = post.content
    form.value.contentHtml = post.contentHtml
    form.value.excerpt = post.excerpt || ''
    form.value.coverImage = post.coverImage || ''
    form.value.categoryId = post.category?.id || ''
    form.value.tags = post.tags?.map((t) => t.name) || []
    form.value.tagsInput = form.value.tags.join(', ')
    form.value.published = post.published
    form.value.pinned = post.pinned

    if (editor.value) {
      editor.value.commands.setContent(post.contentHtml)
    }
  } catch {
    window.$toast?.error('加载文章失败')
    router.push('/console/posts')
  }
}

async function fetchMeta() {
  try {
    const [catRes, tagRes] = await Promise.all([
      getAdminCategories(),
      getAdminTags(),
    ])
    categories.value = catRes.data.data
    availableTags.value = tagRes.data.data
  } catch {
    // silently fail
  }
}

async function handleSave(publish: boolean) {
  if (!form.value.title.trim()) {
    window.$toast?.error('请输入文章标题')
    return
  }

  saving.value = true
  try {
    const data = {
      title: form.value.title.trim(),
      slug: form.value.slug || generateSlug(form.value.title),
      content: form.value.content,
      contentHtml: form.value.contentHtml,
      excerpt: form.value.excerpt,
      coverImage: form.value.coverImage,
      categoryId: form.value.categoryId || undefined,
      tagNames: form.value.tags,
      published: publish,
      pinned: form.value.pinned,
    }

    if (isEdit.value) {
      await updatePost(route.params.id as string, data as Parameters<typeof updatePost>[1])
      window.$toast?.success('文章已更新')
    } else {
      await createPost(data as Parameters<typeof createPost>[0])
      window.$toast?.success('文章已创建')
    }
    router.push('/console/posts')
  } catch (err) {
    window.$toast?.error(err instanceof Error ? err.message : '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await Promise.all([fetchMeta(), fetchPost()])
})
</script>

<template>
  <div class="max-w-4xl">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100">
        {{ isEdit ? '编辑文章' : '新建文章' }}
      </h1>
      <div class="flex items-center gap-2">
        <Button variant="ghost" @click="showPreview = !showPreview">
          <Eye class="w-4 h-4" />
          {{ showPreview ? '编辑' : '预览' }}
        </Button>
        <Button variant="secondary" @click="handleSave(false)" :loading="saving">
          保存草稿
        </Button>
        <Button @click="handleSave(true)" :loading="saving">
          {{ isEdit ? '更新发布' : '发布' }}
        </Button>
      </div>
    </div>

    <div class="space-y-6">
      <!-- Title -->
      <div>
        <input
          v-model="form.title"
          type="text"
          placeholder="文章标题"
          class="w-full text-2xl font-bold bg-transparent border-0 border-b-2 border-gray-200 dark:border-gray-700 py-3 px-0 text-gray-900 dark:text-gray-100 focus:outline-none focus:border-primary-500 placeholder-gray-400"
          @input="handleTitleInput"
        />
      </div>

      <!-- Slug -->
      <div>
        <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">URL 别名</label>
        <input
          v-model="form.slug"
          type="text"
          placeholder="article-slug"
          class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 font-mono"
        />
      </div>

      <!-- Editor / Preview -->
      <div>
        <div v-if="showPreview" class="prose bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-600 rounded-lg p-6 min-h-[400px]" v-html="form.contentHtml" />
        <div v-else class="border border-gray-300 dark:border-gray-600 rounded-lg overflow-hidden">
          <!-- Toolbar -->
          <div class="flex flex-wrap items-center gap-0.5 px-3 py-2 bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="加粗"
              @click="editor?.chain().focus().toggleBold().run()"
            >
              <Bold class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="斜体"
              @click="editor?.chain().focus().toggleItalic().run()"
            >
              <Italic class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="删除线"
              @click="editor?.chain().focus().toggleStrike().run()"
            >
              <Strikethrough class="w-4 h-4" />
            </button>
            <div class="w-px h-5 bg-gray-300 dark:bg-gray-600 mx-1" />
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="标题1"
              @click="editor?.chain().focus().toggleHeading({ level: 1 }).run()"
            >
              <Heading1 class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="标题2"
              @click="editor?.chain().focus().toggleHeading({ level: 2 }).run()"
            >
              <Heading2 class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="标题3"
              @click="editor?.chain().focus().toggleHeading({ level: 3 }).run()"
            >
              <Heading3 class="w-4 h-4" />
            </button>
            <div class="w-px h-5 bg-gray-300 dark:bg-gray-600 mx-1" />
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="无序列表"
              @click="editor?.chain().focus().toggleBulletList().run()"
            >
              <List class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="有序列表"
              @click="editor?.chain().focus().toggleOrderedList().run()"
            >
              <ListOrdered class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="引用"
              @click="editor?.chain().focus().toggleBlockquote().run()"
            >
              <Quote class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="行内代码"
              @click="editor?.chain().focus().toggleCode().run()"
            >
              <Code class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="代码块"
              @click="editor?.chain().focus().toggleCodeBlock().run()"
            >
              <CodeSquare class="w-4 h-4" />
            </button>
            <div class="w-px h-5 bg-gray-300 dark:bg-gray-600 mx-1" />
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="分割线"
              @click="editor?.chain().focus().setHorizontalRule().run()"
            >
              <Minus class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="链接"
              @click="() => {
                const url = window.prompt('输入链接地址：')
                if (url) editor?.chain().focus().setLink({ href: url }).run()
              }"
            >
              <LinkIcon class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="图片"
              @click="() => {
                const url = window.prompt('输入图片地址：')
                if (url) editor?.chain().focus().setImage({ src: url }).run()
              }"
            >
              <ImageIcon class="w-4 h-4" />
            </button>
            <div class="w-px h-5 bg-gray-300 dark:bg-gray-600 mx-1" />
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="撤销"
              @click="editor?.chain().focus().undo().run()"
            >
              <Undo class="w-4 h-4" />
            </button>
            <button
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400 transition-colors"
              title="重做"
              @click="editor?.chain().focus().redo().run()"
            >
              <Redo class="w-4 h-4" />
            </button>
          </div>

          <!-- Editor Content -->
          <EditorContent
            :editor="editor"
            class="prose min-h-[400px] p-6 focus:outline-none"
          />
        </div>
      </div>

      <!-- Settings Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Cover Image -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">封面图片 URL</label>
          <input
            v-model="form.coverImage"
            type="text"
            placeholder="https://example.com/cover.jpg"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
          <div v-if="form.coverImage" class="mt-2 rounded-lg overflow-hidden border border-gray-200 dark:border-gray-700">
            <img :src="form.coverImage" alt="cover" class="w-full h-32 object-cover" />
          </div>
        </div>

        <!-- Category -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">分类</label>
          <select
            v-model="form.categoryId"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="">无分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
        </div>

        <!-- Tags -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">标签（逗号分隔）</label>
          <input
            v-model="form.tagsInput"
            type="text"
            placeholder="Vue, TypeScript, Spring Boot"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
            @blur="handleTagsInput"
          />
        </div>

        <!-- Excerpt -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">摘要</label>
          <textarea
            v-model="form.excerpt"
            rows="3"
            placeholder="文章摘要（可选）"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
          />
        </div>
      </div>

      <!-- Options -->
      <div class="flex items-center gap-6">
        <label class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300 cursor-pointer">
          <input
            v-model="form.pinned"
            type="checkbox"
            class="w-4 h-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500"
          />
          置顶文章
        </label>
      </div>
    </div>
  </div>
</template>

<style>
.tiptap p.is-editor-empty:first-child::before {
  color: #9ca3af;
  content: attr(data-placeholder);
  float: left;
  height: 0;
  pointer-events: none;
}
.dark .tiptap p.is-editor-empty:first-child::before {
  color: #6b7280;
}
</style>
