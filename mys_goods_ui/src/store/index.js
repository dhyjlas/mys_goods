import {
  createStore
} from 'vuex'

export default createStore({
  state() {
    return {
      userInfo: {},
    }
  },
  getters: {},
  mutations: {
    setUserInfo(state, e) {
      state.userInfo = e;
    }
  },
  actions: {},
  modules: {}
})