<template>
  <div
    class="has-background-cool-grey has-text-dark h-100 border-radius p-4"
  >
    <div class="is-size-3">
      Today
    </div>
    <div> {{ formattedDate() }} </div>
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
  methods: {
    dateIsValid(date) {
      return !Number.isNaN(new Date(date).getTime());
    },
    formattedDate() {
      if(this.dateIsValid(this.stats.userSessionStatus.starttime_of_this_day)) {
        const parsed = this.$datefns.parseISO(this.stats.userSessionStatus.starttime_of_this_day);
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
</style>
