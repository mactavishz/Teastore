import { createRequestHandler } from "@remix-run/express";
import express from "express";
import { metrics } from "@opentelemetry/api";
const meter = metrics.getMeter('http-server');

// Create a histogram for http duration
const httpDurationHistogram = meter.createHistogram('http_server_duration', {
  description: 'Duration of HTTP requests',
  unit: 'ms',
});

// Middleware to record HTTP duration with specific route
function recordHttpDuration(req, res, next) {
  const start = Date.now();
  res.once('finish', () => {
    const duration = Date.now() - start;
    const route = req.route ? req.route.path : 'unknown';
    httpDurationHistogram.record(duration, {
      'http.route': route,
      'http.url': req.originalUrl,
      'http.method': req.method,
      'http.status_code': res.statusCode.toString(),
    });
  });
  next();
}

const port = process.env.SERVICE_PORT || 8080;
const baseURL = process.env.BASE_URL || '/tools.descartes.teastore.webui/'
const host = process.env.SERVICE_HOST || "localhost"

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

app.use(recordHttpDuration);
app.get("/health", (req, res) => {
  res.status(200).json({ status: "UP" });
});

// this need to be placed before the createRequestHandler
app.get("/", (req, res) => {
  res.redirect(301, baseURL);
});

app.all("*", createRequestHandler({ build }));


app.listen(port, () => {
  console.log(`App listening on http://${host}:${port}`);
});
