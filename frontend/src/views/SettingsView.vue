<template>
  <div class="settings-view">
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
            系统设置
          </h1>
          <p class="text-body-1 text-grey-darken-1 mt-1">
            管理您的账户偏好和系统配置
          </p>
        </div>
      </div>
    </div>

    <v-row>
      <v-col cols="12" md="3">
        <!-- 设置导航 -->
        <v-card elevation="2">
          <v-list density="compact" nav>
            <v-list-item
              v-for="section in setting_sections"
              :key="section.key"
              :value="section.key"
              :active="active_section === section.key"
              @click="active_section = section.key"
            >
              <template #prepend>
                <v-icon>{{ section.icon }}</v-icon>
              </template>
              <v-list-item-title>{{ section.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>

      <v-col cols="12" md="9">
        <!-- 安全设置 -->
        <v-card v-show="active_section === 'security'" elevation="2" class="mb-4">
          <v-card-title>安全设置</v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item>
                <v-list-item-title>双因素认证</v-list-item-title>
                <v-list-item-subtitle>为您的账户增加额外的安全保护</v-list-item-subtitle>
                <template #append>
                  <v-switch
                    v-model="settings.security.two_factor_enabled"
                    @update:model-value="save_settings"
                    color="primary"
                    hide-details
                  />
                </template>
              </v-list-item>

              <v-divider />

              <v-list-item>
                <v-list-item-title>自动登出</v-list-item-title>
                <v-list-item-subtitle>闲置指定时间后自动退出登录</v-list-item-subtitle>
                <template #append>
                  <v-select
                    v-model="settings.security.auto_logout_minutes"
                    :items="auto_logout_options"
                    @update:model-value="save_settings"
                    variant="outlined"
                    density="compact"
                    style="width: 120px;"
                    hide-details
                  />
                </template>
              </v-list-item>

              <v-divider />

              <v-list-item>
                <v-list-item-title>登录日志</v-list-item-title>
                <v-list-item-subtitle>记录账户登录活动</v-list-item-subtitle>
                <template #append>
                  <v-btn
                    @click="show_login_logs = true"
                    variant="outlined"
                    size="small"
                  >
                    查看日志
                  </v-btn>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>

        <!-- 界面设置 -->
        <v-card v-show="active_section === 'interface'" elevation="2" class="mb-4">
          <v-card-title>界面设置</v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item>
                <v-list-item-title>主题模式</v-list-item-title>
                <v-list-item-subtitle>选择界面的明暗主题</v-list-item-subtitle>
                <template #append>
                  <v-btn-toggle
                    v-model="settings.interface.theme"
                    @update:model-value="save_settings"
                    variant="outlined"
                    divided
                  >
                    <v-btn value="light" size="small">浅色</v-btn>
                    <v-btn value="dark" size="small">深色</v-btn>
                    <v-btn value="auto" size="small">自动</v-btn>
                  </v-btn-toggle>
                </template>
              </v-list-item>

              <v-divider />

              <v-list-item>
                <v-list-item-title>语言</v-list-item-title>
                <v-list-item-subtitle>选择界面显示语言</v-list-item-subtitle>
                <template #append>
                  <v-select
                    v-model="settings.interface.language"
                    :items="language_options"
                    @update:model-value="save_settings"
                    variant="outlined"
                    density="compact"
                    style="width: 120px;"
                    hide-details
                  />
                </template>
              </v-list-item>

              <v-divider />

              <v-list-item>
                <v-list-item-title>数据刷新间隔</v-list-item-title>
                <v-list-item-subtitle>仪表板数据自动刷新频率</v-list-item-subtitle>
                <template #append>
                  <v-select
                    v-model="settings.interface.refresh_interval"
                    :items="refresh_interval_options"
                    @update:model-value="save_settings"
                    variant="outlined"
                    density="compact"
                    style="width: 120px;"
                    hide-details
                  />
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>

        <!-- 通知设置 -->
        <v-card v-show="active_section === 'notifications'" elevation="2" class="mb-4">
          <v-card-title>通知设置</v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item>
                <v-list-item-title>邮件通知</v-list-item-title>
                <v-list-item-subtitle>重要事件的邮件提醒</v-list-item-subtitle>
                <template #append>
                  <v-switch
                    v-model="settings.notifications.email_enabled"
                    @update:model-value="save_settings"
                    color="primary"
                    hide-details
                  />
                </template>
              </v-list-item>

              <v-divider />

              <v-list-item>
                <v-list-item-title>危险行为通知</v-list-item-title>
                <v-list-item-subtitle>检测到危险行为时立即通知</v-list-item-subtitle>
                <template #append>
                  <v-switch
                    v-model="settings.notifications.danger_alerts"
                    @update:model-value="save_settings"
                    color="error"
                    hide-details
                  />
                </template>
              </v-list-item>

              <v-divider />

              <v-list-item>
                <v-list-item-title>系统维护通知</v-list-item-title>
                <v-list-item-subtitle>系统更新和维护信息</v-list-item-subtitle>
                <template #append>
                  <v-switch
                    v-model="settings.notifications.system_updates"
                    @update:model-value="save_settings"
                    color="info"
                    hide-details
                  />
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>

        <!-- 数据设置 -->
        <v-card v-show="active_section === 'data'" elevation="2" class="mb-4">
          <v-card-title>数据管理</v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item>
                <v-list-item-title>数据保留期</v-list-item-title>
                <v-list-item-subtitle>历史数据保留时间</v-list-item-subtitle>
                <template #append>
                  <v-select
                    v-model="settings.data.retention_days"
                    :items="retention_options"
                    @update:model-value="save_settings"
                    variant="outlined"
                    density="compact"
                    style="width: 120px;"
                    hide-details
                  />
                </template>
              </v-list-item>

              <v-divider />

              <v-list-item>
                <v-list-item-title>导出数据</v-list-item-title>
                <v-list-item-subtitle>下载您的个人数据副本</v-list-item-subtitle>
                <template #append>
                  <v-btn
                    @click="export_data"
                    :loading="exporting"
                    variant="outlined"
                    size="small"
                    color="primary"
                  >
                    导出
                  </v-btn>
                </template>
              </v-list-item>

              <v-divider />

              <v-list-item>
                <v-list-item-title class="text-error">删除账户</v-list-item-title>
                <v-list-item-subtitle>永久注销账户（需要输入密码确认，不可恢复）</v-list-item-subtitle>
                <template #append>
                  <v-btn
                    @click="show_delete_account_dialog = true"
                    variant="outlined"
                    size="small"
                    color="error"
                  >
                    删除账户
                  </v-btn>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 成功/错误提示 -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.message }}
    </v-snackbar>

    <!-- 登录日志对话框 -->
    <v-dialog v-model="show_login_logs" max-width="600">
      <v-card>
        <v-card-title>登录日志</v-card-title>
        <v-card-text>
          <v-list density="compact">
            <v-list-item
              v-for="(log, index) in login_logs"
              :key="index"
            >
              <template #prepend>
                <v-icon :color="log.success ? 'success' : 'error'">
                  {{ log.success ? 'mdi-check-circle' : 'mdi-alert-circle' }}
                </v-icon>
              </template>
              <v-list-item-title>{{ log.ip }}</v-list-item-title>
              <v-list-item-subtitle>
                {{ log.time }} - {{ log.location }}
              </v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="show_login_logs = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 删除账户确认对话框 -->
    <v-dialog v-model="show_delete_account_dialog" max-width="500" persistent>
      <v-card>
        <v-card-title class="text-error d-flex align-center">
          <v-icon class="mr-2" size="large">mdi-alert-circle</v-icon>
          确认删除账户
        </v-card-title>
        <v-card-text>
          <v-alert type="error" variant="tonal" class="mb-4">
            <div class="text-subtitle-2 font-weight-bold mb-2">警告：此操作不可逆！</div>
            <ul class="text-body-2 ml-4">
              <li>您的账户将被永久注销</li>
              <li>所有个人信息将被删除</li>
              <li>历史订单记录将被保留用于财务审计</li>
              <li>账户余额将无法找回</li>
            </ul>
          </v-alert>

          <v-text-field
            v-model="delete_password"
            label="请输入您的密码以确认"
            type="password"
            variant="outlined"
            density="comfortable"
            placeholder="输入密码"
            :error-messages="delete_password_error"
            @keyup.enter="confirm_delete_account"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="cancel_delete_account" :disabled="deleting_account">取消</v-btn>
          <v-btn
            @click="confirm_delete_account"
            :loading="deleting_account"
            :disabled="!delete_password"
            color="error"
            variant="elevated"
          >
            确认删除账户
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 清除数据确认对话框 -->
    <v-dialog v-model="show_clear_data_dialog" max-width="400">
      <v-card>
        <v-card-title class="text-error">确认清除数据</v-card-title>
        <v-card-text>
          此操作将永久删除您的所有数据，包括事件记录、统计信息等。此操作不可逆转。
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="show_clear_data_dialog = false">取消</v-btn>
          <v-btn @click="clear_all_data" color="error" variant="elevated">
            确认清除
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { auth_api } from '@/api'

const auth_store = useAuthStore()
const router = useRouter()

const active_section = ref('security')
const show_login_logs = ref(false)
const show_clear_data_dialog = ref(false)
const show_delete_account_dialog = ref(false)
const exporting = ref(false)
const deleting_account = ref(false)
const delete_password = ref('')
const delete_password_error = ref('')

const snackbar = reactive({
  show: false,
  message: '',
  color: 'success'
})

const settings = reactive({
  security: {
    two_factor_enabled: false,
    auto_logout_minutes: 30
  },
  interface: {
    theme: 'auto',
    language: 'zh-CN',
    refresh_interval: 30
  },
  notifications: {
    email_enabled: true,
    danger_alerts: true,
    system_updates: true
  },
  data: {
    retention_days: 90
  }
})

const setting_sections = [
  { key: 'security', title: '安全', icon: 'mdi-shield-account' },
  { key: 'interface', title: '界面', icon: 'mdi-palette' },
  { key: 'notifications', title: '通知', icon: 'mdi-bell' },
  { key: 'data', title: '数据', icon: 'mdi-database' }
]

const auto_logout_options = [
  { title: '15分钟', value: 15 },
  { title: '30分钟', value: 30 },
  { title: '1小时', value: 60 },
  { title: '2小时', value: 120 },
  { title: '永不', value: 0 }
]

const language_options = [
  { title: '中文', value: 'zh-CN' },
  { title: 'English', value: 'en-US' }
]

const refresh_interval_options = [
  { title: '10秒', value: 10 },
  { title: '30秒', value: 30 },
  { title: '1分钟', value: 60 },
  { title: '5分钟', value: 300 },
  { title: '手动', value: 0 }
]

const retention_options = [
  { title: '30天', value: 30 },
  { title: '90天', value: 90 },
  { title: '180天', value: 180 },
  { title: '1年', value: 365 },
  { title: '永久', value: 0 }
]

const login_logs = ref([
  {
    time: '2024-01-15 14:30:25',
    ip: '192.168.1.100',
    location: '北京市',
    success: true
  },
  {
    time: '2024-01-14 09:15:10',
    ip: '192.168.1.100',
    location: '北京市',
    success: true
  },
  {
    time: '2024-01-13 18:45:33',
    ip: '203.123.45.67',
    location: '未知位置',
    success: false
  }
])

const load_settings = () => {
  const saved_settings = localStorage.getItem('bookstore_settings')
  if (saved_settings) {
    Object.assign(settings, JSON.parse(saved_settings))
  }
}

const save_settings = () => {
  localStorage.setItem('bookstore_settings', JSON.stringify(settings))
  snackbar.message = '设置已保存'
  snackbar.color = 'success'
  snackbar.show = true
}

const export_data = async () => {
  exporting.value = true
  try {
    // 模拟导出过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    const data = {
      user_profile: 'user_data',
      settings: settings,
      export_time: new Date().toISOString()
    }
    
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `bookstore_data_${new Date().toISOString().split('T')[0]}.json`
    a.click()
    URL.revokeObjectURL(url)
    
    snackbar.message = '数据导出成功'
    snackbar.color = 'success'
    snackbar.show = true
    
  } catch (error) {
    snackbar.message = '导出失败'
    snackbar.color = 'error'
    snackbar.show = true
  } finally {
    exporting.value = false
  }
}

const clear_all_data = async () => {
  try {
    // 实际实现中这里会调用API
    localStorage.removeItem('bookstore_settings')

    snackbar.message = '数据已清除'
    snackbar.color = 'success'
    snackbar.show = true

  } catch (error) {
    snackbar.message = '清除失败'
    snackbar.color = 'error'
    snackbar.show = true
  }
  show_clear_data_dialog.value = false
}

// 取消删除账户
const cancel_delete_account = () => {
  show_delete_account_dialog.value = false
  delete_password.value = ''
  delete_password_error.value = ''
}

// 确认删除账户
const confirm_delete_account = async () => {
  if (!delete_password.value) {
    delete_password_error.value = '请输入密码'
    return
  }

  delete_password_error.value = ''
  deleting_account.value = true

  try {
    await auth_api.delete_account(delete_password.value)

    // 删除成功，清除本地数据并退出登录
    auth_store.logout()

    snackbar.message = '账户已成功注销'
    snackbar.color = 'success'
    snackbar.show = true

    // 延迟跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 2000)

  } catch (error: any) {
    delete_password_error.value = error.response?.data?.message || '删除失败，请检查密码是否正确'
    deleting_account.value = false
  }
}

onMounted(() => {
  load_settings()
})
</script>

<style scoped>
.settings-view {
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

:deep(.v-list-item--active) {
  background: var(--primary-100);
  color: white;
}
</style>