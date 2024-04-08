<template>
  <div class="main-page">
    <!-- 标题栏 -->
    <div class="main-title">小说</div>
    <!-- 搜索栏 -->
    <el-card shadow="hover" class="main-search-bar">
      <el-form :model="searchData" :inline="true">
        <el-form-item label="书名">
          <el-input v-model="searchData.bookName" clearable></el-input>
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
        <el-table-column prop="bookName" label="书名" />
        <el-table-column prop="chapterUrl" label="站点地址" />
        <el-table-column prop="chapterNum" label="章节数" />
        <el-table-column label="获取状态">
          <template #default="scope">
            <div v-if="scope.row.progress == null || scope.row.progress == 0">未开始</div>
            <div v-else>已获取{{scope.row.progress}}章</div>
          </template>
        </el-table-column>
        <!-- 操作栏 -->
        <!-- <el-table-column :label="$t('tableTest.operate')" align="center" fixed="right">
          <template #default="scope">
            <el-link @click="handlePreview(scope.row)" type="primary">预览</el-link>
            <el-link @click="handleBegin(scope.row)" type="primary">更新章节</el-link>
            <el-link @click="handleDownload(scope.row)" type="primary">下载TXT</el-link>
            <el-link @click="handleUpdate(scope.row)" type="primary">{{$t('button.update')}}</el-link>
            <el-link @click="handleDelete(scope.row)" type="primary">{{$t('button.delete')}}</el-link>
            <el-link @click="handleClearUpdate(scope.row)" type="primary">重置更新</el-link>
          </template>
        </el-table-column> -->
        <el-table-column :label="$t('tableTest.operate')" min-width="120" align="center" fixed="right">
          <template #default="scope">
            <!-- 详情按钮 -->
            <el-link @click="handleBegin(scope.row)" type="primary">更新章节</el-link>
            <el-link @click="handleDownload(scope.row)" type="primary">下载TXT</el-link>
            <el-dropdown>
              <el-link type="primary">{{$t('button.more')}}</el-link>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handlePreview(scope.row)">预览</el-dropdown-item>
                  <el-dropdown-item @click="handleClearUpdate(scope.row)">重置更新</el-dropdown-item>
                  <el-dropdown-item @click="handleUpdate(scope.row)">{{$t('button.update')}}</el-dropdown-item>
                  <el-dropdown-item @click="handleDelete(scope.row)">{{$t('button.delete')}}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 新增修改组件 -->
    <SaveBook ref="saveBook" @onSuccess="getTableData"></SaveBook>
    <!-- 章节组件 -->
    <Chapter ref="chapter"></Chapter>
    <!-- 下载页组件 -->
    <Download ref="download"></Download>
  </div>
</template>

<script>
  import SaveBook from "./SaveBook.vue";
  import Chapter from "./Chapter.vue";
  import Download from "./Download.vue";
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
      SaveBook: SaveBook,
      // 预览组件
      Chapter: Chapter,
      // 下载页组件
      Download: Download
    },
    mounted() {
      this.getTableData();
    },
    methods: {
      // 预览
      handlePreview(e) {
        this.$refs.chapter.openDialog({
          bookName: e.bookName,
          chapterUrl: e.chapterUrl,
          chapterPath: e.chapterPath,
          contentPath: e.contentPath,
          header: e.header,
          replace: e.replace,
          fileName: e.fileName,
        });
      },
      // 点击新增按钮
      handleInsert() {
        this.$refs.saveBook.openDialog({});
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
      handleClearUpdate(e) {
        this.$confirm("该操作会使更新章节从头开始，是否确认", this.$t("tips.warning"), {
          type: "warning",
        }).then(() => {
          this.$notify({
            message: "正在重置，请等待",
            type: "warning",
          })
          this.clearUpdate(e)
        }).catch(e => e);
      },
      handleUpdate(e) {
        this.$refs.saveBook.openDialog({
          bookName: e.bookName,
          chapterUrl: e.chapterUrl,
          chapterPath: e.chapterPath,
          contentPath: e.contentPath,
          header: e.header,
          replace: e.replace,
          fileName: e.fileName,
        });
      },
      handleClear() {
        this.searchData.host = "";
        this.getTableData();
      },
      // 从后台获取数据
      getTableData() {
        this.loading.table = true;
        this.axios
          .post("/book/list", this.searchData)
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
        this.axios.post("/book/delete", {
            fileName: e.fileName
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
      clearUpdate(e) {
        this.axios.post("/book/clear", {
            fileName: e.fileName
          })
          .then((res) => {
            if (res.data.code === 100) {
              this.getTableData();
              this.$notify({
                message: "重置成功",
                type: "success",
              })
            }
          })
      },
      // 开始爬取
      handleBegin(e) {
        this.axios.post("/book/begin", e)
          .then((res) => {
            if (res.data.code === 100) {
              this.$notify({
                message: "开始更新章节",
                type: "success",
              })
            }
          })
      },
      handleDownload(e) {
        this.axios.post("/book/isDownload", e)
          .then((res) => {
            if (res.data.code === 100) {
              const baseUrl = process.env.NODE_ENV == "development" ? "/api" : "";
              const fileUrl = baseUrl + "/book/download/" + e.bookName
              // window.open(fileUrl);
              this.$refs.download.openDialog(fileUrl);
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