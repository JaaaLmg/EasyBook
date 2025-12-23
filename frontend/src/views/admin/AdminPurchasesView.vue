<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { purchase_api, supplier_api } from '@/api'
import type { PurchaseOrder, Supplier } from '@/types'

const purchases = ref<PurchaseOrder[]>([])
const suppliers = ref<Supplier[]>([])
const loading = ref(false)
const selected_status = ref<string>('')
const selected_supplier = ref<string>('')

const create_dialog = ref(false)
const create_items = ref<{ isbn: string; quantity: number; unit_price: number }[]>([])
const create_supplier_id = ref<string>('')

const receive_dialog = ref(false)
const receive_purchase_id = ref<string>('')
const receive_items = ref<{ isbn: string; received_quantity: number }[]>([])

const status_options = [
  { title: '全部', value: '' },
  { title: '草稿', value: 'draft' },
  { title: '已提交', value: 'submitted' },
  { title: '已审批', value: 'approved' },
  { title: '已订购', value: 'ordered' },
  { title: '已收货', value: 'received' },
  { title: '已取消', value: 'cancelled' },
]

const status_color = (status: string) => {
  const map: Record<string, string> = {
    draft: 'default',
    submitted: 'info',
    approved: 'primary',
    ordered: 'warning',
    received: 'success',
    cancelled: 'error',
  }
  return map[status] || 'default'
}

const status_text = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    submitted: '已提交',
    approved: '已审批',
    ordered: '已订购',
    received: '已收货',
    cancelled: '已取消',
  }
  return map[status] || status
}

const fetch_suppliers = async () => {
  const resp = await supplier_api.get_suppliers()
  suppliers.value = resp.data.data || []
}

const fetch_purchases = async () => {
  loading.value = true
  try {
    const resp = await purchase_api.get_purchases({
      status: selected_status.value || undefined,
      supplier_id: selected_supplier.value || undefined,
      page: 1,
      page_size: 100,
    })
    purchases.value = resp.data.data?.items || []
  } finally {
    loading.value = false
  }
}

const open_create = () => {
  create_items.value = [{ isbn: '', quantity: 1, unit_price: 0 }]
  create_supplier_id.value = ''
  create_dialog.value = true
}

const add_item = () => {
  create_items.value.push({ isbn: '', quantity: 1, unit_price: 0 })
}

const remove_item = (idx: number) => {
  create_items.value.splice(idx, 1)
}

const create_purchase = async () => {
  if (!create_supplier_id.value || create_items.value.length === 0) return
  for (const it of create_items.value) {
    if (!it.isbn || it.isbn.trim().length === 0) {
      alert('ISBN不能为空')
      return
    }
    if (it.isbn.trim().length > 20) {
      alert('ISBN长度不能超过20')
      return
    }
  }
  const payload = {
    supplier_id: create_supplier_id.value,
    items: create_items.value.map(it => ({
      isbn: it.isbn.trim(),
      quantity: it.quantity,
      unit_price: it.unit_price
    }))
  }
  try {
    await purchase_api.create_purchase(payload)
    create_dialog.value = false
    fetch_purchases()
  } catch (error: any) {
    alert(error.response?.data?.message || '创建采购单失败')
  }
}

const open_receive = async (purchase_id: string) => {
  receive_purchase_id.value = purchase_id
  const resp = await purchase_api.get_purchase(purchase_id)
  const details = resp.data.data?.details || []
  receive_items.value = details.map((d: any) => ({
    isbn: d.isbn,
    received_quantity: d.quantity - d.received_quantity
  }))
  receive_dialog.value = true
}

const do_receive = async () => {
  await purchase_api.receive_purchase(receive_purchase_id.value, {
    items: receive_items.value
  })
  receive_dialog.value = false
  fetch_purchases()
}

const approve = async (purchase_id: string, approved: boolean) => {
  await purchase_api.approve_purchase(purchase_id, { approved })
  fetch_purchases()
}

onMounted(() => {
  fetch_suppliers()
  fetch_purchases()
})
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4">采购管理</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-text>
            <div class="d-flex ga-4">
              <v-btn-toggle
                v-model="selected_status"
                color="primary"
                variant="outlined"
                divided
                @update:model-value="fetch_purchases"
              >
                <v-btn
                  v-for="option in status_options"
                  :key="option.value"
                  :value="option.value"
                >
                  {{ option.title }}
                </v-btn>
              </v-btn-toggle>
              <v-select
                v-model="selected_supplier"
                :items="suppliers.map(s => ({ title: s.supplier_name, value: s.supplier_id }))"
                label="供应商"
                variant="outlined"
                density="compact"
                style="max-width: 280px"
                @update:model-value="fetch_purchases"
              />
              <v-spacer />
              <v-btn color="primary" @click="open_create">新建采购单</v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-table>
            <thead>
              <tr>
                <th>采购单号</th>
                <th>供应商</th>
                <th>金额</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in purchases" :key="p.purchase_id">
                <td>{{ p.purchase_no }}</td>
                <td>{{ suppliers.find(s => s.supplier_id === p.supplier_id)?.supplier_name || p.supplier_id }}</td>
                <td>¥{{ (p.total_amount || 0).toFixed(2) }}</td>
                <td>
                  <v-chip
                    :color="status_color(p.status)"
                    size="small"
                    label
                  >
                    {{ status_text(p.status) }}
                  </v-chip>
                </td>
                <td>{{ new Date(p.create_time).toLocaleString() }}</td>
                <td>
                  <v-btn
                    size="small"
                    variant="text"
                    @click="open_receive(p.purchase_id)"
                  >
                    收货
                  </v-btn>
                  <v-btn
                    size="small"
                    variant="text"
                    color="success"
                    @click="approve(p.purchase_id, true)"
                    :disabled="p.status !== 'submitted' && p.status !== 'draft'"
                  >
                    审批通过
                  </v-btn>
                  <v-btn
                    size="small"
                    variant="text"
                    color="error"
                    @click="approve(p.purchase_id, false)"
                    :disabled="p.status !== 'submitted' && p.status !== 'draft'"
                  >
                    审批驳回
                  </v-btn>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="create_dialog" max-width="700">
      <v-card>
        <v-card-title>新建采购单</v-card-title>
        <v-card-text>
          <v-select
            v-model="create_supplier_id"
            :items="suppliers.map(s => ({ title: s.supplier_name, value: s.supplier_id }))"
            label="供应商"
            variant="outlined"
            density="compact"
          />
          <div class="mt-2">
            <v-btn size="small" @click="add_item">添加明细</v-btn>
          </div>
          <v-table class="mt-2">
            <thead>
              <tr>
                <th>ISBN</th>
                <th>数量</th>
                <th>单价</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(it, idx) in create_items" :key="idx">
                <td>
                  <v-text-field v-model="it.isbn" variant="outlined" density="compact" maxlength="20" :counter="20" />
                </td>
                <td>
                  <v-text-field v-model.number="it.quantity" type="number" variant="outlined" density="compact" />
                </td>
                <td>
                  <v-text-field v-model.number="it.unit_price" type="number" variant="outlined" density="compact" />
                </td>
                <td>
                  <v-btn size="small" color="error" variant="text" @click="remove_item(idx)">删除</v-btn>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="create_dialog = false">取消</v-btn>
          <v-btn color="primary" @click="create_purchase">创建</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="receive_dialog" max-width="600">
      <v-card>
        <v-card-title>收货登记</v-card-title>
        <v-card-text>
          <v-table>
            <thead>
              <tr>
                <th>ISBN</th>
                <th>收货数量</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(it, idx) in receive_items" :key="idx">
                <td>{{ it.isbn }}</td>
                <td>
                  <v-text-field v-model.number="it.received_quantity" type="number" variant="outlined" density="compact" />
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="receive_dialog = false">取消</v-btn>
          <v-btn color="primary" @click="do_receive">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
