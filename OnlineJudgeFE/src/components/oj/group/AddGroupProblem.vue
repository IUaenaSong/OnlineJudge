<template>
  <div>
    <el-form :model="problemId" @submit.native.prevent>
      <el-form-item :label="$t('m.Problem_ID')" required>
        <el-input v-model="problemId" size="small" @keyup.enter.native="addGroupProblem" ></el-input>
      </el-form-item>
      <el-form-item style="text-align:center">
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="addGroupProblem"
          :loading="loading"
          size="small"
          >{{ $t('m.Add') }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import api from '@/common/api';
export default {
  name: 'AddProblemFromGroup',
  props: {
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
      loading: false,
      problemId: '',
    };
  },
  methods: {
    addGroupProblem() {
      if (this.contestID) {
        this.$prompt(
          this.$i18n.t('m.Enter_The_Problem_Display_ID_in_the_Contest'),
          'Tips'
        ).then(
          ({ value }) => {
            if (value == null || value == '') {
              this.$msg.error(this.$t('m.The_Problem_Display_ID_in_the_Contest_is_required'));
              return;
            }
            api.addGroupContestProblemFromGroup(this.problemId, this.contestID, value).then(
              (res) => {
                this.$msg.success(this.$i18n.t('m.Add_Successfully'));
                this.loading = false;
                this.$emit("currentChangeProblem");
                this.$emit("handleGroupPage");
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
            if (value == null || value == '') {
              this.$msg.error(this.$t('m.The_Problem_Display_ID_in_the_Exam_is_required'));
              return;
            }
            api.addGroupExamProblemFromGroup(this.problemId, this.examID, value).then(
              (res) => {
                this.$msg.success(this.$i18n.t('m.Add_Successfully'));
                this.loading = false;
                this.$emit("currentChangeProblem");
                this.$emit("handleGroupPage");
              },
              () => {}
            );
          },
          () => {}
        );
      } else {
        api.addGroupTrainingProblemFromGroup(this.problemId, this.trainingID).then(
          (res) => {
            this.$msg.success(this.$i18n.t('m.Add_Successfully'));
            this.loading = false;
            this.$emit("currentChangeProblem");
            this.$emit("handleGroupPage");
          },
          () => {}
        );
      }
    },
  },
};
</script>

