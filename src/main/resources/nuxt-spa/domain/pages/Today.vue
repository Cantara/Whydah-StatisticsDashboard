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
    <summary class="is-flex flex-gap">
      <div class="summary-item">
        <div class="is-size-6">
          Total registered users
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
          Total applications
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
          Total user session activities
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
    }
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
</style>
