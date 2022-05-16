<template>
  <el-row>
    <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
      <el-card>
        <div slot="header">
          <span class="panel-title">{{ $t("m.Paper") }}</span>
        </div>
        <div style="font-size: 16px">
          {{ $t("m.Username") }}：<span>{{ paper.username }}</span>
          <br />
          {{ $t("m.RealName") }}：<span>{{ paper.realname }}</span>
          <br />
          {{ $t("m.Score") }}：<span style="color: red">{{ paper.score }}</span>
        </div>
        <el-form :model="paper" label-position="left">
          <div v-for="question in questionList" :key="question.displayId">
            <el-divider />
            <div :padding="10" shadow class="question-detail">
              <div slot="header" class="panel-title">
                <span>{{ question.displayId }}</span
                ><br />
                <div class="question-intr">
                  <template>
                    <span
                      >{{ $t("m.Question_Type") }}：
                      <el-tag
                        v-if="QUESTION_TYPE_REVERSE[question.type]"
                        class="filter-item"
                        size="medium"
                        :type="QUESTION_TYPE_REVERSE[question.type].color"
                        effect="dark"
                      >
                        {{
                          $t(
                            "m." +
                              QUESTION_TYPE_REVERSE[question.type].name +
                              "_Question"
                          )
                        }}
                      </el-tag>
                      <el-tag
                        v-if="question.type == 1"
                        class="filter-item"
                        size="medium"
                        type="primary"
                        effect="plain"
                      >
                        {{
                          $t(
                            "m." +
                              (question.single
                                ? "Single_Choice"
                                : "Indefinite_Multiple_Choice") +
                              "_Question"
                          )
                        }}
                      </el-tag> </span
                    ><br />
                  </template>
                  <template v-if="examID">
                    <span>{{ $t("m.Score") }}：{{ question.score }} </span
                    ><br />
                  </template>
                </div>
              </div>
              <div class="question-content">
                <template>
                  <p class="title">{{ $t("m.Question_Description") }}</p>
                  <p
                    class="content markdown-body"
                    v-html="$markDown.render(question.description)"
                    v-katex
                    v-highlight
                  ></p>
                </template>
                <template v-if="question.type == 1 && question.single">
                  <p class="title">{{ $t("m.User_Answer") }}</p>
                  <p
                    v-if="
                      paper.examQuestionRecordList[question.displayId] != null
                    "
                    class="content"
                  >
                    {{
                      String.fromCharCode(
                        parseInt(
                          paper.examQuestionRecordList[question.displayId]
                            .answer
                        ) + 65
                      )
                    }}
                  </p>
                </template>
                <template v-else-if="question.type == 1 && !question.single">
                  <p class="title">{{ $t("m.User_Answer") }}</p>
                  <p
                    v-if="
                      paper.examQuestionRecordList[question.displayId] != null
                    "
                    class="content"
                  >
                    {{
                      stringToAnswer(
                        paper.examQuestionRecordList[question.displayId].answer
                      )
                    }}
                  </p>
                </template>
                <template v-else-if="question.type == 2">
                  <p class="title">{{ $t("m.User_Answer") }}</p>
                  <p
                    v-if="
                      paper.examQuestionRecordList[question.displayId] != null
                    "
                    class="content"
                  >
                    {{
                      paper.examQuestionRecordList[question.displayId].answer
                    }}
                  </p>
                </template>
                <template v-else>
                  <p class="title">{{ $t("m.User_Answer") }}</p>
                  <p
                    v-if="
                      paper.examQuestionRecordList[question.displayId] != null
                    "
                    class="content markdown-body"
                    v-html="
                      $markDown.render(
                        paper.examQuestionRecordList[question.displayId].answer
                      )
                    "
                    v-katex
                    v-highlight
                  ></p>
                </template>
                <el-form-item
                  required
                  v-if="
                    paper.examQuestionRecordList[question.displayId] != null
                  "
                >
                  <span slot="label" class="title">{{ $t("m.Score") }}： </span>
                  <el-input
                    :disabled="!isExamAdmin"
                    style="width: 100px"
                    v-model="
                      paper.examQuestionRecordList[question.displayId].score
                    "
                    @keyup.enter.native="submitScore"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </div>
          </div>
          <div v-for="problem in problemList" :key="problem.displayId">
            <el-divider />
            <div :padding="10" shadow class="question-detail">
              <div slot="header" class="panel-title">
                <span>{{
                  problem.displayId + ". " + problem.displayTitle
                }}</span
                ><br />
                <div class="question-intr">
                  <template>
                    <span
                      >{{ $t("m.Question_Type") }}：
                      <el-tag
                        class="filter-item"
                        size="medium"
                        type="primary"
                        effect="dark"
                      >
                        {{ $t("m.Code_Question") }}
                      </el-tag> </span
                    ><br />
                  </template>
                  <template>
                    <span>{{ $t("m.Score") }}：{{ problem.score }} </span><br />
                  </template>
                </div>
              </div>
              <div class="question-content">
                <template>
                  <p class="title">{{ $t("m.User_Answer") }}</p>
                  <Highlight
                    v-if="paper.examRecordList[problem.displayId] != null"
                    :code="paper.examRecordList[problem.displayId].code"
                  ></Highlight>
                  <br />
                </template>
                <el-form-item
                  required
                  v-if="paper.examRecordList[problem.displayId] != null"
                >
                  <span slot="label" class="title">{{ $t("m.Score") }}： </span>
                  <el-input
                    :disabled="!isExamAdmin"
                    style="width: 100px"
                    v-model="paper.examRecordList[problem.displayId].score"
                    @keyup.enter.native="submitScore"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </div>
          </div>
        </el-form>
        <el-button type="primary" @click="submitScore" v-if="isExamAdmin">
          {{ $t("m.Submit") }}
        </el-button>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import { QUESTION_TYPE_REVERSE } from "@/common/constants";
import api from "@/common/api";
import Highlight from "@/components/oj/common/Highlight";
export default {
  name: "GroupPaperDetails",
  components: {
    Highlight,
  },
  data() {
    return {
      paper: {},
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
      api
        .getExamPaperList(this.$route.params.examID, this.$route.params.paperID)
        .then(
          (res) => {
            this.paper = res.data.data[0];
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
    stringToAnswer(s) {
      let ans = "";
      for (let i = 0; i < s.length; i++) {
        if (s[i] == "1") {
          if (ans.length > 0) ans += ", ";
          ans += String.fromCharCode(i + 65);
        }
      }
      return ans;
    },
    submitScore() {
      api.submitScore(this.paper, this.examID).then((res) => {
        this.$msg.success(this.$t("m.Submitted_successfully"));
        this.init();
      });
    },
  },
  computed: {
    ...mapState({
      problemList: (state) => state.exam.examProblemList,
    }),
    ...mapGetters(["isAuthenticated", "isSuperAdmin", "isExamAdmin"]),
  },
  watch: {
    $route() {
      this.init();
    },
  },
};
</script>

<style scoped>
.question-detail {
  padding-right: 15px;
}
/deep/.el-tabs--border-card > .el-tabs__content {
  padding-top: 0px;
  padding-right: 0px;
}
.question-intr {
  border-radius: 4px;
  border: 1px solid #ddd;
  border-left: 2px solid #3498db;
  background: #fafafa;
  padding: 10px;
  line-height: 1.8;
  margin-bottom: 10px;
  font-size: 14px;
}
.filter-item {
  font-size: 13px;
  margin: 2px 5px;
}
.question-content .title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #3091f2;
}
p.content {
  margin-left: 25px;
  margin-right: 20px;
  font-size: 15px;
}
span.content {
  font-size: 15px;
}
</style>
