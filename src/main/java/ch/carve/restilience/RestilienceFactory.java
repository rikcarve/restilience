package ch.carve.restilience;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestilienceFactory implements ServerListener {
	private static Logger logger = LoggerFactory.getLogger(RestilienceFactory.class);
	
	private String basePath;
	private WebTarget webTarget;
	private String currentServer;
	private final Client client;
	private EtcdAdapter etcd;
	
	public RestilienceFactory(String serviceName, String basePath) {
		this.basePath = basePath;
		client = new ResteasyClientBuilder()
				.maxPooledPerRoute(5)
                .connectionPoolSize(10)
                .socketTimeout(10, TimeUnit.SECONDS)
                .build();
		etcd = new EtcdAdapter(serviceName, this);
	}
	
	public WebTarget getWebTarget() {
		// todo sync
		if (webTarget == null) {
			webTarget = client.target("http://" + getServer() + "/" + basePath);
		}
		return webTarget;
	}
	
	private String getServer() {
		currentServer = etcd.getServer();
		return currentServer;
	}

	public void onError() {
		logger.warn("Host/Port failed: {}/{}", webTarget.getUri().getHost(), webTarget.getUri().getPort());
		webTarget = null;
	}

	@Override
	public void serverNotification(String server) {
		logger.info("Server notification: {}", server);
		if (!server.equals(currentServer)) {
			webTarget = client.target("http://" + server + "/" + basePath);;
		}
	}
}
