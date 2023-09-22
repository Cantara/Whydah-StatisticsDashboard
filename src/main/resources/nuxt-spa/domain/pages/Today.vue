<template>
  <div
    class="has-background-cool-grey has-text-dark h-100 border-radius p-4"
  >
    <div class="is-size-3">
      Today
    </div>
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
    // console.log(this.stats)
  }
}
</script>

<style lang="scss" scoped>

.border-radius {
  border-radius: 1rem;
}
</style>
