package ch.carve.restilience;

import static org.junit.Assert.*;

import javax.ws.rs.ProcessingException;

import org.junit.Test;

public class RestilienceFactoryTest {

	@Test
	public void test1() {
		RestilienceFactory factory = new RestilienceFactory("hello", "hello/v1/hello");
		try {
			factory.getWebTarget().request().get().getStatus();
		} catch (ProcessingException e) {
			factory.onError();
		}
	}
}
