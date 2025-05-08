import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/observa-api': {
        target: 'http://k8s.orb.local',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
