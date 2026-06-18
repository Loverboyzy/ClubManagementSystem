import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const router = new VueRouter({
  routes: [
    {
      path: '/',
      name: 'login',
      component: require("../views/login.vue").default
    },
    {
      path: '/index',
      name: 'index',
      component: require("../views/home.vue").default,
      meta: { requiresAuth: true }
    }
  ]
});

// 路由守卫：权限校验
router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token')

  // 需要登录的页面
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next({ path: '/', query: { redirect: to.fullPath } })
    } else {
      next()
    }
  } else if (to.path === '/' && token) {
    // 已登录用户访问登录页 → 跳转首页
    next('/index')
  } else {
    next()
  }
})

export default router
