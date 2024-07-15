import http from 'k6/http';
import config from "../../../config/endpoints.js"
import { options as workLoadOpts } from "../../../config/workload.js" 
import { sleep } from 'k6';

const persistenceConfig = config.persistence;
export const options = {
  stages: workLoadOpts.stages
};

// In default, there are 10 categories, each category has 500 products
const categoryIds = [2,3,4,5,6,7,8,9,10,11]
const startIndexes = Array.from({length: 25}, (v, k) => k * 20) 
const maxProductsPerPage = 20

export function setup() {
  // setup code
}

export default function () {
  let cid = categoryIds[Math.floor(Math.random() * categoryIds.length)]
  let idx = startIndexes[Math.floor(Math.random() * startIndexes.length)]
  http[persistenceConfig.getProductsByCategory.method.toLowerCase()](persistenceConfig.base + persistenceConfig.getProductsByCategory.url + "/" + cid + "?start=" + idx + "&max=" + maxProductsPerPage);
  sleep(1);
}

export function teardown(data) {
  // teardown code
}