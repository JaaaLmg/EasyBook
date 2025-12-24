<!--
  管理员客户管理页面
  功能：查看所有客户、调整信用等级、充值、冻结/解冻账户
-->
<template>
  <v-container fluid class="pa-4">
    <!-- 页面标题 -->
    <v-row class="mb-4">
      <v-col cols="12">
        <h1 class="text-h4 font-weight-bold">
          <v-icon size="32" class="mr-2">mdi-account-group</v-icon>
          客户管理
        </h1>
      </v-col>
    </v-row>

    <!-- 筛选和搜索 -->
    <v-row class="mb-4">
      <v-col cols="12" md="4">
        <v-text-field
          v-model="search_keyword"
          label="搜索客户"
          placeholder="用户名、姓名、邮箱"
          prepend-inner-icon="mdi-magnify"
          clearable
          variant="outlined"
          density="comfortable"
          @keyup.enter="load_customers"
        />
      </v-col>
      <v-col cols="12" md="3">
        <v-select
          v-model="filter_credit_level"
          label="信用等级"
          :items="credit_level_options"
          clearable
          variant="outlined"
          density="comfortable"
          @update:model-value="load_customers"
        />
      </v-col>
      <v-col cols="12" md="3">
        <v-select
          v-model="filter_account_status"
          label="账户状态"
          :items="account_status_options"
          clearable
          variant="outlined"
          density="comfortable"
          @update:model-value="load_customers"
        />
      </v-col>
      <v-col cols="12" md="2" class="d-flex align-center">
        <v-btn
          color="primary"
          block
          @click="load_customers"
        >
          <v-icon start>mdi-magnify</v-icon>
          搜索
        </v-btn>
      </v-col>
    </v-row>

    <!-- 客户列表 -->
    <v-card elevation="2">
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="customers"
          :loading="loading"
          :items-per-page="page_size"
          hide-default-footer
          class="elevation-0"
        >
          <!-- 客户ID -->
          <template #item.customer_id="{ item }">
            <span class="text-caption text-grey">{{ item.customer_id }}</span>
          </template>

          <!-- 客户姓名 -->
          <template #item.real_name="{ item }">
            <div>
              <div class="font-weight-medium">{{ item.real_name }}</div>
              <div class="text-caption text-grey">{{ item.username }}</div>
            </div>
          </template>

          <!-- 联系方式 -->
          <template #item.contact="{ item }">
            <div>
              <div class="text-caption">
                <v-icon size="14" class="mr-1">mdi-email</v-icon>
                {{ item.email }}
              </div>
              <div v-if="item.phone" class="text-caption">
                <v-icon size="14" class="mr-1">mdi-phone</v-icon>
                {{ item.phone }}
              </div>
            </div>
          </template>

          <!-- 账户余额 -->
          <template #item.account_balance="{ item }">
            <v-chip
              :color="item.account_balance >= 0 ? 'success' : 'error'"
              size="small"
              variant="tonal"
            >
              ¥{{ item.account_balance?.toFixed(2) || '0.00' }}
            </v-chip>
          </template>

          <!-- 信用等级 -->
          <template #item.credit_level="{ item }">
            <v-chip
              :color="get_credit_color(item.credit_level)"
              size="small"
              variant="flat"
            >
              {{ get_credit_name(item.credit_level) }}
            </v-chip>
          </template>

          <!-- 累计消费 -->
          <template #item.total_purchase="{ item }">
            <span class="font-weight-medium">
              ¥{{ item.total_purchase?.toFixed(2) || '0.00' }}
            </span>
          </template>

          <!-- 账户状态 -->
          <template #item.account_status="{ item }">
            <v-chip
              :color="get_status_color(item.account_status)"
              size="small"
              variant="tonal"
            >
              {{ get_status_name(item.account_status) }}
            </v-chip>
          </template>

          <!-- 操作列 -->
          <template #item.actions="{ item }">
            <v-btn
              icon
              size="small"
              variant="text"
              color="primary"
              @click="open_detail_dialog(item)"
            >
              <v-icon>mdi-eye</v-icon>
              <v-tooltip activator="parent" location="top">查看详情</v-tooltip>
            </v-btn>
            <v-btn
              icon
              size="small"
              variant="text"
              color="success"
              @click="open_recharge_dialog(item)"
            >
              <v-icon>mdi-cash-plus</v-icon>
              <v-tooltip activator="parent" location="top">充值</v-tooltip>
            </v-btn>
            <v-btn
              icon
              size="small"
              variant="text"
              color="warning"
              @click="open_credit_dialog(item)"
            >
              <v-icon>mdi-star</v-icon>
              <v-tooltip activator="parent" location="top">调整等级</v-tooltip>
            </v-btn>
            <v-btn
              icon
              size="small"
              variant="text"
              :color="item.account_status === 'active' ? 'error' : 'success'"
              @click="toggle_account_status(item)"
            >
              <v-icon>{{ item.account_status === 'active' ? 'mdi-lock' : 'mdi-lock-open' }}</v-icon>
              <v-tooltip activator="parent" location="top">
                {{ item.account_status === 'active' ? '冻结' : '解冻' }}
              </v-tooltip>
            </v-btn>
          </template>
        </v-data-table>

        <!-- 分页 -->
        <div class="d-flex justify-center mt-4">
          <v-pagination
            v-model="current_page"
            :length="total_pages"
            :total-visible="7"
            @update:model-value="load_customers"
          />
        </div>
      </v-card-text>
    </v-card>

    <!-- 客户详情对话框 -->
    <v-dialog v-model="detail_dialog" max-width="800">
      <v-card v-if="selected_customer">
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-2">mdi-account-details</v-icon>
          客户详情
          <v-spacer />
          <v-btn icon variant="text" @click="detail_dialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-divider />
        <v-card-text>
          <v-row>
            <v-col cols="12" md="6">
              <div class="mb-3">
                <div class="text-caption text-grey">客户ID</div>
                <div class="font-weight-medium">{{ selected_customer.customer_id }}</div>
              </div>
              <div class="mb-3">
                <div class="text-caption text-grey">用户名</div>
                <div class="font-weight-medium">{{ selected_customer.username }}</div>
              </div>
              <div class="mb-3">
                <div class="text-caption text-grey">真实姓名</div>
                <div class="font-weight-medium">{{ selected_customer.real_name }}</div>
              </div>
              <div class="mb-3">
                <div class="text-caption text-grey">邮箱</div>
                <div class="font-weight-medium">{{ selected_customer.email }}</div>
              </div>
              <div class="mb-3">
                <div class="text-caption text-grey">电话</div>
                <div class="font-weight-medium">{{ selected_customer.phone || '未填写' }}</div>
              </div>
            </v-col>
            <v-col cols="12" md="6">
              <div class="mb-3">
                <div class="text-caption text-grey">账户余额</div>
                <div class="font-weight-medium text-h6">
                  ¥{{ selected_customer.account_balance?.toFixed(2) || '0.00' }}
                </div>
              </div>
              <div class="mb-3">
                <div class="text-caption text-grey">信用等级</div>
                <v-chip :color="get_credit_color(selected_customer.credit_level)" variant="flat">
                  {{ get_credit_name(selected_customer.credit_level) }}
                </v-chip>
              </div>
              <div class="mb-3">
                <div class="text-caption text-grey">累计消费</div>
                <div class="font-weight-medium text-h6">
                  ¥{{ selected_customer.total_purchase?.toFixed(2) || '0.00' }}
                </div>
              </div>
              <div class="mb-3">
                <div class="text-caption text-grey">账户状态</div>
                <v-chip :color="get_status_color(selected_customer.account_status)" variant="tonal">
                  {{ get_status_name(selected_customer.account_status) }}
                </v-chip>
              </div>
              <div class="mb-3">
                <div class="text-caption text-grey">注册日期</div>
                <div class="font-weight-medium">{{ format_date(selected_customer.registration_date) }}</div>
              </div>
            </v-col>
            <v-col cols="12">
              <div class="mb-3">
                <div class="text-caption text-grey">地址</div>
                <div class="font-weight-medium">{{ selected_customer.address || '未填写' }}</div>
              </div>
            </v-col>
          </v-row>

          <!-- 最近订单 -->
          <v-divider class="my-4" />
          <h3 class="mb-3">最近订单</h3>
          <div v-if="customer_detail?.recent_orders?.length > 0">
            <v-list>
              <v-list-item
                v-for="order in customer_detail.recent_orders.slice(0, 5)"
                :key="order.order_id"
                class="px-0"
              >
                <template #prepend>
                  <v-icon>mdi-package-variant</v-icon>
                </template>
                <v-list-item-title>{{ order.order_no }}</v-list-item-title>
                <v-list-item-subtitle>
                  {{ format_date(order.order_time) }} | ¥{{ order.actual_amount?.toFixed(2) }}
                </v-list-item-subtitle>
                <template #append>
                  <v-chip size="small" :color="get_order_status_color(order.order_status)">
                    {{ get_order_status_name(order.order_status) }}
                  </v-chip>
                </template>
              </v-list-item>
            </v-list>
            <div class="text-caption text-grey mt-2">
              共 {{ customer_detail.order_count }} 笔订单
            </div>
          </div>
          <div v-else class="text-center text-grey py-4">
            暂无订单
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- 充值对话框 -->
    <v-dialog v-model="recharge_dialog" max-width="500">
      <v-card>
        <v-card-title>
          <v-icon class="mr-2">mdi-cash-plus</v-icon>
          客户充值
        </v-card-title>
        <v-divider />
        <v-card-text v-if="selected_customer">
          <div class="mb-4">
            <div class="text-caption text-grey">客户</div>
            <div class="font-weight-medium">{{ selected_customer.real_name }} ({{ selected_customer.username }})</div>
          </div>
          <div class="mb-4">
            <div class="text-caption text-grey">当前余额</div>
            <div class="text-h5 font-weight-bold">¥{{ selected_customer.account_balance?.toFixed(2) || '0.00' }}</div>
          </div>
          <v-text-field
            v-model.number="recharge_amount"
            label="充值金额"
            type="number"
            prefix="¥"
            variant="outlined"
            :rules="[
              v => v > 0 || '充值金额必须大于0',
              v => v <= 100000 || '单次充值不能超过10万元'
            ]"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="recharge_dialog = false">取消</v-btn>
          <v-btn color="primary" variant="flat" @click="handle_recharge">确认充值</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 调整信用等级对话框 -->
    <v-dialog v-model="credit_dialog" max-width="500">
      <v-card>
        <v-card-title>
          <v-icon class="mr-2">mdi-star</v-icon>
          调整信用等级
        </v-card-title>
        <v-divider />
        <v-card-text v-if="selected_customer">
          <div class="mb-4">
            <div class="text-caption text-grey">客户</div>
            <div class="font-weight-medium">{{ selected_customer.real_name }} ({{ selected_customer.username }})</div>
          </div>
          <div class="mb-4">
            <div class="text-caption text-grey">当前等级</div>
            <v-chip :color="get_credit_color(selected_customer.credit_level)" variant="flat">
              {{ get_credit_name(selected_customer.credit_level) }}
            </v-chip>
          </div>
          <v-radio-group v-model="new_credit_level" inline>
            <v-radio
              v-for="level in 5"
              :key="level"
              :label="`${level}级 (${get_credit_discount(level)}折)`"
              :value="level"
              :color="get_credit_color(level)"
            />
          </v-radio-group>
          <v-alert type="info" variant="tonal" class="mt-3">
            <div class="text-caption">
              <div>1级: 10%折扣，不能透支</div>
              <div>2级: 15%折扣，不能透支</div>
              <div>3级: 15%折扣，可透支500元</div>
              <div>4级: 20%折扣，可透支1000元</div>
              <div>5级: 25%折扣，透支无限额</div>
            </div>
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="credit_dialog = false">取消</v-btn>
          <v-btn color="primary" variant="flat" @click="handle_update_credit">确认调整</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/client'

const router = useRouter()

// 数据状态
const loading = ref(false)
const customers = ref<any[]>([])
const current_page = ref(1)
const page_size = ref(20)
const total = ref(0)

// 筛选条件
const search_keyword = ref('')
const filter_credit_level = ref<number | null>(null)
const filter_account_status = ref<string | null>(null)

// 对话框状态
const detail_dialog = ref(false)
const recharge_dialog = ref(false)
const credit_dialog = ref(false)
const selected_customer = ref<any>(null)
const customer_detail = ref<any>(null)

// 充值和调整等级
const recharge_amount = ref(0)
const new_credit_level = ref(1)

// 表格列定义
const headers = [
  { title: '客户ID', key: 'customer_id', sortable: false, width: '100px' },
  { title: '客户姓名', key: 'real_name', sortable: false },
  { title: '联系方式', key: 'contact', sortable: false },
  { title: '账户余额', key: 'account_balance', sortable: false, width: '120px' },
  { title: '信用等级', key: 'credit_level', sortable: false, width: '100px' },
  { title: '累计消费', key: 'total_purchase', sortable: false, width: '120px' },
  { title: '账户状态', key: 'account_status', sortable: false, width: '100px' },
  { title: '操作', key: 'actions', sortable: false, width: '200px', align: 'center' }
]

// 下拉选项
const credit_level_options = [
  { title: '全部等级', value: null },
  { title: '一级会员', value: 1 },
  { title: '二级会员', value: 2 },
  { title: '三级会员', value: 3 },
  { title: '四级会员', value: 4 },
  { title: '五级会员', value: 5 }
]

const account_status_options = [
  { title: '全部状态', value: null },
  { title: '正常', value: 'active' },
  { title: '冻结', value: 'frozen' },
  { title: '关闭', value: 'closed' }
]

// 计算属性
const total_pages = computed(() => Math.ceil(total.value / page_size.value))

// 工具函数
const get_credit_name = (level: number) => {
  const names = ['', '一级会员', '二级会员', '三级会员', '四级会员', '五级会员']
  return names[level] || '未知'
}

const get_credit_color = (level: number) => {
  const colors = ['', 'grey', 'blue', 'green', 'orange', 'red']
  return colors[level] || 'grey'
}

const get_credit_discount = (level: number) => {
  const discounts = [0, 9, 8.5, 8.5, 8, 7.5]
  return discounts[level] || 10
}

const get_status_name = (status: string) => {
  const names: Record<string, string> = {
    active: '正常',
    frozen: '冻结',
    closed: '关闭'
  }
  return names[status] || status
}

const get_status_color = (status: string) => {
  const colors: Record<string, string> = {
    active: 'success',
    frozen: 'warning',
    closed: 'error'
  }
  return colors[status] || 'grey'
}

const get_order_status_name = (status: string) => {
  const names: Record<string, string> = {
    pending: '待支付',
    paid: '已支付',
    processing: '处理中',
    shipped: '已发货',
    delivered: '已送达',
    cancelled: '已取消',
    refunded: '已退款'
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
    cancelled: 'error',
    refunded: 'grey'
  }
  return colors[status] || 'grey'
}

const format_date = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// API 调用
const load_customers = async () => {
  loading.value = true
  try {
    const params: any = {
      page: current_page.value,
      pageSize: page_size.value
    }
    if (search_keyword.value) params.keyword = search_keyword.value
    if (filter_credit_level.value) params.creditLevel = filter_credit_level.value
    if (filter_account_status.value) params.accountStatus = filter_account_status.value

    const response = await api.get('/admin/customers', { params })
    if (response.data.code === 200) {
      customers.value = response.data.data.items
      total.value = response.data.data.total
    }
  } catch (error: any) {
    alert('加载客户列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const load_customer_detail = async (customer_id: string) => {
  try {
    const response = await api.get(`/admin/customers/${customer_id}`)
    if (response.data.code === 200) {
      customer_detail.value = response.data.data
    }
  } catch (error: any) {
    alert('加载客户详情失败: ' + (error.response?.data?.message || error.message))
  }
}

// 对话框操作
const open_detail_dialog = async (customer: any) => {
  selected_customer.value = customer
  await load_customer_detail(customer.customer_id)
  detail_dialog.value = true
}

const open_recharge_dialog = (customer: any) => {
  selected_customer.value = customer
  recharge_amount.value = 0
  recharge_dialog.value = true
}

const open_credit_dialog = (customer: any) => {
  selected_customer.value = customer
  new_credit_level.value = customer.credit_level
  credit_dialog.value = true
}

// 业务操作
const handle_recharge = async () => {
  if (!selected_customer.value || recharge_amount.value <= 0) {
    alert('请输入有效的充值金额')
    return
  }

  try {
    const response = await api.post(
      `/admin/customers/${selected_customer.value.customer_id}/recharge`,
      { amount: recharge_amount.value, payment_method: 'admin_recharge' }
    )
    if (response.data.code === 200) {
      alert('充值成功')
      recharge_dialog.value = false
      await load_customers()
    }
  } catch (error: any) {
    alert('充值失败: ' + (error.response?.data?.message || error.message))
  }
}

const handle_update_credit = async () => {
  if (!selected_customer.value) return

  try {
    const response = await api.put(
      `/admin/customers/${selected_customer.value.customer_id}/credit-level`,
      { credit_level: new_credit_level.value }
    )
    if (response.data.code === 200) {
      alert('信用等级调整成功')
      credit_dialog.value = false
      await load_customers()
    }
  } catch (error: any) {
    alert('调整失败: ' + (error.response?.data?.message || error.message))
  }
}

const toggle_account_status = async (customer: any) => {
  const new_status = customer.account_status === 'active' ? 'frozen' : 'active'
  const action = new_status === 'frozen' ? '冻结' : '解冻'

  if (!confirm(`确定要${action}账户 "${customer.real_name}" 吗？`)) return

  try {
    const response = await api.put(
      `/admin/customers/${customer.customer_id}/status`,
      { account_status: new_status }
    )
    if (response.data.code === 200) {
      alert(`${action}成功`)
      await load_customers()
    }
  } catch (error: any) {
    alert(`${action}失败: ` + (error.response?.data?.message || error.message))
  }
}

// 生命周期
onMounted(() => {
  load_customers()
})
</script>

<style scoped>
.full-width-container {
  max-width: 100%;
}
</style>
