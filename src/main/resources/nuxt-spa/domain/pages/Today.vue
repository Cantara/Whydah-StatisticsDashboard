<template>
  <div
    class="has-background-cool-grey has-text-dark h-100 border-radius p-4"
  >
    <header class="mb-2">
      <div class="is-size-3">
        Today
        <span class="is-size-6">({{ formattedDate() }})</span>
      </div>
      <div> Last updated: {{ getLastUpdated() }} </div>
    </header>
    <summary class="is-flex flex-gap">
      <div>
        <div class="is-size-6">
          Total registered users
        </div>
        <div class="has-text-weight-bold is-size-4 is-flex is-align-items-center">
          <span class="icon">
            <font-awesome-icon
              icon="fas fa-users"
              size="2xs"
            />
          </span>
          <span>{{ stats.userSessionStatus.total_number_of_users }}</span>
        </div>
      </div>
      <div>
        <div class="is-size-6">
          Total applications
        </div>
        <div class="has-text-weight-bold is-size-4 is-flex is-align-items-center">
          <span class="icon">
            <font-awesome-icon
              icon="fas fa-folder"
              size="2xs"
            />
          </span>
          <span>
            {{ stats.userSessionStatus.total_number_of_applications }}
          </span>
        </div>
      </div>
      <div>
        <div class="is-size-6">
          Total user session activities
        </div>
        <div class="has-text-weight-bold is-size-4">
          <span class="icon">
            <font-awesome-icon
              icon="fas fa-user"
              size="2xs"
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

.border-radius {
  border-radius: 1rem;
}

.flex-gap {
  gap: 2.5rem;
}
</style>
