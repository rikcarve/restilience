package ch.carve.restilience;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mousio.client.promises.ResponsePromise;
import mousio.client.promises.ResponsePromise.IsSimplePromiseResponseHandler;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdKeysResponse.EtcdNode;

/**
 * Communication with ETCD, service name can be registered, sends notification with current best
 * host/port.
 */
public class EtcdAdapter implements IsSimplePromiseResponseHandler<EtcdKeysResponse> {
	private static Logger logger = LoggerFactory.getLogger(EtcdAdapter.class);
	private static EtcdClient etcdClient;
	
	private ServerListener serverListener;
	private String serviceName;
	private String currentServer;
	
	{
        String etcdAddress = System.getProperty("ETCD_HOST", "192.168.99.100:2379");
        etcdClient = new EtcdClient(URI.create("http://" + etcdAddress));
    }

	public EtcdAdapter(String serviceName, ServerListener serverListener) {
		this.serviceName = serviceName;
		this.serverListener = serverListener;
        try {
			etcdClient.getDir("services/" + serviceName).recursive().waitForChange().send().addListener(this);
		} catch (IOException e) {
			logger.warn("Problem with adding listener to etcd: {}", serviceName, e);
		}		
	}
	
	public String getServer() {
		try {
			List<EtcdNode> nodes = etcdClient.getDir("services/" + serviceName).recursive().send().get().node.nodes;
			if (nodes.isEmpty()) {
				return null;
			}
			Optional<EtcdNode> localServer = nodes.stream().filter(s -> isLocalServer(s.getValue())).findAny();
			if (localServer.isPresent()) {
				currentServer = localServer.get().getValue();
			} else {
			    currentServer = nodes.get(0).getValue();
			}
			logger.info("Using {}", currentServer);
			return currentServer;
		} catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException e) {
			logger.warn("Problem with etcd query {}", serviceName, e);
			return null;
		}
	}

	@Override
	public void onResponse(ResponsePromise<EtcdKeysResponse> response) {
		try {
			logger.info(response.get().action.name());
		} catch (Exception e) {
			logger.warn("Problem with etcd listener {}", serviceName, e);
		}
		String server = getServer();
		if (server != null) {
			serverListener.serverNotification(getServer());
		}
	}
	
	private boolean isLocalServer(String server) {
		return "localhost:8080".equals(server);
	}
}
