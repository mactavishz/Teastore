export function buildStaticURL(serviceName: string, path: string): string {
  let url = process.env[`${serviceName.toUpperCase()}_HOST`]; 
  let port = process.env[`${serviceName.toUpperCase()}_PORT`];
  if (!url) {
    throw new Error(`Service ${serviceName} not found`);
  }
  if (!port) {
    throw new Error(`Port for service ${serviceName} not found`);
  }
  url = `http://${url}:${port}/tools.descartes.teastore.${serviceName.toLowerCase()}/${path}`;
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

export function createPOSTFetcher(serviceName: string, path: string, data: object, fetchOptions: object = {}): Promise<Response> {
  const url = buildURL(serviceName, path);
  return fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
    ...fetchOptions,
  })
}

export function createPutFetcher(serviceName: string, path: string, data: object, fetchOptions: object = {}): Promise<Response> {
  const url = buildURL(serviceName, path);
  return fetch(url, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
    ...fetchOptions,
  })
}

export function createGETFetcher(serviceName: string, path: string, data: { [x: string]: string | number; } = {}, fetchOptions: object = {}): Promise<Response> {
  const url = new URL(buildURL(serviceName, path)); 
  
  for (const key in data) {
    url.searchParams.append(key, String(data[key]));
  }

  return fetch(url, {
    method: "GET",
    ...fetchOptions,
  })
}