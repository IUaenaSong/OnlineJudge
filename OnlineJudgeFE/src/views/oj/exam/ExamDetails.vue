<template>
  <div class="exam-body">
    <el-row>
      <el-col :xs="24" :md="24" :lg="24" style="margin-top: 10px; margin-bottom: 10px;">
        <el-card shadow>
          <div class="exam-title">
            <div slot="header">
              <span class="panel-title">{{ exam.title }}</span>
            </div>
          </div>
          <el-row style="margin-top: 10px;">
            <el-col :span="24" class="text-align:left">
              <el-tooltip
                v-if="exam.auth != null && exam.auth != undefined"
                :content="$t('m.' + EXAM_TYPE_REVERSE[exam.auth]['tips'])"
                placement="top"
              >
                <el-tag
                  :type.sync="EXAM_TYPE_REVERSE[exam.auth]['color']"
                  effect="plain"
                >
                  {{ $t('m.' + EXAM_TYPE_REVERSE[exam.auth]['name']) }}
                </el-tag>
              </el-tooltip>
            </el-col>

          </el-row>
          <div class="exam-time">
            <el-row>
              <el-col :xs="24" :md="12" class="left">
                <p>
                  <i class="fa fa-hourglass-start" aria-hidden="true"></i>
                  {{ $t('m.StartAt') }}：{{ exam.startTime | localtime }}
                </p>
              </el-col>
              <el-col :xs="24" :md="12" class="right">
                <p>
                  <i class="fa fa-hourglass-end" aria-hidden="true"></i>
                  {{ $t('m.EndAt') }}：{{ exam.endTime | localtime }}
                </p>
              </el-col>
            </el-row>
          </div>
          <div class="slider">
            <el-slider
              v-model="progressValue"
              :format-tooltip="formatTooltip"
              :step="timeStep"
            ></el-slider>
          </div>
          <el-row>
            <el-col :span="24" style="text-align:center">
              <el-tag effect="dark" size="medium" :style="countdownColor">
                <i class="fa fa-circle" aria-hidden="true"></i>
                {{ examCountdown }}
              </el-tag>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    <div>
      <!-- 判断是否需要密码验证 -->
      <el-tabs @tab-click="tabClick" v-model="route_name">
        <el-tab-pane name="ExamDetails" lazy >
          <span slot="label"
            ><i class="el-icon-s-home"></i>&nbsp;{{ $t('m.Exam_Description') }}</span
          >
          <el-row>
            <el-col :span="24" v-if="examPasswordFormVisible" style="margin-top: 10px; margin-bottom: 10px;">
              <el-card
                class="password-form-card"
                style="text-align:center;margin-bottom:15px"
              >
                <div slot="header">
                  <span class="panel-title" style="color: #e6a23c;"
                    ><i class="el-icon-warning">
                      {{ $t('m.Password_Required') }}</i
                    ></span
                  >
                </div>
                <p class="password-form-tips">
                  {{ $t('m.To_Enter_Need_Password') }}
                </p>
                <el-form>
                  <el-input
                    v-model="examPassword"
                    type="password"
                    :placeholder="$t('m.Enter_the_exam_password')"
                    @keydown.enter.native="checkPassword"
                    style="width:70%"
                  />
                  <el-button
                    type="primary"
                    @click="checkPassword"
                    style="float:right;"
                    >{{ $t('m.Enter') }}</el-button
                  >
                </el-form>
              </el-card>
            </el-col>
            <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px;">
              <el-card class="box-card">
                <div
                  v-html="descriptionHtml"
                  v-highlight
                  class="markdown-body"
                ></div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane
          name="ExamProblemList"
          lazy
          :disabled="examMenuDisabled"
        >
          <span slot="label"
            ><i class="fa fa-list" aria-hidden="true"></i>&nbsp;{{
              $t('m.Group_Question')
            }}</span
          >
          <transition name="el-fade-in-linear">
            <router-view
              v-if="route_name === 'ExamProblemList'"
            ></router-view>
          </transition>
        </el-tab-pane>
        <el-tab-pane
          name="ExamProblemList"
          lazy
          :disabled="examMenuDisabled"
        >
          <span slot="label"
            ><i class="fa fa-list" aria-hidden="true"></i>&nbsp;{{
              $t('m.Problem')
            }}</span
          >
          <transition name="el-fade-in-linear">
            <router-view
              v-if="route_name === 'ExamProblemList'"
            ></router-view>
          </transition>
        </el-tab-pane>
        <el-tab-pane
          name="ExamSubmissionList"
          lazy
          :disabled="examMenuDisabled"
        >
          <span slot="label"
            ><i class="el-icon-menu"></i>&nbsp;{{ $t('m.Status') }}</span
          >
          <transition name="el-fade-in-linear">
            <router-view
              v-if="route_name === 'ExamSubmissionList'"
            ></router-view>
          </transition>
        </el-tab-pane>
        <el-tab-pane name="ExamRank" lazy :disabled="examMenuDisabled">
          <span slot="label"
            ><i class="fa fa-bar-chart" aria-hidden="true"></i>&nbsp;{{
              $t('m.NavBar_Rank')
            }}</span
          >
          <transition name="el-fade-in-linear">
            <router-view v-if="route_name === 'ExamRank'"></router-view>
          </transition>
        </el-tab-pane>
        <el-tab-pane
          name="ExamAnnouncementList"
          lazy
          :disabled="examMenuDisabled"
        >
          <span slot="label"
            ><i class="fa fa-bullhorn" aria-hidden="true"></i>&nbsp;{{
              $t('m.Announcement')
            }}</span
          >
          <el-row>
            <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px;"> 
              <transition name="el-fade-in-linear">
                <router-view
                  v-if="route_name === 'ExamAnnouncementList'"
                ></router-view>
              </transition>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane name="ExamComment" lazy :disabled="examMenuDisabled">
          <span slot="label"
            ><i class="fa fa-commenting" aria-hidden="true"></i>&nbsp;{{
              $t('m.Comment')
            }}</span
          >
          <el-row>
            <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px;"> 
              <transition name="el-fade-in-linear">
                <router-view v-if="route_name === 'ExamComment'"></router-view>
              </transition>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane
          name="ExamPrint"
          lazy
          :disabled="examMenuDisabled"
          v-if="exam.openPrint"
        >
          <span slot="label"
            ><i class="el-icon-printer"></i>&nbsp;{{ $t('m.Print') }}</span
          >
          <transition name="el-fade-in-linear">
            <router-view v-if="route_name === 'ExamPrint'"></router-view>
          </transition>
        </el-tab-pane>
        <el-tab-pane
          name="ExamACInfo"
          lazy
          :disabled="examMenuDisabled"
          v-if="showAdminHelper"
        >
          <span slot="label"
            ><i class="el-icon-s-help" aria-hidden="true"></i>&nbsp;{{
              $t('m.Admin_Helper')
            }}</span
          >
          <transition name="el-fade-in-linear">
            <router-view v-if="route_name === 'ExamACInfo'"></router-view>
          </transition>
        </el-tab-pane>
        <el-tab-pane
          name="ExamAdminPrint"
          lazy
          :disabled="examMenuDisabled"
          v-if="isSuperAdmin && exam.openPrint"
        >
          <span slot="label"
            ><i class="el-icon-printer"></i>&nbsp;{{
              $t('m.Admin_Print')
            }}</span
          >
          <transition name="el-fade-in-linear">
            <router-view
              v-if="route_name === 'ExamAdminPrint'"
            ></router-view>
          </transition>
        </el-tab-pane>
        <el-tab-pane
          name="ExamRejudgeAdmin"
          lazy
          :disabled="examMenuDisabled"
          v-if="isSuperAdmin"
        >
          <span slot="label"
            ><i class="el-icon-refresh" aria-hidden="true"></i>&nbsp;{{
              $t('m.Rejudge')
            }}</span
          >
          <el-row>
            <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px;"> 
              <transition name="el-fade-in-linear">
                <router-view
                  v-if="route_name === 'ExamRejudgeAdmin'"
                ></router-view>
              </transition>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>
<script>
import time from '@/common/time';
import moment from 'moment';
import api from '@/common/api';
import { mapState, mapGetters, mapActions } from 'vuex';
import { addCodeBtn } from '@/common/codeblock';
import {
  EXAM_STATUS_REVERSE,
  EXAM_STATUS,
  EXAM_TYPE_REVERSE,
  RULE_TYPE,
  buildExamAnnounceKey,
} from '@/common/constants';
import storage from '@/common/storage';
export default {
  name: 'ExamDetails',
  data() {
    return {
      route_name: 'examDetails',
      timer: null,
      EXAM_STATUS: {},
      EXAM_STATUS_REVERSE: {},
      EXAM_TYPE_REVERSE: {},
      RULE_TYPE: {},
      btnLoading: false,
      examPassword: '',
    };
  },
  created() {
    this.examID = this.$route.params.examID;
    this.route_name = this.$route.name;
    if (this.route_name == 'ExamProblemDetails') {
      this.route_name = 'ExamProblemList';
    }
    if (this.route_name == 'ExamSubmissionDetails') {
      this.route_name = 'ExamSubmissionList';
    }
    this.EXAM_TYPE_REVERSE = Object.assign({}, EXAM_TYPE_REVERSE);
    this.EXAM_STATUS = Object.assign({}, EXAM_STATUS);
    this.EXAM_STATUS_REVERSE = Object.assign({}, EXAM_STATUS_REVERSE);
    this.RULE_TYPE = Object.assign({}, RULE_TYPE);
    this.$store.dispatch('getExam').then((res) => {
      this.changeDomTitle({ title: res.data.data.title });
      let data = res.data.data;
      let endTime = moment(data.endTime);
      // 如果当前时间还是在比赛结束前的时间，需要计算倒计时，同时开启获取比赛公告的定时器
      if (endTime.isAfter(moment(data.now))) {
        // 实时更新时间
        this.timer = setInterval(() => {
          this.$store.commit('nowAdd1s');
        }, 1000);

        // 每分钟获取一次是否存在未阅读的公告
        this.announceTimer = setInterval(() => {
          let key = buildExamAnnounceKey(this.userInfo.uid, this.examID);
          let readAnnouncementList = storage.get(key) || [];
          let data = {
            cid: this.examID,
            readAnnouncementList: readAnnouncementList,
          };

          api.getExamUserNotReadAnnouncement(data).then((res) => {
            let newAnnounceList = res.data.data;
            for (let i = 0; i < newAnnounceList.length; i++) {
              readAnnouncementList.push(newAnnounceList[i].id);
              this.$notify({
                title: newAnnounceList[i].title,
                message:
                  '<p style="text-align:center;"><i class="el-icon-time"> ' +
                  time.utcToLocal(newAnnounceList[i].gmtCreate) +
                  '</i></p>' +
                  '<p style="text-align:center;color:#409eff">' +
                  this.$i18n.t(
                    'm.Please_check_the_exam_announcement_for_details'
                  ) +
                  '</p>',
                type: 'warning',
                dangerouslyUseHTMLString: true,
                duration: 0,
              });
            }
            storage.set(key, readAnnouncementList);
          });
        }, 60 * 1000);
      }

      this.$nextTick((_) => {
        addCodeBtn();
      });
    });
  },
  methods: {
    ...mapActions(['changeDomTitle']),
    formatTooltip(val) {
      if (this.exam.status == -1) {
        // 还未开始
        return '00:00:00';
      } else if (this.exam.status == 0) {
        return time.secondFormat(this.examBeginToNowDuration); // 格式化时间
      } else {
        return time.secondFormat(this.exam.duration);
      }
    },
    checkPassword() {
      if (this.examPassword === '') {
        this.$msg.warning(this.$i18n.t('m.Enter_the_exam_password'));
        return;
      }
      this.btnLoading = true;
      api.registerExam(this.examID + '', this.examPassword).then(
        (res) => {
          this.$msg.success(this.$i18n.t('m.Register_exam_successfully'));
          this.$store.commit('examIntoAccess', { intoAccess: true });
          this.btnLoading = false;
        },
        (res) => {
          this.btnLoading = false;
        }
      );
    },
    tabClick(tab) {
      let name = tab.name;
      if (name !== this.$route.name) {
        this.$router.push({ name: name });
      }
    },
  },
  computed: {
    ...mapState({
      exam: (state) => state.exam.exam,
      now: (state) => state.exam.now,
    }),
    ...mapGetters([
      'examMenuDisabled',
      'examRuleType',
      'examStatus',
      'examCountdown',
      'examBeginToNowDuration',
      'isExamAdmin',
      'isSuperAdmin',
      'ExamRealTimePermission',
      'examPasswordFormVisible',
      'userInfo',
    ]),
    progressValue: {
      get: function() {
        return this.$store.getters.examProgressValue;
      },
      set: function() {},
    },
    timeStep() {
      // 时间段平分滑条长度
      return 100 / this.exam.duration;
    },
    countdownColor() {
      if (this.examStatus) {
        return 'color:' + EXAM_STATUS_REVERSE[this.examStatus].color;
      }
    },
    showAdminHelper() {
      return this.isExamAdmin && this.examRuleType === RULE_TYPE.ACM;
    },
    descriptionHtml() {
      if (this.exam.description) {
        return this.$markDown.render(this.exam.description);
      }
    },
    examEnded() {
      return this.examStatus === EXAM_STATUS.ENDED;
    },
  },
  watch: {
    $route(newVal) {
      this.route_name = newVal.name;
      if (newVal.name == 'ExamProblemDetails') {
        this.route_name = 'ExamProblemList';
      }
      if (this.route_name == 'ExamSubmissionDetails') {
        this.route_name = 'ExamSubmissionList';
      }
      this.examID = newVal.params.examID;
      this.changeDomTitle({ title: this.exam.title });
    },
  },
  beforeDestroy() {
    clearInterval(this.timer);
    clearInterval(this.announceTimer);
    this.$store.commit('clearExam');
  },
};
</script>
<style scoped>
.panel-title {
  font-size: 1.5rem !important;
  font-weight: 500;
}
@media screen and (min-width: 768px) {
  .exam-time .left {
    text-align: left;
  }
  .exam-time .right {
    text-align: right;
  }
  .password-form-card {
    width: 400px;
    margin: 0 auto;
  }
}
@media screen and (max-width: 768px) {
  .exam-time .left,
  .exam-time .right {
    text-align: center;
  }
}
/deep/.el-slider__button {
  width: 20px !important;
  height: 20px !important;
  background-color: #409eff !important;
}
/deep/.el-slider__button-wrapper {
  z-index: 500;
}
/deep/.el-slider__bar {
  height: 10px !important;
  background-color: #09be24 !important;
}
/deep/ .el-card__header {
  border-bottom: 0px;
  padding-bottom: 0px;
}
/deep/.el-tabs__nav-wrap {
  background: #fff;
  border-radius: 3px;
}
/deep/.el-tabs--top .el-tabs__item.is-top:nth-child(2) {
  padding-left: 20px;
}
/deep/.el-tabs__header {
  margin-bottom: 0px;
}
.exam-title {
  text-align: center;
}
.exam-time {
  width: 100%;
  font-size: 16px;
}
.el-tag--dark {
  border-color: #fff;
}
.el-tag {
  color: rgb(25, 190, 107);
  background: #fff;
  border: 1px solid #e9eaec;
  font-size: 18px;
}
.password-form-tips {
  text-align: center;
  font-size: 14px;
}
</style>
