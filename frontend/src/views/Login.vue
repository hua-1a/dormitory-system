<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">🏠</div>
        <div class="login-title">宿舍管理系统</div>
        <div class="login-subtitle">Dormitory Management System</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>

      <div class="login-tip">默认账号：admin &nbsp;|&nbsp; 密码：123456</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const user = await request.post('/login', form)
      localStorage.setItem('dorm_user', JSON.stringify(user))
      ElMessage.success('登录成功，欢迎回来！')
      router.push('/dashboard')
    } catch (e) {
      // 错误提示已在拦截器处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 45%, #409eff 100%);
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
  top: -180px;
  right: -120px;
}

.login-page::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  bottom: -160px;
  left: -100px;
}

.login-card {
  width: 400px;
  padding: 40px 36px 28px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 16px 48px rgba(0, 21, 41, 0.35);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-logo {
  font-size: 44px;
  line-height: 1;
  margin-bottom: 10px;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2d3d;
  letter-spacing: 2px;
}

.login-subtitle {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
  letter-spacing: 1px;
}

.login-btn {
  width: 100%;
  margin-top: 6px;
  letter-spacing: 8px;
  font-weight: 600;
}

.login-tip {
  margin-top: 18px;
  text-align: center;
  font-size: 12px;
  color: #a8abb2;
}
</style>
