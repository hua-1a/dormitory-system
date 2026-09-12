<template>
  <el-container class="layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="aside">
      <div class="logo">
        <span class="logo-icon">🏠</span>
        <span class="logo-text">宿舍管理系统</span>
      </div>
      <el-menu
        :default-active="activePath"
        router
        background-color="#001529"
        text-color="#a6adb4"
        active-text-color="#409eff"
        class="menu"
      >
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">
          <el-icon><component :is="m.icon" /></el-icon>
          <span>{{ m.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-title">{{ currentTitle }}</div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="avatar">{{ avatarText }}</el-avatar>
              <span class="user-name">{{ user?.realName || user?.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="changePwd">
                  <el-icon><Key /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, Key, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const user = ref(JSON.parse(localStorage.getItem('dorm_user') || 'null'))

const menus = computed(() => {
  const root = router.options.routes.find((r) => r.path === '/')
  return (root?.children || [])
    .filter((c) => !c.meta?.hidden)
    .map((c) => ({
      path: '/' + c.path,
      title: c.meta?.title,
      icon: c.meta?.icon
    }))
})

const activePath = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')
const avatarText = computed(() => (user.value?.realName || user.value?.username || '管').charAt(0))

const handleCommand = (command) => {
  if (command === 'changePwd') {
    router.push('/change-password')
  }
  if (command === 'logout') {
    localStorage.removeItem('dorm_user')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}

.aside {
  background: #001529;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-icon {
  font-size: 22px;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
}

.menu {
  border-right: none;
}

.menu :deep(.el-menu-item) {
  height: 52px;
}

.menu :deep(.el-menu-item.is-active) {
  background: rgba(64, 158, 255, 0.12);
  border-right: 3px solid #409eff;
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 6px rgba(0, 21, 41, 0.08);
  z-index: 2;
}

.header-title {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #303133;
  outline: none;
}

.avatar {
  background: #409eff;
  color: #fff;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
}

.main {
  background: #f0f2f5;
  padding: 16px;
  overflow-y: auto;
}
</style>
