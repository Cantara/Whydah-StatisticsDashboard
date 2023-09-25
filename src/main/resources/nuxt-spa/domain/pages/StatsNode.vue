<template>
  <div
    class="has-text-white has-text-center has-background-dark-grey item p-4 is-flex is-flex-direction-column"
  >
    <div class="is-flex is-flex-direction-column mb-2">
      <div class="is-flex is-align-items-center is-justify-content-space-between">
        <span class="is-size-4">
          {{ getTheDay() }}
        </span>
        <div
          class="p-2 more-menu"
        >
          <span class="icon">
            <font-awesome-icon icon="fas fa-ellipsis" />
          </span>
        </div>
      </div>
      <span
        class="is-size-7 has-text-grey-light"
      >
        Last updated: {{ getLastUpdated() }}
      </span>
    </div>

    <StatsRow
      label="Total registered users:"
      :value="stats.userSessionStatus.total_number_of_users"
      icon-class="fa-users"
    />
    <StatsRow
      label="Total applications:"
      :value="stats.userSessionStatus.total_number_of_applications"
      icon-class="fa-folder"
    />
    <StatsRow
      label="Total user session activities"
      :value="stats.userSessionStatus.total_number_of_session_actions_this_day"
      icon-class="fa-user"
    />
    <highchart
      id="stats-node-chart"
      class="hc"
      :more="true"
      :options="chartOptions"
    />

    <!-- <div class="tags"> -->
    <!--   <span class="tag is-info"> -->
    <!--     {{ stats.userSessionStatus.number_of_registered_users_this_day }} new users registered -->
    <!--   </span> -->
    <!--   <span class="tag is-success"> -->
    <!--     {{ stats.userSessionStatus.number_of_unique_logins_this_day }} unique new logins -->
    <!--   </span> -->
    <!--   <span class="tag is-danger"> -->
    <!--     {{ stats.userSessionStatus.number_of_deleted_users_this_day }} users deleted -->
    <!--   </span> -->
    <!--   <span class="tag is-warning"> -->
    <!--     {{ stats.userSessionStatus.number_of_active_user_sessions }} active user sessions -->
    <!--   </span> -->
    <!-- </div> -->

    <!-- <div class="is-flex is-flex-direction-row is-flex-wrap-wrap"> -->
    <!--   <div -->
    <!--     v-for="p in stats.userApplicationStatistics" -->
    <!--     :key="p.last_updated" -->
    <!--     class="is-flex is-flex-direction-column app-item" -->
    <!--   > -->
    <!--     <span class="is-size-6 has-text-weight-semibold pl-3 pr-3 pt-1 pb-1"> -->
    <!--       appid - {{ p.for_application }} -->
    <!--     </span> -->
    <!--     <span class="has-background-info is-small pl-2 pr-2"> -->
    <!--       {{ p.number_of_registered_users_this_day }} new users registered today -->
    <!--     </span> -->
    <!--     <span class="has-background-success is-small pl-2 pr-2"> -->
    <!--       {{ p.number_of_unique_logins_this_day }} unique user logins today -->
    <!--     </span> -->
    <!--     <span class="has-background-danger is-small pl-2 pr-2"> -->
    <!--       {{ p.number_of_deleted_users_this_day }} users deleted -->
    <!--     </span> -->
    <!--   </div> -->
    <!-- </div> -->
  </div>
</template>

<script>

// import { runInThisContext } from "vm";
import { mapState, mapActions, mapMutations } from 'vuex'; // eslint-disable-line
import toaster from "@/mixins/toaster";
import StatsRow from "~/domain/pages/StatsRow.vue";
// import { parseISO, compareAsc, isToday } from "date-fns";

export default {
  components: {
    StatsRow
  },
  mixins: [toaster],
  props: {
    stats: {
      type: Object,
      default: () => ({})
    },
    ids: {
      required: true,
      type: Array,
    }
  },
  data() {
    return {};
  },
  computed: {
    categories() {
      return ["New users", "Logins", "Deleted"]
    },
    chartOptions() {
      return {
        chart: {
          type: "column",
          styledMode: true,
        },

        tooltip: {
          formatter: function () {
            return `<b>
              ${this.x} - ${this.point.appId}
              </b></br>${this.series.name}: ${this.y}
              <br/>Total: ${this.point.stackTotal}`
          }
        },
        title: {
          text: ""
        },
        xAxis: {
          // tickmarkPlacement: "on",
          type: "category",
          categories: this.categories,
        },
        series: this.getSeries(),
        credits: false,
        yAxis: {
        },

        legend: {
          enabled: false,
        },
        plotOptions: {
          series: {
            stacking: 'normal',
            shadow: false,
            groupPadding: 0,
            // pointPlacement: 'on'
          },
          column: {
            grouping: false,
          }
        },
      }
    },
  },
  mounted() {
    // console.log(this.categories)
    // console.log("series: ", this.getSeries())
    // console.log("in stats: : ", this.stats)
  },
  methods: {

    getSeries() {
      const series = this.ids.map((id) => {
        return this.getSeriesDataForAppId(id);
      })
      // console.log("final series: ", this.$lodash.flatten(series))
      return this.$lodash.flatten(series)
    },
    getSeriesDataForAppId(appId) {
      const result = [];
      result.push({ "name": "New users", "data": [], stack: "New users"});
      result.push({ "name": "Logins", "data": [], stack: "Logins"});
      result.push({ "name": "Deleted", "data": [], stack: "Deleted users"});
      // if (idx === 0) {
      //   result[0].id = "users"
      //   result[1].id = "logins"
      //   result[2].id = "deleted"
      // } else {
      //   result[0].linkedTo = "users"
      //   result[1].linkedTo = "logins"
      //   result[2].linkedTo = "deleted"
      // }
      const target = this.stats.userApplicationStatistics;
      if (target) {
        target.forEach(x => {
          if (x.for_application === appId) {
            result[0].data.push({ name: "New users", y: x.number_of_registered_users_this_day, appId})
            result[1].data.push({ name: "Logins", y: x.number_of_unique_logins_this_day, appId })
            result[2].data.push({ name: "Deleted", y: x.number_of_deleted_users_this_day, appId })
          }
        })
      }

      return result
    },
    getLastUpdated(){
      const d = this.stats.userSessionStatus.last_updated
      if(this.dateIsValid(d)) {
        const parsed = this.$datefns.parseISO(d);
        return this.$datefns.format(parsed, "HH:mm")
      } else {
        console.error('invalid date format value=' + this.stats.userSessionStatus.last_updated);
        return 'N/A';
      }
    },
    dateIsValid(date) {
      return date && !Number.isNaN(new Date(date).getTime());
    },
    getTheDay() {
      const d = this.stats.userSessionStatus.starttime_of_this_day;
      if(this.dateIsValid(d)) {
        const parsed = this.$datefns.parseISO(d);
        if (this.$datefns.isToday(parsed)) {
          return 'Today';
        } else {
          return this.$datefns.format(parsed, "EEE, LLL dd, yyyy")
        }
      } else {
        console.error('invalid date value=' + this.stats.userSessionStatus.starttime_of_this_day);
        return 'N/A';
      }
    }

  },

};
</script>

<style lang="scss" scoped>

@import "~/assets/styles/_colors.module.scss";
@import 'bulma/sass/utilities/derived-variables.sass';

.item {
  border-radius: 1rem;
}

.item:hover {
  opacity: 1;
  transition: .9s;
}

.more-menu {
  background: darken($color-dark-grey, 3);
  border-radius: 50%;
  border: 2px solid transparent;
  transition: all 100ms ease-in-out;
}

.more-menu:hover {
  border: 2px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  /* background: darken($color-dark-grey, 5); */
}

.hc {
  height: 300px;
}
</style>

