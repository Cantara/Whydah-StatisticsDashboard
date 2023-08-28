<template>
  <div class="container">
    <section class="hero is-fullheight">
      <div class="hero-head">
</div>
      <div class="hero-body is-flex is-flex-direction-column">
        <div class=" is-flex is-flex-direction-column">
          <div v-if="status" class="has-text-centered">
{{ status }}
</div>
</div>
</div>
      <div class="is-flex is-justify-content-center has-text-grey">
        All rights reserved &amp; copyright © 2023
      </div>
    </section>
  </div>
</template>

<script>

import { runInThisContext } from "vm";
import {mapState,  mapActions, mapMutations} from 'vuex';
import toaster from "@/mixins/toaster";

export default {
  auth: false,
  mixins: [toaster],

  data() {
    return {
      status: null,
      interval: 10000
    };
  },
  computed: {
    ...mapState({

    }),
  },
  watch: {

  },

  mounted() {
    this.startAutoPoller();
  },


  methods: {
    ...mapMutations({
       setToken: 'auth/setToken'
    }),

    ...mapActions("api", ["get_usersession_status"]),

    startAutoPoller () {
          this.get_usersession_status({
            callbackfunc: (data) => {
              this.status = data;
            }
          });
          this.polling = setInterval(() => {
            this.get_usersession_status({
            callbackfunc: (data) => {
              this.status = data;
            }
          });
          }, this.interval)
    },

  },

};
</script>

<style lang="scss" scoped>
.subtitle {
  font-weight: 300;
  font-size: 42px;
  color: #526488;
  word-spacing: 5px;
  padding-bottom: 15px;
}

.links {
  padding-top: 15px;
}

.ps {
  height: 320px;
}

</style>
