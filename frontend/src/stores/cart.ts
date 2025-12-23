/**
 * 购物车状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cart_api } from '../api'
import type { CartItem, CartSummary } from '@/types'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const items = ref<CartItem[]>([])
  const discount_rate = ref(0)
  const is_loading = ref(false)

  // 计算属性
  const total_items = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const total_amount = computed(() => {
    return items.value.reduce((sum, item) => sum + (item.unit_price * item.quantity), 0)
  })

  const discount_amount = computed(() => {
    return total_amount.value * discount_rate.value
  })

  const final_amount = computed(() => {
    return total_amount.value - discount_amount.value
  })

  const is_empty = computed(() => items.value.length === 0)

  // 获取购物车
  const fetch_cart = async () => {
    try {
      is_loading.value = true
      const response = await cart_api.get_cart()
      const data = response.data.data
      if (data) {
        items.value = data.items || []
        discount_rate.value = data.discount_rate || 0
      }
      return data
    } catch (error) {
      console.error('获取购物车失败:', error)
      throw error
    } finally {
      is_loading.value = false
    }
  }

  // 添加商品到购物车
  const add_item = async (isbn: string, quantity: number = 1) => {
    try {
      is_loading.value = true
      const response = await cart_api.add_item(isbn, quantity)
      await fetch_cart() // 重新获取购物车
      return response.data.data
    } catch (error) {
      console.error('添加购物车失败:', error)
      throw error
    } finally {
      is_loading.value = false
    }
  }

  // 更新数量
  const update_quantity = async (item_id: string, quantity: number) => {
    try {
      is_loading.value = true
      await cart_api.update_quantity(item_id, quantity)
      await fetch_cart() // 重新获取购物车
    } catch (error) {
      console.error('更新数量失败:', error)
      throw error
    } finally {
      is_loading.value = false
    }
  }

  // 移除商品
  const remove_item = async (item_id: string) => {
    try {
      is_loading.value = true
      await cart_api.remove_item(item_id)
      await fetch_cart() // 重新获取购物车
    } catch (error) {
      console.error('移除商品失败:', error)
      throw error
    } finally {
      is_loading.value = false
    }
  }

  // 清空购物车
  const clear_cart = async () => {
    try {
      is_loading.value = true
      await cart_api.clear_cart()
      items.value = []
      discount_rate.value = 0
    } catch (error) {
      console.error('清空购物车失败:', error)
      throw error
    } finally {
      is_loading.value = false
    }
  }

  return {
    // 状态
    items,
    discount_rate,
    is_loading,

    // 计算属性
    total_items,
    total_amount,
    discount_amount,
    final_amount,
    is_empty,

    // 方法
    fetch_cart,
    add_item,
    update_quantity,
    remove_item,
    clear_cart,
  }
})
