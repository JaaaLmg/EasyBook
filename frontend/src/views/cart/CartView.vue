<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { order_api } from '@/api'

const router = useRouter()
const cart_store = useCartStore()
const auth_store = useAuthStore()

// 收货信息
const shipping_address = ref('')
const recipient_name = ref('')
const recipient_phone = ref('')

// 对话框
const checkout_dialog = ref(false)
const loading = ref(false)

// 更新数量
const update_quantity = async (item_id: string, quantity: number) => {
  if (quantity < 1) return
  try {
    await cart_store.update_quantity(item_id, quantity)
  } catch (error: any) {
    alert(error.response?.data?.message || '更新失败')
  }
}

// 移除商品
const remove_item = async (item_id: string) => {
  if (!confirm('确定要移除该商品吗？')) return
  try {
    await cart_store.remove_item(item_id)
  } catch (error: any) {
    alert(error.response?.data?.message || '移除失败')
  }
}

// 去结算
const go_to_checkout = async () => {
  if (cart_store.is_empty) {
    alert('购物车是空的')
    return
  }

  // 同步最新用户资料
  try {
    await auth_store.fetch_user_profile()
  } catch {}

  // 预填充用户信息
  if (auth_store.user) {
    recipient_name.value = auth_store.user.real_name
    recipient_phone.value = auth_store.user.phone || ''
    shipping_address.value = auth_store.user.address || ''
  }

  checkout_dialog.value = true
}

// 提交订单
const submit_order = async () => {
  if (!shipping_address.value || !recipient_name.value || !recipient_phone.value) {
    alert('请填写完整的收货信息')
    return
  }

  // 检查余额
  const available = auth_store.available_balance
  const amount = cart_store.final_amount
  if (available < amount) {
    const shortfall = amount - available
    alert(`账户余额不足！还需 ¥${shortfall.toFixed(2)}，请先充值`)
    return
  }

  try {
    loading.value = true
    const response = await order_api.create_order({
      shipping_address: shipping_address.value,
      recipient_name: recipient_name.value,
      recipient_phone: recipient_phone.value,
      payment_method: 'balance',
      notes: ''
    })

    const order_data = response.data.data
    alert(`订单创建成功！订单号: ${order_data?.order_no}`)

    checkout_dialog.value = false
    router.push('/orders')
  } catch (error: any) {
    console.error('创建订单失败:', error)
    alert(error.response?.data?.message || '创建订单失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  cart_store.fetch_cart()
})
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4" style="color: var(--text-100);">
          购物车
        </h1>
      </v-col>
    </v-row>

    <!-- 购物车商品列表 -->
    <v-row v-if="!cart_store.is_empty">
      <v-col cols="12" md="8">
        <v-card elevation="2">
          <v-card-text>
            <v-list>
              <v-list-item
                v-for="item in cart_store.items"
                :key="item.item_id"
                class="mb-2"
              >
                <template #prepend>
                  <v-avatar size="80" rounded>
                    <v-img
                      :src="item.cover_image || '/placeholder-book.png'"
                      cover
                    />
                  </v-avatar>
                </template>

                <v-list-item-title class="text-h6 mb-2">
                  {{ item.title }}
                </v-list-item-title>
                <v-list-item-subtitle>
                  {{ item.authors }}
                </v-list-item-subtitle>

                <div class="d-flex align-center mt-2">
                  <div class="text-subtitle-1 mr-4" style="color: var(--primary-100);">
                    ¥{{ item.unit_price.toFixed(2) }} × {{ item.quantity }}
                  </div>

                  <v-btn-group size="small" variant="outlined">
                    <v-btn
                      icon="mdi-minus"
                      @click="update_quantity(item.item_id, item.quantity - 1)"
                    />
                    <v-btn disabled>
                      {{ item.quantity }}
                    </v-btn>
                    <v-btn
                      icon="mdi-plus"
                      :disabled="item.quantity >= item.available_stock"
                      @click="update_quantity(item.item_id, item.quantity + 1)"
                    />
                  </v-btn-group>

                  <v-spacer />

                  <div class="text-h6" style="color: var(--primary-100);">
                    ¥{{ item.subtotal.toFixed(2) }}
                  </div>

                  <v-btn
                    icon="mdi-delete"
                    variant="text"
                    color="error"
                    size="small"
                    class="ml-2"
                    @click="remove_item(item.item_id)"
                  />
                </div>

                <div v-if="item.available_stock < 10" class="text-caption text-warning mt-1">
                  库存不足，仅剩 {{ item.available_stock }} 件
                </div>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 订单摘要 -->
      <v-col cols="12" md="4">
        <v-card elevation="2" class="sticky-card">
          <v-card-title>订单摘要</v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item>
                <v-list-item-title>商品总数</v-list-item-title>
                <template #append>
                  <span class="font-weight-bold">{{ cart_store.total_items }} 件</span>
                </template>
              </v-list-item>

              <v-list-item>
                <v-list-item-title>商品原价</v-list-item-title>
                <template #append>
                  <span>¥{{ cart_store.total_amount.toFixed(2) }}</span>
                </template>
              </v-list-item>

              <v-list-item v-if="cart_store.discount_rate > 0">
                <v-list-item-title>
                  会员折扣 ({{ (cart_store.discount_rate * 100).toFixed(0) }}%)
                </v-list-item-title>
                <template #append>
                  <span class="text-success">-¥{{ cart_store.discount_amount.toFixed(2) }}</span>
                </template>
              </v-list-item>

              <v-divider class="my-2" />

              <v-list-item>
                <v-list-item-title class="text-h6">
                  应付金额
                </v-list-item-title>
                <template #append>
                  <span class="text-h6" style="color: var(--primary-100);">
                    ¥{{ cart_store.final_amount.toFixed(2) }}
                  </span>
                </template>
              </v-list-item>

              <v-list-item>
                <v-list-item-title>账户余额</v-list-item-title>
                <template #append>
                  <span>¥{{ auth_store.user?.account_balance.toFixed(2) }}</span>
                </template>
              </v-list-item>

              <v-list-item v-if="auth_store.can_overdraft">
                <v-list-item-title>可用透支额度</v-list-item-title>
                <template #append>
                  <span>¥{{ auth_store.overdraft_limit.toFixed(2) }}</span>
                </template>
              </v-list-item>
            </v-list>

            <v-btn
              color="primary"
              block
              size="large"
              class="mt-4"
              @click="go_to_checkout"
            >
              去结算
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 空购物车 -->
    <v-row v-if="cart_store.is_empty && !cart_store.is_loading">
      <v-col cols="12" class="text-center py-12">
        <v-icon size="120" color="grey-lighten-1">mdi-cart-off</v-icon>
        <div class="text-h5 mt-4" style="color: var(--text-200);">
          购物车是空的
        </div>
        <v-btn
          color="primary"
          class="mt-4"
          to="/books"
        >
          去逛逛
        </v-btn>
      </v-col>
    </v-row>

    <!-- 结算对话框 -->
    <v-dialog v-model="checkout_dialog" max-width="600">
      <v-card>
        <v-card-title>填写收货信息</v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field
              v-model="recipient_name"
              label="收货人姓名"
              required
              variant="outlined"
              density="compact"
            />
            <v-text-field
              v-model="recipient_phone"
              label="收货人电话"
              required
              variant="outlined"
              density="compact"
            />
            <v-textarea
              v-model="shipping_address"
              label="收货地址"
              required
              variant="outlined"
              density="compact"
              rows="3"
            />

            <v-alert
              type="info"
              variant="tonal"
              density="compact"
              class="mt-4"
            >
              <div class="d-flex justify-space-between">
                <span>应付金额:</span>
                <strong>¥{{ cart_store.final_amount.toFixed(2) }}</strong>
              </div>
              <div class="d-flex justify-space-between">
                <span>可用余额:</span>
                <strong>¥{{ auth_store.available_balance.toFixed(2) }}</strong>
              </div>
            </v-alert>

            <div class="text-caption text-warning mt-2">
              * 提交订单后将预留库存，支付后发货时扣款
            </div>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            variant="text"
            @click="checkout_dialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            :loading="loading"
            @click="submit_order"
          >
            提交订单
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.sticky-card {
  position: sticky;
  top: 20px;
}
</style>
