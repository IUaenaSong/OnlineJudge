<template>
  <el-row>
    <el-col :span="24">
      <el-card shadow="never">
        <div slot="header">
          <span class="panel-title home-title">
            {{ title }}
          </span>
        </div>
        <el-form label-position="top">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item :label="$t('m.Exam_Title')" required>
                <el-input
                  v-model="exam.title"
                  :placeholder="$t('m.Exam_Title')"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item :label="$t('m.Exam_Description')" required>
                <Editor :value.sync="exam.description"></Editor>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item :label="$t('m.Exam_Start_Time')" required>
                <el-date-picker
                  v-model="exam.startTime"
                  @change="changeDuration"
                  type="datetime"
                  :placeholder="$t('m.Exam_Start_Time')"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item :label="$t('m.Exam_End_Time')" required>
                <el-date-picker
                  v-model="exam.endTime"
                  @change="changeDuration"
                  type="datetime"
                  :placeholder="$t('m.Exam_End_Time')"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>

            <el-col :md="8" :xs="24">
              <el-form-item :label="$t('m.Exam_Duration')" required>
                <el-input v-model="durationText" disabled> </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item
                :label="$t('m.Rank_Score_Type')"
              >
                <el-radio
                  class="radio"
                  v-model="exam.rankScoreType"
                  label="Recent"
                  >{{ $t('m.Rank_Score_Type_Recent') }}</el-radio
                >
                <el-radio
                  class="radio"
                  v-model="exam.rankScoreType"
                  label="Highest"
                  >{{ $t('m.Rank_Score_Type_Highest') }}</el-radio
                >
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :md="12" :xs="24">
              <el-form-item :label="$t('m.Real_Score')" required>
                <el-switch
                  v-model="exam.realScore"
                  active-color="#13ce66"
                  inactive-color=""
                >
                </el-switch>
              </el-form-item>
            </el-col>
            <el-col :md="12" :xs="24">
              <el-form-item
                :label="$t('m.Auto_Real_Score')"
                required
              >
                <el-switch
                  v-model="exam.autoRealRank"
                  :active-text="$t('m.Real_Score_After_Exam')"
                  :inactive-text="$t('m.Seal_Score_After_Exam')"
                >
                </el-switch>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item :label="$t('m.Rank_Show_Name')" required>
                <el-radio-group v-model="exam.rankShowName">
                  <el-radio label="username">{{
                    $t('m.Show_Username')
                  }}</el-radio>
                  <el-radio label="nickname">{{
                    $t('m.Show_Nickname')
                  }}</el-radio>
                  <el-radio label="realname">{{
                    $t('m.Show_Realname')
                  }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item :label="$t('m.Exam_Auth')" required>
                <el-select v-model="exam.auth">
                  <el-option :label="$t('m.Exam_Public')" :value="0"></el-option>
                  <el-option :label="$t('m.Exam_Private')" :value="1"></el-option>
                  <el-option :label="$t('m.Exam_Protected')" :value="2"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item
                :label="$t('m.Exam_Password')"
                v-show="exam.auth != 0"
                :required="exam.auth != 0"
              >
                <el-input
                  v-model="exam.pwd"
                  :placeholder="$t('m.Exam_Password')"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item
                :label="$t('m.Account_Limit')"
                v-show="exam.auth != 0"
                :required="exam.auth != 0"
              >
                <el-switch v-model="exam.openAccountLimit"> </el-switch>
              </el-form-item>
            </el-col>

            <template v-if="exam.openAccountLimit">
              <el-form :model="formRule">
                <el-col :md="6" :xs="24">
                  <el-form-item :label="$t('m.Prefix')" prop="prefix">
                    <el-input
                      v-model="formRule.prefix"
                      placeholder="Prefix"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :md="6" :xs="24">
                  <el-form-item :label="$t('m.Suffix')" prop="suffix">
                    <el-input
                      v-model="formRule.suffix"
                      placeholder="Suffix"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :md="6" :xs="24">
                  <el-form-item :label="$t('m.Start_Number')" prop="number_from">
                    <el-input-number
                      v-model="formRule.number_from"
                      style="width: 100%"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :md="6" :xs="24">
                  <el-form-item :label="$t('m.End_Number')" prop="number_to">
                    <el-input-number
                      v-model="formRule.number_to"
                      style="width: 100%"
                    ></el-input-number>
                  </el-form-item>
                </el-col>

                <div
                  class="userPreview"
                  v-if="formRule.number_from <= formRule.number_to"
                >
                  {{ $t('m.The_allowed_account_will_be') }}
                  {{ formRule.prefix + formRule.number_from + formRule.suffix }},
                  <span v-if="formRule.number_from + 1 < formRule.number_to">
                    {{
                      formRule.prefix +
                        (formRule.number_from + 1) +
                        formRule.suffix +
                        '...'
                    }}
                  </span>
                  <span v-if="formRule.number_from + 1 <= formRule.number_to">
                    {{ formRule.prefix + formRule.number_to + formRule.suffix }}
                  </span>
                </div>

                <el-col :md="24" :xs="24">
                  <el-form-item :label="$t('m.Extra_Account')" prop="prefix">
                    <el-input
                      type="textarea"
                      :placeholder="$t('m.Extra_Account_Tips')"
                      :rows="8"
                      v-model="formRule.extra_account"
                    >
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-form>
            </template>
          </el-row>
        </el-form>
        <el-button type="primary" @click.native="submit">{{
          $t('m.Save')
        }}</el-button>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import api from '@/common/api';
import time from '@/common/time';
import moment from 'moment';
import { mapGetters } from 'vuex';
import Editor from '@/components/admin/Editor.vue';
export default {
  name: 'GroupExam',
  components: {
    Editor,
  },
  props: {
    mode: {
      type: String,
      default: 'edit'
    },
    title: {
      type: String,
      default: 'Edit Exam'
    },
    apiMethod: {
      type: String,
      default: 'addGroupExam'
    },
    cid: {
      type: Number,
      default: null
    },
  },
  data() {
    return {
      disableRuleType: false,
      durationText: '',
      seal_rank_time: 2,
      exam: {
        title: '',
        description: '',
        startTime: '',
        endTime: '',
        duration: 0,
        pwd: '',
        realScore: false,
        autoRealScore: true,
        auth: 0,
        rankShowName: 'username',
        openAccountLimit: false,
        accountLimitRule: '',
        rankScoreType: 'Recent',
      },
      formRule: {
        prefix: '',
        suffix: '',
        number_from: 0,
        number_to: 10,
        extra_account: '',
      },
    };
  },
  mounted() {
    this.init();
  },
  watch: {
    $route() {
      this.exam = {
        title: '',
        description: '',
        startTime: '',
        endTime: '',
        duration: 0,
        pwd: '',
        realScore: false,
        autoRealScore: true,
        auth: 0,
        rankShowName: 'username',
        openAccountLimit: false,
        accountLimitRule: '',
        rankScoreType: 'Recent',
      };
      this.init();
    },
  },
  methods: {
    init() {
      if (this.mode === 'edit') {
        api.getGroupExam(this.cid).then((res) => {
          let data = res.data.data;
          this.exam = data;
          this.changeDuration();
          if (this.exam.accountLimitRule) {
            this.formRule = this.changeStrToAccountRule(
              this.exam.accountLimitRule
            );
          }
        })
        .catch(() => {});
      }
    },
    submit() {
      if (!this.exam.title) {
        this.$msg.error(
          this.$i18n.t('m.Exam_Title') + ' ' + this.$i18n.t('m.is_required')
        );
        return;
      }
      if (!this.exam.description) {
        this.$msg.error(
          this.$i18n.t('m.Exam_Description') +
            ' ' +
            this.$i18n.t('m.is_required')
        );
        return;
      }
      if (!this.exam.startTime) {
        this.$msg.error(
          this.$i18n.t('m.Exam_Start_Time') +
            ' ' +
            this.$i18n.t('m.is_required')
        );
        return;
      }
      if (!this.exam.endTime) {
        this.$msg.error(
          this.$i18n.t('m.Exam_End_Time') +
            ' ' +
            this.$i18n.t('m.is_required')
        );
        return;
      }
      if (!this.exam.duration || this.exam.duration <= 0) {
        this.$msg.error(this.$i18n.t('m.Exam_Duration_Check'));
        return;
      }
      if (this.exam.auth != 0 && !this.exam.pwd) {
        this.$msg.error(
          this.$i18n.t('m.Exam_Password') +
            ' ' +
            this.$i18n.t('m.is_required')
        );
        return;
      }
      if (this.exam.openAccountLimit) {
        this.exam.accountLimitRule = this.changeAccountRuleToStr(
          this.formRule
        );
      }
      let data = Object.assign({}, this.exam);
      if (this.mode === 'add') {
        data['uid'] = this.userInfo.uid;
        data['author'] = this.userInfo.username;
        data['gid'] = this.$route.params.groupID;
      }
      api[this.apiMethod](data)
        .then((res) => {
          this.$msg.success('success');
          this.$router.push({ name: 'GroupExamList' });
          if (this.mode === 'edit') {
            this.$emit("handleEditPage");
          } else {
            this.$emit("handleCreatePage");
          }
          this.$emit("currentChange", 1);
        })
        .catch(() => {});
    },
    changeDuration() {
      let start = this.exam.startTime;
      let end = this.exam.endTime;
      let durationMS = time.durationMs(start, end);
      if (durationMS < 0) {
        this.durationText = this.$i18n.t('m.Contets_Time_Check');
        this.exam.duration = 0;
        return;
      }
      if (start != '' && end != '') {
        this.durationText = time.formatSpecificDuration(start, end);
        this.exam.duration = durationMS;
      }
    },
    changeAccountRuleToStr(formRule) {
      let result =
        '<prefix>' +
        formRule.prefix +
        '</prefix><suffix>' +
        formRule.suffix +
        '</suffix><start>' +
        formRule.number_from +
        '</start><end>' +
        formRule.number_to +
        '</end><extra>' +
        formRule.extra_account +
        '</extra>';
      return result;
    },
    changeStrToAccountRule(value) {
      let reg =
        '<prefix>([\\s\\S]*?)</prefix><suffix>([\\s\\S]*?)</suffix><start>([\\s\\S]*?)</start><end>([\\s\\S]*?)</end><extra>([\\s\\S]*?)</extra>';
      let re = RegExp(reg, 'g');
      let tmp = re.exec(value);
      return {
        prefix: tmp[1],
        suffix: tmp[2],
        number_from: tmp[3],
        number_to: tmp[4],
        extra_account: tmp[5],
      };
    },
  },
  computed: {
    ...mapGetters(['userInfo']),
  },
};
</script>
<style scoped>
.userPreview {
  padding-left: 10px;
  padding-top: 20px;
  padding-bottom: 20px;
  color: red;
  font-size: 16px;
  margin-bottom: 10px;
}
.input-new-star-user {
  width: 200px;
}
</style>
