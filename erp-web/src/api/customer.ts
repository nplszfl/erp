import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.response.use(
  response => response.data,
  error => {
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export interface Customer {
  id: number
  name: string
  email: string
  phone: string
  platform: string
  platformCustomerId: string
  country: string
  state: string
  city: string
  address: string
  postalCode: string
  level: string
  totalOrders: number
  totalSpent: number
  avgOrderValue: number
  lastOrderAt: string
  remark: string
  status: string
  createTime: string
  updateTime: string
}

export interface CustomerStats {
  totalCustomers: number
  vipCount: number
  activeCount: number
  totalRevenue: number
  avgSpent: number
}

export interface PageResult<T> {
  current: number
  size: number
  total: number
  records: T[]
}

export const customerApi = {
  // 创建客户
  create: (data: Customer) => 
    request.post<any, number>('/customer/customer/create', data),

  // 获取客户详情
  getById: (id: number) => 
    request.get<any, Customer>(`/customer/customer/${id}`),

  // 分页查询客户
  getList: (params: {
    current: number
    size: number
    keyword?: string
    platform?: string
    level?: string
    country?: string
  }) => request.get<any, PageResult<Customer>>('/customer/customer/list', { params }),

  // 获取客户统计
  getStats: () => 
    request.get<any, CustomerStats>('/customer/customer/stats'),

  // 获取VIP客户
  getVipList: () => 
    request.get<any, Customer[]>('/customer/customer/vip'),

  // 更新客户
  update: (id: number, data: Customer) => 
    request.put<any, void>(`/customer/customer/${id}`, data),

  // 删除客户
  delete: (id: number) => 
    request.delete<any, void>(`/customer/customer/${id}`)
}