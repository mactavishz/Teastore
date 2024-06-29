import { vitePlugin as remix } from "@remix-run/dev";
import { installGlobals } from "@remix-run/node";
import { defineConfig } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

installGlobals();

export default defineConfig({
  plugins: [
    remix({
      basename: process.env.BASE_URL || "/tools.descartes.teastore.webui/"
    }),
    tsconfigPaths()
  ],
  server: {
    hmr: {
      port: process.env.WEBUI_HMR_PORT
    }
  }
});
