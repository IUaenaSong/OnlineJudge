<template>
  <el-row>
    <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
      <el-card>
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
          border="inner"
          stripe
          auto-resize
          highlight-hover-row
          :data="questionList"
          :loading="loading"
          align="center"
          @cell-click="goExamQuestion"
        >
          <vxe-table-column :title="$t('m.Status')" width="100">
            <template v-slot="{ row }">
              <el-tag
                type="success"
                effect="dark"
                v-if="submittedQuestion.indexOf(row.displayId) != -1"
              >
                {{ $t("m.Submitted") }}
              </el-tag>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="displayId"
            :title="$t('m.Question_ID')"
            width="150"
            show-overflow
          >
          </vxe-table-column>
          <vxe-table-column
            field="description"
            :title="$t('m.Question_Description')"
            min-width="200"
            show-overflow
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
                {{
                  $t("m." + QUESTION_TYPE_REVERSE[row.type].name + "_Question")
                }}
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
          <vxe-table-column
            field="score"
            :title="$t('m.Score')"
            min-width="50"
            show-overflow
          >
          </vxe-table-column>
        </vxe-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import { mapGetters } from "vuex";
import { QUESTION_TYPE_REVERSE } from "@/common/constants";
import api from "@/common/api";
export default {
  name: "GroupQuestionList",
  data() {
    return {
      questionList: [],
      type: 0,
      examID: null,
      loading: true,
      submittedQuestion: [],
    };
  },
  mounted() {
    this.examID = this.$route.params.examID;
    this.init();
  },
  created() {
    this.QUESTION_TYPE_REVERSE = Object.assign({}, QUESTION_TYPE_REVERSE);
  },
  methods: {
    init() {
      this.getExamQuestionList();
    },
    onPageSizeChange(pageSize) {
      this.limit = pageSize;
      this.init();
    },
    currentChange(page) {
      this.currentPage = page;
      this.init();
    },
    getExamQuestionList() {
      this.loading = true;
      api.getExamQuestionList(this.type, this.$route.params.examID).then(
        (res) => {
          this.questionList = res.data.data;
          if (this.isAuthenticated) {
            api.getExamQuestionStatus(this.$route.params.examID).then((res) => {
              this.submittedQuestion = res.data.data;
            });
          }
          this.loading = false;
        },
        (err) => {
          this.loading = false;
        }
      );
    },
    goExamQuestion(event) {
      this.$router.push({
        name: "ExamQuestionDetails",
        params: {
          questionId: event.row.displayId,
          examID: this.examID,
        },
      });
    },
    filterByType(type) {
      this.type = parseInt(type);
      this.currentChange(1);
    },
  },
  computed: {
    ...mapGetters(["isAuthenticated", "isSuperAdmin", "isGroupAdmin"]),
  },
  watch: {
    $route() {
      this.init();
    },
    isAuthenticated(newVal) {
      if (newVal === true) {
        this.init();
      }
    },
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
