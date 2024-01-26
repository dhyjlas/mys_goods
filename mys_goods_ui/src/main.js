import {
  createApp
} from "vue"
import App from "./App.vue"
import ElementPlus from "element-plus";
import "element-plus/dist/index.css"
import "./assets/css/element-variables.css";
import "./assets/css/common.css";
import router from "./router";
import store from "./store";
import axios from "./plugins/axios";
import dateFormat from "./plugins/dateFormat";
import i18n from "./lang";
import "./assets/iconfont/iconfont.css";

import {
  verify
} from "@/plugins/authority"

const app = createApp(App);
app.use(ElementPlus).use(store).use(router).use(i18n).mount("#app");
app.config.globalProperties.axios = axios;
app.config.globalProperties.verify = verify;
app.config.globalProperties.dateFormat = dateFormat;
