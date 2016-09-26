package ch.carve.restilience;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class RestilienceFactory implements ErrorListener {
	private WebTarget webTarget;
	
	public RestilienceFactory(String basePath) {
		Client client = new ResteasyClientBuilder()
				.maxPooledPerRoute(5)
                .connectionPoolSize(10)
                .socketTimeout(10, TimeUnit.SECONDS)
                .register(new ClientErrorFilter(this))
                .build();
		webTarget = client.target("http://" + getHost() + "/" + basePath);
	}
	
	public WebTarget getWebTarget() {
		return webTarget;
	}
	
	private String getHost() {
		return "localhost:8080";
	}

	@Override
	public void onError(String host) {
		
	}
}
