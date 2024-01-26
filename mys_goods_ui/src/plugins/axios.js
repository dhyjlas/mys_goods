"use strict";

import axios from "axios";
import {
  ElMessage
} from "element-plus";
import router from '@/router'

// Full config:  https://github.com/axios/axios#request-config
// axios.defaults.baseURL = process.env.baseURL || process.env.apiUrl || '';
// axios.defaults.headers.common['Authorization'] = AUTH_TOKEN;
// axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

let config = {
  baseURL: process.env.NODE_ENV == "development" ?
    "/api" : "/",
  timeout: 60 * 1000,
  withCredentials: true,
};

const _axios = axios.create(config);

_axios.interceptors.request.use(
  function(config) {
    const token = localStorage.getItem("Authorization");
    if (token) {
      config.headers["Authorization"] = `Bearer ` + token;
    }
    return config;
  },
  function(error) {
    return Promise.reject(error);
  }
);

_axios.interceptors.response.use(
  function(response) {
    if (response.data.code == 902) {
      ElMessage({
        message: "登录信息已过期，请重新登录",
        type: "error",
      });
      localStorage.removeItem("Authorization");
      localStorage.removeItem("userHeader");
      localStorage.removeItem("menu");
      localStorage.removeItem("userTokenTime");
      router.replace('/login');
      return response;
    }
    if (response.data.code != 100) {
      ElMessage({
        message: `${response.data.msg}`,
        type: "error",
      });
    }
    return response;
  },
  function(error) {
    ElMessage({
      message: error,
      type: "error",
    });

    const time = new Date().getTime();
    const tokenTime = localStorage.getItem("userTokenTime");
    if (tokenTime == null || tokenTime == '' || time > Number(tokenTime) + 8 * 60 * 60 * 1000) {
      localStorage.removeItem("Authorization");
      localStorage.removeItem("userHeader");
      localStorage.removeItem("menu");
      localStorage.removeItem("userTokenTime");
      router.replace('/login');
    }

    return Promise.reject(error);
  }
);
export default _axios;
