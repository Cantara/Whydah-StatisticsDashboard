export default {
  name: "auth",
  namespaced: true,
  state() {
    authToken: '' // eslint-disable-line
  },
  mutations: {
    setToken (state, token) {
      state.authToken = token;
    },
  }
}

