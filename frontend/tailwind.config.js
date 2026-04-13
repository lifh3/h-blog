/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'cyber-dark':    '#0f1419',
        'cyber-gray':    '#1a2332',
        'cyber-card':    '#1a2332',
        'cyber-elevated':'#212d40',
        'cyber-border':  '#2d3a4f',
        'neon-blue':     '#4a9eaf',
        'neon-purple':   '#8a6ab8',
        'neon-green':    '#4a9e6a',
        'neon-yellow':   '#b8963a',
        'neon-pink':     '#b87070',
        'text-primary':  '#e8eef4',
        'text-secondary':'#a8b8c8',
        'text-muted':    '#5c7080',
      },
      fontFamily: {
        'cyber': ['"Orbitron"', 'sans-serif'],
        'tech': ['"JetBrains Mono"', 'monospace'],
      },
      boxShadow: {
        'card':   '0 4px 20px rgba(0,0,0,0.4)',
        'accent': '0 0 16px rgba(74,158,175,0.25)',
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
      },
    },
  },
  plugins: [
    function ({ addUtilities }) {
      addUtilities({
        '.neon-text': {
          color: '#4a9eaf',
          'text-shadow': '0 0 8px rgba(74,158,175,0.5)',
        },
      })
    },
  ],
}
