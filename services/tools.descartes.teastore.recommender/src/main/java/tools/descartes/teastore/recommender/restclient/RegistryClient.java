package tools.descartes.teastore.recommender.restclient;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.teastore.recommender.startup.StartupCallbackTask;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RegistryClient {
    private static final Logger LOG = LoggerFactory.getLogger(tools.descartes.teastore.registryclient.RegistryClient.class);
    private static final int HEARTBEAT_INTERVAL_MS = 2500;
    private static final String registryURL = "http://registry:8080/tools.descartes.teastore.registry/rest/services/";

    private static ScheduledExecutorService heartbeatScheduler = Executors
            .newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService availabilityScheduler = Executors
            .newSingleThreadScheduledExecutor();

    public static void runAfterServiceIsAvailable(String requestedService, Runnable callback,
                                                  String myService) {
        availabilityScheduler.schedule(new StartupCallbackTask(requestedService, callback, myService),
                0, TimeUnit.NANOSECONDS);
        availabilityScheduler.shutdown();
    }

    public static List<String> getServersForService(String targetService) {
        List<String> list = null;
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(registryURL)
                    .path("/" + targetService + "/").request(MediaType.APPLICATION_JSON)
                    .get();
            list = response.readEntity(new GenericType<List<String>>() {
            });
        } catch (ProcessingException e) {
            return null;
        }

        return list;
    }

    public static boolean unregisterOnce(String serviceName, String serverName) {
        try {
            Client client = ClientBuilder.newClient();
            Response res = client.target(registryURL)
                    .path(serviceName)
                    .path(serverName).request(MediaType.APPLICATION_JSON)
                    .delete();
            return (res.getStatus() == Response.Status.OK.getStatusCode());
        } catch (ProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean registerOnce(String serviceName, String serverName) {
        try {
            Client client = ClientBuilder.newClient();
            Response res = client.target(registryURL)
                    .path(serviceName)
                    .path(serverName).request(MediaType.APPLICATION_JSON)
                    .put(Entity.text(""));
            return (res.getStatus() == Response.Status.OK.getStatusCode());
        } catch (ProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void register(String serviceName, String serverName) {
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

    public static void unregister(String serviceName, String serverName) {
        heartbeatScheduler.shutdown();
        availabilityScheduler.shutdown();
        try {
            heartbeatScheduler.awaitTermination(20, TimeUnit.SECONDS);
            availabilityScheduler.awaitTermination(20, TimeUnit.SECONDS);
            unregisterOnce(serviceName, serverName);
        } catch (ProcessingException e) {
            LOG.warn("Could not unregister " + serviceName + " when it was shutting "
                    + "down, since it could not reach the registry. This can be caused by shutting "
                    + "down the registry before other services, but is in it self not a problem.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
