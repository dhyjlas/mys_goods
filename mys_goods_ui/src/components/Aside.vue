<template>
  <div class="menu">
    <!-- :collapse="isCollapse" -->
    <el-menu :default-active="activeIndex" :unique-opened="uniqueOpened"
      :background-color="bgColor" :text-color="textColor" :active-text-color="activeColor">
      <!-- 一级菜单 -->
      <template v-for="menu in menuList" :key="menu">
        <el-menu-item v-if="menu.menuType == 'C' && menu.menuState == 0" :index="menu.index" @click="handleClick">
          <el-icon><i :class="menu.menuIcon"></i></el-icon>
          <span>{{ menu.menuName }}</span>
        </el-menu-item>
        <el-sub-menu v-else-if="menu.menuType == 'M' && menu.menuState == 0" :index="menu.index">
          <template #title>
            <el-icon><i :class="menu.menuIcon"></i></el-icon>
            <span>{{ menu.menuName }}</span>
          </template>
          <!-- 二级菜单 -->
          <template v-for="item in menu.children" :key="item">
            <el-menu-item v-if="item.menuType == 'C' && item.menuState == 0" :index="item.index" @click="handleClick">
              <el-icon><i :class="item.menuIcon"></i></el-icon>
              <span>{{ item.menuName }}</span>
            </el-menu-item>
            <el-sub-menu v-else-if="item.menuType == 'M' && item.menuState == 0" :index="item.index">
              <template #title>
                <el-icon><i :class="item.menuIcon"></i></el-icon>
                <span>{{ item.menuName }}</span>
              </template>
              <!-- 三级菜单 -->
              <template v-for="zitem in item.children" :key="zitem">
                <el-menu-item :index="zitem.index" @click="handleClick"
                  v-if="zitem.menuType == 'C' && zitem.menuState == 0">
                  <el-icon><i :class="zitem.menuIcon"></i></el-icon>
                  <span>{{ zitem.menuName }}</span>
                </el-menu-item>
              </template>
            </el-sub-menu>
          </template>
        </el-sub-menu>
      </template>
    </el-menu>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        textColor: "#fff",
        bgColor: "#3d3d3d",
        activeColor: "#ffd04b"
      };
    },
    props: {
      isCollapse: {
        type: Boolean,
      },
      menuList: {
        type: Array,
      },
      activeIndex: {
        type: String,
        default: null,
      },
      uniqueOpened: {
        type: Boolean,
        default: true,
      },
    },
    methods: {
      handleClick(e) {
        this.$emit("evntClick", e);
      },
    },
  };
</script>
<style>
  .menu .el-menu {
    border-right: solid 0px !important;
  }

  .menu .el-menu-item.is-active {
    background-color: #323232;
    /* border-left: 3px solid var(--el-menu-active-color); */
  }
</style>