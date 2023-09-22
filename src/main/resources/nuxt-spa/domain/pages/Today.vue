<template>
  <div
    class="has-background-cool-grey h-100 border-radius"
  >
    {{ stats }}
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
  }
}
</script>

<style lang="scss" scoped>

.border-radius {
  border-radius: 1rem;
}
</style>
