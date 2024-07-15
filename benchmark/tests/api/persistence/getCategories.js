import http from 'k6/http';
import config from "../../../config/endpoints.js"
import { options as workLoadOpts } from "../../../config/workload.js" 
import { sleep } from 'k6';

const persistenceConfig = config.persistence;
export const options = {
  stages: workLoadOpts.stages
};

export function setup() {
  // setup code
}

export default function () {
  http[persistenceConfig.getCategories.method.toLowerCase()](persistenceConfig.base + persistenceConfig.getCategories.url);
  sleep(1);
}

export function teardown(data) {
  // teardown code
}