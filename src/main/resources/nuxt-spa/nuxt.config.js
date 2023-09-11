//const config = require('./config.js')

export default {
  /*
   ** Nuxt rendering mode
   ** See https://nuxtjs.org/api/configuration-mode
   */
  ssr: false,

  /*
   ** Nuxt target
   ** See https://nuxtjs.org/api/configuration-target
   */
  target: 'static',

  router: {
    middleware: 'query-parameters'
  },

  /*
   ** Headers of the page
   ** See https://nuxtjs.org/api/configuration-head
   */
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
    style: [
      { cssText: '.icon svg { height: 20px !important; }', type: 'text/css' }
    ],
  },
  /*
   ** Global CSS
   */
  css: [
    '@/assets/styles/main.scss',
  ],
  /*
   ** Plugins to load before mounting the App
   ** https://nuxtjs.org/guide/plugins
   */

  plugins: [
    {src:'~/plugins/font-awesome.js', mode: 'client'},
    '~plugins/highcharts.js',
    '~plugins/lodash.js',
    '~plugins/axios.js',
    '~plugins/date-fns.js',

  ],
  //makes hub always loaded first, so other plugins can use it
  extendPlugins (plugins) {
    plugins.unshift('~/plugins/event-hub.js')
    return plugins
  },
  /*
   ** Auto import components
   ** See https://nuxtjs.org/api/configuration-components
   */
  components: false,
  /*
   ** Nuxt.js dev-modules
   */
  buildModules: [
    // Doc: https://github.com/nuxt-community/eslint-module
    '@nuxtjs/google-fonts',
    '@nuxtjs/eslint-module',
  ],
  googleFonts: {
    download: true
  },
  /*
   ** Nuxt.js modules
   */
  modules: [
    '@nuxtjs/proxy',
    ["nuxt-buefy", {
      materialDesignIcons: true,
      css: true,
      defaultIconPack: "fas",
      defaultIconComponent: "font-awesome-icon",
    }],
    '@nuxtjs/toast',
    '@nuxtjs/sentry',
    '@nuxtjs/axios',
    ['nuxt-fontawesome', {
      component: 'fa',
      imports: [
        {
          set: '@fortawesome/free-solid-svg-icons',
          icons: ['fas']
        },
        {
          set: '@fortawesome/free-regular-svg-icons',
          icons: ['far']
        },
        {
          set: '@fortawesome/free-brands-svg-icons',
          icons: ['fab']
        }
      ]
    }]
  ],
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
  sentry: {
    dsn: 'https://f8cf946a403f4e709a6d6639a528da04@o450136.ingest.sentry.io/5434296', // Enter your project's DSN here
    config: {
      logErrors: true
    },
  },
  /*
   ** Axios module configuration
   ** See https://axios.nuxtjs.org/options
   */
  axios: {
    proxy: true,
  },
  proxy: {
    /*UNCOMMENT THE LINE BELOW WHEN TESTING ON YOUR LOCALHOST*/
    '/api': { target: 'http://localhost:8088/', pathRewrite: {'^/api': '/'}, changeOrigin: true},
    '/status': { target: 'http://localhost:8088/', pathRewrite: {'^/status': 'status'}, changeOrigin: true},
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
    babel: {
      "presets": [
        [
          "@babel/preset-env",
          {
            targets: {
              esmodules: true
            },
            "loose": true,
            "shippedProposals": true
          }
        ]
      ]
    },
    loaders:  {
      vue: {
        prettify: false
      },
      sass: {
        implementation: require('sass')
      },
      scss: {
        implementation: require('sass')
      }
    },
    extend(config, ctx) {
      config.module.rules.push({
        enforce: 'pre',
        test: /\.(js|vue)$/,
        loader: 'eslint-loader',
        exclude: /(node_modules)/,
        options: {
          fix: true,
          cache:false
        }
      });


    }
  }
}
