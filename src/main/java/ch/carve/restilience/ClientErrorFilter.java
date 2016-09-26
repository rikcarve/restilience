package ch.carve.restilience;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientErrorFilter implements ClientResponseFilter {

	private static Logger logger = LoggerFactory.getLogger(ClientErrorFilter.class);
	
	private ErrorListener errorListener;
	
	public ClientErrorFilter(ErrorListener errorListener) {
		this.errorListener = errorListener;
	}
	
	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		String host = requestContext.getUri().getHost() + ":" + requestContext.getUri().getPort();
		int status = responseContext.getStatus();
		logger.debug("Response code {} from {}", status, host);
	}

}
