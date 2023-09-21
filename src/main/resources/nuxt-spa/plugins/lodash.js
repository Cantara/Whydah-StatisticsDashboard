// import lodash from 'lodash'
import merge from "lodash/merge";
import flatten from "lodash/flatten";

const lodash = {
  merge,
  flatten
};

export default defineNuxtPlugin(nuxtApp => {
  nuxtApp.provide('lodash', lodash)
})


