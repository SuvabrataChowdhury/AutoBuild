import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react-swc'
import path from "path";

// https://vite.dev/config/
//TODO: Thresholds to be increased after adding more tests
const COVERAGE_THRESHOLDS = {
  lines: 75,
  functions: 65,
  branches: 65,
  statements: 75,
};

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src")
    }
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: path.resolve(__dirname, 'test/setup.ts'),
    include: ['test/**/*.test.{ts,tsx}', 'test/**/*.spec.{ts,tsx}'],
    coverage: {
      provider: 'v8',
      reporter: ['text'],
      reportsDirectory: path.resolve(__dirname, 'coverage'),
      thresholds: COVERAGE_THRESHOLDS,
      exclude: ['./src/gen/**.ts', './src/**/*.css', './src/services/*.ts']
    },
    hookTimeout: 30000
  }
})
