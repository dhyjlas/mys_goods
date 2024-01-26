<template>
  <el-dialog v-model="dialogVisible" title="新增用户" width="700px" :close-on-click-modal="false" @close="handleClose">
    <el-steps :active="active" align-center>
      <el-step title="输入手机号" />
      <el-step title="第一次输入验证码" />
      <el-step title="第二次输入验证码" />
      <el-step title="完成" />
    </el-steps>

    <el-form label-width="90px">
      <div v-if="active===0">
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="mobile" />
        </el-form-item>
        <el-button type="primary" @click="next0" :loading="loading.confirm">下一步</el-button>
      </div>

      <div v-if="active===1">
        <div class="text">请在浏览器打开
          <a href="https://user.mihoyo.com/#/login/captcha" target="_blank">https://user.mihoyo.com/#/login/captcha</a>
          ，输入手机号({{mobile}})后获取验证码，但不要登录，然后在下方输入获取到的手机验证码
        </div>
        <el-form-item label="验证码">
          <el-input v-model="captcha1" />
        </el-form-item>
        <el-button type="primary" @click="next1" :loading="loading.confirm">下一步</el-button>
      </div>

      <div v-if="active===2">
        <div class="text">重新刷新或打开
          <a href="https://user.mihoyo.com/#/login/captcha" target="_blank">https://user.mihoyo.com/#/login/captcha</a>
          ，再次输入手机号({{mobile}})后获取验证码，依旧不要登录，然后在下方输入获取到的手机验证码
        </div>
        <el-form-item label="验证码">
          <el-input v-model="captcha2" />
        </el-form-item>
        <el-button type="primary" @click="next2" :loading="loading.confirm">下一步</el-button>
      </div>

      <div v-if="active===3">
        <div class="text">用户信息已成功添加
        </div>
        <el-button type="primary" @click="next3">完成</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
  export default {
    data() {
      return {
        loading: {
          confirm: false,
        },
        dialogVisible: false,
        active: 0,
        mobile: "",
        captcha1: "",
        captcha2: "",
      };
    },
    methods: {
      // 关闭窗口
      handleClose() {
        this.$emit("selectUser", "list");
        this.dialogVisible = false;
      },
      // 打开窗口
      openDialog() {
        this.loading.confirm = false;
        this.active = 0;
        this.mobile = "";
        this.captcha1 = "";
        this.captcha2 = "";
        this.dialogVisible = true;
      },
      next0() {
        if (!this.validatePhoneNumber(this.mobile)) {
          this.$message({
            message: "请输入正确的手机号",
            type: "error",
          });
          return;
        }
        this.active++;
      },
      next1() {
        if (this.captcha1 < 4) {
          this.$message({
            message: "请输入正确的验证码",
            type: "error",
          });
          return;
        }
        this.loading.confirm = true;
        this.axios.post("/user/add/1", {
            mobile: this.mobile,
            captcha: this.captcha1
          })
          .then((res) => {
            if (res.data.code === 100) {
              this.active++;
            }
            this.loading.confirm = false;
          })
          .catch(() => {
            this.loading.confirm = false;
          });
      },
      next2() {
        if (this.captcha2 < 4) {
          this.$message({
            message: "请输入正确的验证码",
            type: "error",
          });
          return;
        }
        this.loading.confirm = true;
        this.axios.post("/user/add/2", {
            mobile: this.mobile,
            captcha: this.captcha2
          })
          .then((res) => {
            if (res.data.code === 100) {
              this.active++;
            }
            this.loading.confirm = false;
          })
          .catch(() => {
            this.loading.confirm = false;
          });
      },
      next3() {
        this.handleClose();
      },
      validatePhoneNumber(phone) {
        // 定义手机号的正则表达式规则
        var regExp = /^1[3456789]\d{9}$/;

        if (regExp.test(phone)) {
          console.log("手机号格式正确");
          return true;
        } else {
          console.log("手机号格式不正确");
          return false;
        }
      }
    },
  };
</script>
<style scoped>
  .el-form {
    margin-top: 30px;
  }

  .el-button {
    width: 100%;
  }

  .text {
    font-weight: bold;
    margin: 10px 10px 20px 7px;
  }
</style>