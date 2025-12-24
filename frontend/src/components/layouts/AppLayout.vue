<!--
  主应用布局
  包含导航栏、侧边栏和主内容区域
-->
<template>
  <v-layout>
    <!-- 应用栏 -->
    <v-app-bar
      :elevation="3"
      color="primary"
      dark
      app
      class="app-header"
    >
      <v-app-bar-nav-icon
        @click="drawer = !drawer"
      />
      
      <v-toolbar-title class="font-weight-bold">
        {{ app_title }}
      </v-toolbar-title>
      
      <v-spacer />

      <!-- 角色切换按钮 (开发模式) -->
      <!-- <v-chip
        v-if="is_dev_mode"
        :color="auth_store.is_admin ? 'error' : 'success'"
        class="mr-4"
        variant="flat"
        size="large"
        @click="toggle_admin"
        style="cursor: pointer; transition: all 0.3s;"
      >
        <v-icon start>{{ auth_store.is_admin ? 'mdi-shield-account' : 'mdi-account' }}</v-icon>
        {{ auth_store.is_admin ? '管理员模式' : '客户模式' }}
        <v-icon end>mdi-swap-horizontal</v-icon>
        <v-tooltip activator="parent" location="bottom">
          点击切换角色 (开发模式)
        </v-tooltip>
      </v-chip> -->

      <!-- 购物车图标 (仅客户模式) -->
      <v-btn
        v-if="!auth_store.is_admin"
        icon
        variant="tonal"
        color="accent-lighten-1"
        class="mr-2"
        to="/cart"
      >
        <v-badge
          :content="cart_store.total_items"
          :model-value="cart_store.total_items > 0"
          color="error"
        >
          <v-icon>mdi-cart</v-icon>
        </v-badge>
      </v-btn>

      <!-- 用户菜单 -->
      <v-menu offset-y>
        <template #activator="{ props }">
          <v-btn
            icon
            variant="tonal"
            color="accent-lighten-1"
            v-bind="props"
          >
            <v-avatar size="32">
              <v-icon>mdi-account</v-icon>
            </v-avatar>
          </v-btn>
        </template>
        
        <v-list>
          <v-list-item>
            <v-list-item-title class="font-weight-medium">
              {{ user_display_name }}
            </v-list-item-title>
            <v-list-item-subtitle>
              {{ user?.email }}
            </v-list-item-subtitle>
          </v-list-item>
          
          <v-divider />
          
          <v-list-item
            prepend-icon="mdi-account-circle"
            title="个人资料"
            @click="go_to_profile"
          />
          
          <v-list-item
            prepend-icon="mdi-cog"
            title="设置"
            @click="go_to_settings"
          />
          
          <v-divider />
          
          <v-list-item
            prepend-icon="mdi-logout"
            title="退出登录"
            @click="handle_logout"
          />
        </v-list>
      </v-menu>
    </v-app-bar>
    
    <!-- 侧边导航栏 -->
    <v-navigation-drawer
      v-model="drawer"
      :rail="rail"
      permanent
      app
    >
      <template #prepend>
        <div class="nav-header">
          <v-btn
            variant="text"
            :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'"
            class="nav-toggle-btn"
            @click="rail = !rail"
          />
        </div>
      </template>
      
      <v-divider />
      
      <v-list density="compact" nav>
        <v-list-item
          v-for="item in nav_items"
          :key="item.path"
          :prepend-icon="item.icon"
          :title="item.title"
          :value="item.path"
          :to="item.path"
          :active="$route.path === item.path"
        >
          <template v-if="('badge' in item) && (item as any).badge" #append>
            <v-badge
              :content="(item as any).badge"
              color="error"
              inline
            />
          </template>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    
    <!-- 主内容区域 -->
    <v-main>
      <v-container fluid class="pa-4 full-width-container">
        <transition
          name="page-transition"
          mode="out-in"
        >
          <router-view />
        </transition>
      </v-container>
    </v-main>
  </v-layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useCartStore } from '../../stores/cart'

const router = useRouter()
const auth_store = useAuthStore()
const cart_store = useCartStore()

// 应用标题
const app_title = import.meta.env.VITE_APP_TITLE || 'EasyBook'

// 开发模式标志
const is_dev_mode = ref(true) // 生产环境设置为 false

// 抽屉和边栏状态
const drawer = ref(true)
const rail = ref(false)

// 用户信息
const user = computed(() => auth_store.user)
const user_display_name = computed(() => auth_store.user_display_name)

// 导航菜单项 - 根据用户角色显示
const nav_items = computed(() => {
  const is_admin = auth_store.is_admin

  // 前台菜单 (所有用户)
  const customer_nav = [
    {
      title: '图书商城',
      icon: 'mdi-book-open-variant',
      path: '/books',
    },
    {
      title: '购物车',
      icon: 'mdi-cart',
      path: '/cart',
      badge: cart_store.total_items > 0 ? cart_store.total_items : undefined,
    },
    {
      title: '我的订单',
      icon: 'mdi-package-variant',
      path: '/orders',
    },
    {
      title: '账户管理',
      icon: 'mdi-account-cash',
      path: '/account',
    },
  ]

  // 后台菜单 (仅管理员)
  const admin_nav = [
    {
      title: '仪表板',
      icon: 'mdi-view-dashboard',
      path: '/dashboard',
    },
    {
      title: '图书管理',
      icon: 'mdi-book-open-variant',
      path: '/admin/books',
    },
    {
      title: '订单管理',
      icon: 'mdi-clipboard-text',
      path: '/admin/orders',
    },
    {
      title: '库存管理',
      icon: 'mdi-package-variant-closed',
      path: '/admin/inventory',
    },
    {
      title: '采购管理',
      icon: 'mdi-clipboard-list',
      path: '/admin/purchases',
    },
    {
      title: '供应商管理',
      icon: 'mdi-truck',
      path: '/admin/suppliers',
    },
    {
      title: '缺书管理',
      icon: 'mdi-alert-circle',
      path: '/admin/shortages',
    },
    {
      title: '客户管理',
      icon: 'mdi-account-group',
      path: '/admin/customers',
    },
    {
      title: '丛书管理',
      icon: 'mdi-book-multiple',
      path: '/admin/book-series',
    },
  ]

  // 根据角色返回不同菜单
  return is_admin ? admin_nav : customer_nav
})

// 导航方法
const go_to_profile = () => {
  router.push('/profile')
}

const go_to_settings = () => {
  router.push('/change-password')
}

// 退出登录
const handle_logout = () => {
  auth_store.logout()
  router.push('/auth/login')
}

// 切换管理员/客户模式 (仅开发模式)
const toggle_admin = () => {
  if (!auth_store.user) return

  // 切换 is_admin 标志
  const updated_user = {
    ...auth_store.user,
    is_admin: !auth_store.user.is_admin
  }

  auth_store.update_user(updated_user)

  // 切换后跳转到对应首页
  if (updated_user.is_admin) {
    router.push('/dashboard')
  } else {
    router.push('/books')
  }
}
</script>

<style scoped>
:deep(.v-navigation-drawer__content) {
  display: flex;
  flex-direction: column;
}

.app-header {
  background: linear-gradient(135deg, var(--primary-100) 0%, var(--accent-100) 100%) !important;
  backdrop-filter: blur(10px);
}

:deep(.v-navigation-drawer) {
  border-right: 1px solid var(--bg-300);
  background: var(--bg-100);
}

:deep(.v-list-item) {
  border-radius: 12px;
  margin: 4px 8px;
  transition: all 250ms ease;
  color: var(--text-200);
}

:deep(.v-list-item:hover) {
  background: var(--accent-200);
  transform: translateX(4px);
  color: var(--text-100);
}

:deep(.v-list-item--active) {
  background: var(--primary-100);
  color: white;
  border-left: 3px solid var(--accent-100);
}

:deep(.v-main) {
  background: var(--bg-100);
  overflow-y: auto;
}

.full-width-container {
  width: 100%;
  max-width: 100%;
}

.nav-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 12px;
  min-height: 56px;
}

.nav-toggle-btn {
  color: var(--accent-100) !important;
  transition: all 250ms ease;
}

.nav-toggle-btn:hover {
  background: var(--accent-200) !important;
  transform: scale(1.1);
}
</style>
