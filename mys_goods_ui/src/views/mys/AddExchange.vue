<template>
  <div class="main-page">
    <!-- 标题栏 -->
    <div class="main-title">选择一个商品</div>
    <!-- 内容框 -->
    <el-card shadow="never" class="main-content-bar">
      <!-- 操作栏 -->
      <div class="main-content-operate-bar">
        <!-- 新增按钮 -->
        <span>选择分类：</span>
        <el-select v-model="game" @change="getTableData">
          <el-option label="崩坏3" value="bh3"></el-option>
          <el-option label="原神" value="hk4e"></el-option>
          <el-option label="崩坏：星穹铁道" value="hkrpg"></el-option>
          <el-option label="崩坏学园2" value="bh2"></el-option>
          <el-option label="未定事件簿" value="nxx"></el-option>
          <el-option label="米游社" value="bbs"></el-option>
        </el-select>
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
    <AddExchangeInfo ref="addExchangeInfo"></AddExchangeInfo>
  </div>
</template>

<script>
  import AddExchangeInfo from "@/components/AddExchangeInfo.vue";
  export default {
    data() {
      return {
        loading: {
          table: "false"
        },
        game: "hkrpg",
        tableData: [],
        userInfo: localStorage.userInfo ? JSON.parse(localStorage.userInfo) : {
          mys_uid: "未选择",
          point: 0
        },
      };
    },
    components: {
      AddExchangeInfo: AddExchangeInfo,
    },
    mounted() {
      this.getTableData();
    },
    methods: {
      getTableData() {
        this.loading.table = true;
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
        this.userInfo=localStorage.userInfo ? JSON.parse(localStorage.userInfo) : {
          mys_uid: "未选择",
          point: 0
        },
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
</style>