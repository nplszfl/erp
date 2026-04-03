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

export interface Supplier {
  id: number
  name: string
  contactPerson: string
  phone: string
  email: string
  type: string
  country: string
  city: string
  address: string
  category: string
  cooperationStartAt: string
  totalPurchaseAmount: number
  totalOrders: number
  rating: number
  level: string
  remark: string
  status: string
  createTime: string
  updateTime: string
}

export interface PurchaseOrder {
  id: number
  orderNo: string
  supplierId: number
  supplierName: string
  status: string
  totalAmount: number
  paidAmount: number
  currency: string
  expectedArrivalAt: string
  actualArrivalAt: string
  paymentMethod: string
  paymentStatus: string
  purchaserId: number
  purchaserName: string
  remark: string
  createTime: string
  items?: PurchaseOrderItem[]
}

export interface PurchaseOrderItem {
  id: number
  orderId: number
  productName: string
  sku: string
  quantity: number
  unitPrice: number
  totalAmount: number
}

export interface SupplierEvaluation {
  id: number
  supplierId: number
  orderId: number
  qualityScore: number
  deliveryScore: number
  priceScore: number
  serviceScore: number
  overallScore: number
  comment: string
  evaluateBy: string
  evaluateAt: string
}

export interface SupplierStats {
  totalSuppliers: number
  activeSuppliers: number
  avgRating: number
  totalPurchaseAmount: number
}

export interface PageResult<T> {
  current: number
  size: number
  total: number
  records: T[]
}

export const supplierApi = {
  // ========== 供应商管理 ==========
  
  // 创建供应商
  createSupplier: (data: Supplier) => 
    request.post<any, number>('/supplier/supplier/create', data),

  // 获取供应商详情
  getSupplierById: (id: number) => 
    request.get<any, Supplier>(`/supplier/supplier/${id}`),

  // 分页查询供应商
  getSupplierList: (params: {
    current: number
    size: number
    keyword?: string
    type?: string
    level?: string
    country?: string
    status?: string
  }) => request.get<any, PageResult<Supplier>>('/supplier/supplier/list', { params }),

  // 获取供应商统计
  getSupplierStats: () => 
    request.get<any, SupplierStats>('/supplier/supplier/stats'),

  // 更新供应商
  updateSupplier: (id: number, data: Supplier) => 
    request.put<any, void>(`/supplier/supplier/${id}`, data),

  // 删除供应商
  deleteSupplier: (id: number) => 
    request.delete<any, void>(`/supplier/supplier/${id}`),

  // ========== 采购订单管理 ==========

  // 创建采购订单
  createPurchaseOrder: (data: PurchaseOrder) => 
    request.post<any, number>('/supplier/purchase-order/create', data),

  // 获取采购订单详情
  getPurchaseOrderById: (id: number) => 
    request.get<any, PurchaseOrder>(`/supplier/purchase-order/${id}`),

  // 分页查询采购订单
  getPurchaseOrderList: (params: {
    current: number
    size: number
    supplierId?: number
    status?: string
    paymentStatus?: string
  }) => request.get<any, PageResult<PurchaseOrder>>('/supplier/purchase-order/list', { params }),

  // 更新采购订单状态
  updatePurchaseOrderStatus: (id: number, status: string) => 
    request.put<any, void>(`/supplier/purchase-order/${id}/status`, null, { params: { status } }),

  // 删除采购订单
  deletePurchaseOrder: (id: number) => 
    request.delete<any, void>(`/supplier/purchase-order/${id}`),

  // ========== 供应商绩效评估 ==========

  // 获取供应商绩效评估列表
  getEvaluationList: (supplierId: number) => 
    request.get<any, SupplierEvaluation[]>(`/supplier/evaluation/${supplierId}/list`),

  // 创建供应商评估
  createEvaluation: (data: SupplierEvaluation) => 
    request.post<any, number>('/supplier/evaluation/create', data),

  // 获取供应商评分统计
  getEvaluationStats: (supplierId: number) => 
    request.get<any, { avgQuality: number, avgDelivery: number, avgPrice: number, avgService: number, avgOverall: number }>(
      `/supplier/evaluation/${supplierId}/stats`
    )
}