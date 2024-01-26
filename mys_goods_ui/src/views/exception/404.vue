<template>
  <div>
    <div class="wrap">
      <div style="margin-top: -20px">
        <h1 class="animated swing">404</h1>
        <h4>很抱歉，您所访问的页面不存在。</h4>
        <el-button type="primary" @click="back" style="margin-left: -20px">返回</el-button>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    data() {
      return {};
    },
    methods: {
      back() {
        const token = localStorage.getItem("Authorization");
        const menu = localStorage.getItem("menu");
        if (token == null || token.length < 1 || menu == null || menu.length < 1) {
          this.$router.push("/login");
        } else {
          this.$router.go(-1);
        }
      }
    },
    created() {}
  };
</script>
<style scoped>
  .wrap {
    height: 100vh;
    display: flex;
    justify-content: space-around;
    align-items: center;
    text-align: center;
  }

  .wrap h1 {
    text-align: center;
    font-size: 10em;
    font-family: fantasy;
    color: #475669;
    letter-spacing: 40px;
    margin: 0;
  }

  .wrap h4 {
    color: #666;
  }
</style>
