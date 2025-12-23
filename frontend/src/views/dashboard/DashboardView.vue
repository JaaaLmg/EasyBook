<script setup lang="ts">
import { onMounted } from 'vue'

// 显示角色切换提示
const show_role_tip = () => {
  if (localStorage.getItem('hide_role_tip')) return

  setTimeout(() => {
    const message = `欢迎使用 EasyBook 书店管理系统！

🎭 您可以通过顶部的角色切换按钮在"管理员模式"和"客户模式"之间切换：

📖 客户模式：浏览图书、购物车、下单、账户管理
🛠️ 管理员模式：订单管理（发货）、库存管理（预警触发器）

点击顶部的彩色标签即可切换！`

    alert(message)
    localStorage.setItem('hide_role_tip', 'true')
  }, 1000)
}

onMounted(() => {
  show_role_tip()
})
</script>

<template>
  <div>
    <!-- 仪表板内容 -->
    <v-container fluid>
      <v-row>
        <v-col cols="12">
          <h1 class="text-h4 mb-4" style="color: var(--text-100);">
            管理员仪表板
          </h1>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <v-alert
            type="info"
            variant="tonal"
            prominent
          >
            <v-alert-title>
              <!-- <v-icon>mdi-information</v-icon> -->
              系统说明
            </v-alert-title>
            <div class="mt-2">
              <p class="mb-2">
                <strong>这是一个展示数据库功能的书店管理系统前端。</strong>
              </p>
              <!-- <p class="mb-2">核心数据库功能：</p>
              <ul class="ml-4">
                <li><strong>库存预留机制</strong>：下单时预留库存 (reserved_quantity)</li>
                <li><strong>存储过程</strong>：sp_process_delivery (发货扣库存、释放预留库存)</li>
                <li><strong>触发器</strong>：tr_inventory_low_stock (库存预警自动生成缺书记录)</li>
                <li><strong>信用等级体系</strong>：5级会员制度，自动计算折扣和透支额度</li>
                <li><strong>并发控制</strong>：使用 SELECT FOR UPDATE 防止超卖</li>
              </ul>
              <p class="mt-4">
                <v-icon color="primary">mdi-swap-horizontal</v-icon>
                <strong>提示</strong>：点击顶部的角色切换按钮，可以在"管理员"和"客户"视角之间切换
              </p> -->
            </div>
          </v-alert>
        </v-col>
      </v-row>

      <!-- 快捷入口 -->
      <v-row>
        <v-col cols="12" md="6">
          <v-card elevation="2">
            <v-card-title>
              <v-icon class="mr-2">mdi-clipboard-text</v-icon>
              订单管理
            </v-card-title>
            <v-card-text>
              查看所有订单，执行发货操作（调用 sp_process_delivery 存储过程）
            </v-card-text>
            <v-card-actions>
              <v-btn color="primary" to="/admin/orders">
                前往管理
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>

        <v-col cols="12" md="6">
          <v-card elevation="2">
            <v-card-title>
              <v-icon class="mr-2">mdi-package-variant-closed</v-icon>
              库存管理
            </v-card-title>
            <v-card-text>
              管理图书库存，触发低库存预警（tr_inventory_low_stock 触发器）
            </v-card-text>
            <v-card-actions>
              <v-btn color="primary" to="/admin/inventory">
                前往管理
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

      <!-- 切换到客户模式提示 -->
      <!-- <v-row>
        <v-col cols="12">
          <v-card elevation="2" color="success" variant="tonal">
            <v-card-text class="d-flex align-center">
              <v-icon size="large" class="mr-4">mdi-account-switch</v-icon>
              <div>
                <div class="text-h6 mb-2">想体验客户购书流程？</div>
                <div class="text-body-2">
                  点击顶部的 <strong>"管理员模式"</strong> 按钮，切换到客户模式，体验完整的购书流程：
                  浏览图书 → 加入购物车 → 下单 → 支付 → 查看订单
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row> -->
    </v-container>
  </div>
</template>
