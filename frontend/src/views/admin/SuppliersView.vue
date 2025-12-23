<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { supplier_api } from '@/api'
import type { Supplier } from '@/types'

const suppliers = ref<Supplier[]>([])
const loading = ref(false)
const keyword = ref('')

const edit_dialog = ref(false)
const edit_supplier = ref<Partial<Supplier>>({})

const books_dialog = ref(false)
const books_supplier_id = ref<string>('')
const supplier_books = ref<any[]>([])
const new_book = ref<{ isbn: string; supply_price: number; min_order_quantity?: number; delivery_days?: number }>({ isbn: '', supply_price: 0, min_order_quantity: 1, delivery_days: 7 })

const fetch_suppliers = async () => {
  loading.value = true
  try {
    const resp = await supplier_api.get_suppliers({ keyword: keyword.value || undefined })
    suppliers.value = resp.data.data || []
  } finally {
    loading.value = false
  }
}

const open_create = () => {
  edit_supplier.value = { supplier_name: '', contact_person: '', contact_phone: '', email: '', address: '', cooperation_status: 'active' }
  edit_dialog.value = true
}

const open_edit = (s: Supplier) => {
  edit_supplier.value = { ...s }
  edit_dialog.value = true
}

const save_supplier = async () => {
  if (!edit_supplier.value?.supplier_id) {
    const resp = await supplier_api.create_supplier(edit_supplier.value)
    suppliers.value.unshift(resp.data.data as any)
  } else {
    const resp = await supplier_api.update_supplier(edit_supplier.value.supplier_id!, edit_supplier.value)
    const idx = suppliers.value.findIndex(s => s.supplier_id === edit_supplier.value.supplier_id)
    if (idx >= 0) suppliers.value[idx] = resp.data.data as any
  }
  edit_dialog.value = false
}

const delete_supplier = async (supplier_id: string) => {
  await supplier_api.delete_supplier(supplier_id)
  suppliers.value = suppliers.value.filter(s => s.supplier_id !== supplier_id)
}

const open_books = async (supplier_id: string) => {
  books_supplier_id.value = supplier_id
  const resp = await supplier_api.get_supplier_books(supplier_id)
  supplier_books.value = resp.data.data || []
  new_book.value = { isbn: '', supply_price: 0, min_order_quantity: 1, delivery_days: 7 }
  books_dialog.value = true
}

const add_supplier_book = async () => {
  if (!books_supplier_id.value || !new_book.value.isbn) return
  try {
    new_book.value.isbn = new_book.value.isbn.trim()
    if (new_book.value.isbn.length > 20) {
      alert('ISBN长度不能超过20')
      return
    }
    const resp = await supplier_api.upsert_supplier_book(books_supplier_id.value, new_book.value)
    const updated = resp.data.data as any
    const idx = supplier_books.value.findIndex(b => b.isbn === updated.isbn)
    if (idx >= 0) supplier_books.value[idx] = updated
    else supplier_books.value.push(updated)
    new_book.value = { isbn: '', supply_price: 0, min_order_quantity: 1, delivery_days: 7 }
  } catch (error: any) {
    alert(error.response?.data?.message || '添加供货图书失败')
  }
}

onMounted(fetch_suppliers)
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4">供应商管理</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-text class="d-flex ga-4">
            <v-text-field
              v-model="keyword"
              label="搜索供应商"
              variant="outlined"
              density="compact"
              style="max-width: 320px"
              @keyup.enter="fetch_suppliers"
            />
            <v-btn color="primary" @click="fetch_suppliers">搜索</v-btn>
            <v-spacer />
            <v-btn color="primary" @click="open_create">新增供应商</v-btn>
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
                <th>名称</th>
                <th>联系人</th>
                <th>电话</th>
                <th>邮箱</th>
                <th>地址</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in suppliers" :key="s.supplier_id">
                <td>{{ s.supplier_name }}</td>
                <td>{{ s.contact_person }}</td>
                <td>{{ s.contact_phone }}</td>
                <td>{{ s.email }}</td>
                <td>{{ s.address }}</td>
                <td>
                  <v-chip
                    :color="({ active: 'success', suspended: 'warning', terminated: 'error' }[s.cooperation_status]) || 'default'"
                    size="small"
                    label
                  >
                    {{ ({ active: '在合作', suspended: '暂停', terminated: '终止' }[s.cooperation_status]) || s.cooperation_status }}
                  </v-chip>
                </td>
                <td>
                  <v-btn size="small" variant="text" @click="open_edit(s)">编辑</v-btn>
                  <v-btn size="small" variant="text" @click="open_books(s.supplier_id)">供货信息</v-btn>
                  <v-btn size="small" color="error" variant="text" @click="delete_supplier(s.supplier_id)">删除</v-btn>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="edit_dialog" max-width="600">
      <v-card>
        <v-card-title>供应商信息</v-card-title>
        <v-card-text>
          <v-text-field v-model="edit_supplier.supplier_name" label="名称" variant="outlined" density="compact" />
          <v-text-field v-model="edit_supplier.contact_person" label="联系人" variant="outlined" density="compact" />
          <v-text-field v-model="edit_supplier.contact_phone" label="电话" variant="outlined" density="compact" />
          <v-text-field v-model="edit_supplier.email" label="邮箱" variant="outlined" density="compact" />
          <v-text-field v-model="edit_supplier.address" label="地址" variant="outlined" density="compact" />
          <v-select
            v-model="edit_supplier.cooperation_status"
            :items="[{ title: '在合作', value: 'active' }, { title: '暂停', value: 'suspended' }, { title: '终止', value: 'terminated' }]"
            label="合作状态"
            variant="outlined"
            density="compact"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="edit_dialog = false">取消</v-btn>
          <v-btn color="primary" @click="save_supplier">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="books_dialog" max-width="800">
      <v-card>
        <v-card-title>供应商供货信息</v-card-title>
        <v-card-text>
          <v-table>
            <thead>
              <tr>
                <th>ISBN</th>
                <th>供应价格</th>
                <th>最小起订量</th>
                <th>交货天数</th>
                <th>可供应</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="b in supplier_books" :key="b.isbn">
                <td>{{ b.isbn }}</td>
                <td>¥{{ b.supply_price }}</td>
                <td>{{ b.min_order_quantity }}</td>
                <td>{{ b.delivery_days }}</td>
                <td>{{ b.is_available ? '是' : '否' }}</td>
              </tr>
            </tbody>
          </v-table>
          <div class="mt-4">
            <div class="text-subtitle-2 mb-2">添加供货图书</div>
            <div class="d-flex ga-4">
              <v-text-field v-model="new_book.isbn" label="ISBN" variant="outlined" density="compact" maxlength="20" :counter="20" />
              <v-text-field v-model.number="new_book.supply_price" type="number" label="供应价格" variant="outlined" density="compact" />
              <v-text-field v-model.number="new_book.min_order_quantity" type="number" label="最小起订量" variant="outlined" density="compact" />
              <v-text-field v-model.number="new_book.delivery_days" type="number" label="交货天数" variant="outlined" density="compact" />
              <v-btn color="primary" @click="add_supplier_book">添加</v-btn>
            </div>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="books_dialog = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
