// Extend Testing Library matchers
import '@testing-library/jest-dom';

// Clean up DOM between tests
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';
afterEach(() => cleanup());
