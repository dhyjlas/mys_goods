<template>
  <div>
    <!-- 新增修改子窗口 -->
    <el-dialog v-model="dialogVisible" :title="form.host ? '修改' : '新增'" :width="width" :before-close="handleClose"
      :close-on-click-modal="false" destroy-on-close :draggable="true">
      <!-- 数据提交表单 -->
      <el-form ref="form" :model="form" label-width="150px" :rules="rules">
        <el-row>
          <el-col :span="24">
            <el-form-item label="小说目录地址：" prop="chapterUrl">
              <el-input v-model="form.chapterUrl" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="isCheck">
            <el-form-item label="书名：" prop="bookName">
              <el-input v-model="form.bookName" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="isCheck">
            <el-form-item label="章节爬取规则：" prop="chapterPath">
              <el-input v-model="form.chapterPath" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="isCheck">
            <el-form-item label="正文爬取规则：" prop="contentPath">
              <el-input v-model="form.contentPath" clearable></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="isCheck">
            <el-form-item label="请求头：" prop="header">
              <div class="label-value-body" v-for="header in form.header" :key="header">
                <el-input type="textarea" :rows="1" class="label-value" v-model="header.key"></el-input> :
                <el-input type="textarea" :rows="1" class="label-value" v-model="header.value"></el-input>
                <el-link :underline="false" @click="deleteHeader(header)" style="margin-left: 6px;">➖</el-link>
              </div>
              <el-link :underline="false" @click="addHeader">➕</el-link>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="isCheck">
            <el-form-item label="正文替换：" prop="replace">
              <div class="label-value-body" v-for="replace in form.replace" :key="replace">
                <el-input type="textarea" :rows="1" class="label-value" v-model="replace.key"></el-input> :
                <el-input type="textarea" :rows="1" class="label-value" v-model="replace.value"></el-input>
                <el-link :underline="false" @click="deleteReplace(replace)" style="margin-left: 6px;">➖</el-link>
              </div>
              <el-link :underline="false" @click="addReplace">➕</el-link>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <!-- 取消按钮 -->
        <el-button @click="handleClose">取消</el-button>
        <!-- 预览按钮 -->
        <el-button @click="handlePreview" :loading="loading.confirm" v-if="isCheck">预览</el-button>
        <!-- 提交按钮 -->
        <el-button type="primary" @click="handleConfirm" :loading="loading.confirm" v-if="isCheck">提交</el-button>
        <!-- 下一步按钮 -->
        <el-button type="primary" @click="checkChapterUrl" :loading="loading.confirm" v-if="!isCheck">下一步</el-button>
      </template>
    </el-dialog>
    <!-- 章节组件 -->
    <Chapter ref="chapter"></Chapter>
  </div>
</template>

<script>
  import Chapter from "./Chapter.vue";
  export default {
    data() {
      return {
        // 是否显示新增修改窗口
        dialogVisible: false,
        isCheck: false,
        // 表单数据
        form: {},
        // 加载状态
        loading: {
          confirm: false,
        },
        // 表单数据规则
        rules: {
          chapterUrl: [{
            required: true,
            message: "请输入小说目录地址",
            trigger: "blur"
          }],
          chapterPath: [{
            required: true,
            message: "请输入章节爬取规则",
            trigger: "blur"
          }],
          contentPath: [{
            required: true,
            message: "请输入正文爬取规则",
            trigger: "blur"
          }],
        },
        width: document.body.clientWidth < 860 ? "100%" : "860px",
      };
    },
    components: {
      // 预览组件
      Chapter: Chapter
    },
    // 定义组件事件
    emits: ["onSuccess"],
    methods: {
      handlePreview(){
        this.$refs.chapter.openDialog({
          bookName: this.form.bookName,
          chapterUrl: this.form.chapterUrl,
          chapterPath: this.form.chapterPath,
          contentPath: this.form.contentPath,
          header: this.form.header,
          replace: this.form.replace,
          fileName: this.form.fileName
        });
      },
      checkChapterUrl() {
        this.loading.confirm = true;
        this.$refs["form"].validate((valid) => {
          if (valid) {
            // 提交数据
            this.axios
              .post("/book/check", this.form)
              .then((res) => {
                if (res.data.code === 100) {
                  this.isCheck = true;
                  if (res.data.data != null) {
                    this.form = res.data.data;
                  } else {
                    this.$notify({
                      message: "未配置地址，请自行添加规则",
                      type: "warning",
                    })
                  }
                }
                this.loading.confirm = false;
              })
              .catch(() => {
                this.loading.confirm = false;
              });
          } else {
            this.loading.confirm = false;
          }
        });
      },
      addHeader() {
        if (this.form.header == null) {
          this.form.header = [];
        }
        this.form.header.push({});
      },
      deleteHeader(e) {
        let index = this.form.header.indexOf(e);
        this.form.header.splice(index, 1);
      },
      addReplace() {
        if (this.form.replace == null) {
          this.form.replace = [];
        }
        this.form.replace.push({});
      },
      deleteReplace(e) {
        let index = this.form.replace.indexOf(e);
        this.form.replace.splice(index, 1);
      },
      // 向后台提交数据
      handleConfirm() {
        this.loading.confirm = true;
        this.$refs["form"].validate((valid) => {
          if (valid) {
            // 提交数据
            this.axios
              .post("/book/save", this.form)
              .then((res) => {
                if (res.data.code === 100) {
                  this.$emit("onSuccess");
                  this.handleClose();
                }
                this.loading.confirm = false;
              })
              .catch(() => {
                this.loading.confirm = false;
              });
          } else {
            this.loading.confirm = false;
          }
        });
      },
      // 关闭新增修改窗口
      handleClose() {
        this.dialogVisible = false;
      },
      // 打开新增修改窗口
      openDialog(e) {
        this.dialogVisible = true;
        this.form = e;
        this.isCheck = e.chapterUrl != null;
        console.log(e.chapterUrl, this.isCheck);
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
</style>