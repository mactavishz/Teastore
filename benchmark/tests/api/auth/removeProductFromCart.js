import http from "k6/http";
import config from "../../../config/endpoints.js";
import { options as workLoadOpts } from "../../../config/workload.js";
import { sleep } from "k6";

const authConfig = config.auth;
const persistenceConfig = config.persistence;
export const options = {
  stages: workLoadOpts.stages,
};

export function setup() {
  // setup code
  const allProducts = http[persistenceConfig.getProducts.method.toLowerCase()](
    persistenceConfig.base + persistenceConfig.getProducts.url
  ).json();
  const blob = addProductToCart(allProducts);
  return blob
}

function addProductToCart (allProducts) {
  const product = allProducts[Math.floor(Math.random() * allProducts.length)];
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
    orderItems: [
      { id: 0, productId: product.id, orderId: 0, quantity: 1, unitPriceInCents: product.listPriceInCents },
    ],
    message: null,
  };
  const res = http[authConfig.addToCart.method.toLowerCase()](
    authConfig.base + authConfig.addToCart.url + `/${product.id}`,
    JSON.stringify(data),
    {
      headers: { "Content-Type": "application/json" },
    }
  );
  return res.json()
}

export default function (blob) {
  http[authConfig.removeProductFromCart.method.toLowerCase()](
    authConfig.base + authConfig.removeProductFromCart.url + `/${blob.orderItems[0].productId}`,
    JSON.stringify(blob),
    {
      headers: { "Content-Type": "application/json" },
    }
  );
  sleep(1);
}

export function teardown(data) {
  // teardown code
}
