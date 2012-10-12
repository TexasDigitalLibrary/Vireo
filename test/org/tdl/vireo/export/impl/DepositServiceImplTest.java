package org.tdl.vireo.export.impl;

import java.net.MalformedURLException;

import org.junit.Test;
import org.tdl.vireo.export.MockDepositor;
import org.tdl.vireo.export.MockPackager;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.model.MockDepositLocation;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.search.MockSearchFilter;
import org.tdl.vireo.search.MockSearcher;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.MockState;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the deposit service. We mock everything so no database interaction is
 * used for this. Nor are the packagers or depositors actually used.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DepositServiceImplTest extends UnitTest {

	// The deposit service to test
	public static DepositServiceImpl service = Spring.getBeanOfType(DepositServiceImpl.class);
	public static JobManager jobManager = Spring.getBeanOfType(JobManager.class);
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);

	/**
	 * Test depositing a single item.
	 */
	@Test
	public void testSingleDeposit() throws MalformedURLException {

		context.turnOffAuthorization();
		try {
			// Set up our mock objects.
			MockSubmission submission = new MockSubmission();
			MockDepositLocation location = new MockDepositLocation();
			location.repository = "http://localhost/repository";
			location.collection = "http://localhost/repository/collection";
			location.packager = new MockPackager();
			location.depositor = new MockDepositor();
			MockState successState = new MockState();
			
			// Do the deposit
			service.deposit(location, submission, successState, false);
			
			// Wait for deposit to finish.
			jobManager.waitForJobs();
	
			// Check the state.		
			String depositId = submission.getDepositId();
			assertNotNull(depositId);
			assertTrue(depositId.startsWith("http://repository.edu/deposit/"));
			assertEquals(successState, submission.getState());
			assertNotNull(submission.getLastLogEntry());
		} finally {
			context.restoreAuthorization();
		}

	}
	
}
