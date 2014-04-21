package org.tdl.vireo.model.jpa;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.Semester;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;
import org.tdl.vireo.state.impl.StateManagerImpl;

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
		try {
		JPA.em().clear();
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		context.logout();
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		} catch (RuntimeException re) {
			
		}
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
	 * Test finding all programs used by submissions.
	 */
	@Test
	public void testFindAllPrograms() {
		
		Submission sub = subRepo.createSubmission(person);
		sub.setProgram("My Program");
		sub.save();
		
		List<String> programs = subRepo.findAllPrograms();
		assertTrue(programs.contains("My Program"));
		
		sub.delete();
	}
	
	/**
	 * Test finding all colleges used by submissions.
	 */
	@Test
	public void testFindAllColleges() {
		
		Submission sub = subRepo.createSubmission(person);
		sub.setCollege("My College");
		sub.save();
		
		List<String> colleges = subRepo.findAllColleges();
		assertTrue(colleges.contains("My College"));
		
		sub.delete();
	}
	
	/**
	 * Test finding all departments used by submissions.
	 */
	@Test
	public void testFindAllDepartments() {
		
		Submission sub = subRepo.createSubmission(person);
		sub.setDepartment("My Department");
		sub.save();
		
		List<String> departments = subRepo.findAllDepartments();
		assertTrue(departments.contains("My Department"));
		
		sub.delete();
	}
	
	/**
	 * Test finding all majors used by submissions.
	 */
	@Test
	public void testFindAllMajors() {
		
		Submission sub = subRepo.createSubmission(person);
		sub.setMajor("My Major");
		sub.save();
		
		List<String> majors = subRepo.findAllMajors();
		assertTrue(majors.contains("My Major"));
		
		sub.delete();
	}
	
	/**
	 * Test finding all degrees used by submissions.
	 */
	@Test
	public void testFindAllDegrees() {
		
		Submission sub = subRepo.createSubmission(person);
		sub.setDegree("My Degree");
		sub.save();
		
		List<String> degrees = subRepo.findAllDegrees();
		assertTrue(degrees.contains("My Degree"));
		
		sub.delete();
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
	 * Test the approved state
	 */
	@Test
	public void testApproveState() {
		Submission sub = subRepo.createSubmission(person).save();
		
		for(State state : stateManager.getAllStates()) {
			if (state.isApproved()) {
				sub.setState(state);
				break;
			}
		}
		
		assertNotNull(sub.getApprovalDate());
		
		sub.delete();
	}
	
	/**
	 * Test converting document language into a locale
	 */
	@Test
	public void testGetLocale() {
		
		Submission sub = subRepo.createSubmission(person).save();
		
		sub.setDocumentLanguage("de");
		Locale german = LocaleUtils.toLocale("de");
		assertEquals(german, sub.getDocumentLanguageLocale());
		
		sub.setDocumentLanguage("de_CH");
		Locale germanAustria = LocaleUtils.toLocale("de_CH");
		assertEquals(germanAustria, sub.getDocumentLanguageLocale());

		sub.setDocumentLanguage(null);
		assertEquals(null, sub.getDocumentLanguageLocale());
		
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
	 * Test name formatting
	 */
	@Test 
	public void testStudentNameFormat() {
		Submission sub = subRepo.createSubmission(person);

		sub.setStudentFirstName("First");
		sub.setStudentMiddleName("Middle");
		sub.setStudentLastName("Last");
		sub.setStudentBirthYear(1980);
		
		assertEquals("Last, First Middle 1980-",sub.getStudentFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));

	}
	
	
	/**
	 * Test document subjects
	 */
	@Test
	public void testDocumentSubjects() {
		
		Submission sub = subRepo.createSubmission(person);

		assertEquals(0,sub.getDocumentSubjects().size());
		
		sub.addDocumentSubject("one");
		sub.addDocumentSubject("two");
		sub.addDocumentSubject("three");
		
		assertEquals("one",sub.getDocumentSubjects().get(0));
		assertEquals("two",sub.getDocumentSubjects().get(1));
		assertEquals("three",sub.getDocumentSubjects().get(2));
		
		sub.removeDocumentSubject("two");
		
		assertEquals("one",sub.getDocumentSubjects().get(0));
		assertEquals("three",sub.getDocumentSubjects().get(1));
		
		sub.addDocumentSubject("one");
		
		assertEquals("one",sub.getDocumentSubjects().get(0));
		assertEquals("three",sub.getDocumentSubjects().get(1));
		assertEquals("one",sub.getDocumentSubjects().get(2));

		
		sub.removeDocumentSubject("one");
		
		assertEquals("three",sub.getDocumentSubjects().get(0));
		assertEquals("one",sub.getDocumentSubjects().get(1));
	}
	
	
	/**
	 * Test document language
	 */
	@Test
	public void testDocumentLanguage() {
		
		Submission sub = subRepo.createSubmission(person);
		
		assertEquals(null,sub.getDocumentLanguage());
		
		sub.setDocumentLanguage("en");
		
		assertEquals("en",sub.getDocumentLanguage());
		
		sub.setDocumentLanguage(null);
		
		assertEquals(null,sub.getDocumentLanguage());
	}
	
	/**
	 * Test reviewer notes action log are marked as private.
	 */
	@Test
	public void testReviewerNotesLogsArePrivate() {
		
		Submission sub = subRepo.createSubmission(person);
		sub.setReviewerNotes("notes");
		sub.save();
		
		List<ActionLog> logs = subRepo.findActionLog(sub);

		assertEquals(2,logs.size());
		assertEquals("Reviewer notes changed to 'notes'",logs.get(0).getEntry());
		assertTrue(logs.get(0).isPrivate());
		
		sub.delete();
	}
	
	
	/**
	 * Test that action logs are generated appropriately.
	 * @throws InterruptedException 
	 */
	@Test
	public void testActionLogGeneration() throws InterruptedException {
		
		Submission sub = subRepo.createSubmission(person).save();

		sub.setCommitteeContactEmail("advisor@email.com");
		
		Date now = new Date();
		State initialState = stateManager.getInitialState();
		State nextState = initialState.getTransitions(sub).get(0);
		
		sub.setState(nextState);
		sub.save();
		
		
		// Okay, we should start generating action log messages now.
		sub.setStudentFirstName("first");
		sub.setStudentLastName("last");
		sub.setStudentMiddleName("middle");
		sub.setStudentBirthYear(2002);
		sub.setDocumentTitle("docTitle");
		sub.setDocumentAbstract("docAbstract");
		sub.setDocumentKeywords("docKeywords");
		sub.setDocumentLanguage("en");
		sub.addDocumentSubject("subject");
		sub.setPublishedMaterial("published");
		sub.setCommitteeContactEmail("contactEmail");
		sub.setCommitteeEmailHash("hash");
		sub.setCommitteeApprovalDate(now);
		sub.setCommitteeEmbargoApprovalDate(now);
		sub.setSubmissionDate(now);
		sub.setApprovalDate(now);
		sub.setLicenseAgreementDate(now);
		sub.setDegree("degree");
		sub.setDegreeLevel(DegreeLevel.UNDERGRADUATE);
		sub.setDepartment("department");
		sub.setProgram("program");
		sub.setCollege("college");
		sub.setMajor("major");
		sub.setDocumentType("docType");
		sub.setGraduationMonth(0);
		sub.setGraduationYear(2002);
		sub.setUMIRelease(false);
		sub.setDepositId("depositId");
		sub.setDepositDate(now);
		sub.setReviewerNotes("notes");
		
		sub.save();
		
		// Test clearing
		sub.setDocumentTitle(null);
		sub.setDocumentAbstract(null);
		sub.setDocumentKeywords(null);
		sub.setDocumentLanguage(null);
		sub.removeDocumentSubject("subject");
		sub.setPublishedMaterial(null);
		sub.setCommitteeContactEmail(null);
		sub.setCommitteeEmailHash(null);
		sub.setCommitteeApprovalDate(null);
		sub.setCommitteeEmbargoApprovalDate(null);
		sub.setSubmissionDate(null);
		sub.setApprovalDate(null);
		sub.setLicenseAgreementDate(null);
		sub.setDegree(null);
		sub.setDegreeLevel(null);
		sub.setDepartment(null);
		sub.setProgram(null);
		sub.setCollege(null);
		sub.setMajor(null);
		sub.setDocumentType(null);
		sub.setGraduationMonth(null);
		sub.setGraduationYear(null);
		sub.setUMIRelease(null);
		sub.setDepositId(null);
		sub.setDepositDate(null);
		sub.setReviewerNotes(null);

		
		sub.save();
		
		
		
		
		List<ActionLog> logs = subRepo.findActionLog(sub);
		Iterator<ActionLog> logItr = logs.iterator();
		
		sub.delete();
		
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		String formattedDate = format.format(now);

		assertEquals("Document subjects cleared", logItr.next().getEntry());
		assertEquals("Reviewer notes cleared", logItr.next().getEntry());
		assertEquals("Repository deposit ID cleared", logItr.next().getEntry());
		assertEquals("UMI Release cleared", logItr.next().getEntry());
		assertEquals("Graduation year cleared", logItr.next().getEntry());
		assertEquals("Graduation month cleared", logItr.next().getEntry());
		assertEquals("Document type cleared", logItr.next().getEntry());
		assertEquals("Major cleared", logItr.next().getEntry());
		assertEquals("College cleared", logItr.next().getEntry());
		assertEquals("Program cleared", logItr.next().getEntry());
		assertEquals("Department cleared", logItr.next().getEntry());
		assertEquals("Degree level cleared", logItr.next().getEntry());
		assertEquals("Degree cleared", logItr.next().getEntry());
		assertEquals("Submission license agreement cleared", logItr.next().getEntry());
		assertEquals("Submission approval cleared", logItr.next().getEntry());
		assertEquals("Submission date cleared", logItr.next().getEntry());
		assertEquals("Committee approval of embargo cleared", logItr.next().getEntry());
		assertEquals("Committee approval of submission cleared", logItr.next().getEntry());
		assertEquals("New committee email hash generated", logItr.next().getEntry());
		assertEquals("Committee contact email address cleared", logItr.next().getEntry());
		assertEquals("Published material cleared", logItr.next().getEntry());
		assertEquals("Document language cleared", logItr.next().getEntry());
		assertEquals("Document keywords cleared", logItr.next().getEntry());
		assertEquals("Document abstract cleared", logItr.next().getEntry());
		assertEquals("Document title cleared", logItr.next().getEntry());

		assertEquals("Document subjects changed to 'subject'", logItr.next().getEntry());
		assertEquals("Reviewer notes changed to 'notes'", logItr.next().getEntry());
		assertEquals("Repository deposit ID changed to 'depositId'", logItr.next().getEntry());
		assertEquals("UMI Release changed to 'No'", logItr.next().getEntry());
		assertEquals("Graduation year changed to '2002'", logItr.next().getEntry());
		assertEquals("Graduation month changed to 'January'", logItr.next().getEntry());
		assertEquals("Document type changed to 'docType'", logItr.next().getEntry());
		assertEquals("Major changed to 'major'", logItr.next().getEntry());
		assertEquals("College changed to 'college'", logItr.next().getEntry());
		assertEquals("Program changed to 'program'", logItr.next().getEntry());
		assertEquals("Department changed to 'department'", logItr.next().getEntry());
		assertEquals("Degree level changed to 'UNDERGRADUATE'", logItr.next().getEntry());
		assertEquals("Degree changed to 'degree'", logItr.next().getEntry());
		assertEquals("Submission license agreement set", logItr.next().getEntry());
		assertEquals("Submission approval set", logItr.next().getEntry());
		assertEquals("Submission date set to "+formattedDate, logItr.next().getEntry());
		assertEquals("Committee approval of embargo set", logItr.next().getEntry());
		assertEquals("Committee approval of submission set", logItr.next().getEntry());
		assertEquals("New committee email hash generated", logItr.next().getEntry());
		assertEquals("Committee contact email address changed to 'contactEmail'", logItr.next().getEntry());
		assertEquals("Published material changed to 'published'", logItr.next().getEntry());
		assertEquals("Document language changed to 'English'", logItr.next().getEntry());
		assertEquals("Document keywords changed to 'docKeywords'", logItr.next().getEntry());
		assertEquals("Document abstract changed to 'docAbstract'", logItr.next().getEntry());
		assertEquals("Document title changed to 'docTitle'", logItr.next().getEntry());
		assertEquals("Student birth year changed to '2002'",logItr.next().getEntry());
		assertEquals("Student middle name changed to 'middle'",logItr.next().getEntry());		
		assertEquals("Student last name changed to 'last'",logItr.next().getEntry());
		assertEquals("Student first name changed to 'first'",logItr.next().getEntry());	
		assertEquals("Submission status changed to 'Submitted'",logItr.next().getEntry());
		assertEquals("Submission created",logItr.next().getEntry());
		
		assertFalse(logItr.hasNext());
		
		assertEquals("Document subjects cleared", sub.getLastLogEntry());
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
		sub.addDocumentSubject("one");
		sub.addDocumentSubject("two");
		sub.addDocumentSubject("three");
		sub.setDocumentLanguage("en");
		sub.setPublishedMaterial("published");
		sub.setCommitteeContactEmail("contactEmail");
		sub.setCommitteeEmailHash("hash");
		sub.setCommitteeApprovalDate(now);
		sub.setCommitteeEmbargoApprovalDate(now);
		sub.setSubmissionDate(now);
		sub.setApprovalDate(now);
		sub.setLicenseAgreementDate(now);
		sub.setDegree("degree");
		sub.setDegreeLevel(DegreeLevel.UNDERGRADUATE);
		sub.setDepartment("department");
		sub.setProgram("program");
		sub.setCollege("college");
		sub.setMajor("major");
		sub.setDocumentType("docType");
		sub.setGraduationMonth(0);
		sub.setGraduationYear(2002);
		sub.setUMIRelease(false);
		sub.setDepositId("depositId");
		sub.setDepositDate(now);
		sub.setReviewerNotes("notes");
		sub.save();
		
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
		
		assertEquals("docTitle",sub.getDocumentTitle());
		assertEquals("docAbstract",sub.getDocumentAbstract());
		assertEquals("docKeywords",sub.getDocumentKeywords());
		assertEquals("one",sub.getDocumentSubjects().get(0));
		assertEquals("two",sub.getDocumentSubjects().get(1));
		assertEquals("three",sub.getDocumentSubjects().get(2));
		assertEquals("en",sub.getDocumentLanguage());
		assertEquals("published",sub.getPublishedMaterial());
		assertEquals("contactEmail",sub.getCommitteeContactEmail());
		assertEquals("hash",sub.getCommitteeEmailHash());
		assertEquals(now,sub.getCommitteeApprovalDate());
		assertEquals(now,sub.getCommitteeEmbargoApprovalDate());
		assertEquals(now,sub.getSubmissionDate());
		assertEquals(now,sub.getApprovalDate());
		assertEquals(now,sub.getLicenseAgreementDate());
		assertEquals("degree",sub.getDegree());
		assertEquals(DegreeLevel.UNDERGRADUATE,sub.getDegreeLevel());
		assertEquals("department",sub.getDepartment());
		assertEquals("program",sub.getProgram());
		assertEquals("college",sub.getCollege());
		assertEquals("major",sub.getMajor());
		assertEquals("docType",sub.getDocumentType());
		assertEquals(Integer.valueOf(0),sub.getGraduationMonth());
		assertEquals(Integer.valueOf(2002),sub.getGraduationYear());
		assertEquals(Boolean.valueOf(false),sub.getUMIRelease());
		assertEquals("depositId",sub.getDepositId());
		assertEquals(now,sub.getDepositDate());
		assertEquals("notes",sub.getReviewerNotes());

		
		sub.delete();
		person.delete();
		person = null;
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}	
	
	/**
	 * Test that submission is scrubbed of control characters.
	 */
	@Test
	public void testControlScrubbing() {
		
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
		
		// "\u0000-\u0009", // CO Control (including: Bell, Backspace, and Horizontal Tab)
		// "\u000B-\u000C", // CO Control (Line Tab and Form Feed)
		// "\u000E-\u001F", // CO Control (including: Escape)
		// "\u007F",        // CO Control (Delete Character)
		// "\u0080-\u009F"  // C1 Control
		
		// Adding a few control characters from each range
		sub.setDocumentTitle("doc\u0005Title\u0007");
		sub.setDocumentAbstract("doc\u000FAbstract\u0018");
		sub.setDocumentKeywords("doc\u007FKeywords\u000Btest\u000C");
		sub.addDocumentSubject("one");
		sub.addDocumentSubject("two");
		sub.addDocumentSubject("three");
		sub.setDocumentLanguage("en");
		sub.setPublishedMaterial("published\u0088");
		sub.setCommitteeContactEmail("contactEmail");
		sub.setCommitteeEmailHash("hash");
		sub.setCommitteeApprovalDate(now);
		sub.setCommitteeEmbargoApprovalDate(now);
		sub.setSubmissionDate(now);
		sub.setApprovalDate(now);
		sub.setLicenseAgreementDate(now);
		sub.setDegree("degree");
		sub.setDegreeLevel(DegreeLevel.UNDERGRADUATE);
		sub.setDepartment("department");
		sub.setProgram("program");
		sub.setCollege("college");
		sub.setMajor("major");
		sub.setDocumentType("docType");
		sub.setGraduationMonth(0);
		sub.setGraduationYear(2002);
		sub.setUMIRelease(false);
		sub.setDepositId("depositId");
		sub.setDepositDate(now);
		sub.setReviewerNotes("notes");
		sub.save();
		
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		
		sub = subRepo.findSubmission(sub.getId());
		
		assertEquals("docTitle",sub.getDocumentTitle());
		assertEquals("doc Abstract ",sub.getDocumentAbstract());
		assertEquals("doc Keywords test ",sub.getDocumentKeywords());
		assertEquals("one",sub.getDocumentSubjects().get(0));
		assertEquals("two",sub.getDocumentSubjects().get(1));
		assertEquals("three",sub.getDocumentSubjects().get(2));
		assertEquals("en",sub.getDocumentLanguage());
		assertEquals("published ",sub.getPublishedMaterial());
		assertEquals("contactEmail",sub.getCommitteeContactEmail());
		assertEquals("hash",sub.getCommitteeEmailHash());
		assertEquals(now,sub.getCommitteeApprovalDate());
		assertEquals(now,sub.getCommitteeEmbargoApprovalDate());
		assertEquals(now,sub.getSubmissionDate());
		assertEquals(now,sub.getApprovalDate());
		assertEquals(now,sub.getLicenseAgreementDate());
		assertEquals("degree",sub.getDegree());
		assertEquals(DegreeLevel.UNDERGRADUATE,sub.getDegreeLevel());
		assertEquals("department",sub.getDepartment());
		assertEquals("program",sub.getProgram());
		assertEquals("college",sub.getCollege());
		assertEquals("major",sub.getMajor());
		assertEquals("docType",sub.getDocumentType());
		assertEquals(Integer.valueOf(0),sub.getGraduationMonth());
		assertEquals(Integer.valueOf(2002),sub.getGraduationYear());
		assertEquals(Boolean.valueOf(false),sub.getUMIRelease());
		assertEquals("depositId",sub.getDepositId());
		assertEquals(now,sub.getDepositDate());
		assertEquals("notes",sub.getReviewerNotes());

		
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
	
	/**
	 * Test that reviewer notes have limitted access.
	 */
	@Test
	public void testReviewerNotesAccess() {

		context.login(person);
		Submission sub = subRepo.createSubmission(person).save();
		

		// test that the student can't see or edit their own notes.
		try {
			sub.setReviewerNotes("notes");
			fail("student was able to edit their reviewer notes");
		} catch (SecurityException se) {
			/* yay */
		}
		
		context.login(MockPerson.getAdministrator());
		sub.delete();
	}
}
