<template>
  <div
    class="has-background-cool-grey has-text-dark h-100 border-radius p-4 is-flex is-flex-direction-column is-flex-grow-1"
  >
    <header class="mb-4">
      <div class="is-size-3 has-text-weight-bold">
        <div class="is-flex is-justify-content-space-between">
          <div>
            Today
            <span class="is-size-6 has-text-weight-normal">({{ formattedDate() }})</span>
          </div>
          <div
            class="p-2 more-menu is-align-items-center is-flex is-justify-content-center"
          >
            <span class="icon">
              <font-awesome-icon icon="fas fa-ellipsis" />
            </span>
          </div>
        </div>
      </div>
      <div> Last updated: {{ getLastUpdated() }} </div>
    </header>
    <summary class="columns is-multiline is-mobile is-2 is-variable">
      <div class="column is-one-third-widescreen is-half-touch py-2">
        <div class="summary-item">
          <div class="is-size-6 truncate">
            Total registered users:
          </div>
          <div class="has-text-weight-bold is-size-4 is-flex is-align-items-center">
            <span class="icon mr-1">
              <font-awesome-icon
                icon="fas fa-users"
                size="xs"
              />
            </span>
            <span v-if="stats?.userSessionStatus">
              {{ stats.userSessionStatus.total_number_of_users }}
            </span>
            <span v-else>N/A</span>
          </div>
        </div>
      </div>
      <div class="column is-half-touch is-one-third-widescreen py-2">
        <div class="summary-item">
          <div class="is-size-6 truncate">
            Total applications:
          </div>
          <div class="has-text-weight-bold is-size-4 is-flex is-align-items-center">
            <span class="icon mr-1">
              <font-awesome-icon
                icon="fas fa-folder"
                size="xs"
              />
            </span>
            <span v-if="stats?.userSessionStatus">
              {{ stats.userSessionStatus.total_number_of_applications }}
            </span>
            <span v-else>N/A</span>
          </div>
        </div>
      </div>
      <div class="column is-half-touch is-one-third-widescreen py-2">
        <div class="summary-item">
          <div class="is-size-6 truncate">
            Total user session activities:
          </div>
          <div class="has-text-weight-bold is-size-4">
            <span class="icon mr-1">
              <font-awesome-icon
                icon="fas fa-user"
                size="xs"
              />
            </span>
            <span v-if="stats?.userSessionStatus">
              {{ stats.userSessionStatus.total_number_of_session_actions_this_day }}
            </span>
            <span v-else>N/A</span>
          </div>
        </div>
      </div>

      <div class="column is-half-touch is-one-third-widescreen py-2">
        <div class="summary-item">
          <div class="is-size-6 truncate">
            Today new users:
          </div>
          <div class="has-text-weight-bold is-size-4 is-flex is-align-items-center">
            <span class="icon mr-1">
              <font-awesome-icon
                icon="fas fa-user-plus"
                size="xs"
              />
            </span>
            <span v-if="stats?.userSessionStatus">
              {{ stats.userSessionStatus.number_of_registered_users_this_day }}
            </span>
            <span v-else>N/A</span>
          </div>
        </div>
      </div>
      <div class="column is-half-touch is-one-third-widescreen py-2">
        <div class="summary-item">
          <div class="is-size-6 truncate">
            Today logins:
          </div>
          <div class="has-text-weight-bold is-size-4 is-flex is-align-items-center">
            <span class="icon mr-1">
              <font-awesome-icon
                icon="fas fa-right-to-bracket"
                size="xs"
              />
            </span>
            <span v-if="stats?.userSessionStatus">
              {{ stats.userSessionStatus.number_of_unique_logins_this_day }}
            </span>
            <span v-else>N/A</span>
          </div>
        </div>
      </div>
      <div class="column is-half-touch is-one-third-widescreen py-2">
        <div class="summary-item">
          <div class="is-size-6 truncate">
            Today deleted users:
          </div>
          <div class="has-text-weight-bold is-size-4">
            <span class="icon mr-1">
              <font-awesome-icon
                icon="fas fa-delete-left"
                size="xs"
              />
            </span>
            <span v-if="stats?.userSessionStatus">
              {{ stats.userSessionStatus.number_of_deleted_users_this_day }}
            </span>
            <span v-else>N/A</span>
          </div>
        </div>
      </div>
    </summary>
    <main class="is-flex is-flex-grow-1">
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
    statsProp: {
      type: Array,
      required: true,
    }
  },
  computed: {
    stats() {
      return this.statsProp[1];
    },
    categories() {
      if (this.stats?.hourlyStatusTreeMap) {
        return Object.keys(this.stats.hourlyStatusTreeMap).map(x => {
          const [, h] = x.split(":");
          return `${h}:00`
        })
      } else {
        return []
      }
    },
    chartOptions() {
      return {
        chart: {
          type: 'line',
          styledMode: true,
          height: null,
          width: null,
        },
        accessibility: {
          enabled: false,
        },
        credits: false,
        xAxis: {
          type: 'cateogory',
          categories: this.categories
        },
        yAxis: {
          title: {
            enabled: false,
            text: 'Number of user activities',
          },
        },
        title: {
          text: "Hourly statistics",
          align: "left",
        },
        plotOptions: {
        },
        series: this.getSeriesData

      }
    },
    getSeriesData() {
      const result = [];
      result.push({ "name": "New users", "data": []});
      result.push({ "name": "Unique logins", "data": []});
      result.push({ "name": "Deleted users", "data": []});
      Object.values(this.stats?.hourlyStatusTreeMap ?? {}).forEach(e => {
        result[0].data.push(e?.number_of_registered_users_this_hour ?? 0);
        result[1].data.push(e?.number_of_unique_logins_this_hour ?? 0);
        result[2].data.push(e?.number_of_deleted_users_this_day ?? 0);
      });

      return result
    }
  },
  mounted() {
    // console.log(this.stats.hourlyStatusTreeMap)
  },
  methods: {
    dateIsValid(date) {
      return date && !Number.isNaN(new Date(date).getTime());
    },
    getLastUpdated() {
      const d = this.stats?.userSessionStatus?.last_updated
      if(this.dateIsValid(d) && !d===null) {
        const parsed = this.$datefns.parseISO(d);
        return this.$datefns.format(parsed, "HH:mm")
      } else {
        console.error('invalid date format value=' + this.stats?.userSessionStatus?.last_updated);
        return 'N/A';
      }
    },
    formattedDate() {
      const target = this.stats?.userSessionStatus?.starttime_of_this_day ?? this.statsProp[0];
      if(this.dateIsValid(target) && !target===null) {
        const parsed = this.$datefns.parseISO(target);
        return this.$datefns.format(parsed, "EEE, LLL dd, yyyy")
      } else {
        console.error('invalid date format value=' + this.stats?.userSessionStatus?.last_updated);
        return "N/A"
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
  width: 100%;
}

.more-menu {
  background: lighten($color-cool-grey, 4);
  border-radius: 50%;
  border: 2px solid transparent;
  transition: all 100ms ease-in-out;
  width: 48px;
  height: 48px
}

.more-menu:hover {
  border: 2px solid rgba(0, 0, 0, 0.2);
  cursor: pointer;
}

.truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
