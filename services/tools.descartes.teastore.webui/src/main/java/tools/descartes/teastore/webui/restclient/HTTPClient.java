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
    private final Logger LOG = LoggerFactory.getLogger(HTTPClient.class);
    private final String persistenceRESTEndpoint = Service.getServiceRESTEndpoint(Service.PERSISTENCE, "PERSISTENCE_HOST", "PERSISTENCE_PORT");
    private final String authRESTEndpoint = Service.getServiceRESTEndpoint(Service.AUTH, "AUTH_HOST", "AUTH_PORT");
    private final String recommenderRESTEndpoint = Service.getServiceRESTEndpoint(Service.RECOMMENDER, "RECOMMENDER_HOST", "RECOMMENDER_PORT");

    public List<Category> getCategories(int startIndex, int limit) {
        Client client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return entities;
    }

    public Category getCategory(long id) {
        Client client = null;
        Response response = null;
        Category result = null;
        try {
            client = ClientBuilder.newClient();
            response = client.target(persistenceRESTEndpoint)
                    .path("categories")
                    .path(String.valueOf(id))
                    .request()
                    .get();
            result = response.readEntity(Category.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public List<Product> getProductsByCategory(long categoryID, int startIndex, int limit) {
        Client client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return entities;
    }

    public Product getProduct(Long id) {
        Client client = null;
        Response response = null;
        Product result = null;
        try {
            client = ClientBuilder.newClient();
            response = client.target(persistenceRESTEndpoint)
                    .path("products")
                    .path(String.valueOf(id))
                    .request()
                    .get();

            result = response.readEntity(Product.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public int getProductsCount(long categoryID) {
        Client client = null;
        Response response = null;
        String text = null;
        int count = -1;
        try {
            client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return count;
    }

    public List<Long> getRecommendations(List<OrderItem> items, Long uid) {
        Client client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return entities;
    }


    public List<OrderItem> getOrderItems(int startIndex, int limit) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(persistenceRESTEndpoint).path("orderitems");
        if (startIndex >= 0) {
            target = target.queryParam("start", startIndex);
        }
        if (limit >= 0) {
            target = target.queryParam("max", limit);
        }
        GenericType<List<OrderItem>> listType = new GenericType<List<OrderItem>>() {
        };
        Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
        List<OrderItem> entities = new ArrayList<>();

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
        client.close();
        if (response != null) {
            response.close();
        }
        return entities;
    }

    public List<Order> getUserOrders(long id, int startIndex, int limit) {
        Client client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return entities;
    }

    public boolean isLoggedIn(SessionBlob blob) {
        Client client = null;
        Response response = null;
        boolean result = false;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public SessionBlob login(SessionBlob blob, String name, String password) {
        Client client = null;
        Response response = null;
        SessionBlob result = null;
        try {
            client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public SessionBlob logout(SessionBlob blob) {
        Client client = null;
        Response response = null;
        SessionBlob result = null;
        try {
            client = ClientBuilder.newClient();
            response = client.target(authRESTEndpoint)
                    .path("useractions")
                    .path("isloggedin")
                    .request()
                    .post(Entity.entity(blob, MediaType.APPLICATION_JSON));
            result = RESTUtil.readThrowAndOrClose(response, SessionBlob.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public User getUser(long id) {
        Client client = null;
        Response response = null;
        User result = null;
        try {
            client = ClientBuilder.newClient();
            response = client.target(persistenceRESTEndpoint)
                    .path("users")
                    .path(String.valueOf(id))
                    .request()
                    .get();
            result = response.readEntity(User.class);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public SessionBlob addProductToCart(SessionBlob blob, long productID) {
        Client client = null;
        Response response = null;
        SessionBlob result = null;
        try {
            client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public SessionBlob removeProductFromCart(SessionBlob blob, long productID) {
        Client client = null;
        Response response = null;
        SessionBlob result = null;
        try {
            client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public SessionBlob updateQuantity(SessionBlob blob, long productID, int quantity) {
        Client client = null;
        Response response = null;
        SessionBlob result = null;
        try {
            client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public SessionBlob placeOrder(SessionBlob blob, String addressName, String address1,
                                  String address2, String creditCardCompany, String creditCardExpiryDate,
                                  long totalPriceInCents, String creditCardNumber
    ) {
        Client client = null;
        Response response = null;
        SessionBlob result = null;
        try {
            client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public boolean isServiceUp(String serviceBaseURL) {
        Client client = null;
        Response response = null;
        try {
            client = ClientBuilder.newClient();
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
            if (client != null) {
                client.close();
            }
        }
        return false;
    }
}
