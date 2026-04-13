/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}'
  ],
  theme: {
    extend: {
      colors: {
        'white': '#ffffff',
        'gray-100': '#f3f4f6',
        'gray-200': '#e5e7eb',
        'gray-300': '#d1d5db',
        'cyber': {
          bg:     '#0f1419',
          surface:'#1a2332',
          border: '#2d3a4f',
          cyan:   '#4a9eaf',   // 柔和蓝绿
          blue:   '#3a8ab0',
          purple: '#8a6ab8',   // 柔和薰衣草紫
          green:  '#4a9e6a',   // 柔和绿
          red:    '#b85a5a',
          yellow: '#b8963a',
          text:   '#e8eef4',
          muted:  '#9aacba',   // 调亮，从 #6a8090 改为高对比度版本
        }
      },
      backgroundImage: {
        'gradient-cyber': 'linear-gradient(135deg, #4a9eaf 0%, #8a6ab8 100%)',
        'gradient-dark':  'linear-gradient(180deg, #1a2332 0%, #0f1419 100%)'
      }
    }
  },
  plugins: []
}
