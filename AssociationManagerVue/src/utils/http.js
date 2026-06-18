/*
 * HTTP 请求工具
 * - 自动附带 Token
 * - 401 自动跳转登录
 * - 统一错误处理
 */
import axios from 'axios'
import qs from 'qs'
import { Message } from 'element-ui'
import router from '../router/index'

axios.defaults.headers['Content-Type'] = 'application/x-www-form-urlencoded';

const service = axios.create({
    baseURL: 'http://localhost:9211/association',
    timeout: 15000
})

// 请求拦截器：自动附带 Token + POST 数据序列化
service.interceptors.request.use(config => {
    const token = sessionStorage.getItem('token')

    if (config.method === 'get') {
        if (token) {
            config.params = { ...config.params, token }
        }
    } else if (config.method === 'post') {
        // 自动附带 token（如果有）
        if (token && typeof config.data === 'object' && config.data !== null) {
            config.data.token = token
        }
        // POST 数据必须序列化为 form-urlencoded 格式（无论有无 token）
        if (typeof config.data === 'object' && config.data !== null) {
            config.data = qs.stringify(config.data, { indices: false })
        }
    }
    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截器：处理错误码和 401
service.interceptors.response.use(
    resp => {
        const data = resp.data
        if (data.code == 0 || data.code == 1) {
            return data
        } else if (data.code == 2) {
            Message({ message: data.msg, type: 'error', center: true })
            return Promise.reject(data)
        } else {
            return data
        }
    },
    error => {
        if (error.response) {
            const status = error.response.status
            if (status === 401) {
                Message({ message: '登录已过期，请重新登录', type: 'error', center: true, duration: 3000 })
                sessionStorage.clear()
                router.push('/')
            } else if (status >= 500) {
                Message({ message: '服务器繁忙，请稍后重试', type: 'error', center: true, duration: 3000 })
            } else {
                Message({ message: '请求失败，请检查网络', type: 'error', center: true, duration: 3000 })
            }
        } else {
            Message({ message: '网络连接异常', type: 'error', center: true, duration: 3000 })
        }
        return Promise.reject(error)
    }
)

export default service
