import { createApp } from 'vue'
// @ts-ignore
import App from '@/App.vue'

document.body.addEventListener('mountVueApp', () => {
  createApp(App).mount('#container')
})
document.body.dispatchEvent(new Event('vueReady'))
