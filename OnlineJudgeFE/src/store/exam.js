import moment from 'moment'
import api from '@/common/api'
import { EXAM_STATUS, EXAM_TYPE } from '@/common/constants'
import time from '@/common/time'
const state = {
  now: moment(),
  intoAccess: false,
  submitAccess: false,
  exam: {
    auth: EXAM_TYPE.PUBLIC,
    rankShowName: 'username',
  },
  examProblemList: [],
  itemVisible: {
    table: true,
    chart: false,
  },
  groupExamAuth: 0,
  answerList: {},
}

const getters = {
  examStatus: (state, getters) => {
    return state.exam.status
  },
  examRuleType: (state, getters) => {
    return state.exam.type
  },
  isExamAdmin: (state, getters, _, rootGetters) => {
    return rootGetters.isAuthenticated &&
      (state.exam.author === rootGetters.userInfo.username || rootGetters.isSuperAdmin || state.groupExamAuth == 5)
  },
  examCanSubmit: (state, getters) => {
    return state.intoAccess || state.submitAccess || state.exam.auth === EXAM_TYPE.PUBLIC || getters.isExamAdmin
  },
  examMenuDisabled: (state, getters) => {
    if (getters.isExamAdmin) return false
    if (getters.examStatus === EXAM_STATUS.SCHEDULED) return true

    if (state.exam.auth === EXAM_TYPE.PRIVATE) {
      return !state.intoAccess
    }

  },
  examAutoRealScore: (state, getters) => {
    return state.exam.autoRealScore
  },

  ExamRealScorePermission: (state, getters, _, rootGetters) => {
    if (getters.examStatus === EXAM_STATUS.ENDED) {
      return true
    }
    if (getters.isExamAdmin) {
      return true
    }
    return state.exam.realScore === true
  },
  examProblemSubmitDisabled: (state, getters, _, rootGetters) => {
    if (getters.examStatus === EXAM_STATUS.ENDED) {
      return true
    } else if (getters.examStatus === EXAM_STATUS.SCHEDULED) {
      return !getters.isExamAdmin
    }
    return !rootGetters.isAuthenticated
  },
  examPasswordFormVisible: (state, getters) => {
    return state.exam.auth !== EXAM_TYPE.PUBLIC && state.exam.auth !== EXAM_TYPE.PROTECTED && !state.intoAccess && !getters.isExamAdmin
  },
  examStartTime: (state) => {
    return moment(state.exam.startTime)
  },
  examEndTime: (state) => {
    return moment(state.exam.endTime)
  },
  examCountdown: (state, getters) => {
    if (getters.examStatus === EXAM_STATUS.SCHEDULED) {

      let durationMs = getters.examStartTime.diff(state.now, 'seconds')

      let duration = moment.duration(durationMs, 'seconds')
      if (duration.weeks() > 0) {
        return 'Start At ' + duration.humanize()
      }

      if (duration.asSeconds() <= 0) {
        state.exam.status = EXAM_STATUS.RUNNING
      }

      let texts = time.secondFormat(durationMs)
      return '-' + texts
    } else if (getters.examStatus === EXAM_STATUS.RUNNING) {
      if (getters.examEndTime.diff(state.now, 'seconds') > 0) {
        let texts = time.secondFormat(getters.examEndTime.diff(state.now, 'seconds'))
        return '-' + texts
      } else {
        state.exam.status = EXAM_STATUS.ENDED
        return "00:00:00"
      }

    } else {
      return 'Ended'
    }
  },
  examBeginToNowDuration: (state, getters) => {
    return moment.duration(state.now.diff(getters.examStartTime, 'seconds'), 'seconds').asSeconds()
  },
  examProgressValue: (state, getters) => {
    if (getters.examStatus === EXAM_STATUS.SCHEDULED) {
      return 0
    } else if (getters.examStatus === EXAM_STATUS.RUNNING) {
      let duration = getters.examBeginToNowDuration
      return (duration / state.exam.duration) * 100
    } else {
      return 100
    }
  },
}

const mutations = {
  changeExam(state, payload) {
    state.exam = payload.exam
  },
  changeExamItemVisible(state, payload) {
    state.itemVisible = { ...state.itemVisible, ...payload }
  },
  changeRankForceUpdate(state, payload) {
    state.forceUpdate = payload.value
  },
  changeRankRemoveStar(state, payload) {
    state.removeStar = payload.value
  },
  changeAnswerList(state, payload) {
    state.answerList = payload.value
  },
  changeExamProblemList(state, payload) {
    state.examProblemList = payload.examProblemList
    let tmp = {}
    for (var j = 0, len = payload.examProblemList.length; j < len; j++) {
      tmp[payload.examProblemList[j].displayId] = payload.examProblemList[j].color
    }
    state.disPlayIdMapColor = tmp
  },
  changeExamRankLimit(state, payload) {
    state.rankLimit = payload.rankLimit
  },
  examIntoAccess(state, payload) {
    state.intoAccess = payload.intoAccess
  },
  changeGroupExamAuth(state, payload) {
    state.groupExamAuth = payload.groupExamAuth
  },
  examSubmitAccess(state, payload) {
    state.submitAccess = payload.submitAccess
  },
  clearExam(state) {
    state.exam = {}
    state.examProblemList = []
    state.intoAccess = false
    state.submitAccess = false
    state.itemVisible = {
      table: true,
      chart: false,
      realName: false
    }
    state.groupExamAuth = 0
  },
  now(state, payload) {
    state.now = payload.now
  },
  nowAdd1s(state) {
    state.now = moment(state.now.add(1, 's'))
  },
}

const actions = {
  getExam({ commit, rootState, dispatch }) {
    return new Promise((resolve, reject) => {
      api.getExam(rootState.route.params.examID).then((res) => {
        resolve(res)
        let exam = res.data.data
        commit('changeExam', { exam: exam })
        if (exam.gid) {
          dispatch('getGroupExamAuth', { gid: exam.gid })
        }
        commit('now', { now: moment(exam.now) })
        if (exam.auth == EXAM_TYPE.PRIVATE) {
          dispatch('getExamAccess', { auth: EXAM_TYPE.PRIVATE })
        } else if (exam.auth == EXAM_TYPE.PROTECTED) {
          dispatch('getExamAccess', { auth: EXAM_TYPE.PROTECTED })
        }
      }, err => {
        reject(err)
      })
    })
  },

  getExamProblemList({ commit, rootState }) {
    return new Promise((resolve, reject) => {
      api.getExamProblemList(rootState.route.params.examID).then(res => {
        resolve(res)
        commit('changeExamProblemList', { examProblemList: res.data.data })
      }, (err) => {
        commit('changeExamProblemList', { examProblemList: [] })
        reject(err)
      })
    })
  },
  getExamAccess({ commit, rootState }, examType) {
    return new Promise((resolve, reject) => {
      api.getExamAccess(rootState.route.params.examID).then(res => {
        if (examType.auth == EXAM_TYPE.PRIVATE) {
          commit('examIntoAccess', { intoAccess: res.data.data.access })
        } else {
          commit('examSubmitAccess', { submitAccess: res.data.data.access })
        }
        resolve(res)
      }).catch()
    })
  },
  getGroupExamAuth({ commit, rootState }, gid) {
    return new Promise((resolve, reject) => {
      api.getGroupAuth(gid.gid).then(res => {
        commit('changeGroupExamAuth', { groupExamAuth: res.data.data })
        resolve(res)
      }).catch()
    })
  }
}

export default {
  state,
  mutations,
  getters,
  actions
}
