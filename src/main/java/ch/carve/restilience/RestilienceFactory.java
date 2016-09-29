package ch.carve.restilience;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestilienceFactory {
	private static Logger logger = LoggerFactory.getLogger(RestilienceFactory.class);
	
	private WebTarget webTarget;
	private String serviceName;
	
	private EtcdAdapter etcd = new EtcdAdapter();
	
	public RestilienceFactory(String serviceName, String basePath) {
		this.serviceName = serviceName;
		Client client = new ResteasyClientBuilder()
				.maxPooledPerRoute(5)
                .connectionPoolSize(10)
                .socketTimeout(10, TimeUnit.SECONDS)
                .build();
		webTarget = client.target("http://" + getHost() + "/" + basePath);
	}
	
	public WebTarget getWebTarget() {
		return webTarget;
	}
	
	private String getHost() {
		return etcd.getHostPort(serviceName);
	}

	public void onError() {
		logger.warn("Host/Port failed: {}/{}", webTarget.getUri().getHost(), webTarget.getUri().getPort());
	}
}
