package ch.carve.restilience;

import static org.junit.Assert.*;

import org.junit.Test;

public class RestilienceFactoryTest {

	@Test
	public void test1() {
		RestilienceFactory factory = new RestilienceFactory("jaxrs1-1.0-SNAPSHOT/simple/answer");
		factory.getWebTarget().request().get().getStatus();
	}
}
