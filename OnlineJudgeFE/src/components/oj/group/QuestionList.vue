<template>
  <div>
    <vxe-table
      stripe
      auto-resize
      :data="questionList"
      :loading="loading"
      align="center"
      v-if="!editPage"
    >
      <vxe-table-column min-width="64" field="id" title="ID">
      </vxe-table-column>
      <vxe-table-column
        min-width="100"
        field="questionId"
        :title="$t('m.Display_ID')"
        v-if="!examID"
      >
      </vxe-table-column>
      <vxe-table-column
        min-width="150"
        :title="$t('m.Display_ID')"
        v-else
        align="left"
      >
        <template v-slot="{ row }">
          <p>{{ $t("m.Original_Display_ID") }}：{{ row.questionId }}</p>
          <p v-if="examQuestionMap[row.id]">
            {{ $t("m.Exam_Display_ID") }}：{{
              examQuestionMap[row.id]["displayId"]
            }}
          </p>
          <p v-if="examQuestionMap[row.id]">
            {{ $t("m.Score") }}：
            <el-input
              v-model="examQuestionMap[row.id].score"
              size="small"
              style="width: 60%"
              @keyup.enter.native="
                changeExamQuestionScore(examQuestionMap[row.id])
              "
            >
            </el-input>
          </p>
        </template>
      </vxe-table-column>
      <vxe-table-column
        field="description"
        min-width="150"
        :title="$t('m.Question_Description')"
        show-overflow
        v-if="!examID"
      >
      </vxe-table-column>
      <vxe-table-column
        field="author"
        min-width="130"
        :title="$t('m.Author')"
        show-overflow
      >
      </vxe-table-column>
      <vxe-table-column min-width="120" :title="$t('m.Created_Time')">
        <template v-slot="{ row }">
          {{ row.gmtCreate | localtime }}
        </template>
      </vxe-table-column>
      <vxe-table-column
        min-width="96"
        field="modifiedUser"
        :title="$t('m.Modified_User')"
        show-overflow
      >
      </vxe-table-column>
      <vxe-table-column min-width="120" :title="$t('m.Auth')">
        <template v-slot="{ row }">
          <el-select
            v-model="row.auth"
            @change="changeQuestionAuth(row.id, row.auth)"
            size="small"
            :disabled="
              row.gid != gid ||
              (!isGroupRoot && userInfo.username != row.author)
            "
          >
            <el-option :label="$t('m.Public_Question')" :value="1"></el-option>
            <el-option :label="$t('m.Private_Question')" :value="2"></el-option>
            <el-option
              :label="$t('m.Exam_Question')"
              :value="3"
              :disabled="!examID"
            ></el-option>
          </el-select>
        </template>
      </vxe-table-column>
      <vxe-table-column :title="$t('m.Option')" min-width="200">
        <template v-slot="{ row }">
          <el-tooltip
            effect="dark"
            :content="$t('m.Edit')"
            placement="top"
            v-if="
              row.gid == gid && (isGroupRoot || userInfo.username == row.author)
            "
          >
            <el-button
              icon="el-icon-edit-outline"
              size="mini"
              @click.native="goEditQuestion(row.id)"
              type="primary"
            >
            </el-button>
          </el-tooltip>
          <el-tooltip
            effect="dark"
            :content="$t('m.Remove')"
            placement="top"
            v-if="examID"
          >
            <el-button
              icon="el-icon-close"
              size="mini"
              @click.native="removeQuestion(row.id)"
              type="warning"
            >
            </el-button>
          </el-tooltip>
          <p v-if="examID"></p>
          <el-tooltip
            effect="dark"
            :content="$t('m.Delete')"
            placement="top"
            v-if="
              row.gid == gid && (isGroupRoot || userInfo.username == row.author)
            "
          >
            <el-button
              icon="el-icon-delete-solid"
              size="mini"
              @click.native="deleteQuestion(row.id)"
              type="danger"
            >
            </el-button>
          </el-tooltip>
        </template>
      </vxe-table-column>
    </vxe-table>
    <Pagination
      v-if="!editPage"
      :total="total"
      :page-size="limit"
      @on-change="currentChange"
      :current.sync="currentPage"
      @on-page-size-change="onPageSizeChange"
      :layout="'prev, pager, next, sizes'"
    ></Pagination>
    <Question
      v-if="editPage"
      mode="edit"
      :title="$t('m.Edit_Question')"
      apiMethod="updateGroupQuestion"
      :qid="qid"
      :examID="examID"
      @handleEditPage="handleEditPage"
      @currentChange="currentChange"
    ></Question>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
const Pagination = () => import("@/components/oj/common/Pagination");
import api from "@/common/api";
import Question from "@/components/oj/group/Question";
export default {
  name: "GroupQuestionList",
  components: {
    Pagination,
    Question,
  },
  props: {
    examID: {
      type: Number,
      default: null,
    },
    type: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      total: 0,
      currentPage: 1,
      limit: 10,
      questionList: [],
      examQuestionMap: {},
      loading: false,
      qid: null,
      editPage: false,
      predefineColors: null,
      gid: null,
    };
  },
  mounted() {
    this.gid = this.$route.params.groupID;
    this.init();
  },
  watch: {
    type(newVal) {
      this.currentChange(1);
    },
  },
  methods: {
    init() {
      this.getAdminQuestionList();
    },
    onPageSizeChange(pageSize) {
      this.limit = pageSize;
      this.init();
    },
    currentChange(page) {
      this.currentPage = page;
      this.init();
    },
    getAdminQuestionList() {
      this.loading = true;
      if (!this.examID) {
        api
          .getGroupAdminQuestionList(
            this.currentPage,
            this.limit,
            this.type,
            this.$route.params.groupID
          )
          .then(
            (res) => {
              this.questionList = res.data.data.records;
              this.total = res.data.data.total;
              this.loading = false;
            },
            (err) => {
              this.loading = false;
            }
          );
      } else {
        let params = {
          eid: this.examID,
          questionType: this.type,
          queryExisted: true,
        };
        api.getGroupExamQuestionList(this.currentPage, this.limit, params).then(
          (res) => {
            this.total = res.data.data.questionList.total;
            this.questionList = res.data.data.questionList.records;
            this.examQuestionMap = res.data.data.examQuestionMap;
            this.loading = false;
          },
          (err) => {
            this.loading = false;
          }
        );
      }
    },
    handleEditPage() {
      this.editPage = false;
      this.$emit("currentChange", 1);
      if (this.examID) {
        this.$emit("handleEditQuestionPage");
      } else {
        this.$emit("handleEditPage");
      }
    },
    goEditQuestion(id) {
      this.qid = id;
      this.editPage = !this.editPage;
      if (this.examID) {
        this.$emit("handleEditQuestionPage");
      } else {
        this.$emit("handleEditPage");
      }
    },
    changeQuestionAuth(qid, auth) {
      api.changeGroupQuestionAuth(qid, auth).then((res) => {
        this.$msg.success(this.$i18n.t("m.Update_Successfully"));
        this.$emit("currentChange", 1);
      });
    },
    changeExamQuestionScore(examQuestion) {
      api.updateGroupExamQuestion(examQuestion).then((res) => {
        this.$msg.success(this.$i18n.t("m.Update_Successfully"));
      });
    },
    removeQuestion(qid) {
      this.$confirm(
        this.$i18n.t("m.Remove_Exam_Question_Tips"),
        this.$i18n.t("m.Warning"),
        {
          type: "warning",
        }
      ).then(
        () => {
          api
            .deleteGroupExamQuestion(qid, this.examID)
            .then((res) => {
              this.$msg.success(this.$t("m.Remove_Successfully"));
              this.$emit("currentChangeQuestion");
            })
            .catch(() => {});
        },
        () => {}
      );
    },
    deleteQuestion(id) {
      this.$confirm(
        this.$i18n.t("m.Delete_Question_Tips"),
        this.$i18n.t("m.Warning"),
        {
          type: "warning",
        }
      ).then(
        () => {
          api
            .deleteGroupQuestion(id, this.$route.params.groupID)
            .then((res) => {
              this.$msg.success(this.$i18n.t("m.Delete_successfully"));
              this.$emit("currentChange", 1);
              this.currentChange(1);
            })
            .catch(() => {});
        },
        () => {}
      );
    },
  },
  computed: {
    ...mapGetters(["userInfo", "isGroupRoot"]),
  },
};
</script>