import { metrics } from "@opentelemetry/api";
const meter = metrics.getMeter('http-client');

// Create a histogram for http client duration
const httpClientDurationHistogram = meter.createHistogram('http_client_duration', {
  description: 'Duration of HTTP client requests',
  unit: 'ms',
});

// Function to record HTTP client metrics
function recordHttpClientMetrics(method: string, url: string, statusCode: number, duration: number) {
  const parsedUrl = new URL(url);
  httpClientDurationHistogram.record(duration, {
    'http.method': method,
    'http.status_code': statusCode.toString(),
    'http.url': parsedUrl.pathname,
    'net.peer.name': parsedUrl.hostname,
    'net.peer.port': parsedUrl.port || (parsedUrl.protocol === 'https:' ? '443' : '80'),
  });
}

export function buildStaticImageURL(path: string): string {
  const serviceName = "image_cdn"
  let url = process.env[`${serviceName.toUpperCase()}_HOST`];
  let port = process.env[`${serviceName.toUpperCase()}_PORT`];
  if (!url) {
    throw new Error(`Service ${serviceName} not found`);
  }
  if (!port) {
    throw new Error(`Port for service ${serviceName} not found`);
  }
  url = `http://${url}:${port}/tools.descartes.teastore.image/${path}`;
  return url
}

export function buildURL(serviceName: string, path: string): string {
  let url = process.env[`${serviceName.toUpperCase()}_HOST`];
  let port = process.env[`${serviceName.toUpperCase()}_PORT`];
  if (!url) {
    throw new Error(`Service ${serviceName} not found`);
  }
  if (!port) {
    throw new Error(`Port for service ${serviceName} not found`);
  }
  url = `http://${url}:${port}/tools.descartes.teastore.${serviceName.toLowerCase()}/rest/${path}`;
  return url
}

export async function createPOSTFetcher(serviceName: string, path: string, data: object, fetchOptions: object = {}): Promise<Response> {
  const startTime = Date.now();
  const url = buildURL(serviceName, path);
  const method = "POST";
  try {
    const response = await fetch(url, {
      method,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
      ...fetchOptions,
    })
    const duration = Date.now() - startTime;
    recordHttpClientMetrics(method, url, response.status, duration);
    return response;
  } catch (err) {
    const duration = Date.now() - startTime;
    recordHttpClientMetrics(method, url, 0, duration); // Use 0 or another appropriate value for failed requests
    throw err;
  }
}

export async function createPutFetcher(serviceName: string, path: string, data: object, fetchOptions: object = {}): Promise<Response> {
  const startTime = Date.now();
  const method = "PUT";
  const url = buildURL(serviceName, path);
  try {
    const response = await fetch(url, {
      method,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
      ...fetchOptions,
    })
    const duration = Date.now() - startTime;
    recordHttpClientMetrics(method, url, response.status, duration);
    return response;
  } catch (err) {
    const duration = Date.now() - startTime;
    recordHttpClientMetrics(method, url, 0, duration); // Use 0 or another appropriate value for failed requests
    throw err;
  }
}

export async function createGETFetcher(serviceName: string, path: string, data: { [x: string]: string | number; } = {}, fetchOptions: object = {}): Promise<Response> {
  const url = new URL(buildURL(serviceName, path));

  for (const key in data) {
    url.searchParams.append(key, String(data[key]));
  }
  const startTime = Date.now();
  const method = "GET";
  try {
    const response = await fetch(url, {
      method,
      ...fetchOptions,
    })
    const duration = Date.now() - startTime;
    recordHttpClientMetrics(method, url.toString(), response.status, duration);
    return response;
  } catch (err) {
    const duration = Date.now() - startTime;
    recordHttpClientMetrics(method, url.toString(), 0, duration); // Use 0 or another appropriate value for failed requests
    throw err;
  }
}