<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { book_series_api, book_api } from '@/api'
import type { Book } from '@/types'

interface BookSeries {
  series_id: string
  series_name: string
  publisher_id: string
  publisher_name?: string
  total_books?: number
  description?: string
}

const series_list = ref<BookSeries[]>([])
const loading = ref(false)
const keyword = ref('')
const publisher_filter = ref('')

const publishers = ref<any[]>([])

const edit_dialog = ref(false)
const edit_series = ref<Partial<BookSeries>>({})

const books_dialog = ref(false)
const current_series_id = ref<string>('')
const current_series_name = ref<string>('')
const series_books = ref<Book[]>([])

// 获取丛书列表
const fetch_series = async () => {
  loading.value = true
  try {
    const resp = await book_series_api.get_series({
      keyword: keyword.value || undefined,
      publisherId: publisher_filter.value || undefined
    })
    series_list.value = resp.data.data || []
  } catch (error: any) {
    alert(error.response?.data?.message || '获取丛书列表失败')
  } finally {
    loading.value = false
  }
}

// 获取出版社列表
const fetch_publishers = async () => {
  try {
    const resp = await book_api.get_publishers()
    publishers.value = resp.data.data || []
  } catch (error: any) {
    console.error('获取出版社列表失败', error)
  }
}

// 打开创建对话框
const open_create = () => {
  edit_series.value = {
    series_name: '',
    publisher_id: '',
    total_books: 0,
    description: ''
  }
  edit_dialog.value = true
}

// 打开编辑对话框
const open_edit = (series: BookSeries) => {
  edit_series.value = { ...series }
  edit_dialog.value = true
}

// 保存丛书（创建或更新）
const save_series = async () => {
  try {
    if (!edit_series.value.series_name || !edit_series.value.publisher_id) {
      alert('请填写丛书名称和出版社')
      return
    }

    if (!edit_series.value.series_id) {
      // 创建
      const resp = await book_series_api.create_series(edit_series.value as any)
      alert('创建成功')
      await fetch_series() // 重新加载列表
    } else {
      // 更新
      await book_series_api.update_series(edit_series.value.series_id, edit_series.value)
      alert('更新成功')
      await fetch_series() // 重新加载列表
    }
    edit_dialog.value = false
  } catch (error: any) {
    alert(error.response?.data?.message || '保存失败')
  }
}

// 删除丛书
const delete_series = async (series_id: string) => {
  if (!confirm('确定要删除这个丛书吗？删除后丛书下的图书不会被删除，但会失去丛书关联。')) {
    return
  }

  try {
    await book_series_api.delete_series(series_id)
    series_list.value = series_list.value.filter(s => s.series_id !== series_id)
    alert('删除成功')
  } catch (error: any) {
    alert(error.response?.data?.message || '删除失败')
  }
}

// 查看丛书包含的图书
const view_books = async (series: BookSeries) => {
  current_series_id.value = series.series_id
  current_series_name.value = series.series_name

  try {
    const resp = await book_series_api.get_series_books(series.series_id)
    series_books.value = resp.data.data || []
    books_dialog.value = true
  } catch (error: any) {
    alert(error.response?.data?.message || '获取丛书图书列表失败')
  }
}

onMounted(() => {
  fetch_series()
  fetch_publishers()
})
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-book-multiple</v-icon>
        丛书管理
      </v-card-title>

      <!-- 搜索栏 -->
      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="keyword"
              label="搜索丛书名称"
              prepend-inner-icon="mdi-magnify"
              clearable
              @keyup.enter="fetch_series"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              v-model="publisher_filter"
              :items="[{ publisher_id: '', publisher_name: '全部出版社' }, ...publishers]"
              item-title="publisher_name"
              item-value="publisher_id"
              label="出版社筛选"
              clearable
            ></v-select>
          </v-col>
          <v-col cols="12" md="4" class="d-flex align-center">
            <v-btn color="primary" @click="fetch_series" class="mr-2">
              <v-icon left>mdi-magnify</v-icon>
              搜索
            </v-btn>
            <v-btn color="success" @click="open_create">
              <v-icon left>mdi-plus</v-icon>
              新建丛书
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>

      <!-- 丛书列表 -->
      <v-data-table
        :items="series_list"
        :loading="loading"
        :headers="[
          { title: '丛书ID', key: 'series_id', width: '120px' },
          { title: '丛书名称', key: 'series_name' },
          { title: '出版社', key: 'publisher_name' },
          { title: '计划总册数', key: 'total_books', align: 'center', width: '120px' },
          { title: '描述', key: 'description' },
          { title: '操作', key: 'actions', sortable: false, width: '280px' }
        ]"
        items-per-page="10"
      >
        <template v-slot:item.total_books="{ item }">
          {{ item.total_books || '-' }}
        </template>

        <template v-slot:item.description="{ item }">
          <span class="text-truncate" style="max-width: 300px; display: inline-block;">
            {{ item.description || '-' }}
          </span>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-btn size="small" color="info" variant="text" @click="view_books(item)">
            <v-icon left size="small">mdi-book-open-page-variant</v-icon>
            查看图书
          </v-btn>
          <v-btn size="small" color="primary" variant="text" @click="open_edit(item)">
            <v-icon left size="small">mdi-pencil</v-icon>
            编辑
          </v-btn>
          <v-btn size="small" color="error" variant="text" @click="delete_series(item.series_id)">
            <v-icon left size="small">mdi-delete</v-icon>
            删除
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- 创建/编辑丛书对话框 -->
    <v-dialog v-model="edit_dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ edit_series.series_id ? '编辑丛书' : '新建丛书' }}</span>
        </v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field
              v-model="edit_series.series_name"
              label="丛书名称 *"
              required
            ></v-text-field>

            <v-select
              v-model="edit_series.publisher_id"
              :items="publishers"
              item-title="publisher_name"
              item-value="publisher_id"
              label="出版社 *"
              required
            ></v-select>

            <v-text-field
              v-model.number="edit_series.total_books"
              label="计划总册数"
              type="number"
              min="0"
            ></v-text-field>

            <v-textarea
              v-model="edit_series.description"
              label="丛书描述"
              rows="3"
            ></v-textarea>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="edit_dialog = false">取消</v-btn>
          <v-btn color="primary" variant="text" @click="save_series">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 查看丛书图书列表对话框 -->
    <v-dialog v-model="books_dialog" max-width="900px">
      <v-card>
        <v-card-title>
          <span class="text-h5">丛书图书列表 - {{ current_series_name }}</span>
        </v-card-title>
        <v-card-text>
          <v-alert v-if="series_books.length === 0" type="info" variant="tonal">
            该丛书暂无关联图书
          </v-alert>
          <v-data-table
            v-else
            :items="series_books"
            :headers="[
              { title: 'ISBN', key: 'isbn' },
              { title: '书名', key: 'title' },
              { title: '版次', key: 'edition' },
              { title: '价格', key: 'price', align: 'right' },
              { title: '出版日期', key: 'publish_date' }
            ]"
            items-per-page="10"
          >
            <template v-slot:item.price="{ item }">
              ¥{{ item.price?.toFixed(2) }}
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" variant="text" @click="books_dialog = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
