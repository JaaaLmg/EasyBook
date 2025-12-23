<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { order_api } from '@/api'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
const loading = ref(false)
const selected_status = ref<string>('')

const status_options = [
  { title: '全部', value: '' },
  { title: '待支付', value: 'pending' },
  { title: '已支付', value: 'paid' },
  { title: '处理中', value: 'processing' },
  { title: '已发货', value: 'shipped' },
  { title: '已送达', value: 'delivered' },
  { title: '已取消', value: 'cancelled' }
]

const status_color = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    paid: 'info',
    processing: 'primary',
    shipped: 'success',
    delivered: 'success',
    cancelled: 'error',
    refunded: 'error'
  }
  return map[status] || 'default'
}

const status_text = (status: string) => {
  const map: Record<string, string> = {
    pending: '待支付',
    paid: '已支付',
    processing: '处理中',
    shipped: '已发货',
    delivered: '已送达',
    cancelled: '已取消',
    refunded: '已退款'
  }
  return map[status] || status
}

// 获取订单列表
const fetch_orders = async () => {
  try {
    loading.value = true
    const response = await order_api.get_orders({
      status: selected_status.value || undefined,
      page: 1,
      page_size: 50
    })
    orders.value = response.data.data?.items || []
  } catch (error: any) {
    console.error('获取订单列表失败:', error)
    alert(error.response?.data?.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 支付订单
const pay_order = async (order_id: string) => {
  if (!confirm('确认支付该订单吗？')) return

  try {
    await order_api.pay_order(order_id, 'balance')
    alert('支付成功！')
    fetch_orders()
    try {
      const { useAuthStore } = await import('@/stores/auth')
      const auth_store = useAuthStore()
      await auth_store.fetch_user_profile()
    } catch {}
  } catch (error: any) {
    alert(error.response?.data?.message || '支付失败')
  }
}

// 取消订单
const cancel_order = async (order_id: string) => {
  if (!confirm('确认取消该订单吗？')) return

  try {
    await order_api.cancel_order(order_id, '用户取消')
    alert('订单已取消')
    fetch_orders()
    try {
      const { useAuthStore } = await import('@/stores/auth')
      const auth_store = useAuthStore()
      await auth_store.fetch_user_profile()
    } catch {}
  } catch (error: any) {
    alert(error.response?.data?.message || '取消失败')
  }
}

// 确认收货
const confirm_delivery = async (order_id: string) => {
  if (!confirm('确认收货吗？')) return

  try {
    await order_api.confirm_delivery(order_id)
    alert('确认收货成功！')
    fetch_orders()
    try {
      const { useAuthStore } = await import('@/stores/auth')
      const auth_store = useAuthStore()
      await auth_store.fetch_user_profile()
    } catch {}
  } catch (error: any) {
    alert(error.response?.data?.message || '确认失败')
  }
}

const delete_order = async (order_id: string) => {
  if (!confirm('确认删除该订单吗？删除后无法恢复')) return
  try {
    await order_api.delete_order(order_id)
    alert('订单已删除')
    fetch_orders()
  } catch (error: any) {
    alert(error.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  fetch_orders()
})
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4" style="color: var(--text-100);">
          我的订单
        </h1>
      </v-col>
    </v-row>

    <!-- 筛选 -->
    <v-row>
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-text>
            <v-btn-toggle
              v-model="selected_status"
              color="primary"
              variant="outlined"
              divided
              @update:model-value="fetch_orders"
            >
              <v-btn
                v-for="option in status_options"
                :key="option.value"
                :value="option.value"
              >
                {{ option.title }}
              </v-btn>
            </v-btn-toggle>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 订单列表 -->
    <v-row>
      <v-col
        v-for="order in orders"
        :key="order.order_id"
        cols="12"
      >
        <v-card elevation="2">
          <v-card-title class="d-flex align-center">
            <div class="flex-grow-1">
              <div class="text-subtitle-1">
                订单号: {{ order.order_no }}
              </div>
              <div class="text-caption" style="color: var(--text-200);">
                下单时间: {{ new Date(order.order_time).toLocaleString() }}
              </div>
            </div>
            <v-chip
              :color="status_color(order.order_status)"
              label
            >
              {{ status_text(order.order_status) }}
            </v-chip>
          </v-card-title>

          <v-divider />

          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <div class="text-subtitle-2 mb-2">收货信息</div>
                <div class="text-body-2">
                  <div>收货人: {{ order.recipient_name }}</div>
                  <div>电话: {{ order.recipient_phone }}</div>
                  <div>地址: {{ order.shipping_address }}</div>
                </div>
              </v-col>
              <v-col cols="12" md="6">
                <div class="text-subtitle-2 mb-2">订单金额</div>
                <div class="text-body-2">
                  <div>商品总额: ¥{{ order.total_amount.toFixed(2) }}</div>
                  <div>折扣金额: -¥{{ order.discount_amount.toFixed(2) }}</div>
                  <div>运费: ¥{{ order.shipping_fee.toFixed(2) }}</div>
                  <div class="text-h6 mt-2" style="color: var(--primary-100);">
                    实付金额: ¥{{ order.actual_amount.toFixed(2) }}
                  </div>
                </div>
              </v-col>
            </v-row>
            <v-alert
              v-if="order.order_status === 'processing'"
              type="warning"
              variant="tonal"
              density="compact"
              class="mt-2"
            >
              缺货：当前库存不足，补货后将自动恢复为“待支付”
            </v-alert>

            <div v-if="order.payment_time" class="text-caption mt-2">
              支付时间: {{ new Date(order.payment_time).toLocaleString() }}
            </div>
            <div v-if="order.shipping_time" class="text-caption">
              发货时间: {{ new Date(order.shipping_time).toLocaleString() }}
            </div>
            <div v-if="order.delivery_time" class="text-caption">
              送达时间: {{ new Date(order.delivery_time).toLocaleString() }}
            </div>
          </v-card-text>

          <v-divider />

          <v-card-actions>
            <v-btn
              size="small"
              variant="text"
              :to="`/orders/${order.order_id}`"
            >
              查看详情
            </v-btn>

            <v-spacer />

            <!-- 待支付状态 -->
            <template v-if="order.order_status === 'pending'">
              <v-btn
                size="small"
                color="primary"
                @click="pay_order(order.order_id)"
              >
                立即支付
              </v-btn>
              <v-btn
                size="small"
                color="error"
                variant="outlined"
                @click="cancel_order(order.order_id)"
              >
                取消订单
              </v-btn>
            </template>

            <!-- 已支付状态 -->
            <template v-if="order.order_status === 'paid'">
              <v-btn
                size="small"
                color="error"
                variant="outlined"
                @click="cancel_order(order.order_id)"
              >
                取消订单
              </v-btn>
            </template>

            <!-- 已发货状态 -->
            <template v-if="order.order_status === 'shipped'">
              <v-btn
                size="small"
                color="primary"
                @click="confirm_delivery(order.order_id)"
              >
                确认收货
              </v-btn>
            </template>

            <!-- 已送达状态 -->
            <template v-if="order.order_status === 'delivered'">
              <v-btn
                size="small"
                color="error"
                variant="outlined"
                @click="delete_order(order.order_id)"
              >
                删除订单
              </v-btn>
            </template>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- 空状态 -->
    <v-row v-if="!loading && orders.length === 0">
      <v-col cols="12" class="text-center py-12">
        <v-icon size="120" color="grey-lighten-1">mdi-package-variant</v-icon>
        <div class="text-h5 mt-4" style="color: var(--text-200);">
          暂无订单
        </div>
        <v-btn
          color="primary"
          class="mt-4"
          to="/books"
        >
          去购物
        </v-btn>
      </v-col>
    </v-row>

    <!-- 加载中 -->
    <v-row v-if="loading">
      <v-col cols="12" class="text-center py-8">
        <v-progress-circular
          indeterminate
          color="primary"
          size="64"
        />
      </v-col>
    </v-row>
  </v-container>
</template>
