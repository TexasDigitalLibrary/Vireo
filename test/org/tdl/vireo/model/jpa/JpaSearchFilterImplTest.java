package org.tdl.vireo.model.jpa;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SearchFilter;
import org.tdl.vireo.security.SecurityContext;

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
		filter.addStatus("status1");
		filter.addStatus("status2");
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
		assertTrue(retrieved.getStatus().contains("status1"));
		assertTrue(retrieved.getStatus().contains("status2"));
		assertFalse(retrieved.getStatus().contains("status3"));
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
		assertTrue(retrieved.getDepartment().contains("dept1"));
		assertTrue(retrieved.getDepartment().contains("dept2"));
		assertFalse(retrieved.getDepartment().contains("dept3"));
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
	
}
