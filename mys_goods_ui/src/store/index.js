import {
  createStore
} from 'vuex'

export default createStore({
  state() {
    return {
      authList: {}
    }
  },
  getters: {},
  mutations: {
    setAuthList(state, e) {
      state.authList = e;
    }
  },
  actions: {},
  modules: {}
})
