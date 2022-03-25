<template>
  <div id="app">
    <el-backtop :right="10"></el-backtop>
    <div v-if="!isAdminView">
      <nav-bar></nav-bar>
      <div id="oj-content">
        <transition name="el-fade-in-linear">
          <router-view></router-view>
        </transition>
      </div>
      <footer>
        <div class="mundb-footer">
          <el-row>
            <el-col :md="6" :sm="12" :xs="24">
              <h1>{{ toUpper(websiteConfig.shortName) }}</h1>
              <span
                style="line-height:25px"
                v-html="websiteConfig.description"
                v-katex
                v-highlight
              >
              </span>
            </el-col>
            <el-col class="hr-none">
              <el-divider></el-divider>
            </el-col>
            <el-col :md="6" :sm="12" :xs="24">
              <h1>{{ $t('m.Service') }}</h1>
              <p>
                <a @click="goRoute('/status')">{{ $t('m.Judging_Queue') }}</a>
              </p>
              <p>
                <a @click="goRoute('/developer')">{{ $t('m.System_Info') }}</a>
              </p>
            </el-col>
            <el-col class="hr-none">
              <el-divider></el-divider>
            </el-col>
            <el-col :md="6" :sm="12" :xs="24">
              <h1>{{ $t('m.Development') }}</h1>
              <p class="mb-1">
                <a :href="websiteConfig.projectUrl" target="_blank">{{
                  $t('m.Open_Source')
                }}</a>
              </p>
              <p class="mb-1"><a @click="goRoute('/#')">API</a></p>
            </el-col>
            <el-col class="hr-none">
              <el-divider></el-divider>
            </el-col>
            <el-col :md="6" :sm="12" :xs="24">
              <h1>{{ $t('m.Support') }}</h1>
              <p>
                <i class="fa fa-info-circle" aria-hidden="true"></i
                ><a @click="goRoute('/introduction')"> {{ $t('m.Help') }}</a>
              </p>
              <p>
                <i class="fa fa-qq" aria-hidden="true"></i>
                {{ $t('m.QQ') }} 1078596795
              </p>
            </el-col>
          </el-row>
        </div>
        <div class="mundb-footer">
          <a
            style="color:#1E9FFF"
            :href="websiteConfig.recordUrl"
            target="_blank"
            >{{ websiteConfig.recordName }}</a
          >
          Powered by
          <a
            :href="websiteConfig.projectUrl"
            style="color:#1E9FFF"
            target="_blank"
            >{{ websiteConfig.projectName }}</a
          >
          <span style="margin-left:10px">
            <el-dropdown @command="changeLanguage" placement="top">
              <span class="el-dropdown-link">
                <i class="fa fa-globe" aria-hidden="true">
                  {{ this.webLanguage == 'zh-CN' ? '简体中文' : 'English' }}</i
                ><i class="el-icon-arrow-up el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="zh-CN">简体中文</el-dropdown-item>
                <el-dropdown-item command="en-US">English</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </span>
        </div>
      </footer>
    </div>
    <div v-else>
      <div id="admin-content">
        <transition name="el-fade-in-linear">
          <router-view></router-view>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from '@/components/oj/common/NavBar';
import { mapActions, mapState, mapGetters } from 'vuex';
import { LOGO, MOTTO } from '@/common/logo';
export default {
  name: 'app-content',
  components: {
    NavBar,
  },
  data() {
    return {
      isAdminView: false,
    };
  },
  methods: {
    ...mapActions(['changeDomTitle', 'getWebsiteConfig']),
    goRoute(path) {
      this.$router.push({
        path: path,
      });
    },
    toUpper(str) {
      if (str) {
        return str.toUpperCase();
      }
    },
    changeLanguage(language) {
      this.$store.commit('changeWebLanguage', { language: language });
    },
  },
  watch: {
    $route(newVal, oldVal) {
      this.changeDomTitle();
      if (newVal !== oldVal && newVal.path.split('/')[1] == 'admin') {
        this.isAdminView = true;
      } else {
        this.isAdminView = false;
      }
    },
    websiteConfig() {
      this.changeDomTitle();
    },
  },
  computed: {
    ...mapState(['websiteConfig']),
    ...mapGetters(['webLanguage']),
  },
  created: function() {
    this.$nextTick(function() {
      try {
        document.body.removeChild(document.getElementById('app-loader'));
      } catch (e) {}
    });

    if (this.$route.path.split('/')[1] != 'admin') {
      this.isAdminView = false;
    } else {
      this.isAdminView = true;
    }
  },
  mounted() {
    console.log(LOGO);
    console.log(MOTTO);
    this.getWebsiteConfig();
  },
};
</script>

<style>
* {
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}
body {
  background-color: #eff3f5 !important;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif !important;
  color: #495060 !important;
  font-size: 12px !important;
}
code,
kbd,
pre,
samp {
  font-family: Consolas, Menlo, Courier, monospace;
}
::-webkit-scrollbar {
  width: 10px;
  height: 12px;
  background-color: #fff;
}

::-webkit-scrollbar-thumb {
  display: block;
  min-height: 12px;
  min-width: 10px;
  border-radius: 6px;
  background-color: rgb(217, 217, 217);
}

::-webkit-scrollbar-thumb:hover {
  display: block;
  min-height: 12px;
  min-width: 10px;
  border-radius: 6px;
  background-color: rgb(159, 159, 159);
}

#admin-content {
  background-color: #1e9fff;
  position: absolute;
  top: 0;
  bottom: 0;
  width: 100%;
}
.mobile-menu-active {
  background-color: rgba(0, 0, 0, 0.1);
}
.mobile-menu-active .mu-item-title {
  color: #2d8cf0 !important;
}
.mobile-menu-active .mu-icon {
  color: #2d8cf0 !important;
}
#particles-js {
  position: fixed;
  z-index: 0;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
a {
  text-decoration: none;
  background-color: transparent;
  color: #495060;
  outline: 0;
  cursor: pointer;
  transition: color 0.2s ease;
}
a:hover {
  color: #2196f3 !important;
}

.panel-title {
  font-size: 21px;
  font-weight: 500;
  padding-top: 10px;
  padding-bottom: 20px;
  line-height: 30px;
}

.home-title {
  color: #409eff;
  font-family: 'Raleway';
}

.vxe-table {
  color: #000 !important;
  font-size: 12px !important;
  font-weight: 500 !important;
}
.row--hover {
  cursor: pointer;
  background-color: #ebf7ff !important;
}
.vxe-table .vxe-body--column:not(.col--ellipsis),
.vxe-table .vxe-footer--column:not(.col--ellipsis),
.vxe-table .vxe-header--column:not(.col--ellipsis) {
  padding: 9px 0 !important;
}
#nprogress .bar {
  background: #66b1ff !important;
}
@media screen and (min-width: 1050px) {
  #oj-content {
    margin-top: 70px;
    padding: 0 3%;
  }
}
.markdown-body img {
  max-width: 100%;
}
.contest-description img {
  max-width: 100%;
}
@media screen and (max-width: 1050px) {
  #oj-content {
    margin-top: 70px;
    padding: 0 5px;
  }
  .el-message-box {
    width: 70% !important;
  }
}
#problem-content .sample pre {
  -ms-flex: 1 1 auto;
  flex: 1 1 auto;
  -ms-flex-item-align: stretch;
  align-self: stretch;
  border-style: solid;
  background: #fafafa;
  border-left: 2px solid #3498db;
}

.el-menu--popup {
  min-width: 120px !important;
  text-align: center;
}
.panel-options {
  margin-top: 10px;
  text-align: center;
}
.el-tag--dark {
  border-color: #fff !important;
}
.v-note-wrapper .v-note-panel {
  height: 460px !important;
}
footer {
  margin-top: 10px;
  color: #555 !important;
  background-color: #fff;
  text-align: center;
}
footer a {
  color: #555;
}
footer a:hover {
  color: #409eff;
  text-decoration: none;
}

footer h1 {
  font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC,
    Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial,
    sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;
  font-weight: 300;
  color: #3d3d3d;
  line-height: 1.1;
  font-size: 1.5rem;
}

.mundb-footer {
  padding: 1rem 2.5rem;
  width: 100%;
  font-weight: 400;
  font-size: 1rem;
  line-height: 1;
}
@media (min-width: 768px) {
  .hr-none {
    display: none !important;
  }
}

.el-empty {
  max-width: 256px;
  margin: 0 auto;
}

.el-empty__description {
  text-align: center;
  color: #3498db;
  font-size: 13px;
}
</style>
