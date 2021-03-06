<template>
  <el-row>
    <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
      <el-card>
        <vxe-table
          border="inner"
          stripe
          auto-resize
          highlight-hover-row
          :data="problemList"
          align="center"
          @cell-click="goExamProblem"
        >
          <!-- OI赛制的最近提交显示 -->
          <vxe-table-column
            field="status"
            title=""
            width="50"
            v-if="isAuthenticated"
          >
            <template v-slot="{ row }">
              <template v-if="isGetStatusOk">
                <span
                  :class="
                    getScoreColor(
                      (row.myScore * row.score) / row.ioScore,
                      row.score
                    )
                  "
                  v-if="row.myScore != null"
                  >{{ (row.myScore * row.score) / row.ioScore }}</span
                >
                <el-tooltip
                  :content="JUDGE_STATUS[row.myStatus]['name']"
                  placement="top"
                  v-else-if="row.myStatus == -5"
                >
                  <i
                    class="fa fa-question"
                    :style="getIconColor(row.myStatus)"
                  ></i>
                </el-tooltip>
                <el-tooltip
                  :content="JUDGE_STATUS[row.myStatus]['name']"
                  placement="top"
                  v-else
                >
                  <i
                    class="el-icon-minus"
                    :style="getIconColor(row.myStatus)"
                    v-if="row.myStatus != -10"
                  ></i>
                </el-tooltip>
              </template>
            </template>
          </vxe-table-column>

          <vxe-table-column field="displayId" width="80" title="#">
            <template v-slot="{ row }">
              <span style="vertical-align: top" v-if="row.color">
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
                    :fill="row.color"
                    p-id="5841"
                  ></path>
                </svg>
              </span>
              <span>{{ row.displayId }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="displayTitle"
            :title="$t('m.Title')"
            min-width="200"
          ></vxe-table-column>
          <vxe-table-column
            field="score"
            :title="$t('m.Score')"
            min-width="50"
          ></vxe-table-column>

          <!-- 以下列只有在实时刷新榜单的情况下才显示 -->
          <vxe-table-column field="ac" :title="$t('m.AC')" min-width="80">
            <template v-slot="{ row }">
              <span v-if="!isExamAdmin">
                <i
                  class="fa fa-question"
                  style="font-weight: 600; font-size: 16px; color: #909399"
                ></i>
              </span>
              <span v-else>
                {{ row.ac }}
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="total" :title="$t('m.Total')" min-width="80">
            <template v-slot="{ row }">
              <span v-if="!isExamAdmin">
                <i
                  class="fa fa-question"
                  style="font-weight: 600; font-size: 16px; color: #909399"
                ></i>
              </span>
              <span v-else>
                {{ row.ac + row.error }}
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="ACRate"
            :title="$t('m.AC_Rate')"
            min-width="120"
          >
            <template v-slot="{ row }">
              <span>
                <el-tooltip
                  effect="dark"
                  :content="row.ac + '/' + (row.ac + row.error)"
                  placement="top"
                >
                  <el-progress
                    :text-inside="true"
                    :stroke-width="20"
                    :percentage="getPassingRate(row.ac, row.ac + row.error)"
                  ></el-progress>
                </el-tooltip>
              </span>
            </template>
          </vxe-table-column>
        </vxe-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import { mapState, mapGetters } from "vuex";
import { JUDGE_STATUS } from "@/common/constants";
import api from "@/common/api";
export default {
  name: "ExamProblemList",
  data() {
    return {
      JUDGE_STATUS: {},
      isGetStatusOk: false,
      testcolor: "rgba(0, 206, 209, 1)",
    };
  },
  mounted() {
    this.JUDGE_STATUS = Object.assign({}, JUDGE_STATUS);
    this.getExamProblemList();
  },
  methods: {
    getExamProblemList() {
      this.$store.dispatch("getExamProblemList").then((res) => {
        if (this.isAuthenticated) {
          let isContestProblemList = false; // 为了与比赛题目区分
          let isExamProblemList = true;
          // 如果已登录，则需要查询对当前页面题目列表中各个题目的提交情况
          let pidList = [];
          if (this.problemList && this.problemList.length > 0) {
            for (let index = 0; index < this.problemList.length; index++) {
              pidList.push(this.problemList[index].pid);
            }
            api
              .getUserProblemStatus(
                pidList,
                isContestProblemList,
                isExamProblemList,
                0,
                this.$route.params.examID
              )
              .then((res) => {
                let result = res.data.data;
                for (let index = 0; index < this.problemList.length; index++) {
                  this.problemList[index]["myStatus"] =
                    result[this.problemList[index].pid]["status"];
                  this.problemList[index]["myScore"] =
                    result[this.problemList[index].pid]["score"];
                }
                this.isGetStatusOk = true;
              });
          }
        }
      });
    },
    goExamProblem(event) {
      this.$router.push({
        name: "ExamProblemDetails",
        params: {
          examID: this.$route.params.examID,
          problemID: event.row.displayId,
        },
      });
    },
    getPassingRate(ac, total) {
      if (!total) {
        return 0;
      }
      return ((ac / total) * 100).toFixed(2);
    },
    getIconColor(status) {
      return (
        "font-weight: 600;font-size: 16px;color:" + JUDGE_STATUS[status].rgb
      );
    },
    getScoreColor(myScore, score) {
      if (myScore == 0) {
        return "el-tag el-tag--small oi-0";
      } else if (myScore > 0 && myScore < score) {
        return "el-tag el-tag--small oi-between";
      } else if (myScore == score) {
        return "el-tag el-tag--small oi-100";
      }
    },
  },
  computed: {
    ...mapState({
      problemList: (state) => state.exam.examProblemList,
    }),
    ...mapGetters(["isAuthenticated", "isExamAdmin"]),
  },
};
</script>

<style scoped>
@media screen and (min-width: 1050px) {
  /deep/ .vxe-table--body-wrapper {
    overflow-x: hidden !important;
  }
}
</style>
