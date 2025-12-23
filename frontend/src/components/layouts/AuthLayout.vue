<!--
  认证页面布局 - 简洁版本
-->
<template>
  <v-app>
    <v-main class="auth-layout">
      <v-container 
        fluid 
        fill-height
        class="d-flex align-center justify-center"
      >
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
// 简化的认证布局组件
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--bg-100) 0%, var(--bg-200) 100%);
  position: relative;
  overflow: hidden;
}

/* 添加微妙的背景纹理 */
.auth-layout::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 30%, rgba(247, 202, 201, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(235, 235, 235, 0.1) 0%, transparent 50%);
  animation: backgroundShift 20s ease-in-out infinite;
}

@keyframes backgroundShift {
  0%, 100% {
    transform: scale(1) rotate(0deg);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.05) rotate(0.5deg);
    opacity: 0.4;
  }
}
</style>