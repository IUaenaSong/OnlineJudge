<template>
  <el-card :body-style="{ padding: 0 }" shadow>
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
            <template v-if="question.description">
              <p class="title">{{ $t('m.Question_Description') }}</p>
              <p
                class="content markdown-body"
                v-html="question.description"
                v-katex
                v-highlight
              ></p>
            </template>
            <template v-if="userInfo.username == question.author || isGroupRoot || isSuperAdmin">
              <p class="title">{{ $t('m.Question_Answer') }}</p>
              <p
                class="content markdown-body"
                v-html="question.answer"
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
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import utils from '@/common/utils';
import api from '@/common/api';
import { QUESTION_TYPE_REVERSE } from '@/common/constants';
import Editor from '@/components/admin/Editor.vue';
export default {
  name: 'QuestionDetails',
  components: {
    Editor
  },
  data() {
    return {
      activeName: 'QuestionDetail',
      loading: false,
      questionId: null,
      examID: null,
      question: {},
      QUESTION_TYPE_REVERSE: {},
    }
  },
  created() {
    this.QUESTION_TYPE_REVERSE = Object.assign({}, QUESTION_TYPE_REVERSE);
  },
  mounted() {
    this.init();
  },
  methods: {
    ...mapActions(['changeDomTitle']),
    init() {
      if (this.$route.params.examID) {
        this.examID = this.$route.params.examID;
      }
      this.questionId = this.$route.params.questionId;
      let func =
        this.$route.name === 'GroupExamQuestionDetails'
          ? 'getExamQuestion'
          : 'getQuestion';
      this.loading = true;
      api[func](this.questionId, this.examID).then(
        (res) => {
          let result = res.data.data;
          this.changeDomTitle({ title: result.questionId });
          result.choices = utils.stringToChoices(
            result.choices
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
          this.loading = false;
        },
        (err) => {
          this.loading = false;
        }
      );
    },
    handleClickTab({ name }) {
    },
    goUserHome(username) {
      this.$router.push({
        path: '/user-home',
        query: { username },
      });
    },
  },
  computed: {
    ...mapGetters([
      'isSuperAdmin',
      'userInfo',
      'isGroupRoot',
    ]),
  },
  watch: {
    $route() {
      this.init();
    },
  }
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
</style>