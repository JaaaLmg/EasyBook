<!--
  管理员仪表盘
  展示系统关键指标和数据统计
-->
<template>
  <v-container fluid class="pa-4">
    <!-- 页面标题 -->
    <v-row class="mb-4">
      <v-col cols="12">
        <div class="d-flex align-center justify-space-between">
          <div>
            <h1 class="text-h4 font-weight-bold">
              <v-icon size="32" class="mr-2">mdi-view-dashboard</v-icon>
              管理仪表盘
            </h1>
            <p class="text-body-2 text-grey mt-1">
              欢迎回来！这是您的系统概览
            </p>
          </div>
          <v-btn
            color="primary"
            prepend-icon="mdi-refresh"
            variant="tonal"
            @click="load_stats"
          >
            刷新数据
          </v-btn>
        </div>
      </v-col>
    </v-row>

    <!-- 核心指标卡片 -->
    <v-row class="mb-4">
      <v-col cols="12" sm="6" md="3">
        <MetricCard
          title="今日订单"
          :value="stats.today_orders || 0"
          subtitle="待处理订单"
          icon="mdi-cart-outline"
          icon-color="primary"
          :loading="loading"
        />
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <MetricCard
          title="今日销售额"
          :value="'¥' + (stats.today_sales || '0.00')"
          subtitle="已完成订单"
          icon="mdi-currency-cny"
          icon-color="success"
          :loading="loading"
        />
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <MetricCard
          title="今日新客户"
          :value="stats.today_new_customers || 0"
          subtitle="新注册用户"
          icon="mdi-account-plus"
          icon-color="info"
          :loading="loading"
        />
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <MetricCard
          title="库存预警"
          :value="stats.low_stock_count || 0"
          subtitle="低于安全库存"
          icon="mdi-alert-circle"
          :icon-color="(stats.low_stock_count || 0) > 0 ? 'error' : 'grey'"
          :loading="loading"
          clickable
          @click="$router.push('/admin/inventory')"
        />
      </v-col>
    </v-row>

    <!-- 总体统计 -->
    <v-row class="mb-4">
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="d-flex align-center justify-space-between">
            <div>
              <div class="text-caption text-grey">总订单数</div>
              <div class="text-h5 font-weight-bold mt-1">{{ stats.total_orders || 0 }}</div>
            </div>
            <v-icon size="32" color="primary">mdi-clipboard-text-outline</v-icon>
          </div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="d-flex align-center justify-space-between">
            <div>
              <div class="text-caption text-grey">总销售额</div>
              <div class="text-h5 font-weight-bold mt-1">¥{{ stats.total_sales || '0.00' }}</div>
            </div>
            <v-icon size="32" color="success">mdi-cash-multiple</v-icon>
          </div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="d-flex align-center justify-space-between">
            <div>
              <div class="text-caption text-grey">总客户数</div>
              <div class="text-h5 font-weight-bold mt-1">{{ stats.total_customers || 0 }}</div>
            </div>
            <v-icon size="32" color="info">mdi-account-group</v-icon>
          </div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2" class="pa-4">
          <div class="d-flex align-center justify-space-between">
            <div>
              <div class="text-caption text-grey">缺书待处理</div>
              <div class="text-h5 font-weight-bold mt-1">{{ stats.pending_shortages || 0 }}</div>
            </div>
            <v-icon size="32" color="warning">mdi-book-alert</v-icon>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 图表和列表 -->
    <v-row>
      <!-- 订单状态分布 -->
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-chart-donut</v-icon>
            订单状态分布
          </v-card-title>
          <v-divider />
          <v-card-text>
            <div v-if="!loading && stats.order_status_stats" class="py-4">
              <PieChart
                :data="order_status_chart_data"
                :labels="order_status_labels"
                :colors="order_status_colors"
                height="300"
              />
            </div>
            <div v-else-if="loading" class="text-center py-8">
              <v-progress-circular indeterminate color="primary" />
            </div>
            <div v-else class="text-center text-grey py-8">
              暂无数据
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 近7天销售趋势 -->
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-chart-line</v-icon>
            近7天销售趋势
          </v-card-title>
          <v-divider />
          <v-card-text>
            <div v-if="!loading && stats.sales_trend" class="py-4">
              <LineChart
                :labels="sales_trend_labels"
                :data="sales_trend_data"
                label="销售额 (¥)"
                color="#4CAF50"
                height="300"
              />
            </div>
            <div v-else-if="loading" class="text-center py-8">
              <v-progress-circular indeterminate color="primary" />
            </div>
            <div v-else class="text-center text-grey py-8">
              暂无数据
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 信用等级分布 -->
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-chart-bar</v-icon>
            客户信用等级分布
          </v-card-title>
          <v-divider />
          <v-card-text>
            <div v-if="!loading && stats.credit_level_stats" class="py-4">
              <BarChart
                :labels="credit_level_labels"
                :data="credit_level_data"
                label="客户数量"
                color="#2196F3"
                height="300"
              />
            </div>
            <div v-else-if="loading" class="text-center py-8">
              <v-progress-circular indeterminate color="primary" />
            </div>
            <div v-else class="text-center text-grey py-8">
              暂无数据
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 最近订单 -->
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center justify-space-between">
            <div>
              <v-icon class="mr-2">mdi-format-list-bulleted</v-icon>
              最近订单
            </div>
            <v-btn
              size="small"
              variant="text"
              color="primary"
              @click="$router.push('/admin/orders')"
            >
              查看全部
              <v-icon end>mdi-arrow-right</v-icon>
            </v-btn>
          </v-card-title>
          <v-divider />
          <v-card-text class="pa-0">
            <v-list v-if="recent_orders.length > 0">
              <v-list-item
                v-for="order in recent_orders"
                :key="order.order_id"
                @click="$router.push(`/admin/orders/${order.order_id}`)"
              >
                <template #prepend>
                  <v-icon :color="get_order_status_color(order.order_status)">
                    mdi-package-variant
                  </v-icon>
                </template>
                <v-list-item-title>{{ order.order_no }}</v-list-item-title>
                <v-list-item-subtitle>
                  {{ format_date(order.order_time) }} | ¥{{ order.actual_amount?.toFixed(2) }}
                </v-list-item-subtitle>
                <template #append>
                  <v-chip
                    size="small"
                    :color="get_order_status_color(order.order_status)"
                    variant="tonal"
                  >
                    {{ get_order_status_name(order.order_status) }}
                  </v-chip>
                </template>
              </v-list-item>
            </v-list>
            <div v-else-if="loading" class="text-center py-8">
              <v-progress-circular indeterminate color="primary" size="32" />
            </div>
            <div v-else class="text-center text-grey py-8">
              暂无订单
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 库存预警 -->
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center justify-space-between">
            <div>
              <v-icon class="mr-2" color="error">mdi-alert</v-icon>
              库存预警
            </div>
            <v-btn
              size="small"
              variant="text"
              color="primary"
              @click="$router.push('/admin/inventory')"
            >
              查看全部
              <v-icon end>mdi-arrow-right</v-icon>
            </v-btn>
          </v-card-title>
          <v-divider />
          <v-card-text class="pa-0">
            <v-list v-if="low_stock_items.length > 0">
              <v-list-item
                v-for="item in low_stock_items"
                :key="item.isbn"
              >
                <template #prepend>
                  <v-icon color="error">mdi-alert-circle</v-icon>
                </template>
                <v-list-item-title>{{ item.isbn }}</v-list-item-title>
                <v-list-item-subtitle>
                  当前库存: {{ item.quantity }} | 安全库存: {{ item.safety_stock }}
                </v-list-item-subtitle>
                <template #append>
                  <v-chip size="small" color="error" variant="tonal">
                    缺{{ item.shortage }}
                  </v-chip>
                </template>
              </v-list-item>
            </v-list>
            <div v-else-if="loading" class="text-center py-8">
              <v-progress-circular indeterminate color="primary" size="32" />
            </div>
            <div v-else class="text-center text-success py-8">
              <v-icon size="48" color="success">mdi-check-circle</v-icon>
              <div class="mt-2">库存充足</div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 热销图书 -->
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2" color="warning">mdi-fire</v-icon>
            热销图书 Top 5
          </v-card-title>
          <v-divider />
          <v-card-text class="pa-0">
            <v-list v-if="top_books.length > 0">
              <v-list-item
                v-for="(book, index) in top_books"
                :key="book.isbn"
              >
                <template #prepend>
                  <v-avatar :color="get_rank_color(index)" size="32">
                    <span class="text-white font-weight-bold">{{ index + 1 }}</span>
                  </v-avatar>
                </template>
                <v-list-item-title>{{ book.isbn }}</v-list-item-title>
                <v-list-item-subtitle>
                  销量: {{ book.sales_count }} 本
                </v-list-item-subtitle>
                <template #append>
                  <v-icon color="warning">mdi-trophy</v-icon>
                </template>
              </v-list-item>
            </v-list>
            <div v-else-if="loading" class="text-center py-8">
              <v-progress-circular indeterminate color="primary" size="32" />
            </div>
            <div v-else class="text-center text-grey py-8">
              暂无数据
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 快捷操作 -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title>
            <v-icon class="mr-2">mdi-lightning-bolt</v-icon>
            快捷操作
          </v-card-title>
          <v-divider />
          <v-card-text>
            <v-row>
              <v-col cols="6" sm="4" md="2">
                <v-btn
                  block
                  color="primary"
                  variant="tonal"
                  prepend-icon="mdi-clipboard-text"
                  to="/admin/orders"
                >
                  订单管理
                </v-btn>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <v-btn
                  block
                  color="success"
                  variant="tonal"
                  prepend-icon="mdi-package-variant-closed"
                  to="/admin/inventory"
                >
                  库存管理
                </v-btn>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <v-btn
                  block
                  color="info"
                  variant="tonal"
                  prepend-icon="mdi-account-group"
                  to="/admin/customers"
                >
                  客户管理
                </v-btn>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <v-btn
                  block
                  color="warning"
                  variant="tonal"
                  prepend-icon="mdi-clipboard-list"
                  to="/admin/purchases"
                >
                  采购管理
                </v-btn>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <v-btn
                  block
                  color="purple"
                  variant="tonal"
                  prepend-icon="mdi-truck"
                  to="/admin/suppliers"
                >
                  供应商
                </v-btn>
              </v-col>
              <v-col cols="6" sm="4" md="2">
                <v-btn
                  block
                  color="error"
                  variant="tonal"
                  prepend-icon="mdi-alert-circle"
                  to="/admin/shortages"
                >
                  缺书管理
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/client'
import MetricCard from '@/components/common/MetricCard.vue'
import PieChart from '@/components/common/PieChart.vue'
import LineChart from '@/components/common/LineChart.vue'
import BarChart from '@/components/common/BarChart.vue'

const router = useRouter()

// 数据状态
const loading = ref(false)
const stats = ref<any>({})
const recent_orders = ref<any[]>([])
const low_stock_items = ref<any[]>([])
const top_books = ref<any[]>([])

// 订单状态图表数据
const order_status_chart_data = computed(() => {
  const statusStats = stats.value.order_status_stats || {}
  return [
    statusStats.pending || 0,
    statusStats.paid || 0,
    statusStats.processing || 0,
    statusStats.shipped || 0,
    statusStats.delivered || 0,
    statusStats.cancelled || 0
  ]
})

const order_status_labels = ['待支付', '已支付', '处理中', '已发货', '已送达', '已取消']
const order_status_colors = ['#FFC107', '#2196F3', '#9C27B0', '#4CAF50', '#8BC34A', '#F44336']

// 销售趋势图表数据
const sales_trend_labels = computed(() => {
  const trend = stats.value.sales_trend || []
  return trend.map((item: any) => {
    const date = new Date(item.date)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })
})

const sales_trend_data = computed(() => {
  const trend = stats.value.sales_trend || []
  return trend.map((item: any) => parseFloat(item.sales))
})

// 信用等级图表数据
const credit_level_labels = ['一级会员', '二级会员', '三级会员', '四级会员', '五级会员']
const credit_level_data = computed(() => {
  const levelStats = stats.value.credit_level_stats || {}
  return [
    levelStats.level_1 || 0,
    levelStats.level_2 || 0,
    levelStats.level_3 || 0,
    levelStats.level_4 || 0,
    levelStats.level_5 || 0
  ]
})

// 工具函数
const get_order_status_name = (status: string) => {
  const names: Record<string, string> = {
    pending: '待支付',
    paid: '已支付',
    processing: '处理中',
    shipped: '已发货',
    delivered: '已送达',
    cancelled: '已取消'
  }
  return names[status] || status
}

const get_order_status_color = (status: string) => {
  const colors: Record<string, string> = {
    pending: 'warning',
    paid: 'info',
    processing: 'primary',
    shipped: 'success',
    delivered: 'success',
    cancelled: 'error'
  }
  return colors[status] || 'grey'
}

const get_rank_color = (index: number) => {
  const colors = ['#FFD700', '#C0C0C0', '#CD7F32', '#4CAF50', '#2196F3']
  return colors[index] || 'grey'
}

const format_date = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// API 调用
const load_stats = async () => {
  loading.value = true
  try {
    const [statsRes, ordersRes, stockRes, booksRes] = await Promise.all([
      api.get('/admin/dashboard/stats'),
      api.get('/admin/dashboard/recent-orders?limit=10'),
      api.get('/admin/dashboard/low-stock?limit=10'),
      api.get('/admin/dashboard/top-books?limit=5')
    ])

    if (statsRes.data.code === 200) {
      stats.value = statsRes.data.data
    }
    if (ordersRes.data.code === 200) {
      recent_orders.value = ordersRes.data.data
    }
    if (stockRes.data.code === 200) {
      low_stock_items.value = stockRes.data.data
    }
    if (booksRes.data.code === 200) {
      top_books.value = booksRes.data.data
    }
  } catch (error: any) {
    console.error('加载仪表盘数据失败:', error)
    alert('加载数据失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  load_stats()
})
</script>

<style scoped>
.v-card {
  border-radius: 12px;
}

.v-list-item {
  cursor: pointer;
  transition: background-color 0.2s;
}

.v-list-item:hover {
  background-color: rgba(0, 0, 0, 0.04);
}
</style>
