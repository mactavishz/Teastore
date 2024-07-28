package tools.descartes.teastore.auth.restclient;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;
import tools.descartes.teastore.utils.NotFoundException;
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.utils.TimeoutException;

public class HTTPClient {
    private final static Logger LOG = LoggerFactory.getLogger(HTTPClient.class);
    private final static String persistenceRESTEndpoint = Service.getServiceRESTEndpoint(Service.PERSISTENCE, "PERSISTENCE_HOST", "PERSISTENCE_PORT");
    private final static Client client = ClientBuilder.newClient();

    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    public static Product getProduct(Long id){
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

    public static long createOrder(Order order){
        Response response = null;
        long id = -1L;
        try {
            response = client.target(persistenceRESTEndpoint)
                    .path("orders")
                    .request()
                    .post(Entity.entity(order, MediaType.APPLICATION_JSON), Response.class);
            id = -1L;
            // If resource was created successfully
            if (response != null && response.getStatus() == 201) {
                id = 0L;
                // check if response an Id; if yes: return the id
                try {
                    id = response.readEntity(Long.class);
                } catch (ProcessingException e) {
                    LOG.warn("Response did not conform to expected message type. Expected a Long ID.");
                }
            } else if (response != null) {
                response.bufferEntity();
            }
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new tools.descartes.teastore.utils.NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new tools.descartes.teastore.utils.TimeoutException();
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
            id = -1;
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return id;
    }

    public static long createOrderItem(OrderItem orderItem){
        Response response = null;
        long id = -1L;
        try {
            response = client.target(persistenceRESTEndpoint)
                    .path("orderitems")
                    .request()
                    .post(Entity.entity(orderItem, MediaType.APPLICATION_JSON), Response.class);
            // If resource was created successfully
            if (response != null && response.getStatus() == 201) {
                id = 0L;
                // check if response an Id; if yes: return the id
                try {
                    id = response.readEntity(Long.class);
                } catch (ProcessingException e) {
                    LOG.warn("Response did not conform to expected message type. Expected a Long ID.");
                }
            } else if (response != null) {
                response.bufferEntity();
            }
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new tools.descartes.teastore.utils.NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new tools.descartes.teastore.utils.TimeoutException();
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
            id = -1;
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return id;
    }

    public static User getUser(String propertyName, String propertyValue){
        User user = null;
        Response response = null;
        try {
            response = client.target(persistenceRESTEndpoint)
                    .path("users")
                    .path(propertyName)
                    .path(propertyValue)
                    .request()
                    .get();
            if (response != null && response.getStatus() < 400) {
                try {
                    user = response.readEntity(User.class);
                } catch (NullPointerException | ProcessingException e) {
                    // This happens if no entity was found
                }
            } else if (response != null) {
                response.bufferEntity();
            }
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new TimeoutException();
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return user;
    }
}
