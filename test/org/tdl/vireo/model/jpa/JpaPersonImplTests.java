package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.Test;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test all the properties and characteristics of a JPA-based person object.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaPersonImplTests extends UnitTest {
	
	public static JpaPersonRepositoryImpl repo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	
	/**
	 * Test creating a person
	 */
	@Test
	public void testCreatePerson() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		
		assertEquals("netid",person.getNetId());
		assertEquals("email@email.com",person.getEmail());
		assertEquals("first",person.getFirstName());
		assertEquals("last",person.getLastName());
		assertEquals(RoleType.NONE,person.getRole());
		person.delete();
	}
	
	/**
	 * Test creating a person with null and blank values, they should all fail.
	 */
	@Test
	public void testBadCreatePerson() {
		// Create with null netid
		try {
			repo.createPerson(null, "email@email.com", "first", "last", RoleType.NONE);
			fail("Able to create person with null netid");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with null email
		try {
			repo.createPerson("netid", null, "first", "last", RoleType.NONE);
			fail("Able to create person with null email");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with null first
		try {
			repo.createPerson("netid", "email@email.com",null, "last", RoleType.NONE);
			fail("Able to create person with null first");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with null last
		try {
			repo.createPerson("netid", "email@email.com", "first", null, RoleType.NONE);
			fail("Able to create person with null last");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with null role
		try {
			repo.createPerson("netid", "email@email.com", "first", "last", null);
			fail("Able to create person with null role");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// Create with blank netid
		try {
			repo.createPerson("", "email@email.com", "first", "last", RoleType.NONE);
			fail("Able to create person with blank netid");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with blank email
		try {
			repo.createPerson("netid", "", "first", "last", RoleType.NONE);
			fail("Able to create person with blank email");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with blank first
		try {
			repo.createPerson("netid", "email@email.com","", "last", RoleType.NONE);
			fail("Able to create person with blank first");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with blank last
		try {
			repo.createPerson("netid", "email@email.com", "first", "", RoleType.NONE);
			fail("Able to create person with blank last");
		} catch (IllegalArgumentException iae) {/* yay */}
	}
	
	/**
	 * Test creating a user when one all ready exists for their netid or email address.
	 */
	@Test
	public void testCreateDuplicate() {
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		long personId = person.getId();
		
		// Duplicate netid
		try {
			repo.createPerson("netid", "different@email.com", "different", "different", RoleType.NONE).save();
		} catch (RuntimeException re) {
			// Some exception needs to be thrown either at creation time or at save() time.
		} 
		
		JPA.em().clear();
		
		// Duplicate email
		try {
			repo.createPerson("different", "netid@email.com", "different", "different", RoleType.NONE).save();
		} catch (RuntimeException re) {
			// Some exception needs to be thrown either at creation time or at save() time.
		} 
		
		repo.findPerson(personId).delete();
	}
	
	/**
	 * Test that persons are assigned ids.
	 */
	@Test
	public void testId() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		
		assertNotNull(person.getId());
		person.delete();
	}
	
	/**
	 * Test retrievial by ids
	 */
	@Test
	public void testFindPersonById() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		
		assertNotNull(person.getId());
		
		Person retrieved = repo.findPerson(person.getId());
		assertNotNull(retrieved);
		assertEquals(person.getId(),retrieved.getId());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieval by email
	 */
	@Test 
	public void testFindPersonByEmail() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		assertNotNull(person.getId());
		
		Person retrieved = repo.findPersonByEmail("email@email.com");
		assertNotNull(retrieved);
		assertEquals(person.getId(),retrieved.getId());

		retrieved.delete();
		assertNull(repo.findPersonByEmail("email@email.com"));
	}
	
	/**
	 * Testing retrieval by netid.
	 */
	@Test
	public void testFindPersonByNetId() {
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		assertNotNull(person.getId());
		
		Person retrieved = repo.findPersonByNetId("netid");
		assertNotNull(retrieved);
		assertEquals(person.getId(),retrieved.getId());

		retrieved.delete();
		assertNull(repo.findPersonByNetId("netid"));
	}
	
	/**
	 * Test retrieving all persons
	 */
	@Test
	public void testFindAllPersons() {
		
		int countBefore = repo.findAllPersons().size();
		
		Person person1 = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Person person2 = repo.createPerson("othernetid", "otheremail@email.com", "otherfirst", "otherlast", RoleType.NONE).save();

		List<Person> people = repo.findAllPersons();
		int countAfter = people.size();
		
		assertEquals(countBefore + 2, countAfter);
		
		boolean foundNetId = false;
		boolean foundOther = false;
		for (Person person : people) {
			if ("netid".equals(person.getNetId()))
				foundNetId = true;
			if ("othernetid".equals(person.getNetId()))
				foundOther = true;
		}
		assertTrue(foundNetId);
		assertTrue(foundOther);
		
		person1.delete();
		person2.delete();
	}
	
	/**
	 * Test that none of the required properties can be set to null or blank.
	 */
	@Test
	public void testPropertyValidation() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		
		// Netid to null
		try {
			person.setNetId(null);
			fail("able to set netid to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Email to null
		try {
			person.setEmail(null);
			fail("able to set email to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// First name to null
		try {
			person.setFirstName(null);
			fail("able to set firstName to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Lastname to null
		try {
			person.setLastName(null);
			fail("able to set lastName to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Role to null
		try {
			person.setRole(null);
			fail("able to set role to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Netid to blank
		try {
			person.setNetId("");
			fail("able to set netid to blank");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Email to blank
		try {
			person.setEmail("");
			fail("able to set email to blank");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// First name to blank
		try {
			person.setFirstName("");
			fail("able to set firstName to blank");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Lastname to blank
		try {
			person.setLastName("");
			fail("able to set lastName to blank");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Grad month out of bounds
		try {
			person.setCurrentGraduationMonth(12); // Remember starts at zero
			fail("able to set graduation month beyond 12 months");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Grad month out of bounds
		try {
			person.setCurrentGraduationMonth(-1);
			fail("able to set negative graduation month");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		person.delete();
	}
	
	/**
	 * test that you can get/set all the parameters.
	 */
	@Test
	public void testProperties() {
		Person person = repo.createPerson("netid", "email@email.com", "other", "other", RoleType.NONE);
		person.save();
		
		person.setFirstName("first");
		person.setMiddleInitial("middle");
		person.setLastName("last");
		person.setDisplayName("display");
		person.setBirthYear(1945);
		person.setCurrentPhoneNumber("currentPhone");
		person.setCurrentPostalAddress("currentPostal");
		person.setCurrentEmailAddress("currentEmail");
		person.setPermanentPhoneNumber("PermanentPhone");
		person.setPermanentPostalAddress("PermanentPostal");
		person.setPermanentEmailAddress("PermanentEmail");
		person.setCurrentDepartment("department");
		person.setCurrentCollege("college");
		person.setCurrentMajor("major");
		person.setCurrentGraduationYear(2005);
		person.setCurrentGraduationMonth(5);
		person.setRole(RoleType.ADMINISTRATOR);
		person.save();
		
		
		assertEquals("first",person.getFirstName());
		assertEquals("middle",person.getMiddleInitial());
		assertEquals("last",person.getLastName());
		assertEquals("display",person.getDisplayName());
		assertEquals(Integer.valueOf(1945),person.getBirthYear());
		assertEquals("currentPhone",person.getCurrentPhoneNumber());
		assertEquals("currentPostal",person.getCurrentPostalAddress());
		assertEquals("currentEmail",person.getCurrentEmailAddress());
		assertEquals("PermanentPhone",person.getPermanentPhoneNumber());
		assertEquals("PermanentPostal",person.getPermanentPostalAddress());
		assertEquals("PermanentEmail",person.getPermanentEmailAddress());
		assertEquals("department",person.getCurrentDepartment());
		assertEquals("college",person.getCurrentCollege());
		assertEquals("major",person.getCurrentMajor());
		assertEquals(2005,person.getCurrentGraduationYear());
		assertEquals(Integer.valueOf(5),person.getCurrentGraduationMonth());
		assertEquals(RoleType.ADMINISTRATOR,person.getRole());
		
		person.delete();
	}
	
	/**
	 * Test that you can't get around the unique requirements for netid and
	 * email by changing the values to an existing person.
	 */
	@Test
	public void testChangeToDuplicate() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		
		Person otherPerson = repo.createPerson("other", "other@email.com", "other", "other", RoleType.NONE);
		otherPerson.save();
		
		try {
			otherPerson.setNetId("netid");
			otherPerson.save();
			fail("Able to create duplicate netids.");
		} catch (RuntimeException re) { /* yay */ }
		
		JPA.em().clear();
		
		try {
			otherPerson.setEmail("other@email.com");
			otherPerson.save();
			fail("Able to create duplicate emails.");
		} catch (RuntimeException re) { /* yay */ }
		
		JPA.em().clear();
		
		repo.findPerson(person.getId()).delete();
		repo.findPerson(otherPerson.getId()).delete();
	}
	
	/**
	 * Test that the person object saves state beyond a database transaction.
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
		
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		Long personId = person.getId();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		
		person = repo.findPerson(personId);
		
		assertNotNull(person);
		assertEquals(personId, person.getId());
		assertEquals("netid",person.getNetId());
		assertEquals("email@email.com",person.getEmail());
		assertEquals("first", person.getFirstName());
		assertEquals("last", person.getLastName());
		assertEquals(RoleType.NONE, person.getRole());
		
		person.delete();
		
	}
}
