import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react-swc'
import path from "path";
import fs from "fs";

// https://vite.dev/config/
//TODO: Thresholds to be increased after adding more tests
const COVERAGE_THRESHOLDS = {
  lines: 50,
  functions: 50,
  branches: 50,
  statements: 50,
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
      thresholds: COVERAGE_THRESHOLDS
    },
    hookTimeout: 30000
  }
})
