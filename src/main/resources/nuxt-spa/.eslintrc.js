
module.exports = {
  root: true,
  extends: ["@nuxt/eslint-config"],
  ignorePatterns: ["dist", "node_modules"],
  globals: {
    "defineNuxtPlugin": "readonly",
    "defineNuxtConfig": "readonly",
    "defineNuxtRouteMiddleware" : "readonly",
    "$fetch": "readonly"
  },
  rules: {
    "no-unused-vars": "warn",
    "vue/no-unused-components": "warn",
  }
}
