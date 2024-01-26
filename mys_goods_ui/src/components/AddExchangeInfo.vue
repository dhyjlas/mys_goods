<template>
  <el-dialog v-model="dialogVisible" title="添加兑换任务" width="600px" @close="handleClose" :draggable="true">
    <el-form label-width="90px">
      <el-row>
        <el-col :span="24">
          <el-form-item label="账号UID：">
            {{userInfo.mys_uid}}
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="商品名称：">
            {{goodsInfo.goods_name}}
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="goodsInfo.type === 1 || goodsInfo.type === 4">
          <el-form-item label="配送地址：">
            <el-select v-model="address">
              <el-option v-for="info in userInfo.address_list" :key="info"
                :label="info.province_name + info.city_name + info.county_name + info.addr_ext + '-' + info.connect_name"
                :value="info.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="goodsInfo.game !== 'bbs' && goodsInfo.type === 2">
          <el-form-item label="选择角色：">
            <el-select v-model="role">
              <el-option v-for="info in gameList" :key="info"
                :label="info.game_biz + '-' + info.region_name + '-' + info.nickname + '-' + info.level + '级'"
                :value="info.game_uid+'-'+info.region"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="线程数：">
            <el-select v-model="thread">
              <el-option :label="1" :value="1"></el-option>
              <el-option :label="2" :value="2"></el-option>
              <el-option :label="3" :value="3"></el-option>
              <el-option :label="4" :value="4"></el-option>
              <el-option :label="5" :value="5"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="重试次数：">
            <el-select v-model="retry">
              <el-option :label="1" :value="1"></el-option>
              <el-option :label="2" :value="2"></el-option>
              <el-option :label="3" :value="3"></el-option>
              <el-option :label="4" :value="4"></el-option>
              <el-option :label="5" :value="5"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <!-- 取消按钮 -->
      <el-button @click="handleClose">取消</el-button>
      <!-- 提交按钮 -->
      <el-button type="primary" @click="handleConfirm" :loading="loading.confirm">提交</el-button>
    </template>

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
        address: "",
        role: "",
        goodsInfo: {},
        userInfo: {},
        thread: 1,
        retry: 3,
        gameList: []
      };
    },
    methods: {
      // 关闭窗口
      handleClose() {
        this.dialogVisible = false;
      },
      // 打开窗口
      openDialog(e1, e2) {
        this.dialogVisible = true;
        this.loading.confirm = false;
        this.goodsInfo = e1;
        this.userInfo = e2;
        this.gameList = [];
        if (this.userInfo.game_list.length > 0) {
          this.userInfo.game_list.forEach(e => {
            if (e.game_biz.startsWith(this.goodsInfo.game + '_')) {
              this.gameList.push(e);
            }
          });
        }
        this.address = this.userInfo.address_list.length > 0 ? this.userInfo.address_list[0].id : "";
        this.role = this.gameList.length > 0 ?
          this.gameList[0].game_uid + "-" + this.gameList[0].region : "";
        this.thread = 1;
        this.retry = 3;
      },
      handleConfirm() {
        this.loading.confirm = true;
        let data = {
          mys_uid: this.userInfo.mys_uid,
          goods_id: this.goodsInfo.goods_id,
          thread: this.thread,
          retry: this.retry,
        };
        if (this.goodsInfo.type === 1 || this.goodsInfo.type === 4) {
          data.address_id = this.address;
        }
        if (this.goodsInfo.game !== 'bbs' && this.goodsInfo.type === 2) {
          let str = this.role.split("-")
          data.game_uid = str[0];
          data.region = str[1];
        }
        this.axios.post("/exchange/add", data)
          .then((res) => {
            if (res.data.code === 100) {
              this.handleClose();
            }
            this.loading.confirm = false;
          })
          .catch(() => {
            this.loading.confirm = false;
          });

      }
    },
  };
</script>
<style scoped>
  .el-form {
    margin-top: 30px;
  }
</style>