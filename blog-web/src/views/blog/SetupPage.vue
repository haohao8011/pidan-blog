<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSetupStatus, testDatabase, setupAdmin } from '@/api/admin'
import Button from '@/components/ui/Button.vue'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'
import { Database, User, Check, ArrowRight, ArrowLeft } from 'lucide-vue-next'

const router = useRouter()

const currentStep = ref(1)
const initialized = ref(false)
const checkingStatus = ref(true)
const testingDb = ref(false)
const submitting = ref(false)
const dbTestResult = ref<'success' | 'error' | null>(null)

// Step 1: Database config
const dbConfig = ref({
  port: 5432,
  username: 'pidan_blog',
  password: '',
  database: 'pidan_blog',
})

// Step 2: Admin config
const adminConfig = ref({
  username: 'admin',
  password: '',
  displayName: '管理员',
  blogTitle: 'PiDan Blog',
  blogDescription: '一个简洁优雅的博客系统',
})

onMounted(async () => {
  try {
    const res = await getSetupStatus()
    initialized.value = res.data.data.initialized
    if (initialized.value) {
      router.push('/')
    }
  } catch {
    // proceed to setup
  } finally {
    checkingStatus.value = false
  }
})

async function handleTestDatabase() {
  testingDb.value = true
  dbTestResult.value = null
  try {
    await testDatabase({
      port: dbConfig.value.port,
      username: dbConfig.value.username,
      password: dbConfig.value.password,
      database: dbConfig.value.database,
    })
    dbTestResult.value = 'success'
    setTimeout(() => {
      currentStep.value = 2
    }, 1000)
  } catch {
    dbTestResult.value = 'error'
  } finally {
    testingDb.value = false
  }
}

async function handleSetup() {
  if (!adminConfig.value.username || !adminConfig.value.password) return
  submitting.value = true
  try {
    await setupAdmin({
      username: adminConfig.value.username,
      password: adminConfig.value.password,
      displayName: adminConfig.value.displayName,
      blogTitle: adminConfig.value.blogTitle,
      blogDescription: adminConfig.value.blogDescription,
    })
    window.$toast?.success('初始化成功，请登录')
    router.push('/setup?login=1')
  } catch (err) {
    window.$toast?.error(err instanceof Error ? err.message : '初始化失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-950 px-4">
    <div v-if="checkingStatus" class="py-20">
      <LoadingSpinner size="lg" />
    </div>

    <div v-else class="w-full max-w-md">
      <!-- Header -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">
          PiDan Blog
        </h1>
        <p class="text-gray-500 dark:text-gray-400">
          首次使用，请完成以下配置
        </p>
      </div>

      <!-- Progress Steps -->
      <div class="flex items-center justify-center gap-2 mb-8">
        <div
          :class="[
            'flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-colors',
            currentStep >= 1
              ? 'bg-primary-600 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-600 dark:text-gray-400',
          ]"
        >
          <Database class="w-4 h-4" />
          数据库
        </div>
        <div :class="['w-8 h-px', currentStep >= 2 ? 'bg-primary-600' : 'bg-gray-300 dark:bg-gray-600']" />
        <div
          :class="[
            'flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-colors',
            currentStep >= 2
              ? 'bg-primary-600 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-600 dark:text-gray-400',
          ]"
        >
          <User class="w-4 h-4" />
          管理员
        </div>
      </div>

      <!-- Card -->
      <div class="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-8 shadow-lg">
        <!-- Step 1: Database -->
        <div v-if="currentStep === 1">
          <h2 class="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-6">
            数据库配置
          </h2>
          <form class="space-y-4" @submit.prevent="handleTestDatabase">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">端口</label>
              <input
                v-model.number="dbConfig.port"
                type="number"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">用户名</label>
              <input
                v-model="dbConfig.username"
                type="text"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">密码</label>
              <input
                v-model="dbConfig.password"
                type="password"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">数据库名</label>
              <input
                v-model="dbConfig.database"
                type="text"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>

            <!-- Test Result -->
            <div v-if="dbTestResult === 'success'" class="flex items-center gap-2 text-green-600 dark:text-green-400 text-sm">
              <Check class="w-4 h-4" />
              数据库连接成功，正在跳转...
            </div>
            <div v-else-if="dbTestResult === 'error'" class="text-red-600 dark:text-red-400 text-sm">
              数据库连接失败，请检查配置
            </div>

            <Button type="submit" :loading="testingDb" class="w-full">
              <template v-if="!testingDb">
                测试连接
                <ArrowRight class="w-4 h-4" />
              </template>
            </Button>
          </form>
        </div>

        <!-- Step 2: Admin -->
        <div v-if="currentStep === 2">
          <h2 class="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-6">
            创建管理员
          </h2>
          <form class="space-y-4" @submit.prevent="handleSetup">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">用户名</label>
              <input
                v-model="adminConfig.username"
                type="text"
                required
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">密码</label>
              <input
                v-model="adminConfig.password"
                type="password"
                required
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">显示名称</label>
              <input
                v-model="adminConfig.displayName"
                type="text"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">博客标题</label>
              <input
                v-model="adminConfig.blogTitle"
                type="text"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">博客描述</label>
              <textarea
                v-model="adminConfig.blogDescription"
                rows="3"
                class="w-full px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
              />
            </div>

            <div class="flex gap-3">
              <Button variant="secondary" @click="currentStep = 1">
                <ArrowLeft class="w-4 h-4" />
                上一步
              </Button>
              <Button type="submit" :loading="submitting" class="flex-1">
                完成设置
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
