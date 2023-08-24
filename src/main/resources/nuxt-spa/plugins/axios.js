
export default ({app, $axios, $mediator}) => {
  
  $axios.onRequest(config => {
    app.$eventHub.$emit('before-request',config);
    return config;
  });
  $axios.onRequestError(error => {
    app.$eventHub.$emit('request-error',error);
  })
  $axios.onResponse(response => {
    app.$eventHub.$emit('after-response',response);
    return response;
  })
  $axios.onRequestError(error => {
    app.$eventHub.$emit('response-error',error);
  })

  $axios.onError(error => {
   
    if (error && error.response.status === 401) {
      if (app.$auth.loggedIn) {
        app.$auth.logout();
        app.router.push('/ui/w');
      } else {
        app.router.push('/ui/w');
      }
    }
  });
}
