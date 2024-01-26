const {
  defineConfig
} = require('@vue/cli-service')
module.exports = defineConfig({
  //是否在保存文件时执行代码检查
  lintOnSave: true,
  transpileDependencies: true,
  devServer: {
    proxy: {
      "/api": {
        target: "http://192.168.2.21:8085",
        changeOrigin: true,
        pathRewrite: {
          "^/api": "",
        },
      }
    },
  }
});