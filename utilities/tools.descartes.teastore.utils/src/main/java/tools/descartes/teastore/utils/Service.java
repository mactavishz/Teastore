package tools.descartes.teastore.utils;

public enum Service {
	/**
	 * Persistence service.
	 */
	PERSISTENCE("tools.descartes.teastore.persistence"),
	/**
	 * Recommender service.
	 */
	RECOMMENDER("tools.descartes.teastore.recommender"),
	/**
	 * Store service.
	 */
	AUTH("tools.descartes.teastore.auth"),
	/**
	 * WebUi service.
	 */
	WEBUI("tools.descartes.teastore.webui"),
	/**
	 * Image Provider service.
	 */
	IMAGE("tools.descartes.teastore.image"),
	/**
	 * Registry Provider service.
	 */
	REGISTRY("tools.descartes.teastore.registry");

	private String serviceName;

	/**
	 * Service enums have service names. Names are also contexts.
	 * @param serviceName The name of the service.
	 */
	Service(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Gets the service name; name is also context.
	 * @return The service name.
	 */
	public String getServiceName() {
		return serviceName;
	}

	public static String getServerName(String HOST_ENV, String PORT_ENV) {
		String serviceHost = System.getenv(HOST_ENV);
		String portStr = System.getenv(PORT_ENV);
		if (portStr == null) {
			portStr = "8080";
		}

		if (serviceHost == null) {
			serviceHost = "localhost";
		}
		return serviceHost + ":"  + portStr;
	}

	public static String getRegistryURL() {
		String portStr = System.getenv("REGISTRY_PORT");
		String serviceHost = System.getenv("REGISTRY_HOST");

		if (portStr == null) {
			portStr = "8080";
		}

		if (serviceHost == null) {
			serviceHost = "localhost";
		}


		return String.format("%s://%s:%s/%s/rest/services", getServiceProtocol(), serviceHost, portStr, Service.REGISTRY.getServiceName());
	}

	public static String getServiceProtocol() {
		String useHTTPS = System.getenv("USE_HTTPS");
		boolean https = false;

		if (useHTTPS != null && useHTTPS.equals("true")) {
			https = true;
		}
		return (https ? "https" : "http");
	}

	public static String getSelfServiceRESTEndpoint(Service service) {
		return String.format("%s://%s/%s/rest/", getServiceProtocol(), getServerName("SERVICE_HOST", "SERVICE_PORT"),
				service.getServiceName());
	}

	public static String getServiceRESTEndpoint(Service service, String HOST_ENV, String PORT_ENV) {
		return String.format("%s://%s/%s/rest/", getServiceProtocol(), getServerName(HOST_ENV, PORT_ENV),
				service.getServiceName());
	}

	public static String getServiceBaseURL(String HOST_ENV, String PORT_ENV) {
		return String.format("%s://%s", getServiceProtocol(), getServerName(HOST_ENV, PORT_ENV));
	}
}