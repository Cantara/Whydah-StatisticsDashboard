import { createStore } from 'vuex'
import apiStore from '~/store/api'
import authStore from '~/store/auth'

const store = createStore({
  modules: {
    api: apiStore,
    auth: authStore
  }
})

export default store;
