
import Vue from 'vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { config } from '@fortawesome/fontawesome-svg-core'
//import { library, dom } from '@fortawesome/fontawesome-svg-core'
//import { fas } from '@fortawesome/free-solid-svg-icons'
//import { faTwitter } from '@fortawesome/free-brands-svg-icons'

//library.add(fas, faTwitter);
//dom.watch();
config.autoAddCss = false

Vue.component('font-awesome-icon', FontAwesomeIcon)
Vue.config.productionTip = false
