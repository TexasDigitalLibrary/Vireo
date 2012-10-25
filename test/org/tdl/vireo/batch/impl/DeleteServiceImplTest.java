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
 * Test the delete service. We mock everything so no database interaction is
 * used for this.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DeleteServiceImplTest extends UnitTest {

	// The transition service to test
	public static DeleteServiceImpl service = Spring.getBeanOfType(DeleteServiceImpl.class);
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
	 * Test batch deleting
	 */
	@Test
	public void testBatchDelete() throws MalformedURLException {

		context.turnOffAuthorization();
		Searcher originalSearcher = service.searcher;
		SubmissionRepository originalSubRepo = service.subRepo;

		try {

			// Set up our mock objects.
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			for (int i=0; i<10; i++)
				searcher.submissions.add(new DeletableMockSubmission());



			// Do the deposit
			service.searcher = searcher;
			service.subRepo = searcher.subRepo;
			service.delete(filter);

			// Wait for deposit to finish.
			jobManager.waitForJobs();
			

			// Check the state.
			for (MockSubmission submission : searcher.submissions) {
				
				assertTrue( ((DeletableMockSubmission) submission).hasBeenDeleted);
			}



		} finally {
			service.searcher = originalSearcher;
			service.subRepo = originalSubRepo;
			context.restoreAuthorization();
		}

	}
	
	/**
	 * Mock submission which keeps track if it has been deleted.
	 */
	public class DeletableMockSubmission extends MockSubmission {
		
		public boolean hasBeenDeleted = false;
		
		@Override
		public MockSubmission delete() {
			hasBeenDeleted = true;
			return this;
		}
		
	}
}
