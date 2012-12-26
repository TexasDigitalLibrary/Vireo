package org.tdl.vireo.model.jpa;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.Semester;
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
public class JpaNamedSearchFilterImplTest extends UnitTest {
	
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
		
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		
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
		
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		
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
	 * This is a test to test VIREO-116 which is a bug where a person was only
	 * able to be referenced once by any filter. The second time that person is
	 * referenced there would be a DB failure.
	 */
	@Test
	public void testTwoReferencesSamePerson() {
		
		Person otherPerson = personRepo.createPerson("other-netid", "other@email.com", "first", "last", RoleType.NONE).save();

		NamedSearchFilter filter1 = subRepo.createSearchFilter(person, "test 1");
		filter1.addAssignee(otherPerson);
		filter1.save();
		
		NamedSearchFilter filter2 = subRepo.createSearchFilter(person, "test 2").save();
		filter2.addAssignee(otherPerson);
		filter2.save();
		
		assertEquals(1,filter1.getAssignees().size());
		assertEquals(1,filter2.getAssignees().size());
		
		
		filter1.delete();
		filter2.delete();
		otherPerson.delete();
	}
	
	/**
	 * Test the id.
	 */
	@Test
	public void testId() {
		
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		
		assertNotNull(filter.getId());
		
		filter.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "filter").save();

		NamedSearchFilter retrieved = subRepo.findSearchFilter(filter.getId());
		
		assertEquals(filter.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all filters
	 */
	@Test
	public void testFindAllFilters() {

		int initialSize = subRepo.findAllSearchFilters().size();
		
		NamedSearchFilter filter1 = subRepo.createSearchFilter(person, "filter1").save();
		NamedSearchFilter filter2 = subRepo.createSearchFilter(person, "filter2").save();

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

		
		NamedSearchFilter filter1 = subRepo.createSearchFilter(otherPerson, "public other person").save();
		filter1.setPublic(true);
		filter1.save();
		NamedSearchFilter filter2 = subRepo.createSearchFilter(otherPerson, "private other person").save();
		NamedSearchFilter filter3 = subRepo.createSearchFilter(person, "person").save();
		
		
		List<NamedSearchFilter> filters = subRepo.findSearchFiltersByCreatorOrPublic(person);
		
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

		NamedSearchFilter filter1 = subRepo.createSearchFilter(person, "filter").save();
		NamedSearchFilter filter2 = subRepo.createSearchFilter(otherPerson, "filter").save();
		
		
		NamedSearchFilter retrieved1 = subRepo.findSearchFilterByCreatorAndName(person, "filter");
		NamedSearchFilter retrieved2 = subRepo.findSearchFilterByCreatorAndName(otherPerson, "filter");

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
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		NamedSearchFilter test = subRepo.createSearchFilter(person, "test").save();
		
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

		EmbargoType embargo1 = settingRepo.createEmbargoType("embargo1", "description", 12, true).save();
		EmbargoType embargo2 = settingRepo.createEmbargoType("embargo2", "description", 24, true).save();
		
		Submission sub1 = subRepo.createSubmission(otherPerson).save();
		Submission sub2 = subRepo.createSubmission(otherPerson).save();
		
		ActionLog log1 = sub1.logAction("log1").save();
		ActionLog log2 = sub2.logAction("log2").save();

		
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "filter");
		filter.setPublic(false);
		filter.addIncludedSubmission(sub1);
		filter.addIncludedSubmission(sub2);
		filter.addExcludedSubmission(sub1);
		filter.addExcludedSubmission(sub2);
		filter.addIncludedActionLog(log1);
		filter.addIncludedActionLog(log2);
		filter.addExcludedActionLog(log1);
		filter.addExcludedActionLog(log2);
		filter.addSearchText("text1");
		filter.addSearchText("text2");
		filter.addState("status1");
		filter.addState("status2");
		filter.addAssignee(person);
		filter.addAssignee(otherPerson);
		filter.addEmbargoType(embargo1);
		filter.addEmbargoType(embargo2);
		filter.addGraduationSemester(2002,05);
		filter.addGraduationSemester(2002,null);
		filter.addDegree("degree1");
		filter.addDegree("degree2");
		filter.addDepartment("dept1");
		filter.addDepartment("dept2");
		filter.addProgram("program1");
		filter.addProgram("program2");
		filter.addCollege("college1");
		filter.addCollege("college2");
		filter.addMajor("major1");
		filter.addMajor("major2");
		filter.addDocumentType("docType1");
		filter.addDocumentType("docType2");
		filter.setUMIRelease(true);
		filter.setDateRangeStart(new Date());
		filter.setDateRangeEnd(new Date());
		filter.save();

		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		NamedSearchFilter retrieved = subRepo.findSearchFilter(filter.getId());
		
		//assertFalse(retrieved.isPublic());
		assertTrue(retrieved.getIncludedSubmissions().contains(sub1));
		assertTrue(retrieved.getIncludedSubmissions().contains(sub2));
		assertTrue(retrieved.getExcludedSubmissions().contains(sub1));
		assertTrue(retrieved.getExcludedSubmissions().contains(sub2));
		assertTrue(retrieved.getIncludedActionLogs().contains(log1));
		assertTrue(retrieved.getIncludedActionLogs().contains(log2));
		assertTrue(retrieved.getExcludedActionLogs().contains(log1));
		assertTrue(retrieved.getExcludedActionLogs().contains(log2));
		assertTrue(retrieved.getSearchText().contains("text1"));
		assertTrue(retrieved.getSearchText().contains("text2"));
		assertFalse(retrieved.getSearchText().contains("text3"));
		assertTrue(retrieved.getStates().contains("status1"));
		assertTrue(retrieved.getStates().contains("status2"));
		assertFalse(retrieved.getStates().contains("status3"));
		assertTrue(retrieved.getAssignees().contains(person));
		assertTrue(retrieved.getAssignees().contains(otherPerson));
		assertTrue(retrieved.getEmbargoTypes().contains(embargo1));
		assertTrue(retrieved.getEmbargoTypes().contains(embargo2));
		
		assertTrue(retrieved.getGraduationSemesters().size() == 2);
		boolean foundSemester1 = false;
		boolean foundSemester2 = false;
		for (Semester semester : retrieved.getGraduationSemesters()) {
			if (semester.year == 2002 && semester.month == null)
				foundSemester2 = true;
			else if (semester.year == 2002 && semester.month == 5)
				foundSemester1 = true;
		}
		assertTrue(foundSemester1);
		assertTrue(foundSemester2);
		assertTrue(retrieved.getDegrees().contains("degree1"));
		assertTrue(retrieved.getDegrees().contains("degree2"));
		assertFalse(retrieved.getDegrees().contains("degree3"));
		assertTrue(retrieved.getDepartments().contains("dept1"));
		assertTrue(retrieved.getDepartments().contains("dept2"));
		assertFalse(retrieved.getDepartments().contains("dept3"));
		assertTrue(retrieved.getPrograms().contains("program1"));
		assertTrue(retrieved.getPrograms().contains("program2"));
		assertFalse(retrieved.getPrograms().contains("program3"));
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
		subRepo.findSubmission(sub1.getId()).delete();
		subRepo.findSubmission(sub2.getId()).delete();
		settingRepo.findEmbargoType(embargo1.getId()).delete();
		settingRepo.findEmbargoType(embargo2.getId()).delete();
		personRepo.findPerson(otherPerson.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
		person = null;
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test filtering by unassigned.
	 */
	@Test
	public void testUnassigned() {

		NamedSearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		filter.addAssignee(person);
		filter.addAssignee(null);
		filter.save();

		// Commit and reopen a new transaction.
		JPA.em().flush();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		person = personRepo.findPerson(person.getId());
		NamedSearchFilter retrieved = subRepo.findSearchFilter(filter.getId());
		
		assertTrue(retrieved.getAssignees().contains(person));
		assertTrue(retrieved.getAssignees().contains(null));
		assertEquals(2,retrieved.getAssignees().size());
		
		retrieved.delete();
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
		NamedSearchFilter filter = subRepo.createSearchFilter(person, "filter").save();
		filter.setPublic(true);
		filter.save();
		filter.delete();
		
		try {
			context.login(MockPerson.getReviewer());
			NamedSearchFilter other = subRepo.createSearchFilter(person, "other").save();
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
}
