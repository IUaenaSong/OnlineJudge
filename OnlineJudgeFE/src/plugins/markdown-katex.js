import Vue from 'vue'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import '@/styles/markdown.css'
import Katex from '@/plugins/katex'
import Md_Katex from '@iktakahiro/markdown-it-katex'

Vue.use(mavonEditor)
Vue.use(Katex)
Vue.prototype.$markDown = mavonEditor.mavonEditor.getMarkdownIt().use(Md_Katex)