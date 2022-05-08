<template>
  <div style="text-align:center">
    <vxe-input
      v-model="keyword"
      :placeholder="$t('m.Enter_keyword')"
      type="search"
      size="medium"
      @search-click="filterByKeyword"
      @keyup.enter.native="filterByKeyword"
      style="margin-bottom:10px"
    ></vxe-input>
    <vxe-table
      :data="problemList"
      :loading="loading"
      auto-resize
      stripe
      align="center"
    >
      <vxe-table-column title="ID" min-width="100" field="problemId">
      </vxe-table-column>
      <vxe-table-column min-width="150" :title="$t('m.Title')" field="title">
      </vxe-table-column>
      <vxe-table-column :title="$t('m.Option')" align="center" min-width="100">
        <template v-slot="{ row }">
          <el-tooltip effect="dark" :content="$t('m.Add')" placement="top">
            <el-button
              icon="el-icon-plus"
              size="mini"
              @click.native="addPublicProblem(row.id, row.problemId)"
              type="primary"
            >
            </el-button>
          </el-tooltip>
        </template>
      </vxe-table-column>
    </vxe-table>
    <el-pagination
      v-if="total"
      class="page"
      layout="prev, pager, next, sizes"
      @current-change="currentChange"
      :page-size="limit"
      :current-page.sync="currentPage"
      :total="total"
      @size-change="onPageSizeChange"
      :page-sizes="[10, 30, 50, 100]"
    >
    </el-pagination>
  </div>
</template>
<script>
import api from '@/common/api';
import Pagination from '@/components/oj/common/Pagination';
export default {
  name: 'AddProblemFromPublic',
  components: {
    Pagination,
  },
  props: {
    apiMethod: {
      type: String,
      default: 'getGroupTrainingProblemList'
    },
    trainingID: {
      type: Number,
      default: null
    },
    contestID: {
      type: Number,
      default: null
    },
    examID: {
      type: Number,
      default: null
    },
  },
  data() {
    return {
      currentPage: 1,
      limit: 10,
      total: 0,
      loading: false,
      problemList: [],
      contest: {},
      exam: {},
      keyword: '',
    };
  },
  mounted() {
    if (this.contestID) {
      api.getGroupContest(this.contestID).then((res) => {
          this.contest = res.data.data;
          this.init();
        })
        .catch(() => {});
    } else if (this.examID) {
      api.getGroupExam(this.examID).then((res) => {
          this.exam = res.data.data;
          this.init();
        })
        .catch(() => {});
    } else if (this.trainingID) {
      this.init();
    }
  },
  methods: {
    init() {
      this.getPublicProblem();
    },
    onPageSizeChange(pageSize) {
      this.limit = pageSize;
      this.init();
    },
    currentChange(page) {
      this.currentPage = page;
      this.init();
    },
    getPublicProblem() {
      this.loading = true;
      let params = {
        keyword: this.keyword,
        problemType: this.contest.type,
        cid: this.contest.id,
        eid: this.exam.id,
        tid: this.trainingID,
        queryExisted: false,
      };
      if (this.examID) {
        params.problemType = 1;
      }
      api[this.apiMethod](this.currentPage, this.limit, params)
        .then((res) => {
          this.loading = false;
          this.total = res.data.data.problemList.total;
          this.problemList = res.data.data.problemList.records;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    addPublicProblem(id, problemId) {
      if (this.contestID) {
        this.$prompt(
          this.$i18n.t('m.Enter_The_Problem_Display_ID_in_the_Contest'),
          'Tips'
        ).then(
          ({ value }) => {
            let data = {
              pid: id,
              cid: this.contestID,
              displayId: value,
            };
            if (value == null || value == '') {
              this.$msg.error(this.$t('m.The_Problem_Display_ID_in_the_Contest_is_required'));
              return;
            }
            api.addGroupContestProblemFromPublic(data).then(
              (res) => {
                this.$msg.success(this.$i18n.t('m.Add_Successfully'));
                this.$emit("currentChangeProblem");
                this.currentChange(1);
              },
              () => {}
            );
          },
          () => {}
        );
      } else if (this.examID) {
        this.$prompt(
          this.$i18n.t('m.Enter_The_Problem_Display_ID_in_the_Exam'),
          'Tips'
        ).then(
          ({ value }) => {
            let data = {
              pid: id,
              eid: this.examID,
              displayId: value,
            };
            if (value == null || value == '') {
              this.$msg.error(this.$t('m.The_Problem_Display_ID_in_the_Exam_is_required'));
              return;
            }
            api.addGroupExamProblemFromPublic(data).then(
              (res) => {
                this.$msg.success(this.$i18n.t('m.Add_Successfully'));
                this.$emit("currentChangeProblem");
                this.currentChange(1);
              },
              () => {}
            );
          },
          () => {}
        );
      } else {
        let data = {
          pid: id,
          tid: this.trainingID,
          displayId: problemId,
        };
        api.addGroupTrainingProblemFromPublic(data).then(
          (res) => {
            this.$msg.success(this.$i18n.t('m.Add_Successfully'));
            this.$emit("currentChangeProblem");
            this.currentChange(1);
          },
          () => {}
        );
      }
    },
    filterByKeyword() {
      this.getPublicProblem(this.page);
    },
  },
};
</script>
<style scoped>
.page {
  margin-top: 20px;
  text-align: right;
}
</style>
