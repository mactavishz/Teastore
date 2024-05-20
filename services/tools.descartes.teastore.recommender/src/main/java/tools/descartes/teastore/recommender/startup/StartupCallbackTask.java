package tools.descartes.teastore.recommender.startup;

// import com.netflix.loadbalancer.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import tools.descartes.teastore.registryclient.RegistryClient;
// import tools.descartes.teastore.registryclient.Service;
// import tools.descartes.teastore.registryclient.StartupCallback;
import tools.descartes.teastore.recommender.restclient.RegistryClient;

import java.util.List;

/**
 * Runnable to get callback once service is online.
 *
 * @author Simon
 */
public class StartupCallbackTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);

    private String requestedService;
    private Runnable callback;
    private String myService;

    /**
     * Constructor.
     *
     * @param requestedService service
     * @param callback         callback object
     * @param myService        service
     */
    public StartupCallbackTask(String requestedService, Runnable callback, String myService) {
        this.requestedService = requestedService;
        this.callback = callback;
        this.myService = myService;
    }

    @Override
    public void run() {
        try {
            List<String> servers;
            boolean msgLogged = false;
            do {
                servers = RegistryClient.getServersForService(requestedService);
                if (servers == null || servers.isEmpty()) {
                    try {
                        if (servers == null) {
                            LOG.info("Registry not online. " + myService + " is waiting for it to come online");
                        } else {
                            LOG.info(requestedService + " not online. "
                                    + myService + " is waiting for it to come online");
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Servers for " + requestedService + " are online: " + servers);
                }
            } while (servers == null || servers.isEmpty());

            callback.run();

        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
    }

}

