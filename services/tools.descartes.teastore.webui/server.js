import { createRequestHandler } from "@remix-run/express";
import express from "express";

const port = process.env.SERVICE_PORT || 8080;
const baseURL = process.env.BASE_URL || '/tools.descartes.teastore.webui/'

const viteDevServer =
  process.env.NODE_ENV === "production"
    ? null
    : await import("vite").then((vite) =>
        vite.createServer({
          server: { middlewareMode: true },
        })
      );

const app = express();
app.use(
  viteDevServer
    ? viteDevServer.middlewares
    : express.static("build/client")
);

const build = viteDevServer
  ? () =>
      viteDevServer.ssrLoadModule(
        "virtual:remix/server-build"
      )
  : await import("./build/server/index.js");

// this need to be placed before the createRequestHandler
app.get("/", (req, res) => {
  res.redirect(301, baseURL);
});

app.all("*", createRequestHandler({ build }));


app.listen(port, () => {
  console.log(`App listening on http://localhost:${port}`);
});
