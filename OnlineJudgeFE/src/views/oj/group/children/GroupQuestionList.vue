<template>
  <el-card>
    <div class="filter-row">
      <el-row>
        <el-col :span="3">
          <span class="title">{{ $t('m.Group_Question') }}</span>
        </el-col>
        <el-col :span="18" v-if="isSuperAdmin || isGroupAdmin">
          <el-button
            v-if="!editPage"
            :type="createPage ? 'danger' : 'primary'"
            size="small"
            @click="handleCreatePage"
            :icon="createPage ? 'el-icon-back' : 'el-icon-plus'"
          >{{ createPage ? $t('m.Back') : $t('m.Create') }}</el-button>
          <el-button
            v-if="editPage && adminPage"
            type="danger"
            size="small"
            @click="handleEditPage"
            icon="el-icon-back"
          >{{ $t('m.Back') }}</el-button>`
          <el-button
            :type="adminPage ? 'warning' : 'success'"
            size="small"
            @click="handleAdminPage"
            :icon="adminPage ? 'el-icon-back' : 'el-icon-s-opportunity'"
          >{{ adminPage ? $t('m.Back') : $t('m.Question_Admin') }}</el-button>
        </el-col>
      </el-row>
    </div>
    <section v-if="!createPage && !editPage">
      <b class="question-filter">{{ $t('m.Question_Type') }}</b>
      <div>
        <el-tag
          size="medium"
          class="filter-item"
          type="primary"
          :effect="type == 0 ? 'dark' : 'plain'"
          @click="filterByType(0)"
        > {{ $t('m.All') }} </el-tag>
        <el-tag
          size="medium"
          class="filter-item"
          v-for="(key, index) in QUESTION_TYPE_REVERSE"
          :type="key.color"
          :effect="type == index ? 'dark' : 'plain'"
          :key="index"
          @click="filterByType(index)"
        > {{ $t('m.' + key.name + '_Question') }} </el-tag>
      </div>
    </section>
    <div v-if="!adminPage && !createPage">
      <vxe-table
        border="inner"
        stripe
        auto-resize
        highlight-hover-row
        :data="questionList"
        :loading="loading"
        align="center"
        @cell-click="goGroupQuestion"
      >
        <vxe-table-column
          field="questionId"
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
            > {{ $t('m.' + QUESTION_TYPE_REVERSE[row.type].name + '_Question') }} </el-tag>
            <el-tag
              v-if="row.type == 1"
              size="medium"
              type="primary"
              effect="plain"
            >
              {{ $t('m.' + (row.single ? 'Single_Choice' : 'Indefinite_Multiple_Choice') + '_Question') }}
            </el-tag>
          </template>
        </vxe-table-column>
      </vxe-table>
      <Pagination
        :total="total"
        :page-size="limit"
        @on-change="currentChange"
        :current.sync="currentPage"
        @on-page-size-change="onPageSizeChange"
        :layout="'prev, pager, next, sizes'"
      ></Pagination>
    </div>
    <QuestionList
      ref="questionList"
      v-if="adminPage && !createPage"
      :type="type"
      @handleEditPage="handleEditPage"
      @currentChange="currentChange"
    ></QuestionList>
    <Question
      v-if="createPage && !editPage"
      mode="add"
      :title="title"
      :apiMethod="apiMethod"
      @handleCreatePage="handleCreatePage"
      @currentChange="currentChange"
    >
    </Question>
  </el-card>
</template>

<script>
import { mapGetters } from 'vuex';
import Pagination from '@/components/oj/common/Pagination';
import QuestionList from '@/components/oj/group/QuestionList';
import Question from '@/components/oj/group/Question';
import { QUESTION_TYPE_REVERSE } from '@/common/constants';
import api from '@/common/api';
export default {
  name: 'GroupQuestionList',
  components: {
    Pagination,
    Question,
    QuestionList,
  },
  data() {
    return {
      total: 0,
      currentPage: 1,
      limit: 10,
      questionList: [],
      type: 0,
      title: '',
      apiMethod: '',
      loading: false,
      routeName: '',
      adminPage: false,
      createPage: false,
      editPage: false,
    }
  },
  mounted() {
    this.routeName = this.$route.name;
    if (this.routeName === 'GroupQuestionList') {
      this.title = this.$t('m.Create_Question');
      this.apiMethod = 'addGroupQuestion'
    } else if (this.routeName === 'GroupExamQuestionList') {
      this.title = this.$t('m.Create_Exam_Question');
      this.apiMethod = 'addGroupExamQuestion'
    }
    this.init();
  },
  created() {
    this.QUESTION_TYPE_REVERSE = Object.assign({}, QUESTION_TYPE_REVERSE);
  },
  methods: {
    init() {
      this.getGroupQuestionList();
    },
    onPageSizeChange(pageSize) {
      this.limit = pageSize;
      this.init();
    },
    currentChange(page) {
      this.currentPage = page;
      this.init();
    },
    getGroupQuestionList() {
      this.loading = true;
      api.getGroupQuestionList(this.currentPage, this.limit, this.type, this.$route.params.groupID).then(
        (res) => {
          this.questionList = res.data.data.records;
          this.total = res.data.data.total;
          this.loading = false;
        },
        (err) => {
          this.loading = false;
        }
      );
    },
    goGroupQuestion(event) {
      this.$router.push({
        name: 'GroupQuestionDetails',
        params: {
          questionId: event.row.questionId,
        },
      });
    },
    handleCreatePage() {
      this.createPage = !this.createPage;
    },
    handleEditPage() {
      this.editPage = !this.editPage;
      this.$refs.questionList.editPage = this.editPage;
    },
    handleAdminPage() {
      this.adminPage = !this.adminPage;
      this.createPage = false;
      this.editPage = false;
    },
    filterByType(type) {
      this.type = parseInt(type);
      this.currentChange(1);
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'isSuperAdmin', 'isGroupAdmin']),
  },
}
</script>

<style scoped>
.title {
  font-size: 20px;
  vertical-align: middle;
  float: left;
}
.filter-row {
  margin-bottom: 5px;
  text-align: center;
}
@media screen and (max-width: 768px) {
  .filter-row span {
    margin-left: 5px;
    margin-right: 5px;
  }
}
@media screen and (min-width: 768px) {
  .filter-row span {
    margin-left: 10px;
    margin-right: 10px;
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
