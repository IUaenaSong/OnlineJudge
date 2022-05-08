import Home from '@/views/oj/Home.vue'
import SetNewPassword from "@/views/oj/user/SetNewPassword.vue"
import UserHome from "@/views/oj/user/UserHome.vue"
import Setting from "@/views/oj/user/Setting.vue"
import ProblemList from "@/views/oj/problem/ProblemList.vue"
import Logout from "@/views/oj/user/Logout.vue"
import SubmissionList from "@/views/oj/status/SubmissionList.vue"
import SubmissionDetails from "@/views/oj/status/SubmissionDetails.vue"
import ContestList from "@/views/oj/contest/ContestList.vue"
import ProblemDetails from "@/views/oj/problem/ProblemDetails.vue"
import QuestionDetails from '@/views/oj/question/QuestionDetails.vue'
import ACMRank from "@/views/oj/rank/ACMRank.vue"
import OIRank from "@/views/oj/rank/OIRank.vue"
import ContestDetails from "@/views/oj/contest/ContestDetails.vue"
import ExamDetails from '@/views/oj/exam/ExamDetails.vue'
import ACMScoreBoard from "@/views/oj/contest/outside/ACMScoreBoard.vue"
import OIScoreBoard from "@/views/oj/contest/outside/OIScoreBoard.vue"
import ContestProblemList from "@/views/oj/contest/children/ContestProblemList.vue"
import ContestRank from "@/views/oj/contest/children/ContestRank.vue"
import ACMInfoAdmin from "@/views/oj/contest/children/ACMInfoAdmin.vue"
import Announcements from "@/components/oj/common/Announcements.vue"
import ContestComment from "@/views/oj/contest/children/ContestComment.vue"
import ContestPrint from "@/views/oj/contest/children/ContestPrint.vue"
import ContestAdminPrint from "@/views/oj/contest/children/ContestAdminPrint.vue"
import ContestRejudgeAdmin from "@/views/oj/contest/children/ContestRejudgeAdmin.vue"
import ExamProblemList from "@/views/oj/exam/children/ExamProblemList.vue"
import ExamQuestionList from "@/views/oj/exam/children/ExamQuestionList.vue"
import ExamPaperList from "@/views/oj/exam/children/ExamPaperList.vue"
import ExamPaperDetails from "@/views/oj/exam/children/ExamPaperDetails.vue"
import ExamRejudgeAdmin from '@/views/oj/exam/children/ExamRejudgeAdmin.vue'
import DiscussionList from "@/views/oj/discussion/DiscussionList.vue"
import DiscussionDetails from "@/views/oj/discussion/DiscussionDetails.vue"
import Introduction from "@/views/oj/about/Introduction.vue"
import Developer from "@/views/oj/about/Developer.vue"
import Message from "@/views/oj/message/Message.vue"
import UserMsg from "@/views/oj/message/UserMsg.vue"
import SysMsg from "@/views/oj/message/SysMsg.vue"
import TrainingList from "@/views/oj/training/TrainingList.vue"
import TrainingDetails from "@/views/oj/training/TrainingDetails.vue"
import TrainingProblemList from "@/views/oj/training/TrainingProblemList.vue"
import TrainingRank from "@/views/oj/training/TrainingRank.vue"
import GroupList from '@/views/oj/group/GroupList.vue'
import GroupDetails from '@/views/oj/group/GroupDetails.vue'
import GroupAnnouncementList from '@/views/oj/group/children/GroupAnnouncementList.vue'
import GroupProblemList from '@/views/oj/group/children/GroupProblemList.vue'
import GroupQuestionList from '@/views/oj/group/children/GroupQuestionList.vue'
import GroupQuestionDetails from '@/views/oj/question/QuestionDetails.vue'
import GroupTrainingList from '@/views/oj/group/children/GroupTrainingList.vue'
import GroupContestList from '@/views/oj/group/children/GroupContestList.vue'
import GroupExamList from '@/views/oj/group/children/GroupExamList.vue'
import GroupRank from '@/views/oj/group/children/GroupRank.vue'
import GroupDiscussionList from '@/views/oj/group/children/GroupDiscussionList.vue'
import GroupMemberList from '@/views/oj/group/children/GroupMemberList.vue'
import GroupSetting from '@/views/oj/group/children/GroupSetting.vue'
import NotFound from "@/views/404.vue"

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
    meta: { title: 'Problem Discussion' }
    
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
        meta: { title: 'Contest Submission' }
      },
      {
        path: 'submission/:submitID',
        name: 'ContestSubmissionDetails',
        component: SubmissionDetails,
        meta: { title: 'Contest Submission Details' }
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
        meta: { title: 'Contest AC Info'}
      },
      {
        path: 'rejudge',
        name: 'ContestRejudgeAdmin',
        component: ContestRejudgeAdmin,
        meta: { title: 'Contest Rejudge', requireSuperAdmin:true }
      },
      {
        path: 'comment',
        name: 'ContestComment',
        component: ContestComment,
        meta: { title: 'Contest Comment'}
      },
      {
        path: 'print',
        name: 'ContestPrint',
        component: ContestPrint,
        meta: { title: 'Contest Print'}
      },
      {
        path: 'admin-print',
        name: 'ContestAdminPrint',
        component: ContestAdminPrint,
        meta: { title: 'Contest Admin Print'}
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
        meta: { title: 'Exam Submission' }
      },
      {
        path: 'submission/:submitID',
        name: 'ExamSubmissionDetails',
        component: SubmissionDetails,
        meta: { title: 'Exam Submission Details' }
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
    meta: { title: 'Status' }
  },
  {
    path: '/status/:submitID',
    name: 'SubmissionDetails',
    component: SubmissionDetails,
    meta: { title: 'Submission Details' }
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
    meta: { title: 'Discussion' }
  },
  {
    path: '/discussion/:discussionID',
    name: 'DiscussionDetails',
    component: DiscussionDetails,
    meta: {title: 'Discussion Details'}
  },
  {
    path: '/group',
    name: 'GroupList',
    component: GroupList,
    meta: {title: 'Group'}
  },
  {
    path: '/group/:groupID',
    name: 'GroupDetails',
    component: GroupDetails,
    meta: {title: 'Group Details'},
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
        meta: { title: 'Group Discussion' }
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
    meta: {title: 'Introduction'}
  },
  {
    path: '/developer',
    name: 'Develpoer',
    component: Developer,
    meta: {title: 'Developer'}
  },
  {
    path: '/message/',
    name: 'Message',
    component:Message,
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
        meta: { title: 'System Message', requireAuth: true}
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
