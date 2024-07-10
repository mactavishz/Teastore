export default {
  persistence: {
    base: getBaseURL("persistence"),
    getCategories: {
      url: "/tools.descartes.teastore.persistence/rest/categories",
      method: "GET",
    },
    getProductsByCategory: {
      url: "/tools.descartes.teastore.persistence/rest/products/category",
      method: "GET",
    },
    getProducts: {
      url: "/tools.descartes.teastore.persistence/rest/products",
      method: "GET",
    },
  },
  auth: {
    base: getBaseURL("auth"),
    addToCart: {
      url: "/tools.descartes.teastore.auth/rest/cart/add",
      method: "POST",
    },
    updateCart: {
      url: "/tools.descartes.teastore.auth/rest/cart",
      method: "PUT",
    },
    removeProductFromCart: {
      url: "/tools.descartes.teastore.auth/rest/cart/remove",
      method: "POST",
    },
    isLoggedIn: {
      url: "/tools.descartes.teastore.auth/rest/useractions/isloggedin",
      method: "POST",
    },
    placeOrder: {
      url: "/tools.descartes.teastore.auth/rest/useractions/placeorder",
      method: "POST",
    },
    login: {
      url: "/tools.descartes.teastore.auth/rest/useractions/login",
      method: "POST",
    },
    logout: {
      url: "/tools.descartes.teastore.auth/rest/useractions/logout",
      method: "POST",
    },
  },
  image: {
    base: getBaseURL("image"),
    getWebImages: {
      url: "/tools.descartes.teastore.image/rest/image/getWebImages",
      method: "POST",
    },
    getProductImages: {
      url: "/tools.descartes.teastore.image/rest/image/getProductImages",
      method: "POST",
    },
  },
  recommender: {
    base: getBaseURL("recommender"),
    getRecommendations: {
      url: "/tools.descartes.teastore.recommender/rest/recommend",
      method: "POST",
    },
  },
  webui: {
    base: getBaseURL("webui"),
    userpasswd: "password",
    home: {
      url: "/tools.descartes.teastore.webui",
      method: "GET",
    },
    login: {
      url: "/tools.descartes.teastore.webui/login",
      method: "GET",
    },
    loginAction: {
      url: "/tools.descartes.teastore.webui/loginAction",
      method: "POST",
    },
    category: {
      url: "/tools.descartes.teastore.webui/category",
      method: "GET",
    },
    product: {
      url: "/tools.descartes.teastore.webui/product",
      method: "GET",
    },
    cart: {
      url: "/tools.descartes.teastore.webui/cart",
      method: "GET",
    },
    cartAction: {
      url: "/tools.descartes.teastore.webui/cartAction",
      method: "POST",
    },
    profile: {
      url: "/tools.descartes.teastore.webui/profile",
      method: "GET",
    },
    order: {
      url: "/tools.descartes.teastore.webui/order",
      method: "GET",
    }
  },
};

function getBaseURL(name) {
  switch (name) {
    case "persistence":
      if (__ENV.PERSISTENCE_BASE_URL) {
        return __ENV.PERSISTENCE_BASE_URL;
      } else {
        return "http://localhost:8084";
      }
    case "auth":
      if (__ENV.AUTH_BASE_URL) {
        return __ENV.AUTH_BASE_URL;
      } else {
        return "http://localhost:8083";
      }
    case "image":
      if (__ENV.IMAGE_BASE_URL) {
        return __ENV.IMAGE_BASE_URL;
      } else {
        return "http://localhost:8082";
      }
    case "recommender":
      if (__ENV.RECOMMENDER_BASE_URL) {
        return __ENV.RECOMMENDER_BASE_URL;
      } else {
        return "http://localhost:8081";
      }
    case "webui":
      if (__ENV.WEBUI_BASE_URL) {
        return __ENV.WEBUI_BASE_URL;
      } else {
        return "http://localhost:8080";
      }
    default:
      return "http://localhost:8080";
  }
}
