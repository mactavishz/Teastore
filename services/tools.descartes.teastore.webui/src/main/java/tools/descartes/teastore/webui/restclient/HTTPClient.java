package tools.descartes.teastore.webui.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.teastore.entities.*;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.utils.NotFoundException;
import tools.descartes.teastore.utils.RESTUtil;
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.utils.TimeoutException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class HTTPClient {
    public static final Logger LOG = LoggerFactory.getLogger(HTTPClient.class);
    public static final String persistenceRESTEndpoint = Service.getServiceRESTEndpoint(Service.PERSISTENCE, "PERSISTENCE_HOST", "PERSISTENCE_PORT");
    public static final String authRESTEndpoint = Service.getServiceRESTEndpoint(Service.AUTH, "AUTH_HOST", "AUTH_PORT");
    public static final String recommenderRESTEndpoint = Service.getServiceRESTEndpoint(Service.RECOMMENDER, "RECOMMENDER_HOST", "RECOMMENDER_PORT");
    private static Client client = ClientBuilder.newClient();

    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    public static List<Category> getCategories(int startIndex, int limit) {
        WebTarget target = client.target(persistenceRESTEndpoint).path("categories");
        List<Category> entities = new ArrayList<>();
        Response response = null;
        try {
            if (startIndex >= 0) {
                target = target.queryParam("start", startIndex);
            }
            if (limit >= 0) {
                target = target.queryParam("max", limit);
            }
            GenericType<List<Category>> listType = new GenericType<List<Category>>() {
            };
            response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();

            if (response != null && response.getStatus() == 200) {
                try {
                    entities = response.readEntity(listType);
                } catch (ProcessingException e) {
                    LOG.warn("Response did not conform to expected entity type. List expected.");
                }
            } else if (response != null) {
                response.bufferEntity();
            }
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return entities;
    }

    public static Category getCategory(long id) {
        Response response = null;
        Category result = null;
        try {
            response = client.target(persistenceRESTEndpoint)
                    .path("categories")
                    .path(String.valueOf(id))
                    .request()
                    .get();
            result = response.readEntity(Category.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static List<Product> getProductsByCategory(long categoryID, int startIndex, int limit) {
        WebTarget target = client.target(persistenceRESTEndpoint).path("products").path("category").path(String.valueOf(categoryID));
        List<Product> entities = new ArrayList<>();
        Response response = null;
        try {
            if (startIndex >= 0) {
                target = target.queryParam("start", startIndex);
            }
            if (limit >= 0) {
                target = target.queryParam("max", limit);
            }
            GenericType<List<Product>> listType = new GenericType<List<Product>>() {
            };
            response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();

            if (response != null && response.getStatus() == 200) {
                try {
                    entities = response.readEntity(listType);
                } catch (ProcessingException e) {
                    LOG.warn("Response did not conform to expected entity type. List expected.");
                }
            } else if (response != null) {
                response.bufferEntity();
            }
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return entities;
    }

    public static Product getProduct(Long id) {
        Response response = null;
        Product result = null;
        try {
            response = client.target(persistenceRESTEndpoint)
                    .path("products")
                    .path(String.valueOf(id))
                    .request()
                    .get();

            result = response.readEntity(Product.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static int getProductsCount(long categoryID) {
        Response response = null;
        String text = null;
        int count = -1;
        try {
            response = client.target(persistenceRESTEndpoint)
                    .path("products")
                    .path("count")
                    .path(String.valueOf(categoryID))
                    .request()
                    .get();

            text = response.readEntity(String.class);
            count = Integer.parseInt(text);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return count;
    }

    public static List<Long> getRecommendations(List<OrderItem> items, Long uid) {
        WebTarget target = client.target(recommenderRESTEndpoint).path("recommend").queryParam("uid", uid);
        Response response = null;
        List<Long> entities = new ArrayList<>();
        try {
            GenericType<List<Long>> listType = new GenericType<List<Long>>() {
            };
            response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(items, MediaType.APPLICATION_JSON));
            if (response != null) {
                if (response.getStatus() < 400) {
                    entities = response.readEntity(listType);
                } else {
                    response.bufferEntity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return entities;
    }

    public static List<Order> getUserOrders(long id, int startIndex, int limit) {
        WebTarget target = client.target(persistenceRESTEndpoint)
                .path("orders")
                .path("user")
                .path(String.valueOf(id));
        Response response = null;
        List<Order> entities = new ArrayList<>();
        if (startIndex >= 0) {
            target = target.queryParam("start", startIndex);
        }
        if (limit >= 0) {
            target = target.queryParam("max", limit);
        }

        try {
            GenericType<List<Order>> listType = new GenericType<List<Order>>() {
            };
            response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();

            if (response != null && response.getStatus() == 200) {
                try {
                    entities = response.readEntity(listType);
                } catch (ProcessingException e) {
                    LOG.warn("Response did not conform to expected entity type. List expected.");
                }
            } else if (response != null) {
                response.bufferEntity();
            }
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return entities;
    }

    public static boolean isLoggedIn(SessionBlob blob) {
        Response response = null;
        boolean result = false;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = client.target(authRESTEndpoint)
                    .path("useractions")
                    .path("isloggedin")
                    .request()
                    .post(Entity.entity(blob, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String responseBody = response.readEntity(String.class);
                if (responseBody == null || responseBody.isEmpty()) {
                    return false;
                }
                SessionBlob responseBlob = objectMapper.readValue(responseBody, SessionBlob.class);
                result = responseBlob != null;
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static SessionBlob login(SessionBlob blob, String name, String password) {
        Response response = null;
        SessionBlob result = null;
        try {
            response = client.target(authRESTEndpoint)
                    .path("useractions")
                    .path("login")
                    .queryParam("name", name)
                    .queryParam("password", password)
                    .request()
                    .post(Entity.entity(blob, MediaType.APPLICATION_JSON));
            result = RESTUtil.readThrowAndOrClose(response, SessionBlob.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static SessionBlob logout(SessionBlob blob) {
        Response response = null;
        SessionBlob result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = client.target(authRESTEndpoint)
                    .path("useractions")
                    .path("isloggedin")
                    .request()
                    .post(Entity.entity(blob, MediaType.APPLICATION_JSON));
             if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String responseBody = response.readEntity(String.class);
                if (responseBody == null || responseBody.isEmpty()) {
                    return null;
                }
                SessionBlob responseBlob = objectMapper.readValue(responseBody, SessionBlob.class);
                result = responseBlob;
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static User getUser(long id) {
        Response response = null;
        User result = null;
        try {
            response = client.target(persistenceRESTEndpoint)
                    .path("users")
                    .path(String.valueOf(id))
                    .request()
                    .get();
            result = response.readEntity(User.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static SessionBlob addProductToCart(SessionBlob blob, long productID) {
        Response response = null;
        SessionBlob result = null;
        try {
            response = client.target(authRESTEndpoint)
                    .path("cart")
                    .path("add")
                    .path(String.valueOf(productID))
                    .request()
                    .post(Entity.entity(blob, MediaType.APPLICATION_JSON));
            result = RESTUtil.readThrowAndOrClose(response, SessionBlob.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static SessionBlob removeProductFromCart(SessionBlob blob, long productID) {
        Response response = null;
        SessionBlob result = null;
        try {
            response = client.target(authRESTEndpoint)
                    .path("cart")
                    .path("remove")
                    .path(String.valueOf(productID))
                    .request()
                    .post(Entity.entity(blob, MediaType.APPLICATION_JSON));
            result = RESTUtil.readThrowAndOrClose(response, SessionBlob.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static SessionBlob updateQuantity(SessionBlob blob, long productID, int quantity) {
        Response response = null;
        SessionBlob result = null;
        try {
            response = client.target(authRESTEndpoint)
                    .path("cart")
                    .path(String.valueOf(productID))
                    .queryParam("quantity", quantity)
                    .request()
                    .put(Entity.entity(blob, MediaType.APPLICATION_JSON));
            result = RESTUtil.readThrowAndOrClose(response, SessionBlob.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static SessionBlob placeOrder(SessionBlob blob, String addressName, String address1,
                                  String address2, String creditCardCompany, String creditCardExpiryDate,
                                  long totalPriceInCents, String creditCardNumber
    ) {
        Response response = null;
        SessionBlob result = null;
        try {
            response = client.target(authRESTEndpoint)
                    .path("useractions")
                    .path("placeorder")
                    .queryParam("addressName", addressName).queryParam("address1", address1)
                    .queryParam("address2", address2).queryParam("creditCardCompany", creditCardCompany)
                    .queryParam("creditCardNumber", creditCardNumber)
                    .queryParam("creditCardExpiryDate", creditCardExpiryDate)
                    .queryParam("totalPriceInCents", totalPriceInCents).request()
                    .post(Entity.entity(blob, MediaType.APPLICATION_JSON));
            result = RESTUtil.readThrowAndOrClose(response, SessionBlob.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static boolean isServiceUp(String serviceBaseURL) {
        Response response = null;
        try {
            response = client.target(serviceBaseURL)
                    .path("health")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
                JsonObject jsonResponse = response.readEntity(JsonObject.class);
                String status = jsonResponse.getString("status", "");
                return "UP".equals(status);
            }
        } catch (ProcessingException | JsonException e) {
            // Log the exception
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return false;
    }
}
