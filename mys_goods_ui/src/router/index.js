import {
  createRouter,
  // createWebHistory,
  createWebHashHistory,
} from "vue-router";

const routes = [{
  path: '/',
  name: 'index',
  meta: {
    requiresAuth: true,
    title: '首页'
  },
  component: () => import('@/views/index'),
  children: [{
      path: "/user",
      name: "userInfo",
      meta: {
        requiresAuth: true,
        activeId: "userInfo",
        title: "用户列表"
      },
      component: () =>
        import(`@/views/mys/UserInfo.vue`)
    },
    {
      path: "/addExchange",
      name: "addExchange",
      meta: {
        requiresAuth: true,
        activeId: "addExchange",
        title: "添加兑换"
      },
      component: () =>
        import(`@/views/mys/AddExchange.vue`)
    },
    {
      path: "/exchange",
      name: "exchangeInfo",
      meta: {
        requiresAuth: true,
        activeId: "exchangeInfo",
        title: "兑换列表"
      },
      component: () =>
        import(`@/views/mys/ExchangeInfo.vue`)
    },
    {
      path: "/website",
      name: "website",
      meta: {
        requiresAuth: true,
        activeId: "website",
        title: "站点配置"
      },
      component: () =>
        import(`@/views/book/WebsiteList.vue`)
    },
    {
      path: "/book",
      name: "bookList",
      meta: {
        requiresAuth: true,
        activeId: "bookList",
        title: "小说列表"
      },
      component: () =>
        import(`@/views/book/BookList.vue`)
    },
    {
      name: "404",
      path: '/404',
      meta: {
        title: '404'
      },
      component: () =>
        import('@/views/exception/404')
    },
  ]
}];

const router = createRouter({
  // history: createWebHistory(),
  history: createWebHashHistory(),
  routes,
});

// 当一个导航触发时，全局的 before 钩子按照创建顺序调用。钩子是异步解析执行，此时导航在所有钩子 resolve 完之前一直处于等待中。
router.beforeEach((to, _from, next) => {
  if (to.matched.length === 0) {
    next({
      path: "/404",
    });
    return;
  }
  // // 自动化修改页面标签的 title
  // if (to.meta.title) {
  //   document.title = to.meta.title;
  // }
  next();
});


export default router;