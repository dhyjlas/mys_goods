<template>
  <div class="main-page">
    <!-- 标题栏 -->
    <div class="main-title">兑换信息</div>
    <!-- 内容框 -->
    <el-card shadow="never" class="main-content-bar">
      <!-- 表格栏 -->
      <el-table :data="tableData" v-loading="loading.table">
        <el-table-column prop="id" label="ID" width="140" />
        <el-table-column prop="mys_uid" label="账号UID" />
        <el-table-column prop="goods_id" label="商品ID" />
        <el-table-column prop="goods_name" label="商品名称" width="200" />
        <el-table-column prop="game_biz" label="商品区服" />
        <el-table-column prop="exchange_num" label="兑换数量" />
        <el-table-column prop="game_uid" label="游戏ID" />
        <el-table-column prop="region" label="游戏区服" />
        <el-table-column prop="address_id" label="地址" width="280">
          <template #default="scope">
            <span
              v-if="scope.row.address_id != ''">{{scope.row.addressInfo.province_name}}{{scope.row.addressInfo.city_name}}{{scope.row.addressInfo.county_name}}<br />
              {{scope.row.addressInfo.addr_ext}}<br />
              {{scope.row.addressInfo.connect_areacode}} {{scope.row.addressInfo.connect_mobile}}<br />
              {{scope.row.addressInfo.connect_name}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="exchange_time" label="兑换时间" width="100">
          <template #default="scope">
            {{ dateFormat(scope.row.exchange_time*1000, 'yyyy-MM-dd hh:mm:ss') }}
          </template>
        </el-table-column>
        <el-table-column prop="thread" label="线程数" />
        <el-table-column prop="retry" label="重试次数" />
        <el-table-column prop="status" label="兑换状态" />
        <!-- 操作栏 -->
        <el-table-column :label="$t('tableTest.operate')" align="center" fixed="right">
          <template #default="scope">
            <el-link @click="handleDelete(scope.row)" type="primary">{{$t('button.delete')}}</el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
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
    mounted() {
      this.getTableData();
    },
    methods: {
      // 点击删除按钮
      handleDelete(e) {
        this.$confirm(this.$t("tips.delete"), this.$t("tips.warning"), {
          type: "warning",
        }).then(() => {
          this.$notify({
            message: "正在删除，请等待",
            type: "warning",
          })
          this.delete(e)
        }).catch(e => e);
      },
      // 从后台获取数据
      getTableData() {
        this.loading.table = true;
        this.axios
          .post("/exchange/list", {})
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
        this.axios.post("/exchange/delete", {
            mys_uid: e.mys_uid,
            id: e.id
          })
          .then((res) => {
            if (res.data.code === 100) {
              this.getTableData();
              this.$notify({
                message: "删除成功",
                type: "success",
              })
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