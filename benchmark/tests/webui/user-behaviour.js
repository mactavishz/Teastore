import http from "k6/http";
import exec from "k6/execution";
import { Trend } from "k6/metrics";
import config from "../../config/endpoints.js";
import { options as workLoadOpts } from "../../config/workload.js";
import { choice, randomInt } from "../../utils.js";
import { sleep, check } from "k6";

const webuiConfig = config.webui;
const CATEGORY_COUNT = 10;
const PRODUCT_COUNT_PER_CATEGORY = 500;
const PAGE_COUNT = 25;

export const options = {
  stages: workLoadOpts.stages,
};

const loginPageTrend = new Trend("visit_login_page_time", true);
const loginTrend = new Trend("login_time", true);
const categoryTrend = new Trend("visit_category_page_time", true);
const productTrend = new Trend("visit_product_page_time", true);
const orderTrend = new Trend("visit_order_page_time", true);
const cartTrend = new Trend("visit_cart_page_time", true);
const placeOrderTrend = new Trend("place_order_time", true);
const addToCartTrend = new Trend("add_to_cart_time", true);
const removeFromCartTrend = new Trend("remove_from_cart_time", true);
const logoutTrend = new Trend("logout_time", true);
const visitHomePageTrend = new Trend("visit_home_page_time", true);

// make sure to have a available number of users
const getUserName = () => `user${exec.vu.idInTest}`;

export function setup() {
  // setup code
  return {
    isLoggedIn: false,
    cartProductIds: [],
  };
}

export default function (data) {
  visitHomePage(data);
  let login_choice = choice();
  if (login_choice) {
    login(data);
  }
  browse(data);
  placeOrder(data);
  profile(data);
  logout(data);
  console.log("User behavior completed");
}

function visitHomePage(data) {
  const res = http[webuiConfig.home.method.toLowerCase()](
    webuiConfig.base + webuiConfig.home.url,
  );
  visitHomePageTrend.add(res.timings.duration);
  let ok = check(res, {
    "status is ok": (r) => r.status < 400,
  });
  if (!ok) {
    console.log(`Failed to visit home page with status ${res.status}`);
  } else {
    console.log("Visited home page");
  }
  sleep(1);
}

function browse(data) {
  for (let i = 0; i < randomInt(1, 10); i++) {
    let randCategoryId = randomInt(2, CATEGORY_COUNT + 1);
    let randPageId = randomInt(1, PAGE_COUNT);
    let res = http[webuiConfig.category.method.toLowerCase()](
      webuiConfig.base +
        webuiConfig.category.url +
        `?category=${randCategoryId}&page=${randPageId}`,
    );
    categoryTrend.add(res.timings.duration);
    let ok = check(res, {
      "status is ok": (r) => r.status < 400,
    });
    if (!ok) {
      console.log(
        `Failed to browse category ${randCategoryId} page ${randPageId} with status ${res.status}`,
      );
      sleep(1);
    } else {
      console.log(
        `Browsed category ${randCategoryId} page ${randPageId} with status ${res.status}`,
      );
      sleep(1);
      // browse 2 random products
      for (let i = 0; i < 5; i++) {
        let randProductId = randomInt(
          CATEGORY_COUNT + 2,
          PRODUCT_COUNT_PER_CATEGORY * CATEGORY_COUNT,
        );
        res = http[webuiConfig.product.method.toLowerCase()](
          webuiConfig.base +
            webuiConfig.product.url +
            `?product=${randProductId}`,
        );
        productTrend.add(res.timings.duration);
        ok = check(res, {
          "status is ok": (r) => r.status < 400,
        });
        if (!ok) {
          console.log(
            `Failed to browse product ${randProductId} with status ${res.status}`,
          );
        } else {
          console.log(
            `Browsed product ${randProductId} with status ${res.status}`,
          );
        }
        sleep(1);
        let addToCartChoice = choice();
        // 50% chance to add product to cart
        if (addToCartChoice) {
          res = http[webuiConfig.cartAction.method.toLowerCase()](
            webuiConfig.base + webuiConfig.cartAction.url,
            {
              addToCart: "addToCart",
              productid: randProductId,
            },
          );
          addToCartTrend.add(res.timings.duration);
          ok = check(res, {
            "status is ok": (r) => r.status < 400,
          });
          if (!ok) {
            console.log(
              `Failed to add product ${randProductId} to cart with status ${res.status}`,
            );
          } else {
            data.cartProductIds = [...data.cartProductIds, randProductId];
            console.log(
              `Added product ${randProductId} to cart with status ${res.status}`,
            );
          }
          sleep(1);
        }
      }
    }
  }
}

function placeOrder(data) {
  const user_data = {
    firstname: "User",
    lastname: "User",
    address1: "Road",
    address2: "City",
    cardtype: "volvo",
    cardnumber: "314159265359",
    expirydate: "12/2050",
    confirm: "Confirm",
  };
  if (!data.isLoggedIn || data.cartProductIds.length === 0) {
    return;
  }
  let res = http[webuiConfig.cart.method.toLowerCase()](
    webuiConfig.base + webuiConfig.cart.url,
  );
  cartTrend.add(res.timings.duration);
  let ok = check(res, {
    "status is ok": (r) => r.status < 400,
  });
  if (!ok) {
    console.log(`Failed to visit cart page with status ${res.status}`);
  } else {
    console.log(`Visited cart page with status ${res.status}`);
  }
  sleep(1);
  let removeProductChoice = choice();
  // 50% chance to remove one product from cart
  if (removeProductChoice) {
    const rmPid =
      data.cartProductIds[randomInt(0, data.cartProductIds.length - 1)];
    data.cartProductIds = data.cartProductIds.filter((pid) => pid !== rmPid);
    if (rmPid) {
      let formData = {};
      formData[`removeProduct_${rmPid}`] = "whatever";
      res = http[webuiConfig.cartAction.method.toLowerCase()](
        webuiConfig.base + webuiConfig.cartAction.url,
        formData,
      );
      removeFromCartTrend.add(res.timings.duration);
      ok = check(res, {
        "status is ok": (r) => r.status < 400,
      });
      if (!ok) {
        console.log(
          `Failed to remove product ${rmPid} from cart with status ${res.status}`,
        );
      } else {
        console.log(
          `Removed product ${rmPid} from cart with status ${res.status}`,
        );
      }
      sleep(1);
    }
  }
  if (data.cartProductIds.length === 0) {
    return;
  }
  res = http[webuiConfig.order.method.toLowerCase()](
    webuiConfig.base + webuiConfig.order.url,
  );
  orderTrend.add(res.timings.duration);
  ok = check(res, {
    "status is ok": (r) => r.status < 400,
  });
  if (!ok) {
    console.log(`Failed to visit order page with status ${res.status}`);
  } else {
    console.log(`Visited order page with status ${res.status}`);
  }
  sleep(1);
  res = http[webuiConfig.cartAction.method.toLowerCase()](
    webuiConfig.base + webuiConfig.cartAction.url,
    user_data,
  );
  placeOrderTrend.add(res.timings.duration);
  ok = check(res, {
    "status is ok": (r) => r.status < 400,
  });
  if (!ok) {
    console.log(`Failed to place order with status ${res.status}`);
  } else {
    console.log(`Placed order with status ${res.status}`);
  }
  sleep(1);
}

function login(data) {
  let res = http[webuiConfig.login.method.toLowerCase()](
    webuiConfig.base + webuiConfig.login.url,
  );
  loginPageTrend.add(res.timings.duration);
  let ok = check(res, {
    "status is ok": (r) => r.status < 400,
  });
  if (!ok) {
    console.log(`Failed to visit login page with status ${res.status}`);
  } else {
    console.log(`Visited login page with status ${res.status}`);
    sleep(1);
    let password = webuiConfig.userpasswd;
    let username = getUserName();
    res = http.post(webuiConfig.base + webuiConfig.loginAction.url, {
      username,
      password,
    });
    loginTrend.add(res.timings.duration);
    ok = check(res, {
      "status is ok": (r) => r.status < 400,
    });
    if (!ok) {
      console.log(`Failed to login with status ${res.status}`);
    } else {
      data.isLoggedIn = true;
      console.log(`User ${username} logged in with status ${res.status}`);
    }
  }
  sleep(1);
}

function profile(data) {
  let res = http[webuiConfig.profile.method.toLowerCase()](
    webuiConfig.base + webuiConfig.profile.url,
  );
  let ok = check(res, {
    "status is ok": (r) => r.status < 400,
  });
  if (!ok) {
    console.log(`Failed to visit profile page with status ${res.status}`);
  } else {
    console.log(`Visited profile page with status ${res.status}`);
  }
  sleep(1);
}

function logout(data) {
  let res = http[webuiConfig.loginAction.method.toLowerCase()](
    webuiConfig.base + webuiConfig.loginAction.url,
    { logout: "" },
  );
  logoutTrend.add(res.timings.duration);
  let ok = check(res, {
    "status is ok": (r) => r.status < 400,
  });
  if (!ok) {
    console.log(`Failed to logout with status ${res.status}`);
  } else {
    data.isLoggedIn = false;
    const username = getUserName();
    console.log(`User ${username} logged out with status ${res.status}`);
  }
}

export function teardown() {
  // teardown code
  // logout();
}
