<template>
  <el-container>
    <!-- UI插件语言 -->
    <el-config-provider :locale="locale" />
    <!-- 页面头部 -->
    <el-container>
      <!-- 菜单栏 -->
      <el-aside :class="isCollapse ? 'asideClose' : 'asideOpen'">
        <LayoutMenu :isCollapse="isCollapse" :menuList="menuList" @evntClick="handleClick" :activeIndex="activeIndex"
          class="layoutMenu" />
      </el-aside>
      <!-- 主窗体 -->
      <el-main ref="main">
        <router-view ref="scrollTopBox" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
  import cn from "element-plus/dist/locale/zh-cn";
  import en from "element-plus/dist/locale/en";
  import LayoutMenu from "@/components/Aside.vue";
  export default {
    name: "App",
    data() {
      return {
        locale: this.$i18n.locale === "en" ? en : cn,
        isCollapse: document.body.clientWidth < 1320,
        menuList: [{
          index: "userInfo",
          menuName: "用户列表",
          menuIcon: "hope-icon-usercenter1",
          menuType: "C",
          menuState: 0
        }, {
          index: "addExchange",
          menuName: "新增兑换",
          menuIcon: "hope-icon-add-cart-fill",
          menuType: "C",
          menuState: 0
        }, {
          index: "exchangeInfo",
          menuName: "兑换列表",
          menuIcon: "hope-icon-cart-Empty-fill",
          menuType: "C",
          menuState: 0
        }, ],
        activeIndex: this.$route.meta.activeId,
        activeMenu: {},
        menu: {}
      };
    },
    components: {
      LayoutMenu: LayoutMenu,
    },
    watch: {
      '$route'(to) {
        this.menu = this.foundMenu(this.menuList, to.name);
        this.activeIndex = to.meta.activeId;
      },
      '$i18n.locale'(to) {
        this.locale = to === "en" ? en : cn;
      },
    },
    mounted() {
      this.menu = this.foundMenu(this.menuList, this.activeIndex);
    },
    beforeUnmount() {
      window.onresize = null;
    },
    methods: {
      changeCollapse(e) {
        this.isCollapse = e;
      },
      handleClick(e) {
        const r = this.foundByIndex(this.menuList, e.index);
        if (r.menuTarget == '_blank') {
          window.open(r.menuUrl, '_blank');
        } else {
          this.$router.push({
            name: e.index,
          });
        }
      },
      collapse() {
        this.isCollapse = !this.isCollapse;
      },
      foundMenu(m, acIndex) {
        for (var i1 = 0; i1 < m.length; i1++) {
          var m1 = m[i1];
          var rMenu = this.foundMenuActive(m1.children, m1, acIndex);
          if (rMenu != null) {
            return rMenu;
          }
        }
        return m[0];
      },
      foundMenuActive(menus, pMenu, acIndex) {
        if (menus != null) {
          for (var i = 0; i < menus.length; i++) {
            var sMenu = menus[i];
            if (sMenu.index === acIndex) {
              return pMenu;
            } else {
              var rMenu = this.foundMenuActive(sMenu.children, pMenu, acIndex);
              if (rMenu != null) {
                return rMenu;
              }
            }
          }
        }
        return null;
      },
      foundByIndex(menus, index) {
        for (var i = 0; i < menus.length; i++) {
          const menu = menus[i];
          if (menu.index == index) {
            return menu;
          }
          if (menu.children && menu.children.length > 0) {
            const foundMenu = this.foundByIndex(menu.children, index);
            if (foundMenu != null) {
              return foundMenu;
            }
          }
        }
        return null;
      },
    },
  };
</script>
<style scoped>
  .el-aside {
    height: 100vh;
    background-color: #3d3d3d;
  }

  .el-main {
    background-color: #e9eef3;
    padding: 0px;
    height: 100vh;
  }

  ::-webkit-scrollbar {
    display: none;
    /* Chrome Safari */
  }

  .el-main,
  .el-aside {
    scrollbar-width: none;
    /* firefox */
    -ms-overflow-style: none;
    /* IE 10+ */
  }

  .main-copyright {
    margin-top: 8px;
    text-align: center;
    font-size: 12px;
    color: #666;
    height: 24px;
  }

  .asideClose {
    width: 64px;
    transition: width 0.3s;
  }

  .asideOpen {
    width: 210px;
    transition: width 0.3s;
  }

  .el-header {
    --el-header-height: 58px;
  }
</style>