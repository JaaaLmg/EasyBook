/**
 * API接口定义
 * 定义所有后端API接口的调用方法 - 网上书店管理系统
 */
import api_client from './client'
import type {
  ApiResponse,
  PaginatedResponse,
  User,
  Book,
  BookDetail,
  Order,
  OrderWithDetails,
  CartSummary,
  CartItem,
  CreditRule,
  InventoryInfo,
  OutOfStockRecord,
  PurchaseOrder,
  DashboardData,
  Category,
  Publisher
} from '@/types'

// ============ 用户认证相关接口 ============
export const auth_api = {
  // 用户登录
  login: (username: string, password: string) =>
    api_client.post<ApiResponse<{ access_token: string; user: User }>>('/auth/login', { username, password }),

  // 用户注册
  register: (data: {
    username: string
    email: string
    password: string
    real_name: string
    phone?: string
    address?: string
  }) =>
    api_client.post<ApiResponse<{ customer_id: string; username: string }>>('/auth/register', data),

  // 获取用户资料
  get_profile: () =>
    api_client.get<ApiResponse<User>>('/auth/profile'),

  // 更新用户资料
  update_profile: (data: {
    real_name?: string
    email?: string
    phone?: string
    address?: string
  }) =>
    api_client.put<ApiResponse<User>>('/auth/profile', data),

  // 修改密码
  change_password: (data: {
    current_password: string
    new_password: string
  }) =>
    api_client.post<ApiResponse<null>>('/auth/change-password', data),

  // 删除账户（需要验证密码）
  delete_account: (password: string) =>
    api_client.delete<ApiResponse<null>>('/auth/profile', { data: { password } }),
}

// ============ 图书相关接口 ============
export const book_api = {
  // 获取图书列表
  get_books: (params?: {
    keyword?: string
    category_id?: string
    publisher_id?: string
    min_price?: number
    max_price?: number
    status?: string
    sort?: string
    page?: number
    page_size?: number
  }) =>
    api_client.get<ApiResponse<PaginatedResponse<Book>>>('/books', { params }),

  // 获取图书详情
  get_book: (isbn: string) =>
    api_client.get<ApiResponse<BookDetail>>(`/books/${isbn}`),

  // 全文搜索
  search_books: (keyword: string, page?: number, page_size?: number) =>
    api_client.get<ApiResponse<PaginatedResponse<Book>>>('/books/search', {
      params: { q: keyword, page, page_size }
    }),

  // 新增图书 (管理员)
  create_book: (data: any) =>
    api_client.post<ApiResponse<Book>>('/books', data),

  // 更新图书 (管理员)
  update_book: (isbn: string, data: any) =>
    api_client.put<ApiResponse<Book>>(`/books/${isbn}`, data),

  // 删除图书 (管理员)
  delete_book: (isbn: string) =>
    api_client.delete<ApiResponse<null>>(`/books/${isbn}`),

  // 获取分类列表
  get_categories: () =>
    api_client.get<ApiResponse<Category[]>>('/categories'),

  // 获取出版社列表
  get_publishers: () =>
    api_client.get<ApiResponse<Publisher[]>>('/publishers'),
}

// ============ 购物车相关接口 ============
export const cart_api = {
  // 获取购物车
  get_cart: () =>
    api_client.get<ApiResponse<CartSummary>>('/cart'),

  // 添加商品到购物车
  add_item: (isbn: string, quantity: number) =>
    api_client.post<ApiResponse<CartItem>>('/cart/items', { isbn, quantity }),

  // 更新购物车商品数量
  update_quantity: (item_id: string, quantity: number) =>
    api_client.put<ApiResponse<CartItem>>(`/cart/items/${item_id}`, { quantity }),

  // 移除购物车商品
  remove_item: (item_id: string) =>
    api_client.delete<ApiResponse<null>>(`/cart/items/${item_id}`),

  // 清空购物车
  clear_cart: () =>
    api_client.delete<ApiResponse<null>>('/cart'),
}

// ============ 订单相关接口 ============
export const order_api = {
  // 获取订单列表
  get_orders: (params?: {
    status?: string
    start_date?: string
    end_date?: string
    page?: number
    page_size?: number
    all?: boolean
  }) =>
    api_client.get<ApiResponse<PaginatedResponse<Order>>>('/orders', { params }),

  // 获取订单详情
  get_order: (order_id: string) =>
    api_client.get<ApiResponse<{ order: Order; details: OrderDetail[]; delivery?: DeliveryOrder }>>(`/orders/${order_id}`),

  // 创建订单 (从购物车结算)
  create_order: (data: {
    shipping_address: string
    recipient_name: string
    recipient_phone: string
    payment_method?: string
    notes?: string
  }) =>
    api_client.post<ApiResponse<{ order_id: string; order_no: string; actual_amount: number }>>('/orders', data),

  // 支付订单
  pay_order: (order_id: string, payment_method: string) =>
    api_client.post<ApiResponse<{ order_status: string; payment_time: string }>>(`/orders/${order_id}/pay`, { payment_method }),

  // 取消订单
  cancel_order: (order_id: string, reason?: string) =>
    api_client.post<ApiResponse<null>>(`/orders/${order_id}/cancel`, { reason }),

  // 删除订单（客户）
  delete_order: (order_id: string) =>
    api_client.delete<ApiResponse<null>>(`/orders/${order_id}`),

  // 确认收货
  confirm_delivery: (order_id: string) =>
    api_client.post<ApiResponse<null>>(`/orders/${order_id}/confirm`),

  // 发货 (管理员)
  ship_order: (order_id: string, data: {
    delivery_company: string
    tracking_no: string
  }) =>
    api_client.post<ApiResponse<{ delivery_id: string; delivery_no: string }>>(`/admin/orders/${order_id}/ship`, data),
  
  // 删除订单（管理员）
  admin_delete_order: (order_id: string) =>
    api_client.delete<ApiResponse<null>>(`/admin/orders/${order_id}`),
}

// ============ 账户相关接口 ============
export const account_api = {
  // 账户充值
  recharge: (amount: number, payment_method: string) =>
    api_client.post<ApiResponse<null>>('/account/recharge', { amount, payment_method }),

  // 查询余额
  get_balance: () =>
    api_client.get<ApiResponse<{
      account_balance: number
      credit_level: number
      overdraft_limit: number
      available_balance: number
    }>>('/account/balance'),

  // 信用等级信息
  get_credit_info: () =>
    api_client.get<ApiResponse<{
      current_level: number
      level_name: string
      discount_rate: number
      overdraft_enabled: boolean
      overdraft_limit: number
      total_purchase: number
      next_level?: {
        level: number
        required_purchase: number
        remaining: number
      }
    }>>('/account/credit-info'),

  // 交易记录
  get_transactions: (params?: {
    page?: number
    type?: string
    start_date?: string
    end_date?: string
  }) =>
    api_client.get<ApiResponse<PaginatedResponse<any>>>('/account/transactions', { params }),
}

// ============ 库存相关接口 (管理员) ============
export const inventory_api = {
  // 库存列表
  get_inventory: (params?: {
    status?: string
    keyword?: string
    sort?: string
    page?: number
    page_size?: number
  }) =>
    api_client.get<ApiResponse<PaginatedResponse<InventoryInfo>>>('/admin/inventory', { params }),

  // 库存详情
  get_inventory_detail: (isbn: string) =>
    api_client.get<ApiResponse<any>>(`/admin/inventory/${isbn}`),

  // 更新库存
  update_inventory: (isbn: string, data: {
    operation: 'in' | 'out' | 'check'
    quantity: number
    reason: string
    location_code?: string
  }) =>
    api_client.put<ApiResponse<null>>(`/admin/inventory/${isbn}`, data),

  // 低库存预警列表
  get_low_stock: () =>
    api_client.get<ApiResponse<InventoryInfo[]>>('/admin/inventory/low-stock'),
}

// ============ 缺书相关接口 ============
export const shortage_api = {
  // 缺书记录列表 (管理员)
  get_shortages: (params?: {
    source_type?: string
    status?: string
    priority?: number
    keyword?: string
    page?: number
    page_size?: number
  }) =>
    api_client.get<ApiResponse<PaginatedResponse<OutOfStockRecord>>>('/admin/out-of-stock', { params }),

  // 登记缺书 (客户/管理员)
  register_shortage: (data: {
    isbn: string
    required_quantity: number
    remark?: string
    priority?: number
    supplier_id?: string
    source_type?: 'manual' | 'customer'
  }) =>
    api_client.post<ApiResponse<OutOfStockRecord>>('/out-of-stock', data),

  // 更新缺书记录 (管理员)
  update_shortage: (record_id: string, data: {
    status?: string
    priority?: number
    required_quantity?: number
  }) =>
    api_client.put<ApiResponse<null>>(`/admin/out-of-stock/${record_id}`, data),

  // 发送到货通知
  send_notification: (record_id: string) =>
    api_client.post<ApiResponse<null>>(`/admin/out-of-stock/${record_id}/notify`),

  // 扫描库存生成缺书记录 (管理员)
  scan_shortages: (min_safety?: number) =>
    api_client.post<ApiResponse<null>>('/admin/out-of-stock/scan', min_safety ? { min_safety } : {}),
}

// ============ 采购相关接口 (管理员) ============
export const purchase_api = {
  // 采购单列表
  get_purchases: (params?: {
    status?: string
    supplier_id?: string
    start_date?: string
    end_date?: string
    page?: number
    page_size?: number
  }) =>
    api_client.get<ApiResponse<PaginatedResponse<PurchaseOrder>>>('/admin/purchases', { params }),

  // 创建采购单
  create_purchase: (data: any) =>
    api_client.post<ApiResponse<{ purchase_id: string; purchase_no: string }>>('/admin/purchases', data),

  // 采购单详情
  get_purchase: (purchase_id: string) =>
    api_client.get<ApiResponse<any>>(`/admin/purchases/${purchase_id}`),

  // 审批采购单
  approve_purchase: (purchase_id: string, data: {
    approved: boolean
    comment?: string
  }) =>
    api_client.put<ApiResponse<null>>(`/admin/purchases/${purchase_id}/approve`, data),

  // 收货登记
  receive_purchase: (purchase_id: string, data: any) =>
    api_client.post<ApiResponse<null>>(`/admin/purchases/${purchase_id}/receive`, data),
}

// ============ 供应商相关接口 (管理员) ============
export const supplier_api = {
  // 供应商列表
  get_suppliers: (params?: { keyword?: string }) =>
    api_client.get<ApiResponse<Supplier[]>>('/admin/suppliers', { params }),
  // 新增供应商
  create_supplier: (data: Partial<Supplier>) =>
    api_client.post<ApiResponse<Supplier>>('/admin/suppliers', data),
  // 更新供应商
  update_supplier: (supplier_id: string, data: Partial<Supplier>) =>
    api_client.put<ApiResponse<Supplier>>(`/admin/suppliers/${supplier_id}`, data),
  // 删除供应商
  delete_supplier: (supplier_id: string) =>
    api_client.delete<ApiResponse<null>>(`/admin/suppliers/${supplier_id}`),
  // 供应商图书
  get_supplier_books: (supplier_id: string) =>
    api_client.get<ApiResponse<any[]>>(`/admin/suppliers/${supplier_id}/books`),
  upsert_supplier_book: (supplier_id: string, data: {
    isbn: string
    supply_price: number
    min_order_quantity?: number
    delivery_days?: number
    is_available?: boolean
  }) =>
    api_client.post<ApiResponse<any>>(`/admin/suppliers/${supplier_id}/books`, data),
}

// ============ 统计相关接口 (管理员) ============
export const stats_api = {
  // 仪表板数据
  get_dashboard: () =>
    api_client.get<ApiResponse<DashboardData>>('/admin/stats/dashboard'),

  // 销售统计
  get_sales_stats: (params?: {
    start_date?: string
    end_date?: string
    group_by?: string
    dimension?: string
  }) =>
    api_client.get<ApiResponse<any>>('/admin/stats/sales', { params }),

  // 客户统计
  get_customer_stats: (params?: {
    start_date?: string
    end_date?: string
  }) =>
    api_client.get<ApiResponse<any>>('/admin/stats/customers', { params }),

  // 库存统计
  get_inventory_stats: () =>
    api_client.get<ApiResponse<any>>('/admin/stats/inventory'),
}

// ============ 丛书相关接口 ============
export const book_series_api = {
  // 获取丛书列表
  get_series: (params?: { keyword?: string; publisherId?: string }) =>
    api_client.get<ApiResponse<any[]>>('/admin/book-series', { params }),

  // 获取丛书详情
  get_series_detail: (seriesId: string) =>
    api_client.get<ApiResponse<any>>(`/admin/book-series/${seriesId}`),

  // 获取丛书包含的图书
  get_series_books: (seriesId: string) =>
    api_client.get<ApiResponse<Book[]>>(`/admin/book-series/${seriesId}/books`),

  // 创建丛书
  create_series: (data: {
    series_name: string
    publisher_id: string
    total_books?: number
    description?: string
  }) =>
    api_client.post<ApiResponse<any>>('/admin/book-series', data),

  // 更新丛书
  update_series: (seriesId: string, data: {
    series_name?: string
    publisher_id?: string
    total_books?: number
    description?: string
  }) =>
    api_client.put<ApiResponse<any>>(`/admin/book-series/${seriesId}`, data),

  // 删除丛书
  delete_series: (seriesId: string) =>
    api_client.delete<ApiResponse<null>>(`/admin/book-series/${seriesId}`),
}

// ============ 系统相关接口 ============
export const system_api = {
  // 健康检查
  health_check: () =>
    api_client.get<ApiResponse<{ status: string }>>('/system/health'),

  // 获取信用等级规则
  get_credit_rules: () =>
    api_client.get<ApiResponse<CreditRule[]>>('/system/credit-rules'),
}
