<template>
  <div
    v-if="status"
    class="has-background-black has-text-white min-height-full"
  >
    <div class="columns is-marginless is-1 p-2">
      <div class="column is-half p-2">
        <highchart
          id="line-chart"
          class="hc border-radius mb-4"
          :options="chartOptions"
        />
        <div
          v-for="(x, i) in getAllAppIdsForChart()"
          :key="i"
        >
          <highchart
            id="column-chart"
            class="hc border-radius"
            :options="getChartOptions(x)"
          />
        </div>
      </div>
      <div class="column is-half p-2">
        <div
          class="has-background-cool-grey h-100 border-radius"
        />
      </div>
    </div>
    <!-- <div class="container"> -->
    <!--   <StatsNode -->
    <!--     v-for="(b, i) in Object.keys(status).reverse()" -->
    <!--     :key="i" -->
    <!--     :stats="getValue(b)" -->
    <!--   /> -->
    <!-- </div> -->
  </div>
</template>

<script>

// import { runInThisContext } from "vm";
import { mapState, mapActions, mapMutations } from 'vuex'; // eslint-disable-line
// import toaster from "@/mixins/toaster";
// import { parseISO, compareAsc, isToday, parse } from "date-fns";
import StatsNode from "../domain/pages/StatsNode.vue";
import toaster from "@/mixins/toaster";
import colors from "@/assets/styles/_colors.module.scss";

export default {
  auth: false,
  components: {
    // StatsNode
  },
  mixins: [toaster],
  data() {
    return {
      status: null,
      interval: 10000
    };
  },

  computed: {
    getSeriesData() {
      const result = [];
      if (this.status) {
        result.push({ "name": "New users", "data": [], "color": "#209cee" });
        result.push({ "name": "Logins", "data": [], "color": "#23d160" });
        result.push({ "name": "Deleted users", "data": [], "color": "#ff3860" });
        Object.values(this.status).forEach(e => {
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
  },
  methods: {
    // ...mapMutations({
    //   setToken: 'auth/setToken'
    // }),
    // ...mapActions("api", ["get_usersession_status"]),
    getValue(day) {
      return this.status[day];
    },

    getChartOptions(appId){
      return {
        chart: {
          type: 'column',
          styledMode: true,
        },
        credits: false,
        xAxis: {
          type: 'datetime'
        },
        yAxis: {
          title: {
            text: 'Number of user activities'
          }
        },
        title: {
          text: 'User activities ' + ' for appid ' + appId
        },
        plotOptions: {
          series: {
            pointStart: this.getStartDateInUTCForChart,
            pointInterval: 24 * 3600 * 1000 // one day
          },
          line: {
            lineWidth: 3
          }
        },
        series: this.getSeriesDataForAppId(appId)

      }
    },

    getSeriesDataForAppId(appId){
      const result = [];
      if (this.status) {
        result.push({ "name": "New users", "data": [], "color": "#209cee" });
        result.push({ "name": "Logins", "data": [], "color": "#23d160" });
        result.push({ "name": "Deleted users", "data": [], "color": "#ff3860" });

        Object.values(this.status).forEach(e => {
          const found = e.userApplicationStatistics.find(x => x.for_application === appId);
          if (found) {
            result[0].data.push(found.number_of_registered_users_this_day);
            result[1].data.push(found.number_of_unique_logins_this_day);
            result[2].data.push(found.number_of_deleted_users_this_day);
          }
        });

      }
      return result;
    },

    getAllAppIdsForChart(){
      if (this.status) {
        const appids = new Set();
        Object.values(this.status).forEach(e => {
          e.userApplicationStatistics.forEach(j => {
            appids.add(j.for_application);
          })
        });
        return appids;
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

.min-height-full {
  min-height: 100vh;

}
/* .container { */
/*   margin-left: 10px; */
/*   margin-right: 10px; */
/*   margin-top: 20px; */
/*   display: flex; */
/*   flex-direction: row; */
/*   flex-wrap: wrap; */
/*   align-content: flex-start; */
/*   gap: 1rem; */
/* } */

.hc {
  height: 300px;
}
.border-radius {
  border-radius: 1rem;
}
.h-100 {
  height: 100%;
}
</style>
