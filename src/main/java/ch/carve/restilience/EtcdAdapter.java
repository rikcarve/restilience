package ch.carve.restilience;

import java.io.IOException;
import java.net.URI;
import java.util.List;
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
 * @author rik
 *
 */
public class EtcdAdapter implements IsSimplePromiseResponseHandler<EtcdKeysResponse> {
	private static Logger logger = LoggerFactory.getLogger(EtcdAdapter.class);
	private static EtcdClient etcdClient;
	
	private ServerListener serverListener;
	private String serviceName;
	
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
			e.printStackTrace();
		}		
	}
	
	public String getHostPort() {
		try {
			List<EtcdNode> nodes = etcdClient.getDir("services/" + serviceName).recursive().send().get().node.nodes;
			if (nodes.isEmpty()) {
				return null;
			}
			logger.info("Use {}", nodes.get(0).getValue());
			return nodes.get(0).getValue();
		} catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException e) {
			return null;
		}
	}

	@Override
	public void onResponse(ResponsePromise<EtcdKeysResponse> response) {
		try {
			logger.info(response.get().action.name());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serverListener.serverNotification(getHostPort());		
	}
}
