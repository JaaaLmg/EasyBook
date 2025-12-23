/**
 * Vuetify3 配置
 * 实现网上书店管理系统浅色主题
 */
import { createVuetify } from 'vuetify'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const light_theme = {
    dark: false,
    colors: {
        // 书店主题配色方案
        primary: '#f7cac9',       // --primary-100: 温柔粉色
        'primary-darken-1': '#e0a5a4', // 主要色深色变体
        'primary-darken-2': '#5c5c5c', // --primary-300: 深灰
        secondary: '#5c5c5c',     // --primary-200: 深灰
        accent: '#5c5c5c',        // --accent-100: 深灰强调色
        'accent-lighten-1': '#ebebeb', // --accent-200: 浅灰
        success: '#81C784',       // 成功色(绿)
        warning: '#FFB74D',       // 警告色(橙)
        error: '#E57373',         // 错误色(红)
        info: '#64B5F6',          // 信息色(蓝)
        surface: '#f5f5e9',       // --bg-200: 浅米色表面
        'surface-variant': '#ccccc0', // --bg-300: 灰米色变体
        background: '#fffff3',    // --bg-100: 米白背景
        'on-background': '#333333', // --text-100: 深灰文字
        'on-surface': '#5c5c5c',  // --text-200: 中灰文字
        'on-primary': '#ffffff',  // 主要色上的文字(白色)
        'on-accent': '#ffffff',   // 强调色上的文字(白色)
    }
}

export default createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi',
        aliases,
        sets: {
            mdi,
        },
    },
    theme: {
        defaultTheme: 'light',
        themes: {
            light: light_theme,
        },
    },
    defaults: {
        VCard: {
            elevation: 2,
            rounded: 'lg',
        },
        VBtn: {
            rounded: 'lg',
            elevation: 1,
        },
        VTextField: {
            rounded: 'lg',
            variant: 'outlined',
        },
    },
})