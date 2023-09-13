import store from '~/store/index.js'

export default defineNuxtPlugin(nuxtApp => {
  nuxtApp.vueApp.use(store);
})
