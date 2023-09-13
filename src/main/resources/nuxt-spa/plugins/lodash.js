// import Vue from 'vue'
// import VueLodash from 'vue-lodash'
import lodash from 'lodash'
// Vue.use(VueLodash, { name: '_lodash' ,  lodash })

// export default (ctx, inject) => {
//   inject('lodash',lodash)
// }

export default defineNuxtPlugin(nuxtApp => {
  nuxtApp.provide('lodash', () => lodash)
  // nuxtApp.vueApp.use(lodash);
})
