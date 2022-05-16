import Vue from 'vue'
import 'xe-utils'
import VXETable from 'vxe-table'
import 'vxe-table/lib/style.css'
import VXETablePluginElement from 'vxe-table-plugin-element'
import 'vxe-table-plugin-element/dist/style.css'
import i18n from '@/i18n'

VXETable.setup({
  i18n: (key, value) => i18n.t(key, value)
})
Vue.use(VXETable)
VXETable.use(VXETablePluginElement)