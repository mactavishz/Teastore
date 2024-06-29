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

package tools.descartes.teastore.auth.startup;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.utils.RegistryClient;
import tools.descartes.teastore.utils.Service;


/**
 * Application Lifecycle Listener implementation class Registry Client Startup.
 *
 * @author Simon Eismann
 *
 */
@WebListener
public class AuthStartup implements ServletContextListener {
  private final Logger LOG = LoggerFactory.getLogger(AuthStartup.class);
  private final String serverName = Service.getServerName("SERVICE_HOST", "SERVICE_PORT");
  private final RegistryClient client = new RegistryClient();

  /**
   * Also set this accordingly in RegistryClientStartup.
   */

  /**
   * Empty constructor.
   */
  public AuthStartup() {

  }

  /**
   * shutdown routine.
   * @see ServletContextListener#contextDestroyed(ServletContextEvent)
   * @param event The servlet context event at destruction.
   */
  public void contextDestroyed(ServletContextEvent event) {
    client.unregister(Service.AUTH.getServiceName(), serverName);
  }

  /**
   * startup routine.
   * @see ServletContextListener#contextInitialized(ServletContextEvent)
   * @param event The servlet context event at initialization.
   */
  public void contextInitialized(ServletContextEvent event) {
    LOG.info("Waiting for dependent services to become available.");
    client.runAfterServiceIsAvailable(Service.PERSISTENCE.getServiceName(), () -> {
      LOG.info("Persistence service is available");
      client.register(Service.AUTH.getServiceName(), serverName);
    }, Service.AUTH.getServiceName());

  }

}
