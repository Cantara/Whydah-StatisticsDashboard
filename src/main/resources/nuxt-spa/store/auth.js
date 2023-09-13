export default {
  name: "auth",
  namespaced: true,
  state() {
    authToken: ''
  },
  mutations: {
    setToken (state, token) {
      state.authToken = token;
    },
  }
}

