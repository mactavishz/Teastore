package tools.descartes.teastore.model.restclient;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tools.descartes.teastore.utils.NotFoundException;
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.utils.TimeoutException;

public class PersistenceClient {
    // private final Logger LOG = LoggerFactory.getLogger(PersistenceClient.class);
    private final String persistenceRESTEndpoint = Service.getServiceRESTEndpoint(Service.PERSISTENCE, "PERSISTENCE_HOST", "PERSISTENCE_PORT");

    public void setMaintenanceMode(final Boolean maintenanceMode) {
        Client client = null;
        Response response = null;
        try {
            client = ClientBuilder.newClient();
            response = client.target(persistenceRESTEndpoint)
                    .path("generatedb")
                    .path("maintenance")
                    .request(MediaType.TEXT_PLAIN)
                    .post(Entity.entity(String.valueOf(maintenanceMode), MediaType.TEXT_PLAIN));
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new TimeoutException();
            }
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
    }

    public String resetRemoteEMF() {
        Client client = null;
        Response response = null;
        String message = null;
        try {
            client = ClientBuilder.newClient();
            response = client.target(persistenceRESTEndpoint)
                    .path("cache")
                    .path("emf")
                    .request(MediaType.TEXT_PLAIN)
                    .delete();
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new TimeoutException();
            } else if (response != null && response.getStatus() == 200) {
                message = response.readEntity(String.class);
		    }
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
        return message;
    }

    public String clearRemoteCacheREST(Class<?> entityClass) {
        Client client = null;
        Response response = null;
        String message = null;
        WebTarget target = null;
        try {
            client = ClientBuilder.newClient();
            target = client.target(persistenceRESTEndpoint)
                    .path("cache");
            if (entityClass != null) {
			    target = target.path("class").path(entityClass.getName());
		    } else {
			    target = target.path("cache");
		    }
            response = target.request(MediaType.TEXT_PLAIN).delete();
            if (response != null && response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new NotFoundException();
            } else if (response != null && response.getStatus() == Response.Status.REQUEST_TIMEOUT.getStatusCode()) {
                throw new TimeoutException();
            } else if (response != null && response.getStatus() == 200) {
                message = response.readEntity(String.class);
		    }
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
        return message;
    }
}
