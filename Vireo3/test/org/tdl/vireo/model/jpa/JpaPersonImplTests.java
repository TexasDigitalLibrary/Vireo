package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test all the properties and characteristics of a JPA-based person object.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaPersonImplTests extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaPersonRepositoryImpl repo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	
	
	@Before
	public void setup() {
		context.login(MockPerson.getAdministrator());
	}
	
	@After
	public void cleanup() {
		JPA.em().clear();
		context.logout();
		
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	
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
		
		// create with null email
		try {
			repo.createPerson("netid", null, "first", "last", RoleType.NONE);
			fail("Able to create person with null email");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with null first & last name
		try {
			repo.createPerson("netid", "email@email.com", null, null, RoleType.NONE);
			fail("Able to create person with null last");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with null role
		try {
			repo.createPerson("netid", "email@email.com", "first", "last", null);
			fail("Able to create person with null role");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with blank email
		try {
			repo.createPerson("netid", "", "first", "last", RoleType.NONE);
			fail("Able to create person with blank email");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// create with blank first & last name
		try {
			repo.createPerson("netid", "email@email.com","", "", RoleType.NONE);
			fail("Able to create person with blank first");
		} catch (IllegalArgumentException iae) {/* yay */}
		
	}
	
	/**
	 * Test creating a user when one all ready exists for their netid or email address.
	 */
	@Test
	public void testCreateDuplicate() {
		repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		
		// Duplicate netid
		try {
			repo.createPerson("netid", "different@email.com", "different", "different", RoleType.NONE).save();
		} catch (RuntimeException re) {
			// Some exception needs to be thrown either at creation time or at save() time.
		} 
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		
		repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		
		// Duplicate email
		try {
			repo.createPerson("different", "netid@email.com", "different", "different", RoleType.NONE).save();
		} catch (RuntimeException re) {
			// Some exception needs to be thrown either at creation time or at save() time.
		} 
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that users can be located based upon their roles.
	 */
	@Test
	public void findPersonsByRole() {
		
		// This test depends upon data created by the TestDataLoader.
		Person admin = repo.findPersonByEmail("bthornton@gmail.com");
		Person manager = repo.findPersonByEmail("mdriver@gmail.com");
		Person reviewer = repo.findPersonByEmail("jdimaggio@gmail.com");
		
		List<Person> persons = repo.findPersonsByRole(RoleType.REVIEWER);
		
		assertNotNull(persons);
		assertTrue(persons.contains(admin));
		assertTrue(persons.contains(manager));
		assertTrue(persons.contains(reviewer));
		assertEquals(3,persons.size());
		
		
		persons = repo.findPersonsByRole(RoleType.MANAGER);
		
		assertNotNull(persons);
		assertTrue(persons.contains(admin));
		assertTrue(persons.contains(manager));
		assertEquals(2,persons.size());
		
		persons = repo.findPersonsByRole(RoleType.ADMINISTRATOR);
		
		assertNotNull(persons);
		assertTrue(persons.contains(admin));
		assertEquals(1,persons.size());
		
	}
	
	/**
	 * Test searching for people
	 */
	@Test 
	public void searchPersons() {
		
		// This test depends upon data created by the TestDataLoader
		
		Person billy1 = repo.findPersonByEmail("bthornton@gmail.com");
		Person billy2 = repo.findPersonByEmail("bcrudup@gmail.com");

		
		// Search for billy which should match at least two records
		List<Person> results = repo.searchPersons("Billy", 0, 100);
		
		assertNotNull(results);
		assertTrue(results.size() >= 2);
		assertTrue(results.contains(billy1));
		assertTrue(results.contains(billy2));
		
		
		// Search for @ which matches everyone.
		List<Person> page1 = repo.searchPersons("@", 0, 2);
		assertNotNull(page1);
		assertEquals(2,page1.size());
		
		List<Person> page2 = repo.searchPersons("@",2,2);
		assertNotNull(page2);
		assertEquals(2,page2.size());
		assertFalse(page2.contains(page1.get(0)));
		assertFalse(page2.contains(page1.get(1)));
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
	 * Test retrieving count of all persons
	 */
	@Test
	public void testFindPersonsTotal() {
		
		assertEquals(repo.findAllPersons().size(),repo.findPersonsTotal());
	}
	
	/**
	 * Test that getDisplayName() and getCurrentEmailAddress() default to their
	 * alternative attributes when not defined.
	 */
	@Test
	public void testNameAndEmailDefaults() {
		
		Person person = repo.createPerson("netid", "email1@email.com", "first", "last", RoleType.NONE).save();

		assertEquals("first last",person.getDisplayName());
		assertEquals("email1@email.com",person.getCurrentEmailAddress());
		
		person.setDisplayName("changed");
		person.setCurrentEmailAddress("changed@email.com");
		
		assertEquals("changed",person.getDisplayName());
		assertEquals("changed@email.com",person.getCurrentEmailAddress());
		
		person.setDisplayName(null);
		person.setCurrentEmailAddress(null);
		
		assertEquals("first last",person.getDisplayName());
		assertEquals("email1@email.com",person.getCurrentEmailAddress());
		
		person.delete();
	}
	
	/**
	 * Test that netids are optional, but when pressent must be unique.
	 */
	@Test
	public void testNetIds() {
		
		Person person1 = repo.createPerson("netid", "email1@email.com", "first", "last", RoleType.NONE).save();
		Person person2 = repo.createPerson(null, "email2@email.com", "first", "last", RoleType.NONE).save();
		Person person3 = repo.createPerson(null, "email3@email.com", "first", "last", RoleType.NONE).save();
		Person person4 = repo.createPerson(null, "email4@email.com", "first", "last", RoleType.NONE).save();
		
		person1.delete();
		person2.delete();
		person3.delete();
		person4.delete();
	}
	
	
	/**
	 * Test validation of passwords.
	 */
	@Test
	public void testPasswords() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		
		// With no password set everything fails.
		assertFalse(person.validatePassword(null));
		assertFalse(person.validatePassword(""));
		assertFalse(person.validatePassword("something else"));
		assertFalse(person.validatePassword("password"));
		
		// Test setting the password.
		person.setPassword("password");
		assertFalse(person.validatePassword(null));
		assertFalse(person.validatePassword(""));
		assertFalse(person.validatePassword("something else"));
		assertTrue(person.validatePassword("password"));
		
		// Set the password to something else.
		person.setPassword("something else");
		assertFalse(person.validatePassword(null));
		assertFalse(person.validatePassword(""));
		assertTrue(person.validatePassword("something else"));
		assertFalse(person.validatePassword("password"));

		
		// Test setting the password back to null.
		person.setPassword(null);
		assertFalse(person.validatePassword(null));
		assertFalse(person.validatePassword(""));
		assertFalse(person.validatePassword("something else"));
		assertFalse(person.validatePassword("password"));

		person.delete();
	}
	
	
	/**
	 * Test that none of the required properties can be set to null or blank.
	 */
	@Test
	public void testPropertyValidation() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		person.save();
		
		// Email to null
		try {
			person.setEmail(null);
			fail("able to set email to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// first and last name to null
		try {
			person.setFirstName(null);
			person.setLastName(null);
			person.save();
			fail("able to set first and last name to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
	
		// Role to null
		try {
			person.setRole(null);
			fail("able to set role to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// Email to blank
		try {
			person.setEmail("");
			fail("able to set email to blank");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		// first and last name to blank
		try {
			person.setFirstName("");
			person.setLastName("");
			person.save();
			fail("able to set first and last name to blank");
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
	 * Test that we can support users with either a first or a last name.
	 */
	@Test
	public void testFirstOrLasteName() {
		
		// Create a person with no last name.
		Person hasFirst = repo.createPerson("netid-first", "first@email.com", "first", null, RoleType.NONE);
		hasFirst.save();
		
		// Create a person with no first name.
		Person hasLast = repo.createPerson("netid-last","last@email.com",null,"last", RoleType.NONE);
		hasLast.save();
		
		// Switch hasFirst to just a last name
		hasFirst.setFirstName(null);
		hasFirst.setLastName("last");
		hasFirst.save();
		
		// Switch hasLast to just a first name
		hasLast.setFirstName("first");
		hasLast.setLastName(null);
		hasLast.save();
		
		// Give them both
		hasFirst.setFirstName("first");
		hasFirst.setLastName("last");
		hasFirst.save();
		
		// Try and modify to no first or last name.
		hasLast.setFirstName(null);
		hasLast.setLastName(null);
		try {
			hasLast.save();
			fail("able to modify to having no first or last name");
		} catch (IllegalArgumentException iae) {
			// yay
		}
		
		hasFirst.delete();
		hasLast.delete();
		
	}
	
	
	/** 
	 * Test the fullName method.
	 */
	@Test
	public void testFullName() {
		Person person = repo.createPerson("netid", "email@email.com", "First", "Last", RoleType.NONE);
		person.setMiddleName("Middle");
		person.setBirthYear(1980);
	
		// Try with everything defined
		assertEquals("First Last", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("First Middle Last", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("First Middle Last 1980-", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("First Last 1980-", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("Last, First", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("Last, First Middle", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("Last, First Middle 1980-", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("Last, First 1980-", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));
		
		// Without a first name
		person.setFirstName(null);
		assertEquals("Last", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("Middle Last", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("Middle Last 1980-", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("Last 1980-", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("Last", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("Last, Middle", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("Last, Middle 1980-", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("Last 1980-", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));
		
		// Without a last name
		person.setFirstName("First");
		person.setLastName(null);
		assertEquals("First", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("First Middle", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("First Middle 1980-", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("First 1980-", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("First", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("First Middle", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("First Middle 1980-", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("First 1980-", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));

		// Without a middle name
		person.setLastName("Last");
		person.setMiddleName(null);
		assertEquals("First Last", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("First Last", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("First Last 1980-", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("First Last 1980-", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("Last, First", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("Last, First", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("Last, First 1980-", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("Last, First 1980-", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));
		
		// Without a birth year
		person.setMiddleName("Middle");
		person.setBirthYear(null);
		assertEquals("First Last", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("First Middle Last", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("First Middle Last", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("First Last", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("Last, First", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("Last, First Middle", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("Last, First Middle", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("Last, First", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));
		
		
		// With just a first name
		person.setFirstName("First");
		person.setMiddleName(null);
		person.setLastName(null);
		person.setBirthYear(null);
		assertEquals("First", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("First", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("First", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("First", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("First", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("First", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("First", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("First", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));
		
		// With just a last name
		person.setFirstName(null);
		person.setMiddleName(null);
		person.setLastName("Last");
		person.setBirthYear(null);
		assertEquals("Last", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("Last", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("Last", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("Last", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("Last", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("Last", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("Last", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("Last", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));
		
		// With just a first name and a birth year
		person.setFirstName("First");
		person.setMiddleName(null);
		person.setLastName(null);
		person.setBirthYear(1980);
		assertEquals("First", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("First", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("First 1980-", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("First 1980-", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("First", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("First", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("First 1980-", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("First 1980-", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));
		
		
		// With just a last name and birth year
		person.setFirstName(null);
		person.setMiddleName(null);
		person.setLastName("Last");
		person.setBirthYear(1980);
		assertEquals("Last", person.getFormattedName(NameFormat.FIRST_LAST));
		assertEquals("Last", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST));
		assertEquals("Last 1980-", person.getFormattedName(NameFormat.FIRST_MIDDLE_LAST_BIRTH));
		assertEquals("Last 1980-", person.getFormattedName(NameFormat.FIRST_LAST_BIRTH));
		assertEquals("Last", person.getFormattedName(NameFormat.LAST_FIRST));
		assertEquals("Last", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE));
		assertEquals("Last 1980-", person.getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH));
		assertEquals("Last 1980-", person.getFormattedName(NameFormat.LAST_FIRST_BIRTH));
		
		
		person.delete();
	}
	
	/**
	 * test that you can get/set all the parameters.
	 */
	@Test
	public void testProperties() {
		Person person = repo.createPerson("netid", "email@email.com", "other", "other", RoleType.NONE);
		person.save();
		
		person.setInstitutionalIdentifier("identifier");
		person.setFirstName("first");
		person.setMiddleName("middle");
		person.setLastName("last");
		person.setDisplayName("display");
		person.setBirthYear(1945);
		person.addAffiliation("affiliation1");
		person.addAffiliation("affiliation2");
		person.setCurrentPhoneNumber("currentPhone");
		person.setCurrentPostalAddress("currentPostal");
		person.setCurrentEmailAddress("currentEmail");
		person.setPermanentPhoneNumber("PermanentPhone");
		person.setPermanentPostalAddress("PermanentPostal");
		person.setPermanentEmailAddress("PermanentEmail");
		person.setCurrentDegree("degree");
		person.setCurrentDepartment("department");
		person.setCurrentCollege("college");
		person.setCurrentMajor("major");
		person.setCurrentGraduationYear(2005);
		person.setCurrentGraduationMonth(5);
		person.setRole(RoleType.ADMINISTRATOR);
		person.save();
		
		
		assertEquals("identifier",person.getInstitutionalIdentifier());
		assertEquals("first",person.getFirstName());
		assertEquals("middle",person.getMiddleName());
		assertEquals("last",person.getLastName());
		assertEquals("display",person.getDisplayName());
		assertEquals(Integer.valueOf(1945),person.getBirthYear());
		assertTrue(person.getAffiliations().contains("affiliation1"));
		assertTrue(person.getAffiliations().contains("affiliation2"));
		assertEquals("currentPhone",person.getCurrentPhoneNumber());
		assertEquals("currentPostal",person.getCurrentPostalAddress());
		assertEquals("currentEmail",person.getCurrentEmailAddress());
		assertEquals("PermanentPhone",person.getPermanentPhoneNumber());
		assertEquals("PermanentPostal",person.getPermanentPostalAddress());
		assertEquals("PermanentEmail",person.getPermanentEmailAddress());
		assertEquals("degree",person.getCurrentDegree());
		assertEquals("department",person.getCurrentDepartment());
		assertEquals("college",person.getCurrentCollege());
		assertEquals("major",person.getCurrentMajor());
		assertEquals(Integer.valueOf(2005),person.getCurrentGraduationYear());
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
		
		repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		
		Person otherPerson = repo.createPerson("other", "other@email.com", "other", "other", RoleType.NONE);
		otherPerson.save();
		
		try {
			otherPerson.setNetId("netid");
			otherPerson.save();
			fail("Able to create duplicate netids.");
		} catch (RuntimeException re) { /* yay */ }
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
		
		repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		
		otherPerson = repo.createPerson("other", "other@email.com", "other", "other", RoleType.NONE);
		otherPerson.save();
		
		try {
			otherPerson.setEmail("email@email.com");
			otherPerson.save();
			fail("Able to create duplicate emails.");
		} catch (RuntimeException re) { /* yay */ }
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
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
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that access to update a person is restricted.
	 */
	@Test
	public void testAccess() {
		
		// Test that with authorizations turned off that we can create a new person object.
		context.logout();
		context.turnOffAuthorization();
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.STUDENT);
		person.save();
		context.restoreAuthorization();
		
		// That someone else is not able to modify the student object.
		try {
			context.login(MockPerson.getStudent());
			
			person.setLastName("Changed");
			person.setFirstName("Changed");
			person.save();
			fail("Able to modify someone elses person object");
				
		} catch (SecurityException se) {
			/* yay */
		}
		
		// VIREO-133: Test tha reviewer can update student's email
		context.login(MockPerson.getReviewer());
		person.setEmail("changed@email.com");
		person.setLastName("Changed");
		person.setFirstName("Changed");
		person.save();
		
		// Test that an administrator is able to modify the student object.
		context.login(MockPerson.getAdministrator());
		person.setLastName("Changed");
		person.setFirstName("Changed");
		person.save();
		
		// Test that a manager is able to upgrade the student to a reviwer.
		context.login(MockPerson.getManager());
		person.setRole(RoleType.REVIEWER);
		person.save();
		person.setRole(RoleType.MANAGER);
		person.save();
		
		// but not administrator.
		try {
			person.setRole(RoleType.REVIEWER);
			person.save();
		} catch (SecurityException se) {
			/* yay */
		}
		
		context.login(MockPerson.getAdministrator());
		person.delete();
		context.logout();
	}
	
}
