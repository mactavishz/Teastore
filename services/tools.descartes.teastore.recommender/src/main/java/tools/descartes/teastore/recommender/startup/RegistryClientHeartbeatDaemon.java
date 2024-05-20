package tools.descartes.teastore.recommender.startup;

import tools.descartes.teastore.recommender.restclient.RegistryClient;
/**
 * Daemon which sends out heartbeats to the registry.
 * @author Simon
 */
public class RegistryClientHeartbeatDaemon implements Runnable {

	private String service;
	private String registryServer;

	/**
	 * Constructor.
	 * @param service Service enum
	 * @param server Service location
	 */
	public RegistryClientHeartbeatDaemon(String service, String server) {
		this.registryServer = server;
		this.service = service;
	}

	@Override
	public void run() {
		try {
			// RegistryClient.getClient().registerOnce(service, server);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

