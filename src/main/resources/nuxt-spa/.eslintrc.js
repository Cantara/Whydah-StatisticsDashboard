
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
  }
}
