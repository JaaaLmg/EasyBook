<template>
  <div class="profile-view">
    <div class="d-flex align-center justify-space-between mb-6">
      <div class="d-flex align-center">
        <v-btn
          @click="$router.back()"
          icon
          variant="text"
          color="primary"
          class="mr-3"
        >
          <v-icon>mdi-arrow-left</v-icon>
        </v-btn>
        <div>
          <h1 class="text-h4 font-weight-bold text-primary">
            个人资料
          </h1>
          <p class="text-body-1 text-grey-darken-1 mt-1">
            管理您的账户信息
          </p>
        </div>
      </div>
    </div>

    <v-row>
      <v-col cols="12" md="8">
        <v-card elevation="2">
          <v-card-title>基本信息</v-card-title>
          <v-card-text>
            <v-form ref="form_ref" v-model="form_valid">
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="form_data.username"
                    label="用户名"
                    variant="outlined"
                    readonly
                    prepend-inner-icon="mdi-account"
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="form_data.email"
                    label="邮箱"
                    variant="outlined"
                    readonly
                    prepend-inner-icon="mdi-email"
                  />
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="form_data.real_name"
                    :rules="[(v) => !!v || '姓名不能为空']"
                    label="姓名"
                    variant="outlined"
                    :disabled="loading"
                    prepend-inner-icon="mdi-account-outline"
                  />
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-text-field
                    :model-value="masked_phone"
                    label="手机号"
                    variant="outlined"
                    readonly
                    prepend-inner-icon="mdi-phone"
                    hide-details
                  />
                  <v-btn
                    class="mt-2"
                    variant="outlined"
                    color="primary"
                    size="small"
                    @click="open_phone_dialog"
                  >
                    编辑手机号
                  </v-btn>
                </v-col>
              </v-row>

              <v-row>
                <v-col cols="12">
                  <v-textarea
                    v-model="form_data.address"
                    label="地址"
                    variant="outlined"
                    :disabled="loading"
                    rows="2"
                  />
                </v-col>
              </v-row>
 
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn
              @click="reset_form"
              :disabled="loading"
              variant="outlined"
            >
              重置
            </v-btn>
            <v-btn
              @click="save_profile"
              :loading="loading"
              :disabled="!form_valid || !has_changes"
              color="primary"
            >
              保存更改
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card elevation="2" class="mb-4">
          <v-card-title>账户状态</v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item>
                <v-list-item-title>账户类型</v-list-item-title>
                <v-list-item-subtitle>
                  <v-chip :color="user?.is_admin ? 'error' : 'primary'" size="small">
                    {{ user?.is_admin ? '管理员' : '普通用户' }}
                  </v-chip>
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title>用户ID</v-list-item-title>
                <v-list-item-subtitle>{{ user?.customer_id }}</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <v-list-item-title>创建时间</v-list-item-title>
                <v-list-item-subtitle>{{ format_date(user?.registration_date) }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>

        <v-card elevation="2">
          <v-card-title>安全设置</v-card-title>
          <v-card-text>
            <v-btn
              @click="$router.push('/change-password')"
              block
              variant="outlined"
              color="warning"
              prepend-icon="mdi-lock-reset"
              class="mb-3"
            >
              修改密码
            </v-btn>
            <v-btn
              @click="show_delete_dialog = true"
              block
              variant="outlined"
              color="error"
              prepend-icon="mdi-account-remove"
            >
              删除账户
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 成功/错误提示 -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.message }}
    </v-snackbar>

    <!-- 删除账户确认对话框 -->
    <v-dialog v-model="show_delete_dialog" max-width="400">
      <v-card>
        <v-card-title class="text-error">确认删除账户</v-card-title>
        <v-card-text>
          此操作不可逆转。删除账户将永久移除您的所有数据。

          <v-text-field
            v-model="delete_password"
            class="mt-4"
            label="请输入当前密码以确认"
            type="password"
            variant="outlined"
            prepend-inner-icon="mdi-lock"
            :disabled="loading"
            :rules="[(v) => !!v || '请输入密码']"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="show_delete_dialog = false">取消</v-btn>
          <v-btn @click="delete_account" :disabled="!delete_password" :loading="loading" color="error" variant="elevated">
            确认删除
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
 
    <!-- 编辑手机号对话框 -->
    <v-dialog v-model="show_phone_dialog" max-width="420">
      <v-card>
        <v-card-title>编辑手机号</v-card-title>
        <v-card-text>
          <v-form ref="phone_form_ref" v-model="phone_form_valid">
            <v-text-field
              v-model="new_phone"
              :rules="phone_rules"
              label="手机号"
              variant="outlined"
              prepend-inner-icon="mdi-phone"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="show_phone_dialog = false">取消</v-btn>
          <v-btn :disabled="!phone_form_valid" :loading="loading" color="primary" @click="save_phone">
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { auth_api } from '../api'

const router = useRouter()
const auth_store = useAuthStore()

const form_ref = ref()
const form_valid = ref(false)
const loading = ref(false)
const show_delete_dialog = ref(false)
const show_phone_dialog = ref(false)
const delete_password = ref('')
const new_phone = ref('')
const phone_form_ref = ref()
const phone_form_valid = ref(false)

const snackbar = reactive({
  show: false,
  message: '',
  color: 'success'
})

const form_data = reactive({
  username: '',
  email: '',
  real_name: '',
  phone: '',
  address: ''
})

const original_data = ref({})

const user = computed(() => auth_store.user)

const role_options = []

const masked_phone = computed(() => {
  const p = String(form_data.phone || '')
  if (!p) return ''
  const last4 = p.slice(-4)
  return '*'.repeat(Math.max(p.length - 4, 0)) + last4
})

const phone_rules = [
  (v: string) => !!v || '请输入手机号',
  (v: string) => /^1[3-9]\d{9}$/.test(v) || '请输入有效的手机号码'
]

const has_changes = computed(() => {
  return JSON.stringify(form_data) !== JSON.stringify(original_data.value)
})

const format_date = (date_string?: string) => {
  if (!date_string) return '未知'
  return new Date(date_string).toLocaleDateString('zh-CN')
}

const load_profile = () => {
  if (user.value) {
    const data = {
      username: user.value.username || '',
      email: user.value.email || '',
      real_name: user.value.real_name || '',
      phone: user.value.phone || '',
      address: user.value.address || ''
    }
    Object.assign(form_data, data)
    original_data.value = { ...data }
  }
}

const reset_form = () => {
  Object.assign(form_data, original_data.value)
}

const save_profile = async () => {
  if (!form_valid.value) return
  
  loading.value = true
  try {
    const payload: any = {
      real_name: form_data.real_name,
      address: form_data.address
    }
    if (form_data.phone && form_data.phone.trim()) {
      payload.phone = form_data.phone
    }
    await auth_api.update_profile(payload)
    
    await auth_store.fetch_user_profile()
    load_profile()
    
    snackbar.message = '个人资料已更新'
    snackbar.color = 'success'
    snackbar.show = true
    
  } catch (error: any) {
    snackbar.message = error.message || '更新失败'
    snackbar.color = 'error'
    snackbar.show = true
  } finally {
    loading.value = false
  }
}

const delete_account = async () => {
  if (!delete_password.value) {
    snackbar.message = '请输入密码以确认删除'
    snackbar.color = 'error'
    snackbar.show = true
    return
  }

  loading.value = true
  try {
    await auth_api.delete_account(delete_password.value)

    // 注销成功：清理本地登录态并跳转登录页
    auth_store.logout()
    snackbar.message = '账户已注销'
    snackbar.color = 'success'
    snackbar.show = true

    show_delete_dialog.value = false
    delete_password.value = ''
    await router.push('/auth/login')
  } catch (error: any) {
    snackbar.message = error.message || '注销失败'
    snackbar.color = 'error'
    snackbar.show = true
  } finally {
    loading.value = false
  }
}

const open_phone_dialog = () => {
  new_phone.value = form_data.phone || ''
  show_phone_dialog.value = true
}

const save_phone = async () => {
  if (!phone_form_valid.value) return
  loading.value = true
  try {
    await auth_api.update_profile({ phone: new_phone.value })
    await auth_store.fetch_user_profile()
    form_data.phone = auth_store.user?.phone || ''
    snackbar.message = '手机号已更新'
    snackbar.color = 'success'
    snackbar.show = true
    show_phone_dialog.value = false
  } catch (error: any) {
    snackbar.message = error.message || '更新失败'
    snackbar.color = 'error'
    snackbar.show = true
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await auth_store.fetch_user_profile()
  load_profile()
})
</script>

<style scoped>
.profile-view {
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

h1 {
  background: linear-gradient(135deg, var(--primary-100) 0%, var(--accent-100) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

:deep(.v-card) {
  transition: all 300ms ease;
}

:deep(.v-card:hover) {
  transform: translateY(-2px);
}
</style>
