package org.tdl.vireo.model.jpa;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
	 * Okay, there is almost literally is an infinite number of search filters
	 * that we could test. Instead of testing each combination I'm just going to
	 * test the currently implemented clauses and then when we find bugs we'll
	 * add individual tests for those bugs.
	 */
	@Test
	public void testSubmissionFilterSearch() {
		Person otherPerson = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.REVIEWER).save();
		
		Submission sub1 = createSubmission("B Title", "This is really important work", "One; Two; Three;", 
				"committee@email.com", "I approve this ETD", "degree", "department", "college", "major",
				"documentType", 2002, 5, true);
		sub1.setAssignee(otherPerson);
		sub1.save();
		Submission sub2 = createSubmission("A Title", "I really like this work", "One; Four; Five;", 
				"anotherCommittee@email.com", "I reject this ETD", "another", "another", "another", "another",
				"another", 2003, 6, null).save();
		
		
		// Search Text Filter
		SearchFilter filter = subRepo.createSearchFilter(person, "test1");
		filter.addSearchText("I really%this work");
		filter.save();
		
		List<Submission> submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// State Filter
		filter = subRepo.createSearchFilter(person, "test2");
		filter.addState(stateManager.getInitialState().getBeanName());
		filter.save();
		
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		filter.delete();
		
		// Assignee Filter
		filter = subRepo.createSearchFilter(person, "test3");
		filter.addAssignee(otherPerson);
		filter.save();
		
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Graduation Year Filter
		filter = subRepo.createSearchFilter(person, "test4");
		filter.addGraduationYear(2002);
		filter.addGraduationYear(2003);
		filter.save();
		
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(sub2.getId(),submissions.get(1).getId());
		assertEquals(2,submissions.size());
		filter.delete();
		
		// Graduation Month Filter
		filter = subRepo.createSearchFilter(person, "test5");
		filter.addGraduationMonth(5);
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		
		// Degree Filter
		filter = subRepo.createSearchFilter(person, "test6");
		filter.addDegree("degree");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Department Filter
		filter = subRepo.createSearchFilter(person, "test7");
		filter.addDepartment("department");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		
		// College Filter
		filter = subRepo.createSearchFilter(person, "test8");
		filter.addCollege("college");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Major Filter
		filter = subRepo.createSearchFilter(person, "test9");
		filter.addMajor("major");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Document Type Filter
		filter = subRepo.createSearchFilter(person, "test10");
		filter.addDocumentType("documentType");
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// UMI Release Filter
		filter = subRepo.createSearchFilter(person, "test11");
		filter.setUMIRelease(true);
		filter.save();

		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.ID, SearchDirection.ASCENDING, 0, 10);
		
		assertEquals(sub1.getId(),submissions.get(0).getId());
		assertEquals(1,submissions.size());
		filter.delete();
		
		// Test a different sort order.
		filter = subRepo.createSearchFilter(person, "test12");
		filter.addSearchText("Title"); // should match both titles
		filter.save();
		
		submissions = subRepo.filterSearchSubmissions(filter, SearchOrder.DOCUMENT_TITLE, SearchDirection.DESCENDING, 0, 10);
		
		assertEquals(sub2.getId(),submissions.get(0).getId());
		assertEquals(sub1.getId(),submissions.get(1).getId());

		assertEquals(2,submissions.size());
		filter.delete();
		
		sub1.delete();
		sub2.delete();
		otherPerson.delete();
	}
	
	/**
	 * A short cut method for creating a submission.
	 */
	private Submission createSubmission(String title, String docAbstract,
			String keywords, String committeeEmail,
			String committeeDisposition, String degree, String department,
			String college, String major, String documentType,
			Integer gradYear, Integer gradMonth, Boolean UMIRelease) {
	
		Submission sub = subRepo.createSubmission(person);
		
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
