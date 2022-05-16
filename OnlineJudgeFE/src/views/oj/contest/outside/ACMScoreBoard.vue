<template>
  <el-row>
    <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
      <el-card shadow v-loading="loading.info">
        <div class="contest-title">
          <div slot="header">
            <span class="panel-title">{{ contest.title }}</span>
          </div>
        </div>
        <el-row style="margin-top: 10px">
          <el-col :span="12" class="text-align:left">
            <el-tooltip
              v-if="contest.auth != null && contest.auth != undefined"
              :content="$t('m.' + CONTEST_TYPE_REVERSE[contest.auth]['tips'])"
              placement="top"
            >
              <el-tag
                :type.sync="CONTEST_TYPE_REVERSE[contest.auth]['color']"
                effect="plain"
              >
                {{ $t("m." + CONTEST_TYPE_REVERSE[contest.auth]["name"]) }}
              </el-tag>
            </el-tooltip>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-button size="small" plain v-if="contest.count">
              <i class="el-icon-user-solid" style="color: rgb(48, 145, 242)"></i
              >x{{ contest.count }}
            </el-button>
            <el-button
              size="small"
              v-if="contest != null"
              :type="contest.type == 0 ? 'primary' : 'warning'"
            >
              <i class="fa fa-trophy"></i>
              {{ contest.type | parseContestType }}
            </el-button>
          </el-col>
        </el-row>
        <div class="contest-time">
          <el-row>
            <el-col :xs="24" :md="12" class="left">
              <p>
                <i class="fa fa-hourglass-start" aria-hidden="true"></i>
                {{ $t("m.StartAt") }}：{{ contest.startTime | localtime }}
              </p>
            </el-col>
            <el-col :xs="24" :md="12" class="right">
              <p>
                <i class="fa fa-hourglass-end" aria-hidden="true"></i>
                {{ $t("m.EndAt") }}：{{ contest.endTime | localtime }}
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
          <el-col :span="24" style="text-align: center">
            <el-tag effect="dark" size="medium" :style="countdownColor">
              <i class="fa fa-circle" aria-hidden="true"></i>
              {{ countdown }}
            </el-tag>
          </el-col>
        </el-row>
      </el-card>
    </el-col>
    <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
      <el-card shadow v-loading="loading.rank">
        <div class="contest-rank-switch">
          <span style="float: right">
            <span>{{ $t("m.Auto_Refresh") }}(30s)</span>
            <el-switch
              :disabled="contestEnded"
              @change="handleAutoRefresh"
              v-model="autoRefresh"
            ></el-switch>
          </span>
          <span style="float: right" v-if="isContestAdmin">
            <span>{{ $t("m.Force_Update") }}</span>
            <el-switch
              v-model="forceUpdate"
              @change="getContestOutsideScoreboard"
            ></el-switch>
          </span>
          <span style="float: right">
            <span>{{ $t("m.Star_User") }}</span>
            <el-switch
              v-model="showStarUser"
              @change="getContestOutsideScoreboard"
            ></el-switch>
          </span>
        </div>
        <vxe-table
          round
          border
          auto-resize
          size="medium"
          align="center"
          :data="dataRank"
          :cell-class-name="cellClassName"
        >
          <vxe-table-column
            field="rank"
            width="50"
            fixed="left"
            :title="$t('m.Contest_Rank_Seq')"
          >
            <template v-slot="{ row }">
              {{ row.rank == -1 ? "*" : row.rank }}
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="username"
            fixed="left"
            v-if="!isMobileView"
            min-width="280"
            :title="$t('m.User')"
            header-align="center"
            align="left"
          >
            <template v-slot="{ row }">
              <avatar
                v-if="row[contest.rankShowName]"
                :username="row[contest.rankShowName]"
                :inline="true"
                :size="37"
                color="#FFF"
                :src="row.avatar"
                :title="row[contest.rankShowName]"
              ></avatar>
              <el-tooltip placement="top">
                <div slot="content">
                  {{
                    row.isConcerned ? $t("m.Unfollow") : $t("m.Top_And_Follow")
                  }}
                </div>
                <span
                  class="contest-rank-concerned"
                  @click="updateConcernedList(row.uid, !row.isConcerned)"
                >
                  <svg
                    v-if="row.isConcerned"
                    xmlns="http://www.w3.org/2000/svg"
                    xmlns:xlink="http://www.w3.org/1999/xlink"
                    viewBox="0 0 512 512"
                    width="16"
                    height="16"
                  >
                    <path
                      d="M462.3 62.6C407.5 15.9 326 24.3 275.7 76.2L256 96.5l-19.7-20.3C186.1 24.3 104.5 15.9 49.7 62.6c-62.8 53.6-66.1 149.8-9.9 207.9l193.5 199.8c12.5 12.9 32.8 12.9 45.3 0l193.5-199.8c56.3-58.1 53-154.3-9.8-207.9z"
                      fill="red"
                    ></path>
                  </svg>
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    xmlns:xlink="http://www.w3.org/1999/xlink"
                    viewBox="0 0 512 512"
                    width="16"
                    height="16"
                  >
                    <path
                      d="M458.4 64.3C400.6 15.7 311.3 23 256 79.3C200.7 23 111.4 15.6 53.6 64.3C-21.6 127.6-10.6 230.8 43 285.5l175.4 178.7c10 10.2 23.4 15.9 37.6 15.9c14.3 0 27.6-5.6 37.6-15.8L469 285.6c53.5-54.7 64.7-157.9-10.6-221.3zm-23.6 187.5L259.4 430.5c-2.4 2.4-4.4 2.4-6.8 0L77.2 251.8c-36.5-37.2-43.9-107.6 7.3-150.7c38.9-32.7 98.9-27.8 136.5 10.5l35 35.7l35-35.7c37.8-38.5 97.8-43.2 136.5-10.6c51.1 43.1 43.5 113.9 7.3 150.8z"
                      fill="currentColor"
                    ></path>
                  </svg>
                </span>
              </el-tooltip>
              <span style="float: right; text-align: right">
                <a @click="getUserHomeByUsername(row.uid, row.username)">
                  <span class="contest-username"
                    ><span class="contest-rank-flag" v-if="row.rank == -1"
                      >Star</span
                    >
                    <span
                      class="contest-rank-flag"
                      v-if="row.gender == 'female'"
                      >Girl</span
                    >
                    {{ row[contest.rankShowName] }}</span
                  >
                  <span class="contest-school" v-if="row.school">{{
                    row.school
                  }}</span>
                </a>
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="username"
            v-else
            min-width="280"
            :title="$t('m.User')"
            header-align="center"
            align="left"
          >
            <template v-slot="{ row }">
              <avatar
                v-if="row[contest.rankShowName]"
                :username="row[contest.rankShowName]"
                :inline="true"
                :size="37"
                color="#FFF"
                :src="row.avatar"
                :title="row[contest.rankShowName]"
              ></avatar>
              <el-tooltip placement="top">
                <div slot="content">
                  {{
                    row.isConcerned ? $t("m.Unfollow") : $t("m.Top_And_Follow")
                  }}
                </div>
                <span
                  class="contest-rank-concerned"
                  @click="updateConcernedList(row.uid, !row.isConcerned)"
                >
                  <svg
                    v-if="row.isConcerned"
                    xmlns="http://www.w3.org/2000/svg"
                    xmlns:xlink="http://www.w3.org/1999/xlink"
                    viewBox="0 0 512 512"
                    width="16"
                    height="16"
                  >
                    <path
                      d="M462.3 62.6C407.5 15.9 326 24.3 275.7 76.2L256 96.5l-19.7-20.3C186.1 24.3 104.5 15.9 49.7 62.6c-62.8 53.6-66.1 149.8-9.9 207.9l193.5 199.8c12.5 12.9 32.8 12.9 45.3 0l193.5-199.8c56.3-58.1 53-154.3-9.8-207.9z"
                      fill="red"
                    ></path>
                  </svg>
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    xmlns:xlink="http://www.w3.org/1999/xlink"
                    viewBox="0 0 512 512"
                    width="16"
                    height="16"
                  >
                    <path
                      d="M458.4 64.3C400.6 15.7 311.3 23 256 79.3C200.7 23 111.4 15.6 53.6 64.3C-21.6 127.6-10.6 230.8 43 285.5l175.4 178.7c10 10.2 23.4 15.9 37.6 15.9c14.3 0 27.6-5.6 37.6-15.8L469 285.6c53.5-54.7 64.7-157.9-10.6-221.3zm-23.6 187.5L259.4 430.5c-2.4 2.4-4.4 2.4-6.8 0L77.2 251.8c-36.5-37.2-43.9-107.6 7.3-150.7c38.9-32.7 98.9-27.8 136.5 10.5l35 35.7l35-35.7c37.8-38.5 97.8-43.2 136.5-10.6c51.1 43.1 43.5 113.9 7.3 150.8z"
                      fill="currentColor"
                    ></path>
                  </svg>
                </span>
              </el-tooltip>
              <span style="float: right; text-align: right">
                <a @click="getUserHomeByUsername(row.uid, row.username)">
                  <span class="contest-username"
                    ><span class="contest-rank-flag" v-if="row.rank == -1"
                      >Star</span
                    >
                    <span
                      class="contest-rank-flag"
                      v-if="row.gender == 'female'"
                      >Girl</span
                    >
                    {{ row[contest.rankShowName] }}</span
                  >
                  <span class="contest-school" v-if="row.school">{{
                    row.school
                  }}</span>
                </a>
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="rating" :title="$t('m.AC')" width="50">
            <template v-slot="{ row }">
              <span
                style="
                  color: rgb(87, 163, 243);
                  font-weight: 600;
                  font-size: 14px;
                "
                >{{ row.ac }}
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="totalTime"
            :title="$t('m.TotalTime')"
            width="70"
          >
            <template v-slot="{ row }">
              <el-tooltip effect="dark" placement="top">
                <div slot="content">
                  {{ parseTimeToSpecific(row.totalTime) }}
                </div>
                <span style="font-size: 14px">{{
                  parseInt(row.totalTime / 60)
                }}</span>
              </el-tooltip>
            </template>
          </vxe-table-column>
          <vxe-table-column
            width="64"
            v-for="problem in contestProblems"
            :key="problem.displayId"
          >
            <template v-slot:header>
              <el-tooltip effect="dark" placement="top">
                <div slot="content">
                  {{ problem.displayId + ". " + problem.displayTitle }}
                  <br />
                  {{ "Accepted: " + problem.ac }}
                  <br />
                  {{ "Rejected: " + problem.error }}
                </div>
                <div>
                  <span style="vertical-align: middle" v-if="problem.color">
                    <svg
                      t="1633685184463"
                      class="icon"
                      viewBox="0 0 1088 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="5840"
                      width="25"
                      height="25"
                    >
                      <path
                        d="M575.872 849.408c-104.576 0-117.632-26.56-119.232-31.808-6.528-22.528 32.896-70.592 63.744-96.768l-1.728-2.624c137.6-42.688 243.648-290.112 243.648-433.472A284.544 284.544 0 0 0 478.016 0a284.544 284.544 0 0 0-284.288 284.736c0 150.4 116.352 415.104 263.744 438.336-25.152 29.568-50.368 70.784-39.104 108.928 12.608 43.136 62.72 63.232 157.632 63.232 7.872 0 11.52 9.408 4.352 19.52-21.248 29.248-77.888 63.424-167.68 63.424V1024c138.944 0 215.936-74.816 215.936-126.528a46.72 46.72 0 0 0-16.32-36.608 56.32 56.32 0 0 0-36.416-11.456zM297.152 297.472c0 44.032-38.144 25.344-38.144-38.656 0-108.032 85.248-195.712 190.592-195.712 62.592 0 81.216 39.232 38.08 39.232-105.152 0.064-190.528 87.04-190.528 195.136z"
                        :fill="problem.color"
                        p-id="5841"
                      ></path>
                    </svg>
                  </span>
                  <span class="emphasis" style="color: #495060"
                    >{{ problem.displayId }}
                    <br />
                    <span>{{ problem.ac }}</span>
                  </span>
                </div>
              </el-tooltip>
            </template>
            <template v-slot="{ row }">
              <span v-if="row.submissionInfo[problem.displayId]">
                <el-tooltip effect="dark" placement="top">
                  <div slot="content">
                    {{ row.submissionInfo[problem.displayId].specificTime }}
                  </div>
                  <span
                    v-if="row.submissionInfo[problem.displayId].isAC"
                    class="submission-time"
                    >{{ row.submissionInfo[problem.displayId].ACTime }}<br />
                  </span>
                </el-tooltip>

                <span
                  class="submission-error"
                  v-if="
                    row.submissionInfo[problem.displayId].tryNum == null &&
                    row.submissionInfo[problem.displayId].errorNum != 0
                  "
                >
                  {{
                    row.submissionInfo[problem.displayId].errorNum > 1
                      ? row.submissionInfo[problem.displayId].errorNum +
                        " tries"
                      : row.submissionInfo[problem.displayId].errorNum + " try"
                  }}
                </span>
                <span
                  v-if="row.submissionInfo[problem.displayId].tryNum != null"
                  ><template
                    v-if="row.submissionInfo[problem.displayId].errorNum > 0"
                  >
                    {{
                      row.submissionInfo[problem.displayId].errorNum
                    }}+</template
                  >{{ row.submissionInfo[problem.displayId].tryNum
                  }}{{
                    row.submissionInfo[problem.displayId].errorNum +
                      row.submissionInfo[problem.displayId].tryNum >
                    1
                      ? " tries"
                      : " try"
                  }}
                </span>
              </span>
            </template>
          </vxe-table-column>
        </vxe-table>
      </el-card>
    </el-col>
  </el-row>
</template>
<script>
import Avatar from "vue-avatar";
import time from "@/common/time";
import { mapActions } from "vuex";
import ScoreBoardMixin from "./scoreBoardMixin";
export default {
  name: "ACMScoreBoard",
  mixins: [ScoreBoardMixin],
  components: {
    Avatar,
  },
  data() {
    return {
      autoRefresh: false,
      removeStar: false,
      loading: {
        info: false,
        rank: false,
      },
      contestID: "",
      dataRank: [],
      timer: null,
      CONTEST_STATUS: {},
      CONTEST_STATUS_REVERSE: {},
      CONTEST_TYPE_REVERSE: {},
      RULE_TYPE: {},
    };
  },
  created() {
    this.init();
  },
  mounted() {
    this.getContestOutsideScoreboard();
  },
  methods: {
    ...mapActions(["getContestProblems"]),
    getUserHomeByUsername(uid, username) {
      this.$router.push({
        name: "UserHome",
        query: { username: username, uid: uid },
      });
    },
    cellClassName({ row, rowIndex, column, columnIndex }) {
      if (column.property === "username" && row.userCellClassName) {
        return row.userCellClassName;
      }
      if (
        column.property !== "rank" &&
        column.property !== "rating" &&
        column.property !== "totalTime" &&
        column.property !== "username"
      ) {
        return row.cellClassName[
          [this.contestProblems[columnIndex - 4].displayId]
        ];
      } else {
        if (row.isConcerned && column.property !== "username") {
          return "bg-concerned";
        }
      }
    },
    applyToTable(dataRank) {
      dataRank.forEach((rank, i) => {
        let info = rank.submissionInfo;
        let cellClass = {};
        if (this.concernedList.indexOf(rank.uid) != -1) {
          dataRank[i].isConcerned = true;
        }
        Object.keys(info).forEach((problemID) => {
          dataRank[i][problemID] = info[problemID];
          if (dataRank[i][problemID].ACTime != null) {
            dataRank[i][problemID].errorNum += 1;
            dataRank[i][problemID].specificTime = this.parseTimeToSpecific(
              dataRank[i][problemID].ACTime
            );
            dataRank[i][problemID].ACTime = parseInt(
              dataRank[i][problemID].ACTime / 60
            );
          }
          let status = info[problemID];
          if (status.isFirstAC) {
            cellClass[problemID] = "first-ac";
          } else if (status.isAC) {
            cellClass[problemID] = "ac";
          } else if (status.tryNum != null && status.tryNum > 0) {
            cellClass[problemID] = "try";
          } else if (status.errorNum != 0) {
            cellClass[problemID] = "wa";
          }
        });
        dataRank[i].cellClassName = cellClass;
        if (dataRank[i].rank == -1) {
          dataRank[i].userCellClassName = "bg-star";
        }
        if (dataRank[i].gender == "female") {
          dataRank[i].userCellClassName = "bg-female";
        }
      });
      this.dataRank = dataRank;
    },
    parseTimeToSpecific(totalTime) {
      return time.secondFormat(totalTime);
    },
  },
  computed: {
    isMobileView() {
      return window.screen.width < 768;
    },
  },
};
</script>
<style scoped>
@media screen and (min-width: 1050px) {
  .scoreboard-body {
    margin-left: -2%;
    margin-right: -2%;
  }
}
.contest-title {
  text-align: center;
}
.panel-title {
  font-size: 1.5rem !important;
  font-weight: 500;
}
.contest-time {
  width: 100%;
  font-size: 16px;
}
@media screen and (min-width: 768px) {
  .contest-time .left {
    text-align: left;
  }
  .contest-time .right {
    text-align: right;
  }
}
@media screen and (max-width: 768px) {
  .contest-time .left,
  .contest-time .right {
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

.el-tag--dark {
  border-color: #fff;
}
.el-tag {
  color: rgb(25, 190, 107);
  background: #fff;
  border: 1px solid #e9eaec;
  font-size: 18px;
}
/deep/.vxe-table .vxe-header--column:not(.col--ellipsis) {
  padding: 4px 0 !important;
}
/deep/.vxe-table .vxe-body--column {
  padding: 4px 0 !important;
  line-height: 20px !important;
}
/deep/.vxe-table .vxe-body--column:not(.col--ellipsis) {
  line-height: 20px !important;
  padding: 0 !important;
}
/deep/.el-card__body {
  padding: 15px !important;
  padding-top: 20px !important;
}

.vxe-cell p,
.vxe-cell span {
  margin: 0;
  padding: 0;
}
/deep/.vxe-body--column {
  min-width: 0;
  height: 48px;
  box-sizing: border-box;
  text-align: left;
  text-overflow: ellipsis;
  vertical-align: middle;
}
/deep/.vxe-table .vxe-cell {
  padding-left: 5px !important;
  padding-right: 5px !important;
}
.submission-time {
  font-size: 15.6px;
  font-family: Roboto, sans-serif;
}
.submission-error {
  font-weight: 400;
}
</style>
