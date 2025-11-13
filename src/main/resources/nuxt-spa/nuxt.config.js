const config = require("../../../../environment_config.json");

export default defineNuxtConfig({
  ssr: false,
  target: 'static',
  compatibilityDate: '2025-11-13',

  app: {
    head: {
      title: process.env.npm_package_name || '',
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { hid: 'description', name: 'description', content: process.env.npm_package_description || '' }
      ],
      link: [
        {
          rel: 'icon',
          type: 'image/x-icon',
          href: `/${config.favicon}`
        },
      ],
    },
  },

  css: [
    '@/assets/styles/main.scss',
    "@fortawesome/fontawesome-svg-core/styles.css",
  ],

  googleFonts: {
    families: {
      "Noto+Sans": true
    },
  },

  modules: [
    '@nuxtjs/google-fonts',
    'nuxt-highcharts',
  ],

  nitro: {
    devProxy: {
      '/status': { 
        target: 'http://localhost:8088/status', 
        changeOrigin: true
      },
      '/env': { 
        target: 'http://localhost:8088/env', 
        changeOrigin: true
      },
    }
  },

  generate: {
    dir: process.env.APP_ENV === 'production' ? 'dist' : `dist-${process.env.APP_ENV}`
  },

  vite: {
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler', // or 'modern' depending on sass version
          quietDeps: true, // Suppress deprecation warnings from dependencies
          silenceDeprecations: ['import', 'global-builtin', 'color-functions'], // Silence specific deprecations
        }
      }
    }
  }
})