<template>
  <div>
    <!-- 章节窗口 -->
    <el-dialog v-model="dialogVisible" :title="chapter.bookName" :width="width" :before-close="handleClose"
      destroy-on-close>
      <el-table :data="tableData" v-loading="loading.table">
        <el-table-column>
          <template #default="scope">
            <el-link @click="handleContent(scope.row)">{{scope.row.value}}</el-link>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <!-- 关闭按钮 -->
        <el-button @click="handleClose">关闭</el-button>
      </template>
    </el-dialog>
    <!-- 内容组件 -->
    <Content ref="content"></Content>
  </div>
</template>

<script>
  import Content from "./Content.vue";
  export default {
    data() {
      return {
        // 是否显示新增修改窗口
        dialogVisible: false,
        // 加载状态
        loading: {
          table: false,
        },
        // 表格数据
        tableData: [],
        chapter: {},
        width: document.body.clientWidth < 660 ? "100%" : "660px",
      };
    },
    components: {
      // 内容组件
      Content: Content
    },
    methods: {
      handleContent(e) {
        this.$refs.content.openDialog({
          bookName: this.chapter.bookName,
          chapterUrl: this.chapter.chapterUrl,
          chapterPath: this.chapter.chapterPath,
          contentPath: this.chapter.contentPath,
          header: this.chapter.header,
          replace: this.chapter.replace,
          fileName: this.chapter.fileName,
          contentUrl: e.key,
          chapterName: e.value
        });
      },
      // 从后台获取数据
      getTableData(e) {
        this.loading.table = true;
        this.axios
          .post("/book/chapter", e)
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
      // 关闭新增修改窗口
      handleClose() {
        this.dialogVisible = false;
      },
      // 打开新增修改窗口
      openDialog(e) {
        this.tableData = [];
        this.dialogVisible = true;
        this.chapter = e;
        this.getTableData(e);
      },
    },
  };
</script>
<style scoped>
  .label-value-body {
    width: 100%;
  }

  .label-value {
    width: 45%;
  }

  .icon {
    font-size: 22px;
  }

  :deep(.el-table__header-wrapper) {
    display: none;
  }
</style>