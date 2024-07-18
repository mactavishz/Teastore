package tools.descartes.teastore.webui.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

    public List<Order> getOrders(int startIndex, int limit) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(persistenceRESTEndpoint).path("orders");
        if (startIndex >= 0) {
            target = target.queryParam("start", startIndex);
        }
        if (limit >= 0) {
            target = target.queryParam("max", limit);
        }
        GenericType<List<Order>> listType = new GenericType<List<Order>>() {
        };
        Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
        List<Order> entities = new ArrayList<>();

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
}
