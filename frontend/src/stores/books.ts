/**
 * 图书状态管理
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { book_api } from '../api'
import type { Category, Publisher, Keyword } from '@/types'

export const useBooksStore = defineStore('books', () => {
  // 状态
  const categories = ref<Category[]>([])
  const publishers = ref<Publisher[]>([])
  const search_history = ref<string[]>(
    JSON.parse(localStorage.getItem('search_history') || '[]')
  )
  const is_loading = ref(false)

  // 获取分类列表
  const fetch_categories = async () => {
    try {
      const response = await book_api.get_categories()
      categories.value = response.data.data || []
      return categories.value
    } catch (error) {
      console.error('获取分类列表失败:', error)
      return []
    }
  }

  // 获取出版社列表
  const fetch_publishers = async () => {
    try {
      const response = await book_api.get_publishers()
      publishers.value = response.data.data || []
      return publishers.value
    } catch (error) {
      console.error('获取出版社列表失败:', error)
      return []
    }
  }

  // 添加搜索历史
  const add_search_history = (keyword: string) => {
    if (!keyword || !keyword.trim()) return

    // 移除重复项
    const history = search_history.value.filter(k => k !== keyword)
    // 添加到开头
    history.unshift(keyword)
    // 只保留最近10条
    search_history.value = history.slice(0, 10)

    // 持久化
    localStorage.setItem('search_history', JSON.stringify(search_history.value))
  }

  // 清除搜索历史
  const clear_search_history = () => {
    search_history.value = []
    localStorage.removeItem('search_history')
  }

  return {
    // 状态
    categories,
    publishers,
    search_history,
    is_loading,

    // 方法
    fetch_categories,
    fetch_publishers,
    add_search_history,
    clear_search_history,
  }
})
