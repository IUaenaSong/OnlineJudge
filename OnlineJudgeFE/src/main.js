import Vue from 'vue'
import App from './App.vue'
import store from './store'
import i18n from '@/i18n'
import router from '@/router'
import '@/plugins/echarts'
import '@/plugins/vxe-table'
import '@/plugins/element-ui'
import '@/plugins/highlight'
import '@/plugins/message'
import '@/plugins/markdown-katex'
import '@/styles/scoreboard.css'

import 'font-awesome/css/font-awesome.min.css'
import axios from 'axios'
import VueClipboard from 'vue-clipboard2'
import filters from '@/common/filters.js'
import VueCropper from 'vue-cropper'
import VueParticles from 'vue-particles'
import SlideVerify from 'vue-monoplasty-slide-verify'
import {Drawer,List,Menu,Icon,AppBar,Button,Divider} from 'muse-ui';
import 'muse-ui/dist/muse-ui.css';

Vue.use(Drawer)
Vue.use(List)
Vue.use(Menu)
Vue.use(Icon)
Vue.use(AppBar)
Vue.use(Button)
Vue.use(Divider)
Vue.use(VueParticles)
Vue.use(VueClipboard)
Vue.use(VueCropper)
Vue.use(SlideVerify)
Vue.prototype.$axios = axios
Vue.config.productionTip = false

Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

new Vue({
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount('#app')

