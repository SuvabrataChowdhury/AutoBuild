// Extend Testing Library matchers
import '@testing-library/jest-dom';

// Clean up DOM between tests
import { cleanup } from '@testing-library/react';
import { afterEach, beforeEach, vi } from 'vitest';

beforeEach(() => {
  vi.stubEnv('VITE_IDP', 'keycloak');  // Default
});

afterEach(() => cleanup());
