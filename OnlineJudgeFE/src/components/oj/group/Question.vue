<template>
  <el-row>
    <el-col :span="24">
      <el-card shadow="never">
        <div slot="header">
          <span class="panel-title home-title">{{ title }}</span>
        </div>
        <el-form
          ref="form"
          :model="question"
          :rules="rules"
          label-position="top"
        >
          <el-row>
            <el-col :span="24">
              <el-form-item
                prop="questionId"
                :label="$t('m.Question_Display_ID')"
                required
              >
                <el-input
                  :placeholder="$t('m.Question_Display_ID')"
                  v-model="question.questionId"
                >
                  <template slot="prepend" >
                    {{ group.shortName.toUpperCase() }}
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-row :gutter="20" v-if="examID">
                <el-col :md="12" :xs="24">
                  <el-form-item :label="$t('m.Exam_Display_ID')" required>
                    <el-input
                      :placeholder="$t('m.Exam_Display_ID')"
                      v-model="examQuestion.displayId"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :md="12" :xs="24">
                  <el-form-item :label="$t('m.Score')" required>
                    <el-input
                      :placeholder="$t('m.Score')"
                      v-model="examQuestion.score"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-col>
            <el-col :span="24">
              <el-row :gutter="10">
                <el-col :sm="12" :xs="24">
                  <el-form-item
                    prop="type"
                    :label="$t('m.Question_Type')"
                    required
                  >
                    <el-radio-group v-model="question.type" @change="switchType">
                      <el-radio :label="1">{{ $t('m.Choice_Question') }}</el-radio>
                      <el-radio :label="2">{{ $t('m.Judge_Question') }}</el-radio>
                      <el-radio :label="3">{{ $t('m.Blank_Question') }}</el-radio>
                      <el-radio :label="4">{{ $t('m.Answer_Question') }}</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
                <el-col :sm="12" :xs="24" v-if="question.type == 1">
                  <el-form-item
                    prop="single"
                    :label="$t('m.Question_Type')"
                    required
                  >
                    <el-switch
                      v-model="question.single"
                      :active-text="$t('m.Single_Choice_Question')"
                      :inactive-text="$t('m.Indefinite_Multiple_Choice_Question')"
                    >
                    </el-switch>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-col>
            <el-col :span="24">
              <el-row :gutter="10">
                <el-col :sm="12" :xs="24">
                  <el-form-item :label="$t('m.Auth')" required>
                    <el-select v-model="question.auth" size="small">
                      <el-option
                        :label="$t('m.Public_Question')"
                        :value="1"
                      ></el-option>
                      <el-option
                        :label="$t('m.Private_Question')"
                        :value="2"
                      ></el-option>
                      <el-option
                        :label="$t('m.Exam_Question')"
                        :value="3"
                        :disabled="!examID"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :sm="12" :xs="24">
                  <el-form-item
                    :label="$t('m.Answer_Share')"
                    required
                  >
                    <el-switch
                      v-model="question.share"
                    >
                    </el-switch>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-col>
            <el-col :span="24">
              <el-form-item
                prop="description"
                :label="$t('m.Question_Description')"
                required
              >
                <Editor :value.sync="question.description"></Editor>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item
                prop="answer"
                :label="$t('m.Question_Answer')"
                required
              >
                <Editor :value.sync="question.answer"></Editor>
              </el-form-item>
            </el-col>
            <el-col :span="24" v-if="question.type == 1">
              <el-form-item
                :label="$t('m.Question_Choices')"
                required
              >
                <el-button
                  @click="addChoice"
                  icon="el-icon-plus"
                  type="success"
                  size="small"
                  >{{ $t('m.Add_Choice') }}
                </el-button>
                <el-button
                  @click="deleteAllChoices"
                  icon="el-icon-minus"
                  type="danger"
                  size="small"
                  >{{ $t('m.Delete_All') }}
                </el-button>
                <div
                  v-for="(choice, index) in question.choices"
                  :key="index"
                  class="choices"
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
                    <el-col :sm="19" :xs="24" v-if="choice.content != null && choice.content != undefined">
                      <Editor
                        :value.sync="choice.content"
                        :toolbarsFlag="false"
                        :height="50"
                      ></Editor>
                    </el-col>
                    <el-col :sm="3" :xs="24">
                      <el-button
                        type="danger"
                        size="small"
                        icon="el-icon-delete"
                        @click="deleteChoice(index)"
                      >
                        {{ $t('m.Delete') }}
                      </el-button>
                    </el-col>
                  </el-row>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24" v-if="question.type == 1 && question.single" class="right-answer">
              {{ $t('m.Right_Answer') + ': ' }}
              <span v-if="question.radio != null">
                {{ String.fromCharCode(question.radio + 65) }}
              </span>
            </el-col>
            <el-col :span="24" v-if="question.type == 1 && !question.single" class="right-answer">
              {{ $t('m.Right_Answer') + ': ' }}
              <span
                v-for="(choice, index) in question.choices"
                :key="index"
              >
                {{ choice.status ? String.fromCharCode(index + 65) + ',' : '' }}
              </span>
            </el-col>
            <el-col :span="24" v-if="question.type == 2">
              <el-form-item
                prop="judge"
                :label="$t('m.Question_Choices')"
                required
              >
                <el-switch
                  v-model="question.judge"
                  :active-text="$t('m.True')"
                  :inactive-text="$t('m.False')"
                >
                </el-switch>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-button type="primary" @click.native="submit" size="small">{{
                $t('m.Save')
              }}</el-button>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import utils from '@/common/utils';
import api from '@/common/api';
import { mapGetters } from 'vuex';
import Editor from '@/components/admin/Editor.vue';
export default {
  name: 'GroupQuestion',
  components: {
    Editor
  },
  props: {
    mode: {
      type: String,
      default: 'add'
    },
    title: {
      type: String,
      default: 'Create Question'
    },
    examID: {
      type: Number,
      default: null
    },
    qid: {
      type: Number,
      default: null
    },
    apiMethod: {
      type: String,
      default: 'addGroupQuestion',
    }
  },
  data() {
    return {
      rules: {
        description: {
          required: true,
          message: 'Description is required',
          trigger: 'blur',
        },
      },
      question: {
        questionId: '',
        type: 1,
        auth: 1,
        single: true,
        judge: null,
        description: '',
        answer: '',
        radio: null,
        share: false,
        choices: [],
      },
      reQuestion: {},
      examQuestion: {
        displayId: null,
        score: null,
        eid: null,
        qid: null,
      },
    }
  },
  mounted() {
    this.problem = this.reProblem = {
      questionId: '',
      type: 1,
      auth: 1,
      single: true,
      judge: null,
      description: '',
      answer: '',
      radio: null,
      share: false,
      choices: [],
    }
    if (this.examID) {
      this.question.auth = this.reQuestion.auth = 3;
    }
    this.init();
  },
  watch: {
    $route() {
      this.$refs.form.resetFields();
      this.question = this.reQuestion;
      this.init();
    },
  },
  methods: {
    init() {
      if (this.mode === 'edit') {
        api.getGroupQuestion(this.qid).then((res) => {
          let data = res.data.data;
          data.choices = utils.stringToChoices(data.choices);
          data.questionId = data.questionId.slice(this.group.shortName.length);
          this.question = data;
          if (this.examID) {
            api.getGroupExamQuestion(this.qid, this.examID).then((res) => {
              this.examQuestion = res.data.data;
            });
          }
        })
      } else {
        this.addChoice();
      }
    },
    switchType(type) {
      this.question.type = type;
    },
    addChoice() {
      this.question.choices.push({ content: '', status: false });
    },
    deleteChoice(index) {
      this.question.choices.splice(index, 1);
      if (this.question.radio == index) {
        this.question.radio = null;
      }
    },
    deleteAllChoices() {
      this.$confirm(this.$i18n.t('m.Delete_Choice_Tips'), this.$i18n.t('m.Warning'), {
        type: 'warning',
      }).then(() => {
          this.question.choices = [];
          this.question.radio = null;
        },
        () => {}
      );
    },
    submit() {
      if (!this.question.questionId) {
        this.$msg.error(
          this.$i18n.t('m.Question_Display_ID') +
            ' ' +
            this.$i18n.t('m.is_required')
        );
        return;
      }
      if (this.mode === 'add') {
        this.question.gid = this.$route.params.groupID;
        this.question.author = this.userInfo.username;
      }
      if (this.question.type == 1) {
        if (this.question.choices.length == 0) {
          this.$msg.error(this.$i18n.t('m.Question_No_Choice'));
          return;
        }      
        let count = 0;
        for (let choice of this.question.choices) {
          if (choice.status) {
            count += 1;
          }
        }
        if (this.question.single && (this.question.radio == null ||
          this.question.radio < 0 ||
          this.question.radio >= this.question.choices.length)
            || !this.question.single && count == 0) {
          this.$msg.error(this.$i18n.t('m.Question_No_Right_Answer'));
          return;
        }
      }
      let tmp = this.question.choices;
      this.question.choices = utils.choicesToString(
        this.question.choices
      );
      this.question.score = 0;
      api[this.apiMethod](this.question)
        .then((res) => {
          if (this.examID) {
            if (res.data.data) {
              this.examQuestion['qid'] = res.data.data.qid;
              this.examQuestion['eid'] = this.examID;
            }
            api.updateGroupExamQuestion(this.examQuestion).then((res) => {
              if (this.mode === 'edit') {
                this.$msg.success(this.$t('m.Update_Successfully'));
                this.$emit("handleEditPage");
              } else {
                this.$msg.success(this.$t('m.Create_Successfully'));
                if (this.examID) {
                  this.$emit("handleCreateQuestionPage");
                } else {
                  this.$emit("handleCreatePage");
                }
              }
              this.$emit("currentChange", 1);
            });
          } else {
            if (this.mode === 'edit') {
              this.$msg.success(this.$t('m.Update_Successfully'));
              this.$emit("handleEditPage");
            } else {
              this.$msg.success(this.$t('m.Create_Successfully'));
              if (this.examID) {
                this.$emit("handleCreateQuestionPage");
              } else {
                this.$emit("handleCreatePage");
              }
            }
            this.$emit("currentChange", 1);
          }
          
        })
        .catch(() => {});
        this.question.choices = tmp;
    }
  },
  computed: {
    ...mapGetters(['userInfo', 'group']),
  },
}
</script>

<style scoped>
/deep/.el-form-item__label {
  padding: 0 !important;
}
.el-form-item {
  margin-bottom: 10px !important;
}
.choices {
  margin-top: 10px;
  margin-bottom: 10px;
}
.right-answer {
  font-size: 15px;
  margin-bottom: 10px;
}
</style>