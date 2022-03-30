import Vue from 'vue'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-light.css'

Vue.use({
  install (Vue, options) {
    Vue.directive('highlight', {
      deep: true,
      bind: function (el, binding) {
        Array.from(el.querySelectorAll('pre code')).forEach((target) => {
          if (binding.value) {
            target.textContent = binding.value.replace(/\t/g,"    ");
          }
          hljs.highlightBlock(target)
        })
      },
      componentUpdated: function (el, binding) {
        Array.from(el.querySelectorAll('pre code')).forEach((target) => {
          if (binding.value) {
            target.textContent = binding.value.replace(/\t/g,"    ");
          }
          hljs.highlightBlock(target)
        })
      }
    })
  }
})