<template>
  <el-row>
    <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
      <el-card>
        <div slot="header">
          <span class="panel-title">{{ $t("m.Exam_Paper") }}</span>
          <span style="float: right; font-size: 20px">
            <el-popover trigger="hover" placement="left-start">
              <i class="el-icon-s-tools" slot="reference"></i>
              <el-button type="primary" size="small" @click="exportDataEvent">{{
                $t("m.Download_as_CSV")
              }}</el-button>
            </el-popover>
          </span>
        </div>
        <vxe-table
          ref="paper"
          round
          border
          auto-resize
          size="medium"
          highlight-hover-row
          :data="paperList"
          :loading="loading"
          align="center"
        >
          <vxe-table-column type="seq" width="50"></vxe-table-column>
          <vxe-table-column
            field="username"
            :title="$t('m.Username')"
            min-width="100"
            show-overflow
          >
            <template v-slot="{ row }">
              <span @click="goUserHome(row.username)">
                {{ row.username }}
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="realName"
            :title="$t('m.RealName')"
            width="96"
            show-overflow
          >
          </vxe-table-column>
          <vxe-table-column
            field="score"
            :title="$t('m.Total_Score')"
            width="120"
          >
            <template v-slot="{ row }">
              <span style="font-size: 14px"
                ><a
                  @click="goExamPaper(row.uid)"
                  style="color: rgb(87, 163, 243)"
                  >{{ row.score }}</a
                >
                <br />
                <span class="problem-time">{{ row.time }}ms</span>
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column
            width="64"
            v-for="question in questionList"
            :key="question.displayId"
            :field="question.displayId"
          >
            <template slot="header">
              <el-tooltip effect="dark" placement="top">
                <div slot="content">
                  {{
                    question.displayId +
                    ". " +
                    $t(
                      "m." +
                        QUESTION_TYPE_REVERSE[question.type].name +
                        "_Question"
                    )
                  }}
                  <br />
                  {{ "Accepted: " + question.ac }}
                  <br />
                  {{ "Rejected: " + question.error }}
                  <br />
                  {{
                    "Average: " +
                    (question.average != null ? question.average : 0)
                  }}
                </div>
                <div>
                  <span>
                    <a
                      @click="goExamQuestion(question.displayId)"
                      class="emphasis"
                      style="color: #495060"
                      >{{ question.displayId }}
                    </a>
                    <br />
                    <span>{{ question.ac }}</span>
                  </span>
                </div>
              </el-tooltip>
            </template>
            <template v-slot="{ row }">
              <div
                v-if="row.examQuestionRecordList[question.displayId]"
                class="submission-hover"
              >
                <span style="font-size: 14px">
                  {{
                    row.examQuestionRecordList[question.displayId].score != null
                      ? row.examQuestionRecordList[question.displayId].score
                      : 0
                  }}</span
                >
              </div>
            </template>
          </vxe-table-column>
          <template v-if="questionList.length">
            <vxe-table-column
              width="64"
              v-for="problem in problemList"
              :key="problem.displayId"
              :field="problem.displayId"
            >
              <template slot="header">
                <el-tooltip effect="dark" placement="top">
                  <div slot="content">
                    {{ problem.displayId + ". " + problem.displayTitle }}
                    <br />
                    {{ "Accepted: " + problem.ac }}
                    <br />
                    {{ "Rejected: " + problem.error }}
                  </div>
                  <div>
                    <span>
                      <a
                        @click="goExamProblem(problem.displayId)"
                        class="emphasis"
                        style="color: #495060"
                        >{{ problem.displayId }}
                      </a>
                      <br />
                      <span>{{ problem.ac }}</span>
                    </span>
                  </div>
                </el-tooltip>
              </template>
              <template v-slot="{ row }">
                <div
                  v-if="row.examRecordList[problem.displayId]"
                  class="submission-hover"
                >
                  <span style="font-size: 14px">{{
                    row.examRecordList[problem.displayId].score
                  }}</span>
                  <br />
                  <span
                    v-if="row.examRecordList[problem.displayId].time != null"
                    >{{ row.examRecordList[problem.displayId].time }}ms</span
                  >
                </div>
              </template>
            </vxe-table-column>
          </template>
        </vxe-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import { QUESTION_TYPE_REVERSE } from "@/common/constants";
import api from "@/common/api";
export default {
  name: "GroupPaperList",
  data() {
    return {
      paperList: [],
      type: 0,
      examID: null,
      loading: true,
      questionList: [],
    };
  },
  mounted() {
    this.examID = this.$route.params.examID;
    this.init();
  },
  created() {
    this.QUESTION_TYPE_REVERSE = Object.assign({}, QUESTION_TYPE_REVERSE);
  },
  methods: {
    init() {
      this.getExamPaperList();
      this.getExamQuestionList();
      this.getExamProblemList();
    },
    getExamPaperList() {
      this.loading = true;
      api.getExamPaperList(this.$route.params.examID, null).then(
        (res) => {
          this.paperList = res.data.data;
          this.loading = false;
        },
        (err) => {
          this.loading = false;
        }
      );
    },
    getExamQuestionList() {
      this.loading = true;
      api.getExamQuestionList(0, this.$route.params.examID).then(
        (res) => {
          this.questionList = res.data.data;
          this.loading = false;
        },
        (err) => {
          this.loading = false;
        }
      );
    },
    getExamProblemList() {
      this.$store.dispatch("getExamProblemList");
    },
    goExamPaper(uid) {
      this.$router.push({
        name: "ExamPaperDetails",
        params: {
          paperID: uid,
          examID: this.examID,
        },
      });
    },
    goExamProblem(displayId) {
      this.$router.push({
        name: "ExamProblemDetails",
        params: {
          problemID: displayId,
          examID: this.examID,
        },
      });
    },
    goExamQuestion(displayId) {
      this.$router.push({
        name: "ExamQuestionDetails",
        params: {
          questionId: displayId,
          examID: this.examID,
        },
      });
    },
    goUserHome(username) {
      this.$router.push({
        name: "UserHome",
        query: { username: username },
      });
    },
    exportDataEvent() {
      this.$refs.paper.exportData({ type: "csv" });
    },
  },
  computed: {
    ...mapState({
      problemList: (state) => state.exam.examProblemList,
    }),
    ...mapGetters(["isAuthenticated", "isSuperAdmin", "isGroupAdmin"]),
  },
  watch: {
    $route() {
      this.init();
    },
    isAuthenticated(newVal) {
      if (newVal === true) {
        this.init();
      }
    },
  },
};
</script>

<style scoped>
@media screen and (min-width: 1050px) {
  /deep/ .vxe-table--body-wrapper {
    overflow-x: hidden !important;
  }
}
.echarts {
  margin: 20px auto;
  height: 400px;
  width: 100%;
}
/deep/.el-card__body {
  padding: 20px !important;
  padding-top: 0 !important;
}
@media screen and (max-width: 768px) {
  /deep/.el-card__body {
    padding: 0 !important;
  }
}

.vxe-cell p,
.vxe-cell span {
  margin: 0;
  padding: 0;
}
/deep/.vxe-table .vxe-header--column:not(.col--ellipsis) {
  padding: 4px 0 !important;
}
/deep/.vxe-table .vxe-body--column {
  line-height: 20px !important;
  padding: 0px !important;
}
/deep/.vxe-body--column {
  min-width: 0;
  height: 48px;
  box-sizing: border-box;
  text-align: left;
  text-overflow: ellipsis;
  vertical-align: middle;
}
a.emphasis {
  color: #495060 !important;
}
a.emphasis:hover {
  color: #2d8cf0 !important;
}
.problem-time {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}

/deep/.vxe-table .vxe-cell {
  padding-left: 5px !important;
  padding-right: 5px !important;
}
</style>
