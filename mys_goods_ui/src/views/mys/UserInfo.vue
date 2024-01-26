<template>
  <div class="main-page">
    <!-- 标题栏 -->
    <div class="main-title">用户列表</div>
    <!-- 内容框 -->
    <el-card shadow="never" class="main-content-bar">
      <!-- 操作栏 -->
      <div class="main-content-operate-bar">
        <!-- 新增按钮 -->
        <el-button type="success" @click="handleInsert">
          <i class="hope-icon-add" />{{$t("button.add")}}
        </el-button>
      </div>
      <!-- 表格栏 -->
      <el-table :data="tableData" v-loading="loading.table">
        <el-table-column type="expand">
          <template #default="props">
            <div>游戏角色列表：</div>
            <div v-for="game in props.row.game_list" :key="game" class="game-card">
              <el-card shadow="never">
                <div>游戏名：{{game.game_biz}}</div>
                <div>服务器：{{game.region_name}}</div>
                <div>游戏ID：{{game.game_uid}}</div>
                <div>角色名：{{game.nickname}}</div>
                <div>等级：{{game.level}}</div>
              </el-card>
            </div>
            <div>配送地址列表：</div>
            <div v-if="props.row.address_list===null || props.row.address_list.length<1" class="game-card">
              <el-card shadow="never">请先去米游社完善信息</el-card>
            </div>
            <div v-for="game in props.row.address_list" :key="game" class="game-card">
              <el-card shadow="never">
                <div>{{game.province_name}}{{game.city_name}}{{game.county_name}}</div>
                <div>{{game.addr_ext}}</div>
                <div>{{game.connect_areacode}} {{game.connect_mobile}}</div>
                <div>{{game.connect_name}}</div>
              </el-card>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="mys_uid" label="账号UID" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="point" label="米游币" />
        <!-- 操作栏 -->
        <el-table-column :label="$t('tableTest.operate')" align="center" fixed="right">
          <template #default="scope">
            <el-link @click="handleRefresh(scope.row)" type="primary">{{$t('button.refresh')}}</el-link>
            <el-link @click="handleDelete(scope.row)" type="primary">{{$t('button.delete')}}</el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <AddUserInfo ref="addUserInfo" @selectUser="finish"></AddUserInfo>
  </div>
</template>

<script>
  import AddUserInfo from "@/components/AddUserInfo.vue";
  export default {
    data() {
      return {
        // 加载状态
        loading: {
          table: false
        },
        // 表格数据
        tableData: [],
      };
    },
    components: {
      AddUserInfo: AddUserInfo,
    },
    mounted() {
      // 在模板渲染成html后，向后台请求表格数据
      this.getTableData();
    },
    methods: {
      // 点击新增按钮
      handleInsert() {
        this.$refs.addUserInfo.openDialog();
      },
      finish() {
        this.getTableData();
      },
      // 点击删除按钮
      handleDelete(e) {
        this.$confirm(this.$t("tips.delete"), this.$t("tips.warning"), {
          type: "warning",
        }).then(() => {
          this.$notify({
            message: "正在删除，请等待",
            type: "warning",
          });
          this.delete(e)
        }).catch(e => e);
      },
      // 点击刷新按钮
      handleRefresh(e) {
        this.$notify({
          message: "正在刷新，请等待",
          type: "warning",
        });
        this.axios
          .post("/user/update", {
            uid: e.mys_uid
          })
          .then((res) => {
            if (res.data.code === 100) {
              this.getTableData();
              this.$notify({
                message: "用户信息已刷新",
                type: "success",
              });
            }
          })
      },
      // 从后台获取数据
      getTableData() {
        this.loading.table = true;
        this.axios
          .post("/user/list", {})
          .then((res) => {
            if (res.data.code === 100) {
              this.tableData = res.data.data;
            }
            this.loading.table = false;
          })
          .catch(() => {
            this.loading.table = false;
          });
      },
      // 向后台发送删除数据
      delete(e) {
        this.axios.post("/user/delete", {
            uid: e.mys_uid
          })
          .then((res) => {
            if (res.data.code === 100) {
              this.getTableData();
              this.$notify({
                message: "用户信息已删除",
                type: "success",
              });
            }
          })
      },
    },
  };
</script>
<!-- 在该页面中应用的样式 -->
<style scoped>
  .el-col {
    margin-bottom: 10px;
  }

  .el-select,
  .el-input {
    width: 240px;
  }

  .game-card {
    width: 250px;
    display: inline-block;
    margin: 0px 10px 10px 0;
  }

  .el-card {
    --el-card-padding: 20px 20px 20px 20px;
  }
</style>