import Vue from 'vue'

import {
  add,
  addDays,
  differenceInHours,
  differenceInDays,
  eachMonthOfInterval,
  format,
  formatDistance,
  formatDistanceToNow,
  formatDistanceStrict,
  getMonth,
  isAfter,
  isValid,
  isWithinInterval,
  parseISO,
  parse,
  startOfDay,
  sub,
  isToday
} from 'date-fns'

const datefns = {
  add,
  addDays,
  differenceInDays,
  differenceInHours,
  eachMonthOfInterval,
  format,
  formatDistance,
  formatDistanceToNow,
  formatDistanceStrict,
  getMonth,
  isAfter,
  isValid,
  isWithinInterval,
  parseISO,
  parse,
  startOfDay,
  sub,
  isToday
}
Vue.prototype.$datefns = datefns

export default (ctx, inject) => {
  inject('datefns', datefns)
}
