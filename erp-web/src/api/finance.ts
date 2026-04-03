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

// ==================== 账户相关 ====================

export interface FinanceAccount {
  id: number
  accountNo: string
  accountName: string
  accountType: string
  bankName: string
  currency: string
  balance: number
  status: number
  isDefault: number
  createTime: string
}

export const financeAccountApi = {
  // 获取账户列表
  getAccounts: () =>
    request.get<any, FinanceAccount[]>('/finance/account/list'),

  // 创建账户
  createAccount: (data: Partial<FinanceAccount>) =>
    request.post<any, FinanceAccount>('/finance/account', data),

  // 更新账户
  updateAccount: (data: Partial<FinanceAccount>) =>
    request.put<any, FinanceAccount>('/finance/account', data),

  // 删除账户
  deleteAccount: (id: number) =>
    request.delete<any, void>(`/finance/account/${id}`),

  // 设置默认账户
  setDefault: (id: number) =>
    request.post<any, void>(`/finance/account/${id}/default`)
}

// ==================== 收支记录相关 ====================

export interface FinanceFlow {
  id: number
  recordNo: string
  type: string
  amount: number
  accountId: number
  accountName: string
  categoryId: number
  categoryName: string
  orderNo: string
  remark: string
  createTime: string
  createBy: string
}

export interface FinanceFlowQuery {
  current: number
  size: number
  accountId?: number
  type?: string
  categoryId?: number
  startDate?: string
  endDate?: string
  keyword?: string
}

export interface FinanceStatistics {
  totalIncome: number
  totalExpense: number
  profit: number
  recordCount: number
}

export interface TrendData {
  date: string
  income: number
  expense: number
}

export interface CategoryData {
  categoryName: string
  amount: number
  type: string
}

export const financeFlowApi = {
  // 获取统计概览
  getStatistics: (params?: { startDate?: string; endDate?: string }) =>
    request.get<any, FinanceStatistics>('/finance/flow/statistics', { params }),

  // 分页查询流水
  getFlows: (params: FinanceFlowQuery) =>
    request.get<any, { current: number; size: number; total: number; records: FinanceFlow[] }>('/finance/flow/list', { params }),

  // 获取流水详情
  getFlowDetail: (id: number) =>
    request.get<any, FinanceFlow>(`/finance/flow/${id}`),

  // 创建流水
  createFlow: (data: Partial<FinanceFlow>) =>
    request.post<any, FinanceFlow>('/finance/flow', data),

  // 更新流水
  updateFlow: (data: Partial<FinanceFlow>) =>
    request.put<any, FinanceFlow>('/finance/flow', data),

  // 删除流水
  deleteFlow: (id: number) =>
    request.delete<any, void>(`/finance/flow/${id}`),

  // 获取趋势数据
  getTrendData: (days: number) =>
    request.get<any, TrendData[]>('/finance/flow/trend', { params: { days } }),

  // 获取分类统计数据
  getCategoryData: (type?: string) =>
    request.get<any, CategoryData[]>('/finance/flow/category', { params: { type } })
}

// ==================== 分类相关 ====================

export interface FinanceCategory {
  id: number
  name: string
  type: string
  parentId: number
  sort: number
  status: number
}

export const financeCategoryApi = {
  // 获取分类列表
  getCategories: (type?: string) =>
    request.get<any, FinanceCategory[]>('/finance/category/list', { params: { type } }),

  // 创建分类
  createCategory: (data: Partial<FinanceCategory>) =>
    request.post<any, FinanceCategory>('/finance/category', data),

  // 更新分类
  updateCategory: (data: Partial<FinanceCategory>) =>
    request.put<any, FinanceCategory>('/finance/category', data),

  // 删除分类
  deleteCategory: (id: number) =>
    request.delete<any, void>(`/finance/category/${id}`)
}