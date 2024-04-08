<template>
  <div class="main-page">
    <!-- 标题栏 -->
    <div class="main-title">选择一个商品</div>
    <!-- 搜索栏 -->
    <el-card shadow="never" class="main-search-bar">
      <el-form :inline="true">
        <el-form-item label="账号：">
          {{userInfo.mys_uid?userInfo.mys_uid+'-'+userInfo.nickname:'未选择'}}
          <i @click="selectUser('list')" class="hope-icon-sorting icon-select"></i>
        </el-form-item>
        <el-form-item label="分类：">
          <el-select v-model="game" @change="getTableData">
            <el-option label="崩坏3" value="bh3"></el-option>
            <el-option label="原神" value="hk4e"></el-option>
            <el-option label="崩坏：星穹铁道" value="hkrpg"></el-option>
            <el-option label="崩坏学园2" value="bh2"></el-option>
            <el-option label="未定事件簿" value="nxx"></el-option>
            <el-option label="米游社" value="bbs"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>
    <!-- 内容框 -->
    <el-card shadow="never" class="main-content-bar">
      <!-- 操作栏 -->
      <div class="main-content-operate-bar">
        <div style="font-size: 13px;color: red;">目前仅支持需要抢购且还有库存的商品</div>
        <div style="font-size: 13px;color: red;">当前还有{{userInfo.point}}米游币，添加任务时请注意是否满足兑换条件</div>
        <div style="font-size: 13px;color: red;">选择地址或角色时不要选错，无地址或角色请先去客户端添加</div>
      </div>
      <div v-loading="loading.table">
        <div style="display: inline-block;" v-for="data in tableData" :key="data">
          <el-card shadow="hover" @click="selectData(data)" class="good-card"
            v-if="data.next_num>0 && data.next_time>0">
            <el-image :src="data.icon" lazy></el-image>
            <div style="margin:10px;font-size: 13px;">
              <div>{{data.goods_name}}</div>
              <div>{{data.price}}米游币</div>
              <div style="display: inline-block;">
                库存{{data.next_time > 0 ? data.next_num : data.total}}
              </div>
              <div style="float: right;">限购{{data.account_exchange_num}}/{{data.account_cycle_limit}}</div>
              <div v-if="data.next_time > 0" style="color: #409eff;font-weight: bold;">
                {{ dateFormat(data.next_time * 1000, 'yyyy-MM-dd hh:mm:ss') }}
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
    <UserInfoList ref="userInfoList" @selectUser="selectUser"></UserInfoList>
    <AddExchangeInfo ref="addExchangeInfo"></AddExchangeInfo>
  </div>
</template>

<script>
  import UserInfoList from "@/components/UserInfoList.vue";
  import AddExchangeInfo from "@/components/AddExchangeInfo.vue";
  export default {
    data() {
      return {
        loading: {
          table: "false"
        },
        game: "hkrpg",
        tableData: [],
        userInfo: {},
      };
    },
    components: {
      AddExchangeInfo: AddExchangeInfo,
      UserInfoList: UserInfoList,
    },
    mounted() {
      if (this.$store.state.userInfo && this.$store.state.userInfo !== {} && this.$store.state.userInfo.mys_uid) {
        this.userInfo = this.$store.state.userInfo;
        this.getTableData();
      } else {
        this.$refs.userInfoList.openDialog();
      }
    },
    methods: {
      selectUser(e) {
        if (e === "add") {
          this.$router.push({
            name: "userInfo",
          });
        } else if (e === "list") {
          this.$refs.userInfoList.openDialog();
        } else {
          this.userInfo = e;
          this.$store.commit("setUserInfo", this.userInfo);
          this.getTableData();
        }
      },
      getTableData() {
        this.loading.table = true;
        if (this.userInfo === {} || this.userInfo.mys_uid === null) {
          this.loading.table = false;
          return;
        }
        this.axios
          .post("/goods/list", {
            uid: this.userInfo.mys_uid,
            game: this.game
          })
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
      selectData(e) {
        e.game = this.game;
        this.$refs.addExchangeInfo.openDialog(e, this.userInfo);
      }
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

  .good-card {
    --el-card-padding: 0;
    display: inline-block;
    margin: 0px 10px 10px 0;
    width: 200px;
    height: 310px;
    cursor: pointer;
  }

  .el-image {
    width: 200px;
  }

  .icon-select {
    font-size: 17px;
    cursor: pointer;
    margin-left: 5px;
    transform: rotate(90deg);
  }
</style>