package ch.carve.restilience;

import static org.junit.Assert.*;

import javax.ws.rs.ProcessingException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class RestilienceFactoryTest {

	private static RestilienceFactory factory;
	
	@Rule
	public RepeatRule rule = new RepeatRule(10);
	
	@BeforeClass
	public static void initClass() {
		factory = new RestilienceFactory("hello", "hello/v1/hello");
	}
	
	@Test
	public void test1() {
		try {
			factory.getWebTarget().request().get().close();
		} catch (ProcessingException e) {
			factory.onError();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
