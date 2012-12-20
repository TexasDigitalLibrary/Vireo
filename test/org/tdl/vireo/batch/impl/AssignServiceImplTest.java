package org.tdl.vireo.batch.impl;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.model.MockPerson;
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
 * Test the assign service. We mock everything so no database interaction is
 * used for this.
 * 
 * @author Micah Cooper
 */
public class AssignServiceImplTest extends UnitTest {

	// The transition service to test
	public static AssignServiceImpl service = Spring.getBeanOfType(AssignServiceImpl.class);
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
	 * Test a regular assignee update.
	 */
	@Test
	public synchronized void testBatchAssignUpdate() throws MalformedURLException {

		context.turnOffAuthorization();
		Searcher originalSearcher = service.searcher;
		SubmissionRepository originalSubRepo = service.subRepo;
		try {

			// Set up our mock objects.
			MockPerson person = new MockPerson();
			person.id = 1L;
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			for (int i=0; i<10; i++)
				searcher.submissions.add(new MockSubmission());
			assertNotNull(person.getId());


			// Change the assignee
			service.searcher = searcher;
			service.subRepo = searcher.subRepo;
			service.assign(filter,  person.getId());

			// Wait for jobs to finish.
			jobManager.waitForJobs();
			

			// Check the assignee.
			for (MockSubmission submission : searcher.submissions) {
				assertNotNull(submission.getAssignee());
				assertEquals(person.getId(), submission.getAssignee().getId());
			}



		} finally {
			service.searcher = originalSearcher;
			service.subRepo = originalSubRepo;
			context.restoreAuthorization();
		}

	}
}
