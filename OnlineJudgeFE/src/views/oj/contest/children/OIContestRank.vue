<template>
  <el-row>
    <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
      <el-card shadow>
        <div slot="header">
          <span class="panel-title"
            >{{ $t("m.Contest_Rank") }}（{{
              contest.oiRankScoreType == "Recent"
                ? $t("m.Based_on_The_Recent_Score_Submitted_Of_Each_Problem")
                : $t("m.Based_on_The_Highest_Score_Submitted_For_Each_Problem")
            }}）</span
          >
          <span style="float: right; font-size: 20px">
            <el-popover trigger="hover" placement="left-start">
              <i class="el-icon-s-tools" slot="reference"></i>
              <div id="switches">
                <p>
                  <span>{{ $t("m.Chart") }}</span>
                  <el-switch v-model="showChart"></el-switch>
                </p>
                <p>
                  <span>{{ $t("m.Table") }}</span>
                  <el-switch v-model="showTable"></el-switch>
                </p>
                <p>
                  <span>{{ $t("m.Star_User") }}</span>
                  <el-switch
                    v-model="showStarUser"
                    @change="getContestRankData(page)"
                  ></el-switch>
                </p>
                <p>
                  <span>{{ $t("m.Auto_Refresh") }}(10s)</span>
                  <el-switch
                    :disabled="refreshDisabled"
                    @change="handleAutoRefresh"
                    v-model="autoRefresh"
                  ></el-switch>
                </p>
                <template v-if="isContestAdmin">
                  <p>
                    <span>{{ $t("m.Force_Update") }}</span>
                    <el-switch
                      v-model="forceUpdate"
                      @change="getContestRankData(page)"
                    ></el-switch>
                  </p>
                </template>
                <template v-if="isContestAdmin">
                  <el-button
                    type="primary"
                    size="small"
                    @click="downloadRankCSV"
                    >{{ $t("m.Download_as_CSV") }}</el-button
                  >
                </template>
              </div>
            </el-popover>
          </span>
        </div>
        <div v-show="showChart" class="echarts">
          <ECharts :options="options" ref="chart" :autoresize="true"></ECharts>
        </div>
        <div v-show="showTable">
          <vxe-table
            round
            border
            auto-resize
            size="medium"
            align="center"
            ref="OIContestRank"
            :data="dataRank"
            :cell-class-name="cellClassName"
            @cell-click="getUserProblemSubmission"
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
                      row.isConcerned
                        ? $t("m.Unfollow")
                        : $t("m.Top_And_Follow")
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
                    <span class="contest-username">
                      <span
                        class="contest-rank-flag"
                        v-if="row.uid == userInfo.uid"
                        >Me</span
                      >
                      <span class="contest-rank-flag" v-if="row.rank == -1"
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
              fixed="left"
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
                      row.isConcerned
                        ? $t("m.Unfollow")
                        : $t("m.Top_And_Follow")
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
                    <span class="contest-username">
                      <span
                        class="contest-rank-flag"
                        v-if="row.uid == userInfo.uid"
                        >Me</span
                      >
                      <span class="contest-rank-flag" v-if="row.rank == -1"
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
              field="realname"
              width="96"
              :title="$t('m.RealName')"
              v-if="isContestAdmin"
              show-overflow
            >
            </vxe-table-column>
            <vxe-table-column
              field="totalScore"
              :title="$t('m.Total_Score')"
              width="120"
            >
              <template v-slot="{ row }">
                <span style="font-size: 14px"
                  ><a
                    @click="getUserTotalSubmit(row.username)"
                    style="color: rgb(87, 163, 243)"
                    >{{ row.totalScore }}</a
                  >
                  <br />
                  <span class="problem-time">{{ row.totalTime }}ms</span>
                </span>
              </template>
            </vxe-table-column>
            <vxe-table-column
              width="64"
              v-for="problem in contestProblems"
              :key="problem.displayId"
              :field="problem.displayId"
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
                    <span>
                      <a
                        @click="getContestProblemById(problem.displayId)"
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
                  v-if="row.submissionInfo[problem.displayId]"
                  class="submission-hover"
                >
                  <span style="font-size: 14px">{{
                    row.submissionInfo[problem.displayId]
                  }}</span>
                  <br />
                  <span
                    v-if="
                      row.timeInfo && row.timeInfo[problem.displayId] != null
                    "
                    >{{ row.timeInfo[problem.displayId] }}ms</span
                  >
                </div>
              </template>
            </vxe-table-column>
          </vxe-table>
        </div>
        <Pagination
          :total="total"
          :page-size.sync="limit"
          :current.sync="page"
          @on-change="getContestRankData"
          @on-page-size-change="getContestRankData(1)"
          :layout="'prev, pager, next, sizes'"
        ></Pagination>
      </el-card>
    </el-col>
  </el-row>
</template>
<script>
import Avatar from "vue-avatar";
import { mapActions } from "vuex";
import ContestRankMixin from "./contestRankMixin";
import utils from "@/common/utils";
const Pagination = () => import("@/components/oj/common/Pagination");
export default {
  name: "OIContestRank",
  components: {
    Pagination,
    Avatar,
  },
  mixins: [ContestRankMixin],
  data() {
    return {
      total: 0,
      page: 1,
      limit: 30,
      contestID: "",
      dataRank: [],
      autoRefresh: false,
      options: {
        title: {
          text: this.$i18n.t("m.Top_10_Teams"),
          left: "center",
        },
        tooltip: {
          trigger: "axis",
        },
        toolbox: {
          show: true,
          feature: {
            dataView: { show: true, readOnly: true },
            magicType: { show: true, type: ["line", "bar"] },
            saveAsImage: { show: true, title: this.$i18n.t("m.save_as_image") },
          },
          right: "10%",
          top: "5%",
        },
        calculable: true,
        xAxis: [
          {
            type: "category",
            data: ["root"],
            boundaryGap: true,
            axisLabel: {
              interval: 0,
              showMinLabel: true,
              showMaxLabel: true,
              align: "center",
              formatter: (value, index) => {
                return utils.breakLongWords(value, 14);
              },
            },
            axisTick: {
              alignWithLabel: true,
            },
          },
        ],
        yAxis: [
          {
            type: "value",
          },
        ],
        grid: {
          left: "11%",
        },
        series: [
          {
            name: this.$i18n.t("m.Score"),
            type: "bar",
            barMaxWidth: "80",
            data: [0],
            markPoint: {
              data: [{ type: "max", name: "max" }],
            },
          },
        ],
      },
    };
  },
  created() {
    this.initConcernedList();
  },
  mounted() {
    this.contestID = this.$route.params.contestID;
    this.getContestRankData(1);
    if (!this.refreshDisabled) {
      this.autoRefresh = true;
      this.handleAutoRefresh(true);
    }
  },
  computed: {
    contest() {
      return this.$store.state.contest.contest;
    },
    isMobileView() {
      return window.screen.width < 768;
    },
  },
  methods: {
    ...mapActions(["getContestProblems"]),

    cellClassName({ row, rowIndex, column, columnIndex }) {
      if (row.username == this.userInfo.username) {
        if (
          column.property == "rank" ||
          column.property == "totalScore" ||
          column.property == "username" ||
          column.property == "realname"
        ) {
          return "own-submit-row";
        }
      }

      if (column.property === "username" && row.userCellClassName) {
        return row.userCellClassName;
      }

      if (
        column.property !== "rank" &&
        column.property !== "totalScore" &&
        column.property !== "username" &&
        column.property !== "realname"
      ) {
        if (this.isContestAdmin) {
          return row.cellClassName[
            [this.contestProblems[columnIndex - 4].displayId]
          ];
        } else {
          return row.cellClassName[
            [this.contestProblems[columnIndex - 3].displayId]
          ];
        }
      } else {
        if (row.isConcerned && column.property !== "username") {
          return "bg-concerned";
        }
      }
    },
    getUserTotalSubmit(username) {
      this.$router.push({
        name: "ContestSubmissionList",
        query: { username: username },
      });
    },
    getUserHomeByUsername(uid, username) {
      this.$router.push({
        name: "UserHome",
        query: { username: username, uid: uid },
      });
    },
    getContestProblemById(pid) {
      this.$router.push({
        name: "ContestProblemDetails",
        params: {
          contestID: this.contestID,
          problemID: pid,
        },
      });
    },
    getUserProblemSubmission({ row, column }) {
      if (
        column.property !== "rank" &&
        column.property !== "totalScore" &&
        column.property !== "username" &&
        column.property !== "realname" &&
        row[column.property]
      ) {
        this.$router.push({
          name: "ContestSubmissionList",
          query: { username: row.username, problemID: column.property },
        });
      }
    },
    applyToChart(rankData) {
      let [user, scores] = [[], []];
      let len = rankData.length;
      let topIndex = this.concernedList.length || 0;
      if (rankData.length > 0) {
        if (rankData[0].uid == this.userInfo.uid) {
          topIndex++;
        }
      }
      for (let i = topIndex; i < len && i < topIndex + 10; i++) {
        let ele = rankData[i];
        user.push(ele[this.contest.rankShowName]);
        scores.push(ele.totalScore);
      }
      this.options.xAxis[0].data = user;
      this.options.series[0].data = scores;
    },
    applyToTable(dataRank) {
      dataRank.forEach((rank, i) => {
        let submissionInfo = rank.submissionInfo;
        let timeInfo = rank.timeInfo;
        let cellClass = {};
        if (this.concernedList.indexOf(rank.uid) != -1) {
          dataRank[i].isConcerned = true;
        }
        Object.keys(submissionInfo).forEach((problemID) => {
          dataRank[i][problemID] = submissionInfo[problemID];
          let score = submissionInfo[problemID];
          if (timeInfo != null && timeInfo[problemID] != undefined) {
            cellClass[problemID] = "oi-100";
          } else if (score == 0) {
            cellClass[problemID] = "oi-0";
          } else if (score != null) {
            cellClass[problemID] = "oi-between";
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

    downloadRankCSV() {
      utils.downloadFile(
        `/api/file/download-contest-rank?cid=${
          this.$route.params.contestID
        }&forceRefresh=${this.forceUpdate ? true : false}`
      );
    },
  },
};
</script>
<style scoped>
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

.screen-full {
  margin-right: 8px;
}

#switches p {
  margin-top: 5px;
}
#switches p:first-child {
  margin-top: 0;
}
#switches p span {
  margin-left: 8px;
  margin-right: 4px;
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
