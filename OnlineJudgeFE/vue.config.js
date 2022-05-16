module.exports = {
  publicPath: '/',
  assetsDir: "assets",
  devServer: {
    open: true,
    host: '0.0.0.0',
    port: 8048,
    public: 'oj.iuaenasong.com:8048',
    proxy: {
      '/api': {
        target: 'http://localhost:6688',
        changeOrigin: true
      }
    },
    disableHostCheck: true,
  },
  productionSourceMap: false
}