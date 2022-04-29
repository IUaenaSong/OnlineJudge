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
    rankShowName:'username',
  },
  examProblems: [],
  itemVisible: {
    table: true,
    chart: false,
  },
  groupExamAuth: 0,
}

const getters = {
  examStatus: (state, getters) => {
    return state.exam.status;
  },
  examRuleType: (state,getters) => {
    return state.exam.type;
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
    if(getters.examStatus === EXAM_STATUS.SCHEDULED) return true

    if (state.exam.auth === EXAM_TYPE.PRIVATE) {
      return !state.intoAccess
    }
    
  },

  ExamRealTimePermission: (state, getters, _, rootGetters) => {
    if (getters.examStatus === EXAM_STATUS.ENDED) {
      return true
    }
    if (getters.isExamAdmin) {
      return true
    }
    if (state.exam.sealRank === true) {
      return !state.now.isAfter(moment(state.exam.sealRankTime))
    } else {
      return true
    }
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
    return state.exam.auth !== EXAM_TYPE.PUBLIC &&state.exam.auth !== EXAM_TYPE.PROTECTED &&!state.intoAccess && !getters.isExamAdmin 
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

      if(duration.asSeconds()<=0){
        state.exam.status = EXAM_STATUS.RUNNING
      }

      let texts = time.secondFormat(durationMs)
      return '-' + texts
    } else if (getters.examStatus === EXAM_STATUS.RUNNING) {
      if(getters.examEndTime.diff(state.now, 'seconds')>0){
        let texts = time.secondFormat(getters.examEndTime.diff(state.now, 'seconds'))
        return '-' + texts
      }else{
        state.exam.status = EXAM_STATUS.ENDED
        return "00:00:00"
      }
      
    } else {
      return 'Ended'
    }
  },
  examBeginToNowDuration:(state,getters) => {
    return moment.duration(state.now.diff(getters.examStartTime, 'seconds'), 'seconds').asSeconds()
  },
  examProgressValue:(state,getters) => {
    if (getters.examStatus === EXAM_STATUS.SCHEDULED) {
      return 0;
    } else if (getters.examStatus === EXAM_STATUS.RUNNING) {
      let duration = getters.examBeginToNowDuration
      return (duration / state.exam.duration)*100
    }else{
      return 100;
    }
  },
}

const mutations = {
  changeExam (state, payload) {
    state.exam = payload.exam
  },
  changeExamItemVisible(state, payload) {
    state.itemVisible = {...state.itemVisible, ...payload}
  },
  changeRankForceUpdate (state, payload) {
    state.forceUpdate = payload.value
  },
  changeRankRemoveStar(state, payload){
    state.removeStar = payload.value
  },
  changeConcernedList(state, payload){
    state.concernedList = payload.value
  },
  changeExamProblems(state, payload) {
    state.examProblems = payload.examProblems;
    let tmp={};
    for(var j = 0,len = payload.examProblems.length; j < len; j++){
      tmp[payload.examProblems[j].displayId] = payload.examProblems[j].color;
    }
    state.disPlayIdMapColor = tmp;
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
  clearExam (state) {
    state.exam = {}
    state.examProblems = []
    state.intoAccess = false
    state.submitAccess = false
    state.itemVisible = {
      table: true,
      chart: false,
      realName: false
    }
    state.forceUpdate = false
    state.removeStar = false
    state.groupExamAuth = 0
  },
  now(state, payload) {
    state.now = payload.now
  },
  nowAdd1s (state) {
    state.now = moment(state.now.add(1, 's'))
  },
}

const actions = {
  getExam ({commit, rootState, dispatch}) {
    return new Promise((resolve, reject) => {
      api.getExam(rootState.route.params.examID).then((res) => {
        resolve(res)
        let exam = res.data.data
        commit('changeExam', {exam: exam})
        if (exam.gid) {
          dispatch('getGroupExamAuth', {gid: exam.gid})
        }
        commit('now', {now: moment(exam.now)})
        if (exam.auth == EXAM_TYPE.PRIVATE) {
          dispatch('getExamAccess',{auth:EXAM_TYPE.PRIVATE})
        }else if(exam.auth == EXAM_TYPE.PROTECTED){
          dispatch('getExamAccess',{auth:EXAM_TYPE.PROTECTED})
        }
      }, err => {
        reject(err)
      })
    })
  },
  getScoreBoardExamInfo ({commit, rootState, dispatch}) {
    return new Promise((resolve, reject) => {
      api.getScoreBoardExamInfo(rootState.route.params.examID).then((res) => {
        resolve(res)
        let exam = res.data.data.exam;
        let problemList = res.data.data.problemList;
        commit('changeExam', {exam: exam})
        commit('changeExamProblems', {examProblems: problemList})
        commit('now', {now: moment(exam.now)})
      }, err => {
        reject(err)
      })
    })
  },

  getExamProblems ({commit, rootState}) {
    return new Promise((resolve, reject) => {
      api.getExamProblemList(rootState.route.params.examID).then(res => {
        resolve(res)
        commit('changeExamProblems', {examProblems: res.data.data})
      }, (err) => {
        commit('changeExamProblems', {examProblems: []})
        reject(err)
      })
    })
  },
  getExamAccess ({commit, rootState},examType) {
    return new Promise((resolve, reject) => {
      api.getExamAccess(rootState.route.params.examID).then(res => {
        if(examType.auth == EXAM_TYPE.PRIVATE){
          commit('examIntoAccess', {intoAccess: res.data.data.access})
        }else{
          commit('examSubmitAccess', {submitAccess: res.data.data.access})
        }
        resolve(res)
      }).catch()
    })
  },
  getGroupExamAuth ({commit, rootState}, gid) {
    return new Promise((resolve, reject) => {
      api.getGroupAuth(gid.gid).then(res => {
        commit('changeGroupExamAuth', {groupExamAuth: res.data.data})
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
