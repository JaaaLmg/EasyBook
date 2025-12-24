<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { inventory_api, book_api } from '@/api'
import type { InventoryInfo } from '@/types'

const inventories = ref<InventoryInfo[]>([])
const loading = ref(false)
const selected_status = ref<string>('')

const detail_dialog = ref(false)
const detail_loading = ref(false)
const detail_isbn = ref('')
const detail_book = ref<any>(null)
const detail_inventory = ref<any>(null)

// 调整库存对话框
const adjust_dialog = ref(false)
const adjust_isbn = ref('')
const adjust_operation = ref<'in' | 'out' | 'check'>('in')
const adjust_quantity = ref(0)
const adjust_reason = ref('')

const status_options = [
  { title: '全部', value: '' },
  { title: '正常', value: 'normal' },
  { title: '偏低', value: 'low' },
  { title: '缺货', value: 'out_of_stock' }
]

const status_color = (status: string) => {
  const map: Record<string, string> = {
    normal: 'success',
    low: 'warning',
    out_of_stock: 'error'
  }
  return map[status] || 'default'
}

const status_text = (status: string) => {
  const map: Record<string, string> = {
    normal: '正常',
    low: '偏低',
    out_of_stock: '缺货'
  }
  return map[status] || status
}

// 获取库存列表
const fetch_inventory = async () => {
  try {
    loading.value = true
    const response = await inventory_api.get_inventory({
      status: selected_status.value || undefined,
      page: 1,
      page_size: 100
    })
    inventories.value = response.data.data?.items || []
  } catch (error: any) {
    console.error('获取库存列表失败:', error)
    alert(error.response?.data?.message || '获取库存列表失败')
  } finally {
    loading.value = false
  }
}

const open_detail = async (isbn: string) => {
  try {
    detail_dialog.value = true
    detail_loading.value = true
    detail_isbn.value = isbn
    const [invResp, bookResp] = await Promise.all([
      inventory_api.get_inventory_detail(isbn),
      book_api.get_book(isbn)
    ])
    detail_inventory.value = invResp.data.data?.inventory || null
    detail_book.value = bookResp.data.data || null
  } catch (error: any) {
    alert(error.response?.data?.message || '获取详情失败')
  } finally {
    detail_loading.value = false
  }
}

// 打开调整对话框
const open_adjust_dialog = (isbn: string) => {
  adjust_isbn.value = isbn
  adjust_operation.value = 'in'
  adjust_quantity.value = 0
  adjust_reason.value = ''
  adjust_dialog.value = true
}

// 调整库存
const adjust_inventory = async () => {
  if (adjust_quantity.value <= 0) {
    alert('请输入有效的数量')
    return
  }

  if (!adjust_reason.value.trim()) {
    alert('请输入调整原因')
    return
  }

  try {
    await inventory_api.update_inventory(adjust_isbn.value, {
      operation: adjust_operation.value,
      quantity: adjust_quantity.value,
      reason: adjust_reason.value
    })

    alert('库存调整成功')
    adjust_dialog.value = false
    fetch_inventory()
  } catch (error: any) {
    alert(error.response?.data?.message || '库存调整失败')
  }
}

onMounted(() => {
  fetch_inventory()
})
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4" style="color: var(--text-100);">
          库存管理
        </h1>
        <p class="text-body-2" style="color: var(--text-200);">
          <v-icon size="small">mdi-information</v-icon>
          当库存低于安全库存时，系统会自动触发 <code>tr_inventory_low_stock</code> 触发器，生成缺书记录
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
              @update:model-value="fetch_inventory"
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

    <!-- 库存列表 -->
    <v-row>
      <v-col cols="12">
        <v-card elevation="2">
          <v-table>
            <thead>
              <tr>
                <th>ISBN</th>
                <th>当前库存</th>
                <th>安全库存</th>
                <th>状态</th>
                <th>仓库</th>
                <th>位置</th>
                <th>最后补货</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in inventories" :key="item.inventory_id">
                <td>
                  <span class="font-weight-medium">{{ item.isbn }}</span>
                </td>
                <td>{{ item.quantity }}</td>
                <td>{{ item.safety_stock }}</td>
                <td>
                  <v-chip
                    :color="status_color(item.status)"
                    size="small"
                    label
                  >
                    {{ status_text(item.status) }}
                  </v-chip>
                </td>
                <td>{{ item.warehouse }}</td>
                <td>{{ item.location_code || '-' }}</td>
                <td>
                  <span v-if="item.last_restock" class="text-caption">
                    {{ new Date(item.last_restock).toLocaleDateString() }}
                  </span>
                  <span v-else class="text-caption text-grey">未补货</span>
                </td>
                <td class="v-align-center">
                  <v-btn
                    size="small"
                    variant="tonal"
                    color="secondary"
                    @click="open_detail(item.isbn)"
                  >
                    查看详情
                  </v-btn>
                  <v-btn
                    size="small"
                    variant="tonal"
                    color="primary"
                    @click="open_adjust_dialog(item.isbn)"
                  >
                    调整库存
                  </v-btn>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-col>
    </v-row>

    <!-- 空状态 -->
    <v-row v-if="!loading && inventories.length === 0">
      <v-col cols="12" class="text-center py-12">
        <v-icon size="120" color="grey-lighten-1">mdi-package-variant-closed</v-icon>
        <div class="text-h5 mt-4" style="color: var(--text-200);">
          暂无库存记录
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

    <!-- 调整库存对话框 -->
    <v-dialog v-model="adjust_dialog" max-width="500">
      <v-card>
        <v-card-title>调整库存</v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field
              :model-value="adjust_isbn"
              label="ISBN"
              readonly
              variant="outlined"
              density="compact"
            />

            <v-select
              v-model="adjust_operation"
              :items="[
                { title: '入库 (增加库存)', value: 'in' },
                { title: '出库 (减少库存)', value: 'out' },
                { title: '盘点 (调整库存)', value: 'check' }
              ]"
              label="操作类型"
              variant="outlined"
              density="compact"
            />

            <v-text-field
              v-model.number="adjust_quantity"
              label="数量"
              type="number"
              :min="1"
              variant="outlined"
              density="compact"
            />

            <v-textarea
              v-model="adjust_reason"
              label="调整原因"
              variant="outlined"
              density="compact"
              rows="3"
              placeholder="请输入调整原因..."
            />

            <v-alert
              type="info"
              variant="tonal"
              density="compact"
              class="mt-2"
            >
              <div class="text-caption">
                <strong>注意:</strong> 库存调整会立即生效。
                如果调整后库存低于安全库存，将自动触发缺书预警。
              </div>
            </v-alert>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            variant="text"
            @click="adjust_dialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            @click="adjust_inventory"
          >
            确认调整
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 查看详情对话框 -->
    <v-dialog v-model="detail_dialog" max-width="800">
      <v-card>
        <v-card-title>库存详情</v-card-title>
        <v-card-text>
          <div v-if="detail_loading" class="text-center py-8">
            <v-progress-circular indeterminate color="primary" />
          </div>
          <div v-else>
            <v-row>
              <v-col cols="12" md="4">
                <v-img
                  :src="detail_book?.cover_image"
                  height="180"
                  class="mb-2"
                  cover
                />
                <div class="text-subtitle-1">{{ detail_book?.title }}</div>
                <div class="text-caption">
                  作者：{{ detail_book?.authors?.map((a: any) => a.author_name).join('，') || '-' }}
                </div>
                <div class="text-caption">
                  出版社：{{ detail_book?.publisher?.publisher_name || '-' }}
                </div>
                <div class="text-caption">
                  价格：￥{{ detail_book?.price ?? 0 }}
                </div>
              </v-col>
              <v-col cols="12" md="8">
                <v-row>
                  <v-col cols="6">
                    <div class="text-body-2">当前库存：{{ detail_book?.inventory?.quantity ?? detail_inventory?.quantity ?? 0 }}</div>
                  </v-col>
                  <v-col cols="6">
                    <div class="text-body-2">预留数量：{{ detail_book?.inventory?.reserved_quantity ?? detail_inventory?.reserved_quantity ?? 0 }}</div>
                  </v-col>
                  <v-col cols="6">
                    <div class="text-body-2">可用库存：{{ detail_book?.inventory?.available_quantity ?? detail_inventory?.available_quantity ?? 0 }}</div>
                  </v-col>
                  <v-col cols="6">
                    <div class="text-body-2">安全库存：{{ detail_book?.inventory?.safety_stock ?? detail_inventory?.safety_stock ?? 0 }}</div>
                  </v-col>
                  <v-col cols="6">
                    <div class="text-body-2">仓库：{{ detail_inventory?.warehouse || 'main' }}</div>
                  </v-col>
                  <v-col cols="6">
                    <div class="text-body-2">位置：{{ detail_inventory?.location_code || '-' }}</div>
                  </v-col>
                </v-row>
              </v-col>
            </v-row>
            <v-divider class="my-4" />
            <div class="text-body-2">
              目录：{{ detail_book?.table_of_contents || '无' }}
            </div>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="detail_dialog = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.v-align-center {
  display: flex;
  align-items: center;
}
</style>
