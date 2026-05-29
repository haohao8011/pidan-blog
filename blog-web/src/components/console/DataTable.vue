<script setup lang="ts" generic="T extends Record<string, unknown>">
export interface Column {
  key: string
  label: string
  width?: string
  align?: 'left' | 'center' | 'right'
}

interface Props {
  columns: Column[]
  data: T[]
  loading?: boolean
  rowKey?: string
}

withDefaults(defineProps<Props>(), {
  loading: false,
  rowKey: 'id',
})

defineSlots<{
  cell(key: string, row: T): unknown
  actions(row: T): unknown
  empty(): unknown
}>()
</script>

<template>
  <div class="overflow-x-auto">
    <table class="w-full text-sm">
      <thead>
        <tr class="border-b border-gray-200 dark:border-gray-700">
          <th
            v-for="col in columns"
            :key="col.key"
            :class="[
              'px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400',
              col.align === 'center' ? 'text-center' : '',
              col.align === 'right' ? 'text-right' : '',
            ]"
            :style="col.width ? { width: col.width } : {}"
          >
            {{ col.label }}
          </th>
          <th
            v-if="$slots.actions"
            class="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400"
            style="width: 120px;"
          >
            操作
          </th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
        <tr
          v-for="(row, index) in data"
          :key="(row[rowKey] as string) ?? index"
          class="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors"
        >
          <td
            v-for="col in columns"
            :key="col.key"
            :class="[
              'px-4 py-3 text-gray-700 dark:text-gray-300',
              col.align === 'center' ? 'text-center' : '',
              col.align === 'right' ? 'text-right' : '',
            ]"
          >
            <slot :name="col.key" :row="row" :value="row[col.key]">
              {{ row[col.key] }}
            </slot>
          </td>
          <td v-if="$slots.actions" class="px-4 py-3 text-right">
            <slot name="actions" :row="row" />
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="!loading && data.length === 0" class="py-12 text-center text-gray-500 dark:text-gray-400">
      <slot name="empty">
        暂无数据
      </slot>
    </div>

    <div v-if="loading" class="py-12 text-center text-gray-500 dark:text-gray-400">
      <div class="inline-flex items-center gap-2">
        <svg class="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
        加载中...
      </div>
    </div>
  </div>
</template>
