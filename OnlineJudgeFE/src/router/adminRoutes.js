// 引入 view 组件
import Login from '@/views/admin/Login'
import Home from '@/views/admin/Home'
import Dashboard from '@/views/admin/Dashboard'
import User from '@/views/admin/general/User'
import Auth from '@/views/admin/general/Auth'
import Announcement from '@/views/admin/general/Announcement'
import SysNotice from '@/views/admin/general/SysNotice'
import SystemConfig from '@/views/admin/general/SystemConfig'
import ProblemList from '@/views/admin/problem/ProblemList'
import Problem from '@/views/admin/problem/Problem'
import Tag from '@/views/admin/problem/Tag'
import ProblemImportAndExport from '@/views/admin/problem/ImportAndExport'
import Contest from '@/views/admin/contest/Contest'
import ContestList from '@/views/admin/contest/ContestList'
import Training from '@/views/admin/training/Training'
import TrainingList from '@/views/admin/training/TrainingList'
import TrainingProblemList from '@/views/admin/training/TrainingProblemList'
import TrainingCategory from '@/views/admin/training/Category'
import DiscussionList from '@/views/admin/discussion/Discussion'

const adminRoutes= [
  {
    path: '/admin/login',
    name: 'admin-login',
    component: Login,
    meta: { title: 'Login' }
  },
  {
    path: '/admin/',
    component: Home,
    meta: { requireAuth: true, requireAdmin: true },
    children: [
      {
        path: '',
        redirect: 'dashboard',
        component: Dashboard,
        meta: { title: 'Dashboard' }
      },
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: Dashboard,
        meta: { title: 'Dashboard' }
      },
      {
        path: 'user',
        name: 'admin-user',
        component: User,
        meta: { title: 'User Admin', requireSuperAdmin: true },
      },
      {
        path: 'auth',
        name: 'admin-auth',
        component: Auth,
        meta: { title: 'Auth Admin', requireSuperAdmin: true },
      },
      {
        path: 'announcement',
        name: 'admin-announcement',
        component: Announcement,
        meta: { title: 'Announcement Admin', requireSuperAdmin: true },
      },
      {
        path: 'notice',
        name: 'admin-notice',
        component: SysNotice,
        meta: { title: 'Notice Admin', requireSuperAdmin: true },
      },
      {
        path: 'conf',
        name: 'admin-conf',
        component: SystemConfig,
        meta: { title: 'System Config', requireSuperAdmin: true },
      },
      {
        path: 'problems',
        name: 'admin-problem-list',
        component: ProblemList,
        meta: { title: 'Problem List' },
      },
      {
        path: 'problem/create',
        name: 'admin-create-problem',
        component: Problem,
        meta: { title: 'Create Problem' },
      },
      {
        path: 'problem/edit/:problemId',
        name: 'admin-edit-problem',
        component: Problem,
        meta: { title: 'Edit Problem' },
      },
      {
        path: 'problem/tag',
        name: 'admin-problem-tag',
        component: Tag,
        meta: { title: 'Admin Tag' },
      },
      {
        path: 'problem/batch-operation',
        name: 'admin-problem_batch_operation',
        component: ProblemImportAndExport,
        meta: { title: 'Export Import Problem' },
      },
      {
        path: 'training/create',
        name: 'admin-create-training',
        component: Training,
        meta: { title: 'Create Training' },
      },
      {
        path: 'training',
        name: 'admin-training-list',
        component: TrainingList,
        meta: { title: 'Training List' }
      },
      {
        path: 'training/:trainingID/edit',
        name: 'admin-edit-training',
        component: Training,
        meta: { title: 'Edit Training' }
      },
      {
        path: 'training/:trainingID/problems',
        name: 'admin-training-problem-list',
        component: TrainingProblemList,
        meta: { title: 'Training Problem List' }
      },
      {
        path: 'training/category',
        name: 'admin-training-category',
        component: TrainingCategory,
        meta: { title: 'Admin Category' }
      },
      {
        path: 'contest/create',
        name: 'admin-create-contest',
        component: Contest,
        meta: { title: 'Create Contest' },
      },
      {
        path: 'contest',
        name: 'admin-contest-list',
        component: ContestList,
        meta: { title: 'Contest List' }
      },
      {
        path: 'contest/:contestID/edit',
        name: 'admin-edit-contest',
        component: Contest,
        meta: { title: 'Edit Contest' }
      },
      {
        path: 'contest/:contestID/announcement',
        name: 'admin-contest-announcement',
        component: Announcement,
        meta: { title: 'Contest Announcement' }
      },
      {
        path: 'contest/:contestID/problems',
        name: 'admin-contest-problem-list',
        component: ProblemList,
        meta: { title: 'Contest Problem List' }
      },
      {
        path: 'contest/:contestID/problem/create',
        name: 'admin-create-contest-problem',
        component: Problem,
        meta: { title: 'Create Problem' }
      },
      {
        path: 'contest/:contestID/problem/:problemId/edit',
        name: 'admin-edit-contest-problem',
        component: Problem,
        meta: { title: 'Edit Problem' }
      },
      {
        path: 'discussion',
        name: 'admin-discussion-list',
        component: DiscussionList,
        meta: { title: 'Discussion Admin' }
      },
    ]
  },
  {
    path: '/admin/*', redirect: '/admin/login'
  }
]

  export default adminRoutes
