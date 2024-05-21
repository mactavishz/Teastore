package tools.descartes.teastore.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Runnable to get callback once service is online.
 *
 * @author Simon
 */
public class StartupCallbackTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(StartupCallbackTask.class);

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

