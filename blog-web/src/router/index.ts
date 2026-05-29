import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  },
  routes: [
    {
      path: '/',
      component: () => import('@/components/blog/BlogLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/blog/HomePage.vue'),
        },
        {
          path: 'posts/:slug',
          name: 'post-detail',
          component: () => import('@/views/blog/PostDetailPage.vue'),
        },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('@/views/blog/CategoryListPage.vue'),
        },
        {
          path: 'categories/:slug',
          name: 'category-posts',
          component: () => import('@/views/blog/CategoryPostsPage.vue'),
        },
        {
          path: 'tags',
          name: 'tags',
          component: () => import('@/views/blog/TagListPage.vue'),
        },
        {
          path: 'tags/:slug',
          name: 'tag-posts',
          component: () => import('@/views/blog/TagPostsPage.vue'),
        },
        {
          path: 'archive',
          name: 'archive',
          component: () => import('@/views/blog/ArchivePage.vue'),
        },
        {
          path: 'about',
          name: 'about',
          component: () => import('@/views/blog/AboutPage.vue'),
        },
      ],
    },
    {
      path: '/setup',
      component: () => import('@/views/blog/SetupPage.vue'),
    },
    {
      path: '/console',
      component: () => import('@/views/console/ConsoleLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'console-dashboard',
          component: () => import('@/views/console/ConsoleDashboard.vue'),
        },
        {
          path: 'posts',
          name: 'console-posts',
          component: () => import('@/views/console/PostList.vue'),
        },
        {
          path: 'posts/new',
          name: 'console-post-new',
          component: () => import('@/views/console/PostEdit.vue'),
        },
        {
          path: 'posts/:id/edit',
          name: 'console-post-edit',
          component: () => import('@/views/console/PostEdit.vue'),
        },
        {
          path: 'comments',
          name: 'console-comments',
          component: () => import('@/views/console/CommentList.vue'),
        },
        {
          path: 'categories',
          name: 'console-categories',
          component: () => import('@/views/console/CategoryManage.vue'),
        },
        {
          path: 'tags',
          name: 'console-tags',
          component: () => import('@/views/console/TagManage.vue'),
        },
        {
          path: 'settings',
          name: 'console-settings',
          component: () => import('@/views/console/SettingsPage.vue'),
        },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    const token = document.cookie
      .split('; ')
      .find((row) => row.startsWith('pidan-blog-token='))
    if (!token) {
      next({ path: '/setup', query: { redirect: to.fullPath } })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
