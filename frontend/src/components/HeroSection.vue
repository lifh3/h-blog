<template>
  <section class="relative h-screen flex items-center justify-center overflow-hidden">
    <canvas ref="canvas" class="absolute inset-0 w-full h-full"></canvas>
    
    <div class="absolute inset-0 bg-gradient-to-b from-cyber-dark/50 via-transparent to-cyber-dark"></div>
    
    <div class="relative z-10 text-center px-4">
      <h1 class="font-cyber text-5xl md:text-7xl font-bold mb-6">
        <span class="gradient-text">TechBlog</span>
      </h1>
      <p class="text-xl md:text-2xl text-text-secondary mb-4 font-light">
        探索科技的边界 · 记录代码的艺术
      </p>
      <p class="text-text-muted mb-8 max-w-2xl mx-auto">
        分享前沿技术、编程心得与创意灵感
      </p>
      <div class="flex justify-center space-x-4">
        <router-link to="/#articles" class="btn-cyber">
          开始探索
          <svg class="inline-block w-4 h-4 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"/>
          </svg>
        </router-link>
      </div>
    </div>

    <div class="absolute bottom-10 left-1/2 -translate-x-1/2 animate-bounce">
      <svg class="w-6 h-6 text-neon-blue" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"/>
      </svg>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const canvas = ref(null)
let animationId = null
let particles = []

class Particle {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }
  
  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.size = Math.random() * 2 + 0.5
    this.speedX = (Math.random() - 0.5) * 0.5
    this.speedY = (Math.random() - 0.5) * 0.5
    this.opacity = Math.random() * 0.5 + 0.2
    this.hue = Math.random() > 0.5 ? 180 : 280
  }
  
  update() {
    this.x += this.speedX
    this.y += this.speedY
    if (this.x < 0 || this.x > this.canvas.width) this.speedX *= -1
    if (this.y < 0 || this.y > this.canvas.height) this.speedY *= -1
  }
  
  draw(ctx) {
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fillStyle = `hsla(${this.hue}, 100%, 65%, ${this.opacity})`
    ctx.fill()
  }
}

function initCanvas() {
  const c = canvas.value
  if (!c) return
  const ctx = c.getContext('2d')
  
  function resize() {
    c.width = window.innerWidth
    c.height = window.innerHeight
    initParticles()
  }
  
  function initParticles() {
    particles = []
    const count = Math.min(100, Math.floor((c.width * c.height) / 15000))
    for (let i = 0; i < count; i++) {
      particles.push(new Particle(c))
    }
  }
  
  function drawLines() {
    for (let i = 0; i < particles.length; i++) {
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[i].x - particles[j].x
        const dy = particles[i].y - particles[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 150) {
          ctx.beginPath()
          ctx.moveTo(particles[i].x, particles[i].y)
          ctx.lineTo(particles[j].x, particles[j].y)
          ctx.strokeStyle = `rgba(0, 240, 255, ${0.1 * (1 - dist / 150)})`
          ctx.lineWidth = 0.5
          ctx.stroke()
        }
      }
    }
  }
  
  function animate() {
    ctx.clearRect(0, 0, c.width, c.height)
    
    particles.forEach(p => {
      p.update()
      p.draw(ctx)
    })
    
    drawLines()
    animationId = requestAnimationFrame(animate)
  }
  
  resize()
  window.addEventListener('resize', resize)
  animate()
}

onMounted(() => {
  initCanvas()
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  window.removeEventListener('resize', () => {})
})
</script>
