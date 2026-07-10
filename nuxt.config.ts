// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-04-03',
  future: {
    compatibilityVersion: 4,
  },
  modules: [
    '@nuxtjs/tailwindcss'
  ],
  css: [
    '~/assets/css/main.css'
  ],
  devtools: { enabled: false },
  devServer: {
    port: 3001
  },
  app: {
    head: {
      title: 'KinovioTV — Cinema Pessoal',
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1, maximum-scale=5, viewport-fit=cover' },
        { name: 'description', content: 'KinovioTV — Seu cinema pessoal com agregação inteligente de múltiplas fontes. Filmes, séries, animes, K-dramas e TV ao vivo.' },
        { name: 'theme-color', content: '#020203' },
        { name: 'apple-mobile-web-app-capable', content: 'yes' },
        { name: 'apple-mobile-web-app-status-bar-style', content: 'black-translucent' },
        { name: 'mobile-web-app-capable', content: 'yes' },
        { property: 'og:title', content: 'KinovioTV — Cinema Pessoal' },
        { property: 'og:description', content: 'Seu cinema pessoal premium com filmes, séries, animes e TV ao vivo.' },
        { property: 'og:type', content: 'website' },
      ],
      link: [
        { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
        { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' },
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap' },
        { rel: 'preconnect', href: 'https://image.tmdb.org' },
      ]
    }
  }
})
