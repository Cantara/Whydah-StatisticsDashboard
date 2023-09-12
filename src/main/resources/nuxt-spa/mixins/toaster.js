export default {

  data() {
    return {
      unsubscribe:null,
      suppress: false
    }
  },

  methods: {

  },

  beforeDestroy() {
    this.unsubscribe();
  },

  mounted() {

    this.unsubscribe = this.$store.subscribe((mutation, state) => {

      if (this.suppress) {
        return;
      }
      if (mutation.type.includes("ERROR")) {
        let text = "";
        // console.log('error ' + mutation.payload.response);
        if (mutation.payload.response && mutation.payload.response.data) {
          text = mutation.payload.response.data.message;
        } else {
          text = mutation.payload;
        }

        console.error(text)
        // this.$buefy.toast.open({
        //   message: text,
        //   type: 'is-danger'
        // });
        // this.$toasted.show(text, {position: 'bottom-center', type:'error', theme: 'outline', keepOnHover:true});
      } else if (mutation.type.includes("SUCCESS")) {
        const text = mutation.payload;
        console.log(text)
        //this.$toasted.show(text, {position: 'bottom-center', type:'success', theme: 'outline'});
        // this.$buefy.toast.open({
        //   message: text,
        //   type: 'is-success'
        // });
      }
    });

  },
};

