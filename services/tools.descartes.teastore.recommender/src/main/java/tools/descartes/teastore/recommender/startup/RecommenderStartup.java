/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.recommender.startup;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.recommender.servlet.RetrainDaemon;
import tools.descartes.teastore.recommender.servlet.TrainingSynchronizer;
import tools.descartes.teastore.utils.EnvVarNotFoundException;
import tools.descartes.teastore.utils.RegistryClient;
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.recommender.restclient.HTTPClient;

/**
 * Startup Handler for the Recommender Service.
 *
 * @author Simon Eismann, Johannes Grohmann
 *
 */
@WebListener
public class RecommenderStartup implements ServletContextListener {
    private final Logger LOG = LoggerFactory.getLogger(RecommenderStartup.class);
    private final String serverName = Service.getServerName("SERVICE_HOST", "SERVICE_PORT");
    private final String persistenceBaseURL = Service.getServiceBaseURL("PERSISTENCE_HOST", "PERSISTENCE_PORT");
    private final RegistryClient client = new RegistryClient();
    private RetrainDaemon retrainDaemon = null;
    /**
     * Also set this accordingly in RegistryClientStartup.
     */

    /**
     * Empty constructor.
     */
    public RecommenderStartup() {

    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     * @param event
     *            The servlet context event at destruction.
     */
    public void contextDestroyed(ServletContextEvent event) {
        LOG.info(String.format("Recommender service on %s destroyed\n", serverName));
        client.destroy();
        HTTPClient.closeClient();
        if (retrainDaemon != null) {
            retrainDaemon.stop();
        }
    }

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     * @param event
     *            The servlet context event at initialization.
     */
    public void contextInitialized(ServletContextEvent event) {
        LOG.info(String.format("Recommender service initialized on %s\n", serverName));
        LOG.info(persistenceBaseURL);
        client.runAfterServiceIsAvailable(persistenceBaseURL, () -> {
            TrainingSynchronizer.getInstance().retrieveDataAndRetrain();
            try {
                String looptimeStr = System.getenv("RECOMMENDER_RETRAIN_LOOP_TIME");
                if (looptimeStr == null) {
                    throw new EnvVarNotFoundException("RECOMMENDER_RETRAIN_LOOP_TIME");
                }
                long looptime = Long.parseLong(looptimeStr);
                // if a looptime is specified, a retraining daemon is started
                if (looptime > 0) {
                    retrainDaemon = new RetrainDaemon(looptime);
                    retrainDaemon.start();
                    LOG.info("Periodic retraining every " + looptime + " milliseconds");
                } else {
                    LOG.info("Recommender loop time not set. Disabling periodic retraining.");
                }
            } catch (EnvVarNotFoundException | NumberFormatException e) {
                LOG.info("Recommender loop time not set. Disabling periodic retraining.");
            }
        });
    }
}
