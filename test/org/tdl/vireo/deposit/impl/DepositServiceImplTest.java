package org.tdl.vireo.deposit.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.tdl.vireo.deposit.MockDepositor;
import org.tdl.vireo.deposit.MockPackager;
import org.tdl.vireo.model.MockDepositLocation;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.search.MockSearchFilter;
import org.tdl.vireo.search.MockSearcher;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.state.MockState;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the deposit service. We mock everything so no database interaction is
 * used for this. Nor are the packagers or depositors actualy used.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DepositServiceImplTest extends UnitTest {

	// The deposit service to test
	public static DepositServiceImpl service = Spring.getBeanOfType(DepositServiceImpl.class);

	/**
	 * Test depositing a single item.
	 */
	@Test
	public void testSingleDeposit() throws MalformedURLException {

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
		while (service.isDepositRunning()) {
			Thread.yield();
		}

		// Check the state.		
		String depositId = submission.getDepositId();
		assertNotNull(depositId);
		assertTrue(depositId.startsWith("http://repository.edu/deposit/"));
		assertEquals(successState, submission.getState());
		assertNotNull(submission.getLastLogEntry());

	}

	/**
	 * Test depositing a batch of items.
	 */
	@Test
	public void testBatchDeposit() throws MalformedURLException {

		Searcher originalSearcher = service.searcher;
		try {

			// Set up our mock objects.
			MockDepositLocation location = new MockDepositLocation();
			location.repository = "http://localhost/repository";
			location.collection = "http://localhost/repository/collection";
			location.packager = new MockPackager();
			location.depositor = new MockDepositor();
			MockState successState = new MockState();
			MockSearchFilter filter = new MockSearchFilter();
			MockSearcher searcher = new MockSearcher();
			for (int i=0; i<50; i++)
				searcher.submissions.add(new MockSubmission());



			// Do the deposit
			service.searcher = searcher;
			service.deposit(location, filter, successState);

			// Wait for deposit to finish.
			while (service.isDepositRunning()) {
				Thread.yield();
			}

			// Check the state.
			for (MockSubmission submission : searcher.submissions) {
				String depositId = submission.getDepositId();
				assertNotNull(depositId);
				assertTrue(depositId.startsWith("http://repository.edu/deposit/"));
				assertEquals(successState, submission.getState());
				assertNotNull(submission.getLastLogEntry());
			}



		} finally {
			service.searcher = originalSearcher;
		}

	}





}
