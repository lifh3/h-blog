<template>
  <div class="min-h-screen bg-cyber-dark cyber-grid-bg">
    <TechNavbar />
    <main>
      <router-view />
    </main>
    <footer class="border-t border-cyber-border py-8 mt-16">
      <div class="max-w-7xl mx-auto px-4 text-center text-text-muted">
        <p>&copy; 2026 TechBlog. Built with Vue + Cyberpunk Style.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import TechNavbar from '@/components/TechNavbar.vue'
</script>
