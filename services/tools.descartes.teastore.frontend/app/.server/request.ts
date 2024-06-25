export function createHTTPFetcher(serviceName: string, path: string, fetchOptions: object = {}): Promise<Response> {
  let url = process.env[`${serviceName.toUpperCase()}_HOST`]; 
  let port = process.env[`${serviceName.toUpperCase()}_PORT`];
  if (!url) {
    throw new Error(`Service ${serviceName} not found`);
  }
  if (!port) {
    throw new Error(`Port for service ${serviceName} not found`);
  }
  url = `http://${url}:${port}/tools.descartes.teastore.${serviceName.toLowerCase()}/rest/${path}`;
  return fetch(url, fetchOptions)
}