export const JUDGE_STATUS = {
  '-10': {
    name: 'Not Submitted',
    short: 'NS',
    color: 'gray',
    type: 'info',
    rgb: '#909399'
  },
  '-5': {
    name: 'Submitted Unknown Result',
    short: 'SNR',
    color: 'gray',
    type: 'info',
    rgb: '#909399'
  },
  '-3': {
    name: 'Presentation Error',
    short: 'PE',
    color: 'yellow',
    type: 'warning',
    rgb: '#f90'
  },
  '-2': {
    name: 'Compile Error',
    short: 'CE',
    color: 'yellow',
    type: 'warning',
    rgb: '#f90'
  },
  '-1': {
    name: 'Wrong Answer',
    short: 'WA',
    color: 'red',
    type: 'danger',
    rgb: '#ed3f14'
  },
  '0': {
    name: 'Accepted',
    short: 'AC',
    color: 'green',
    type: 'success',
    rgb: '#19be6b'
  },
  '1': {
    name: 'Time Limit Exceeded',
    short: 'TLE',
    color: 'red',
    type: 'danger',
    rgb: '#ed3f14'
  },
  '2': {
    name: 'Memory Limit Exceeded',
    short: 'MLE',
    color: 'red',
    type: 'danger',
    rgb: '#ed3f14'
  },
  '3': {
    name: 'Runtime Error',
    short: 'RE',
    color: 'red',
    type: 'danger',
    rgb: '#ed3f14'
  },
  '4': {
    name: 'System Error',
    short: 'SE',
    color: 'gray',
    type: 'info',
    rgb: '#909399'
  },
  '5': {
    name: 'Pending',
    color: 'yellow',
    type: 'warning',
    rgb: '#f90'
  },
  '6': {
    name: 'Compiling',
    short: 'CP',
    color: 'green',
    type: 'info',
    rgb: '#25bb9b'
  },
  '7': {
    name: 'Judging',
    color: 'blue',
    type: '',
    rgb: '#2d8cf0'
  },
  '8': {
    name: 'Partial Accepted',
    short: 'PAC',
    color: 'blue',
    type: '',
    rgb: '#2d8cf0'
  },
  '9': {
    name: 'Submitting',
    color: 'yellow',
    type: 'warning',
    rgb: '#f90'
  },
  '10': {
    name: "Submitted Failed",
    color: 'gray',
    short: 'SF',
    type: 'info',
    rgb: '#909399',
  }
}

export const JUDGE_STATUS_RESERVE = {
  'snr': -5,
  'pe': -3,
  'ce': -2,
  'wa': -1,
  'ac': 0,
  'tle': 1,
  'mle': 2,
  're': 3,
  'se': 4,
  'Compiling': 5,
  'Pending': 6,
  'Judging': 7,
  'pa': 8,
  'sf': 10,
}

export const PROBLEM_LEVEL = {
  '0': {
    name: {
      'zh-CN': '??????',
      'en-US': 'Extremely Easy',
    },
    color: '#fe4c61'
  },
  '1': {
    name: {
      'zh-CN': '??????-',
      'en-US': 'Easy',
    },
    color: '#f39c11'
  },
  '2': {
    name: {
      'zh-CN': '??????/??????-',
      'en-US': 'Easy-Mid',
    },
    color: '#ffc116'
  },
  '3': {
    name: {
      'zh-CN': '??????+/??????',
      'en-US': 'Mid',
    },
    color: '#52c41a'
  },
  '4': {
    name: {
      'zh-CN': '??????+/??????-',
      'en-US': 'Mid-Hard',
    },
    color: '#3498db'
  },
  '5': {
    name: {
      'zh-CN': '??????/NOI-',
      'en-US': 'Hard',
    },
    color: '#9d3dcf'
  },
  '6': {
    name: {
      'zh-CN': 'NOI/NOI+/CTSC',
      'en-US': 'Extremely Hard',
    },
    color: '#0e1d69'
  }
}

export const REMOTE_OJ = [
  {
    name: 'HDU',
    key: "HDU"
  },
  {
    name: "Codeforces",
    key: "CF"
  },
  {
    name: "POJ",
    key: "POJ"
  },
  {
    name: "GYM",
    key: "GYM"
  },
  {
    name: "AtCoder",
    key: "AC"
  },
  {
    name: "SPOJ",
    key: "SPOJ"
  }
]

export const CONTEST_STATUS = {
  'SCHEDULED': -1,
  'RUNNING': 0,
  'ENDED': 1
}

export const CONTEST_STATUS_REVERSE = {
  '-1': {
    name: 'Scheduled',
    color: '#f90'
  },
  '0': {
    name: 'Running',
    color: '#19be6b'
  },
  '1': {
    name: 'Ended',
    color: '#ed3f14'
  }
}

export const EXAM_STATUS = {
  'SCHEDULED': -1,
  'RUNNING': 0,
  'ENDED': 1
}

export const EXAM_STATUS_REVERSE = {
  '-1': {
    name: 'Scheduled',
    color: '#f90'
  },
  '0': {
    name: 'Running',
    color: '#19be6b'
  },
  '1': {
    name: 'Ended',
    color: '#ed3f14'
  }
}

export const TRAINING_TYPE = {
  'Public': {
    name: 'Public',
    color: 'success'

  },
  'Private': {
    name: 'Private',
    color: 'danger'

  }
}

export const GROUP_TYPE = {
  PUBLIC: 1,
  PROTECTED: 2,
  PRIVATE: 3
}

export const GROUP_TYPE_REVERSE = {
  '1': {
    name: 'Public',
    color: 'success',
    tips: 'Group_Public_Tips',
  },
  '2': {
    name: 'Protected',
    color: 'warning',
    tips: 'Group_Protected_Tips',
  },
  '3': {
    name: 'Private',
    color: 'danger',
    tips: 'Group_Private_Tips',
  }
}

export const QUESTION_TYPE_REVERSE = {
  '1': {
    name: 'Choice',
    color: 'success',
  },
  '2': {
    name: 'Judge',
    color: 'warning',
  },
  '3': {
    name: 'Blank',
    color: 'danger',
  },
  '4': {
    name: 'Answer',
    color: 'info',
  }
}

export const RULE_TYPE = {
  ACM: 0,
  OI: 1
}

export const CONTEST_TYPE_REVERSE = {
  '0': {
    name: 'Public',
    color: 'success',
    tips: 'Contest_Public_Tips',
    submit: true,              // ????????????????????????
    look: true,
  },
  '1': {
    name: 'Private',
    color: 'danger',
    tips: 'Contest_Private_Tips',
    submit: false,         // ????????? ?????????????????????????????????
    look: false,
  },
  '2': {
    name: 'Protected',
    color: 'warning',
    tips: 'Contest_Protected_Tips',
    submit: false,       //????????????????????????????????????????????????????????????????????????
    look: true,
  }
}

export const CONTEST_TYPE = {
  PUBLIC: 0,
  PRIVATE: 1,
  PROTECTED: 2
}

export const EXAM_TYPE_REVERSE = {
  '0': {
    name: 'Exam_Public',
    color: 'success',
    tips: 'Exam_Public_Tips',
    submit: true,
    look: true,
  },
  '1': {
    name: 'Exam_Private',
    color: 'danger',
    tips: 'Exam_Private_Tips',
    submit: false,
    look: false,
  },
  '2': {
    name: 'Exam_Protected',
    color: 'warning',
    tips: 'Exam_Protected_Tips',
    submit: false,
    look: true,
  }
}

export const EXAM_TYPE = {
  PUBLIC: 0,
  PRIVATE: 1,
  PROTECTED: 2
}

export const USER_TYPE = {
  REGULAR_USER: 'user',
  ADMIN: 'admin',
  PROBLEM_ADMIN: 'problem_admin',
  SUPER_ADMIN: 'root'
}

export const STORAGE_KEY = {
  AUTHED: 'authed',
  PROBLEM_CODE: 'ojProblemCode',
  languages: 'languages',
  CONTEST_ANNOUNCE: 'ojContestAnnounce',
  EXAM_ANSWER: 'ojExamAnswer',
  individualLanguageAndTheme: 'ojIndividualLanguageAndTheme',
  CONTEST_RANK_CONCERNED: 'ojContestRankConcerned'
}

export function buildIndividualLanguageAndThemeKey() {
  return `${STORAGE_KEY.individualLanguageAndTheme}`
}

export function buildProblemCodeKey(problemID, contestID = null) {
  if (contestID) {
    return `${STORAGE_KEY.PROBLEM_CODE}_${contestID}_${problemID}`
  }
  return `${STORAGE_KEY.PROBLEM_CODE}_NoContest_${problemID}`
}

export function buildContestAnnounceKey(uid, contestID) {
  return `${STORAGE_KEY.CONTEST_ANNOUNCE}_${uid}_${contestID}`
}

export function buildContestRankConcernedKey(contestID) {
  return `${STORAGE_KEY.CONTEST_RANK_CONCERNED}_${contestID}`
}

export function buildExamAnswerKey(examID) {
  return `${STORAGE_KEY.EXAM_ANSWER}_${examID}`
}

