import { metrics } from "@opentelemetry/api";
const meter = metrics.getMeter('http-server');
import process from "node:process";
import { remixFastify } from "@mcansh/remix-fastify";
import { installGlobals } from "@remix-run/node";
import { fastify } from "fastify";

installGlobals();

const port = process.env.SERVICE_PORT || 8080;
const baseURL = process.env.BASE_URL || '/tools.descartes.teastore.webui/'
const host = process.env.SERVICE_HOST || "localhost"

const app = fastify();

await app.register(remixFastify);
// Register the caching plugin

// Create a histogram for http duration
const httpDurationHistogram = meter.createHistogram('http_server_duration', {
  description: 'Duration of HTTP requests',
  unit: 'ms',
});

// Middleware to record HTTP duration with specific route
app.addHook('onRequest', (request, reply, done) => {
  request.startTime = Date.now();
  done();
});

app.addHook('onResponse', (request, reply, done) => {
  const duration = Date.now() - request.startTime;
  const route = request.routerPath || 'unknown';
  httpDurationHistogram.record(duration, {
    'http.route': route,
    'http.url': request.url,
    'http.method': request.method,
    'http.status_code': reply.statusCode.toString(),
  });
  done();
});



// const viteDevServer =
//   process.env.NODE_ENV === "production"
//     ? null
//     : await import("vite").then((vite) =>
//       vite.createServer({
//         server: { middlewareMode: true },
//       })
//     );
//
// const app = express();
//
// app.use(
//   viteDevServer
//     ? viteDevServer.middlewares
//     : express.static("build/client")
// );
//
// const build = viteDevServer
//   ? () =>
//     viteDevServer.ssrLoadModule(
//       "virtual:remix/server-build"
//     )
//   : await import("./build/server/index.js");
//
// app.use(recordHttpDuration);
// app.get("/health", (req, res) => {
//   res.status(200).json({ status: "UP" });
// });
//
// // this need to be placed before the createRequestHandler
// app.get("/", (req, res) => {
//   res.redirect(301, baseURL);
// });
//
// app.all("*", createRequestHandler({ build }));
//
//
// app.listen(port, () => {
//   console.log(`App listening on http://${host}:${port} in ${process.env.NODE_ENV || "development"} mode`);
// });

app.get("/health", async (request, reply) => {
  return { status: "UP" };
});

app.get("/", async (request, reply) => {
  reply.redirect(301, baseURL);
});

const start = async () => {
  try {
    await app.listen({ port, host });
    console.log("App powered by Fastify");
    console.log(`App listening on http://${host}:${port} in ${process.env.NODE_ENV || "development"} mode`);
  } catch (err) {
    app.log.error(err);
    process.exit(1);
  }
};

start();