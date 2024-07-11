import { createRequire } from 'module';
const require = createRequire(import.meta.url);

// Load the CJS tracing module
require('./tracing.cjs');

// Now import and run the ESM server
import('./server.js');