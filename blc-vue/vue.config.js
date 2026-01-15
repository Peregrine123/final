module.exports = {
    devServer: {
        overlay: {
            // 让浏览器 overlay 同时显示警告和错误
            warnings: true,
            errors: true
        },
        host: 'localhost',
        port: 8080, // 端口号
        https: false, // https:{type:Boolean}
        open: true, // 配置自动启动浏览器
        hotOnly: true, // 热更新
        proxy: {
            // Proxy API requests in dev so the app can use a relative `/api` baseURL.
            '/api': {
                // Override via env (e.g. VUE_APP_API_TARGET=http://192.168.0.2:8443)
                target: process.env.VUE_APP_API_TARGET || 'http://localhost:8443',
                changeOrigin: true
            }
        },
        headers: {
            'Access-Control-Allow-Origin': '*',
        }
    }
};
