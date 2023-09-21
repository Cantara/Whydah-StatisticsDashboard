// import lodash from 'lodash'
import merge from "lodash/merge";

console.log(merge)
const lodash = {
  merge
};

export default defineNuxtPlugin(nuxtApp => {
  nuxtApp.provide('lodash', lodash)
})


