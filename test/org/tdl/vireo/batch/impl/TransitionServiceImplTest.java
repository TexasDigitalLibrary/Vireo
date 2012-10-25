package org.tdl.vireo.batch.impl;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.export.MockDepositor;
import org.tdl.vireo.export.MockPackager;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.model.MockDepositLocation;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.MockSearchFilter;
import org.tdl.vireo.search.MockSearcher;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.MockState;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the transition service. We mock everything so no database interaction is
 * used for this. Nor are the packagers or depositors actually used.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class TransitionServiceImplTest extends UnitTest {

	// The transition service to test
	public static TransitionServiceImpl service = Spring.getBeanOfType(TransitionServiceImpl.class);
	public static JobManager jobManager = Spring.getBeanOfType(JobManager.class);
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);

	/** 
	 * Make sure no background jobs are running 
	 */
	@Before
	public void setup() {
		jobManager.waitForJobs();
	}
	
	
	/**
	 * Test a regular status update.
	 */
	@Test
	public void testBatchStatusUpdate() throws MalformedURLException {

		context.turnOffAuthorization();
		Searcher originalSearcher = service.searcher;
		SubmissionRepository originalSubRepo = service.subRepo;

		try {

			// Set up our mock objects.
			MockState state = new MockState();
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			for (int i=0; i<10; i++)
				searcher.submissions.add(new MockSubmission());



			// Do the deposit
			service.searcher = searcher;
			service.subRepo = searcher.subRepo;
			service.transition(filter,  state,  null);

			// Wait for deposit to finish.
			jobManager.waitForJobs();
			

			// Check the state.
			for (MockSubmission submission : searcher.submissions) {
				assertEquals(state, submission.getState());
			}



		} finally {
			service.searcher = originalSearcher;
			service.subRepo = originalSubRepo;
			context.restoreAuthorization();
		}

	}
	
	/**
	 * Test depositing a batch of items.
	 */
	@Test
	public void testBatchDeposit() throws MalformedURLException {

		context.turnOffAuthorization();
		Searcher originalSearcher = service.searcher;
		SubmissionRepository originalSubRepo = service.subRepo;
		try {

			// Set up our mock objects.
			MockDepositLocation location = new MockDepositLocation();
			location.repository = "http://localhost/repository";
			location.collection = "http://localhost/repository/collection";
			location.packager = new MockPackager();
			location.depositor = new MockDepositor();
			MockState state = new MockState();
			state.isDepositable = true;
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			for (int i=0; i<10; i++)
				searcher.submissions.add(new MockSubmission());

			// Do the deposit
			service.searcher = searcher;
			service.subRepo = searcher.subRepo;
			service.transition(filter,  state,  location);

			// Wait for deposit to finish.
			jobManager.waitForJobs();
			
			// Check the state.
			for (MockSubmission submission : searcher.submissions) {
				String depositId = submission.getDepositId();
				assertNotNull(depositId);
				assertTrue(depositId.startsWith("http://repository.edu/deposit/"));
				assertEquals(state, submission.getState());
				assertNotNull(submission.getLastLogEntry());
			}

		} finally {
			service.searcher = originalSearcher;
			service.subRepo = originalSubRepo;
			context.restoreAuthorization();
		}

	}
}
