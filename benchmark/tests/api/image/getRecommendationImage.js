import http from 'k6/http';
import config from "../../../config/endpoints.js"
import { options as workLoadOpts } from "../../../config/workload.js" 
import { sleep } from 'k6';

const imageConfig = config.image;
const persistenceConfig = config.persistence;
export const options = {
  stages: workLoadOpts.stages
};

export function setup() {
  // setup code
  const allProducts = http[persistenceConfig.getProducts.method.toLowerCase()](persistenceConfig.base + persistenceConfig.getProducts.url).json();
  return allProducts;
}

export default function (allProducts) {
  const pid = allProducts[Math.floor(Math.random() * allProducts.length)].id;
  http[imageConfig.getRecommendationImage.method.toLowerCase()](`${imageConfig.base}${imageConfig.getRecommendationImage.url}/${pid}.png`);
  sleep(1);
}

export function teardown(data) {
  // teardown code
}