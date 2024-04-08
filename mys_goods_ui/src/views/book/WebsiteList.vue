<template>
  <div class="main-page">
    <!-- 标题栏 -->
    <div class="main-title">站点配置</div>
    <!-- 搜索栏 -->
    <el-card shadow="hover" class="main-search-bar">
      <el-form :model="searchData" :inline="true">
        <el-form-item label="域名">
          <el-input v-model="searchData.host" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <!-- 查询按钮 -->
          <el-button type="primary" @click="getTableData" :loading="loading.table">
            <i class="hope-icon-search" v-if="!loading.table" />{{ $t("button.query") }}
          </el-button>
          <!-- 重置按钮 -->
          <el-button @click="handleClear"><i class="hope-icon-exchangerate" />{{ $t("button.clear") }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
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
        <el-table-column prop="host" label="域名" />
        <el-table-column prop="bookNamePath" label="书名爬取规则" />
        <el-table-column prop="chapterPath" label="章节爬取规则" />
        <el-table-column prop="contentPath" label="正文爬取规则" />
        <!-- 操作栏 -->
        <el-table-column :label="$t('tableTest.operate')" align="center" fixed="right">
          <template #default="scope">
            <el-link @click="handleUpdate(scope.row)" type="primary">{{$t('button.update')}}</el-link>
            <el-link @click="handleDelete(scope.row)" type="primary">{{$t('button.delete')}}</el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 新增修改组件 -->
    <SaveWebsite ref="saveWebsite" @onSuccess="getTableData"></SaveWebsite>
  </div>
</template>

<script>
  import SaveWebsite from "./SaveWebsite.vue";
  export default {
    data() {
      return {
        // 加载状态
        loading: {
          table: false
        },
        // 表格数据
        tableData: [],
        // 查询时需要用到的参数
        searchData: {
          host: ""
        },
      };
    },
    components: {
      // 新增修改组件
      SaveWebsite: SaveWebsite
    },
    mounted() {
      this.getTableData();
    },
    methods: {
      // 点击新增按钮
      handleInsert() {
        this.$refs.saveWebsite.openDialog({});
      },
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
      handleUpdate(e) {
        this.$refs.saveWebsite.openDialog({
          host: e.host,
          bookNamePath: e.bookNamePath,
          chapterPath: e.chapterPath,
          contentPath: e.contentPath,
          header: e.header,
          replace: e.replace
        });
      },
      handleClear(){
        this.searchData.host = "";
        this.getTableData();
      },      
      // 从后台获取数据
      getTableData() {
        this.loading.table = true;
        this.axios
          .post("/website/list", this.searchData)
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
        this.axios.post("/website/delete", {
            host: e.host
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