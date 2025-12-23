<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { order_api } from '@/api'
import { useAuthStore } from '@/stores/auth'
import type { Order, OrderDetail, DeliveryOrder } from '@/types'

const route = useRoute()
const router = useRouter()
const auth_store = useAuthStore()

const orderId = computed(() => String(route.params.orderId || ''))
const loading = ref(false)
const order = ref<{ order: Order; details: OrderDetail[]; delivery?: DeliveryOrder } | null>(null)

const fetch_detail = async () => {
  try {
    loading.value = true
    const resp = await order_api.get_order(orderId.value)
    order.value = resp.data.data || null
  } catch (error: any) {
    alert(error.response?.data?.message || '获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const pay_order = async () => {
  if (!order.value) return
  try {
    await order_api.pay_order(order.value.order.order_id, 'balance')
    alert('支付成功')
    await fetch_detail()
    try { await auth_store.fetch_user_profile() } catch {}
  } catch (error: any) {
    alert(error.response?.data?.message || '支付失败')
  }
}

const cancel_order = async () => {
  if (!order.value) return
  if (!confirm('确认取消该订单吗？')) return
  try {
    await order_api.cancel_order(order.value.order.order_id, '用户取消')
    alert('订单已取消')
    try { await auth_store.fetch_user_profile() } catch {}
    router.push('/orders')
  } catch (error: any) {
    alert(error.response?.data?.message || '取消失败')
  }
}

const confirm_delivery = async () => {
  if (!order.value) return
  if (!confirm('确认收货吗？')) return
  try {
    await order_api.confirm_delivery(order.value.order.order_id)
    alert('确认收货成功')
    await fetch_detail()
    try { await auth_store.fetch_user_profile() } catch {}
  } catch (error: any) {
    alert(error.response?.data?.message || '确认失败')
  }
}

const delete_order = async () => {
  if (!order.value) return
  if (!confirm('确认删除该订单吗？删除后无法恢复')) return
  try {
    await order_api.delete_order(order.value.order.order_id)
    alert('订单已删除')
    router.push('/orders')
  } catch (error: any) {
    alert(error.response?.data?.message || '删除失败')
  }
}

onMounted(fetch_detail)
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4" style="color: var(--text-100);">
          订单详情
        </h1>
      </v-col>
    </v-row>

    <v-row v-if="loading">
      <v-col cols="12" class="text-center py-8">
        <v-progress-circular indeterminate color="primary" size="64" />
      </v-col>
    </v-row>

    <v-row v-if="!loading && order">
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center">
            <div class="flex-grow-1">
              <div class="text-subtitle-1">订单号: {{ order.order.order_no }}</div>
              <div class="text-caption" style="color: var(--text-200);">
                下单时间: {{ new Date(order.order.order_time).toLocaleString() }}
              </div>
            </div>
              <v-chip label>
                {{ order.order.order_status }}
              </v-chip>
          </v-card-title>
          <v-divider />
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <div class="text-subtitle-2 mb-2">收货信息</div>
                <div class="text-body-2">
                  <div>收货人: {{ order.order.recipient_name }}</div>
                  <div>电话: {{ order.order.recipient_phone }}</div>
                  <div>地址: {{ order.order.shipping_address }}</div>
                </div>
              </v-col>
              <v-col cols="12" md="6">
                <div class="text-subtitle-2 mb-2">金额信息</div>
                <div class="text-body-2">
                  <div>商品总额: ¥{{ order.order.total_amount.toFixed(2) }}</div>
                  <div>折扣金额: -¥{{ order.order.discount_amount.toFixed(2) }}</div>
                  <div>运费: ¥{{ order.order.shipping_fee.toFixed(2) }}</div>
                  <div class="text-h6 mt-2" style="color: var(--primary-100);">
                    实付金额: ¥{{ order.order.actual_amount.toFixed(2) }}
                  </div>
                </div>
              </v-col>
            </v-row>
            <v-alert
              v-if="order.order.order_status === 'processing'"
              type="warning"
              variant="tonal"
              density="compact"
              class="mt-2"
            >
              缺货：当前库存不足，补货后将自动恢复为“待支付”
            </v-alert>
            <div v-if="order.order.payment_time" class="text-caption mt-2">
              支付时间: {{ new Date(order.order.payment_time).toLocaleString() }}
            </div>
            <div v-if="order.order.shipping_time" class="text-caption">
              发货时间: {{ new Date(order.order.shipping_time).toLocaleString() }}
            </div>
            <div v-if="order.order.delivery_time" class="text-caption">
              送达时间: {{ new Date(order.order.delivery_time).toLocaleString() }}
            </div>
          </v-card-text>
          <v-divider />
          <v-card-title>商品明细</v-card-title>
          <v-table>
            <thead>
              <tr>
                <th>ISBN</th>
                <th>数量</th>
                <th>单价</th>
                <th>小计</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="d in order.details" :key="d.detail_id">
                <td>{{ d.isbn }}</td>
                <td>{{ d.quantity }}</td>
                <td>¥{{ d.unit_price.toFixed(2) }}</td>
                <td>¥{{ d.subtotal.toFixed(2) }}</td>
              </tr>
            </tbody>
          </v-table>
          <v-divider />
          <v-card-actions>
            <v-btn variant="text" to="/orders">返回订单列表</v-btn>
            <v-spacer />
            <template v-if="order.order.order_status === 'pending'">
              <v-btn color="primary" @click="pay_order">立即支付</v-btn>
              <v-btn color="error" variant="outlined" @click="cancel_order">取消订单</v-btn>
            </template>
            <template v-if="order.order.order_status === 'paid'">
              <v-btn color="error" variant="outlined" @click="cancel_order">取消订单</v-btn>
            </template>
            <template v-if="order.order.order_status === 'shipped'">
              <v-btn color="primary" @click="confirm_delivery">确认收货</v-btn>
            </template>
            <template v-if="order.order.order_status === 'delivered'">
              <v-btn color="error" variant="outlined" @click="delete_order">删除订单</v-btn>
            </template>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-if="!loading && !order">
      <v-col cols="12" class="text-center py-12">
        <v-icon size="120" color="grey-lighten-1">mdi-package-variant</v-icon>
        <div class="text-h5 mt-4" style="color: var(--text-200);">
          未找到该订单
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>
