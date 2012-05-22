package org.tdl.vireo.model.jpa;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;
import org.tdl.vireo.state.simple.StateManagerImpl;

import play.Logger;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the Submission JPA implementation
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaSubmissionImplTests extends UnitTest {

	// All the repositories
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);

	public static StateManager stateManager = Spring.getBeanOfType(StateManagerImpl.class);
	
	// All the tests share the same person.
	public static Person person;
	
	/**
	 * Create a new person for each test.
	 */
	@Before
	public void setup() {
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
	}
	
	/**
	 * Cleanup the person after each test.
	 */
	@After
	public void cleanup() {
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
	}
	
	/**
	 * Test creating a new submission
	 */
	@Test
	public void testCreateSubmission() {
		
		Submission sub = subRepo.createSubmission(person);

		assertNotNull(sub);
		assertEquals(person,sub.getSubmitter());
		
		sub.delete();
	}
	
	/**
	 * Test creating a bad submission, i.e. a no submitter.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testBadCreateSubmission() {
		subRepo.createSubmission(null);
	}
	
	/**
	 * Test that saved submission have ids.
	 */
	@Test
	public void testId() {
		Submission sub = subRepo.createSubmission(person).save();

		assertNotNull(sub);
		assertNotNull(sub.getId());
		
		sub.delete();
	}

	/**
	 * Test retrieving by a submission's id.
	 */
	@Test
	public void testFindById() {
		
		Submission sub = subRepo.createSubmission(person).save();

		
		Submission retrieved = subRepo.findSubmission(sub.getId());
		assertNotNull(retrieved);
		assertEquals(sub.getId(),retrieved.getId());
		assertEquals(person,retrieved.getSubmitter());
		
		sub.delete();
	}
	
	/**
	 * Test retrieving by submitters
	 */
	@Test
	public void testFindBySubmitter() {
		Submission sub = subRepo.createSubmission(person).save();

		
		List<Submission> submissions = subRepo.findSubmission(person);
		
		assertNotNull(submissions);
		assertEquals(1,submissions.size());
		assertEquals(sub.getId(),submissions.get(0).getId());
		
		sub.delete();
	}
	
	/**
	 * Test retrieving by email hash.
	 */
	@Test
	public void testFindByEmailHash() {
		Submission sub = subRepo.createSubmission(person);
		
		sub.setCommitteeEmailHash("hash");
		sub.save();
		
		Submission retrieved = subRepo.findSubmissionByEmailHash("hash");
		
		assertNotNull(retrieved);
		assertEquals(sub.getId(),retrieved.getId());
		
		retrieved.delete();
	}
	
	/**
	 * Test getting and setting state.
	 */
	@Test
	public void testState() {
		Submission sub = subRepo.createSubmission(person);
		
		State state = stateManager.getInitialState();
		
		sub.setState(state);
		sub.save();
		
		State retrieved = sub.getState();;
		assertNotNull(retrieved);
		assertEquals(state.getBeanName(),retrieved.getBeanName());
	
		sub.delete();
	}
	
	/**
	 * Test validation of graduation months.
	 */
	@Test
	public void testGradMonth() {
		
		Submission sub = subRepo.createSubmission(person);
		
		sub.setGraduationMonth(0);
		assertEquals(Integer.valueOf(0),sub.getGraduationMonth());
		
		sub.setGraduationMonth(11);
		assertEquals(Integer.valueOf(11),sub.getGraduationMonth());

		sub.setGraduationMonth(null);
		assertNull(sub.getGraduationMonth());		

		try {
			sub.setGraduationMonth(-1);
			fail("able to set month to -1");
		} catch (IllegalArgumentException iae) { /* yay */ }

		try {
			sub.setGraduationMonth(12);
			fail("able to set month to 12");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		sub.delete();
	}
	
	/**
	 * Test get/setting email hashes, and ensure they are unique.
	 */
	@Test
	public void testEmailHash() {
		
		Submission sub1 = subRepo.createSubmission(person);
		
		sub1.setCommitteeEmailHash("hash");
		assertEquals("hash",sub1.getCommitteeEmailHash());
		sub1.save();
		
		Submission sub2 = subRepo.createSubmission(person);
		
		try {
			sub2.setCommitteeEmailHash("hash");
			sub2.save();
			fail("Able to duplicate email hashes.");
		} catch (RuntimeException re) {
			/* yay */
		}
		
		JPA.em().clear();
		
		sub1 = subRepo.findSubmission(sub1.getId());
		sub1.delete();
	}
	
	/**
	 * Test interactition with embargo types.
	 */
	@Test
	public void testEmbargoType() {
		Submission sub = subRepo.createSubmission(person);
		EmbargoType embargo = settingRepo.createEmbargoType("embargo", "embargo description", 12L, true).save();
		
		sub.setEmbargoType(embargo);
		sub.save();
		assertEquals(embargo,sub.getEmbargoType());		
		
		sub.delete();
		embargo.delete();
	}
	
	/**
	 * Test that submission is persistent
	 */
	@Test
	public void testPersistance() {
		
		// Commit and reopen a new transaction because some of the other tests
		// may have caused exceptions which set the transaction to be rolled
		// back.
		if (JPA.em().getTransaction().getRollbackOnly())
			JPA.em().getTransaction().rollback();
		else
			JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		
		
		Submission sub = subRepo.createSubmission(person);

		Date now = new Date();
		
		sub.setDocumentTitle("docTitle");
		sub.setDocumentAbstract("docAbstract");
		sub.setDocumentKeywords("docKeywords");
		sub.setCommitteeContactEmail("contactEmail");
		sub.setCommitteeEmailHash("hash");
		sub.setCommitteeApprovalDate(now);
		sub.setCommitteeEmbargoApprovalDate(now);
		sub.setCommitteeDisposition("disposition");
		sub.setSubmissionDate(now);
		sub.setApprovalDate(now);
		sub.setLicenseAgreementDate(now);
		sub.setDegree("degree");
		sub.setDepartment("department");
		sub.setCollege("college");
		sub.setMajor("major");
		sub.setDocumentType("docType");
		sub.setGraduationMonth(0);
		sub.setGraduationYear(2002);
		sub.setUMIRelease(false);
		sub.save();
		
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
		
		assertEquals("docTitle",sub.getDocumentTitle());
		assertEquals("docAbstract",sub.getDocumentAbstract());
		assertEquals("docKeywords",sub.getDocumentKeywords());
		assertEquals("contactEmail",sub.getCommitteeContactEmail());
		assertEquals("hash",sub.getCommitteeEmailHash());
		assertEquals(now,sub.getCommitteeApprovalDate());
		assertEquals(now,sub.getCommitteeEmbargoApprovalDate());
		assertEquals("disposition",sub.getCommitteeDisposition());
		assertEquals(now,sub.getSubmissionDate());
		assertEquals(now,sub.getApprovalDate());
		assertEquals(now,sub.getLicenseAgreementDate());
		assertEquals("degree",sub.getDegree());
		assertEquals("department",sub.getDepartment());
		assertEquals("college",sub.getCollege());
		assertEquals("major",sub.getMajor());
		assertEquals("docType",sub.getDocumentType());
		assertEquals(Integer.valueOf(0),sub.getGraduationMonth());
		assertEquals(Integer.valueOf(2002),sub.getGraduationYear());
		assertEquals(Boolean.valueOf(false),sub.getUMIRelease());
		
		sub.delete();
		person.delete();
		person = null;
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that a person with a submission may not be deleted.
	 */
	@Test
	public void testDeletingPerson() {
		
		Person person = personRepo.createPerson("tobedeleted", "deleted@email.com", "first", "last", RoleType.NONE);
		Submission sub = subRepo.createSubmission(person);
		
		person.save();
		sub.save();
		
		try {
			person.delete();
			fail("able to delete person who has a submission.");
		} catch (RuntimeException re) {
			/* yay */
		}

		JPA.em().clear();

		subRepo.findSubmission(sub.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
	}
}
