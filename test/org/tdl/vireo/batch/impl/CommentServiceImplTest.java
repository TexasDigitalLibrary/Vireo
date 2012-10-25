package org.tdl.vireo.batch.impl;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.MockSearchFilter;
import org.tdl.vireo.search.MockSearcher;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.libs.Mail;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the comment/email service. We mock everything so no database interaction is
 * used for this.
 * 
 * @author Micah Cooper
 */
public class CommentServiceImplTest extends UnitTest {

	// The transition service to test
	public static CommentServiceImpl service = Spring.getBeanOfType(CommentServiceImpl.class);
	public static JobManager jobManager = Spring.getBeanOfType(JobManager.class);
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static EmailService emailService = Spring.getBeanOfType(EmailService.class);

	/** 
	 * Make sure no background jobs are running 
	 */
	@Before
	public void setup() {
		jobManager.waitForJobs();
	}
	
	
	/**
	 * Test adding a public comment with no email.
	 */
	@Test
	public void testBatchCommentUpdate() throws MalformedURLException {

		context.turnOffAuthorization();
		Searcher originalSearcher = service.searcher;
		SubmissionRepository originalSubRepo = service.subRepo;
		try {

			// Set up our mock objects.
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			for (int i=0; i<10; i++)
				searcher.submissions.add(new MockSubmission());


			// Add the comment
			service.searcher = searcher;
			service.subRepo = searcher.subRepo;
			service.comment(filter, "This is the comment", null, true, false, false);

			// Wait for job to finish.
			jobManager.waitForJobs();
			

			// Check the comment.
			for (MockSubmission submission : searcher.submissions) {
				assertEquals("This is the comment", submission.getLastLogEntry());
				assertEquals(1, submission.logs.size());
				assertFalse(submission.logs.get(0).isPrivate());
			}



		} finally {
			service.searcher = originalSearcher;
			service.subRepo = originalSubRepo;
			context.restoreAuthorization();
		}

	}
	
	/**
	 * Test adding a private comment with no email.
	 */
	@Test
	public void testBatchPrivateCommentUpdate() throws MalformedURLException {

		context.turnOffAuthorization();
		Searcher originalSearcher = service.searcher;
		SubmissionRepository originalSubRepo = service.subRepo;
		try {

			// Set up our mock objects.
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			for (int i=0; i<10; i++)
				searcher.submissions.add(new MockSubmission());



			// Add the comment
			service.searcher = searcher;
			service.subRepo = searcher.subRepo;
			service.comment(filter, "This is the comment", null, false, false, false);

			// Wait for job to finish.
			jobManager.waitForJobs();
			

			// Check the comment.
			for (MockSubmission submission : searcher.submissions) {
				assertEquals("This is the comment", submission.getLastLogEntry());
				assertEquals(1, submission.logs.size());
				assertTrue(submission.logs.get(0).isPrivate());
			}			



		} finally {
			service.searcher = originalSearcher;
			service.subRepo = originalSubRepo;
			context.restoreAuthorization();
		}

	}
	
	/**
	 * Test adding a comment with an email.
	 */
	@Test
	public void testBatchEmailCommentUpdate() throws MalformedURLException {

		context.turnOffAuthorization();
		Searcher originalSearcher = service.searcher;
		SubmissionRepository originalSubRepo = service.subRepo;
		try {

			// Set up our mock objects.
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			MockPerson person = new MockPerson();
			person.setEmail("email@email.com");
			person.save();
						
			for (int i=0; i<10; i++) {
				MockSubmission sub = new MockSubmission();
				searcher.submissions.add(sub);
				sub.submitter = person;				
			}
			
			// Add the comment
			service.searcher = searcher;
			service.subRepo = searcher.subRepo;
			service.comment(filter, "This is the comment", "Subject", true, true, false);

			// Wait for job to finish.
			jobManager.waitForJobs();
			

			// Check the comment.
			for (MockSubmission submission : searcher.submissions) {
				String recieved = Mail.Mock.getLastMessageReceivedBy("email@email.com");
				
				assertNotNull(recieved);
				assertTrue(recieved.contains("Subject: Subject"));
				assertTrue(recieved.contains("This is the comment"));
			}			



		} finally {
			service.searcher = originalSearcher;
			service.subRepo = originalSubRepo;
			context.restoreAuthorization();
		}

	}
	
}
