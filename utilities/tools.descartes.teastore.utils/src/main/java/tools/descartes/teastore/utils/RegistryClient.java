package tools.descartes.teastore.utils;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;

public class RegistryClient {
    private final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);
    private final int HEARTBEAT_INTERVAL_MS = 2500;
    private final String registryURL = Service.getRegistryURL();


    private ManagedScheduledExecutorService heartbeatScheduler;

    private ManagedScheduledExecutorService availabilityScheduler;

    public RegistryClient() {
        try {
            InitialContext ctx = new InitialContext();
            heartbeatScheduler = (ManagedScheduledExecutorService) ctx.lookup("java:comp/DefaultManagedScheduledExecutorService");
            availabilityScheduler = heartbeatScheduler;
        } catch (NamingException e) {
            LOG.error("Failed to lookup managed executors", e);
            // Consider throwing a runtime exception here if these are critical
        }
    }

    public void runAfterServiceIsAvailable(String requestedService, Runnable callback,
                                           String myService) {
        if (availabilityScheduler != null) {
            availabilityScheduler.schedule(new StartupCallbackTask(requestedService, callback, myService),
                    0, TimeUnit.NANOSECONDS);
        } else {
            LOG.error("AvailabilityScheduler is null. Cannot schedule task.");
            // Consider running the task immediately or throwing an exception
        }
    }

    public List<String> getServersForService(String targetService) {
        List<String> list = null;
        Client client = null;
        Response response = null;
        try {
            client = ClientBuilder.newClient();
            response = client.target(registryURL)
                    .path(targetService).request(MediaType.APPLICATION_JSON)
                    .get();
            if (response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
                list = response.readEntity(new GenericType<List<String>>() {
                });
            }
        } catch (ProcessingException e) {
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.close();
            }
        }
        return list;
    }

    public boolean unregisterOnce(String serviceName, String serverName) {
        Client client = null;
        Response response = null;
        boolean result = false;
        try {
            client = ClientBuilder.newClient();
            response = client.target(registryURL)
                    .path(serviceName)
                    .path(serverName).request(MediaType.APPLICATION_JSON)
                    .delete();
            if (response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
                result = response.getStatus() == Response.Status.OK.getStatusCode();
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public boolean registerOnce(String serviceName, String serverName) {
        Client client = null;
        Response response = null;
        boolean result = false;
        try {
            client = ClientBuilder.newClient();
            response = client.target(registryURL)
                    .path(serviceName)
                    .path(serverName).request(MediaType.APPLICATION_JSON)
                    .put(Entity.text(""));
            if (response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
                result = response.getStatus() == Response.Status.OK.getStatusCode();
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.close();
            }
        }
        return result;
    }

    public void register(String serviceName, String serverName) {
        heartbeatScheduler.scheduleAtFixedRate(() -> {
                    boolean ok = registerOnce(serviceName, serverName);
                    if (!ok) {
                        LOG.warn("Could not heartbeat " + serviceName + " at the registry.");
                    } else {
                        LOG.info("Heartbeat " + serviceName + " at the registry successfully.");
                    }
                }, 0,
                HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public void unregister(String serviceName, String serverName) {
        if (heartbeatScheduler != null) {
            heartbeatScheduler.shutdownNow();
        }
        if (availabilityScheduler != null) {
            availabilityScheduler.shutdownNow();
        }
        try {
            if (heartbeatScheduler != null) {
                heartbeatScheduler.awaitTermination(20, TimeUnit.SECONDS);
            }
            if (availabilityScheduler != null) {
                availabilityScheduler.awaitTermination(20, TimeUnit.SECONDS);
            }
            unregisterOnce(serviceName, serverName);
        } catch (ProcessingException e) {
            LOG.warn("Could not unregister " + serviceName + " when it was shutting "
                    + "down, since it could not reach the registry. This can be caused by shutting "
                    + "down the registry before other services, but is in itself not a problem.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
