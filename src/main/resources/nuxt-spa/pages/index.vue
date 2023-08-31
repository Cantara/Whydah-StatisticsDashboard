<template>
<div v-if="status" class="container">
  <StatsNode v-for="(b, i) in Object.keys(status).reverse()" :key="b" :stats="getValue(b)">
  </StatsNode>
</div>
</template>

<script>

import { runInThisContext } from "vm";
import {mapState,  mapActions, mapMutations} from 'vuex';
import toaster from "@/mixins/toaster";
import StatsNode from "../domain/pages/StatsNode.vue";

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
        ...mapState({}),
    },
    watch: {},
    mounted() {
        this.startAutoPoller();
    },
    methods: {
        ...mapMutations({
            setToken: 'auth/setToken'
        }),
        ...mapActions("api", ["get_usersession_status"]),
        getValue(day){
          return this.status[day];
        },
        startAutoPoller() {
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
            }, this.interval);
        },
    },
    components: { StatsNode }
};
</script>

<style lang="scss" scoped>
.container {
  margin-left: 20px;
  margin-top: 20px;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-content: flex-start;
  gap: 1rem;
}

ul.a {
  list-style-type: circle;
  padding-left: 20px;
}

.item {
  width: 400px;
  padding: 10px;
  opacity: 0.8;
  -webkit-border-radius: 1px 1px 1px 1px;
  border-radius: 1px 1px 1px 1px;
  border: 1px solid #000;
}

.app-item {
  -webkit-border-radius:  5px 5px 5px 5px;
  border-radius: 5px 5px 5px 5px;
  border: 1px solid #000;
  margin-right: 5px;
}

.item:hover{
  opacity: 1;
  transition: .9s;
}
</style>
