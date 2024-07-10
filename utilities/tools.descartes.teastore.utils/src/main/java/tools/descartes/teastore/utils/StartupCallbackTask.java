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

    private RegistryClient client = new RegistryClient();
    private String serviceBaseURL;
    private Runnable callback;

    /**
     * Constructor.
     *
     * @param serviceBaseURL service
     * @param callback         callback object
     */
    public StartupCallbackTask(String serviceBaseURL, Runnable callback) {
        this.serviceBaseURL = serviceBaseURL;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            boolean up = false;
            do {
                up = client.isServiceUp(serviceBaseURL);
                if (!up) {
                    try {
                        LOG.info("Service is not online yet. Waiting for service to come online.");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    LOG.info("Service is online.");
                }
            } while (!up);
            callback.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
    }

}

