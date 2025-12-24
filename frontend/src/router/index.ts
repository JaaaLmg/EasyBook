import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// 布局组件
import AppLayout from '../components/layouts/AppLayout.vue'
import AuthLayout from '../components/layouts/AuthLayout.vue'

// 认证页面
import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'
import ChangePasswordView from '../views/auth/ChangePasswordView.vue'

// 主页面
import DashboardView from '../views/dashboard/DashboardView.vue'
import ProfileView from '../views/ProfileView.vue'

// 前台页面 (客户)
import BooksView from '../views/books/BooksView.vue'
import BookDetailView from '../views/books/BookDetailView.vue'
import CartView from '../views/cart/CartView.vue'
import OrdersView from '../views/orders/OrdersView.vue'
import OrderDetailView from '../views/orders/OrderDetailView.vue'
import AccountView from '../views/account/AccountView.vue'

// 后台页面 (管理员)
import AdminOrdersView from '../views/admin/AdminOrdersView.vue'
import AdminBooksView from '../views/admin/AdminBooksView.vue'
import InventoryView from '../views/admin/InventoryView.vue'
import AdminPurchasesView from '../views/admin/AdminPurchasesView.vue'
import SuppliersView from '../views/admin/SuppliersView.vue'
import AdminShortagesView from '../views/admin/AdminShortagesView.vue'
import AdminCustomersView from '../views/admin/AdminCustomersView.vue'
import BookSeriesView from '../views/admin/BookSeriesView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/auth/login',
        },
        {
            path: '/auth',
            component: AuthLayout,
            children: [
                {
                    path: 'login',
                    name: 'Login',
                    component: LoginView,
                    meta: { requiresGuest: true },
                },
                {
                    path: 'register',
                    name: 'Register',
                    component: RegisterView,
                    meta: { requiresGuest: true },
                },
            ],
        },
        {
            path: '/',
            component: AppLayout,
            meta: { requiresAuth: true },
            children: [
                // 前台 - 图书浏览
                {
                    path: 'books',
                    name: 'Books',
                    component: BooksView,
                    meta: { title: '图书商城' },
                },
                {
                    path: 'books/:isbn',
                    name: 'BookDetail',
                    component: BookDetailView,
                    meta: { title: '图书详情' },
                },
                // 前台 - 购物车
                {
                    path: 'cart',
                    name: 'Cart',
                    component: CartView,
                    meta: { title: '购物车' },
                },
                // 前台 - 订单
                {
                    path: 'orders',
                    name: 'Orders',
                    component: OrdersView,
                    meta: { title: '我的订单' },
                },
                {
                    path: 'orders/:orderId',
                    name: 'OrderDetail',
                    component: OrderDetailView,
                    meta: { title: '订单详情' },
                },
                // 前台 - 账户
                {
                    path: 'account',
                    name: 'Account',
                    component: AccountView,
                    meta: { title: '账户管理' },
                },
                // 个人资料
                {
                    path: 'profile',
                    name: 'Profile',
                    component: ProfileView,
                    meta: { title: '个人资料' },
                },
                // 修改密码
                {
                    path: 'change-password',
                    name: 'ChangePassword',
                    component: ChangePasswordView,
                    meta: { title: '修改密码' },
                },
                // 后台 - 仪表板
                {
                    path: 'dashboard',
                    name: 'Dashboard',
                    component: DashboardView,
                    meta: { title: '仪表板', requiresAdmin: true },
                },
                // 后台 - 图书管理
                {
                    path: 'admin/books',
                    name: 'AdminBooks',
                    component: AdminBooksView,
                    meta: { title: '图书管理', requiresAdmin: true },
                },
                // 后台 - 订单管理
                {
                    path: 'admin/orders',
                    name: 'AdminOrders',
                    component: AdminOrdersView,
                    meta: { title: '订单管理', requiresAdmin: true },
                },
                {
                    path: 'admin/orders/:orderId',
                    name: 'AdminOrderDetail',
                    component: () => import('../views/admin/AdminOrderDetailView.vue'),
                    meta: { title: '订单详情', requiresAdmin: true },
                },
                // 后台 - 库存管理
                {
                    path: 'admin/inventory',
                    name: 'Inventory',
                    component: InventoryView,
                    meta: { title: '库存管理', requiresAdmin: true },
                },
                {
                    path: 'admin/purchases',
                    name: 'AdminPurchases',
                    component: AdminPurchasesView,
                    meta: { title: '采购管理', requiresAdmin: true },
                },
                {
                    path: 'admin/suppliers',
                    name: 'Suppliers',
                    component: SuppliersView,
                    meta: { title: '供应商管理', requiresAdmin: true },
                },
                {
                    path: 'admin/shortages',
                    name: 'AdminShortages',
                    component: AdminShortagesView,
                    meta: { title: '缺书管理', requiresAdmin: true },
                },
                // 后台 - 客户管理
                {
                    path: 'admin/customers',
                    name: 'AdminCustomers',
                    component: AdminCustomersView,
                    meta: { title: '客户管理', requiresAdmin: true },
                },
                // 后台 - 丛书管理
                {
                    path: 'admin/book-series',
                    name: 'BookSeries',
                    component: BookSeriesView,
                    meta: { title: '丛书管理', requiresAdmin: true },
                },
            ],
        },
    ],
})

// 路由守卫 - 认证检查
router.beforeEach((to, from, next) => {
    const auth_store = useAuthStore()

    // 检查是否需要登录
    if (to.meta.requiresAuth && !auth_store.is_authenticated) {
        next('/auth/login')
        return
    }

    // 检查是否需要游客状态（已登录用户不能访问登录页）
    if (to.meta.requiresGuest && auth_store.is_authenticated) {
        next('/books')
        return
    }

    // 检查是否需要管理员权限
    if (to.meta.requiresAdmin && !auth_store.is_admin) {
        alert('需要管理员权限')
        next('/auth/login')
        return
    }

    next()
})

export default router
