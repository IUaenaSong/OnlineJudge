<template>
  <div>
    <vxe-table
      stripe
      auto-resize
      :data="examList"
      :loading="loading"
      align="center"
      v-if="!editPage"
    >
      <vxe-table-column field="id" width="80" title="ID"> </vxe-table-column>
      <vxe-table-column
        field="title"
        min-width="150"
        :title="$t('m.Title')"
        show-overflow
      >
      </vxe-table-column>
      <vxe-table-column :title="$t('m.Auth')" width="100">
        <template v-slot="{ row }">
          <el-tooltip
            :content="$t('m.' + EXAM_TYPE_REVERSE[row.auth].tips)"
            placement="top"
            effect="light"
          >
            <el-tag :type="EXAM_TYPE_REVERSE[row.auth].color" effect="plain">
              {{ $t("m." + EXAM_TYPE_REVERSE[row.auth].name) }}
            </el-tag>
          </el-tooltip>
        </template>
      </vxe-table-column>
      <vxe-table-column :title="$t('m.Status')" width="100">
        <template v-slot="{ row }">
          <el-tag
            effect="dark"
            :color="EXAM_STATUS_REVERSE[row.status].color"
            size="medium"
          >
            {{ $t("m." + EXAM_STATUS_REVERSE[row.status]["name"]) }}
          </el-tag>
        </template>
      </vxe-table-column>
      <vxe-table-column :title="$t('m.Visible')" min-width="80">
        <template v-slot="{ row }">
          <el-switch
            v-model="row.visible"
            :disabled="!isGroupRoot && userInfo.uid != row.uid"
            @change="changeExamVisible(row.id, row.visible)"
          >
          </el-switch>
        </template>
      </vxe-table-column>
      <vxe-table-column min-width="210" :title="$t('m.Info')">
        <template v-slot="{ row }">
          <p>{{ $t("m.Start_Time") }}: {{ row.startTime | localtime }}</p>
          <p>{{ $t("m.End_Time") }}: {{ row.endTime | localtime }}</p>
          <p>{{ $t("m.Created_Time") }}: {{ row.gmtCreate | localtime }}</p>
          <p>{{ $t("m.Creator") }}: {{ row.author }}</p>
        </template>
      </vxe-table-column>
      <vxe-table-column min-width="150" :title="$t('m.Option')">
        <template v-slot="{ row }">
          <el-tooltip
            effect="dark"
            :content="$t('m.Edit')"
            placement="top"
            v-if="isGroupRoot || userInfo.uid == row.uid"
          >
            <el-button
              icon="el-icon-edit"
              size="mini"
              @click.native="goEditExam(row.id)"
              type="primary"
            >
            </el-button>
          </el-tooltip>
          <el-tooltip
            effect="dark"
            :content="$t('m.View_Exam_Question_List')"
            placement="top"
            v-if="isGroupRoot || userInfo.uid == row.uid"
          >
            <el-button
              icon="el-icon-tickets"
              size="mini"
              @click.native="goExamQuestionList(row.id)"
              type="success"
            >
            </el-button>
          </el-tooltip>
          <p></p>
          <el-tooltip
            effect="dark"
            :content="$t('m.View_Exam_Problem_List')"
            placement="top"
            v-if="isGroupRoot || userInfo.uid == row.uid"
          >
            <el-button
              icon="fa fa-list"
              size="mini"
              @click.native="goExamProblemList(row.id)"
              type="warning"
            >
            </el-button>
          </el-tooltip>
          <el-tooltip
            effect="dark"
            :content="$t('m.Delete')"
            placement="top"
            v-if="isGroupRoot || userInfo.uid == row.uid"
          >
            <el-button
              icon="el-icon-delete"
              size="mini"
              @click.native="deleteExam(row.id)"
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
    <Exam
      v-if="editPage"
      mode="edit"
      :title="$t('m.Edit_Exam')"
      apiMethod="updateGroupExam"
      :cid="cid"
      @handleEditPage="handleEditPage"
      @currentChange="currentChange"
    ></Exam>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
const Pagination = () => import("@/components/oj/common/Pagination");
import api from "@/common/api";
import Exam from "@/components/oj/group/Exam";
import {
  EXAM_STATUS_REVERSE,
  EXAM_TYPE,
  EXAM_TYPE_REVERSE,
  EXAM_STATUS,
} from "@/common/constants";
export default {
  name: "GroupExamList",
  components: {
    Pagination,
    Exam,
  },
  data() {
    return {
      total: 0,
      currentPage: 1,
      limit: 10,
      examList: [],
      loading: false,
      routeName: "",
      cid: null,
      editPage: false,
      downloadDialogVisible: false,
      excludeAdmin: true,
      splitType: "user",
    };
  },
  mounted() {
    this.EXAM_STATUS_REVERSE = Object.assign({}, EXAM_STATUS_REVERSE);
    this.EXAM_TYPE = Object.assign({}, EXAM_TYPE);
    this.EXAM_TYPE_REVERSE = Object.assign({}, EXAM_TYPE_REVERSE);
    this.EXAM_STATUS = Object.assign({}, EXAM_STATUS);
    this.init();
  },
  methods: {
    init() {
      this.getAdminExamList();
    },
    onPageSizeChange(pageSize) {
      this.limit = pageSize;
      this.init();
    },
    currentChange(page) {
      this.currentPage = page;
      this.init();
    },
    handleEditPage() {
      this.editPage = false;
      this.$emit("currentChange", 1);
      this.$emit("handleEditPage");
    },
    getAdminExamList() {
      this.loading = true;
      api
        .getGroupAdminExamList(
          this.currentPage,
          this.limit,
          this.$route.params.groupID
        )
        .then(
          (res) => {
            this.examList = res.data.data.records;
            this.total = res.data.data.total;
            this.loading = false;
          },
          (err) => {
            this.loading = false;
          }
        );
    },
    goEditExam(examID) {
      this.editPage = true;
      this.cid = examID;
      this.$emit("handleEditPage");
    },
    goExamProblemList(examID) {
      this.$emit("handleProblemPage", examID);
    },
    goExamQuestionList(examID) {
      this.$emit("handleQuestionPage", examID);
    },
    changeExamVisible(cid, visible) {
      api.changeGroupExamVisible(cid, visible).then((res) => {
        this.$msg.success(this.$i18n.t("m.Update_Successfully"));
        this.$emit("currentChange", 1);
        this.currentChange(1);
      });
    },
    deleteExam(id) {
      this.$confirm(
        this.$i18n.t("m.Delete_Exam_Tips"),
        this.$i18n.t("m.Warning"),
        {
          type: "warning",
        }
      ).then(
        () => {
          api
            .deleteGroupExam(id, this.$route.params.groupID)
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
    ...mapGetters(["isGroupRoot", "userInfo"]),
  },
};
</script>