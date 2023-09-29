<template>
  <div class="">
    <div
      v-if="env"
      class="is-flex"
    >
      <div
        class="vtitle has-text-white has-text-weight-medium p-2 pt-4 truncate"
      >
        <img
          :src="env.favicon"
          class="my-icon mb-2"
        >
        <span>{{ env.name }}</span>
      </div>
      <div class="p-2 htitle-container has-text-centered has-text-white">
        <div
          class="htitle has-text-white is-flex is-justify-content-center is-align-items-center is-size-5 has-text-weight-medium"
        >
          <img
            :src="env.favicon"
            class="my-icon mr-2"
          >
          <span class="truncate">
            {{ env.name }}
          </span>
        </div>
      </div>
    </div>
    <div
      v-if="status"
      class="has-text-white min-height-full status p-2"
    >
      <!-- {{ logStatus() }} -->
      <div class="columns is-marginless is-1 is-multiline">
        <div class="column is-half is-flex is-flex-direction-column is-full-touch p-2">
          <highchart
            id="line-chart"
            class="hc border-radius mb-4"
            :options="chartOptions"
          />
          <highchart
            id="column-chart"
            class="hc border-radius"
            :options="getChartOptions()"
          />
        </div>
        <div class="column is-half is-full-touch p-2">
          <Today :stats-prop="getToday()" />
        </div>
      </div>
      <div class="column is-full is-paddingless">
        <div class="columns is-multiline is-marginless is-2 is-variable">
          <div
            v-for="([k, v]) in getFilteredDays()"
            :key="v?.userSessionStatus?.starttime_of_this_day ?? k"
            class="column is-one-fifth-fullhd is-half-tablet is-full-mobile is-one-third-desktop"
          >
            <StatsNode
              :ids="getAllAppIdsForChart()"
              :stats="v"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

// import { runInThisContext } from "vm";
import { mapState, mapActions, mapMutations } from 'vuex'; // eslint-disable-line
// import toaster from "@/mixins/toaster";
import { parseISO, compareAsc, isToday, parse, format } from "date-fns"; // eslint-disable-line
import StatsNode from "../domain/pages/StatsNode.vue";
import Today from "../domain/pages/Today.vue";
import toaster from "@/mixins/toaster";
// import colors from "@/assets/styles/_colors.module.scss";

export default {
  auth: false,
  components: {
    StatsNode,
    Today
  },
  mixins: [toaster],
  data() {
    return {
      status: null,
      interval: 10000,
      prev: null,
      env: null,
    };
  },

  computed: {
    getSeriesData() {
      const result = [];
      if (this.status) {
        result.push({ "name": "New users", "data": []});
        result.push({ "name": "Logins", "data": []});
        result.push({ "name": "Deleted users", "data": []});
        Object.values(this.status).forEach(e => {
          const d = e.userSessionStatus.starttime_of_this_day;
          if (this.dateIsValid(d)) {
            const parsed = this.$datefns.parseISO(d);
            if (this.isToday(parsed)) return;
          }
          result[0].data.push(e.userSessionStatus.number_of_registered_users_this_day);
          result[1].data.push(e.userSessionStatus.number_of_unique_logins_this_day);
          result[2].data.push(e.userSessionStatus.number_of_deleted_users_this_day);
        });

      }
      return result;
    },
    getStartDateInUTCForChart() {
      if (this.status) {
        const d = this.$datefns.parse(Object.keys(this.status)[0], 'yyyy-MM-dd', new Date());
        return Date.UTC(d.getFullYear(), d.getMonth(), d.getDate());
      } else {
        return null;
      }

    },
    getStartDateForChart() {
      if (this.status) {
        return Object.keys(this.status)[0];
      } else {
        return null;
      }
    },
    chartOptions() {
      return {
        chart: {
          type: 'line',
          styledMode: true,
        },
        accessibility: {
          enabled: false,
        },
        credits: false,
        xAxis: {
          type: 'datetime'
        },
        yAxis: {
          title: {
            text: 'Number of user activities',
          }
        },
        title: {
          text: 'User activities ' + ' from ' + this.getStartDateForChart,
        },
        plotOptions: {
          series: {
            pointStart: this.getStartDateInUTCForChart,
            pointInterval: 24 * 3600 * 1000 // one day
          },
          line: {
            lineWidth: 5
          }
        },
        series: this.getSeriesData

      }
    }
  },
  watch: {},
  mounted() {
    this.startAutoPoller();
    this.$store.dispatch("api/get_environment", {
      callbackfunc: (data) => {
        this.env = data;
      }
    })
  },
  methods: {
    logStatus() {
      console.log("status: ", this.status)
    },

    dateIsValid(date) {
      return date && !Number.isNaN(new Date(date).getTime());
    },
    getFilteredDays() {
     return Object.entries(this.status).filter(([k, v]) => {
       const d = v?.userSessionStatus?.starttime_of_this_day ?? k;
       if (this.dateIsValid(d)) {
         const parsed = this.$datefns.parseISO(d);
         return !this.isToday(parsed)
       } else {
         return false;
       }
      }).reverse();
    },
    isToday(d) {
      return this.$datefns.isValid(d) && this.$datefns.isToday(d)
    },

    getToday() {
      return Object.entries(this.status).find(([k, v]) => {
        const d = v?.userSessionStatus?.starttime_of_this_day ?? k;
        if (this.dateIsValid(d)) {
          const parsed = this.$datefns.parseISO(d);
          return this.isToday(parsed)
        } else {
          return false
        }
      })
    },

    categories(st) {
      return Object.keys(st).map(x => {
        const parsed = parseISO(x);
        const formatted = format(parsed, "dd MMM")
        return formatted
      })
    },
    getValue(day) {
      return this.status[day];
    },

    getChartOptions() {
      return {
        chart: {
          type: 'column',
          styledMode: true,
        },

        accessibility: {
          enabled: false,
        },
        tooltip: {
          formatter: function () {
            return `<b>
              ${this.x} - ${this.point.appId}
              </b></br>${this.series.name}: ${this.y}
              `
          }
        },
        credits: false,
        xAxis: {
          // type: 'datetime'
          type: 'category',
          categories: this.categories(this.status),
        },
        yAxis: {
          title: {
            text: 'Number of user activities'
          },
          stackLabels: {
            enabled: true,
            allowOverlap: true,
            formatter: function() {
              return this.stack
            }
          },
        },
        title: {
          text: `User activities for apps ${this.getAllAppIdsForChart().join(", ")}`
        },
        plotOptions: {
          series: {
            pointStart: this.getStartDateInUTCForChart,
            pointInterval: 24 * 3600 * 1000 // one day
          },
          column: {
            stacking: 'normal'
          }
        },
        series: this.getSeries()

      }
    },
    getSeries() {
      const series = this.getAllAppIdsForChart().map((id, idx) => {
        return this.getSeriesDataForAppId(id, idx);
      })

      return this.$lodash.flatten(series)
    },

    getSeriesDataForAppId(appId, idx) {
      const result = [];
      if (this.status) {
        result.push({ "name": "New users", "data": [], stack: appId});
        result.push({ "name": "Logins", "data": [], stack: appId});
        result.push({ "name": "Deleted users", "data": [], stack: appId});
        if (idx === 0) {
          result[0].id = "users"
          result[1].id = "logins"
          result[2].id = "deleted"
        } else {
          result[0].linkedTo = "users"
          result[1].linkedTo = "logins"
          result[2].linkedTo = "deleted"

        }
        Object.keys(this.status).forEach(key => {
          const d = this.status[key].userSessionStatus.starttime_of_this_day;
          if (this.dateIsValid(d)) {
            const parsed = this.$datefns.parseISO(d);
            if (this.isToday(parsed)) return;
          }
          const target = this.status[key].userApplicationStatistics;
          if (target) {
            target.forEach(x => {
              const parsed = parseISO(key);
              const formatted = format(parsed, "dd MMM")
              if (x.for_application === appId) {
                result[0].data.push({ name: formatted, y: x.number_of_registered_users_this_day, appId})
                result[1].data.push({ name: formatted, y: x.number_of_unique_logins_this_day, appId })
                result[2].data.push({ name: formatted, y: x.number_of_deleted_users_this_day, appId })
              }
            })
          }

        });

      }
      return result
    },

    getAllAppIdsForChart(){
      if (this.status) {
        const appids = new Set();
        Object.values(this.status).forEach(e => {
          if (e.userApplicationStatistics) {
            e.userApplicationStatistics.forEach(j => {
              appids.add(j.for_application);
            })
          }
        });
        return [...appids];
      }
      return [];
    },

    startAutoPoller() {
      this.$store.dispatch("api/get_usersession_status", {
        callbackfunc: (data) => {
          this.status = data;
        }
      })
      this.polling = setInterval(() => {
        this.$store.dispatch("api/get_usersession_status", {
          callbackfunc: (data) => {
            this.status = data;
          }
        })
      }, this.interval);
    },
  },
};
</script>

<style lang="scss" scoped>

@import 'bulma/sass/utilities/mixins.sass';
@import "~/assets/styles/_colors.module.scss";

.min-height-full {
  min-height: 100vh;

}
.hc {
  height: 300px;
  @include until($widescreen) {
    flex-basis: 50%;
  }
}
.border-radius {
  border-radius: 1rem;
}
.h-100 {
  height: 100%;
}
.w-100 {
  width: 100%;
}
.vtitle {
  writing-mode: vertical-rl;
  text-orientation: upright;
  letter-spacing: -4px;
  position: fixed;
  z-index: 2;
  background: $color-dark-grey;
  bottom: 0;
  top: 0;
  @include until($desktop) {
    display: none;
  }
}

.htitle {
  z-index: 2;
  background: $color-dark-grey;
}
.htitle-container {
  background: $color-dark-grey;
  display: none;
  width: 100%;
  @include until($desktop) {
    display: inline-block;
  }
}

.status {
  margin-left: calc(32px + 0.5rem);
  @include until($desktop) {
    margin-left: 0;
  }
}

.truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.my-icon {
  width: 24px;
  height: 24px;
}
</style>
