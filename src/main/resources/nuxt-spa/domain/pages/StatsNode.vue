<template>
  <div class="has-text-white item is-flex is-flex-direction-column">
    <div class="is-flex is-flex-direction-row is-flex-wrap-nowrap is-justify-content-space-between">
      <span class="is-size-5 has-text-weight-semibold">
        {{ getTheDay() }}
      </span>
      <span v-if="getTheDay()=='Today'" class="is-size-7 mt-1 ml-3">Last updated: {{ getLastUpdated() }}</span>
    </div>

    <ul class="a mt-5 mb-5">
      <li>Total number of registered users: {{ stats.userSessionStatus.total_number_of_users }}</li>
      <li>Total number of applications: {{ stats.userSessionStatus.total_number_of_applications }}</li>
      <li>Total number of user session activities: {{ stats.userSessionStatus.total_number_of_session_actions_this_day }}</li>
    </ul>
    <div class="tags">
      <span class="tag is-info"> {{ stats.userSessionStatus.number_of_registered_users_this_day }} new users registered</span>
      <span class="tag is-success"> {{ stats.userSessionStatus.number_of_unique_logins_this_day }} unique new logins</span>
      <span class="tag is-danger"> {{ stats.userSessionStatus.number_of_deleted_users_this_day }} users deleted</span>
      <span class="tag is-warning"> {{ stats.userSessionStatus.number_of_active_user_sessions }} active user sessions</span>
    </div>

    <div class="is-flex is-flex-direction-row is-flex-wrap-wrap">
      <div v-for="p in stats.userApplicationStatistics" :key="p.last_updated" class="is-flex is-flex-direction-column app-item">
        <span class="is-size-6 has-text-weight-semibold pl-3 pr-3 pt-1 pb-1">appid - {{ p.for_application }}</span>
        <span class="has-background-info is-small pl-2 pr-2">{{ p.number_of_registered_users_this_day }} new users registered today</span>
        <span class="has-background-success is-small pl-2 pr-2">{{ p.number_of_unique_logins_this_day }} unique user logins today</span>
        <span class="has-background-danger is-small pl-2 pr-2">{{ p.number_of_deleted_users_this_day }} users deleted</span>
      </div>
    </div>
  </div>
</template>

<script>

// import { runInThisContext } from "vm";
import { mapState, mapActions, mapMutations } from 'vuex'; // eslint-disable-line
import toaster from "@/mixins/toaster";
// import { parseISO, compareAsc, isToday } from "date-fns";

export default {
  mixins: [toaster],
  props: {
    stats: { type: Object, default: () => ({}) },
  },
  data() {
    return {};
  },
  mounted() {
  },
  methods: {
    getLastUpdated(){
      const parsed = this.$datefns.parseISO(this.stats.userSessionStatus.last_updated);
      return this.$datefns.format(parsed, "H:mm")
    },
    getTheDay() {
      const parsed = this.$datefns.parseISO(this.stats.userSessionStatus.starttime_of_this_day);
      if (this.$datefns.isToday(parsed)) {
        return 'Today';
      } else {
        return this.$datefns.format(parsed, "dd MMM yyyy")
      }
    }

  },

};
</script>

<style lang="scss" scoped>
ul.a {
  list-style-type: circle;
  padding-left: 20px;
}

.item {
  width: 400px;
  padding: 5px;
  opacity: 0.8;
  -webkit-border-radius: 1px 1px 1px 1px;
  border-radius: 1px 1px 1px 1px;
  border: 1px solid #fff;
  margin-bottom: 5px;
}

.app-item {
  -webkit-border-radius: 5px 5px 5px 5px;
  border-radius: 5px 5px 5px 5px;
  border: 1px solid #000;
  margin-right: 5px;
}

.item:hover {
  opacity: 1;
  transition: .9s;
}
</style>

