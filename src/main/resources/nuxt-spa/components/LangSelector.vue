<template>
      <b-field>
            <b-select
              v-model="selected_lang"
              placeholder=""
              @input="selectLang"
            >
              <option v-for="option in langs" :key="option" :value="option">
                {{ option }}
              </option>
            </b-select>
      </b-field>
  </template>
  <script>
  import langSelector from "@/mixins/langSelector";
  
  export default {
  
    mixins: [langSelector],
    
    methods: {
        selectLang(item){
            this.$emit("languageChanged", item);
        },
    },
    
  }
  </script>
  
  <style lang="scss">
  
  </style>
  
