<script setup lang="ts">
import { ref, computed } from 'vue'
import type { Comment } from '@/types'
import { submitComment } from '@/api/post'
import { formatDateTime } from '@/utils/date'
import Button from '@/components/ui/Button.vue'

const props = defineProps<{
  postSlug: string
  comments: Comment[]
}>()

const emit = defineEmits<{
  (e: 'refresh'): void
}>()

const form = ref({
  nickname: localStorage.getItem('comment_nickname') || '',
  email: localStorage.getItem('comment_email') || '',
  content: '',
  parentId: undefined as string | undefined,
})

const submitting = ref(false)
const replyingTo = ref<string | null>(null)

function startReply(parentId: string) {
  form.value.parentId = parentId
  replyingTo.value = parentId
}

function cancelReply() {
  form.value.parentId = undefined
  replyingTo.value = null
}

async function handleSubmit() {
  if (!form.value.nickname.trim() || !form.value.content.trim()) return

  submitting.value = true
  try {
    localStorage.setItem('comment_nickname', form.value.nickname)
    if (form.value.email) {
      localStorage.setItem('comment_email', form.value.email)
    }

    await submitComment(props.postSlug, {
      nickname: form.value.nickname.trim(),
      email: form.value.email.trim() || undefined,
      content: form.value.content.trim(),
      parentId: form.value.parentId,
    })

    form.value.content = ''
    form.value.parentId = undefined
    replyingTo.value = null
    window.$toast?.success('评论提交成功，等待审核')
    emit('refresh')
  } catch (err) {
    window.$toast?.error(err instanceof Error ? err.message : '评论提交失败')
  } finally {
    submitting.value = false
  }
}

// Organize comments: top-level + nested replies
const topLevelComments = computed(() => props.comments.filter((c) => !c.parentId))

function getReplies(commentId: string): Comment[] {
  return props.comments.filter((c) => c.parentId === commentId)
}
</script>

<template>
  <div class="space-y-6">
    <!-- Comment Form -->
    <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-6">
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">
        {{ replyingTo ? '回复评论' : '发表评论' }}
      </h3>

      <div v-if="replyingTo" class="mb-4 p-3 bg-primary-50 dark:bg-primary-900/20 rounded-lg text-sm text-primary-700 dark:text-primary-300 flex items-center justify-between">
        <span>正在回复评论</span>
        <button class="text-primary-600 hover:text-primary-800 dark:text-primary-400" @click="cancelReply">
          取消
        </button>
      </div>

      <form class="space-y-4" @submit.prevent="handleSubmit">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <input
            v-model="form.nickname"
            type="text"
            placeholder="昵称 *"
            required
            class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
          <input
            v-model="form.email"
            type="email"
            placeholder="邮箱（选填）"
            class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>
        <textarea
          v-model="form.content"
          placeholder="写下你的评论..."
          rows="4"
          required
          class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
        />
        <div class="flex justify-end">
          <Button type="submit" :loading="submitting" :disabled="!form.nickname.trim() || !form.content.trim()">
            提交评论
          </Button>
        </div>
      </form>
    </div>

    <!-- Comments List -->
    <div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">
        评论 ({{ comments.length }})
      </h3>

      <div v-if="topLevelComments.length === 0" class="text-center py-8 text-gray-500 dark:text-gray-400 text-sm">
        暂无评论，来发表第一条评论吧
      </div>

      <div class="space-y-4">
        <div v-for="comment in topLevelComments" :key="comment.id" class="border border-gray-200 dark:border-gray-700 rounded-xl p-4">
          <!-- Comment header -->
          <div class="flex items-center gap-3 mb-3">
            <div class="w-8 h-8 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-sm font-medium text-primary-700 dark:text-primary-300">
              {{ comment.nickname.charAt(0).toUpperCase() }}
            </div>
            <div>
              <span class="text-sm font-medium text-gray-900 dark:text-gray-100">
                {{ comment.nickname }}
              </span>
              <span class="text-xs text-gray-500 dark:text-gray-400 ml-2">
                {{ formatDateTime(comment.createdAt) }}
              </span>
            </div>
          </div>

          <!-- Comment content -->
          <p class="text-gray-700 dark:text-gray-300 text-sm leading-relaxed mb-3 whitespace-pre-wrap">
            {{ comment.content }}
          </p>

          <!-- Reply button -->
          <button
            class="text-xs text-primary-600 dark:text-primary-400 hover:text-primary-800 dark:hover:text-primary-300"
            @click="startReply(comment.id)"
          >
            回复
          </button>

          <!-- Replies -->
          <div v-if="getReplies(comment.id).length > 0" class="mt-3 pl-4 border-l-2 border-gray-200 dark:border-gray-700 space-y-3">
            <div v-for="reply in getReplies(comment.id)" :key="reply.id">
              <div class="flex items-center gap-2 mb-1">
                <span class="text-sm font-medium text-gray-900 dark:text-gray-100">
                  {{ reply.nickname }}
                </span>
                <span class="text-xs text-gray-500 dark:text-gray-400">
                  {{ formatDateTime(reply.createdAt) }}
                </span>
              </div>
              <p class="text-gray-700 dark:text-gray-300 text-sm leading-relaxed whitespace-pre-wrap">
                {{ reply.content }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
