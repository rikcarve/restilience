package ch.carve.restilience;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse.EtcdNode;
import mousio.etcd4j.transport.EtcdNettyClient;

public class EtcdAdapter {
	private static Logger logger = LoggerFactory.getLogger(EtcdAdapter.class);
	private static EtcdClient etcdClient;
	
	{
        String etcdAddress = System.getProperty("ETCD_HOST", "192.168.99.100:2379");
        etcdClient = new EtcdClient(URI.create("http://" + etcdAddress));
    }

	public String getHostPort(String serviceName) {
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
}
