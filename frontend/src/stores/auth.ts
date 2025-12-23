/**
 * 认证状态管理
 * 管理用户登录状态、JWT Token和用户信息 - 书店系统
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { auth_api, account_api, system_api } from '../api'
import type { User, CreditRule } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const access_token = ref<string | null>(
    localStorage.getItem('access_token')
  )
  const user = ref<User | null>(
    JSON.parse(localStorage.getItem('user') || 'null')
  )
  const credit_rules = ref<CreditRule[]>([])
  const is_loading = ref(false)

  // 计算属性
  const is_authenticated = computed(() => !!access_token.value)

  const user_display_name = computed(() => {
    if (!user.value) return ''
    return user.value.real_name || user.value.username
  })

  const is_admin = computed(() => user.value?.is_admin || false)

  const credit_info = computed(() => {
    if (!user.value) return null
    const rule = credit_rules.value.find(r => r.level === user.value!.credit_level)
    return {
      level: user.value.credit_level,
      level_name: rule?.level_name || `${user.value.credit_level}级会员`,
      discount_rate: rule?.discount_rate || 0,
      overdraft_enabled: rule?.overdraft_enabled || false,
      overdraft_limit: rule?.overdraft_limit || 0,
      total_purchase: user.value.total_purchase,
      account_balance: user.value.account_balance
    }
  })

  const can_overdraft = computed(() => {
    const info = credit_info.value
    return info ? info.overdraft_enabled : false
  })

  const overdraft_limit = computed(() => {
    const info = credit_info.value
    return info ? info.overdraft_limit : 0
  })

  const available_balance = computed(() => {
    if (!user.value) return 0
    const balance = user.value.account_balance
    const limit = overdraft_limit.value
    return balance + (can_overdraft.value ? limit : 0)
  })

  // 登录
  const login = (token: string, user_data: User) => {
    access_token.value = token
    user.value = user_data

    // 持久化存储
    localStorage.setItem('access_token', token)
    localStorage.setItem('user', JSON.stringify(user_data))
  }

  // 登出
  const logout = () => {
    access_token.value = null
    user.value = null

    // 清除存储
    localStorage.removeItem('access_token')
    localStorage.removeItem('user')
  }

  // 更新用户信息
  const update_user = (user_data: User) => {
    user.value = user_data
    localStorage.setItem('user', JSON.stringify(user_data))
  }

  // 获取用户资料
  const fetch_user_profile = async () => {
    try {
      is_loading.value = true
      const response = await auth_api.get_profile()
      update_user(response.data.data!)
      return response.data.data
    } catch (error) {
      console.error('获取用户资料失败:', error)
      throw error
    } finally {
      is_loading.value = false
    }
  }

  // 获取信用等级规则
  const fetch_credit_rules = async () => {
    try {
      const response = await system_api.get_credit_rules()
      credit_rules.value = response.data.data || []
      return credit_rules.value
    } catch (error) {
      console.error('获取信用等级规则失败:', error)
      return []
    }
  }

  // 充值
  const recharge = async (amount: number, payment_method: string) => {
    try {
      await account_api.recharge(amount, payment_method)
      // 重新获取用户信息以更新余额
      await fetch_user_profile()
    } catch (error) {
      console.error('充值失败:', error)
      throw error
    }
  }

  return {
    // 状态
    access_token,
    user,
    credit_rules,
    is_loading,

    // 计算属性
    is_authenticated,
    user_display_name,
    is_admin,
    credit_info,
    can_overdraft,
    overdraft_limit,
    available_balance,

    // 方法
    login,
    logout,
    update_user,
    fetch_user_profile,
    fetch_credit_rules,
    recharge,
  }
})
