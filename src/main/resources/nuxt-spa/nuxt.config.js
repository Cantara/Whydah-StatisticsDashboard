//const config = require('./config.js')

export default {
  /*
  env: {
    config
  },*/
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
        rel: 'icon', type: 'image/x-icon',
        href: '/favicon.ico'
      },
    ],
    style: [
      { cssText: '.icon svg { height: 20px !important; }', type: 'text/css' }
    ],
    /*
    script: [{
      src: 'https://maps.googleapis.com/maps/api/js?key=AIzaSyDDDW6OIkPWoD3VheJbHAH-FGJQ0mn1TYU&libraries=places'
    }]*/
  },
  /*
  ** Global CSS
  */
  css: [
    '@/assets/styles/main.scss',
    // '@/assets/styles/bulma_modules.sass'
  ],
  /*
  ** Plugins to load before mounting the App
  ** https://nuxtjs.org/guide/plugins
  */

  plugins: ['~plugins/vuelidate.js', 
  { src: '~plugins/perfect-scrollbar.js', ssr: false, mode: 'client' }, 
  {src:'~/plugins/font-awesome.js', mode: 'client'},
  //'~/plugins/font-awesome.js',  
  { src: '~/plugins/vue-slider.js', ssr: false },
    {src:'~/plugins/datagrid.js', mode: 'client'},
    '~plugins/mediator.js',
    '~plugins/filters.js',
    '~plugins/highcharts.js',
    '~plugins/lodash.js',
    '~plugins/axios.js',
    { src: "~plugins/click-outside.js", ssr: true},
    '~plugins/global.js',
    {src:'~/plugins/date-picker.js', mode: 'client'},
    '~plugins/date-fns.js',
   // {src : '~/plugins/vue-apexchart.js', ssr : false}

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
    //'nuxt-basic-auth-module',
    //'@nuxtjs/vuetify',
    '@nuxtjs/proxy',
    [
      "nuxt-buefy",
      {
        materialDesignIcons: true,
        css: true,
        defaultIconPack: "fas",
        defaultIconComponent: "font-awesome-icon",
      },
    ],
    '@nuxtjs/toast',
    '~/modules/autoinjector.js',
    '@nuxtjs/auth-next',
    '@nuxtjs/sentry',
    'vue-scrollto/nuxt',
    '@nuxtjs/moment',
    // Doc: https://github.com/nuxt-community/modules/tree/master/packages/bulma
    // Doc: https://axios.nuxtjs.org/usage
    '@nuxtjs/axios',
    '@nuxtjs/style-resources',
    [
      'nuxt-fontawesome', {
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
    }
    ]
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
    }, // Additional config
  },
  styleResources: {
    scss: ['~assets/styles/variables/_variables.scss','~assets/styles/_mixins.scss'],
  },
  /*
  ** Axios module configuration
  ** See https://axios.nuxtjs.org/options
  */
 
  router: {
    //middleware: ['auth']
  },
  axios: {
    proxy:true,
  },
  proxy: {
    /*UNCOMMENT THE LINE BELOW WHEN TESTING ON YOUR LOCALHOST*/
    '/status': { target: 'http://localhost:8088/', pathRewrite: {'^/status': 'status'}, changeOrigin: true},
  },
  /*
  auth: {
    redirect: {
      login: '/',
      callback: '/login',
      logout: '/',
      home: '/ui'
    },
    strategies: {
      social: {
        scheme: 'oauth2',
          clientId: config.oauth2_clientid,
          scope: ['openid', 'profile', 'email'],
          responseType: 'token',
          grantType: 'authorization_code',
          state: 'UNIQUE_AND_NON_GUESSABLE',
          codeChallengeMethod: '',
          responseMode: '',
          acrValues: '',
          token: {
            property: 'access_token',
            type: 'Bearer',
            required: true,
            maxAge: 1800
          },
          refreshToken: {
            property: 'refresh_token',
            maxAge: 60 * 60 * 24 * 30
          },
          endpoints: {
            "authorization": `${config.oauth2_url}authorize`,
            "userInfo": `${config.oauth2_url}userinfo`,
            "token": `${config.oauth2_url}token`,
            "logout": `${config.oauth2_url}logout`
          },
        
      }
    }
  },
  */
  /*
  publicRuntimeConfig: {
    api_url: config.api_url,
    axios: {
      baseURL: config.api_url
    }
  },*/
  generate: {
    dir: process.env.APP_ENV === 'production'? 'dist': `dist-${process.env.APP_ENV}`
  },
  

  build: {
    vendor: ['vue-datagrid'], 
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
