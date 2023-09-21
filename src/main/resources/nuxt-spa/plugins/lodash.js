// import lodash from 'lodash'
import merge from "lodash/merge";
import flatten from "lodash/flatten";
import join from "lodash/join";

const lodash = {
  merge,
  flatten,
  join
};

export default defineNuxtPlugin(nuxtApp => {
  nuxtApp.provide('lodash', lodash)
})


