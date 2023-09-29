# Whydah Statistics Dashboard Frontend

This is the frontend part of this app, it needs to communicate with a backend
service. See top level instructions on how to build a java package.

This app runs on the latest version of `nuxt 3`, and it was migrated from `nuxt
2`.

## Developing


For the frontend to work correctly you need to build a java package that can
answer our api calls. The first thing you'll need is an
`application.properties`, ask @totto, @misterhuydo, or @roosta for the correct
values depending on your development target.

Start by cd to the root of this project, then build a java package

```bash
mvn clean package
```

The run the server:
```bash
java -jar target/whydah-statisticsdashboard-[VERSION]-snapshot.jar
```
Now the server should be able to answer api calls

#### Build and serve frontend

Navigate to `./src/main/resources/nuxt-spa` and run this:

```bash
# install dependencies
$ npm install

# serve with hot reload at localhost:3000
$ npm start
```

For detailed explanation on how things work, check out [Nuxt.js docs](https://nuxtjs.org).

## Production

To build for production run this script

```bash
$ npm run generate
```

It will output result to `./dist` folder.

## Proxy settings

In the webapp we need to setup some proxy settings so that api requests are
sent to the server when developing. The proxy settings currently in the project
should work without modifications, and it'll only apply to the `devserver`.

```
nitro: {
  devProxy: { // routeRules for prod
    '/env': { target: 'http://localhost:8088/env', changeOrigin: true},
    '/status': { target: 'http://localhost:8088/status', changeOrigin: true},
  }
},
```
## Favicon

To set a favion, it needs to be included in `./public` folder and
`environment_config.json` must contain that filename (without path)

For example, for Quadim stats we add the icon `./public/quadim.favicon.ico`,
then add the filename to `environment_config`:
```
...
"favicon": "quadim.favicon.ico"
...
```

## Migration notes

- [Migrate to Nuxt 3: Overview](https://nuxt.com/docs/migration/overview)


#### Requests

Axios is no longer built into nuxt, and doesn't support nuxt 3. Nuxt 3 instead
includes support for [the fetch
api](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch).

#### Nuxt config

I've removed all deprecated config options, plugins are read from the
`./plugin` directory, and middleware from `./middleware`, no need for configs
for this anymore.

#### State management

Vuex is no longer built in, but it supports nuxt 3. Nuxt recommends `pinia`,
but I opted for migrating vuex to latest versions. See `./store/index.js` for
main storage export, and we use `api`, and `auth` modules.

Works mostly like before, and vuex api is mainly the same, but setup steps
needed to be changed.

#### UI framework

We use bulma for UI, wanted to use buefy but there is some uncertainty that it
will support vue3, see [this issue](https://github.com/buefy/buefy/issues/2505)
for details

