<template>
  <!-- 内容窗口 -->
  <el-dialog v-model="dialogVisible" :width="width" :before-close="handleClose" destroy-on-close>
    <div class="ScanMa down" id="qrCodeUrl"></div>
    <p class="down">请扫描上面二维码下载或者<el-button @click="download">直接下载</el-button></p>
  </el-dialog>
</template>

<script>
  import QRCode from 'qrcodejs2';
  export default {
    data() {
      return {
        // 是否显示新增修改窗口
        dialogVisible: false,
        downloadUrl: "",
        width: document.body.clientWidth < 400 ? "100%" : "400px",
      };
    },
    methods: {
      download(){
        window.open(this.downloadUrl);
      },
      buildQrCode() {
        this.$nextTick(function() {
          document.getElementById("qrCodeUrl").innerHTML = "";
          new QRCode("qrCodeUrl", {
            width: 240,
            height: 240,
            text: window.location.origin + this.downloadUrl,
            colorDark: "#000",
            colorLight: "#fff",
          });
        });
      },
      // 关闭新增修改窗口
      handleClose() {
        this.dialogVisible = false;
      },
      // 打开新增修改窗口
      openDialog(e) {
        this.downloadUrl = e;
        this.dialogVisible = true;
        this.buildQrCode();
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

  .down {
    display: flex;
    justify-content: center;
    align-items: center; /* align vertical */
  }
  .el-button{
    margin-left: 6px;
  }
</style>