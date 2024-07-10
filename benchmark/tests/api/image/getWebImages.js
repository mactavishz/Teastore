import http from 'k6/http';
import config from "../../../config/endpoints.js"
import { options as workLoadOpts } from "../../../config/workload.js" 
import { getRandomItems } from "../../../utils.js"
import { sleep } from 'k6';

const imageConfig = config.image;
const persistenceConfig = config.persistence;
export const options = {
  stages: workLoadOpts.stages
};

const ProductImagePreviewSize = "64x64";
const maxProducts = 20

export function setup() {
  // setup code
  const allProducts = http[persistenceConfig.getProducts.method.toLowerCase()](persistenceConfig.base + persistenceConfig.getProducts.url).json();
  return allProducts;
}

export default function (allProducts) {
  const pids = getRandomItems(allProducts, maxProducts).map(p => p.id)
  let data = {};
  for (let pid of pids) {
    data[pid] = ProductImagePreviewSize;
  }
  http[imageConfig.getWebImages.method.toLowerCase()](imageConfig.base + imageConfig.getWebImages.url, JSON.stringify(data), {
    headers: { 'Content-Type': 'application/json' }
  });
  sleep(1);
}

export function teardown(data) {
  // teardown code
}