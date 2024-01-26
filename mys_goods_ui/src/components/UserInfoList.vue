<template>
  <el-dialog v-model="dialogVisible" title="选择用户" width="600px" :close-on-click-modal="false" :show-close="false">
    <el-card shadow="hover" v-for="userInfo in userInfoList" :key="userInfo" @click="selectUser(userInfo)">
      <span style="margin-right: 25px;">UID：{{userInfo.mys_uid}}</span>
      <span style="margin-right: 25px;">昵称：{{userInfo.nickname}}</span>
      <span>米游币：{{userInfo.point}}</span>
      <el-link @click.stop="deleteUser(userInfo)">删除</el-link>
    </el-card>
    <el-card shadow="hover" @click="selectUser('add')">新增账号</el-card>
  </el-dialog>
</template>

<script>
  export default {
    data() {
      return {
        // 是否显示窗口
        dialogVisible: false,
        userInfoList: []
      };
    },
    methods: {
      getUserInfoList() {
        this.axios
          .post("/user/list", {})
          .then((res) => {
            if (res.data.code === 100) {
              this.userInfoList = res.data.data;
            }
          })
      },
      // 打开窗口
      openDialog() {
        this.dialogVisible = true;
        this.getUserInfoList();
      },
      selectUser(e) {
        this.$emit("selectUser", e);
        this.dialogVisible = false;
      },
      deleteUser(e) {
        this.$confirm(this.$t("tips.delete"), this.$t("tips.warning"), {
          type: "warning",
        }).then(() => {
          this.axios.post("/user/delete", {
              uid: e.mys_uid
            })
            .then((res) => {
              if (res.data.code === 100) {
                this.getUserInfoList();
              }
            })
        }).catch(e => e);
      }
    },
  };
</script>
<style scoped>
  .el-card {
    width: 100%;
    margin: 10px 0;
    --el-card-padding: 20px 20px 20px 20px;
  }

  .el-card:hover {
    cursor: pointer;
  }

  .el-link {
    float: right;
  }
</style>