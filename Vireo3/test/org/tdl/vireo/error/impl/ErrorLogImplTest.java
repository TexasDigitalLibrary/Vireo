package org.tdl.vireo.error.impl;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.error.ErrorReport;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.job.JobMetadata.Progress;

import play.jobs.Job;
import play.modules.spring.Spring;
import play.mvc.Http.Request;
import play.test.UnitTest;

/**
 * Simple tests of the error log.
 *
 */
public class ErrorLogImplTest extends UnitTest {

	public static ErrorLog errorLog = Spring.getBeanOfType(ErrorLog.class);
	
	/**
	 * Test loging each of the types of errors and make sure the right stuff is reported.
	 */
	@Test
	public void testLogError() {
		
		// Clear out the recent reports
		errorLog.clearRecentErrorReports();
		assertEquals(0, errorLog.getRecentErrorReports().size());
		
		// Create an exception of each time
		Exception justException = new RuntimeException("Just Exception (Test)");
		Exception httpException = new IllegalArgumentException("HTTP Exception (Test)");
		Exception jobException = new IllegalStateException("Job Exception (Test)");
		Exception unspecifiedException = new SecurityException("Unspecified Exception (Test)");
		
		Request request = new Request();
		request.action = "action.method";
		request.url = "/somewhere";
		
		JobMetadata job = new MockJobMetadat();
		
		
		errorLog.logError(justException);
		errorLog.logError(httpException, request);
		errorLog.logError(jobException, job);
		errorLog.logError(unspecifiedException, "Unspecified error");
		
		
		List<ErrorReport> reports = errorLog.getRecentErrorReports();
		assertEquals(4,reports.size());
		
		assertEquals("Unspecified error",reports.get(0).getWhere());
		assertEquals("java.lang.SecurityException",reports.get(0).getWhat());
		assertTrue(reports.get(0).getStackTrace().contains("Unspecified Exception (Test)"));
		
		assertEquals("Background job: Mock Job",reports.get(1).getWhere());
		assertEquals("java.lang.IllegalStateException",reports.get(1).getWhat());
		assertTrue(reports.get(1).getStackTrace().contains("Job Exception (Test)"));
		
		assertEquals("HTTP request action.method(): /somewhere",reports.get(2).getWhere());
		assertEquals("java.lang.IllegalArgumentException",reports.get(2).getWhat());
		assertTrue(reports.get(2).getStackTrace().contains("HTTP Exception (Test)"));
		
		assertEquals(null,reports.get(3).getWhere());
		assertEquals("java.lang.RuntimeException",reports.get(3).getWhat());
		assertTrue(reports.get(3).getStackTrace().contains("Just Exception (Test)"));

	}

	/**
	 * Mock job metadata, just enough to full the reporting mechanism.
	 */
	public static class MockJobMetadat implements JobMetadata {

		@Override
		public UUID getId() {
			return null;
		}

		@Override
		public long getStartTime() {
			return 0;
		}

		@Override
		public Long getOwnerId() {
			return null;
		}

		@Override
		public String getName() {
			return "Mock Job";
		}

		@Override
		public JobStatus getStatus() {
			return null;
		}

		@Override
		public void setStatus(JobStatus status) {
		}

		@Override
		public Progress getProgress() {
			return null;
		}

		@Override
		public Job getJob() {
			return null;
		}

		@Override
		public void setJob(Job job) {
		}

		@Override
		public String getMessage() {
			return null;
		}

		@Override
		public void setMessage(String message) {			
		}
		
	}
	
}
