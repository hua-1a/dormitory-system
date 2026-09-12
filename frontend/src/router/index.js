import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue'), meta: { title: '登录' } },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '首页看板', icon: 'HomeFilled' } },
      { path: 'students', component: () => import('../views/student/StudentList.vue'), meta: { title: '学生管理', icon: 'User' } },
      { path: 'dormitories', component: () => import('../views/dorm/DormitoryList.vue'), meta: { title: '宿舍楼栋', icon: 'OfficeBuilding' } },
      { path: 'rooms', component: () => import('../views/dorm/RoomList.vue'), meta: { title: '房间管理', icon: 'House' } },
      { path: 'checkins', component: () => import('../views/checkin/CheckInList.vue'), meta: { title: '入住管理', icon: 'Tickets' } },
      { path: 'repairs', component: () => import('../views/repair/RepairList.vue'), meta: { title: '报修管理', icon: 'Tools' } },
      { path: 'change-password', component: () => import('../views/ChangePassword.vue'), meta: { title: '修改密码', icon: 'Key', hidden: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('dorm_user')
  if (to.path !== '/login' && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router
