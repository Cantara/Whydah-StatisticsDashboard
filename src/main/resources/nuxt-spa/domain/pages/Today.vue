<template>
  <div
    class="has-background-cool-grey has-text-dark h-100 border-radius p-4"
  >
    <header class="mb-2">
      <div class="is-size-3 has-text-weight-bold">
        Today
        <span class="is-size-6 has-text-weight-normal">({{ formattedDate() }})</span>
      </div>
      <div> Last updated: {{ getLastUpdated() }} </div>
    </header>
    <summary class="is-flex flex-gap mb-4">
      <div class="summary-item">
        <div class="is-size-6">
          Total registered users:
        </div>
        <div class="has-text-weight-bold is-size-4 is-flex is-align-items-center">
          <span class="icon mr-1">
            <font-awesome-icon
              icon="fas fa-users"
              size="xs"
            />
          </span>
          <span>{{ stats.userSessionStatus.total_number_of_users }}</span>
        </div>
      </div>
      <div class="summary-item">
        <div class="is-size-6">
          Total applications:
        </div>
        <div class="has-text-weight-bold is-size-4 is-flex is-align-items-center">
          <span class="icon mr-1">
            <font-awesome-icon
              icon="fas fa-folder"
              size="xs"
            />
          </span>
          <span>
            {{ stats.userSessionStatus.total_number_of_applications }}
          </span>
        </div>
      </div>
      <div class="summary-item">
        <div class="is-size-6">
          Total user session activities:
        </div>
        <div class="has-text-weight-bold is-size-4">
          <span class="icon mr-1">
            <font-awesome-icon
              icon="fas fa-user"
              size="xs"
            />
          </span>
          <span>
            {{ stats.userSessionStatus.total_number_of_session_actions_this_day }}
          </span>
        </div>
      </div>
    </summary>
    <main>
      <highchart
        id="today-chart"
        class="hc border-radius"
        :options="chartOptions"
      />
    </main>
  </div>
</template>

<script>
export default {
  name: "Today",
  props: {
    stats: {
      type: Object,
      default() {
        return null
      }
    }
  },
  computed: {
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
          text: "Todays hourly statistics",
          align: "left",
        },
        plotOptions: {
          // series: {
          //   pointStart: this.getStartDateInUTCForChart,
          //   pointInterval: 24 * 3600 * 1000 // one day
          // },
          // line: {
          //   lineWidth: 5
          // }
        },
        series: this.getSeriesData

      }
    },
    getSeriesData() {
      const result = [];
      // if (this.status) {
      //   result.push({ "name": "New users", "data": []});
      //   result.push({ "name": "Logins", "data": []});
      //   result.push({ "name": "Deleted users", "data": []});
      //   Object.values(this.status).forEach(e => {
      //     result[0].data.push(e.userSessionStatus.number_of_registered_users_this_day);
      //     result[1].data.push(e.userSessionStatus.number_of_unique_logins_this_day);
      //     result[2].data.push(e.userSessionStatus.number_of_deleted_users_this_day);
      //   });
      //
      // }
      return result;
    }
  },
  mounted() {
    console.log(this.stats)
  },
  methods: {
    dateIsValid(date) {
      return date && !Number.isNaN(new Date(date).getTime());
    },
    getLastUpdated() {
      const d = this.stats.userSessionStatus.last_updated
      if(this.dateIsValid(d)) {
        const parsed = this.$datefns.parseISO(d);
        return this.$datefns.format(parsed, "HH:mm")
      } else {
        console.error('invalid date format value=' + this.stats.userSessionStatus.last_updated);
        return 'N/A';
      }
    },
    formattedDate() {
      const target = this.stats.userSessionStatus.starttime_of_this_day;
      if(this.dateIsValid(target)) {
        const parsed = this.$datefns.parseISO(target);
        return this.$datefns.format(parsed, "EEE, LLL dd, yyyy")
      }
    },
  },
}
</script>

<style lang="scss" scoped>

@import "~/assets/styles/_colors.module.scss";
@import 'bulma/sass/utilities/derived-variables.sass';

.border-radius {
  border-radius: 1rem;
}

.flex-gap {
  gap: 1rem;
}
.summary-item {
  background: lighten($color-cool-grey, 4);
  padding: 0.5rem;
  border-radius: 6px;

}

.hc {
  height: 100%;
  width: 100%;
}
</style>
