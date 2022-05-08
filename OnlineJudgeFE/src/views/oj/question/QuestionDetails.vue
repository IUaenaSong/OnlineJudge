<template>
  <el-row :gutter="10">
    <el-col v-if="examID" :md="6" :xs="24" :style="examID ? 'margin-top: 10px; margin-bottom: 10px;' : ''">
      <el-card :body-style="{ padding: 0 }" shadow v-loading="loadingType">
        <el-tabs
          v-model="activeType"
          type="border-card"
          @tab-click="handleClickType"
          style="border: 0"
        >
          <el-tab-pane
            v-for="(key, index) in QUESTION_TYPE_REVERSE"
            :name="index"
            :key="index"
            :disabled="questionList[index].length == 0"
          >
            <span slot="label">
              {{ $t('m.' + key.name + '_Question') }}
            </span>
            <el-button
              v-for="question in questionList[index]"
              :key="question"
              class="id-list"
              :type="submittedQuestion.indexOf(question) == -1 || questionId == question? 'primary' : 'success'"
              size="small"
              :plain="questionId != question && submittedQuestion.indexOf(question) == -1"
              @click="goExamQuestion(question)"
            >
              {{ question }}
            </el-button>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </el-col>
    <el-col :md="examID ? 18: 24" :xs="24" :style="examID ? 'margin-top: 10px; margin-bottom: 10px;' : ''">
      <el-card :body-style="{ padding: 0 }" shadow >
        <el-tabs
          v-model="activeName"
          type="border-card"
          @tab-click="handleClickTab"
          style="border: 0"
        >
          <el-tab-pane name="QuestionDetail" v-loading="loading">
            <span slot="label"
              ><i class="fa fa-list-alt">
                {{ $t('m.Question_Description') }}</i
              >
            </span>
            <div :padding="10" shadow class="question-detail">
              <div slot="header" class="panel-title">
                <span>{{ question.questionId }}</span
                ><br />
                <div class="question-intr">
                  <template>
                    <span
                      >{{ $t('m.Question_Type') }}：
                      <el-tag
                        v-if="QUESTION_TYPE_REVERSE[question.type]"
                        class="filter-item"
                        size="medium"
                        :type="QUESTION_TYPE_REVERSE[question.type].color"
                        effect="dark"
                      >
                        {{ $t('m.' + QUESTION_TYPE_REVERSE[question.type].name + '_Question') }}
                      </el-tag>
                      <el-tag
                        v-if="question.type == 1"
                        class="filter-item"
                        size="medium"
                        type="primary"
                        effect="plain"
                      >
                        {{ $t('m.' + (question.single ? 'Single_Choice' : 'Indefinite_Multiple_Choice') + '_Question') }}
                      </el-tag>
                    </span><br />
                  </template>
                  <template v-if="examID">
                    <span
                      >{{ $t('m.Score') }}：{{ question.score }}
                    </span><br />
                  </template>
                  <template v-if="question.author">
                    <span
                      >{{ $t('m.Created') }}：<el-link
                        type="info"
                        class="author-name"
                        @click="goUserHome(question.author)"
                        >{{ question.author }}</el-link
                      ></span
                    ><br />
                  </template>
                </div>
              </div>
              <div class="question-content">
                <el-alert v-show="examID && !hasSaved && examStatus == EXAM_STATUS.RUNNING" style="margin-top: -20px; margin-bottom: 5px" type="error" :closable="false">
                  {{ $t('m.The_Present_Answer_Has_Not_Been_Saved') }}
                </el-alert>
                <template>
                  <p class="title">{{ $t('m.Question_Description') }}</p>
                  <p
                    class="content markdown-body"
                    v-html="question.description"
                    v-katex
                    v-highlight
                  ></p>
                </template>
                <template v-if="question.type == 1">
                  <p class="title">{{ $t('m.Question_Choices') }}</p>
                  <p
                    v-for="(choice, index) in question.choices"
                    :key="index"
                    class="content"
                  >
                    <el-row :gutter="10">
                      <el-col :sm="2" :xs="24" v-if="question.single">
                        <el-radio-group v-model="question.radio">
                          <el-radio :label="index">
                            {{ String.fromCharCode(index + 65) }}
                          </el-radio>
                        </el-radio-group>
                      </el-col>
                      <el-col :sm="2" :xs="24" v-else>
                        <el-checkbox v-model="choice.status">
                          {{ String.fromCharCode(index + 65) }}
                        </el-checkbox>
                      </el-col>
                      <el-col :sm="22" :xs="24">
                        <span
                          class="content markdown-body"
                          v-html="$markDown.render(choice.content.toString())"
                          v-katex
                          v-highlight
                        ></span>
                      </el-col>
                    </el-row>
                  </p>
                </template>
                <template v-if="question.type == 2">
                  <p class="title">{{ $t('m.Question_Choices') }}</p>
                  <p class="content">
                    <el-switch
                      v-model="question.judge"
                      :active-text="$t('m.True')"
                      :inactive-text="$t('m.False')"
                    >
                    </el-switch>
                  </p>
                </template>
                <template v-if="(!examID || examStatus == EXAM_STATUS.ENDED) && question.answer">
                  <el-collapse v-model="activeNames">
                    <el-collapse-item name="answer">
                      <p class="title" slot="title">{{ $t('m.Question_Answer') }}</p>
                      <p
                        
                        class="content markdown-body"
                        v-html="question.answer"
                        v-katex
                        v-highlight
                      ></p>
                    </el-collapse-item>
                  </el-collapse>
                </template>
                <template v-if="examID && (question.type == 3 || question.type == 4) && examStatus != EXAM_STATUS.ENDED">
                  <p class="title" slot="title">{{ $t('m.Question_Answer') }}</p>
                  <Editor :value.sync="question.answer">
                  </Editor>
                </template>
                <template v-if="examID && examStatus != EXAM_STATUS.ENDED">
                  <el-button
                    @click="submitQuestion"
                    type="primary"
                    size="small"
                    style="margin-top: 10px"
                    :loading="btnLoading"
                  >
                    {{ $t('m.Save') }}
                  </el-button>
                  <el-alert style="margin-top:5px" type="error" v-show="examID && !hasSaved && examStatus == EXAM_STATUS.RUNNING" :closable="false">
                    {{ $t('m.The_Present_Answer_Has_Not_Been_Saved') }}
                  </el-alert>
                </template>
                
                <template v-if="examID">
                  <el-divider></el-divider>
                  <el-button
                    size="small"
                    type="warning"
                    style="float:left"
                    :disabled="questionAll.indexOf(questionId) == 0"
                    @click="goExamQuestion(questionAll[questionAll.indexOf(questionId) - 1])"
                  >{{ $t('m.Last_Question') }}</el-button>
                  <el-button
                    size="small"
                    type="warning"
                    style="float:right"
                    :disabled="questionAll.indexOf(questionId) == questionAll.length - 1"
                    @click="goExamQuestion(questionAll[questionAll.indexOf(questionId) + 1])"
                  >{{ $t('m.Next_Question') }}</el-button>
                </template>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import utils from '@/common/utils';
import api from '@/common/api';
import { QUESTION_TYPE_REVERSE, EXAM_STATUS, buildExamAnswerKey } from '@/common/constants';
import Editor from '@/components/admin/Editor.vue';
import storage from '@/common/storage';
export default {
  name: 'QuestionDetails',
  components: {
    Editor
  },
  data() {
    return {
      activeName: 'QuestionDetail',
      loading: false,
      loadingType: false,
      questionId: null,
      examID: null,
      question: {},
      QUESTION_TYPE_REVERSE: {},
      activeType: '1',
      activeNames: '',
      questionAll: [],
      questionList: {
        '1': [],
        '2': [],
        '3': [],
        '4': [],
      },
      submittedQuestion: [],
      questionSave: {},
      btnLoading: false,
      questionAnswer: '',
    }
  },
  created() {
    this.QUESTION_TYPE_REVERSE = Object.assign({}, QUESTION_TYPE_REVERSE);
    this.EXAM_STATUS = Object.assign({}, EXAM_STATUS);
  },
  mounted() {
    this.questionId = this.$route.params.questionId;
    this.examID = this.$route.params.examID;
    let key = buildExamAnswerKey(this.examID);
    this.answerList = storage.get(key) || {};
    if (this.isAuthenticated) {
      this.getQuestionStatus();
    }
    this.init();
    this.getQuestion();
    this.getMyAnswer();
  },
  methods: {
    ...mapActions(['changeDomTitle']),
    init() {
      if (!this.examID) return;
      this.loadingType = true;
      api.getExamQuestionList(0, this.examID).then(
        (res) => {
          for (let question of res.data.data) {
            this.questionList[question.type].push(question.displayId);
            this.questionAll.push(question.displayId);
          }
          this.loadingType = false;
        }
      )
    },
    getQuestionStatus() {
      if (!this.examID) return;
      api.getExamQuestionStatus(this.examID).then(
        (res) => {
          this.submittedQuestion = res.data.data;
        }
      )
    },
    getQuestion() {
      let func =
        this.$route.name === 'ExamQuestionDetails'
          ? 'getExamQuestion'
          : 'getQuestion';
      this.loading = true;
      api[func](this.questionId, this.examID).then(
        (res) => {
          let result = res.data.data;
          this.changeDomTitle({ title: result.questionId });
          let filterAnswer = !(this.isSuperAdmin || this.isGroupRoot || this.userInfo.username == result.author);
          result.choices = utils.stringToChoices(
            result.choices, filterAnswer
          );
          if (result.description) {
            result.description = this.$markDown.render(
              result.description.toString()
            );
          }
          if (result.answer) {
            result.answer = this.$markDown.render(
              result.answer.toString()
            );
          }
          this.question = result;
          this.getMyAnswer();
          this.loading = false;
        },
        (err) => {
          this.loading = false;
        }
      );
    },
    getMyAnswer() {
      if (!this.examID || this.isExamAdmin) return;
      api.getMyAnswer(this.questionId, this.examID).then(
        (res) => {
          this.questionAnswer = res.data.data;
          if (this.answerList[this.questionId] != null) {
            res.data.data = this.answerList[this.questionId];
          }
          if (this.question.type == 1) {
            if (this.question.single) {
              this.question.radio = parseInt(res.data.data);
            } else {
              for (let i = 0; i < res.data.data.length; i++) {
                if (res.data.data[i] == '1') {
                  this.question.choices[i].status = true;
                } else {
                  this.question.choices[i].status = false;
                }
              }
            }
          } else if (this.question.type == 2) {
            if (res.data.data == 'true') {
              this.question.judge = true;
            } else {
              this.question.judge = false;
            }
          } else {
             this.question.answer = res.data.data;
          }
        }
      )
    },
    goExamQuestion(questionId) {
      this.$router.push({
        name: 'ExamQuestionDetails',
        params: {
          questionId: questionId,
        },
      });
    },
    handleClickType({ name }) {
    },
    handleClickTab({ name }) {
    },
    goUserHome(username) {
      this.$router.push({
        path: '/user-home',
        query: { username },
      });
    },
    submitQuestion() {
      this.questionSave["eid"] = this.examID;
      this.questionSave["displayId"] = this.question.questionId;
      this.questionSave["answer"] = utils.questionToString(this.question);
      this.btnLoading = true;
      api.submitQuestion(this.questionSave).then(
        (res) => {
          this.$msg.success(this.$t('m.Save_Successfully'));
          this.questionAnswer = this.questionSave["answer"];
          this.answerList[this.questionId] = this.questionSave["answer"];
          let key = buildExamAnswerKey(this.examID);
          storage.set(key, this.answerList);
          if (this.submittedQuestion.indexOf(this.question.questionId) == -1) {
            this.submittedQuestion.push(this.question.questionId);
          }
          this.btnLoading = false;
        }
      ).catch(() => {
        this.btnLoading = false;
      })
    },
  },
  computed: {
    ...mapGetters([
      'isAuthenticated',
      'isSuperAdmin',
      'userInfo',
      'isGroupRoot',
      'examStatus',
      'isExamAdmin',
      'examStatus',
    ]),
    answerList: {
      get () {
        return this.$store.state.exam.answerList
      },
      set (value) {
        this.$store.commit('changeAnswerList', {value: value})
      }
    },
    hasSaved() {
      let now = utils.questionToString(this.question);
      return now == this.questionAnswer;
    }
  },
  beforeRouteLeave (to, from, next) {
    this.answerList[this.questionId] = utils.questionToString(this.question);
    let key = buildExamAnswerKey(this.examID);
    storage.set(key, this.answerList);
    next();
  },
  watch: {
    $route(newVal) {
      this.answerList[this.questionId] = utils.questionToString(this.question);
      let key = buildExamAnswerKey(this.examID);
      storage.set(key, this.answerList);
      this.questionId = newVal.params.questionId;
      this.getQuestion();
      this.getMyAnswer();
    },
    isAuthenticated(newVal) {
      if (newVal === true) {
        this.getQuestionStatus();
      }
    },
  },
}
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
.choices {
  margin-left: 25px;
  margin-right: 20px;
  margin-top: 10px;
  margin-bottom: 10px;
}
.id-list {
  margin-top: 5px;
  margin-bottom: 5px;
}
/deep/ .el-radio-group {
  margin-top: 6px;
}
/deep/ .el-radio__inner {
  width: 15px;
  height: 15px;
  font-family: -apple-system,BlinkMacSystemFont,Segoe UI,Helvetica,Arial,sans-serif,Apple Color Emoji,Segoe UI Emoji;
}
/deep/ .el-radio__label {
  font-size: 15px;
  font-family: -apple-system,BlinkMacSystemFont,Segoe UI,Helvetica,Arial,sans-serif,Apple Color Emoji,Segoe UI Emoji;
}
/deep/ .el-divider--horizontal {
  margin: 4px 0;
}
</style>