<!--
  数据统计页面
  网上书店销售数据统计与可视化
-->
<template>
  <div class="stats-view">
    <!-- 页面标题 -->
    <div class="d-flex align-center justify-space-between mb-6">
      <div>
        <h1 class="text-h4 font-weight-bold text-primary">
          数据统计
        </h1>
        <p class="text-body-1 text-grey-darken-1 mt-1">
          销售数据分析与可视化
        </p>
      </div>

      <v-btn
        @click="export_report"
        color="primary"
        prepend-icon="mdi-download"
        variant="outlined"
        :disabled="loading"
      >
        导出报表
      </v-btn>
    </div>

    <!-- 统计概览卡片 -->
    <v-row class="mb-6">
      <v-col cols="12" md="3">
        <MetricCard
          title="总订单数"
          :value="overview_stats.total_orders"
          subtitle="统计期间内"
          icon="mdi-cart"
          icon-color="info"
          :loading="loading"
        />
      </v-col>
      <v-col cols="12" md="3">
        <MetricCard
          title="总销售额"
          :value="'¥' + overview_stats.total_sales"
          subtitle="累计收入"
          icon="mdi-currency-cny"
          icon-color="success"
          :loading="loading"
        />
      </v-col>
      <v-col cols="12" md="3">
        <MetricCard
          title="图书种类"
          :value="overview_stats.total_books"
          subtitle="在售图书"
          icon="mdi-book-multiple"
          icon-color="warning"
          :loading="loading"
        />
      </v-col>
      <v-col cols="12" md="3">
        <MetricCard
          title="注册用户"
          :value="overview_stats.total_customers"
          subtitle="累计注册"
          icon="mdi-account-group"
          icon-color="error"
          :loading="loading"
        />
      </v-col>
    </v-row>

    <!-- 图表区域 -->
    <v-row>
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title>统计图表</v-card-title>
          <v-card-text>
            <div class="text-center py-8">
              <v-icon size="64" color="grey-lighten-1">mdi-chart-areaspline</v-icon>
              <p class="text-h6 mt-4">图表功能开发中...</p>
              <p class="text-body-2 text-grey-darken-2">敬请期待</p>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { book_api, order_api, stats_api } from '../../api'
import MetricCard from '../../components/common/MetricCard.vue'

// 状态管理
const loading = ref(false)

// 统计数据
const overview_stats = reactive({
  total_orders: 0,
  total_sales: 0,
  total_books: 0,
  total_customers: 0,
})

// 数据加载
const load_statistics = async () => {
  loading.value = true
  try {
    // TODO: 实现书店统计数据加载
    overview_stats.total_orders = 0
    overview_stats.total_sales = 0
    overview_stats.total_books = 0
    overview_stats.total_customers = 0
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 导出报表
const export_report = () => {
  console.log('导出报表功能待实现')
}

// 初始化
onMounted(() => {
  load_statistics()
})
</script>

<style scoped>
.stats-view {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 主标题样式 */
h1 {
  background: linear-gradient(135deg, #f7cac9 0%, #5c5c5c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 卡片样式 */
:deep(.v-card) {
  transition: all 300ms ease;
}

:deep(.v-card:hover) {
  transform: translateY(-2px);
}
</style>
