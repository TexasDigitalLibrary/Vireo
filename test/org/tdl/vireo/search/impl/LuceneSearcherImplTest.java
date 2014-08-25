package org.tdl.vireo.search.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.jpa.JpaPersonRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSettingsRepositoryImpl;
import org.tdl.vireo.model.jpa.JpaSubmissionRepositoryImpl;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the lucene searcher for both submissions and action logs.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class LuceneSearcherImplTest extends UnitTest{

	// Spring injection
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	public static Indexer indexer = Spring.getBeanOfType(LuceneIndexerImpl.class);
	public static Searcher searcher = Spring.getBeanOfType(LuceneSearcherImpl.class);
	
	// Common state
	public Person person;
	
	/**
	 * Setup before running any tests, clear out the job queue, and create a
	 * test person.
	 * 
	 */
	@Before
	public void setup() throws InterruptedException {
		indexer.rebuild(true);
		assertFalse(indexer.isJobRunning());
		
		context.login(MockPerson.getAdministrator());
		
		person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
	}
	
	/**
	 * Cleanup by committing the database transaction and deleted the person we
	 * created before. Also clear out anything in the index queue before
	 * carrying on.
	 */
	@After
	public void cleanup() throws InterruptedException {
		JPA.em().clear();
		if (person != null)
			personRepo.findPerson(person.getId()).delete();
		context.logout();
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		
		indexer.rebuild(true);
		assertFalse(indexer.isJobRunning());
	}

	
	/**
	 * Test the id search for submisisons.
	 */
	@Test
	public void testSubmissionIDSearch() {
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		SearchFilter filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		
		long[] sortedIds = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING);
		
		
		long lastId = -1;
		for (long id : sortedIds) {			
			assertTrue(lastId < id);
			
			lastId = id;
		}
		
		long total = subRepo.findSubmissionsTotal();
		assertEquals(total,(int)sortedIds.length);
	}
	
	/**
	 * Test the id search for action logs. 
	 */
	@Test
	public void testActionLogIDSearch() {

		
		SearchFilter filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		
		long[] sortedIds = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING);
		
		long lastId = -1;
		for (long id : sortedIds) {
			assertTrue(lastId < id);
			
			lastId = id;
		}
		
		long total = subRepo.findActionLogsTotal();
		assertEquals(total,(int)sortedIds.length);
	}
	
	/**
	 * Okay, there is almost literally is an infinite number of search filters
	 * that we could test. Instead of testing each combination I'm just going to
	 * test the currently implemented clauses and then when we find bugs we'll
	 * add individual tests for those bugs.
	 */
	@Test
	public void testSubmissionFilterSearch() throws InterruptedException {
		Person otherPerson = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.REVIEWER).save();
		
		EmbargoType embargo1 = settingRepo.createEmbargoType("embargo1", "description", 12, true).save();
		EmbargoType embargo2 = settingRepo.createEmbargoType("embargo2", "description", 24, true).save();
		
		Submission sub1 = subRepo.createSubmission(person);
		createSubmission(sub1, "B Title", "This is really important work", "One; Two; Three;", 
				"committee@email.com", "degree", "department", "program", "college", "major",
				"documentType", 2002, 5, true);
		sub1.setAssignee(otherPerson);
		sub1.setSubmissionDate(new Date(2012,5,1));
		sub1.setEmbargoType(embargo2);
		sub1.setState(sub1.getState().getTransitions(sub1).get(0));
		sub1.save();
		
		Submission sub2 = subRepo.createSubmission(person);
		createSubmission(sub2, "A Title", "I really like this work", "One; Four; Five;", 
				"anotherCommittee@email.com", "another", "another", "another", "another", "another",
				"another", 2003, 6, null);
		sub2.setSubmissionDate(new Date(2005,5,1));
		sub2.setEmbargoType(embargo1);
		sub2.save();
				
		// Save our new submissions and add them to the index.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		Thread.sleep(100);
		indexer.rebuild(true);
		assertFalse(indexer.isJobRunning());
		
		// Empty Filter
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "test-empty").save();
		
		try {
			List<Submission> submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			filter.delete();
			
			// Include Submission Filter
			filter = subRepo.createSearchFilter(person, "test-sub");
			filter.addIncludedSubmission(sub1);
			filter.save();
			
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// Exclude Submission Filter
			filter = subRepo.createSearchFilter(person, "test-sub");
			filter.addExcludedSubmission(sub1);
			filter.save();
			
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertFalse(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			filter.delete();
			
			// Search Text Filter
			filter = subRepo.createSearchFilter(person, "test-text");
			filter.addSearchText("important");
			filter.save();
			
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// State Filter
			filter = subRepo.createSearchFilter(person, "test-state");
			filter.addState(stateManager.getInitialState().getBeanName());
			filter.save();
			
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertFalse(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			filter.delete();
			
			// Assignee Filter
			filter = subRepo.createSearchFilter(person, "test-assignee");
			filter.addAssignee(otherPerson);
			filter.save();
			
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// Embargo Filter
			filter = subRepo.createSearchFilter(person, "test-embargo");
			filter.addEmbargoType(embargo1);
			filter.save();
			
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertFalse(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			filter.delete();
			
			// Graduation Semester Filter
			filter = subRepo.createSearchFilter(person, "test-gradSemester1");
			filter.addGraduationSemester(2002,05);
			filter.save();
			
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// Graduation Semester without month Filter
			filter = subRepo.createSearchFilter(person, "test-gradSemester2");
			filter.addGraduationSemester(2003,6);
			filter.save();
	
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertFalse(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			filter.delete();
			
			
			// Degree Filter
			filter = subRepo.createSearchFilter(person, "test-degree");
			filter.addDegree("degree");
			filter.save();
	
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// Department Filter
			filter = subRepo.createSearchFilter(person, "test-department");
			filter.addDepartment("department");
			filter.save();
	
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			//Program Filter
			filter = subRepo.createSearchFilter(person, "test-program");
			filter.addProgram("program");
			filter.save();
			
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// College Filter
			filter = subRepo.createSearchFilter(person, "test-college");
			filter.addCollege("college");
			filter.save();
	
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// Major Filter
			filter = subRepo.createSearchFilter(person, "test-major");
			filter.addMajor("major");
			filter.save();
	
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// Document Type Filter
			filter = subRepo.createSearchFilter(person, "test-document");
			filter.addDocumentType("documentType");
			filter.save();
	
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// UMI Release Filter
			filter = subRepo.createSearchFilter(person, "test-umi");
			filter.setUMIRelease(true);
			filter.save();
	
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertTrue(submissions.contains(sub1));
			assertFalse(submissions.contains(sub2));
			filter.delete();
			
			// Date Range Filter
			filter = subRepo.createSearchFilter(person, "test-range");
			filter.setDateRangeStart(new Date(2000,1,1));
			filter.setDateRangeEnd(new Date(2006,1,1));
			filter.save();
	
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertFalse(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
		
		} finally {
			filter.delete();
			subRepo.findSubmission(sub1.getId()).delete();
			subRepo.findSubmission(sub2.getId()).delete();
			settingRepo.findEmbargoType(embargo1.getId()).delete();
			settingRepo.findEmbargoType(embargo2.getId()).delete();
			personRepo.findPerson(otherPerson.getId()).delete();
		}
	}
	

	/**
	 * Test all the various sort ordering for filter searches on submissions.
	 */
	@Test
	public void testSubmissionFilterSearchOrdering() throws IOException, InterruptedException {
		
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
				"committee@email.com", "degree", "department", "program", "college", "major",
				"documentType", 2012, 5, true);
		sub1.setAssignee(otherPerson);

		sub1.setDocumentLanguage("ak");
		sub1.addDocumentSubject("BBB");
		sub1.setPublishedMaterial("BBB");
		sub1.setEmbargoType(e1);
		sub1.setDocumentType("ZZZZ");
		sub1.addAttachment(file1, AttachmentType.SUPPLEMENTAL);
		sub1.addCommitteeMember("BBBB", "BBBB", "a");
		sub1.addCommitteeMember("CCCC", "CCCC", "a");
		sub1.setDefenseDate(new Date(2012,5,1));
		sub1.setSubmissionDate(new Date(2012,5,1));
		sub1.setApprovalDate(new Date(2012,5,1));
		sub1.setLicenseAgreementDate(new Date(2012,5,1));
		sub1.setCommitteeApprovalDate(new Date(2012,5,1));
		sub1.setCommitteeEmbargoApprovalDate(new Date(2012,5,1));
		sub1.setDepositId("Z");
		sub1.setReviewerNotes("Z");
		sub1.addCustomAction(def, true);
		sub1.setOrcid("2222-2222-2222-2222");

		sub1.save();
		
		// Configure submission 2
		Submission sub2 = subRepo.createSubmission(otherPerson);
		createSubmission(sub2, "A Title", "I really like this work", "One; Four; Five;", 
				"anotherCommittee@email.com", "another", "another", "another", "another", "another",
				"another", 2005, 4, null);
		
		sub2.setDocumentLanguage("aa");
		sub2.addDocumentSubject("AAA");
		sub2.setPublishedMaterial("AAA");
		sub2.setAssignee(person);
		sub2.setEmbargoType(e2);
		sub2.setDocumentType("AAAA");
		sub2.addAttachment(file2, AttachmentType.PRIMARY);
		sub2.addAttachment(file1, AttachmentType.SUPPLEMENTAL);
		sub2.addCommitteeMember("AAAA", "AAAA", "a");
		sub2.setDefenseDate(new Date(2005,5,1));
		sub2.setSubmissionDate(new Date(2005,5,1));
		sub2.setApprovalDate(new Date(2005,5,1));
		sub2.setLicenseAgreementDate(new Date(2005,5,1));
		sub2.setCommitteeApprovalDate(new Date(2002,5,1));
		sub2.setCommitteeEmbargoApprovalDate(new Date(2002,5,1));
		sub2.setDepositId("A");
		sub2.setReviewerNotes("A");
		sub2.setOrcid("1111-1111-1111-1111");
		sub2.save();
		
		
		// Save our new submissions and add them to the index.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		Thread.sleep(100);
		indexer.rebuild(true);
		assertFalse(indexer.isJobRunning());
		
		// Search Text Filter
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "test1");
		filter.addSearchText("Title");
		filter.save();
		List<Submission> submissions;
		try {

			// Submission ID
			submissions = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub2) < submissions.indexOf(sub1));
			
			// Submitter
			submissions = searcher.submissionSearch(filter, SearchOrder.STUDENT_NAME, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub2) < submissions.indexOf(sub1));
			
			// Document Title
			submissions = searcher.submissionSearch(filter, SearchOrder.DOCUMENT_TITLE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Document Abstract
			submissions = searcher.submissionSearch(filter, SearchOrder.DOCUMENT_ABSTRACT, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Document Keywords
			submissions = searcher.submissionSearch(filter, SearchOrder.DOCUMENT_KEYWORDS, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Document Subjects
			submissions = searcher.submissionSearch(filter, SearchOrder.DOCUMENT_SUBJECTS, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Document Language
			submissions = searcher.submissionSearch(filter, SearchOrder.DOCUMENT_LANGUAGE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Published Material
			submissions = searcher.submissionSearch(filter, SearchOrder.PUBLISHED_MATERIAL, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Document EmbargoType
			submissions = searcher.submissionSearch(filter, SearchOrder.EMBARGO_TYPE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub2) < submissions.indexOf(sub1));
			
			// Primary Attachment
			submissions = searcher.submissionSearch(filter, SearchOrder.PRIMARY_DOCUMENT, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub2) < submissions.indexOf(sub1));
			
			// Committee Members
			submissions = searcher.submissionSearch(filter, SearchOrder.COMMITTEE_MEMBERS, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Committee Contact Email
			submissions = searcher.submissionSearch(filter, SearchOrder.COMMITTEE_CONTACT_EMAIL, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Committee Approval Date
			submissions = searcher.submissionSearch(filter, SearchOrder.COMMITTEE_APPROVAL_DATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Committee Embargo Approval Date
			submissions = searcher.submissionSearch(filter, SearchOrder.COMMITTEE_APPROVAL_DATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Submission Date
			submissions = searcher.submissionSearch(filter, SearchOrder.SUBMISSION_DATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Approval Date
			submissions = searcher.submissionSearch(filter, SearchOrder.APPROVAL_DATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
	
			// License Agreement Date
			submissions = searcher.submissionSearch(filter, SearchOrder.LICENSE_AGREEMENT_DATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
	
			// Degree
			submissions = searcher.submissionSearch(filter, SearchOrder.DEGREE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Department
			submissions = searcher.submissionSearch(filter, SearchOrder.DEPARTMENT, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Program
			submissions = searcher.submissionSearch(filter, SearchOrder.COLLEGE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// College
			submissions = searcher.submissionSearch(filter, SearchOrder.COLLEGE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Major
			submissions = searcher.submissionSearch(filter, SearchOrder.MAJOR, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// DocumentType
			submissions = searcher.submissionSearch(filter, SearchOrder.DOCUMENT_TYPE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Graduation Date
			submissions = searcher.submissionSearch(filter, SearchOrder.GRADUATION_DATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Defense Date
			submissions = searcher.submissionSearch(filter, SearchOrder.DEFENSE_DATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// State
			submissions = searcher.submissionSearch(filter, SearchOrder.STATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub2) < submissions.indexOf(sub1));
			
			// Assignee
			submissions = searcher.submissionSearch(filter, SearchOrder.GRADUATION_DATE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// UMI Release
			submissions = searcher.submissionSearch(filter, SearchOrder.UMI_RELEASE, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Custom Action
			submissions = searcher.submissionSearch(filter, SearchOrder.CUSTOM_ACTIONS, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Deposit Id
			submissions = searcher.submissionSearch(filter, SearchOrder.DEPOSIT_ID, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Reviewer Notes
			submissions = searcher.submissionSearch(filter, SearchOrder.REVIEWER_NOTES, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Last Event Entry
			submissions = searcher.submissionSearch(filter, SearchOrder.LAST_EVENT_ENTRY, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
			// Last Event Time
			submissions = searcher.submissionSearch(filter, SearchOrder.LAST_EVENT_TIME, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub2) < submissions.indexOf(sub1));
			
			// ORCID
			submissions = searcher.submissionSearch(filter, SearchOrder.ORCID, SearchDirection.DESCENDING, 0, 20).getResults();
			assertTrue(submissions.contains(sub1));
			assertTrue(submissions.contains(sub2));
			assertTrue(submissions.indexOf(sub1) < submissions.indexOf(sub2));
			
		} finally {
			// Cleanup
			filter.delete();
			subRepo.findSubmission(sub1.getId()).delete();
			subRepo.findSubmission(sub2.getId()).delete();
			file1.delete();
			file2.delete();
			settingRepo.findEmbargoType(e1.getId()).delete();
			settingRepo.findEmbargoType(e2.getId()).delete();
			settingRepo.findCustomActionDefinition(def.getId()).delete();
			personRepo.findPerson(otherPerson.getId()).delete();
		}
		
	}
	
	/**
	 * Creating searching for action logs.
	 */
	@Test
	public void testActionLogFilterSearch() throws InterruptedException {
		// Everything will be assigned to this person.
		Person otherPerson = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.ADMINISTRATOR).save();
		context.login(otherPerson);
		
		EmbargoType embargo1 = settingRepo.createEmbargoType("embargo1", "description", 12, true).save();
		EmbargoType embargo2 = settingRepo.createEmbargoType("embargo2", "description", 24, true).save();
		
		Submission sub1 = subRepo.createSubmission(person);
		createSubmission(sub1, "B UniqueTitle B", "This is really important work", "One; Two; Three;", 
				"committee@email.com", "degree", "department", "program", "college", "major",
				"documentType", 2002, 5, true);
		sub1.setAssignee(otherPerson);
		sub1.setSubmissionDate(new Date(2012,5,1));
		sub1.setEmbargoType(embargo2);
		sub1.save();
		
		Submission sub2 = subRepo.createSubmission(person);
		createSubmission(sub2, "A UniqueTitle A", "I really like this work", "One; Four; Five;", 
				"anotherCommittee@email.com", "another", "another", "another", "another", "another",
				"another", 2003, 6, null);
		sub2.setSubmissionDate(new Date(2005,5,1));
		sub2.setEmbargoType(embargo1);
		sub2.setAssignee(otherPerson);
		sub2.save();
		
		
		// Save our new submissions and add them to the index.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		Thread.sleep(100);
		indexer.rebuild(true);
		
		List<ActionLog> logs;
		NamedSearchFilter filter = null;
		try {
	
			// Empty Filter
			filter = subRepo.createSearchFilter(otherPerson, "test-empty");
			filter.save();
			
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertNotNull(logs);
			assertTrue(logs.size() > 2);
			filter.delete();
			
			// Submission Filter
			filter = subRepo.createSearchFilter(otherPerson, "test-sub");
			filter.addIncludedSubmission(sub2);
			filter.save();
			
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub2,logs.get(0).getSubmission());
			assertEquals("Assignee changed to 'first last'", logs.get(0).getEntry());
			filter.delete();
			
			// Search Text Filter
			filter = subRepo.createSearchFilter(otherPerson, "test-text");
			filter.addAssignee(otherPerson);
			filter.addSearchText("Submission created");
			filter.save();
			
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			String formattedDate = format.format(sub2.getSubmissionDate());
			
			assertEquals(sub2,logs.get(0).getSubmission());
			assertEquals("Submission date set to "+formattedDate, logs.get(0).getEntry());
			filter.delete();
			
			// State Filter
			filter = subRepo.createSearchFilter(otherPerson, "test-state");
			filter.addAssignee(otherPerson);
			filter.addState(stateManager.getInitialState().getBeanName());
			filter.save();
			
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 20).getResults();
			
			assertEquals(sub1,logs.get(0).getSubmission());
			assertEquals("Submission created", logs.get(0).getEntry());
			filter.delete();
			
			// Assignee Filter
			filter = subRepo.createSearchFilter(person, "test-assignee");
			filter.addAssignee(otherPerson);
			filter.save();
			
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub2,logs.get(0).getSubmission());
			assertEquals("Assignee changed to 'first last'", logs.get(0).getEntry());
			filter.delete();
			
			// Embargo Filter
			filter = subRepo.createSearchFilter(person, "test-embargo");
			filter.addAssignee(otherPerson);
			filter.addEmbargoType(embargo1);
			filter.save();
			
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub2,logs.get(0).getSubmission());
			assertEquals("Assignee changed to 'first last'", logs.get(0).getEntry());
			filter.delete();
			
			// Graduation Semester Filter
			filter = subRepo.createSearchFilter(person, "test-semester1");
			filter.addAssignee(otherPerson);
			filter.addGraduationSemester(2002,05);
			filter.save();
			
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
			filter.delete();
			
			// Degree Filter
			filter = subRepo.createSearchFilter(person, "test-degree");
			filter.addAssignee(otherPerson);
			filter.addDegree("degree");
			filter.save();
	
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
			filter.delete();
			
			// Department Filter
			filter = subRepo.createSearchFilter(person, "test-department");
			filter.addAssignee(otherPerson);
			filter.addDepartment("department");
			filter.save();
	
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
			filter.delete();
			
			// Program Filter
			filter = subRepo.createSearchFilter(person, "test-program");
			filter.addAssignee(otherPerson);
			filter.addProgram("program");
			filter.save();
	
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
			filter.delete();
			
			// College Filter
			filter = subRepo.createSearchFilter(person, "test-college");
			filter.addAssignee(otherPerson);
			filter.addCollege("college");
			filter.save();
	
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
			filter.delete();
			
			// Major Filter
			filter = subRepo.createSearchFilter(person, "test-major");
			filter.addAssignee(otherPerson);
			filter.addMajor("major");
			filter.save();
	
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
			filter.delete();
			
			// Document Type Filter
			filter = subRepo.createSearchFilter(person, "test-document");
			filter.addAssignee(otherPerson);
			filter.addDocumentType("documentType");
			filter.save();
	
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
			filter.delete();
			
			// UMI Release Filter
			filter = subRepo.createSearchFilter(person, "test-umi");
			filter.addAssignee(otherPerson);
			filter.setUMIRelease(true);
			filter.save();
	
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub1.getId(),logs.get(0).getSubmission().getId());
			filter.delete();
			
			// Date Range Filter
			filter = subRepo.createSearchFilter(person, "test-range");
			filter.addAssignee(otherPerson);
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
			Date startDate = cal.getTime();
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 2);
			Date endDate = cal.getTime();
			
			
			filter.setDateRangeStart(startDate);
			filter.setDateRangeEnd(endDate);
			filter.save();
	
			logs = searcher.actionLogSearch(filter, SearchOrder.ID, SearchDirection.DESCENDING, 0, 20).getResults();
			
			assertEquals(sub2.getId(),logs.get(0).getSubmission().getId());
		} finally {
			
			filter.delete();
			subRepo.findSubmission(sub1.getId()).delete();
			subRepo.findSubmission(sub2.getId()).delete();
			settingRepo.findEmbargoType(embargo1.getId()).delete();
			settingRepo.findEmbargoType(embargo2.getId()).delete();
			personRepo.findPerson(otherPerson.getId()).delete();
		}
	}
	
	
	
	
	/**
	 * A short cut method for creating a submission.
	 */
	private Submission createSubmission(Submission sub, String title, String docAbstract,
			String keywords, String committeeEmail,
			String degree, String department, String program,
			String college, String major, String documentType,
			Integer gradYear, Integer gradMonth, Boolean UMIRelease) {
	
		sub.setDocumentTitle(title);
		sub.setDocumentAbstract(docAbstract);
		sub.setDocumentKeywords(keywords);
		sub.setCommitteeContactEmail(committeeEmail);
		sub.setCommitteeApprovalDate(new Date());
		sub.setDegree(degree);
		sub.setDepartment(department);
		sub.setProgram(program);
		sub.setCollege(college);
		sub.setMajor(major);
		sub.setDocumentType(documentType);
		sub.setGraduationYear(gradYear);
		sub.setGraduationMonth(gradMonth);
		sub.setUMIRelease(UMIRelease);
		
		return sub;
	}
	
	
	
	
}
