package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.Semester;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;
import org.tdl.vireo.state.impl.StateManagerImpl;

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
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
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
		context.login(MockPerson.getAdministrator());
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
	}
	
	/**
	 * Cleanup the person after each test.
	 */
	@After
	public void cleanup() {
		JPA.em().clear();
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		context.logout();
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test creating a new submission
	 */
	@Test
	public void testCreateSubmission() {
		
		Submission sub = subRepo.createSubmission(person).save();

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
	 * Test finding all submissions using an iterator.
	 */
	@Test
	public void testfindAllSubmissions() {
		
		List<Submission> subs = new ArrayList<Submission>();
		for (int i = 0; i < JpaSubmissionRepositoryImpl.ITERATOR_BATCH_SIZE; i++) {
			subs.add((Submission) subRepo.createSubmission(person).save());
		}
		
		Iterator<Submission> itr = subRepo.findAllSubmissions();
		
		int count = 0;
		while(itr.hasNext()) {
			Submission sub = itr.next();
			
			assertNotNull(sub);
			count++;
		}
		
		assertTrue(subs.size() < count);
		
		for (Submission sub : subs) {
			subRepo.findSubmission(sub.getId()).delete();
		}
	}
	
	/**
	 * Test find total # of submissions 
	 */
	@Test
	public void testFindSubmissionsTotal() {
		
		long previousTotal = subRepo.findSubmissionsTotal();
		
		List<Submission> subs = new ArrayList<Submission>();
		for (int i = 0; i < 10; i++) {
			subs.add((Submission) subRepo.createSubmission(person).save());
		}
		
		assertEquals(previousTotal+10, subRepo.findSubmissionsTotal());
		
		for (Submission sub : subs) {
			subRepo.findSubmission(sub.getId()).delete();
		}
	}
	
	/**
	 * Test find all distinct graduation semesters.
	 */
	@Test
	public void testFindAllGradSemesters() {
		
		Submission sub2002 = subRepo.createSubmission(person);
		Submission sub2003 = subRepo.createSubmission(person);
		Submission sub2005 = subRepo.createSubmission(person);
		Submission subNull = subRepo.createSubmission(person);
		
		sub2002.setGraduationYear(2002);
		sub2002.setGraduationMonth(05);
		sub2003.setGraduationYear(2003);
		sub2003.setGraduationMonth(11);
		sub2005.setGraduationYear(2005);
		sub2005.setGraduationMonth(05);
		
		sub2002.save();
		sub2003.save();
		sub2005.save();
		subNull.save();
		
		
		List<Semester> semesters = subRepo.findAllGraduationSemesters();
		
		sub2002.delete();
		sub2003.delete();
		sub2005.delete();
		subNull.delete();
		
		// Remember there may be other submissions causing other data points.
		assertNotNull(semesters);
		assertTrue(semesters.contains(new Semester(2002,05)));
		assertTrue(semesters.contains(new Semester(2003,11)));
		assertTrue(semesters.contains(new Semester(2005,05)));
		assertTrue(semesters.size() >= 3);
	}
	
	/**
	 * Test find all distinct submission years
	 */
	@Test
	public void testFindAllSubmissionYears() {
		
		Submission sub2002 = subRepo.createSubmission(person);
		Submission sub2003 = subRepo.createSubmission(person);
		Submission sub2005 = subRepo.createSubmission(person);
		Submission subNull = subRepo.createSubmission(person);
		
		sub2002.setSubmissionDate(new Date(102, 05, 01));
		sub2003.setSubmissionDate(new Date(103, 06, 28));
		sub2005.setSubmissionDate(new Date(105, 11, 31));
		
		sub2002.save();
		sub2003.save();
		sub2005.save();
		subNull.save();
				
		List<Integer> years = subRepo.findAllSubmissionYears();
		
		sub2002.delete();
		sub2003.delete();
		sub2005.delete();
		subNull.delete();
		
		// Remember there may be other submissions causing other data points.
		assertNotNull(years);
		assertTrue(years.contains(2002));
		assertTrue(years.contains(2003));
		assertTrue(years.contains(2005));
		assertTrue(years.size() >= 3);
	}
	
	/**
	 * Test getting and setting state.
	 */
	@Test
	public void testState() {
		Submission sub = subRepo.createSubmission(person);
		
		State initial = stateManager.getInitialState();
		
		// Check that a submisison starts with the initial state.
		assertEquals(initial,sub.getState());
		
		// Change the state
		State next = initial.getTransitions(sub).get(0);
		sub.setState(next);
		sub.save();
		assertEquals(next,sub.getState());
		
		// Attempt to change to a null state.
		try {
			sub.setState(null);
			fail("Able to set the state to null.");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		assertEquals(next,sub.getState());
		
		sub.delete();
	}
	
	/**
	 * Test validation of graduation months.
	 */
	@Test
	public void testGradMonth() {
		
		Submission sub = subRepo.createSubmission(person).save();
		
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
		
		Submission sub1 = subRepo.createSubmission(person).save();
		
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
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		person = null;
	}
	
	/**
	 * Test interactition with embargo types.
	 */
	@Test
	public void testEmbargoType() {
		Submission sub = subRepo.createSubmission(person);
		EmbargoType embargo = settingRepo.createEmbargoType("embargo", "embargo description", 12, true).save();
		
		sub.setEmbargoType(embargo);
		sub.save();
		assertEquals(embargo,sub.getEmbargoType());		
		
		sub.delete();
		embargo.delete();
	}
	
	/**
	 * Test that action logs are generated appropriately.
	 */
	@Test
	public void testActionLogGeneration() {
		
		Submission sub = subRepo.createSubmission(person);

		Date now = new Date();
		State initialState = stateManager.getInitialState();
		State nextState = initialState.getTransitions(sub).get(0);
		
		sub.setState(nextState);
		
		// Okay, we should start generating action log messages now.
		sub.setStudentFirstName("first");
		sub.setStudentLastName("last");
		sub.setStudentMiddleName("middle");
		sub.setStudentBirthYear(2002);
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
		sub.setDepositId("depositId");
		
		// Test clearing
		sub.setDocumentTitle(null);
		sub.setDocumentAbstract(null);
		sub.setDocumentKeywords(null);
		sub.setCommitteeContactEmail(null);
		sub.setCommitteeEmailHash(null);
		sub.setCommitteeApprovalDate(null);
		sub.setCommitteeEmbargoApprovalDate(null);
		sub.setCommitteeDisposition(null);
		sub.setSubmissionDate(null);
		sub.setApprovalDate(null);
		sub.setLicenseAgreementDate(null);
		sub.setDegree(null);
		sub.setDepartment(null);
		sub.setCollege(null);
		sub.setMajor(null);
		sub.setDocumentType(null);
		sub.setGraduationMonth(null);
		sub.setGraduationYear(null);
		sub.setUMIRelease(null);
		sub.setDepositId(null);

		
		sub.save();
		
		
		
		
		List<ActionLog> logs = subRepo.findActionLog(sub);
		Iterator<ActionLog> logItr = logs.iterator();
		
		sub.delete();
		
		assertEquals("Repository deposit ID cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("UMI Release cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Graduation year cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Graduation month cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Document type cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Major cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("College cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Department cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Degree cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Submission license agreement cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Submission approval cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Submission date cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Committee disposition cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Committee approval of embargo cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Committee approval of submission cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("New committee email hash generated by Mock Administrator", logItr.next().getEntry());
		assertEquals("Committee contact email address cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Document keywords cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Document abstract cleared by Mock Administrator", logItr.next().getEntry());
		assertEquals("Document title cleared by Mock Administrator", logItr.next().getEntry());

		assertEquals("Repository deposit ID changed to 'depositId' by Mock Administrator", logItr.next().getEntry());
		assertEquals("UMI Release changed to 'No' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Graduation year changed to '2002' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Graduation month changed to 'January' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Document type changed to 'docType' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Major changed to 'major' by Mock Administrator", logItr.next().getEntry());
		assertEquals("College changed to 'college' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Department changed to 'department' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Degree changed to 'degree' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Submission license agreement set by Mock Administrator", logItr.next().getEntry());
		assertEquals("Submission approval set by Mock Administrator", logItr.next().getEntry());
		assertEquals("Submission date set by Mock Administrator", logItr.next().getEntry());
		assertEquals("Committee disposition changed to 'disposition' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Committee approval of embargo set by Mock Administrator", logItr.next().getEntry());
		assertEquals("Committee approval of submission set by Mock Administrator", logItr.next().getEntry());
		assertEquals("New committee email hash generated by Mock Administrator", logItr.next().getEntry());
		assertEquals("Committee contact email address changed to 'contactEmail' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Document keywords changed to 'docKeywords' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Document abstract changed to 'docAbstract' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Document title changed to 'docTitle' by Mock Administrator", logItr.next().getEntry());
		assertEquals("Student birth year changed to '2002' by Mock Administrator",logItr.next().getEntry());
		assertEquals("Student middle name changed to 'middle' by Mock Administrator",logItr.next().getEntry());
		assertEquals("Student last name changed to 'last' by Mock Administrator",logItr.next().getEntry());
		assertEquals("Student first name changed to 'first' by Mock Administrator",logItr.next().getEntry());
		assertEquals("Submission status changed to 'Submitted' by Mock Administrator",logItr.next().getEntry());
		assertEquals("Submission created by Mock Administrator",logItr.next().getEntry());
		
		assertFalse(logItr.hasNext());
		
		assertEquals("Repository deposit ID cleared by Mock Administrator", sub.getLastLogEntry());
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
		sub.setDepositId("depositId");
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
		assertEquals("depositId",sub.getDepositId());

		
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
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		this.person = null;
	}
	
	/**
	 * Test who has access to the submission, and who does not.
	 */
	@Test
	public void testAccess() {

		// Test that the owner can edit their submission.
		context.login(person);
		Submission sub = subRepo.createSubmission(person).save();
		sub.setDocumentTitle("changed");
		sub.save();

		
		// Test that a reviewer can edit a submission
		context.login(MockPerson.getReviewer());
		sub.setDocumentAbstract("changed");
		sub.save();
		
		// Test that someone else can not.
		try {
			context.login(MockPerson.getStudent());
			sub.setDocumentKeywords("changed");
			sub.save();
			fail("Someone else was able to modify a submission.");
		} catch (SecurityException se) {
			/* yay */
		}
		
		context.login(MockPerson.getAdministrator());
		sub.delete();
	}
}
