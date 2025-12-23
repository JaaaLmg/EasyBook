<template>
  <div class="color-scheme-demo">
    <div class="d-flex align-center justify-space-between mb-6">
      <div>
        <h1 class="text-h4 font-weight-bold text-primary">
          配色方案演示
        </h1>
        <p class="text-body-1 mt-1" style="color: var(--text-100);">
          展示新配色方案在各种组件中的应用效果
        </p>
      </div>
    </div>

    <!-- 配色板展示 -->
    <v-card class="mb-6" elevation="2">
      <v-card-title>配色板</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <h3 class="text-h6 mb-3">主要颜色</h3>
            <div class="color-swatch-group">
              <div class="color-swatch" :style="{ backgroundColor: '#f7cac9' }">
                <span class="color-label">Primary 100</span>
                <span class="color-code">#f7cac9</span>
              </div>
              <div class="color-swatch" :style="{ backgroundColor: '#5c5c5c', color: 'white' }">
                <span class="color-label">Primary 200</span>
                <span class="color-code">#5c5c5c</span>
              </div>
              <div class="color-swatch" :style="{ backgroundColor: '#5c5c5c', color: 'white' }">
                <span class="color-label">Primary 300</span>
                <span class="color-code">#5c5c5c</span>
              </div>
            </div>
          </v-col>
          <v-col cols="12" md="4">
            <h3 class="text-h6 mb-3">强调颜色</h3>
            <div class="color-swatch-group">
              <div class="color-swatch" :style="{ backgroundColor: '#5c5c5c', color: 'white' }">
                <span class="color-label">Accent 100</span>
                <span class="color-code">#5c5c5c</span>
              </div>
              <div class="color-swatch" :style="{ backgroundColor: '#ebebeb' }">
                <span class="color-label">Accent 200</span>
                <span class="color-code">#ebebeb</span>
              </div>
            </div>
          </v-col>
          <v-col cols="12" md="4">
            <h3 class="text-h6 mb-3">背景与文字</h3>
            <div class="color-swatch-group">
              <div class="color-swatch" :style="{ backgroundColor: '#fffff3' }">
                <span class="color-label">Background 100</span>
                <span class="color-code">#fffff3</span>
              </div>
              <div class="color-swatch" :style="{ backgroundColor: '#f5f5e9' }">
                <span class="color-label">Background 200</span>
                <span class="color-code">#f5f5e9</span>
              </div>
              <div class="color-swatch" :style="{ backgroundColor: '#ccccc0' }">
                <span class="color-label">Background 300</span>
                <span class="color-code">#ccccc0</span>
              </div>
            </div>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 组件展示 -->
    <v-row>
      <v-col cols="12" md="6">
        <v-card class="mb-4">
          <v-card-title>按钮样式</v-card-title>
          <v-card-text>
            <div class="d-flex flex-wrap gap-2">
              <v-btn color="primary">主要按钮</v-btn>
              <v-btn color="accent">强调按钮</v-btn>
              <v-btn color="primary" variant="outlined">轮廓按钮</v-btn>
              <v-btn color="accent" variant="text">文字按钮</v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card class="mb-4">
          <v-card-title>芯片样式</v-card-title>
          <v-card-text>
            <div class="d-flex flex-wrap gap-2">
              <v-chip color="primary">主要标签</v-chip>
              <v-chip color="accent">强调标签</v-chip>
              <v-chip color="success">成功</v-chip>
              <v-chip color="warning">警告</v-chip>
              <v-chip color="error">错误</v-chip>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 表单展示 -->
    <v-card class="mb-6">
      <v-card-title>表单样式</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              label="标准输入框"
              placeholder="请输入内容"
              variant="outlined"
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-select
              label="选择框"
              :items="['选项1', '选项2', '选项3']"
              variant="outlined"
            />
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 渐变效果展示 -->
    <v-card>
      <v-card-title>渐变效果</v-card-title>
      <v-card-text>
        <div class="gradient-showcase">
          <div class="gradient-item gradient-1">
            <span>主要渐变</span>
          </div>
          <div class="gradient-item gradient-2">
            <span>强调渐变</span>
          </div>
          <div class="gradient-item gradient-3">
            <span>背景渐变</span>
          </div>
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
// 这是一个纯展示组件，不需要特殊逻辑
</script>

<style scoped>
.color-scheme-demo {
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

.color-swatch-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.color-swatch {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 8px;
  color: var(--text-100);
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.color-label {
  font-size: 14px;
}

.color-code {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.3);
  padding: 2px 6px;
  border-radius: 4px;
}

.gradient-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.gradient-item {
  height: 100px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-100);
  font-weight: bold;
  font-size: 16px;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);
}

.gradient-1 {
  background: linear-gradient(135deg, var(--primary-100) 0%, var(--accent-100) 100%);
  color: white;
}

.gradient-2 {
  background: linear-gradient(135deg, var(--accent-100) 0%, var(--accent-200) 100%);
  color: white;
}

.gradient-3 {
  background: linear-gradient(135deg, var(--bg-200) 0%, var(--bg-300) 100%);
  color: var(--text-100);
  text-shadow: none;
}

/* 主标题样式优化 */
h1 {
  background: linear-gradient(135deg, var(--primary-100) 0%, var(--accent-100) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>