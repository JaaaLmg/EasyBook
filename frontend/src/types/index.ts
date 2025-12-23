/**
 * 类型定义 - 网上书店管理系统
 */

// ============ 用户相关 ============
export interface User {
  customer_id: string
  username: string
  email: string
  real_name: string
  phone?: string
  address?: string
  account_balance: number
  credit_level: number
  total_purchase: number
  registration_date: string
  last_login?: string
  account_status: 'active' | 'frozen' | 'closed'
  is_admin: boolean
}

export interface CreditRule {
  level: number
  level_name: string
  discount_rate: number
  overdraft_enabled: boolean
  overdraft_limit?: number
  min_balance?: number
  min_total_purchase?: number
  description?: string
}

// ============ 图书相关 ============
export interface Book {
  isbn: string
  title: string
  edition?: string
  publisher_id: string
  publish_date?: string
  price: number
  page_count?: number
  format?: string
  language: string
  description?: string
  table_of_contents?: string
  cover_image?: string
  series_id?: string
  book_type: 'normal' | 'ebook' | 'audio'
  status: 'active' | 'inactive' | 'out_of_print' | 'out_of_stock'
  create_time: string
  update_time: string
}

export interface BookDetail extends Book {
  authors: Author[]
  publisher: Publisher
  categories: Category[]
  keywords: Keyword[]
  series?: BookSeries
  inventory: InventoryInfo
}

export interface Author {
  author_id: string
  author_name: string
  nationality?: string
  birth_date?: string
  biography?: string
  create_time: string
}

export interface Publisher {
  publisher_id: string
  publisher_name: string
  address?: string
  contact_phone?: string
  contact_person?: string
  email?: string
  website?: string
  description?: string
}

export interface Category {
  category_id: string
  category_name: string
  parent_id?: string
  level: number
  display_order: number
  description?: string
}

export interface Keyword {
  keyword_id: string
  keyword: string
  search_count: number
}

export interface BookSeries {
  series_id: string
  series_name: string
  publisher_id?: string
  total_books?: number
  description?: string
}

// ============ 库存相关 ============
export interface InventoryInfo {
  inventory_id: string
  isbn: string
  quantity: number
  safety_stock: number
  reserved_quantity: number
  available_quantity: number
  location_code?: string
  warehouse: string
  last_restock?: string
  last_check: string
  status: 'normal' | 'low' | 'out_of_stock'
}

// ============ 订单相关 ============
export interface Order {
  order_id: string
  order_no: string
  customer_id: string
  total_amount: number
  discount_amount: number
  actual_amount: number
  shipping_fee: number
  order_status: 'pending' | 'paid' | 'processing' | 'shipped' | 'delivered' | 'cancelled' | 'refunded'
  shipping_address: string
  recipient_name: string
  recipient_phone: string
  payment_method?: string
  payment_time?: string
  order_time: string
  shipping_time?: string
  delivery_time?: string
  notes?: string
}

export interface OrderDetail {
  detail_id: string
  order_id: string
  isbn: string
  quantity: number
  unit_price: number
  discount_rate: number
  subtotal: number
  book_title?: string
  book_cover?: string
}

export interface OrderWithDetails extends Order {
  customer: {
    customer_id: string
    real_name: string
    phone: string
  }
  details: OrderDetail[]
  delivery?: DeliveryOrder
}

// ============ 购物车相关 ============
export interface CartItem {
  item_id: string
  isbn: string
  title: string
  cover_image?: string
  authors: string
  unit_price: number
  quantity: number
  discount_rate: number
  subtotal: number
  available_stock: number
}

export interface CartSummary {
  items: CartItem[]
  total_items: number
  total_amount: number
  discount_amount: number
  final_amount: number
  discount_rate: number
}

// ============ 发货相关 ============
export interface DeliveryOrder {
  delivery_id: string
  order_id: string
  delivery_no: string
  delivery_company: string
  tracking_no?: string
  delivery_address: string
  delivery_status: 'preparing' | 'shipped' | 'in_transit' | 'delivered' | 'returned'
  shipping_fee: number
  weight?: number
  package_count: number
  prepare_time?: string
  ship_time?: string
  expected_arrival?: string
  actual_arrival?: string
  notes?: string
}

// ============ 缺书相关 ============
export interface OutOfStockRecord {
  record_id: string
  isbn: string
  book_title?: string
  publisher_name?: string
  required_quantity: number
  customer_id?: string
  customer_email?: string
  customer_phone?: string
  source_type: 'manual' | 'auto' | 'customer'
  priority: number
  status: 'pending' | 'processing' | 'resolved' | 'cancelled'
  notify_status: 'not_required' | 'pending' | 'sent' | 'failed'
  notify_time?: string
  created_time: string
  resolve_time?: string
  remark?: string
}

// ============ 采购相关 ============
export interface PurchaseOrder {
  purchase_id: string
  purchase_no: string
  supplier_id: string
  total_amount: number
  status: 'draft' | 'submitted' | 'approved' | 'ordered' | 'received' | 'cancelled'
  order_date?: string
  expected_delivery?: string
  actual_delivery?: string
  create_time: string
  create_by: string
  approve_time?: string
  approve_by?: string
}

export interface PurchaseDetail {
  detail_id: string
  purchase_id: string
  isbn: string
  quantity: number
  unit_price: number
  received_quantity: number
  status: 'pending' | 'partial' | 'complete'
}

export interface Supplier {
  supplier_id: string
  supplier_name: string
  contact_person: string
  contact_phone: string
  email: string
  address: string
  bank_account?: string
  tax_number?: string
  cooperation_status: 'active' | 'suspended' | 'terminated'
  rating?: number
  create_time: string
  update_time: string
}

// ============ API响应相关 ============
export interface ApiResponse<T> {
  code: number
  message: string
  data?: T
  timestamp: string
}

export interface PaginatedResponse<T> {
  items: T[]
  total: number
  page: number
  page_size: number
}

// ============ 统计相关 ============
export interface DashboardData {
  today_sales: number
  today_orders: number
  pending_orders: number
  low_stock_count: number
  pending_shortage_count: number
  sales_trend: { date: string; amount: number }[]
  top_books: { isbn: string; title: string; sales: number }[]
}
