import Vue from 'vue'
import Vuex from 'vuex'
import user from '@/store/user'
import contest from "@/store/contest"
import exam from "@/store/exam"
import training from "@/store/training"
import group from "@/store/group"
import api from '@/common/api'
import i18n from '@/i18n'
import storage from '@/common/storage'
import moment from 'moment'
Vue.use(Vuex)
const rootState = {
  modalStatus: {
    mode: 'Login', // or 'register',
    visible: false
  },
  websiteConfig:{
    recordName:'© 2020-2021',
    projectName:'OJ',
    shortName:'OJ',
    recordUrl:'#',
    projectUrl:'#'
  },
  registerTimeOut: 60,
  emailTimeOut: 90,
  mobileTimeOut: 90,
  resetTimeOut: 90,
  language:storage.get('Web_Language') || 'zh-CN',
}

const rootGetters = {
  'modalStatus'(state) {
    return state.modalStatus
  },
  'registerTimeOut'(state) {
    return state.registerTimeOut
  },
  'emailTimeOut'(state) {
    return state.emailTimeOut
  },
  'mobileTimeOut'(state) {
    return state.mobileTimeOut
  },
  'resetTimeOut'(state) {
    return state.resetTimeOut
  },
  'websiteConfig' (state) {
    return state.websiteConfig
  },
  'webLanguage'(state){
    return state.language
  }
}

const rootMutations = {
  changeModalStatus(state, { mode, visible }) {
    if (mode !== undefined) {
      state.modalStatus.mode = mode
    }
    if (visible !== undefined) {
      state.modalStatus.visible = visible
    }
  },
  changeRegisterTimeOut(state, { time }) {
    if (time !== undefined) {
      state.registerTimeOut = time
    }
  },
  changeEmailTimeOut(state, { time }) {
    if (time !== undefined) {
      state.emailTimeOut = time
    }
  },
  changeMobileTimeOut(state, { time }) {
    if (time !== undefined) {
      state.mobileTimeOut = time
    }
  },
  changeResetTimeOut(state, { time }) {
    if (time !== undefined) {
      state.resetTimeOut = time
    }
  },
  startTimeOut(state, { name }) { // 注册邮件和重置邮件倒计时
    if (state.resetTimeOut == 0 ) {
      state.resetTimeOut = 90
      return;
    }
    if (state.emailTimeOut == 0 ) {
      state.emailTimeOut = 90
      return;
    }
    if (state.mobileTimeOut == 0 ) {
      state.mobileTimeOut = 90
      return;
    }
    if (state.registerTimeOut == 0 ) {
      state.registerTimeOut = 60
      return;
    }
    if (name == 'resetTimeOut') {
      state.resetTimeOut--;
    }
    if (name == 'emailTimeOut') {
      state.emailTimeOut--;
    }
    if (name == 'mobileTimeOut') {
      state.mobileTimeOut--;
    }
    if (name == 'registerTimeOut') {
      state.registerTimeOut--;
    }
    setTimeout(() => {this.commit('startTimeOut', { name: name }) }, 1000);
  },
  changeWebsiteConfig(state, payload) {
    state.websiteConfig = payload.websiteConfig
  },
  changeWebLanguage (state, {language}) {
    if (language) {
      state.language = language
      i18n.locale = language
      moment.locale(language);
    }
    storage.set('Web_Language', language)
  }
}
const rootActions = {
  changeModalStatus({ commit }, payload) {
    commit('changeModalStatus', payload)
  },
  changeResetTimeOut({ commit }, payload) {
    commit('changeResetTimeOut', payload)
  },
  changeEmailTimeOut({ commit }, payload) {
    commit('changeEmailTimeOut', payload)
  },
  changeMobileTimeOut({ commit }, payload) {
    commit('changeMobileTimeOut', payload)
  },
  changeRegisterTimeOut({ commit }, payload) {
    commit('changeRegisterTimeOut', payload)
  },
  startTimeOut({ commit }, payload) {
    commit('startTimeOut', payload)
  },
  changeDomTitle ({commit, state}, payload) {
    if (payload && payload.title) {
      window.document.title = payload.title + ' - ' + (state.websiteConfig.shortName+'').toUpperCase()
    } else {
      window.document.title = state.route.meta.title + ' - '+(state.websiteConfig.shortName+'').toUpperCase()
    }
  },
  getWebsiteConfig ({commit}) {
    api.getWebsiteConfig().then(res => {
      commit('changeWebsiteConfig', {
        websiteConfig: res.data.data
      })
    })
  },
}

export default new Vuex.Store({
  modules: {
    user,
    contest,
    exam,
    training,
    group
  },
  state: rootState,
  getters: rootGetters,
  mutations: rootMutations,
  actions: rootActions,
})
