package tools.descartes.teastore.recommender.restclient;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.utils.NotFoundException;
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.utils.TimeoutException;

import java.util.ArrayList;
import java.util.List;

public class HTTPClient {
    private final static Logger LOG = LoggerFactory.getLogger(HTTPClient.class);
    private final static String persistenceRESTEndpoint = Service.getServiceRESTEndpoint(Service.PERSISTENCE, "PERSISTENCE_HOST", "PERSISTENCE_PORT");
    private final static String recommenderRESTEndpoint = Service.getSelfServiceRESTEndpoint(Service.RECOMMENDER);
    private final static Client client = ClientBuilder.newClient();

    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    public static boolean isPersistenceAvailable() {
        Response response = null;
        boolean result = false;
        try {
            response = client.target(persistenceRESTEndpoint)
                    .path("generatedb")
                    .path("finished")
                    .request()
                    .get();

            result = Boolean.parseBoolean(response.readEntity(String.class));
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    public static List<OrderItem> getOrderItems(int startIndex, int limit) {
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
        if (response != null) {
            response.close();
        }
        return entities;
    }

    public static List<Order> getOrders(int startIndex, int limit) {
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
        if (response != null) {
            response.close();
        }
        return entities;
    }

    public static List<Long> getTrainTimestamps() {
        WebTarget target = client.target(recommenderRESTEndpoint).path("train/timestamp");
        Response response = target.request(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get();
        List<Long> list = new ArrayList<>();
        Long timestamp = null;
        if (response != null && response.getStatus() == 200) {
            try {
                timestamp = response.readEntity(Long.class);
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
        if (response != null) {
            response.close();
        }
        list.add(timestamp);
        return list;
    }
}
