<template>
  <el-row>
    <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
      <el-card class="box-card">
        <el-collapse v-model="activeName" accordion>
          <el-collapse-item name="Account">
            <template slot="title">
              <i class="fa fa-gear"> {{ $t("m.Account_Setting") }}</i>
            </template>
            <component :is="Account"></component>
          </el-collapse-item>
          <el-collapse-item name="UserInfo">
            <template slot="title">
              <i class="fa fa-gear"> {{ $t("m.UserInfo_Setting") }}</i>
            </template>
            <component :is="UserInfo"></component>
          </el-collapse-item>
        </el-collapse>
      </el-card>
    </el-col>
  </el-row>
</template>
<script>
const Account = () => import("@/components/oj/setting/Account");
const UserInfo = () => import("@/components/oj/setting/UserInfo");
export default {
  components: {
    Account,
    UserInfo,
  },
  data() {
    return {
      Account: "Account",
      UserInfo: "UserInfo",
      activeName: "Account",
    };
  },
};
</script>
<style scoped>
@media screen and (min-width: 1200px) {
  .box-card {
    margin-left: 10%;
    margin-right: 10%;
  }
}
/deep/ .el-collapse-item__header {
  border-radius: 4px;
  font-size: 18px;
  color: #409eff;
}
</style>
