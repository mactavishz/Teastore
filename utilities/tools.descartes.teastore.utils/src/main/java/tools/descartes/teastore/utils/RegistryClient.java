package tools.descartes.teastore.utils;

import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;

public class RegistryClient {
    private final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);
    private ManagedScheduledExecutorService availabilityScheduler;

    public RegistryClient() {
        try {
            InitialContext ctx = new InitialContext();
            availabilityScheduler  = (ManagedScheduledExecutorService) ctx.lookup("java:comp/DefaultManagedScheduledExecutorService");
        } catch (NamingException e) {
            LOG.error("Failed to lookup managed executors", e);
            // Consider throwing a runtime exception here if these are critical
        }
    }

    public void destroy() {
        if (availabilityScheduler != null) {
            availabilityScheduler.shutdown();
        }
    }

    public void runAfterServiceIsAvailable(String serviceBaseURL, Runnable callback) {
        if (availabilityScheduler != null) {
            availabilityScheduler.schedule(new StartupCallbackTask(serviceBaseURL, callback),
                    0, TimeUnit.NANOSECONDS);
        } else {
            LOG.error("AvailabilityScheduler is null. Cannot schedule task.");
            // Consider running the task immediately or throwing an exception
        }
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
