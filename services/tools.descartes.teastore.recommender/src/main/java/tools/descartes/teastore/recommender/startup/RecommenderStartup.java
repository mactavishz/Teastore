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
package tools.descartes.teastore.recommender.startup;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.recommender.restclient.RegistryClient;
// import tools.descartes.teastore.recommender.servlet.TrainingSynchronizer;
// import tools.descartes.teastore.registryclient.RegistryClient;
// import tools.descartes.teastore.registryclient.Service;
// import tools.descartes.teastore.registryclient.loadbalancers.ServiceLoadBalancer;

/**
 * Startup Handler for the Recommender Service.
 *
 * @author Simon Eismann, Johannes Grohmann
 *
 */
@WebListener
public class RecommenderStartup implements ServletContextListener {

	private static final int REST_READ_TIMOUT = 1750;

	private static final Logger LOG = LoggerFactory.getLogger(RecommenderStartup.class);

	private static final String serverName = "recommender:8080";
	private static final String persistenceServiceName = "tools.descartes.teastore.persistence";
	private static final String recommenderServiceName = "tools.descartes.teastore.recommender";
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
		// RegistryClient.getClient().unregister(event.getServletContext().getContextPath());
		RegistryClient.unregister(recommenderServiceName, serverName);
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 * @param event
	 *            The servlet context event at initialization.
	 */
	public void contextInitialized(ServletContextEvent event) {
		// GlobalTracer.register(Tracing.init(Service.RECOMMENDER.getServiceName()));
		// RESTClient.setGlobalReadTimeout(REST_READ_TIMOUT);
		// ServiceLoadBalancer.preInitializeServiceLoadBalancers(Service.PERSISTENCE);
		// RegistryClient.getClient().runAfterServiceIsAvailable(Service.PERSISTENCE, () -> {
		// 	TrainingSynchronizer.getInstance().retrieveDataAndRetrain();
		// 	RegistryClient.getClient().register(event.getServletContext().getContextPath());
		// }, Service.RECOMMENDER);
		// try {
		// 	long looptime = (Long) new InitialContext().lookup("java:comp/env/recommenderLoopTime");
		// 	// if a looptime is specified, a retraining daemon is started
		// 	if (looptime > 0) {
		// 		// new RetrainDaemon(looptime).start();
		// 		LOG.info("Periodic retraining every " + looptime + " milliseconds");
		// 	} else {
		// 		LOG.info("Recommender loop time not set. Disabling periodic retraining.");
		// 	}
		// } catch (NamingException e) {
		// 	LOG.info("Recommender loop time not set. Disabling periodic retraining.");
		// }
		System.out.println("****************** Waiting for Services to be available ******************");
		RegistryClient.runAfterServiceIsAvailable(persistenceServiceName, () -> {
			System.out.println("****************** Persistence  is available ******************");
			// TrainingSynchronizer.getInstance().retrieveDataAndRetrain();
			RegistryClient.register(recommenderServiceName, serverName);
		}, recommenderServiceName);
	}

}
