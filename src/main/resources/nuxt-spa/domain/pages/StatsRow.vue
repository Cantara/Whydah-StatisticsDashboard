<template>
  <div
    class="p-2 bullet is-flex is-justify-content-space-between mb-2"
  >
    <div>
      <span class="icon mr-2">
        <font-awesome-icon :icon="['fas', iconClass]" />
      </span>
      <span class="">{{ label }}</span>
    </div>
    <span class="has-text-weight-bold">
      {{ value }}
    </span>
  </div>
</template>

<script>
export default {
  name: "StatsRow",
  props: {
    label: {
      required: true,
      type: String,
    },
    iconClass: {
      required: true,
      type: String
    },
    value: {
      required: true,
      type: Number
    }
  }
}
</script>

<style lang="scss" scoped>

@import "~/assets/styles/_colors.module.scss";
@import 'bulma/sass/utilities/derived-variables.sass';

.bullet {
  border-radius: 6px;
  background: lighten($color-dark-grey, 3);
}
</style>
