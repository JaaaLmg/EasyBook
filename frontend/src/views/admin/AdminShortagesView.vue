<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { shortage_api, supplier_api } from '@/api'
import type { OutOfStockRecord } from '@/types'

const records = ref<OutOfStockRecord[]>([])
const loading = ref(false)
const selected_status = ref<string>('')
const selected_source = ref<string>('')
const keyword = ref<string>('')

const register_dialog = ref(false)
const register_form = ref<{ isbn: string; required_quantity: number; priority?: number; supplier_id?: string; remark?: string }>({
  isbn: '',
  required_quantity: 1,
  priority: 2,
  supplier_id: '',
  remark: ''
})
const suppliers = ref<any[]>([])

const status_options = [
  { title: '全部', value: '' },
  { title: '等待', value: 'pending' },
  { title: '处理中', value: 'processing' },
  { title: '已解决', value: 'resolved' },
  { title: '已取消', value: 'cancelled' },
]

const status_color = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    processing: 'info',
    resolved: 'success',
    cancelled: 'error',
  }
  return map[status] || 'default'
}

const status_text = (status: string) => {
  const map: Record<string, string> = {
    pending: '待处理',
    processing: '处理中',
    resolved: '已解决',
    cancelled: '已取消',
  }
  return map[status] || status
}

const source_options = [
  { title: '全部', value: '' },
  { title: '手工', value: 'manual' },
  { title: '自动', value: 'auto' },
  { title: '客户', value: 'customer' },
]

const fetch_shortages = async () => {
  loading.value = true
  try {
    const resp = await shortage_api.get_shortages({
      status: selected_status.value || undefined,
      source_type: selected_source.value || undefined,
      keyword: keyword.value || undefined,
      page: 1,
      page_size: 100,
    })
    records.value = resp.data.data?.items || []
  } finally {
    loading.value = false
  }
}

const fetch_suppliers = async () => {
  const resp = await supplier_api.get_suppliers()
  suppliers.value = resp.data.data || []
}

const update_status = async (record_id: string, status: string) => {
  await shortage_api.update_shortage(record_id, { status })
  fetch_shortages()
}

const send_notify = async (record_id: string) => {
  await shortage_api.send_notification(record_id)
  fetch_shortages()
}

const open_register = async () => {
  register_form.value = { isbn: '', required_quantity: 1, priority: 2, supplier_id: '', remark: '' }
  await fetch_suppliers()
  register_dialog.value = true
}

const do_register = async () => {
  const f = register_form.value
  f.isbn = f.isbn.trim()
  if (!f.isbn) {
    alert('ISBN不能为空')
    return
  }
  if (f.isbn.length > 20) {
    alert('ISBN长度不能超过20')
    return
  }
  if (!f.required_quantity || f.required_quantity <= 0) {
    alert('登记数量必须大于0')
    return
  }
  try {
    await shortage_api.register_shortage({
      isbn: f.isbn,
      required_quantity: f.required_quantity,
      priority: f.priority,
      remark: f.remark,
      supplier_id: f.supplier_id || undefined,
      source_type: 'manual'
    })
    register_dialog.value = false
    fetch_shortages()
  } catch (error: any) {
    alert(error.response?.data?.message || '登记缺书失败')
  }
}

onMounted(fetch_shortages)
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4">缺书管理</h1>
        <div class="d-flex ga-4">
          <v-btn color="primary" @click="fetch_shortages">刷新</v-btn>
          <v-btn color="secondary" @click="shortage_api.scan_shortages()">扫描库存生成缺书记录</v-btn>
          <v-btn color="primary" variant="tonal" @click="open_register">登记缺书</v-btn>
        </div>
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
                @update:model-value="fetch_shortages"
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
                v-model="selected_source"
                :items="source_options"
                label="来源"
                variant="outlined"
                density="compact"
                style="max-width: 220px"
                @update:model-value="fetch_shortages"
              />
              <v-text-field
                v-model="keyword"
                label="搜索ISBN/书名"
                variant="outlined"
                density="compact"
                style="max-width: 320px"
                @keyup.enter="fetch_shortages"
              />
              <v-btn color="primary" @click="fetch_shortages">搜索</v-btn>
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
                <th>ISBN</th>
                <th>书名</th>
                <th>需采购数量</th>
                <th>来源</th>
                <th>优先级</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in records" :key="r.record_id">
                <td>{{ r.isbn }}</td>
                <td>{{ r.book_title }}</td>
                <td>{{ r.required_quantity }}</td>
                <td>{{ r.source_type }}</td>
                <td>{{ r.priority }}</td>
                <td>
                  <v-chip
                    :color="status_color(r.status)"
                    size="small"
                    label
                  >
                    {{ status_text(r.status) }}
                  </v-chip>
                </td>
                <td>{{ new Date(r.created_time).toLocaleString() }}</td>
                <td class="v-align-center">
                  <v-btn
                    size="small"
                    variant="text"
                    @click="update_status(r.record_id, 'processing')"
                  >
                    标记处理中
                  </v-btn>
                  <v-btn
                    size="small"
                    variant="text"
                    @click="update_status(r.record_id, 'resolved')"
                  >
                    标记已解决
                  </v-btn>
                  <v-btn
                    size="small"
                    variant="text"
                    @click="send_notify(r.record_id)"
                  >
                    发送通知
                  </v-btn>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="register_dialog" max-width="620">
      <v-card>
        <v-card-title>登记缺书</v-card-title>
        <v-card-text>
          <v-text-field v-model="register_form.isbn" label="ISBN" variant="outlined" density="compact" maxlength="20" :counter="20" />
          <v-text-field v-model.number="register_form.required_quantity" type="number" min="1" label="登记数量" variant="outlined" density="compact" />
          <v-select
            v-model="register_form.priority"
            :items="[{ title: '低', value: 1 }, { title: '中', value: 2 }, { title: '高', value: 3 }]"
            label="优先级"
            variant="outlined"
            density="compact"
          />
          <v-select
            v-model="register_form.supplier_id"
            :items="suppliers.map(s => ({ title: s.supplier_name, value: s.supplier_id }))"
            label="供应商（可选）"
            variant="outlined"
            density="compact"
          />
          <v-text-field v-model="register_form.remark" label="备注（可选）" variant="outlined" density="compact" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="register_dialog = false">取消</v-btn>
          <v-btn color="primary" @click="do_register">提交</v-btn>
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
