<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { order_api } from '@/api'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
const loading = ref(false)
const selected_status = ref<string>('')

// 发货对话框
const ship_dialog = ref(false)
const ship_order_id = ref('')
const ship_order_no = ref('')
const delivery_company = ref('')
const tracking_no = ref('')

const status_options = [
  { title: '全部', value: '' },
  { title: '待支付', value: 'pending' },
  { title: '已支付', value: 'paid' },
  { title: '处理中', value: 'processing' },
  { title: '已发货', value: 'shipped' },
  { title: '已送达', value: 'delivered' }
]

const status_color = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    paid: 'info',
    processing: 'primary',
    shipped: 'success',
    delivered: 'success',
    cancelled: 'error'
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
    cancelled: '已取消'
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
      page_size: 100,
      all: true
    })
    orders.value = response.data.data?.items || []
  } catch (error: any) {
    console.error('获取订单列表失败:', error)
    alert(error.response?.data?.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 打开发货对话框
const open_ship_dialog = (order_id: string, order_no: string) => {
  ship_order_id.value = order_id
  ship_order_no.value = order_no
  delivery_company.value = ''
  tracking_no.value = ''
  ship_dialog.value = true
}

// 发货 (调用 sp_process_delivery 存储过程)
const ship_order = async () => {
  if (!delivery_company.value.trim() || !tracking_no.value.trim()) {
    alert('请填写物流信息')
    return
  }

  try {
    await order_api.ship_order(ship_order_id.value, {
      delivery_company: delivery_company.value,
      tracking_no: tracking_no.value
    })

    alert('发货成功！\n后端调用了 sp_process_delivery 存储过程：\n1. 扣减库存\n2. 释放预留库存\n3. 更新订单状态')
    ship_dialog.value = false
    fetch_orders()
  } catch (error: any) {
    alert(error.response?.data?.message || '发货失败')
  }
}

const delete_order = async (order_id: string) => {
  if (!confirm('确认删除该订单吗？删除后无法恢复')) return
  try {
    await order_api.admin_delete_order(order_id)
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
          订单管理
        </h1>
        <p class="text-body-2" style="color: var(--text-200);">
          <v-icon size="small">mdi-information</v-icon>
          发货操作会调用 <code>sp_process_delivery</code> 存储过程，扣减库存、释放预留库存等操作
        </p>
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
      <v-col cols="12">
        <v-card elevation="2">
          <v-table>
            <thead>
              <tr>
                <th>订单号</th>
                <th>客户</th>
                <th>下单时间</th>
                <th>应付金额</th>
                <th>状态</th>
                <th>收货人</th>
                <th>电话</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="order in orders"
                :key="order.order_id"
              >
                <td>
                  <span class="font-weight-medium">{{ order.order_no }}</span>
                </td>
                <td>{{ order.customer_id }}</td>
                <td>
                  <span class="text-caption">
                    {{ new Date(order.order_time).toLocaleString() }}
                  </span>
                </td>
                <td>
                  <span class="font-weight-bold" style="color: var(--primary-100);">
                    ¥{{ order.actual_amount.toFixed(2) }}
                  </span>
                </td>
                <td>
                  <v-chip
                    :color="status_color(order.order_status)"
                    size="small"
                    label
                  >
                    {{ status_text(order.order_status) }}
                  </v-chip>
                </td>
                <td>{{ order.recipient_name }}</td>
                <td>{{ order.recipient_phone }}</td>
                <td>
                  <v-btn
                    v-if="order.order_status === 'paid'"
                    size="small"
                    variant="tonal"
                    color="primary"
                    prepend-icon="mdi-truck-delivery"
                    @click="open_ship_dialog(order.order_id, order.order_no)"
                  >
                    发货
                  </v-btn>
                  <v-btn
                    v-else-if="order.order_status === 'shipped'"
                    size="small"
                    variant="text"
                    disabled
                  >
                    已发货
                  </v-btn>
                  <template v-else>
                    <v-btn
                      size="small"
                      variant="text"
                      :to="`/admin/orders/${order.order_id}`"
                    >
                      详情
                    </v-btn>
                    <v-btn
                      v-if="order.order_status === 'delivered'"
                      size="small"
                      color="error"
                      variant="outlined"
                      @click="delete_order(order.order_id)"
                    >
                      删除
                    </v-btn>
                  </template>
                </td>
              </tr>
            </tbody>
          </v-table>
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

    <!-- 发货对话框 -->
    <v-dialog v-model="ship_dialog" max-width="500">
      <v-card>
        <v-card-title>订单发货</v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field
              :model-value="ship_order_no"
              label="订单号"
              readonly
              variant="outlined"
              density="compact"
            />

            <v-text-field
              v-model="delivery_company"
              label="物流公司"
              placeholder="例如：顺丰快递"
              variant="outlined"
              density="compact"
            />

            <v-text-field
              v-model="tracking_no"
              label="物流单号"
              placeholder="例如：SF1234567890"
              variant="outlined"
              density="compact"
            />

            <v-alert
              type="warning"
              variant="tonal"
              density="compact"
              class="mt-2"
            >
              <div class="text-caption">
                <strong>重要:</strong> 点击发货后，系统将：
                <ul class="ml-4 mt-1">
                  <li>调用 <code>sp_process_delivery</code> 存储过程</li>
                  <li>扣减库存数量</li>
                  <li>释放预留库存</li>
                  <li>更新订单状态为"已发货"</li>
                </ul>
              </div>
            </v-alert>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            variant="text"
            @click="ship_dialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            @click="ship_order"
          >
            确认发货
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
