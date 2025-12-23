<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { book_api } from '@/api'
import { useBooksStore } from '@/stores/books'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import type { Book, PaginatedResponse } from '@/types'

const books_store = useBooksStore()
const cart_store = useCartStore()
const auth_store = useAuthStore()

// 图书列表
const books = ref<Book[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const page_size = ref(20)

// 搜索和筛选
const keyword = ref('')
const selected_category = ref<string>('')
const selected_publisher = ref<string>('')
const min_price = ref<number>()
const max_price = ref<number>()
const sort_by = ref('default')

// 获取图书列表
const fetch_books = async () => {
  try {
    loading.value = true
    const response = await book_api.get_books({
      keyword: keyword.value || undefined,
      category_id: selected_category.value || undefined,
      publisher_id: selected_publisher.value || undefined,
      min_price: min_price.value,
      max_price: max_price.value,
      sort: sort_by.value === 'default' ? undefined : sort_by.value,
      page: page.value,
      page_size: page_size.value,
      status: 'active' // 只显示在售图书
    })
    const data = response.data.data
    if (data) {
      books.value = data.items || []
      total.value = data.total || 0
    }
  } catch (error: any) {
    console.error('获取图书列表失败:', error)
    alert(error.response?.data?.message || '获取图书列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const search = () => {
  if (keyword.value.trim()) {
    books_store.add_search_history(keyword.value)
  }
  page.value = 1
  fetch_books()
}

// 重置筛选
const reset_filters = () => {
  keyword.value = ''
  selected_category.value = ''
  selected_publisher.value = ''
  min_price.value = undefined
  max_price.value = undefined
  sort_by.value = 'default'
  page.value = 1
  fetch_books()
}

// 加入购物车
const add_to_cart = async (isbn: string) => {
  if (!auth_store.is_authenticated) {
    alert('请先登录')
    return
  }

  try {
    await cart_store.add_item(isbn, 1)
    alert('已加入购物车')
  } catch (error: any) {
    console.error('加入购物车失败:', error)
    alert(error.response?.data?.message || '加入购物车失败')
  }
}

// 计算折扣后价格
const discounted_price = computed(() => {
  return (price: number) => {
    const rate = auth_store.credit_info?.discount_rate || 0
    return price * (1 - rate)
  }
})

// 页面挂载时获取数据
onMounted(async () => {
  await Promise.all([
    books_store.fetch_categories(),
    books_store.fetch_publishers(),
    fetch_books()
  ])
})
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4" style="color: var(--text-100);">
          图书商城
        </h1>
      </v-col>
    </v-row>

    <!-- 搜索栏 -->
    <v-row>
      <v-col cols="12">
        <v-card elevation="2" style="background: var(--bg-100);">
          <v-card-text>
            <v-row>
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="keyword"
                  label="搜索图书 (书名、ISBN、作者)"
                  prepend-inner-icon="mdi-magnify"
                  variant="outlined"
                  density="compact"
                  clearable
                  @keyup.enter="search"
                />
              </v-col>
              <v-col cols="12" md="2">
                <v-select
                  v-model="selected_category"
                  :items="books_store.categories"
                  item-title="category_name"
                  item-value="category_id"
                  label="分类"
                  variant="outlined"
                  density="compact"
                  clearable
                />
              </v-col>
              <v-col cols="12" md="2">
                <v-select
                  v-model="selected_publisher"
                  :items="books_store.publishers"
                  item-title="publisher_name"
                  item-value="publisher_id"
                  label="出版社"
                  variant="outlined"
                  density="compact"
                  clearable
                />
              </v-col>
              <v-col cols="12" md="2">
                <v-select
                  v-model="sort_by"
                  :items="[
                    { title: '默认排序', value: 'default' },
                    { title: '价格从低到高', value: 'price_asc' },
                    { title: '价格从高到低', value: 'price_desc' },
                    { title: '最新出版', value: 'publish_date_desc' }
                  ]"
                  label="排序"
                  variant="outlined"
                  density="compact"
                />
              </v-col>
              <v-col cols="12" md="2">
                <v-btn
                  color="primary"
                  block
                  @click="search"
                >
                  搜索
                </v-btn>
              </v-col>
            </v-row>
            <v-row v-if="keyword || selected_category || selected_publisher">
              <v-col>
                <v-btn
                  size="small"
                  variant="text"
                  @click="reset_filters"
                >
                  <v-icon>mdi-refresh</v-icon>
                  重置筛选
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 信用等级提示 -->
    <v-row v-if="auth_store.is_authenticated && auth_store.credit_info">
      <v-col cols="12">
        <v-alert
          type="info"
          variant="tonal"
          density="compact"
        >
          <template #prepend>
            <v-icon>mdi-account-star</v-icon>
          </template>
          您是{{ auth_store.credit_info.level_name }}，享受{{ (auth_store.credit_info.discount_rate * 100).toFixed(0) }}%折扣
        </v-alert>
      </v-col>
    </v-row>

    <!-- 图书列表 -->
    <v-row>
      <v-col
        v-for="book in books"
        :key="book.isbn"
        cols="12"
        sm="6"
        md="4"
        lg="3"
      >
        <v-card elevation="2" height="100%">
          <v-img
            :src="book.cover_image || '/placeholder-book.png'"
            height="250"
            cover
          />
          <v-card-title class="text-subtitle-1">
            {{ book.title }}
          </v-card-title>
          <v-card-subtitle>
            ISBN: {{ book.isbn }}
          </v-card-subtitle>
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-2">
              <div>
                <div class="text-h6" style="color: var(--primary-100);">
                  ¥{{ discounted_price(book.price).toFixed(2) }}
                </div>
                <div
                  v-if="auth_store.credit_info && auth_store.credit_info.discount_rate > 0"
                  class="text-caption text-decoration-line-through"
                  style="color: var(--text-200);"
                >
                  ¥{{ book.price.toFixed(2) }}
                </div>
              </div>
              <v-chip
                :color="book.status === 'active' ? 'success' : 'error'"
                size="small"
              >
                {{ book.status === 'active' ? '在售' : '缺货' }}
              </v-chip>
            </div>
            <div class="text-caption" style="color: var(--text-200);">
              {{ book.language }} | {{ book.page_count || '--' }}页
            </div>
          </v-card-text>
          <v-card-actions>
            <v-btn
              :disabled="book.status !== 'active'"
              color="primary"
              variant="tonal"
              size="small"
              prepend-icon="mdi-cart-plus"
              @click="add_to_cart(book.isbn)"
            >
              加入购物车
            </v-btn>
            <v-spacer />
            <v-btn
              color="primary"
              variant="text"
              size="small"
              :to="`/books/${book.isbn}`"
            >
              详情
            </v-btn>
          </v-card-actions>
        </v-card>
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

    <!-- 空状态 -->
    <v-row v-if="!loading && books.length === 0">
      <v-col cols="12" class="text-center py-8">
        <v-icon size="64" color="grey">mdi-book-off-outline</v-icon>
        <div class="text-h6 mt-4" style="color: var(--text-200);">
          没有找到图书
        </div>
      </v-col>
    </v-row>

    <!-- 分页 -->
    <v-row v-if="!loading && books.length > 0">
      <v-col cols="12" class="d-flex justify-center">
        <v-pagination
          v-model="page"
          :length="Math.ceil(total / page_size)"
          @update:model-value="fetch_books"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.v-card {
  transition: all 0.3s ease;
}

.v-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15) !important;
}
</style>
