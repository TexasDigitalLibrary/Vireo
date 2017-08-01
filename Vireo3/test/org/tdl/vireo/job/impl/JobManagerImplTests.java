package org.tdl.vireo.job.impl;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.job.impl.JobManagerImpl;
import org.tdl.vireo.job.impl.JobMetadataImpl;
import org.tdl.vireo.model.MockPerson;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the job manager
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JobManagerImplTests extends UnitTest {

	// The manager to test.
	public static JobManagerImpl manager = Spring
			.getBeanOfType(JobManagerImpl.class);

	/**
	 * Make sure the job queue is inactive before starting any tests.
	 */
	@Before
	public void setup() {

		// Wait for all jobs to complete.
		while (manager.findJobsByStatus(JobStatus.ACTIVE).size() > 0)
			Thread.yield();

		assertEquals(0, manager.findJobsByStatus(JobStatus.ACTIVE).size());
	}

	/**
	 * Test registering and deregistering jobs with the manager.
	 */
	@Test
	public void testRegister() {

		JobMetadata job = manager.register("Register Test");

		try {
			assertNotNull(job.getId());
			assertEquals("Register Test", job.getName());
			assertEquals(JobStatus.WAITING, job.getStatus());

			assertEquals(1, manager.findJobsByStatus(JobStatus.ACTIVE).size());
			assertEquals(job, manager.findJobsByStatus(JobStatus.ACTIVE).get(0));
			assertTrue(manager.findAllJobs().size() >= 1);

		} finally {
			manager.deregister(job);
		}
	}

	/**
	 * Test finding jobs by their owner.
	 */
	@Test
	public void testFindByOwner() {

		MockPerson owner = new MockPerson();

		JobMetadata job = manager.register("Owner Test", owner);

		try {
			assertEquals(1, manager.findJobsByOwner(owner).size());
			assertEquals(job, manager.findJobsByOwner(owner).get(0));
		} finally {
			manager.deregister(job);
		}
	}

	/**
	 * Test finding jobs by their type.
	 */
	@Test
	public void testFindByType() {

		JobMetadata job = manager.register("Type Test");

		try {
			assertContains(manager.findJobsByType(JobMetadata.class), job);
		} finally {
			manager.deregister(job);
		}

	}

	/**
	 * Test finding jobs by their UUID
	 */
	@Test
	public void testFindByUUID() {

		JobMetadata job = manager.register("ID Test");

		try {
			assertEquals(job, manager.findJob(job.getId()));
			assertEquals(null, manager.findJob(UUID.randomUUID()));
		} finally {
			manager.deregister(job);
		}

	}

	/**
	 * Test how the manager prunes old jobs.
	 */
	@Test
	public void testPruning() {

		int originalMaxSize = manager.maxSize;

		try {
			manager.maxSize = 2;

			JobMetadata op1 = manager.register("t1");
			assertContains(manager.findAllJobs(), op1);

			JobMetadata op2 = manager.register("t2");
			assertContains(manager.findAllJobs(), op2, op1);

			op2.setStatus(JobStatus.SUCCESS);
			JobMetadata op3 = manager.register("t3");
			assertContains(manager.findAllJobs(), op3, op1);

			JobMetadata op4 = manager.register("t4");
			JobMetadata op5 = manager.register("t5");
			assertContains(manager.findAllJobs(), op5, op4, op3, op1);

			op1.setStatus(JobStatus.SUCCESS);
			op3.setStatus(JobStatus.CANCELLED);
			op4.setStatus(JobStatus.FAILED);
			op5.setStatus(JobStatus.SUCCESS);
			JobMetadata op6 = manager.register("t6");
			assertContains(manager.findAllJobs(), op6, op5);

			manager.deregister(op6);
			manager.deregister(op5);

		} finally {
			manager.maxSize = originalMaxSize;
		}

	}

	/**
	 * Helpful assert method. It asserts that all the expected occure in the
	 * test list in the same order.
	 * 
	 * @param test
	 *            The list to test.
	 * @param expected
	 *            An orderded list of expected elements.
	 */
	public void assertContains(List<JobMetadata> test, JobMetadata... expected) {

		int i = -1;
		for (JobMetadata job : expected) {

			assertTrue(test.contains(job));
			assertTrue(i < test.indexOf(job));

			i = test.indexOf(job);
		}

	}

}
