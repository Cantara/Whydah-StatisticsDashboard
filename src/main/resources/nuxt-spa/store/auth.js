export default {
  name: "auth",
  namespaced: true,
  state() {
    return {
      authToken: ''
    }
  },
  mutations: {
    setToken(state, token) {
      state.authToken = token;
    },
  }
}