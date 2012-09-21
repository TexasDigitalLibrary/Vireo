package controllers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.search.Indexer;

import play.libs.Mail;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

/**
 * Test the system control panel.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class SystemTest extends AbstractVireoFunctionalTest {
	
	// Spring dependencies
	public static Indexer indexer = Spring.getBeanOfType(Indexer.class);
	
	/**
	 * Test viewing the control panel page
	 */
	@Test
	public void testControlPanel() {
		
		final String PANEL_URL = Router.reverse("System.controlPanel").url;
		
		LOGIN();
		
		Response response = GET(PANEL_URL);
		
		assertContentMatch("System Control Panel", response);
		assertContentMatch("Java Information", response);
		assertContentMatch("Memory Usage", response);
		assertContentMatch("Play Information", response);
		assertContentMatch("Mail Information", response);
		assertContentMatch("Index Information", response);

		// Check that the three controls are on the page.
		assertContentMatch("name=\"submit_testEmail\"", response);
		assertContentMatch("name=\"submit_rebuild\"", response);
		assertContentMatch("name=\"submit_deleteAndRebuild\"", response);
	}
	
	/**
	 * Test sending a... test email?
	 */
	@Test
	public void testEmail() throws InterruptedException {
		
		// Clear out the mail queues.
		Mail.Mock.reset();
		
		final String EMAIL_URL = Router.reverse("System.testEmail").url;
		
		LOGIN();
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("email","test@email.com");
		params.put("submit_testEmail","Send Test Email");
		Response response = POST(EMAIL_URL,params);
		
		assertContentMatch("The test email has been sent.",response);
		
		String recieved = null;
		for (int i = 0; i < 1000; i++) {
			Thread.yield();
			Thread.sleep(100);
			recieved = Mail.Mock.getLastMessageReceivedBy("test@email.com");
			if (recieved != null)
				break;
		}
		assertNotNull(recieved);
		assertTrue(recieved.contains("Subject: Vireo Email Test"));
	}
	
	/** 
	 * Test initiating an index rebuild from the control panel.
	 */
	@Test
	public void testIndex() throws InterruptedException {
		
		// Wait for any index job to finish.
		while (indexer.isJobRunning())
			Thread.yield();
		
		final String PANEL_URL = Router.reverse("System.controlPanel").url;
		final String EMAIL_URL = Router.reverse("System.rebuildIndex").url;
		
		LOGIN();
		
		// Step 1: Do a normal rebuild
		Map<String,String> params = new HashMap<String,String>();
		params.put("submit_rebuild","Rebuild Index");
		Response response = POST(EMAIL_URL,params);
		assertEquals(PANEL_URL, response.getHeader("location"));

		assertTrue(indexer.isJobRunning());
		assertEquals("Rebuild Index",indexer.getCurrentJobLabel());
		
		for (int i=0; i < 1000; i++) {
			Thread.sleep(100);
			if (!indexer.isJobRunning())
				break;
		}
		assertFalse(indexer.isJobRunning());
		
		
		// Step 2: Do a delete and rebuild
		params.clear();
		params.put("submit_deleteAndRebuild","Delete Index and Rebuild");
		response = POST(EMAIL_URL,params);
		assertEquals(PANEL_URL, response.getHeader("location"));

		assertTrue(indexer.isJobRunning());
		assertEquals("Rebuild Index",indexer.getCurrentJobLabel());
		
		for (int i=0; i < 1000; i++) {
			Thread.sleep(100);
			if (!indexer.isJobRunning())
				break;
		}
		assertFalse(indexer.isJobRunning());
	}
}
