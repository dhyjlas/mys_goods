<template>
  <!-- 内容窗口 -->
  <el-dialog v-model="dialogVisible" :title="chapter.chapterName" :width="width" :before-close="handleClose"
    destroy-on-close>
    <div style="white-space: pre-wrap;">
      {{content}}
    </div>
    <template #footer>
      <!-- 关闭按钮 -->
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script>
  export default {
    data() {
      return {
        // 是否显示新增修改窗口
        dialogVisible: false,
        // 加载状态
        loading: {
          content: false,
        },
        content: "",
        chapter: {},
        width: document.body.clientWidth < 860 ? "100%" : "860px",
      };
    },
    methods: {
      // 从后台获取数据
      getContent(e) {
        this.loading.content = true;
        this.axios
          .post("/book/content", e)
          .then((res) => {
            if (res.data.code === 100) {
              this.content = res.data.data;
            }
            this.loading.content = false;
          })
          .catch(() => {
            this.loading.content = false;
          });
      },
      // 关闭新增修改窗口
      handleClose() {
        this.dialogVisible = false;
      },
      // 打开新增修改窗口
      openDialog(e) {
        this.chapter = e;
        this.content = "";
        this.dialogVisible = true;
        this.getContent(e);
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