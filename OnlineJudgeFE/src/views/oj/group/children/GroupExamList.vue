<template>
  <el-card>
    <div class="filter-row">
      <el-row>
        <el-col :span="3">
          <span class="title">{{ $t('m.Group_Exam') }}</span>
        </el-col>
        <el-col :span="18" v-if="(isSuperAdmin || isGroupAdmin) && !problemPage && !editProblemPage && !announcementPage">
          <el-button
            v-if="!editPage"
            :type="createPage ? 'danger' : 'primary'"
            size="small"
            @click="handleCreatePage"
            :icon="createPage ? 'el-icon-back' : 'el-icon-plus'"
          >{{ createPage ? $t('m.Back') : $t('m.Create') }}</el-button>
          <el-button
            v-if="editPage && adminPage"
            type="danger"
            size="small"
            @click="handleEditPage"
            icon="el-icon-back"
          >{{ $t('m.Back') }}</el-button>`
          <el-button
            :type="adminPage ? 'warning' : 'success'"
            size="small"
            @click="handleAdminPage"
            :icon="adminPage ? 'el-icon-back' : 'el-icon-s-opportunity'"
          >{{ adminPage ? $t('m.Back') : $t('m.Exam_Admin') }}</el-button>
        </el-col>
        <el-col :span="18" v-else-if="(isSuperAdmin || isGroupAdmin) && problemPage && !editProblemPage && !createProblemPage && !announcementPage" >
          <el-button
            type="primary"
            size="small"
            @click="handleCreateProblemPage"
            icon="el-icon-plus"
          >{{ $t('m.Create') }}</el-button>
          <el-button
            type="primary"
            size="small"
            @click="publicPage = true"
            icon="el-icon-plus"
          >{{ $t('m.Add_From_Public_Problem') }}</el-button>
          <el-button
            type="success"
            size="small"
            @click="handleGroupPage"
            icon="el-icon-plus"
          >{{ $t('m.Add_From_Group_Problem') }}</el-button>
          <el-button
            type="warning"
            size="small"
            @click="handleProblemPage(null)"
            icon="el-icon-back"
          >{{ $t('m.Back') }}</el-button>
        </el-col>
        <el-col :span="18" v-else-if="(isSuperAdmin || isGroupAdmin) && (editProblemPage || createProblemPage)">
          <el-button
            v-if="editProblemPage"
            type="danger"
            size="small"
            @click="handleEditProblemPage"
            icon="el-icon-back"
          >{{ $t('m.Back') }}</el-button>`
          <el-button
            v-if="createProblemPage"
            type="danger"
            size="small"
            @click="handleCreateProblemPage"
            icon="el-icon-back"
          >{{ $t('m.Back') }}</el-button>`
        </el-col>
        <el-col :span="18" v-else-if="(isSuperAdmin || isGroupAdmin) && announcementPage">
          <el-button
            type="primary"
            size="small"
            @click="handleCreateAnnouncementPage"
            icon="el-icon-plus"
          >{{ $t('m.Create') }}</el-button>
          <el-button
            type="warning"
            size="small"
            @click="handleAnnouncementPage"
            icon="el-icon-back"
          >{{ $t('m.Back') }}</el-button>
        </el-col>
      </el-row>
    </div>
    <div v-if="!adminPage && !createPage && !problemPage">
      <p id="no-exam" v-show="examList.length == 0">
        <el-empty :description="$t('m.No_Exam')"></el-empty>
      </p>
      <ol id="exam-list">
        <li
          v-for="exam in examList"
          :key="exam.title"
          :style="getborderColor(exam)"
        >
          <el-row type="flex" justify="space-between" align="middle">
            <el-col :xs="10" :sm="4" :md="3" :lg="2">
              <img
                v-show="exam.type == 0"
                class="trophy"
                :src="acmSrc"
                width="95px"
              />
              <img
                v-show="exam.type == 1"
                class="trophy"
                :src="oiSrc"
                width="95px"
              />
            </el-col>
            <el-col
              :xs="10"
              :sm="16"
              :md="19"
              :lg="20"
              class="exam-main"
            >
              <p class="exam-title">
                <a class="entry" @click.stop="goGroupExam(exam.id)">
                  {{ exam.title }}
                </a>
                <template v-if="exam.auth == 1">
                  <i
                    class="el-icon-lock"
                    size="20"
                    style="color:#d9534f"
                  ></i>
                </template>
                <template v-if="exam.auth == 2">
                  <i
                    class="el-icon-lock"
                    size="20"
                    style="color:#f0ad4e"
                  ></i>
                </template>
              </p>
              <ul class="detail">
                <li>
                  <i
                    class="fa fa-calendar"
                    aria-hidden="true"
                    style="color: #3091f2"
                  ></i>
                  {{ exam.startTime | localtime }}
                </li>
                <li>
                  <i
                    class="fa fa-clock-o"
                    aria-hidden="true"
                    style="color: #3091f2"
                  ></i>
                  {{ getDuration(exam.startTime, exam.endTime) }}
                </li>
                <li>
                  <el-tooltip
                    :content="
                      $t('m.' + EXAM_TYPE_REVERSE[exam.auth].tips)
                    "
                    placement="top"
                    effect="light"
                  >
                    <el-tag
                      :type="EXAM_TYPE_REVERSE[exam.auth]['color']"
                      effect="plain"
                    >
                      {{
                        $t(
                          'm.' + EXAM_TYPE_REVERSE[exam.auth]['name']
                        )
                      }}
                    </el-tag>
                  </el-tooltip>
                </li>
                <li v-if="exam.auth != EXAM_TYPE.PUBLIC">
                  <i
                    class="el-icon-user-solid"
                    style="color:rgb(48, 145, 242);"
                  ></i
                  >x{{ exam.count != null ? exam.count : 0 }}
                </li>
                <li v-if="exam.openRank">
                  <el-tooltip
                    :content="$t('m.Exam_Outside_ScoreBoard')"
                    placement="top"
                    effect="dark"
                  >
                    <el-button
                      circle
                      size="small"
                      type="primary"
                      :disabled="exam.status == EXAM_STATUS.SCHEDULED"
                      icon="el-icon-data-analysis"
                      @click="
                        goExamOutsideScoreBoard(exam.id, exam.type)
                      "
                    ></el-button>
                  </el-tooltip>
                </li>
              </ul>
            </el-col>
            <el-col
              :xs="4"
              :sm="4"
              :md="2"
              :lg="2"
              style="text-align: center"
            >
              <el-tag
                effect="dark"
                :color="EXAM_STATUS_REVERSE[exam.status]['color']"
                size="medium"
              >
                <i class="fa fa-circle" aria-hidden="true"></i>
                {{
                  $t('m.' + EXAM_STATUS_REVERSE[exam.status]['name'])
                }}
              </el-tag>
            </el-col>
          </el-row>
        </li>
      </ol>
      <Pagination
        :total="total"
        :page-size="limit"
        @on-change="currentChange"
        :current.sync="currentPage"
        @on-page-size-change="onPageSizeChange"
        :layout="'prev, pager, next, sizes'"
      ></Pagination>
    </div>
    <ExamList
      v-if="adminPage && !createPage && !problemPage && !announcementPage"
      @handleEditPage="handleEditPage"
      @currentChange="currentChange"
      @handleProblemPage="handleProblemPage"
      @handleAnnouncementPage="handleAnnouncementPage"
      ref="examList"
    ></ExamList>
    <ProblemList
      v-if="problemPage && !createProblemPage"
      :examID="examID"
      @currentChangeProblem="currentChangeProblem"
      @handleEditProblemPage="handleEditProblemPage"
      ref="examProblemList"
    >
    </ProblemList>
    <Exam
      v-if="createPage && !editPage && !problemPage"
      mode="add"
      :title="$t('m.Create_Exam')"
      apiMethod="addGroupExam"
      @handleCreatePage="handleCreatePage"
      @currentChange="currentChange"
    ></Exam>
    <Problem
      v-if="createProblemPage"
      mode="add"
      :examID="examID"
      :title="$t('m.Create_Problem')"
      apiMethod="addGroupExamProblem"
      @handleCreateProblemPage="handleCreateProblemPage"
      @currentChange="currentChange"
    ></Problem>
    <el-dialog
      :title="$t('m.Add_Exam_Problem')"
      width="90%"
      :visible.sync="publicPage"
      :close-on-click-modal="false"
    >
      <AddPublicProblem
        v-if="publicPage"
        :examID="examID"
        apiMethod="getGroupExamProblemList"
        @currentChangeProblem="currentChangeProblem"
        ref="addPublicProblem"
      ></AddPublicProblem>
    </el-dialog>
    <el-dialog
      :title="$t('m.Add_Exam_Problem')"
      width="350px"
      :visible.sync="groupPage"
      :close-on-click-modal="false"
    >
      <AddGroupProblem
        :examID="examID"
        @currentChangeProblem="currentChangeProblem"
        @handleGroupPage="handleGroupPage"
      ></AddGroupProblem>
    </el-dialog>
  </el-card>
</template>

<script>
import { mapGetters } from 'vuex';
import Pagination from '@/components/oj/common/Pagination';
import ExamList from '@/components/oj/group/ExamList'
import Exam from '@/components/oj/group/Exam'
import Problem from '@/components/oj/group/Problem'
import ProblemList from '@/components/oj/group/ProblemList'
import AddPublicProblem from '@/components/oj/group/AddPublicProblem.vue';
import AddGroupProblem from '@/components/oj/group/AddGroupProblem.vue';
import AnnouncementList from '@/components/oj/group/AnnouncementList'
import api from '@/common/api';
import time from '@/common/time';
import {
  EXAM_STATUS_REVERSE,
  EXAM_TYPE,
  EXAM_TYPE_REVERSE,
  EXAM_STATUS,
} from '@/common/constants';
export default {
  name: 'GroupExamList',
  components: {
    Pagination,
    ExamList,
    Exam,
    Problem,
    ProblemList,
    AddPublicProblem,
    AddGroupProblem,
    AnnouncementList
  },
  data() {
    return {
      total: 0,
      currentPage: 1,
      limit: 10,
      examList: [],
      loading: false,
      adminPage: false,
      createPage: false,
      editPage: false,
      problemPage: false,
      publicPage: false,
      groupPage: false,
      editProblemPage: false,
      createProblemPage: false,
      announcementPage: false,
      examID: null,
      acmSrc: require('@/assets/acm.jpg'),
      oiSrc: require('@/assets/oi.jpg'),
    };
  },
  mounted() {
    this.EXAM_STATUS_REVERSE = Object.assign({}, EXAM_STATUS_REVERSE);
    this.EXAM_TYPE = Object.assign({}, EXAM_TYPE);
    this.EXAM_TYPE_REVERSE = Object.assign({}, EXAM_TYPE_REVERSE);
    this.EXAM_STATUS = Object.assign({}, EXAM_STATUS);
    this.init();
  },
  methods: {
    init() {
      this.getGroupExamList();
    },
    onPageSizeChange(pageSize) {
      this.limit = pageSize;
      this.init();
    },
    currentChange(page) {
      this.currentPage = page;
      this.init();
    },
    currentChangeProblem() {
      this.$refs.examProblemList.currentChange(1);
    },
    getGroupExamList() {
      this.loading = true;
      api.getGroupExamList(this.currentPage, this.limit, this.$route.params.groupID).then(
        (res) => {
          this.examList = res.data.data.records;
          this.total = res.data.data.total;
          this.loading = false;
        },
        (err) => {
          this.loading = false;
        }
      );
    },
    goGroupExam(examID) {
      this.$router.push({
        name: 'ExamDetails',
        params: {
          examID: examID,
        },
      });
    },
    goExamOutsideScoreBoard(cid, type) {
      if (type == 0) {
        this.$router.push({
          name: 'ACMScoreBoard',
          params: { examID: cid },
        });
      } else if (type == 1) {
        this.$router.push({
          name: 'OIScoreBoard',
          params: { examID: cid },
        });
      }
    },
    getDuration(startTime, endTime) {
      return time.formatSpecificDuration(startTime, endTime);
    },
    getborderColor(exam) {
      return (
        'border-left: 4px solid ' +
        EXAM_STATUS_REVERSE[exam.status]['color']
      );
    },
    handleCreatePage() {
      this.createPage = !this.createPage;
    },
    handleEditPage() {
      this.editPage = !this.editPage;
      this.$refs.examList.editPage = this.editPage;
    },
    handleAdminPage() {
      this.adminPage = !this.adminPage;
      this.createPage = false;
      this.editPage = false;
    },
    handleProblemPage(examID) {
      this.examID = examID;
      this.problemPage = !this.problemPage;
    },
    handleAnnouncementPage(examID) {
      this.examID = examID;
      this.announcementPage = !this.announcementPage;
    },
    handleGroupPage() {
      this.groupPage = !this.groupPage;
    },
    handleEditProblemPage() {
      this.editProblemPage = !this.editProblemPage;
      this.$refs.examProblemList.editPage = this.editProblemPage;
    },
    handleCreateProblemPage() {
      this.createProblemPage = !this.createProblemPage;
      this.$refs.examProblemList.currentChange(1);
    },
    handleCreateAnnouncementPage() {
      this.$refs.examAnnouncementList.openAnnouncementDialog(null);
    },
    
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'isSuperAdmin', 'isGroupAdmin']),
  },
};
</script>

<style scoped>
.title {
  font-size: 20px;
  vertical-align: middle;
  float: left;
}
.filter-row {
  margin-bottom: 5px;
  text-align: center;
}
@media screen and (max-width: 768px) {
  .filter-row span {
    margin-left: 5px;
    margin-right: 5px;
  }
}
@media screen and (min-width: 768px) {
  .filter-row span {
    margin-left: 10px;
    margin-right: 10px;
  }
}
#no-exam {
  text-align: center;
  font-size: 16px;
  padding: 20px;
}
#exam-list > li {
  padding: 5px;
  margin-left: -20px;
  margin-top: 10px;
  width: 100%;
  border-bottom: 1px solid rgba(187, 187, 187, 0.5);
  list-style: none;
  text-align: center;
}
#exam-list .trophy {
  height: 70px;
  margin-left: 10px;
  margin-right: -20px;
}
#exam-list .exam-main .exam-title {
  font-size: 1.25rem;
  padding-left: 8px;
  margin-bottom: 0;
}
#exam-list .exam-main .exam-title a.entry {
  color: #495060;
}
#exam-list .exam-main .exam-title a:hover {
  color: #2d8cf0;
  border-bottom: 1px solid #2d8cf0;
}
#exam-list .exam-main .detail {
  padding-left: 0;
  padding-bottom: 10px;
}
#exam-list .exam-main li {
  display: inline-block;
  padding: 10px 0 0 10px;
}
</style>
