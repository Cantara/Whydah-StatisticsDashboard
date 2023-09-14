import lodash from 'lodash'

export default defineNuxtPlugin(nuxtApp => {
  nuxtApp.provide('lodash', () => lodash)
})
