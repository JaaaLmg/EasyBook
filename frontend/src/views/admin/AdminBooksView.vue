<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { book_api, book_series_api } from '@/api'
import type { Book } from '@/types'

interface BookDetail extends Book {
  authors?: any[]
  categories?: any[]
  publisher_name?: string
  series_name?: string
}

const books = ref<BookDetail[]>([])
const loading = ref(false)
const keyword = ref('')
const category_filter = ref('')
const publisher_filter = ref('')

const categories = ref<any[]>([])
const publishers = ref<any[]>([])
const series_list = ref<any[]>([])

// 编辑对话框
const edit_dialog = ref(false)
const edit_mode = ref<'create' | 'edit'>('create')
const edit_book = ref<any>({
  isbn: '',
  title: '',
  edition: '',
  publisher_id: '',
  publish_date: '',
  price: null,
  page_count: null,
  format: '',
  language: 'zh',
  description: '',
  table_of_contents: '',
  cover_image: '',
  series_id: '',
  book_type: 'normal',
  status: 'active',
  // 扩展字段
  author_names: [],
  category_ids: [],
  keywords: [],
  initial_stock: 0,
  safety_stock: 10,
  location_code: '',
  warehouse: 'main'
})

// 详情对话框
const detail_dialog = ref(false)
const detail_book = ref<BookDetail | null>(null)

// 作者输入
const author_input = ref('')

// 关键字输入
const keyword_input = ref('')

// 获取图书列表
const fetch_books = async () => {
  loading.value = true
  try {
    const resp = await book_api.get_books({
      keyword: keyword.value || undefined,
      category_id: category_filter.value || undefined,
      publisher_id: publisher_filter.value || undefined,
      page: 1,
      page_size: 100
    })
    books.value = resp.data.data?.items || []
  } catch (error: any) {
    alert(error.response?.data?.message || '获取图书列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类、出版社、丛书列表
const fetch_options = async () => {
  try {
    const [catResp, pubResp, seriesResp] = await Promise.all([
      book_api.get_categories(),
      book_api.get_publishers(),
      book_series_api.get_series()
    ])
    categories.value = catResp.data.data || []
    publishers.value = pubResp.data.data || []
    series_list.value = seriesResp.data.data || []
  } catch (error: any) {
    console.error('获取选项列表失败', error)
  }
}

// 打开创建对话框
const open_create = () => {
  edit_mode.value = 'create'
  edit_book.value = {
    isbn: '',
    title: '',
    edition: '',
    publisher_id: '',
    publish_date: '',
    price: null,
    page_count: null,
    format: '16开',
    language: 'zh',
    description: '',
    table_of_contents: '',
    cover_image: '',
    series_id: '',
    book_type: 'normal',
    status: 'active',
    author_names: [],
    category_ids: [],
    keywords: [],
    initial_stock: 0,
    safety_stock: 10,
    location_code: '',
    warehouse: 'main'
  }
  author_input.value = ''
  keyword_input.value = ''
  edit_dialog.value = true
}

// 打开编辑对话框
const open_edit = async (book: BookDetail) => {
  edit_mode.value = 'edit'
  try {
    // 获取完整的图书信息
    const resp = await book_api.get_book(book.isbn)
    const bookDetail = resp.data.data

    edit_book.value = {
      isbn: bookDetail.isbn,
      title: bookDetail.title,
      edition: bookDetail.edition || '',
      publisher_id: bookDetail.publisher_id,
      publish_date: bookDetail.publish_date || '',
      price: bookDetail.price,
      page_count: bookDetail.page_count || null,
      format: bookDetail.format || '16开',
      language: bookDetail.language || 'zh',
      description: bookDetail.description || '',
      table_of_contents: bookDetail.table_of_contents || '',
      cover_image: bookDetail.cover_image || '',
      series_id: bookDetail.series_id || '',
      book_type: bookDetail.book_type || 'normal',
      status: bookDetail.status || 'active',
      author_names: bookDetail.authors?.map((a: any) => a.author_name) || [],
      category_ids: bookDetail.categories?.map((c: any) => c.category_id) || [],
      keywords: [],
      // 库存信息不在这里编辑
      initial_stock: 0,
      safety_stock: 10,
      location_code: '',
      warehouse: 'main'
    }
    author_input.value = ''
    keyword_input.value = ''
    edit_dialog.value = true
  } catch (error: any) {
    alert(error.response?.data?.message || '获取图书详情失败')
  }
}

// 添加作者
const add_author = () => {
  if (author_input.value.trim()) {
    if (edit_book.value.author_names.length >= 4) {
      alert('最多只能添加4个作者')
      return
    }
    edit_book.value.author_names.push(author_input.value.trim())
    author_input.value = ''
  }
}

// 移除作者
const remove_author = (index: number) => {
  edit_book.value.author_names.splice(index, 1)
}

// 添加关键字
const add_keyword = () => {
  if (keyword_input.value.trim()) {
    if (edit_book.value.keywords.length >= 10) {
      alert('最多只能添加10个关键字')
      return
    }
    edit_book.value.keywords.push(keyword_input.value.trim())
    keyword_input.value = ''
  }
}

// 移除关键字
const remove_keyword = (index: number) => {
  edit_book.value.keywords.splice(index, 1)
}

// 保存图书
const save_book = async () => {
  try {
    // 验证必填字段
    if (!edit_book.value.isbn || !edit_book.value.title || !edit_book.value.publisher_id || edit_book.value.price == null) {
      alert('请填写完整的必填信息：ISBN、书名、出版社、价格')
      return
    }

    // 验证 ISBN 长度
    if (edit_book.value.isbn.length > 20) {
      alert('ISBN 长度不能超过20个字符')
      return
    }

    const payload: any = {
      isbn: edit_book.value.isbn,
      title: edit_book.value.title,
      edition: edit_book.value.edition || undefined,
      publisher_id: edit_book.value.publisher_id,
      publish_date: edit_book.value.publish_date || undefined,
      price: Number(edit_book.value.price),
      page_count: edit_book.value.page_count ? Number(edit_book.value.page_count) : undefined,
      format: edit_book.value.format || undefined,
      language: edit_book.value.language || 'zh',
      description: edit_book.value.description || undefined,
      table_of_contents: edit_book.value.table_of_contents || undefined,
      cover_image: edit_book.value.cover_image || undefined,
      series_id: edit_book.value.series_id || undefined,
      book_type: edit_book.value.book_type || 'normal',
      status: edit_book.value.status || 'active',
      author_names: edit_book.value.author_names,
      category_ids: edit_book.value.category_ids
    }

    if (edit_mode.value === 'create') {
      // 创建模式：包含库存信息
      payload.initial_stock = Number(edit_book.value.initial_stock || 0)
      payload.safety_stock = Number(edit_book.value.safety_stock || 10)
      payload.location_code = edit_book.value.location_code || undefined
      payload.warehouse = edit_book.value.warehouse || 'main'

      await book_api.create_book(payload)
      alert('图书创建成功')
    } else {
      // 编辑模式：不包含库存信息
      await book_api.update_book(edit_book.value.isbn, payload)
      alert('图书更新成功')
    }

    edit_dialog.value = false
    await fetch_books()
  } catch (error: any) {
    alert(error.response?.data?.message || '保存失败')
  }
}

// 删除图书（软删除）
const delete_book = async (isbn: string) => {
  if (!confirm('确定要删除这本图书吗？\n\n删除后图书状态将变为"下架"，库存将清零。历史订单数据不会受影响。')) {
    return
  }

  try {
    await book_api.delete_book(isbn)
    alert('图书已下架')
    await fetch_books()
  } catch (error: any) {
    alert(error.response?.data?.message || '删除失败')
  }
}

// 查看图书详情
const view_detail = async (book: BookDetail) => {
  try {
    const resp = await book_api.get_book(book.isbn)
    detail_book.value = resp.data.data
    detail_dialog.value = true
  } catch (error: any) {
    alert(error.response?.data?.message || '获取详情失败')
  }
}

// 状态标签颜色
const status_color = (status: string) => {
  const map: Record<string, string> = {
    active: 'success',
    out_of_stock: 'warning',
    out_of_print: 'error',
    inactive: 'grey'
  }
  return map[status] || 'default'
}

// 状态文本
const status_text = (status: string) => {
  const map: Record<string, string> = {
    active: '在售',
    out_of_stock: '缺货',
    out_of_print: '绝版',
    inactive: '下架'
  }
  return map[status] || status
}

onMounted(() => {
  fetch_books()
  fetch_options()
})
</script>

<template>
  <v-container fluid>
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon class="mr-2">mdi-book-open-variant</v-icon>
        图书管理
      </v-card-title>

      <!-- 搜索和筛选 -->
      <v-card-text>
        <v-row>
          <v-col cols="12" md="3">
            <v-text-field
              v-model="keyword"
              label="搜索书名/ISBN/作者"
              prepend-inner-icon="mdi-magnify"
              clearable
              @keyup.enter="fetch_books"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="category_filter"
              :items="[{ category_id: '', category_name: '全部分类' }, ...categories]"
              item-title="category_name"
              item-value="category_id"
              label="分类筛选"
              clearable
            ></v-select>
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="publisher_filter"
              :items="[{ publisher_id: '', publisher_name: '全部出版社' }, ...publishers]"
              item-title="publisher_name"
              item-value="publisher_id"
              label="出版社筛选"
              clearable
            ></v-select>
          </v-col>
          <v-col cols="12" md="3" class="d-flex align-center">
            <v-btn color="primary" @click="fetch_books" class="mr-2">
              <v-icon left>mdi-magnify</v-icon>
              搜索
            </v-btn>
            <v-btn color="success" @click="open_create">
              <v-icon left>mdi-plus</v-icon>
              新建图书
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>

      <!-- 图书列表 -->
      <v-data-table
        :items="books"
        :loading="loading"
        :headers="[
          { title: 'ISBN', key: 'isbn', width: '150px' },
          { title: '封面', key: 'cover_image', sortable: false, width: '80px' },
          { title: '书名', key: 'title' },
          { title: '作者', key: 'authors', sortable: false },
          { title: '出版社', key: 'publisher_name' },
          { title: '价格', key: 'price', align: 'right', width: '100px' },
          { title: '丛书', key: 'series_name' },
          { title: '状态', key: 'status', width: '100px' },
          { title: '操作', key: 'actions', sortable: false, width: '280px' }
        ]"
        items-per-page="15"
      >
        <template v-slot:item.cover_image="{ item }">
          <v-avatar size="48" rounded="0">
            <v-img v-if="item.cover_image" :src="item.cover_image" cover></v-img>
            <v-icon v-else>mdi-book</v-icon>
          </v-avatar>
        </template>

        <template v-slot:item.authors="{ item }">
          <span v-if="item.authors && item.authors.length > 0">
            {{ item.authors.map((a: any) => a.author_name).join(', ') }}
          </span>
          <span v-else class="text-grey">-</span>
        </template>

        <template v-slot:item.price="{ item }">
          ¥{{ item.price?.toFixed(2) }}
        </template>

        <template v-slot:item.series_name="{ item }">
          {{ item.series_name || '-' }}
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip :color="status_color(item.status)" size="small">
            {{ status_text(item.status) }}
          </v-chip>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-btn size="small" color="info" variant="text" @click="view_detail(item)">
            <v-icon left size="small">mdi-eye</v-icon>
            详情
          </v-btn>
          <v-btn size="small" color="primary" variant="text" @click="open_edit(item)">
            <v-icon left size="small">mdi-pencil</v-icon>
            编辑
          </v-btn>
          <v-btn size="small" color="error" variant="text" @click="delete_book(item.isbn)">
            <v-icon left size="small">mdi-delete</v-icon>
            删除
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- 创建/编辑图书对话框 -->
    <v-dialog v-model="edit_dialog" max-width="900px" scrollable>
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ edit_mode === 'create' ? '新建图书' : '编辑图书' }}</span>
        </v-card-title>
        <v-card-text style="max-height: 600px;">
          <v-form>
            <!-- 基本信息 -->
            <v-card variant="outlined" class="mb-4">
              <v-card-title class="text-subtitle-1 bg-grey-lighten-4">基本信息</v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="edit_book.isbn"
                      label="ISBN *"
                      :disabled="edit_mode === 'edit'"
                      hint="最多20个字符"
                      persistent-hint
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="edit_book.title"
                      label="书名 *"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="edit_book.publisher_id"
                      :items="publishers"
                      item-title="publisher_name"
                      item-value="publisher_id"
                      label="出版社 *"
                      required
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model.number="edit_book.price"
                      label="价格 *"
                      type="number"
                      min="0"
                      step="0.01"
                      prefix="¥"
                      required
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="4">
                    <v-text-field
                      v-model="edit_book.edition"
                      label="版次"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="4">
                    <v-text-field
                      v-model="edit_book.publish_date"
                      label="出版日期"
                      type="date"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="4">
                    <v-text-field
                      v-model.number="edit_book.page_count"
                      label="页数"
                      type="number"
                      min="0"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="edit_book.format"
                      :items="['16开', '32开', '64开', '其他']"
                      label="开本"
                    ></v-select>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="edit_book.language"
                      :items="[
                        { title: '中文', value: 'zh' },
                        { title: '英文', value: 'en' },
                        { title: '其他', value: 'other' }
                      ]"
                      label="语言"
                    ></v-select>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>

            <!-- 作者信息 -->
            <v-card variant="outlined" class="mb-4">
              <v-card-title class="text-subtitle-1 bg-grey-lighten-4">作者（最多4个）</v-card-title>
              <v-card-text>
                <v-chip
                  v-for="(author, index) in edit_book.author_names"
                  :key="index"
                  closable
                  @click:close="remove_author(index)"
                  class="mr-2 mb-2"
                >
                  {{ author }}
                </v-chip>
                <v-text-field
                  v-model="author_input"
                  label="添加作者"
                  @keyup.enter="add_author"
                  append-inner-icon="mdi-plus"
                  @click:append-inner="add_author"
                  :disabled="edit_book.author_names.length >= 4"
                  hint="按回车或点击加号添加"
                  persistent-hint
                ></v-text-field>
              </v-card-text>
            </v-card>

            <!-- 分类 -->
            <v-card variant="outlined" class="mb-4">
              <v-card-title class="text-subtitle-1 bg-grey-lighten-4">分类</v-card-title>
              <v-card-text>
                <v-select
                  v-model="edit_book.category_ids"
                  :items="categories"
                  item-title="category_name"
                  item-value="category_id"
                  label="选择分类"
                  multiple
                  chips
                  closable-chips
                ></v-select>
              </v-card-text>
            </v-card>

            <!-- 丛书 -->
            <v-card variant="outlined" class="mb-4">
              <v-card-title class="text-subtitle-1 bg-grey-lighten-4">所属丛书（可选）</v-card-title>
              <v-card-text>
                <v-select
                  v-model="edit_book.series_id"
                  :items="[{ series_id: '', series_name: '无' }, ...series_list]"
                  item-title="series_name"
                  item-value="series_id"
                  label="选择丛书"
                  clearable
                ></v-select>
              </v-card-text>
            </v-card>

            <!-- 详细信息 -->
            <v-card variant="outlined" class="mb-4">
              <v-card-title class="text-subtitle-1 bg-grey-lighten-4">详细信息</v-card-title>
              <v-card-text>
                <v-textarea
                  v-model="edit_book.description"
                  label="图书描述"
                  rows="3"
                ></v-textarea>
                <v-textarea
                  v-model="edit_book.table_of_contents"
                  label="目录"
                  rows="4"
                  hint="每行一个章节"
                  persistent-hint
                ></v-textarea>
                <v-text-field
                  v-model="edit_book.cover_image"
                  label="封面URL"
                  hint="输入图片URL地址"
                  persistent-hint
                ></v-text-field>
              </v-card-text>
            </v-card>

            <!-- 库存信息（仅创建时） -->
            <v-card v-if="edit_mode === 'create'" variant="outlined" class="mb-4">
              <v-card-title class="text-subtitle-1 bg-grey-lighten-4">初始库存</v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model.number="edit_book.initial_stock"
                      label="初始库存"
                      type="number"
                      min="0"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model.number="edit_book.safety_stock"
                      label="安全库存"
                      type="number"
                      min="0"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="edit_book.location_code"
                      label="货位编码"
                      hint="如：A-01"
                      persistent-hint
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <v-select
                      v-model="edit_book.warehouse"
                      :items="['main', 'backup']"
                      label="仓库"
                    ></v-select>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="edit_dialog = false">取消</v-btn>
          <v-btn color="primary" variant="text" @click="save_book">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 图书详情对话框 -->
    <v-dialog v-model="detail_dialog" max-width="700px" scrollable>
      <v-card v-if="detail_book">
        <v-card-title>
          <span class="text-h5">{{ detail_book.title }}</span>
        </v-card-title>
        <v-card-text style="max-height: 600px;">
          <v-row>
            <v-col cols="12" md="4" v-if="detail_book.cover_image">
              <v-img :src="detail_book.cover_image" aspect-ratio="1" cover></v-img>
            </v-col>
            <v-col cols="12" :md="detail_book.cover_image ? 8 : 12">
              <v-list density="compact">
                <v-list-item><strong>ISBN:</strong> {{ detail_book.isbn }}</v-list-item>
                <v-list-item><strong>书名:</strong> {{ detail_book.title }}</v-list-item>
                <v-list-item v-if="detail_book.authors && detail_book.authors.length > 0">
                  <strong>作者:</strong> {{ detail_book.authors.map((a: any) => a.author_name).join(', ') }}
                </v-list-item>
                <v-list-item><strong>出版社:</strong> {{ detail_book.publisher_name }}</v-list-item>
                <v-list-item><strong>价格:</strong> ¥{{ detail_book.price?.toFixed(2) }}</v-list-item>
                <v-list-item v-if="detail_book.edition"><strong>版次:</strong> {{ detail_book.edition }}</v-list-item>
                <v-list-item v-if="detail_book.publish_date"><strong>出版日期:</strong> {{ detail_book.publish_date }}</v-list-item>
                <v-list-item v-if="detail_book.page_count"><strong>页数:</strong> {{ detail_book.page_count }}</v-list-item>
                <v-list-item v-if="detail_book.format"><strong>开本:</strong> {{ detail_book.format }}</v-list-item>
                <v-list-item><strong>语言:</strong> {{ detail_book.language }}</v-list-item>
                <v-list-item v-if="detail_book.series_name"><strong>所属丛书:</strong> {{ detail_book.series_name }}</v-list-item>
                <v-list-item>
                  <strong>状态:</strong>
                  <v-chip :color="status_color(detail_book.status)" size="small" class="ml-2">
                    {{ status_text(detail_book.status) }}
                  </v-chip>
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" v-if="detail_book.description">
              <v-divider class="my-2"></v-divider>
              <h3 class="text-subtitle-1 mb-2">图书描述</h3>
              <p class="text-body-2">{{ detail_book.description }}</p>
            </v-col>
            <v-col cols="12" v-if="detail_book.table_of_contents">
              <v-divider class="my-2"></v-divider>
              <h3 class="text-subtitle-1 mb-2">目录</h3>
              <pre class="text-body-2" style="white-space: pre-wrap;">{{ detail_book.table_of_contents }}</pre>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" variant="text" @click="detail_dialog = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.bg-grey-lighten-4 {
  background-color: #f5f5f5;
}
</style>
