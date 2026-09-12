import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 响应拦截：统一解包 Result { code, msg, data }
request.interceptors.response.use(
  (res) => {
    const r = res.data
    if (r.code === 200) {
      return r.data
    }
    ElMessage.error(r.msg || '请求失败')
    return Promise.reject(new Error(r.msg || '请求失败'))
  },
  (err) => {
    ElMessage.error(err.response?.data?.msg || err.message || '网络异常，请稍后重试')
    return Promise.reject(err)
  }
)

export default request
