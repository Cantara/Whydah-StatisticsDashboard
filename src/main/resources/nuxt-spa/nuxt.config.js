export default defineNuxtConfig({
  ssr: false,
  target: 'static',

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
          href: '/favicon.ico'
        },
      ],
      // style: [
      //   { cssText: '.icon svg { height: 20px !important; }', type: 'text/css' }
      // ],
    },
  },
  /*
   ** Global CSS
   */
  css: [
    '@/assets/styles/main.scss',
    "@fortawesome/fontawesome-svg-core/styles.css",
  ],

  googleFonts: {
    families: {
      "Noto+Sans":true
    },
  },
  modules: [
    '@nuxtjs/google-fonts',
    'nuxt-highcharts',
    '@nuxtjs/eslint-module',
  ],
  eslint: {
    lintOnStart: false
  },
  toast: {
    position: 'top-center',
    fullWidth:true,
    duration:5000,
    closeOnSwipe:true,
    register: [ // Register custom toasts
      {
        name: 'my-error',
        message: 'Oops...Something went wrong',
        options: {
          type: 'error'
        }
      }
    ]
  },
  // sentry: {
  //   dsn: 'https://f8cf946a403f4e709a6d6639a528da04@o450136.ingest.sentry.io/5434296', // Enter your project's DSN here
  //   config: {
  //     logErrors: true
  //   },
  // },
  //
  nitro: {
    devProxy: { // routeRules for prod
      '/api': { target: 'http://localhost:8088', changeOrigin: true},
      '/status': { target: 'http://localhost:8088/status', changeOrigin: true},
    }
  },
  generate: {
    dir: process.env.APP_ENV === 'production' ? 'dist' : `dist-${process.env.APP_ENV}`
  },

  build: {
    parallel: true,
    cache: true,
    analyze: false,
    optimizeCSS: process.env.APP_ENV === 'production',
    postcss: {
      preset: {
        features: {
          customProperties: false
        }
      }
    },
  }
})
