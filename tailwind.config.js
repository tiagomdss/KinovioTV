/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./app/app.vue",
    "./app/components/**/*.{vue,js,ts}",
    "./app/layouts/**/*.vue",
    "./app/pages/**/*.vue",
    "./app/plugins/**/*.{js,ts}",
    "./app/utils/**/*.{js,ts}",
    "./nuxt.config.{js,ts}"
  ],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      colors: {
        cinema: {
          deep: '#08090B',
          base: '#0D0F13',
          elevated: '#111318',
          surface: '#1A1D23',
          card: '#16161f',
        },
        accent: {
          DEFAULT: '#7A4DFF',
          hover: '#916DFF',
          glow: 'rgba(122, 77, 255, 0.24)',
          soft: 'rgba(122, 77, 255, 0.10)',
        },
        brand: {
          DEFAULT: '#D94CFF',
          glow: 'rgba(217, 76, 255, 0.18)',
        },
        glass: {
          DEFAULT: 'rgba(255, 255, 255, 0.05)',
          border: 'rgba(255, 255, 255, 0.08)',
          hover: 'rgba(255, 255, 255, 0.10)',
        },
        text: {
          primary: '#F6F6F8',
          secondary: '#A5A8B2',
          muted: '#737782',
        },
      },
      borderRadius: {
        'card': '16px',
        '2xl': '16px',
        '3xl': '20px',
        '4xl': '24px',
      },
      boxShadow: {
        'glow-accent': '0 0 24px rgba(122, 77, 255, 0.28)',
        'glow-brand': '0 0 24px rgba(217, 76, 255, 0.22)',
        'glow-success': '0 0 15px rgba(16, 185, 129, 0.25)',
        'premium': '0 10px 40px -10px rgba(0, 0, 0, 0.6)',
        'card': '0 4px 24px -4px rgba(0, 0, 0, 0.4)',
        'hero': '0 20px 60px -20px rgba(0, 0, 0, 0.8)',
      },
      backdropBlur: {
        'glass': '20px',
        'heavy': '40px',
      },
      animation: {
        'shimmer': 'shimmer 2s infinite linear',
        'float': 'float 6s ease-in-out infinite',
        'glow-pulse': 'glow-pulse 2s ease-in-out infinite',
        'slide-up': 'slide-up 0.4s cubic-bezier(0.16, 1, 0.3, 1)',
        'slide-down': 'slide-down 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        'fade-in': 'fade-in 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        'scale-in': 'scale-in 0.25s cubic-bezier(0.16, 1, 0.3, 1)',
        'hero-ken-burns': 'ken-burns 20s ease-in-out infinite alternate',
      },
      keyframes: {
        shimmer: {
          '0%': { transform: 'translateX(-100%)' },
          '100%': { transform: 'translateX(100%)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-8px)' },
        },
        'glow-pulse': {
          '0%, 100%': { opacity: '0.4' },
          '50%': { opacity: '0.8' },
        },
        'slide-up': {
          '0%': { transform: 'translateY(16px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        'slide-down': {
          '0%': { transform: 'translateY(-8px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        'fade-in': {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        'scale-in': {
          '0%': { transform: 'scale(0.95)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
        'ken-burns': {
          '0%': { transform: 'scale(1) translate(0, 0)' },
          '100%': { transform: 'scale(1.08) translate(-1%, -1%)' },
        },
      },
      transitionTimingFunction: {
        'smooth': 'cubic-bezier(0.16, 1, 0.3, 1)',
      },
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
        '120': '30rem',
      },
    },
  },
  plugins: [],
}
