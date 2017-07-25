package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the personal preference class.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaPreferenceImplTests extends UnitTest {

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
	 * Test creating a preference
	 */
	@Test
	public void testCreatePreference() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		Preference preference = person.addPreference("pref", "value");
		
		assertEquals("pref", preference.getName());
		assertEquals("value", preference.getValue());
		
		person.delete();
	}
	
	/**
	 * Test creating a preference with bad values.
	 */
	@Test
	public void testBadCreatePreference() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		
		// Blank name
		try {
			person.addPreference("", "value");
			fail("able to create preference with blank name");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		// Null name
		try {
			person.addPreference(null, "value");
			fail("able to create preference with blank name");
		} catch (IllegalArgumentException iae) {/* yay */}
		
		
		// But null or blank values should work.
		Preference pref1 = person.addPreference("null", null);
		Preference pref2 = person.addPreference("blank", "");
		
		assertEquals("null",pref1.getName());
		assertEquals(null,pref1.getValue());
		assertEquals("blank",pref2.getName());
		assertEquals("",pref2.getValue());
		
		person.delete();
	}
	
	/**
	 * Test weather you can create a duplicate name.
	 */
	@Test
	public void testCreateDuplicate() {
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();

		person.addPreference("pref", "value").save();

		try {
			person.addPreference("pref", "other").save();
			fail("able to create duplicate preferences");
		} catch (RuntimeException re) { /* */ }
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that unique ids are assigned.
	 */
	@Test
	public void testId() {
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		
		Preference pref = person.addPreference("pref","value");
		pref.save();
		
		assertNotNull(pref.getId());
		
		person.delete();
	}
	
	/**
	 * Test retrieving preferences by id.
	 */
	@Test
	public void testFindPreferenceById() {
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();

		Preference pref = person.addPreference("pref","value");
		pref.save();
		
		Preference retrieved = repo.findPreference(pref.getId());
		assertEquals(pref.getId(),retrieved.getId());
		assertEquals("pref",retrieved.getName());
		assertEquals("value", retrieved.getValue());
		
		person.delete();
	}
	
	/**
	 * Test retrieving preferences by name.
	 */
	@Test
	public void testFindPreferenceByName() {
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Preference pref = person.addPreference("pref","value");
		pref.save();
		
		Preference retrieved = person.getPreference("pref");
		assertEquals(pref.getId(),retrieved.getId());
		assertEquals("pref",retrieved.getName());
		assertEquals("value", retrieved.getValue());
		
		person.delete();
	}
	
	/**
	 * Test retrieving preferences by person
	 */
	@Test
	public void testFindPreferenceByPerson() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();

		Preference pref1 = person.addPreference("pref1","value1").save();
		Preference pref2 = person.addPreference("pref2","value2").save();
		Preference pref3 = person.addPreference("pref3","value3").save();
		Preference pref4 = person.addPreference("pref4","value4").save();
		
		List<Preference> preferences = person.getPreferences();
		
		assertEquals(4, preferences.size());
		
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		boolean found4 = false;
		
		for (Preference pref : preferences) {
			if (pref.getId() == pref1.getId())
				found1 = true;
			else if (pref.getId() == pref2.getId())
				found2 = true;
			else if (pref.getId() == pref3.getId())
				found3 = true;
			else if (pref.getId() == pref4.getId())
				found4 = true;
			else 
				fail("unexpectidly found an additional preference.");
		}
		
		assertTrue(found1);
		assertTrue(found2);
		assertTrue(found3);
		assertTrue(found4);
		
		person.delete();
	}
	
	/**
	 * test that properties are validated.
	 */
	@Test
	public void testPropertyValidation() {
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Preference pref = person.addPreference("pref","value");
		pref.setName("changedName");
		pref.setValue("changedValue");
		pref.save();
		
		assertEquals("changedName",pref.getName());
		assertEquals("changedValue", pref.getValue());
		
		try {
			pref.setName(null);
			fail("able to set preference name to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		try {
			pref.setName("");
			fail("able to set preference name to null");
		} catch (IllegalArgumentException iae) { /* yay */ }
		
		person.delete();
	}
	
	/**
	 * Test that a property may not be modified into being duplicate.
	 */
	@Test
	public void testModifyIntoDuplicate() {
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Preference pref = person.addPreference("pref","value").save();
		Preference other = person.addPreference("other","other").save();
		
		try {
			other.setName("pref");
			other.save();
			fail("Able to create duplicate preference");
		} catch (RuntimeException re) { /* */ }
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that preferences are persistent.
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
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Preference pref = person.addPreference("pref","value").save();
		Preference other = person.addPreference("other","other").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		

		pref = repo.findPreference(pref.getId());
		other = repo.findPreference(other.getId());
		
		assertNotNull(pref);
		assertNotNull(other);
		assertEquals("pref",pref.getName());
		assertEquals("value",pref.getValue());
		assertEquals("other",other.getName());
		assertEquals("other",other.getValue());
		
		pref.delete();
		other.delete();
		repo.findPerson(person.getId()).delete();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that deletes cascade
	 */
	@Test
	public void testCascadingDelete() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Preference pref = person.addPreference("pref","value").save();
		Preference other = person.addPreference("other","other").save();
		
		person.delete();
		
		assertNull(repo.findPreference(pref.getId()));
		assertNull(repo.findPreference(other.getId()));
		assertNull(repo.findPerson(person.getId()));
	}
	
	/**
	 * Test that saves cascade
	 */
	@Test
	public void testCascadingSaves() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE);
		Preference pref = person.addPreference("pref","value");
		Preference other = person.addPreference("other","other");
		
		person.save();
		
		assertNotNull(pref.getId());
		assertNotNull(other.getId());
		
		person.delete();
	}
	
	/**
	 * Test that two different people can have the same preference name
	 */
	@Test
	public void testSharingPreferenceNames() {
		
		Person person1 = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Preference pref1 = person1.addPreference("pref", "value").save();
		
		Person person2 = repo.createPerson("other", "other@email.com", "other", "other", RoleType.NONE).save();
		Preference pref2 = person2.addPreference("pref", "other").save();
		
		assertNotNull(pref1);
		assertNotNull(pref2);
		assertEquals("value",pref1.getValue());
		assertEquals("other",pref2.getValue());
		
		person1.delete();
		person2.delete();
	}
	
	/**
	 * Test deleting a preference
	 */
	@Test
	public void testDeletingPreference() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Preference pref1 = person.addPreference("pref1", "value").save();
		Preference pref2 = person.addPreference("pref2", "value").save();
		
		
		person = person.refresh();
		pref2.delete();
		
		assertEquals(1,person.getPreferences().size());
		assertEquals(pref1.getId(),person.getPreferences().iterator().next().getId());
		
		person.delete();
	}
	
	@Test
	public void testAccess() {
		
		Person person = repo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		context.login(person);
		Preference pref1 = person.addPreference("pref1", "value").save();
		
		try {
			context.login(MockPerson.getStudent());
			person.addPreference("pref1", "value").save();
			fail("A non-administrator was able to add a preference to someone else person.");
		} catch (SecurityException se) { 
			/* yay */
		}
		
		context.login(MockPerson.getAdministrator());
		person.delete();
	}
	
}
