<template>
  <el-card shadow="always">
    <div slot="header">
      <span class="panel-title">{{ $t("m.Exam_Rejudge") }}</span>
    </div>
    <section>
      <b class="question-filter">{{ $t("m.Question_Type") }}</b>
      <div>
        <el-tag
          size="medium"
          class="filter-item"
          type="success"
          :effect="type == 1 ? 'dark' : 'plain'"
          @click="filterByType(1)"
        >
          {{ $t("m.Choice_Question") }}
        </el-tag>
        <el-tag
          size="medium"
          class="filter-item"
          type="warning"
          :effect="type == 2 ? 'dark' : 'plain'"
          @click="filterByType(2)"
        >
          {{ $t("m.Judge_Question") }}
        </el-tag>
        <el-tag
          size="medium"
          class="filter-item"
          type="primary"
          :effect="type == 3 ? 'dark' : 'plain'"
          @click="filterByType(3)"
        >
          {{ $t("m.Code_Question") }}
        </el-tag>
      </div>
    </section>
    <vxe-table
      border="inner"
      stripe
      auto-resize
      align="center"
      :loading="loading"
      :data="type == 3 ? examProblemList : examQuestionList"
    >
      <vxe-table-column
        :field="type == 3 ? 'pid' : 'qid'"
        width="60"
        :title="$t('m.ID')"
      >
      </vxe-table-column>
      <vxe-table-column
        field="displayId"
        :title="$t('m.Problem_ID')"
        min-width="100"
      ></vxe-table-column>
      <vxe-table-column
        :field="type == 3 ? 'displayTitle' : 'description'"
        :title="$t('m.Title')"
        min-width="150"
        show-overflow
      >
      </vxe-table-column>
      <vxe-table-column field="ac" :title="$t('m.AC')" min-width="80">
      </vxe-table-column>
      <vxe-table-column field="total" :title="$t('m.Total')" min-width="80">
        <template v-slot="{ row }">
          <span>{{ row.ac + row.error }}</span>
        </template>
      </vxe-table-column>
      <vxe-table-column field="option" :title="$t('m.Option')" min-width="150">
        <template v-slot="{ row }">
          <el-button
            type="primary"
            size="small"
            :loading="btnLoading"
            icon="el-icon-refresh-right"
            @click="rejudgeProblem(row)"
            round
            >{{ $t("m.Rejudge_All") }}</el-button
          >
        </template>
      </vxe-table-column>
    </vxe-table>
  </el-card>
</template>
<script>
import { mapState, mapActions } from "vuex";
import api from "@/common/api";

export default {
  name: "ExamRejudgeAdmin",
  data() {
    return {
      btnLoading: false,
      loading: false,
      type: 1,
      examQuestionList: [],
    };
  },
  mounted() {
    this.examID = this.$route.params.examID;
    this.init();
  },
  methods: {
    ...mapActions(["getExamProblemList"]),
    init() {
      if (this.examProblemList.length == 0) {
        this.getExamProblemList();
      }
      this.getExamQuestionList();
    },
    getExamQuestionList() {
      this.loading = true;
      api.getExamQuestionList(this.type, this.$route.params.examID).then(
        (res) => {
          this.examQuestionList = res.data.data;
          this.loading = false;
        },
        (err) => {
          this.loading = false;
        }
      );
    },
    rejudgeProblem(row) {
      this.$confirm(this.$i18n.t("m.Exam_Rejudge_Tips"), "Tips", {
        confirmButtonText: this.$i18n.t("m.OK"),
        cancelButtonText: this.$i18n.t("m.Cancel"),
        type: "warning",
      }).then(
        () => {
          let params = {
            qid: row.qid,
            pid: row.pid,
            eid: row.eid,
          };
          this.btnLoading = true;
          let apiMethod =
            this.type == 3 ? "ExamRejudgeProblem" : "rejudgeExamQuesiton";
          api[apiMethod](params)
            .then((res) => {
              this.$msg.success(this.$i18n.t("m.Rejudge_successfully"));
              this.btnLoading = false;
            })
            .catch(() => {
              this.btnLoading = false;
            });
        },
        () => {}
      );
    },
    filterByType(type) {
      this.type = parseInt(type);
      this.init();
    },
  },
  computed: {
    ...mapState({
      exam: (state) => state.exam.exam,
      examProblemList: (state) => state.exam.examProblemList,
    }),
  },
};
</script>
<style scoped>
@media screen and (min-width: 1050px) {
  /deep/ .vxe-table--body-wrapper {
    overflow-x: hidden !important;
  }
}
section {
  display: flex;
  align-items: baseline;
  margin-bottom: 0.8em;
  margin-left: 10px;
}
.question-filter {
  margin-right: 1em;
  font-weight: bolder;
  white-space: nowrap;
  font-size: 16px;
  margin-top: 8px;
}
.filter-item {
  font-size: 13px;
  margin: 2px 5px;
}
.filter-item:hover {
  cursor: pointer;
}
</style>
