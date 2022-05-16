<template>
  <el-row>
    <el-collapse v-model="activeName" accordion>
      <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
        <el-card>
          <el-collapse-item name="Auth">
            <div slot="title">
              <span class="panel-title home-title">{{
                $t("m.General_Auth")
              }}</span>
            </div>
            <div class="filter-row">
              <span>
                <el-button
                  type="primary"
                  size="small"
                  @click="openAuthDialog(null)"
                  icon="el-icon-plus"
                  >{{ $t("m.Create") }}</el-button
                >
              </span>
              <span>
                <vxe-input
                  v-model="keyword"
                  :placeholder="$t('m.Enter_keyword')"
                  type="search"
                  size="medium"
                  @search-click="filterByKeyword"
                  @keyup.enter.native="filterByKeyword"
                ></vxe-input>
              </span>
            </div>
            <div class="list">
              <vxe-table
                :loading="loading"
                ref="table"
                :data="authList"
                auto-resize
                stripe
              >
                <vxe-table-column min-width="50" field="id" title="ID">
                </vxe-table-column>
                <vxe-table-column
                  min-width="150"
                  field="name"
                  show-overflow
                  :title="$t('m.Auth_Name')"
                >
                </vxe-table-column>
                <vxe-table-column
                  min-width="150"
                  field="permission"
                  show-overflow
                  :title="$t('m.Auth_Permission')"
                >
                </vxe-table-column>
                <vxe-table-column
                  min-width="150"
                  field="gmtCreate"
                  :title="$t('m.Created_Time')"
                >
                  <template v-slot="{ row }">
                    {{ row.gmtCreate | localtime }}
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  min-width="150"
                  field="gmtModified"
                  :title="$t('m.Modified_Time')"
                >
                  <template v-slot="{ row }">
                    {{ row.gmtModified | localtime }}
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  min-width="100"
                  field="status"
                  :title="$t('m.Status')"
                >
                  <template v-slot="{ row }">
                    <el-switch
                      v-model="row.status"
                      active-text=""
                      inactive-text=""
                      :active-value="0"
                      :inactive-value="1"
                      @change="handleStatusSwitch(row)"
                    >
                    </el-switch>
                  </template>
                </vxe-table-column>
                <vxe-table-column :title="$t('m.Option')" min-width="150">
                  <template v-slot="row">
                    <el-tooltip
                      class="item"
                      effect="dark"
                      :content="$t('m.Edit_Auth')"
                      placement="top"
                    >
                      <el-button
                        icon="el-icon-edit-outline"
                        @click.native="openAuthDialog(row.row)"
                        size="mini"
                        type="primary"
                      ></el-button>
                    </el-tooltip>
                    <el-tooltip
                      class="item"
                      effect="dark"
                      :content="$t('m.Delete_Auth')"
                      placement="top"
                    >
                      <el-button
                        icon="el-icon-delete-solid"
                        @click.native="deleteAuth(row.row.id)"
                        size="mini"
                        type="danger"
                      ></el-button>
                    </el-tooltip>
                  </template>
                </vxe-table-column>
              </vxe-table>

              <div class="panel-options">
                <el-pagination
                  v-if="total"
                  class="page"
                  layout="prev, pager, next, sizes"
                  @current-change="currentChange"
                  :page-size="pageSize"
                  :total="total"
                  @size-change="onPageSizeChange"
                  :page-sizes="[10, 30, 50, 100]"
                >
                </el-pagination>
              </div>
            </div>
          </el-collapse-item>
        </el-card>
      </el-col>
      <el-col :span="24" style="margin-top: 10px; margin-bottom: 10px">
        <el-card>
          <el-collapse-item name="RoleAuth">
            <div slot="title">
              <span class="panel-title home-title">{{
                $t("m.Role_Auth")
              }}</span>
            </div>
            <div class="filter-row">
              <span>
                <el-button
                  type="primary"
                  size="small"
                  @click="openRoleAuthDialog(null)"
                  icon="el-icon-plus"
                  >{{ $t("m.Create") }}</el-button
                >
              </span>
              <span>
                <vxe-input
                  v-model="roleAuthKeyword"
                  :placeholder="$t('m.Enter_keyword')"
                  type="search"
                  size="medium"
                  @search-click="filterRoleAuthByKeyword"
                  @keyup.enter.native="filterRoleAuthByKeyword"
                ></vxe-input>
              </span>
              <span>
                <el-select
                  v-model="roleId"
                  @change="AuthRoleChangeFilter"
                  size="small"
                  style="width: 180px"
                >
                  <el-option
                    label="请选择"
                    :value="0"
                    :key="0"
                    disabled
                  ></el-option>
                  <el-option
                    label="超级管理员"
                    :value="1000"
                    :key="1000"
                  ></el-option>
                  <el-option
                    label="普通管理员"
                    :value="1001"
                    :key="1001"
                  ></el-option>
                  <el-option
                    label="题目管理员"
                    :value="1008"
                    :key="1008"
                  ></el-option>
                  <el-option
                    label="用户(默认)"
                    :value="1002"
                    :key="1002"
                  ></el-option>
                  <el-option
                    label="用户(禁止提交)"
                    :value="1003"
                    :key="1003"
                  ></el-option>
                  <el-option
                    label="用户(禁止发讨论)"
                    :value="1004"
                    :key="1004"
                  ></el-option>
                  <el-option
                    label="用户(禁言)"
                    :value="1005"
                    :key="1005"
                  ></el-option>
                  <el-option
                    label="用户(禁止提交&禁止发讨论)"
                    :value="1006"
                    :key="1006"
                  ></el-option>
                  <el-option
                    label="用户(禁止提交&禁言)"
                    :value="1007"
                    :key="1007"
                  ></el-option>
                </el-select>
              </span>
            </div>
            <div class="list">
              <vxe-table
                :loading="roleAuthLoading"
                ref="table"
                :data="roleAuthList"
                auto-resize
                stripe
              >
                <vxe-table-column min-width="50" field="id" title="ID">
                </vxe-table-column>
                <vxe-table-column
                  min-width="150"
                  field="name"
                  show-overflow
                  :title="$t('m.Auth_Name')"
                >
                </vxe-table-column>
                <vxe-table-column
                  min-width="150"
                  field="permission"
                  show-overflow
                  :title="$t('m.Auth_Permission')"
                >
                </vxe-table-column>
                <vxe-table-column
                  min-width="150"
                  field="gmtCreate"
                  :title="$t('m.Created_Time')"
                >
                  <template v-slot="{ row }">
                    {{ row.gmtCreate | localtime }}
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  min-width="150"
                  field="gmtModified"
                  :title="$t('m.Modified_Time')"
                >
                  <template v-slot="{ row }">
                    {{ row.gmtModified | localtime }}
                  </template>
                </vxe-table-column>
                <vxe-table-column :title="$t('m.Option')" min-width="150">
                  <template v-slot="row">
                    <el-tooltip
                      class="item"
                      effect="dark"
                      :content="$t('m.Delete_Auth')"
                      placement="top"
                    >
                      <el-button
                        icon="el-icon-delete-solid"
                        @click.native="deleteRoleAuth(row.row.id)"
                        size="mini"
                        type="danger"
                      ></el-button>
                    </el-tooltip>
                  </template>
                </vxe-table-column>
              </vxe-table>

              <div class="panel-options">
                <el-pagination
                  v-if="total"
                  class="page"
                  layout="prev, pager, next, sizes"
                  @current-change="roleAuthCurrentChange"
                  :page-size="roleAuthPageSize"
                  :total="roleAuthTotal"
                  @size-change="onRoleAuthPageSizeChange"
                  :page-sizes="[10, 30, 50, 100]"
                >
                </el-pagination>
              </div>
            </div>
          </el-collapse-item>
        </el-card>
      </el-col>
    </el-collapse>
    <el-dialog
      :title="authDialogTitle"
      :visible.sync="showAuthDialog"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form label-width="100px" label-position="left" :model="auth">
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('m.Auth_Name')" required>
              <el-input v-model="auth.name" :placeholder="$t('m.Auth_Name')">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('m.Auth_Permission')" required>
              <el-input
                v-model="auth.permission"
                :placeholder="$t('m.Auth_Permission')"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('m.Status')">
              <el-switch
                :active-value="0"
                :inactive-value="1"
                active-text=""
                inactive-text=""
                v-model="auth.status"
              >
              </el-switch>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="danger" @click.native="showAuthDialog = false">{{
          $t("m.Cancel")
        }}</el-button>
        <el-button type="primary" @click.native="submitAuth">{{
          $t("m.OK")
        }}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('m.Create_Role_Auth')"
      :visible.sync="showRoleAuthDialog"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form label-width="100px" label-position="left" :model="roleAuth">
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('m.Role')" required>
              <el-select v-model="roleAuth.roleId">
                <el-option
                  label="请选择"
                  :value="0"
                  :key="0"
                  disabled
                ></el-option>
                <el-option
                  label="超级管理员"
                  :value="1000"
                  :key="1000"
                ></el-option>
                <el-option
                  label="普通管理员"
                  :value="1001"
                  :key="1001"
                ></el-option>
                <el-option
                  label="题目管理员"
                  :value="1008"
                  :key="1008"
                ></el-option>
                <el-option
                  label="用户(默认)"
                  :value="1002"
                  :key="1002"
                ></el-option>
                <el-option
                  label="用户(禁止提交)"
                  :value="1003"
                  :key="1003"
                ></el-option>
                <el-option
                  label="用户(禁止发讨论)"
                  :value="1004"
                  :key="1004"
                ></el-option>
                <el-option
                  label="用户(禁言)"
                  :value="1005"
                  :key="1005"
                ></el-option>
                <el-option
                  label="用户(禁止提交&禁止发讨论)"
                  :value="1006"
                  :key="1006"
                ></el-option>
                <el-option
                  label="用户(禁止提交&禁言)"
                  :value="1007"
                  :key="1007"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('m.Auth_Id')" required>
              <el-input
                v-model="roleAuth.authId"
                :placeholder="$t('m.Auth_Id')"
                type="number"
              >
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="danger" @click.native="showRoleAuthDialog = false">{{
          $t("m.Cancel")
        }}</el-button>
        <el-button type="primary" @click.native="submitRoleAuth">{{
          $t("m.OK")
        }}</el-button>
      </span>
    </el-dialog>
  </el-row>
</template>

<script>
import api from "@/common/api";
export default {
  name: "Auth",
  data() {
    return {
      activeName: "Auth",
      pageSize: 10,
      roleAuthPageSize: 10,
      total: 0,
      roleAuthTotal: 0,
      mode: "create",
      roleAuthMode: "create",
      showAuthDialog: false,
      showRoleAuthDialog: false,
      authList: [],
      roleAuthList: [],
      roleId: 0,
      auth: {
        id: null,
        name: "",
        permission: "",
        status: 0,
      },
      roleAuth: {
        roleId: null,
        authId: null,
      },
      authDialogTitle: "Edit Auth",
      keyword: "",
      roleAuthKeyword: "",
      loading: false,
      roleAuthLoading: false,
      currentPage: 1,
      roleAuthcurrentPage: 1,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.getAuthList(1);
      this.getRoleAuthList(1);
    },
    // 切换页码回调
    currentChange(page) {
      this.currentPage = page;
      this.getAuthList(page);
    },
    roleAuthCurrentChange(page) {
      this.roleAuthCurrentPage = page;
      this.getRoleAuthList(page);
    },
    onPageSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.getAuthList(this.currentPage);
    },
    onRoleAuthPageSizeChange(pageSize) {
      this.roleAuthPageSize = pageSize;
      this.getRoleAuthList(this.currentPage);
    },
    filterByKeyword() {
      this.currentChange(1);
    },
    filterRoleAuthByKeyword() {
      this.roleAuthCurrentChange(1);
    },
    AuthRoleChangeFilter() {
      this.getRoleAuthList(1);
    },
    getAuthList(page) {
      this.loading = true;
      api.admin_getAuthList(page, this.pageSize, this.keyword).then(
        (res) => {
          this.loading = false;
          this.total = res.data.data.total;
          this.authList = res.data.data.records;
        },
        (res) => {
          this.loading = false;
        }
      );
    },
    submitAuth(data = undefined) {
      let funcName = "";
      if (!data.id) {
        data = this.auth;
      }
      let requestData;
      funcName = this.mode === "edit" ? "admin_updateAuth" : "admin_createAuth";
      requestData = data;
      api[funcName](requestData)
        .then((res) => {
          this.showAuthDialog = false;
          this.$msg.success(this.$i18n.t("m.Successfully"));
          this.getAuthList(1);
        })
        .catch(() => {
          this.showAuthDialog = false;
        });
    },
    deleteAuth(authId) {
      this.$confirm(
        this.$i18n.t("m.Delete_Auth_Tips"),
        this.$i18n.t("m.Warning"),
        {
          confirmButtonText: this.$i18n.t("m.OK"),
          cancelButtonText: this.$i18n.t("m.Cancel"),
          type: "warning",
        }
      )
        .then(() => {
          // then 为确定
          this.loading = true;
          api.admin_deleteAuth(authId).then((res) => {
            this.loading = true;
            this.$msg.success(this.$i18n.t("m.Delete_successfully"));
            this.getAuthList(1);
          });
        })
        .catch(() => {
          // catch 为取消
          this.loading = false;
        });
    },
    openAuthDialog(row) {
      this.showAuthDialog = true;
      if (row !== null) {
        this.authDialogTitle = this.$i18n.t("m.Edit_Auth");
        this.auth = Object.assign({}, row);
        this.mode = "edit";
      } else {
        this.authDialogTitle = this.$i18n.t("m.Create_Auth");
        this.auth.id = null;
        this.auth.name = "";
        this.auth.permission = "";
        this.auth.status = 0;
        this.mode = "create";
      }
    },
    handleStatusSwitch(row) {
      this.mode = "edit";
      this.submitAuth({
        id: row.id,
        name: row.name,
        permission: row.permission,
        status: row.status,
      });
    },
    getRoleAuthList(page) {
      this.roleAuthloading = true;
      api
        .admin_getRoleAuthList(
          page,
          this.roleAuthPageSize,
          this.roleAuthKeyword,
          this.roleId
        )
        .then(
          (res) => {
            this.roleAuthloading = false;
            this.roleAuthTotal = res.data.data.total;
            this.roleAuthList = res.data.data.records;
          },
          (res) => {
            this.roleAuthloading = false;
          }
        );
    },
    submitRoleAuth(data = undefined) {
      if (!data.id) {
        data = this.roleAuth;
      }
      let requestData;
      requestData = data;
      api
        .admin_createRoleAuth(requestData)
        .then((res) => {
          this.showRoleAuthDialog = false;
          this.$msg.success(this.$i18n.t("m.Successfully"));
          this.getRoleAuthList(1);
        })
        .catch(() => {
          this.showRoleAuthDialog = false;
        });
    },
    deleteRoleAuth(authId) {
      this.$confirm(
        this.$i18n.t("m.Delete_Auth_Tips"),
        this.$i18n.t("m.Warning"),
        {
          confirmButtonText: this.$i18n.t("m.OK"),
          cancelButtonText: this.$i18n.t("m.Cancel"),
          type: "warning",
        }
      )
        .then(() => {
          // then 为确定
          this.roleAuthLoading = true;
          api.admin_deleteRoleAuth(authId, this.roleId).then((res) => {
            this.roleAuthLoading = false;
            this.$msg.success(this.$i18n.t("m.Delete_successfully"));
            this.getRoleAuthList(1);
          });
        })
        .catch(() => {
          // catch 为取消
          this.roleAuthLoading = false;
        });
    },
    openRoleAuthDialog(row) {
      this.showRoleAuthDialog = true;
      this.roleAuth.roleId = 0;
      this.roleAuth.authId = null;
    },
  },
  computed: {},
  watch: {},
};
</script>

<style scoped>
/deep/.el-dialog__body {
  padding-bottom: 0;
}
.filter-row {
  margin-bottom: 5px;
}
@media screen and (max-width: 768px) {
  .filter-row span {
    margin-right: 5px;
  }
}
@media screen and (min-width: 768px) {
  .filter-row span {
    margin-right: 20px;
  }
}
</style>
