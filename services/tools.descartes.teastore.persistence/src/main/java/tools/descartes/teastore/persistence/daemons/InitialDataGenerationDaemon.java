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
package tools.descartes.teastore.persistence.daemons;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.utils.RegistryClient;
import tools.descartes.teastore.utils.Service;

/**
 * Application Lifecycle Listener implementation class for data generation.
 *
 * @author Joakim von Kistowski
 *
 */
@WebListener
public class InitialDataGenerationDaemon implements ServletContextListener {
  private final String serverName = Service.getServerName("SERVICE_HOST", "SERVICE_PORT");
  private final RegistryClient client = new RegistryClient();
  private static final Logger LOG = LoggerFactory.getLogger(InitialDataGenerationDaemon.class);
  /**
   * Default constructor.
   */
  public InitialDataGenerationDaemon() {
  }

  /**
   * @see ServletContextListener#contextDestroyed(ServletContextEvent)
   * @param event
   *          The servlet context event at destruction.
   */
  public void contextDestroyed(ServletContextEvent event) {
    client.unregister(Service.PERSISTENCE.getServiceName(), serverName);
  }

  /**
   * @see ServletContextListener#contextInitialized(ServletContextEvent)
   * @param event
   *          The servlet context event at initialization.
   */
  public void contextInitialized(ServletContextEvent event) {
    LOG.info("Persistence started registration");
    client.register(Service.PERSISTENCE.getServiceName(), serverName);
    LOG.info("Persistence finshed registration");
  }
}
