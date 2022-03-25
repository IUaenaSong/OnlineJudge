import Vue from 'vue'
import Message from 'vue-m-message'
import 'vue-m-message/dist/index.css'

const MsgMain = {
  show(type,msg,duration) {
    return Message({ type: type, message: msg, zIndex: 3000 ,position:'top-center',duration:duration});
  },
  info(text, duration = 3000) {
    this.show('info', text, duration);
  },
  success(text, duration = 3000) {
    this.show('success', text, duration);
  },
  error(text, duration = 3000) {
    this.show('error', text, duration);
  },
  warning(text, duration = 3000) {
    this.show('warning', text, duration);
  },
  loading(text, duration = 3000) {
    this.show('loading', text, duration);
  },
}
Vue.use(function Msg() {
  Vue.prototype.$msg = MsgMain;
})