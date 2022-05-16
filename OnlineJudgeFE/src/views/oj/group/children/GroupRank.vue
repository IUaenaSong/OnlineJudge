<template>
  <el-card>
    <el-row>
      <el-col>
        <div style="text-align: center">
          <span>
            <el-input
              :placeholder="$t('m.Rank_Search_Placeholder')"
              v-model="keyword"
              @keyup.enter.native="filterByKeyword"
            >
              <el-button
                slot="append"
                icon="el-icon-search"
                class="search-btn"
                @click="filterByKeyword"
              ></el-button>
            </el-input>
          </span>
        </div>
        <div class="swtich-type">
          <el-switch
            v-model="type"
            :active-value="1"
            :inactive-value="0"
            :active-text="$t('m.Group_ACM_Rank_Type')"
            :inactive-text="$t('m.Group_OI_Rank_Type')"
            @change="filterByType"
          >
          </el-switch>
        </div>
      </el-col>
      <el-col>
        <vxe-table
          :data="rankList"
          :loading="loading"
          align="center"
          highlight-hover-row
          :seq-config="{ seqMethod }"
          auto-resize
          style="font-weight: 500"
        >
          <vxe-table-column type="seq" min-width="50"></vxe-table-column>
          <vxe-table-column
            field="username"
            :title="$t('m.User')"
            min-width="200"
            show-overflow
            align="left"
          >
            <template v-slot="{ row }">
              <avatar
                :username="row.username"
                :inline="true"
                :size="25"
                color="#FFF"
                :src="row.avatar"
                class="user-avatar"
              ></avatar>
              <a
                @click="getInfoByUsername(row.uid, row.username)"
                style="color: #2d8cf0"
                >{{ row.username }}</a
              >
              <span style="margin-left: 2px" v-if="row.titleName">
                <el-tag effect="dark" size="small" :color="row.titleColor">
                  {{ row.titleName }}
                </el-tag>
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="nickname"
            :title="$t('m.Nickname')"
            width="160"
          >
            <template v-slot="{ row }">
              <el-tag
                effect="plain"
                size="small"
                v-if="row.nickname"
                :type="nicknameColor(row.nickname)"
              >
                {{ row.nickname }}
              </el-tag>
            </template>
          </vxe-table-column>
          <vxe-table-column field="ac" :title="$t('m.AC')" min-width="80">
            <template v-slot="{ row }">
              <span>
                <a
                  @click="goUserACStatus(row.username)"
                  style="color: rgb(87, 163, 243)"
                  >{{ row.ac }}</a
                >
              </span>
            </template>
          </vxe-table-column>
          <vxe-table-column
            :title="$t('m.Total')"
            min-width="100"
            field="total"
          >
          </vxe-table-column>
          <vxe-table-column :title="$t('m.Score')" min-width="80">
            <template v-slot="{ row }">
              <span>{{ row.score }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column :title="$t('m.Rating')" min-width="80">
            <template v-slot="{ row }">
              <span>{{ getACRate(row.ac, row.total) }}</span>
            </template>
          </vxe-table-column>
        </vxe-table>
        <Pagination
          :total="total"
          :page-size.sync="limit"
          :current.sync="currentPage"
          @on-change="currentChange"
          show-sizer
          @on-page-size-change="onPageSizeChange"
          :layout="'prev, pager, next, sizes'"
        ></Pagination>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
import api from "@/common/api";
import utils from "@/common/utils";
import { mapGetters } from "vuex";
import Avatar from "vue-avatar";
const Pagination = () => import("@/components/oj/common/Pagination");
export default {
  name: "GroupRank",
  components: {
    Pagination,
    Avatar,
  },
  data() {
    return {
      total: 0,
      currentPage: 1,
      limit: 10,
      keyword: "",
      type: 0,
      rankList: [],
      loading: false,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.getGroupRankList();
    },
    onPageSizeChange(pageSize) {
      this.limit = pageSize;
      this.init();
    },
    currentChange(page) {
      this.currentPage = page;
      this.init();
    },
    filterByKeyword() {
      this.currentChange(1);
    },
    filterByType(type) {
      this.type = type;
      this.currentChange(1);
    },
    getGroupRankList() {
      this.loading = true;
      api
        .getGroupRankList(
          this.currentPage,
          this.limit,
          this.$route.params.groupID,
          this.type,
          this.keyword
        )
        .then((res) => {
          this.total = res.data.data.total;
          this.rankList = res.data.data.records;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    seqMethod({ rowIndex }) {
      return this.limit * (this.currentPage - 1) + rowIndex + 1;
    },
    getInfoByUsername(uid, username) {
      this.$router.push({
        path: "/user-home",
        query: { uid, username },
      });
    },
    goUserACStatus(username) {
      this.$router.push({
        path: "/status",
        query: { username, status: 0, gid: this.$route.params.groupID },
      });
    },
    getACRate(ac, total) {
      return utils.getACRate(ac, total);
    },
    nicknameColor(nickname) {
      let typeArr = ["", "success", "info", "danger", "warning"];
      let index = nickname.length % 5;
      return typeArr[index];
    },
  },
  computed: {
    ...mapGetters(["isAuthenticated", "userInfo"]),
  },
  watch: {
    $route(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.init();
      }
    },
  },
};
</script>

<style scoped>
.swtich-type {
  float: right;
}
@media screen and (max-width: 768px) {
  /deep/.el-card__body {
    padding: 0 !important;
  }
  .swtich-type {
    margin-top: 10px;
    margin-bottom: 10px;
    float: none;
    text-align: center;
  }
}
@media screen and (min-width: 768px) {
  .el-input-group {
    width: 50%;
  }
}

@media screen and (min-width: 1050px) {
  .el-input-group {
    width: 30%;
  }
}
</style>
<style>
.user-avatar {
  margin-right: 5px !important;
  vertical-align: middle;
}
.search-btn {
  color: #fff !important;
  background-color: #409eff !important;
  border-color: #409eff !important;
}
</style>