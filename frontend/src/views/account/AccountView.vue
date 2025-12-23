<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { account_api } from '@/api'

const auth_store = useAuthStore()

// 充值对话框
const recharge_dialog = ref(false)
const recharge_amount = ref(0)
const recharge_loading = ref(false)

// 信用等级信息
const credit_info = computed(() => auth_store.credit_info)

// 下一级信息
const next_level_info = computed(() => {
  if (!credit_info.value) return null
  const current_level = credit_info.value.level
  if (current_level >= 5) return null // 已经是最高等级

  const next_rule = auth_store.credit_rules.find(r => r.level === current_level + 1)
  if (!next_rule) return null

  const required = next_rule.min_total_purchase || 0
  const current = credit_info.value.total_purchase
  const remaining = required - current

  return {
    level: next_rule.level,
    level_name: next_rule.level_name,
    discount_rate: next_rule.discount_rate,
    required_purchase: required,
    remaining: Math.max(0, remaining)
  }
})

// 充值
const do_recharge = async () => {
  if (recharge_amount.value <= 0) {
    alert('请输入有效的充值金额')
    return
  }

  try {
    recharge_loading.value = true
    await auth_store.recharge(recharge_amount.value, 'alipay')
    alert(`充值成功！已充值 ¥${recharge_amount.value}`)
    recharge_dialog.value = false
    recharge_amount.value = 0
  } catch (error: any) {
    alert(error.response?.data?.message || '充值失败')
  } finally {
    recharge_loading.value = false
  }
}

onMounted(async () => {
  try { await auth_store.fetch_user_profile() } catch {}
  auth_store.fetch_credit_rules()
})
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4" style="color: var(--text-100);">
          账户管理
        </h1>
      </v-col>
    </v-row>

    <!-- 账户余额 -->
    <v-row>
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title>
            <v-icon class="mr-2">mdi-wallet</v-icon>
            账户余额
          </v-card-title>
          <v-card-text>
            <div class="text-h3" style="color: var(--primary-100);">
              ¥{{ (auth_store.user?.account_balance ?? 0).toFixed(2) }}
            </div>
            <div v-if="credit_info && credit_info.overdraft_enabled" class="mt-4">
              <div class="text-subtitle-2 mb-2">透支额度</div>
              <div class="text-h6">
                ¥{{ (credit_info?.overdraft_limit ?? 0).toFixed(2) }}
              </div>
            </div>
            <div class="mt-4">
              <div class="text-subtitle-2 mb-2">可用余额</div>
              <div class="text-h6">
                ¥{{ auth_store.available_balance.toFixed(2) }}
              </div>
            </div>
          </v-card-text>
          <v-card-actions>
            <v-btn
              color="primary"
              prepend-icon="mdi-plus"
              @click="recharge_dialog = true"
            >
              充值
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title>
            <v-icon class="mr-2">mdi-chart-line</v-icon>
            消费统计
          </v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item>
                <v-list-item-title>累计消费</v-list-item-title>
                <template #append>
                  <span class="font-weight-bold">
                    ¥{{ (auth_store.user?.total_purchase ?? 0).toFixed(2) }}
                  </span>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 信用等级 -->
    <v-row>
      <v-col cols="12">
        <v-card elevation="2">
          <v-card-title>
            <v-icon class="mr-2">mdi-account-star</v-icon>
            信用等级
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <div class="text-subtitle-2 mb-2">当前等级</div>
                <div class="d-flex align-center mb-4">
                  <v-chip
                    color="primary"
                    size="large"
                    class="mr-4"
                  >
                    {{ credit_info?.level_name }}
                  </v-chip>
                  <div>
                    <div class="text-h6" style="color: var(--primary-100);">
                      {{ ((credit_info?.discount_rate ?? 0) * 100).toFixed(0) }}% 折扣
                    </div>
                    <div v-if="credit_info?.overdraft_enabled" class="text-caption">
                      可透支 ¥{{ (credit_info?.overdraft_limit ?? 0).toFixed(2) }}
                    </div>
                  </div>
                </div>

                <div v-if="next_level_info" class="mt-4">
                  <div class="text-subtitle-2 mb-2">升级进度</div>
                  <div class="text-body-2 mb-2">
                    距离 <strong>{{ next_level_info.level_name }}</strong>
                    还需消费 <strong style="color: var(--primary-100);">
                      ¥{{ next_level_info.remaining.toFixed(2) }}
                    </strong>
                  </div>
                  <v-progress-linear
                    :model-value="next_level_info.required_purchase ? (((credit_info?.total_purchase ?? 0) / next_level_info.required_purchase) * 100) : 0"
                    color="primary"
                    height="8"
                    rounded
                  />
                  <div class="text-caption mt-1" style="color: var(--text-200);">
                    {{ (credit_info?.total_purchase ?? 0).toFixed(0) }} / {{ next_level_info.required_purchase }}
                  </div>
                </div>

                <div v-else class="text-subtitle-1 text-success mt-4">
                  🎉 您已达到最高等级！
                </div>
              </v-col>

              <v-col cols="12" md="6">
                <div class="text-subtitle-2 mb-3">等级规则说明</div>
                <v-list density="compact">
                  <v-list-item
                    v-for="rule in auth_store.credit_rules"
                    :key="rule.level"
                    :class="{ 'bg-primary-lighten-5': rule.level === credit_info?.level }"
                  >
                    <template #prepend>
                      <v-icon
                        v-if="rule.level === credit_info?.level"
                        color="primary"
                      >
                        mdi-check-circle
                      </v-icon>
                      <v-icon v-else color="grey">
                        mdi-circle-outline
                      </v-icon>
                    </template>
                    <v-list-item-title>
                      {{ rule.level_name }}
                    </v-list-item-title>
                    <v-list-item-subtitle>
                      <div>{{ (rule.discount_rate * 100).toFixed(0) }}% 折扣</div>
                      <div v-if="rule.overdraft_enabled">
                        可透支 ¥{{ rule.overdraft_limit }}
                      </div>
                      <div v-if="rule.min_total_purchase" class="text-caption">
                        需累计消费 ¥{{ rule.min_total_purchase }}
                      </div>
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 充值对话框 -->
    <v-dialog v-model="recharge_dialog" max-width="400">
      <v-card>
        <v-card-title>账户充值</v-card-title>
        <v-card-text>
          <v-text-field
            v-model.number="recharge_amount"
            label="充值金额"
            type="number"
            prefix="¥"
            variant="outlined"
            :min="0"
            step="100"
          />

          <div class="d-flex gap-2 mb-4">
            <v-btn
              v-for="amount in [100, 500, 1000, 5000]"
              :key="amount"
              size="small"
              variant="tonal"
              @click="recharge_amount = amount"
            >
              ¥{{ amount }}
            </v-btn>
          </div>

          <v-alert
            type="info"
            variant="tonal"
            density="compact"
          >
            充值后余额将立即到账
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            variant="text"
            @click="recharge_dialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            :loading="recharge_loading"
            @click="do_recharge"
          >
            确认充值
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
