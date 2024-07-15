import http from "k6/http";
import config from "../../../config/endpoints.js";
import { options as workLoadOpts } from "../../../config/workload.js";
import { sleep } from "k6";

const authConfig = config.auth;
export const options = {
  stages: workLoadOpts.stages,
};

export function setup() {
  // setup code
}

export default function () {
  let data = {
    uid: null,
    sid: null,
    token: null,
    order: {
      id: 0,
      userId: 0,
      time: null,
      totalPriceInCents: 0,
      addressName: null,
      address1: null,
      address2: null,
      creditCardCompany: null,
      creditCardNumber: null,
      creditCardExpiryDate: null,
    },
    orderItems: [],
    message: null,
  };
  http[authConfig.isLoggedIn.method.toLowerCase()](
    authConfig.base + authConfig.isLoggedIn.url,
    JSON.stringify(data),
    {
      headers: { "Content-Type": "application/json" },
    }
  );
  sleep(1);
}

export function teardown(data) {
  // teardown code
}
