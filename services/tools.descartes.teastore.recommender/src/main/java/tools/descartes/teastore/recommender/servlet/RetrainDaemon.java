/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.recommender.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.teastore.utils.RegistryClient;

import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.TimeUnit;

/**
 * ManagedDaemon for periodic retraining if required.
 *
 * @author Johannes Grohmann
 * @author Chao Zhan
 */
public class RetrainDaemon {
    private static final Logger LOG = LoggerFactory.getLogger(RetrainDaemon.class);
    private final RegistryClient client = new RegistryClient();
    private final long looptime;
    private ManagedScheduledExecutorService executor;

    /**
     * Constructor.
     *
     * @param looptime The time between retraining in milliseconds
     */
    public RetrainDaemon(long looptime) {
        this.looptime = looptime;
        try {
            InitialContext ctx = new InitialContext();
            executor = (ManagedScheduledExecutorService) ctx.lookup("java:comp/DefaultManagedScheduledExecutorService");
        } catch (NamingException e) {
            LOG.error("Failed to lookup managed executor", e);
            throw new RuntimeException("Failed to initialize RetrainDaemon", e);
        }
    }

    /**
     * Starts the periodic retraining task.
     */
    public void start() {
        executor.scheduleAtFixedRate(this::runTask, 0, looptime, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (executor != null) {
			executor.shutdown();
		}
    }

    private void runTask() {
        try {
            // wait for the persistence service and then retrain
            TrainingSynchronizer.getInstance().retrieveDataAndRetrain();
        } catch (Exception e) {
            LOG.error("Error during retraining task", e);
        }
    }
}