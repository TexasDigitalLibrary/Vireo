package org.tdl.vireo.model.jpa;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SearchDirection;
import org.tdl.vireo.model.SearchFilter;
import org.tdl.vireo.model.SearchOrder;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the search filter interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaSearchFilterImplTest extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	
	public static Person person;
	
	@Before
	public void setup() {
		context.login(MockPerson.getAdministrator());
		
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
	}
	
	@After
	public void cleanup() {
		JPA.em().clear();
		try {
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		context.logout();
		} catch (RuntimeException re) {
			
		}
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	

	/**
	 * Test creating a search filter
	 */
	@Test
	public void testCreate() {
		
		SearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		
		assertNotNull(filter);
		assertEquals("filter",filter.getName());
		
		filter.delete();
	}
	
	/**
	 * Test filters without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			subRepo.createSearchFilter(null, "filter");
			fail("Able to create search without an owner.");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			subRepo.createSearchFilter(person, null);
			fail("Able to create search filter with null name.");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			subRepo.createSearchFilter(person, "");
			fail("Able to create search filter with blank name.");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate filter
	 */
	@Test
	public void testCreateDuplicate() {
		
		SearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		
		try {
			subRepo.createSearchFilter(person, "filter").save();
			fail("Able to create duplicate search filter");
		} catch (RuntimeException re) {
			/* yay */
		}
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		person = null;
	}
	
	/**
	 * Test the id.
	 */
	@Test
	public void testId() {
		
		SearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		
		assertNotNull(filter.getId());
		
		filter.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		SearchFilter filter = subRepo.createSearchFilter(person, "filter").save();

		SearchFilter retrieved = subRepo.findSearchFilter(filter.getId());
		
		assertEquals(filter.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all filters
	 */
	@Test
	public void testFindAllFilters() {

		int initialSize = subRepo.findAllSearchFilters().size();
		
		SearchFilter filter1 = subRepo.createSearchFilter(person, "filter1").save();
		SearchFilter filter2 = subRepo.createSearchFilter(person, "filter2").save();

		int postSize = subRepo.findAllSearchFilters().size();
		
		assertEquals(initialSize +2, postSize);
		
		filter1.delete();
		filter2.delete();
	}
	
	/**
	 * Test find public and personal filters
	 */
	@Test
	public void testFindByCreatorOrPublic() {
		
		Person otherPerson = personRepo.createPerson("other", "other@email.com", "first", "last", RoleType.NONE).save();

		
		SearchFilter filter1 = subRepo.createSearchFilter(otherPerson, "public other person").save();
		filter1.setPublic(true);
		filter1.save();
		SearchFilter filter2 = subRepo.createSearchFilter(otherPerson, "private other person").save();
		SearchFilter filter3 = subRepo.createSearchFilter(person, "person").save();
		
		
		List<SearchFilter> filters = subRepo.findSearchFiltersByCreatorOrPublic(person);
		
		assertEquals(filter1,filters.get(0));
		assertEquals(filter3,filters.get(1));
		assertEquals(2,filters.size());
		
		filter1.delete();
		filter2.delete();
		filter3.delete();
		otherPerson.delete();
	}
	
	/**
	 * Test retrieving filters by name.
	 */
	@Test
	public void testFindByCreatorAndName() {
		
		Person otherPerson = personRepo.createPerson("other", "other@email.com", "first", "last", RoleType.NONE).save();

		SearchFilter filter1 = subRepo.createSearchFilter(person, "filter").save();
		SearchFilter filter2 = subRepo.createSearchFilter(otherPerson, "filter").save();
		
		
		SearchFilter retrieved1 = subRepo.findSearchFilterByCreatorAndName(person, "filter");
		SearchFilter retrieved2 = subRepo.findSearchFilterByCreatorAndName(otherPerson, "filter");

		assertEquals(filter1,retrieved1);
		assertEquals(filter2,retrieved2);
		
		filter1.delete();
		filter2.delete();
		otherPerson.delete();
	}
	
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		SearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		SearchFilter test = subRepo.createSearchFilter(person, "test").save();
		
		try {
			test.setName(null);
			fail("Able to change name to null");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setName("");
			fail("Able to change name to blank");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setName("filter");
			test.save();
			fail("Able to modify object into duplicate.");
		} catch(RuntimeException re) {
			/* yay */
		}
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		person = null;
	}

	/**
	 * Test that the filter search is persistence.
	 * 
	 * Okay, I admit it I went overboard on this test. I've never used the @ElementCollection
	 * annotation before so I wanted to make doubly sure that it works the way I
	 * think it does.
	 */
	@Test
	public void testPersistance() {
		Person otherPerson = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.NONE).save();

		SearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		filter.setPublic(false);
		filter.addSearchText("text1");
		filter.addSearchText("text2");
		filter.addState("status1");
		filter.addState("status2");
		filter.addAssignee(person);
		filter.addAssignee(otherPerson);
		filter.addGraduationYear(2002);
		filter.addGraduationYear(2003);
		filter.addGraduationMonth(0);
		filter.addGraduationMonth(11);
		filter.addDegree("degree1");
		filter.addDegree("degree2");
		filter.addDepartment("dept1");
		filter.addDepartment("dept2");
		filter.addCollege("college1");
		filter.addCollege("college2");
		filter.addMajor("major1");
		filter.addMajor("major2");
		filter.addDocumentType("docType1");
		filter.addDocumentType("docType2");
		filter.setUMIRelease(true);
		filter.setDateRange(new Date(), new Date());
		filter.save();

		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		SearchFilter retrieved = subRepo.findSearchFilter(filter.getId());
		
		//assertFalse(retrieved.isPublic());
		assertTrue(retrieved.getSearchText().contains("text1"));
		assertTrue(retrieved.getSearchText().contains("text2"));
		assertFalse(retrieved.getSearchText().contains("text3"));
		assertTrue(retrieved.getStates().contains("status1"));
		assertTrue(retrieved.getStates().contains("status2"));
		assertFalse(retrieved.getStates().contains("status3"));
		assertTrue(retrieved.getAssignees().contains(person));
		assertTrue(retrieved.getAssignees().contains(otherPerson));
		assertTrue(retrieved.getGraduationYears().contains(2002));
		assertTrue(retrieved.getGraduationYears().contains(2003));
		assertFalse(retrieved.getGraduationYears().contains(2004));
		assertTrue(retrieved.getGraduationMonths().contains(0));
		assertTrue(retrieved.getGraduationMonths().contains(11));
		assertFalse(retrieved.getGraduationMonths().contains(5));
		assertTrue(retrieved.getDegrees().contains("degree1"));
		assertTrue(retrieved.getDegrees().contains("degree2"));
		assertFalse(retrieved.getDegrees().contains("degree3"));
		assertTrue(retrieved.getDepartments().contains("dept1"));
		assertTrue(retrieved.getDepartments().contains("dept2"));
		assertFalse(retrieved.getDepartments().contains("dept3"));
		assertTrue(retrieved.getColleges().contains("college1"));
		assertTrue(retrieved.getColleges().contains("college2"));
		assertFalse(retrieved.getColleges().contains("college3"));
		assertTrue(retrieved.getMajors().contains("major1"));
		assertTrue(retrieved.getMajors().contains("major2"));
		assertFalse(retrieved.getMajors().contains("major3"));
		assertTrue(retrieved.getDocumentTypes().contains("docType1"));
		assertTrue(retrieved.getDocumentTypes().contains("docType2"));
		assertFalse(retrieved.getDocumentTypes().contains("docType3"));
		assertTrue(retrieved.getUMIRelease());
		assertNotNull(retrieved.getDateRangeStart());
		assertNotNull(retrieved.getDateRangeEnd());
		
		retrieved.delete();
		personRepo.findPerson(otherPerson.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
		person = null;
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that managers can make filters public, others can't
	 */
	@Test
	public void testAccess() {
		
		context.login(MockPerson.getManager());
		SearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		filter.setPublic(true);
		filter.save();
		filter.delete();
		
		try {
			context.login(MockPerson.getReviewer());
			SearchFilter other = subRepo.createSearchFilter(person, "other").save();
			other.setPublic(true);
			other.save();
			fail("A reviewer was able to make a filter public");
		} catch (SecurityException se) {
			/* yay */
		}
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		context.logout();
	}
	

	/**
	 * Okay, there is almost literally is an infinite number of search filters
	 * that we could test. Instead of testing each combination I'm just going to
	 * test the currently implemented clauses and then when we find bugs we'll
	 * add individual tests for those bugs.
	 */
	@Test
	public void testSubmissionFilterSearch() {
		Person otherPerson = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.REVIEWER).save();
		
		Submission sub1 = subRepo.createSubmission(person);
		createSubmission(sub1, "B Title", "This is really important work", "One; Two; Three;", 
				"committee@email.com", "I approve this ETD", "degree", "department", "college", "major",
				"documentType", 2002, 5, true);
		sub1.setAssignee(otherPerson);
		sub1.setSubmissionDate(new Date(2012,5,1));
		sub1.save();
		
		Submission sub2 = subRepo.createSubmission(person);
		createSubmission(sub2, "A Title", "I really like this work", "One; Four; Five;", 
				"anotherCommittee@email.com", "I reject this ETD", "another", "another", "another", "another",
				"another", 2003, 6, null);
		sub2.setSubmissionDate(new Date(2005,5,1));
		sub2.save();
		
		
		// Search Text Filter
		SearchFilter filter = subRepo.createSearchFilter(person, "test1");
		filter.addSearchText("I really%this work");
		filter.save();
		
		List<Submission> submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// State Filter
		filter = subRepo.createSearchFilter(person, "test2");
		filter.addState(stateManager.getInitialState().getBeanName());
		filter.save();
		
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		filter.delete();
		
		// Assignee Filter
		filter = subRepo.createSearchFilter(person, "test3");
		filter.addAssignee(otherPerson);
		filter.save();
		
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Graduation Year Filter
		filter = subRepo.createSearchFilter(person, "test4");
		filter.addGraduationYear(2002);
		filter.addGraduationYear(2003);
		filter.save();
		
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		filter.delete();
		
		// Graduation Month Filter
		filter = subRepo.createSearchFilter(person, "test5");
		filter.addGraduationMonth(5);
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		
		// Degree Filter
		filter = subRepo.createSearchFilter(person, "test6");
		filter.addDegree("degree");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Department Filter
		filter = subRepo.createSearchFilter(person, "test7");
		filter.addDepartment("department");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		
		// College Filter
		filter = subRepo.createSearchFilter(person, "test8");
		filter.addCollege("college");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Major Filter
		filter = subRepo.createSearchFilter(person, "test9");
		filter.addMajor("major");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Document Type Filter
		filter = subRepo.createSearchFilter(person, "test10");
		filter.addDocumentType("documentType");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// UMI Release Filter
		filter = subRepo.createSearchFilter(person, "test11");
		filter.setUMIRelease(true);
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Date Range Filter
		filter = subRepo.createSearchFilter(person, "test12");
		filter.setDateRange(new Date(2000,1,1), new Date(2006,1,1));
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		sub1.delete();
		sub2.delete();
		otherPerson.delete();
	}
	

	/**
	 * Test all the various sort ordering for filter searches on submissions.
	 * @throws IOException
	 */
	@Test
	public void testSubmissionFilterSearchOrdering() throws IOException {
		
		// Setup some other objects to be used, like embargo types, other people, file attachments.
		Person otherPerson = personRepo.createPerson("another-netid", "other@email.com", "zzzz", "zzzz", RoleType.REVIEWER).save();
		EmbargoType e1 = settingRepo.createEmbargoType("Embargo One", "one", null, true);
		e1.setDisplayOrder(100);
		e1.save();
		EmbargoType e2 = settingRepo.createEmbargoType("Embargo Two", "two", null, true);
		e2.setDisplayOrder(10);
		e2.save();
		CustomActionDefinition def = settingRepo.createCustomActionDefinition("Action 1").save();

		
		File file1 = File.createTempFile("Asearch-test", ".dat");
		File file2 = File.createTempFile("Bsearch-test", ".dat");
		FileUtils.writeStringToFile(file1, "Some data");
		FileUtils.writeStringToFile(file2, "Some data");

		
		// Configure submission 1
		Submission sub1 = subRepo.createSubmission(person);
		createSubmission(sub1, "B Title", "This is really important work", "One; Two; Three;", 
				"committee@email.com", "I reject this ETD", "degree", "department", "college", "major",
				"documentType", 2012, 5, true);
		sub1.setAssignee(otherPerson);

		sub1.setEmbargoType(e1);
		sub1.setDocumentType("ZZZZ");
		sub1.addAttachment(file1, AttachmentType.SUPPLEMENTAL);
		sub1.addCommitteeMember("BBBB", "BBBB", "a", false);
		sub1.addCommitteeMember("CCCC", "CCCC", "a", false);
		sub1.setSubmissionDate(new Date(2012,5,1));
		sub1.setApprovalDate(new Date(2012,5,1));
		sub1.setLicenseAgreementDate(new Date(2012,5,1));
		sub1.setCommitteeApprovalDate(new Date(2012,5,1));
		sub1.setCommitteeEmbargoApprovalDate(new Date(2012,5,1));
		sub1.addCustomAction(def, true);

		sub1.save();
		
		// Configure submission 2
		Submission sub2 = subRepo.createSubmission(otherPerson);
		createSubmission(sub2, "A Title", "I really like this work", "One; Four; Five;", 
				"anotherCommittee@email.com", "I approve this ETD", "another", "another", "another", "another",
				"another", 2005, 4, null);
		sub2.setAssignee(person);
		sub2.setEmbargoType(e2);
		sub2.setDocumentType("AAAA");
		sub2.addAttachment(file1, AttachmentType.PRIMARY);
		sub2.addAttachment(file2, AttachmentType.SUPPLEMENTAL);
		sub2.addCommitteeMember("AAAA", "AAAA", "a", false);
		sub2.setSubmissionDate(new Date(2005,5,1));
		sub2.setApprovalDate(new Date(2005,5,1));
		sub2.setLicenseAgreementDate(new Date(2005,5,1));
		sub2.setCommitteeApprovalDate(new Date(2002,5,1));
		sub2.setCommitteeEmbargoApprovalDate(new Date(2002,5,1));
		sub2.save();
		
		// Search Text Filter
		SearchFilter filter = subRepo.createSearchFilter(person, "test1");
		filter.addSearchText("Title");
		filter.save();
		List<Submission> submissions;
		
		
		// Submission ID
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Submitter
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.SUBMITTER, SearchDirection.ASCENDING, 0, 10).getResults();

		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Document Title
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.DOCUMENT_TITLE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Document Abstract
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.DOCUMENT_ABSTRACT, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Document Keywords
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.DOCUMENT_KEYWORDS, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Document EmbargoType
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.EMBARGO_TYPE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Primary Attachment
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.PRIMARY_DOCUMENT, SearchDirection.ASCENDING, 0, 10).getResults();
		// Arg nulls first depending upon database implementation.
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Committee Members
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.COMMITTEE_MEMBERS, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Committee Contact Email
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.COMMITTEE_CONTACT_EMAIL, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Committee Approval Date
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.COMMITTEE_APPROVAL_DATE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Committee Embargo Approval Date
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.COMMITTEE_APPROVAL_DATE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Committee Disposition
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.COMMITTEE_DISPOSITION, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Submission Date
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.SUBMISSION_DATE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Approval Date
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.APPROVAL_DATE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());

		// License Agreement Date
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.LICENSE_AGREEMENT_DATE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());

		// Degree
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.DEGREE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());

		// Department
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.DEPARTMENT, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// College
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.COLLEGE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Major
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.MAJOR, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// DocumentType
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.DOCUMENT_TYPE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Graduation Year
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.GRADUATION_YEAR, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Graduation Month
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.GRADUATION_MONTH, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Graduation Date
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.GRADUATION_DATE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// State
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.STATE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Assignee
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.GRADUATION_DATE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// UMI Release
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.UMI_RELEASE, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Custom Action
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.CUSTOM_ACTIONS, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Last Event Entry
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.LAST_EVENT_ENTRY, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Last Event Time
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.LAST_EVENT_TIME, SearchDirection.ASCENDING, 0, 10).getResults();
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		
		// Cleanup
		filter.delete();
		sub1.delete();
		sub2.delete();
		file1.delete();
		file2.delete();
		otherPerson.delete();
		
	}
	
	/**
	 * Creating searching for action logs.
	 */
	@Test
	public void testActionLogFilterSearch() {
		
		Person otherPerson = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.ADMINISTRATOR).save();
		context.login(otherPerson);
		
		Submission sub1 = subRepo.createSubmission(person);
		createSubmission(sub1, "B Title", "This is really important work", "One; Two; Three;", 
				"committee@email.com", "I approve this ETD", "degree", "department", "college", "major",
				"documentType", 2002, 5, true);
		sub1.setAssignee(otherPerson);
		sub1.setSubmissionDate(new Date(2012,5,1));
		sub1.save();
		
		Submission sub2 = subRepo.createSubmission(person);
		createSubmission(sub2, "A Title", "I really like this work", "One; Four; Five;", 
				"anotherCommittee@email.com", "I reject this ETD", "another", "another", "another", "another",
				"another", 2003, 6, null);
		sub2.setSubmissionDate(new Date(2005,5,1));
		sub2.save();
		
		
		List<ActionLog> logs;
		SearchFilter filter;
		
		// Search Text Filter
		filter = subRepo.createSearchFilter(otherPerson, "test1");
		filter.addSearchText("created");
		filter.save();
		
		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1,logs.get(0).getSubmission());
		assertEquals("Submission created by first last", logs.get(0).getEntry());
		filter.delete();
		
		// State Filter
		filter = subRepo.createSearchFilter(otherPerson, "test2");
		filter.addState(stateManager.getInitialState().getBeanName());
		filter.save();
		
		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 10).getResults();
		
		assertEquals(sub2,logs.get(0).getSubmission());
		assertEquals("Submission date set by first last", logs.get(0).getEntry());
		filter.delete();
		
		// Assignee Filter
		filter = subRepo.createSearchFilter(person, "test3");
		filter.addAssignee(otherPerson);
		filter.save();
		
		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1,logs.get(0).getSubmission());
		assertEquals("Submission created by first last", logs.get(0).getEntry());
		filter.delete();
		
		// Graduation Year Filter
		filter = subRepo.createSearchFilter(person, "test4");
		filter.addGraduationYear(2002);
		filter.addGraduationYear(2003);
		filter.save();
		
		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		// Graduation Month Filter
		filter = subRepo.createSearchFilter(person, "test5");
		filter.addGraduationMonth(5);
		filter.save();

		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		
		// Degree Filter
		filter = subRepo.createSearchFilter(person, "test6");
		filter.addDegree("degree");
		filter.save();

		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		// Department Filter
		filter = subRepo.createSearchFilter(person, "test7");
		filter.addDepartment("department");
		filter.save();

		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		
		// College Filter
		filter = subRepo.createSearchFilter(person, "test8");
		filter.addCollege("college");
		filter.save();

		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		// Major Filter
		filter = subRepo.createSearchFilter(person, "test9");
		filter.addMajor("major");
		filter.save();

		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		// Document Type Filter
		filter = subRepo.createSearchFilter(person, "test10");
		filter.addDocumentType("documentType");
		filter.save();

		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		// UMI Release Filter
		filter = subRepo.createSearchFilter(person, "test11");
		filter.setUMIRelease(true);
		filter.save();

		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		// Date Range Filter
		filter = subRepo.createSearchFilter(person, "test12");
		filter.setDateRange(new Date(2000,1,1), new Date(2006,1,1));
		filter.save();

		logs = subRepo.filterSearchActionLogs(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10).getResults();
		
		assertEquals(sub2.getId(),logs.get(0).getSubmission().getId());
		filter.delete();
		
		sub1.delete();
		sub2.delete();
		otherPerson.delete();
	}
	
	
	
	
	/**
	 * A short cut method for creating a submission.
	 */
	private Submission createSubmission(Submission sub, String title, String docAbstract,
			String keywords, String committeeEmail,
			String committeeDisposition, String degree, String department,
			String college, String major, String documentType,
			Integer gradYear, Integer gradMonth, Boolean UMIRelease) {
	
		sub.setDocumentTitle(title);
		sub.setDocumentAbstract(docAbstract);
		sub.setDocumentKeywords(keywords);
		sub.setCommitteeContactEmail(committeeEmail);
		sub.setCommitteeApprovalDate(new Date());
		sub.setCommitteeDisposition(committeeDisposition);
		sub.setDegree(degree);
		sub.setDepartment(department);
		sub.setCollege(college);
		sub.setMajor(major);
		sub.setDocumentType(documentType);
		sub.setGraduationYear(gradYear);
		sub.setGraduationMonth(gradMonth);
		sub.setUMIRelease(UMIRelease);
		
		return sub;
	}
	
	
	
	
}
