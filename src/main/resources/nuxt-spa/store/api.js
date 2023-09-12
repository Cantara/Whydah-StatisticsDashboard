import Crud from "@/store/basestore"
import { createStore } from 'vuex'
const crud = new Crud('')
export default {
  namespaced: true,
  name: "api",
  state () {
    return {
      ...crud.state
    }
  },
  mutations: {
    ...crud.mutations
  },
  actions: {
    ...crud.actions,

    async get_usersession_status({ dispatch, state, commit }, { callbackfunc }) {
      await dispatch('get', { affixPath: '/api/status', key: "get_usersession_status_result" })
      if (state.get_usersession_status_result) {
        if (callbackfunc) {
          callbackfunc(state.get_usersession_status_result);
        }
        commit("SET_REMOVE_KEY", "get_usersession_status_result")
      }
    }
  },
  getters: {
    ...crud.getters
  }
}
