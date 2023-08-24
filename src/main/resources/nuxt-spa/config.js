const configs = {
    
    local : {
        api_url: 'http://localhost:8088/',
        env:'local'
    },
    production : {
        api_url: 'https://stats.1881.no/',
        env:'prod'
    }

}

const nodeEnv = process.env.APP_ENV
module.exports = configs[nodeEnv]
