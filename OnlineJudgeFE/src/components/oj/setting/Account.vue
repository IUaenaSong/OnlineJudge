<template>
  <div class="setting-main">
    <el-row :gutter="20">
      <el-col :sm="24" :md="8" :lg="8">
        <div class="left">
          <p class="section-title">{{ $t('m.Change_Password') }}</p>
          <el-form
            class="setting-content"
            ref="formPassword"
            :model="formPassword"
            :rules="rulePassword"
          >
            <el-form-item :label="$t('m.Old_Password')" prop="oldPassword">
              <el-input v-model="formPassword.oldPassword" type="password" />
            </el-form-item>
            <el-form-item :label="$t('m.New_Password')" prop="newPassword">
              <el-input v-model="formPassword.newPassword" type="password" />
            </el-form-item>
            <el-form-item
              :label="$t('m.Confirm_New_Password')"
              prop="againPassword"
            >
              <el-input v-model="formPassword.againPassword" type="password" />
            </el-form-item>
          </el-form>
          <el-popover
            placement="top"
            width="350"
            v-model="visible.passwordSlideBlock"
            trigger="click"
          >
            <el-button
              type="primary"
              slot="reference"
              :loading="loading.btnPassword"
              :disabled="disabled.btnPassword"
              >{{ $t('m.Update_Password') }}</el-button
            >
            <slide-verify
              :l="42"
              :r="10"
              :w="325"
              :h="100"
              :accuracy="3"
              @success="changePassword"
              @again="onAgain('password')"
              :slider-text="$t('m.Slide_Verify')"
              ref="passwordSlideBlock"
              v-show="!verify.passwordSuccess"
            >
            </slide-verify>
            <el-alert
              :title="$t('m.Slide_Verify_Success')"
              type="success"
              :description="verify.passwordMsg"
              v-show="verify.passwordSuccess"
              :center="true"
              :closable="false"
              show-icon
            >
            </el-alert>
          </el-popover>
        </div>
        <el-alert
          v-show="visible.passwordAlert.show"
          :title="visible.passwordAlert.title"
          :type="visible.passwordAlert.type"
          :description="visible.passwordAlert.description"
          :closable="false"
          effect="dark"
          style="margin-top:15px"
          show-icon
        >
        </el-alert>
      </el-col>
      <el-col :sm="24" :md="8" :lg="8">
        <div class="right">
          <p class="section-title">{{ $t('m.Change_Email') }}</p>
          <el-form
            class="setting-content"
            ref="formEmail"
            :model="formEmail"
            :rules="ruleEmail"
          >
            <el-form-item :label="$t('m.Current_Password')" prop="password">
              <el-input v-model="formEmail.password" type="password" />
            </el-form-item>
            <el-form-item :label="$t('m.Old_Email')">
              <el-input
                v-model="formEmail.oldEmail"
                disabled
                prefix-icon="el-icon-message"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('m.New_Email')" prop="newEmail">
              <el-input
                v-model="formEmail.newEmail"
                prefix-icon="el-icon-message"
              >
                <el-button
                  slot="append"
                  icon="el-icon-message"
                  type="primary"
                  @click.native="sendChangeEmail"
                  :loading="btnEmailLoading"
                >
                  <span v-show="btnEmailLoading">{{ emailCountdownNum }}</span>
                </el-button>
              </el-input>
            </el-form-item>
            <el-form-item :label="$t('m.Captcha')" prop="code">
              <el-input
                v-model="formEmail.code"
                prefix-icon="el-icon-s-check"
                :placeholder="$t('m.Change_Email_Captcha')"
              ></el-input>
            </el-form-item>
          </el-form>
          <el-popover
            placement="top"
            width="350"
            v-model="visible.emailSlideBlock"
            trigger="click"
          >
            <el-button
              type="primary"
              slot="reference"
              :loading="loading.btnEmail"
              :disabled="disabled.btnEmail"
              >{{ $t('m.Update_Email') }}</el-button
            >
            <slide-verify
              :l="42"
              :r="10"
              :w="325"
              :h="100"
              :accuracy="3"
              @success="changeEmail"
              @again="onAgain('email')"
              :slider-text="$t('m.Slide_Verify')"
              ref="emailSlideBlock"
              v-show="!verify.emailSuccess"
            >
            </slide-verify>
            <el-alert
              :title="$t('m.Slide_Verify_Success')"
              type="success"
              :description="verify.emailMsg"
              v-show="verify.emailSuccess"
              :center="true"
              :closable="false"
              show-icon
            >
            </el-alert>
          </el-popover>
        </div>
        <el-alert
          v-show="visible.emailAlert.show"
          :title="visible.emailAlert.title"
          :type="visible.emailAlert.type"
          :description="visible.emailAlert.description"
          :closable="false"
          effect="dark"
          style="margin-top:15px"
          show-icon
        >
        </el-alert>
      </el-col>
      <el-col :sm="24" :md="8" :lg="8">
        <div class="right">
          <p class="section-title">{{ $t('m.Change_Mobile') }}</p>
          <el-form
            class="setting-content"
            ref="formMobile"
            :model="formMobile"
            :rules="ruleMobile"
          >
            <el-form-item :label="$t('m.Current_Password')" prop="password">
              <el-input
                v-model="formMobile.password"
                type="password"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('m.Old_Mobile')">
              <el-input
                v-model="formMobile.oldMobile"
                disabled
                prefix-icon="el-icon-mobile"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('m.New_Mobile')" prop="newMobile">
              <el-input
                v-model="formMobile.newMobile"
                prefix-icon="el-icon-mobile"
              >
                <el-button
                  slot="append"
                  icon="el-icon-message"
                  type="primary"
                  @click.native="sendChangeMobile"
                  :loading="btnMobileLoading"
                >
                  <span v-show="btnMobileLoading">{{ mobileCountdownNum }}</span>
                </el-button>
              </el-input>
            </el-form-item>
            <el-form-item :label="$t('m.Captcha')" prop="code">
              <el-input
                v-model="formMobile.code"
                prefix-icon="el-icon-s-check"
                :placeholder="$t('m.Change_Mobile_Captcha')"
              ></el-input>
            </el-form-item>
          </el-form>
          <el-popover
            placement="top"
            width="350"
            v-model="visible.mobileSlideBlock"
            trigger="click"
          >
            <el-button
              type="primary"
              slot="reference"
              :loading="loading.btnMobile"
              :disabled="disabled.btnMobile"
              >{{ $t('m.Update_Mobile') }}</el-button
            >
            <slide-verify
              :l="42"
              :r="10"
              :w="325"
              :h="100"
              :accuracy="3"
              @success="changeMobile"
              @again="onAgain('mobile')"
              :slider-text="$t('m.Slide_Verify')"
              ref="mobileSlideBlock"
              v-show="!verify.mobileSuccess"
            >
            </slide-verify>
            <el-alert
              :title="$t('m.Slide_Verify_Success')"
              type="success"
              :description="verify.mobileMsg"
              v-show="verify.mobileSuccess"
              :center="true"
              :closable="false"
              show-icon
            >
            </el-alert>
          </el-popover>
        </div>
        <el-alert
          v-show="visible.mobileAlert.show"
          :title="visible.mobileAlert.title"
          :type="visible.mobileAlert.type"
          :description="visible.mobileAlert.description"
          :closable="false"
          effect="dark"
          style="margin-top:15px"
          show-icon
        >
        </el-alert>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import api from '@/common/api';
import 'element-ui/lib/theme-chalk/display.css';
export default {
  data() {
    const oldPasswordCheck = [
      {
        required: true,
        trigger: 'blur',
        message: this.$i18n.t('m.Password_Check_Required'),
      },
      {
        trigger: 'blur',
        min: 6,
        max: 20,
        message: this.$i18n.t('m.Password_Check_Between'),
      },
    ];
    const CheckAgainPassword = (rule, value, callback) => {
      if (value !== this.formPassword.newPassword) {
        callback(new Error(this.$i18n.t('m.Password_does_not_match')));
      }
      callback();
    };
    const CheckNewPassword = (rule, value, callback) => {
      if (this.formPassword.oldPassword !== '') {
        if (this.formPassword.oldPassword === this.formPassword.newPassword) {
          callback(
            new Error(this.$i18n.t('m.The_new_password_does_not_change'))
          );
        } else {
          // 对第二个密码框再次验证
          this.$refs.formPassword.validateField('again_password');
        }
      }
      callback();
    };
    const CheckEmail = (rule, value, callback) => {
      if (this.formEmail.oldEmail !== '') {
        if (this.formEmail.oldEmail === this.formEmail.newEmail) {
          callback(new Error(this.$i18n.t('m.The_new_email_does_not_change')));
        }
      }
      callback();
    };
    const CheckMobile = (rule, value, callback) => {
      if (this.formMobile.oldMobile !== '') {
        if (this.formMobile.oldMobile === this.formMobile.newMobile) {
          callback(new Error(this.$i18n.t('m.The_new_mobile_does_not_change')));
        }
      }
      callback();
    };
    const IsMobile = (rule, value, callback) => {
      if (!/^[1][3,4,5,6,7,8,9][0-9]{9}$/.test(value)) {
        callback(new Error(this.$i18n.t('m.Mobile_Check_Format')));
      }
      callback();
    }
    return {
      btnEmailLoading: false,
      btnMobileLoading: false,
      emailCountdownNum: null,
      mobileCountdownNum: null,
      loading: {
        btnPassword: false,
        btnEmail: false,
        btnMobile: false,
      },
      disabled: {
        btnPassword: false,
        btnEmail: false,
        btnMobile: false,
      },
      verify: {
        passwordSuccess: false,
        passwordMsg: '',
        emailSuccess: false,
        emailMsg: '',
        mobileSuccess: false,
        mobileMsg: '',
      },
      visible: {
        passwordAlert: {
          type: 'success',
          show: false,
          title: '',
          description: '',
        },
        emailAlert: {
          type: 'success',
          show: false,
          title: '',
          description: '',
        },
        mobileAlert: {
          type: 'success',
          show: false,
          title: '',
          description: '',
        },
        passwordSlideBlock: false,
        emailSlideBlock: false,
        mobileSlideBlock: false,
      },
      formPassword: {
        oldPassword: '',
        newPassword: '',
        againPassword: '',
      },
      formEmail: {
        password: '',
        oldEmail: '',
        newEmail: '',
        code: '',
      },
      formMobile: {
        password: '',
        oldMobile: '',
        newMobile: '',
        code: '',
      },
      rulePassword: {
        oldPassword: oldPasswordCheck,
        newPassword: [
          {
            required: true,
            trigger: 'blur',
            message: this.$i18n.t('m.Password_Check_Required'),
          },
          {
            trigger: 'blur',
            min: 6,
            max: 20,
            message: this.$i18n.t('m.Password_Check_Between'),
          },
          { validator: CheckNewPassword, trigger: 'blur' },
        ],
        againPassword: [
          {
            required: true,
            trigger: 'blur',
            message: this.$i18n.t('m.Password_Again_Check_Required'),
          },
          { validator: CheckAgainPassword, trigger: 'blur' },
        ],
      },
      ruleEmail: {
        password: oldPasswordCheck,
        newEmail: [
          {
            required: true,
            message: this.$i18n.t('m.Email_Check_Required'),
            trigger: 'blur',
          },
          {
            type: 'email',
            trigger: 'change',
            message: this.$i18n.t('m.Email_Check_Format'),
          },
          { validator: CheckEmail, trigger: 'blur' },
        ],
        code: [
          {
            required: true,
            message: this.$i18n.t('m.Code_Check_Required'),
            trigger: 'blur',
          },
          {
            min: 6,
            max: 6,
            message: this.$i18n.t('m.Code_Check_Length'),
            trigger: 'blur',
          },
        ],
      },
      ruleMobile: {
        password: oldPasswordCheck,
        newMobile: [
          {
            required: true,
            message: this.$i18n.t('m.Mobile_Check_Required'),
            trigger: 'blur',
          },
          {
            trigger: 'change',
            message: this.$i18n.t('m.Mobile_Check_Format'),
            validator: IsMobile,
          },
          { validator: CheckMobile, trigger: 'blur' },
        ],
        code: [
          {
            required: true,
            message: this.$i18n.t('m.Code_Check_Required'),
            trigger: 'blur',
          },
          {
            min: 6,
            max: 6,
            message: this.$i18n.t('m.Code_Check_Length'),
            trigger: 'blur',
          },
        ],
      },
    };
  },
  mounted() {
    this.formEmail.oldEmail = this.$store.getters.userInfo.email || '';
    this.formMobile.oldMobile = this.$store.getters.userInfo.mobile || '';
  },
  methods: {
    ...mapActions([
      'startTimeOut',
      'changeEmailTimeOut',
      'changeMobileTimeOut',
    ]),
    countDownEmail() {
      let i = this.timeEmail;
      if (i == 0) {
        this.btnEmailLoading = false;
        return;
      }
      this.emailCountdownNum = i;
      setTimeout(() => {
        this.countDownEmail();
      }, 1000);
    },
    countDownMobile() {
      let i = this.timeMobile;
      if (i == 0) {
        this.btnMobileLoading = false;
        return;
      }
      this.mobileCountdownNum = i;
      setTimeout(() => {
        this.countDownMobile();
      }, 1000);
    },
    changePassword(times) {
      this.verify.passwordSuccess = true;
      let time = (times / 1000).toFixed(1);
      this.verify.passwordMsg = 'Total time ' + time + 's';
      setTimeout(() => {
        this.visible.passwordSlideBlock = false;
        this.verify.passwordSuccess = false;
        // 无论后续成不成功，验证码滑动都要刷新
        this.$refs.passwordSlideBlock.reset();
      }, 1000);

      this.$refs['formPassword'].validate((valid) => {
        if (valid) {
          this.loading.btnPassword = true;
          let data = Object.assign({}, this.formPassword);
          delete data.againPassword;
          api.changePassword(data).then(
            (res) => {
              this.loading.btnPassword = false;
              if (res.data.data.code == 200) {
                this.$msg.success(this.$i18n.t('m.Update_Successfully'));
                this.visible.passwordAlert = {
                  show: true,
                  title: this.$i18n.t('m.Update_Successfully'),
                  type: 'success',
                  description: res.data.data.msg,
                };
                setTimeout(() => {
                  this.visible.passwordAlert = false;
                  this.$router.push({ name: 'Logout' });
                }, 5000);
              } else {
                this.$msg.error(res.data.msg);
                this.visible.passwordAlert = {
                  show: true,
                  title: this.$i18n.t('m.Update_Failed'),
                  type: 'warning',
                  description: res.data.data.msg,
                };
                if (res.data.data.code == 403) {
                  this.visible.passwordAlert.type = 'error';
                  this.disabled.btnPassword = true;
                }
              }
            },
            (err) => {
              this.loading.btnPassword = false;
            }
          );
        }
      });
    },
    sendChangeEmail() {
      var emailReg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      if (!emailReg.test(this.formEmail.newEmail)) {
        this.$msg.error(this.$i18n.t('m.Email_Check_Format'));
        return;
      }
      this.btnEmailLoading = true;
      this.emailCountdownNum = 'Waiting...';
      if (this.formEmail.newEmail) {
        this.$msg.info(this.$i18n.t('m.The_system_is_processing'));
        api.getChangeEmail(this.formEmail.newEmail).then(
          (res) => {
            if (res.data.msg != null) {
              this.$msg.success(this.$i18n.t('m.Change_Send_Email_Msg'),10000);
              this.countDownEmail();
              this.startTimeOut({ name: 'emailTimeOut' });
            }
          },
          (res) => {
            this.btnEmailLoading = false;
            this.emailCountdownNum = null;
          }
        );
      }
    },
    sendChangeMobile() {
      var mobileReg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
      if (!mobileReg.test(this.formMobile.newMobile)) {
        this.$msg.error(this.$i18n.t('m.Mobile_Check_Format'));
        return;
      }
      this.btnMobileLoading = true;
      this.mobileCountdownNum = 'Waiting...';
      if (this.formMobile.newMobile) {
        this.$msg.info(this.$i18n.t('m.The_system_is_processing'));
        api.getChangeMobile(this.formMobile.newMobile).then(
          (res) => {
            if (res.data.msg != null) {
              this.$msg.success(this.$i18n.t('m.Change_Send_Mobile_Msg'),10000);
              this.countDownMobile();
              this.startTimeOut({ name: 'mobileTimeOut' });
            }
          },
          (res) => {
            this.btnMobileLoading = false;
            this.mobileCountdownNum = null;
          }
        );
      }
    },
    changeEmail(times) {
      this.verify.emailSuccess = true;
      let time = (times / 1000).toFixed(1);
      this.verify.emailMsg = 'Total time ' + time + 's';
      setTimeout(() => {
        this.visible.emailSlideBlock = false;
        this.verify.emailSuccess = false;
        // 无论后续成不成功，验证码滑动都要刷新
        this.$refs.emailSlideBlock.reset();
      }, 1000);
      this.$refs['formEmail'].validate((valid) => {
        if (valid) {
          this.loading.btnEmail = true;
          let data = Object.assign({}, this.formEmail);
          api.changeEmail(data).then(
            (res) => {
              this.loading.btnEmail = false;
              if (res.data.data.code == 200) {
                this.$msg.success(this.$i18n.t('m.Update_Successfully'));
                this.visible.emailAlert = {
                  show: true,
                  title: this.$i18n.t('m.Update_Successfully'),
                  type: 'success',
                  description: res.data.data.msg,
                };
                // 更新本地缓存
                this.$store.dispatch('setUserInfo', res.data.data.userInfo);
                this.$refs['formEmail'].resetFields();
                this.formEmail.oldEmail = res.data.data.userInfo.email;
              } else {
                this.$msg.error(res.data.msg);
                this.visible.emailAlert = {
                  show: true,
                  title: this.$i18n.t('m.Update_Failed'),
                  type: 'warning',
                  description: res.data.data.msg,
                };
                if (res.data.data.code == 403) {
                  this.visible.emailAlert.type = 'error';
                  this.disabled.btnEmail = true;
                }
              }
            },
            (err) => {
              this.loading.btnEmail = false;
            }
          );
        }
      });
    },
    changeMobile(times) {
      this.verify.mobileSuccess = true;
      let time = (times / 1000).toFixed(1);
      this.verify.mobileMsg = 'Total time ' + time + 's';
      setTimeout(() => {
        this.visible.mobileSlideBlock = false;
        this.verify.mobileSuccess = false;
        // 无论后续成不成功，验证码滑动都要刷新
        this.$refs.mobileSlideBlock.reset();
      }, 1000);
      this.$refs['formMobile'].validate((valid) => {
        if (valid) {
          this.loading.btnMobile = true;
          let data = Object.assign({}, this.formMobile);
          api.changeMobile(data).then(
            (res) => {
              this.loading.btnMobile = false;
              if (res.data.data.code == 200) {
                this.$msg.success(this.$i18n.t('m.Update_Successfully'));
                this.visible.mobileAlert = {
                  show: true,
                  title: this.$i18n.t('m.Update_Successfully'),
                  type: 'success',
                  description: res.data.data.msg,
                };
                // 更新本地缓存
                this.$store.dispatch('setUserInfo', res.data.data.userInfo);
                this.$refs['formMobile'].resetFields();
                this.formMobile.oldMobile = res.data.data.userInfo.mobile;
              } else {
                this.$msg.error(res.data.msg);
                this.visible.mobileAlert = {
                  show: true,
                  title: this.$i18n.t('m.Update_Failed'),
                  type: 'warning',
                  description: res.data.data.msg,
                };
                if (res.data.data.code == 403) {
                  this.visible.mobileAlert.type = 'error';
                  this.disabled.btnMobile = true;
                }
              }
            },
            (err) => {
              this.loading.btnMobile = false;
            }
          );
        }
      });
    },
    onAgain(type) {
      if ((type = 'password')) {
        this.$refs.passwordSlideBlock.reset();
      } else if ((type = 'email')) {
        this.$refs.emailSlideBlock.reset();
      } else {
        this.$refs.mobileSlideBlock.reset();
      }
      this.$msg.warning(this.$i18n.t('m.Guess_robot'));
    },
  },
  computed: {
    ...mapGetters(['emailTimeOut', 'mobileTimeOut']),
    timeEmail: {
      get() {
        return this.emailTimeOut;
      },
      set(value) {
        this.changeEmailTimeOut({ timeEmail: value });
      },
    },
    timeMobile: {
      get() {
        return this.mobileTimeOut;
      },
      set(value) {
        this.changeMobileTimeOut({ timeMobile: value });
      },
    },
  },
  created() {
    if (this.timeEmail != 90 && this.timeEmail != 0) {
      this.btnEmailLoading = true;
      this.countDownEmail();
    }
    if (this.timeMobile != 90 && this.timeMobile != 0) {
      this.btnMobileLoading = true;
      this.countDownMobile();
    }
  },
};
</script>

<style scoped>
.section-title {
  font-size: 21px;
  font-weight: 500;
  padding-top: 10px;
  padding-bottom: 20px;
  line-height: 30px;
}
.left {
  text-align: center;
}
.right {
  text-align: center;
}
.footer {
  overflow: auto;
  margin-top: 20px;
  margin-bottom: -15px;
  text-align: center;
}
/deep/ .el-input-group__append {
  color: #fff;
  background: #25bb9b;
}
/deep/.footer .el-button--primary {
  margin: 0 0 15px 0;
  width: 100%;
}
/deep/ .el-form-item__content {
  margin-left: 0px !important;
}
.separator {
  display: block;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  border: 1px dashed #eee;
}
</style>
