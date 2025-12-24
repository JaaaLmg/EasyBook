<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { book_api, shortage_api, book_series_api } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import type { BookDetail, Book } from '@/types'

const route = useRoute()
const auth_store = useAuthStore()
const cart_store = useCartStore()

const isbn = computed(() => String(route.params.isbn || ''))
const loading = ref(false)
const book = ref<BookDetail | null>(null)
const shortage_dialog = ref(false)
const shortage_quantity = ref<number>(1)
const shortage_remark = ref<string>('')
const shortage_submitting = ref(false)

// 丛书相关
const series_info = ref<any>(null)
const series_books = ref<Book[]>([])
const series_loading = ref(false)

const fetch_detail = async () => {
  try {
    loading.value = true
    // 清空之前的丛书信息
    series_info.value = null
    series_books.value = []

    const resp = await book_api.get_book(isbn.value)
    book.value = resp.data.data || null

    // 如果图书属于某个丛书，加载丛书信息
    if (book.value?.series_id) {
      fetch_series_info(book.value.series_id)
    }
  } catch (error: any) {
    alert(error.response?.data?.message || '获取图书详情失败')
  } finally {
    loading.value = false
  }
}

const fetch_series_info = async (seriesId: string) => {
  try {
    series_loading.value = true
    const [detailResp, booksResp] = await Promise.all([
      book_series_api.get_series_detail(seriesId),
      book_series_api.get_series_books(seriesId)
    ])
    series_info.value = detailResp.data.data
    series_books.value = booksResp.data.data || []
  } catch (error: any) {
    console.error('获取丛书信息失败:', error)
  } finally {
    series_loading.value = false
  }
}

const add_to_cart = async () => {
  if (!book.value) return
  if (!auth_store.is_authenticated) {
    alert('请先登录')
    return
  }
  try {
    await cart_store.add_item(book.value.isbn, 1)
    alert('已加入购物车')
  } catch (error: any) {
    alert(error.response?.data?.message || '加入购物车失败')
  }
}

const open_shortage_dialog = () => {
  if (!auth_store.is_authenticated) {
    alert('请先登录后再登记缺书')
    return
  }
  shortage_quantity.value = 1
  shortage_remark.value = ''
  shortage_dialog.value = true
}

const submit_shortage = async () => {
  if (!auth_store.is_authenticated) {
    alert('请先登录')
    return
  }
  if (!isbn.value) return
  if (!shortage_quantity.value || shortage_quantity.value < 1) {
    alert('登记数量必须大于0')
    return
  }
  try {
    shortage_submitting.value = true
    await shortage_api.register_shortage({
      isbn: isbn.value,
      required_quantity: shortage_quantity.value,
      remark: shortage_remark.value || undefined,
      priority: 3,
      source_type: 'customer'
    })
    alert('缺书登记成功')
    shortage_dialog.value = false
  } catch (error: any) {
    alert(error.response?.data?.message || '缺书登记失败')
  } finally {
    shortage_submitting.value = false
  }
}

// 监听 ISBN 变化，当用户点击丛书中的其他图书时重新加载
watch(isbn, () => {
  fetch_detail()
})

onMounted(fetch_detail)
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-4" style="color: var(--text-100);">
          图书详情
        </h1>
      </v-col>
    </v-row>

    <v-row v-if="loading">
      <v-col cols="12" class="text-center py-8">
        <v-progress-circular indeterminate color="primary" size="64" />
      </v-col>
    </v-row>

    <v-row v-if="!loading && book">
      <v-col cols="12" md="4">
        <v-card elevation="2">
          <v-img :src="book.cover_image" height="320" />
          <v-card-text>
            <div class="text-h6">{{ book.title }}</div>
            <div class="text-caption" style="color: var(--text-200);">
              ISBN: {{ book.isbn }}
            </div>
            <div class="text-caption mt-2" style="color: var(--text-200);">
              出版社: {{ book.publisher?.publisher_name || '--' }}
            </div>
            <div class="text-caption mt-1" style="color: var(--text-200);">
              作者: {{ book.authors?.map(a => a.author_name).join('，') || '--' }}
            </div>
          </v-card-text>
          <v-card-actions>
            <v-btn
              color="primary"
              prepend-icon="mdi-cart-plus"
              @click="add_to_cart"
            >
              加入购物车
            </v-btn>
            <v-spacer />
            <v-btn
              v-if="auth_store.is_authenticated"
              color="secondary"
              variant="tonal"
              prepend-icon="mdi-book-alert"
              @click="open_shortage_dialog"
            >
              缺书登记
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
      <v-col cols="12" md="8">
        <v-card elevation="2">
          <v-card-title>简介</v-card-title>
          <v-card-text>
            <div class="text-body-2" style="white-space: pre-wrap;">
              {{ book.description || '暂无简介' }}
            </div>
          </v-card-text>
          <v-divider />
          <v-card-title>目录</v-card-title>
          <v-card-text>
            <div class="text-body-2" style="white-space: pre-wrap;">
              {{ book.table_of_contents || '暂无目录' }}
            </div>
          </v-card-text>
          <v-divider />
          <v-card-title>库存状态</v-card-title>
          <v-card-text>
            <div class="text-body-2">
              当前库存: {{ book.inventory?.quantity ?? 0 }}
            </div>
            <div class="text-body-2">
              预留数量: {{ book.inventory?.reserved_quantity ?? 0 }}
            </div>
            <div class="text-body-2">
              可用库存: {{ book.inventory?.available_quantity ?? 0 }}
            </div>
            <div class="text-body-2">
              安全库存: {{ book.inventory?.safety_stock ?? 0 }}
            </div>
          </v-card-text>
        </v-card>

        <!-- 丛书信息卡片 -->
        <v-card v-if="series_info" elevation="2" class="mt-4">
          <v-card-title>
            <v-icon class="mr-2">mdi-book-multiple</v-icon>
            所属丛书
          </v-card-title>
          <v-card-text>
            <div class="text-h6 mb-2" style="color: var(--primary-100);">
              {{ series_info.series_name }}
            </div>
            <div class="text-body-2 mb-2" style="color: var(--text-200);">
              {{ series_info.description || '暂无描述' }}
            </div>
            <div class="text-caption" style="color: var(--text-200);">
              共 {{ series_info.total_books || series_books.length }} 本
            </div>
          </v-card-text>
          <v-divider />
          <v-card-title>丛书包含的其他图书</v-card-title>
          <v-card-text>
            <v-row v-if="series_loading">
              <v-col cols="12" class="text-center">
                <v-progress-circular indeterminate color="primary" size="32" />
              </v-col>
            </v-row>
            <v-row v-else>
              <v-col
                v-for="seriesBook in series_books.filter(b => b.isbn !== isbn)"
                :key="seriesBook.isbn"
                cols="12"
                sm="6"
                md="4"
              >
                <v-card
                  :to="`/books/${seriesBook.isbn}`"
                  hover
                  elevation="1"
                  class="series-book-card"
                >
                  <v-img
                    :src="seriesBook.cover_image || '/placeholder-book.png'"
                    height="160"
                    cover
                  />
                  <v-card-text class="pa-2">
                    <div class="text-subtitle-2 text-truncate">
                      {{ seriesBook.title }}
                    </div>
                    <div class="text-caption" style="color: var(--text-200);">
                      ¥{{ seriesBook.price?.toFixed(2) }}
                    </div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
            <v-alert
              v-if="!series_loading && series_books.length <= 1"
              type="info"
              variant="tonal"
              density="compact"
            >
              该丛书暂无其他图书
            </v-alert>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-if="!loading && !book">
      <v-col cols="12" class="text-center py-12">
        <v-icon size="120" color="grey-lighten-1">mdi-book-off-outline</v-icon>
        <div class="text-h5 mt-4" style="color: var(--text-200);">
          未找到该图书
        </div>
      </v-col>
    </v-row>

    <v-dialog v-model="shortage_dialog" max-width="520">
      <v-card>
        <v-card-title>缺书登记</v-card-title>
        <v-card-text>
          <div class="text-body-2 mb-2" style="color: var(--text-200);">
            图书：{{ book?.title || '-' }}（ISBN: {{ isbn }}）
          </div>
          <v-text-field
            v-model.number="shortage_quantity"
            type="number"
            min="1"
            label="登记数量"
            variant="outlined"
            density="compact"
          />
          <v-text-field
            v-model="shortage_remark"
            label="备注（可选）"
            variant="outlined"
            density="compact"
          />
          <v-alert type="info" variant="tonal" density="compact" class="mt-2">
            系统会自动去重：同一本书存在未解决的缺书记录时，不会重复创建。
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="shortage_dialog = false">取消</v-btn>
          <v-btn :disabled="shortage_submitting" color="primary" @click="submit_shortage">
            提交
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.series-book-card {
  transition: transform 0.2s ease;
}

.series-book-card:hover {
  transform: translateY(-4px);
}
</style>
