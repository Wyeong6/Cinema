const { createProxyMiddleware } = require('http-proxy-middleware');

// http-proxy-middleware를 사용하여 api 요청을 프록시하는 설정입니다.
module.exports = function(app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://localhost:8080',
            changeOrigin: true,

        })
    );
}