const Home = () => import('@/views/oj/Home.vue')
const SetNewPassword = () => import('@/views/oj/user/SetNewPassword.vue')
const UserHome = () => import('@/views/oj/user/UserHome.vue')
const Setting = () => import('@/views/oj/user/Setting.vue')
const ProblemList = () => import('@/views/oj/problem/ProblemList.vue')
const Logout = () => import('@/views/oj/user/Logout.vue')
const SubmissionList = () => import('@/views/oj/status/SubmissionList.vue')
const SubmissionDetails = () => import('@/views/oj/status/SubmissionDetails.vue')
const ContestList = () => import('@/views/oj/contest/ContestList.vue')
const ProblemDetails = () => import('@/views/oj/problem/ProblemDetails.vue')
const QuestionDetails = () => import('@/views/oj/question/QuestionDetails.vue')
const ACMRank = () => import('@/views/oj/rank/ACMRank.vue')
const OIRank = () => import('@/views/oj/rank/OIRank.vue')
const ContestDetails = () => import('@/views/oj/contest/ContestDetails.vue')
const ExamDetails = () => import('@/views/oj/exam/ExamDetails.vue')
const ACMScoreBoard = () => import('@/views/oj/contest/outside/ACMScoreBoard.vue')
const OIScoreBoard = () => import('@/views/oj/contest/outside/OIScoreBoard.vue')
const ContestProblemList = () => import('@/views/oj/contest/children/ContestProblemList.vue')
const ContestRank = () => import('@/views/oj/contest/children/ContestRank.vue')
const ACMInfoAdmin = () => import('@/views/oj/contest/children/ACMInfoAdmin.vue')
const Announcements = () => import('@/components/oj/common/Announcements.vue')
const ContestComment = () => import('@/views/oj/contest/children/ContestComment.vue')
const ContestPrint = () => import('@/views/oj/contest/children/ContestPrint.vue')
const ContestAdminPrint = () => import('@/views/oj/contest/children/ContestAdminPrint.vue')
const ContestRejudgeAdmin = () => import('@/views/oj/contest/children/ContestRejudgeAdmin.vue')
const ExamProblemList = () => import('@/views/oj/exam/children/ExamProblemList.vue')
const ExamQuestionList = () => import('@/views/oj/exam/children/ExamQuestionList.vue')
const ExamPaperList = () => import('@/views/oj/exam/children/ExamPaperList.vue')
const ExamPaperDetails = () => import('@/views/oj/exam/children/ExamPaperDetails.vue')
const ExamRejudgeAdmin = () => import('@/views/oj/exam/children/ExamRejudgeAdmin.vue')
const DiscussionList = () => import('@/views/oj/discussion/DiscussionList.vue')
const DiscussionDetails = () => import('@/views/oj/discussion/DiscussionDetails.vue')
const Introduction = () => import('@/views/oj/about/Introduction.vue')
const Developer = () => import('@/views/oj/about/Developer.vue')
const Message = () => import('@/views/oj/message/Message.vue')
const UserMsg = () => import('@/views/oj/message/UserMsg.vue')
const SysMsg = () => import('@/views/oj/message/SysMsg.vue')
const TrainingList = () => import('@/views/oj/training/TrainingList.vue')
const TrainingDetails = () => import('@/views/oj/training/TrainingDetails.vue')
const TrainingProblemList = () => import('@/views/oj/training/TrainingProblemList.vue')
const TrainingRank = () => import('@/views/oj/training/TrainingRank.vue')
const GroupList = () => import('@/views/oj/group/GroupList.vue')
const GroupDetails = () => import('@/views/oj/group/GroupDetails.vue')
const GroupAnnouncementList = () => import('@/views/oj/group/children/GroupAnnouncementList.vue')
const GroupProblemList = () => import('@/views/oj/group/children/GroupProblemList.vue')
const GroupQuestionList = () => import('@/views/oj/group/children/GroupQuestionList.vue')
const GroupQuestionDetails = () => import('@/views/oj/question/QuestionDetails.vue')
const GroupTrainingList = () => import('@/views/oj/group/children/GroupTrainingList.vue')
const GroupContestList = () => import('@/views/oj/group/children/GroupContestList.vue')
const GroupExamList = () => import('@/views/oj/group/children/GroupExamList.vue')
const GroupRank = () => import('@/views/oj/group/children/GroupRank.vue')
const GroupDiscussionList = () => import('@/views/oj/group/children/GroupDiscussionList.vue')
const GroupMemberList = () => import('@/views/oj/group/children/GroupMemberList.vue')
const GroupSetting = () => import('@/views/oj/group/children/GroupSetting.vue')
const NotFound = () => import('@/views/404.vue')
const ojRoutes = [
  {
    path: '/',
    redirect: '/home',
    component: Home,
    meta: { title: 'Home' }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { title: 'Home' }
  },
  {
    path: '/problem',
    name: 'ProblemList',
    component: ProblemList,
    meta: { title: 'Problem' }
  },
  {
    path: '/problem/:problemID',
    name: 'ProblemDetails',
    component: ProblemDetails,
    meta: { title: 'Problem Details' }
  },
  {
    path: '/problem/:problemID/discussion',
    name: 'ProblemDiscussion',
    component: DiscussionList,
    meta: { title: 'Problem Discussion', access: 'discussion' }
  },
  {
    path: '/training',
    name: 'TrainingList',
    component: TrainingList,
    meta: { title: 'Training' }
  },
  {
    path: '/training/:trainingID/',
    name: 'TrainingDetails',
    component: TrainingDetails,
    meta: { title: 'Training Details' },
    children: [
      {
        path: 'problem',
        name: 'TrainingProblemList',
        component: TrainingProblemList,
        meta: { title: 'Training Problem' }
      },
      {
        path: 'problem/:problemID/',
        name: 'TrainingProblemDetails',
        component: ProblemDetails,
        meta: { title: 'Training Problem Details' }
      },
      {
        path: 'rank',
        name: 'TrainingRank',
        component: TrainingRank,
        meta: { title: 'Training Rank' }
      }
    ]
  },
  {
    path: '/contest',
    name: 'ContestList',
    component: ContestList,
    meta: { title: 'Contest' }
  },
  {
    path: '/contest/acm-scoreboard/:contestID',
    name: 'ACMScoreBoard',
    component: ACMScoreBoard,
    meta: { title: 'ACM Contest ScoreBoard' }
  },
  {
    path: '/contest/oi-scoreboard/:contestID',
    name: 'OIScoreBoard',
    component: OIScoreBoard,
    meta: { title: 'OI Contest ScoreBoard' }
  },
  {
    path: '/contest/:contestID/',
    name: 'ContestDetails',
    component: ContestDetails,
    meta: { title: 'Contest Details', requireAuth: true },
    children: [
      {
        path: 'submission',
        name: 'ContestSubmissionList',
        component: SubmissionList,
        meta: { title: 'Contest Submission', access: 'contestJudge' }
      },
      {
        path: 'submission/:submitID',
        name: 'ContestSubmissionDetails',
        component: SubmissionDetails,
        meta: { title: 'Contest Submission Details', access: 'contestJudge' }
      },
      {
        path: 'problem',
        name: 'ContestProblemList',
        component: ContestProblemList,
        meta: { title: 'Contest Problem' }
      },
      {
        path: 'problem/:problemID',
        name: 'ContestProblemDetails',
        component: ProblemDetails,
        meta: { title: 'Contest Problem Details' }
      },
      {
        path: 'announcement',
        name: 'ContestAnnouncementList',
        component: Announcements,
        meta: { title: 'Contest Announcement' }
      },
      {
        path: 'rank',
        name: 'ContestRank',
        component: ContestRank,
        meta: { title: 'Contest Rank' }
      },
      {
        path: 'ac-info',
        name: 'ContestACInfo',
        component: ACMInfoAdmin,
        meta: { title: 'Contest AC Info' }
      },
      {
        path: 'rejudge',
        name: 'ContestRejudgeAdmin',
        component: ContestRejudgeAdmin,
        meta: { title: 'Contest Rejudge', requireSuperAdmin: true }
      },
      {
        path: 'comment',
        name: 'ContestComment',
        component: ContestComment,
        meta: { title: 'Contest Comment', access: 'contestComment' }
      },
      {
        path: 'print',
        name: 'ContestPrint',
        component: ContestPrint,
        meta: { title: 'Contest Print' }
      },
      {
        path: 'admin-print',
        name: 'ContestAdminPrint',
        component: ContestAdminPrint,
        meta: { title: 'Contest Admin Print' }
      }
    ]
  },
  {
    path: '/exam/:examID/',
    name: 'ExamDetails',
    component: ExamDetails,
    meta: { title: 'Exam Details', requireAuth: true },
    children: [
      {
        path: 'submission',
        name: 'ExamSubmissionList',
        component: SubmissionList,
        meta: { title: 'Exam Submission', access: 'contestJudge' }
      },
      {
        path: 'submission/:submitID',
        name: 'ExamSubmissionDetails',
        component: SubmissionDetails,
        meta: { title: 'Exam Submission Details', access: 'contestJudge' }
      },
      {
        path: 'question',
        name: 'ExamQuestionList',
        component: ExamQuestionList,
        meta: { title: 'Exam Qutstion' }
      },
      {
        path: 'question/:questionId',
        name: 'ExamQuestionDetails',
        component: QuestionDetails,
        meta: { title: 'Exam Qutstion Details' }
      },
      {
        path: 'problem',
        name: 'ExamProblemList',
        component: ExamProblemList,
        meta: { title: 'Exam Problem' }
      },
      {
        path: 'problem/:problemID',
        name: 'ExamProblemDetails',
        component: ProblemDetails,
        meta: { title: 'Exam Problem Details' }
      },
      {
        path: 'paper',
        name: 'ExamPaperList',
        component: ExamPaperList,
        meta: { title: 'Paper Problem' }
      },
      {
        path: 'paper/:paperID',
        name: 'ExamPaperDetails',
        component: ExamPaperDetails,
        meta: { title: 'Exam Paper Details' }
      },
      {
        path: 'my-paper/:paperID',
        name: 'MyExamPaperDetails',
        component: ExamPaperDetails,
        meta: { title: 'My Exam Paper Details' }
      },
      {
        path: 'rejudge',
        name: 'ExamRejudgeAdmin',
        component: ExamRejudgeAdmin,
        meta: { title: 'Exam Rejudge Admin' }
      },
    ]
  },
  {
    path: '/status',
    name: 'SubmissionList',
    component: SubmissionList,
    meta: { title: 'Status', access: 'judge' }
  },
  {
    path: '/status/:submitID',
    name: 'SubmissionDetails',
    component: SubmissionDetails,
    meta: { title: 'Submission Details', access: 'judge' }
  },
  {
    path: '/acm-rank',
    name: 'ACM Rank',
    component: ACMRank,
    meta: { title: 'ACM Rank' }
  },
  {
    path: '/oi-rank',
    name: 'OI Rank',
    component: OIRank,
    meta: { title: 'OI Rank' }
  },
  {
    path: '/reset-password',
    name: 'SetNewPassword',
    component: SetNewPassword,
    meta: { title: 'Reset Password' }
  },
  {
    name: 'UserHome',
    path: '/user-home',
    component: UserHome,
    meta: { title: 'User Home' }
  },
  {
    name: 'Setting',
    path: '/setting',
    component: Setting,
    meta: { title: 'Setting', requireAuth: true }
  },
  {
    name: 'Logout',
    path: '/logout',
    component: Logout,
    meta: { title: 'Logout', requireAuth: true }
  },
  {
    path: '/discussion',
    name: 'DiscussionList',
    component: DiscussionList,
    meta: { title: 'Discussion', access: 'discussion' },
  },
  {
    path: '/discussion/:discussionID',
    name: 'DiscussionDetails',
    component: DiscussionDetails,
    meta: { title: 'Discussion Details', access: 'discussion' },
  },
  {
    path: '/group',
    name: 'GroupList',
    component: GroupList,
    meta: { title: 'Group' }
  },
  {
    path: '/group/:groupID',
    name: 'GroupDetails',
    component: GroupDetails,
    meta: { title: 'Group Details' },
    children: [
      {
        path: 'announcement',
        name: 'GroupAnnouncementList',
        component: GroupAnnouncementList,
        meta: { title: 'Group Announcement' },
      },
      {
        path: 'problem',
        name: 'GroupProblemList',
        component: GroupProblemList,
        meta: { title: 'Group Problem' },
      },
      {
        path: 'question',
        name: 'GroupQuestionList',
        component: GroupQuestionList,
        meta: { title: 'Group Question' },
      },
      {
        path: 'question/:questionId',
        name: 'GroupQuestionDetails',
        component: GroupQuestionDetails,
        meta: { title: 'Group Question Details' },
      },
      {
        path: 'training',
        name: 'GroupTrainingList',
        component: GroupTrainingList,
        meta: { title: 'Group Training' }
      },
      {
        path: 'contest',
        name: 'GroupContestList',
        component: GroupContestList,
        meta: { title: 'Group Contest' }
      },
      {
        path: 'exam',
        name: 'GroupExamList',
        component: GroupExamList,
        meta: { title: 'Group Exam' }
      },
      {
        path: 'rank',
        name: 'GroupRank',
        component: GroupRank,
        meta: { title: 'Group Rank' }
      },
      {
        path: 'discussion',
        name: 'GroupDiscussionList',
        component: GroupDiscussionList,
        meta: { title: 'Group Discussion', access: 'groupDiscussion' }
      },
      {
        path: 'member',
        name: 'GroupMemberList',
        component: GroupMemberList,
        meta: { title: 'Group Member' }
      },
      {
        path: 'setting',
        name: 'GroupSetting',
        component: GroupSetting,
        meta: { title: 'Group Setting' }
      },
    ]
  },
  {
    path: '/introduction',
    name: 'Introduction',
    component: Introduction,
    meta: { title: 'Introduction' }
  },
  {
    path: '/developer',
    name: 'Develpoer',
    component: Developer,
    meta: { title: 'Developer' }
  },
  {
    path: '/message/',
    name: 'Message',
    component: Message,
    meta: { title: 'Message', requireAuth: true },
    children: [
      {
        path: 'discuss',
        name: 'DiscussMsg',
        component: UserMsg,
        meta: { title: 'Discuss Message', requireAuth: true }
      },
      {
        path: 'reply',
        name: 'ReplyMsg',
        component: UserMsg,
        meta: { title: 'Reply Message', requireAuth: true }
      },
      {
        path: 'like',
        name: 'LikeMsg',
        component: UserMsg,
        meta: { title: 'Like Message', requireAuth: true }
      },
      {
        path: 'sys',
        name: 'SysMsg',
        component: SysMsg,
        meta: { title: 'System Message', requireAuth: true }
      },
      {
        path: 'mine',
        name: 'MineMsg',
        component: SysMsg,
        meta: { title: 'Mine Message', requireAuth: true }
      },
    ]
  },
  {
    path: '*',
    name: 'Not Found',
    component: NotFound,
    meta: { title: '404' }
  }
]
export default ojRoutes
