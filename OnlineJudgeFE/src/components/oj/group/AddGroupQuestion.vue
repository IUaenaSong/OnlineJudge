<template>
  <div style="text-align: center">
    <vxe-input
      v-model="keyword"
      :placeholder="$t('m.Enter_keyword')"
      type="search"
      size="medium"
      @search-click="filterByKeyword"
      @keyup.enter.native="filterByKeyword"
      style="margin-bottom: 10px"
    ></vxe-input>
    <section>
      <b class="question-filter">{{ $t("m.Question_Type") }}</b>
      <div>
        <el-tag
          size="medium"
          class="filter-item"
          type="primary"
          :effect="type == 0 ? 'dark' : 'plain'"
          @click="filterByType(0)"
        >
          {{ $t("m.All") }}
        </el-tag>
        <el-tag
          size="medium"
          class="filter-item"
          v-for="(key, index) in QUESTION_TYPE_REVERSE"
          :type="key.color"
          :effect="type == index ? 'dark' : 'plain'"
          :key="index"
          @click="filterByType(index)"
        >
          {{ $t("m." + key.name + "_Question") }}
        </el-tag>
      </div>
    </section>
    <vxe-table
      :data="questionList"
      :loading="loading"
      auto-resize
      stripe
      align="center"
    >
      <vxe-table-column title="ID" min-width="100" field="questionId">
      </vxe-table-column>
      <vxe-table-column
        min-width="150"
        :title="$t('m.Description')"
        field="description"
      >
      </vxe-table-column>
      <vxe-table-column
        field="type"
        :title="$t('m.Question_Type')"
        min-width="150"
        show-overflow
      >
        <template v-slot="{ row }">
          <el-tag
            v-if="row.type != 1"
            size="medium"
            class="filter-item"
            :type="QUESTION_TYPE_REVERSE[row.type].color"
            effect="dark"
          >
            {{ $t("m." + QUESTION_TYPE_REVERSE[row.type].name + "_Question") }}
          </el-tag>
          <el-tag
            v-if="row.type == 1"
            size="medium"
            type="primary"
            effect="plain"
          >
            {{
              $t(
                "m." +
                  (row.single
                    ? "Single_Choice"
                    : "Indefinite_Multiple_Choice") +
                  "_Question"
              )
            }}
          </el-tag>
        </template>
      </vxe-table-column>
      <vxe-table-column :title="$t('m.Option')" align="center" min-width="50">
        <template v-slot="{ row }">
          <el-tooltip effect="dark" :content="$t('m.Add')" placement="top">
            <el-button
              icon="el-icon-plus"
              size="mini"
              @click.native="addGroupQuestion(row.id, row.questionId)"
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
import api from "@/common/api";
const Pagination = () => import("@/components/oj/common/Pagination");
import { QUESTION_TYPE_REVERSE } from "@/common/constants";
export default {
  name: "AddQuestionFromGroup",
  components: {
    Pagination,
  },
  props: {
    examID: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      currentPage: 1,
      limit: 10,
      total: 0,
      loading: false,
      questionList: [],
      contest: {},
      exam: {},
      keyword: "",
      type: 0,
    };
  },
  created() {
    this.QUESTION_TYPE_REVERSE = Object.assign({}, QUESTION_TYPE_REVERSE);
  },
  mounted() {
    api
      .getGroupExam(this.examID)
      .then((res) => {
        this.exam = res.data.data;
        this.init();
      })
      .catch(() => {});
  },
  methods: {
    init() {
      this.getGroupQuestion();
    },
    onPageSizeChange(pageSize) {
      this.limit = pageSize;
      this.init();
    },
    currentChange(page) {
      this.currentPage = page;
      this.init();
    },
    getGroupQuestion() {
      this.loading = true;
      let params = {
        keyword: this.keyword,
        questionType: this.type,
        eid: this.exam.id,
        queryExisted: false,
      };
      api
        .getGroupExamQuestionList(this.currentPage, this.limit, params)
        .then((res) => {
          this.loading = false;
          this.total = res.data.data.questionList.total;
          this.questionList = res.data.data.questionList.records;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    addGroupQuestion(id, questionId) {
      this.$prompt(
        this.$i18n.t("m.Enter_The_Question_Display_ID_in_the_Exam"),
        "Tips"
      ).then(
        ({ value }) => {
          let data = {
            qid: id,
            eid: this.examID,
            displayId: value,
          };
          if (value == null || value == "") {
            this.$msg.error(
              this.$t("m.The_Question_Display_ID_in_the_Exam_is_required")
            );
            return;
          }
          api.addGroupExamQuestionFromGroup(data).then(
            (res) => {
              this.$msg.success(this.$i18n.t("m.Add_Successfully"));
              this.$emit("currentChangeQuestion");
              this.currentChange(1);
            },
            () => {}
          );
        },
        () => {}
      );
    },
    filterByKeyword() {
      this.getGroupQuestion(this.page);
    },
    filterByType(type) {
      this.type = parseInt(type);
      this.currentChange(1);
    },
  },
};
</script>
<style scoped>
.page {
  margin-top: 20px;
  text-align: right;
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
