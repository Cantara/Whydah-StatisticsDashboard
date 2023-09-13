import { useStore } from 'vuex';
export default defineNuxtRouteMiddleware((to, from) => {
  const store = useStore();
  const params = from.query;
  const accessToken = params?.accessToken
  if (accessToken && accessToken.length > 0) {
    store.commit('auth/setToken', accessToken)
  }
})

// export default function({ store, route }) {
//
//   const params = route.query;
//   for (const key of Object.keys(params)) {
//     const keyValue = params[key];
//     if (keyValue !== null && keyValue.length > 0) {
//       if (key.toLowerCase() === 'accesstoken')
//         store.commit('auth/setToken',keyValue)
//     }
//   }
//
// }
