package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.impl.LuceneIndexerImpl;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the custom action definition interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaCustomActionDefinitionImplTest extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaPersonRepositoryImpl personRepo = Spring.getBeanOfType(JpaPersonRepositoryImpl.class);
	public static JpaSubmissionRepositoryImpl subRepo = Spring.getBeanOfType(JpaSubmissionRepositoryImpl.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	public static LuceneIndexerImpl indexer = Spring.getBeanOfType(LuceneIndexerImpl.class);
	
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
	 * Test creating an custom action definition
	 */
	@Test
	public void testCreate() {
		
		CustomActionDefinition def = settingRepo.createCustomActionDefinition("label").save();
		
		assertNotNull(def);
		assertEquals("label",def.getLabel());
		
		def.delete();
	}
	
	/**
	 * Test creating the definition without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createCustomActionDefinition(null);
			fail("Able to create null label");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createCustomActionDefinition("");
			fail("Able to create blank label");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate definition
	 */
	@Test
	public void testCreateDuplicate() {
		
		CustomActionDefinition def = settingRepo.createCustomActionDefinition("label").save();
		
		try {
			settingRepo.createCustomActionDefinition("label").save();
			fail("Able to create duplicate custom action definition");
		} catch (RuntimeException re) {
			/* yay */
		}
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test the id.
	 */
	@Test
	public void testId() {
		
		CustomActionDefinition def = settingRepo.createCustomActionDefinition("label").save();

		assertNotNull(def.getId());
		
		def.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		CustomActionDefinition def = settingRepo.createCustomActionDefinition("label").save();

		
		CustomActionDefinition retrieved = settingRepo.findCustomActionDefinition(def.getId());
		
		assertEquals(def.getLabel(), retrieved.getLabel());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all definitions
	 */
	@Test
	public void testFindAllDefinitions() {

		int initialSize = settingRepo.findAllCustomActionDefinition().size();
		
		CustomActionDefinition def1 = settingRepo.createCustomActionDefinition("label1").save();
		CustomActionDefinition def2 = settingRepo.createCustomActionDefinition("label2").save();

		int postSize = settingRepo.findAllCustomActionDefinition().size();
		
		assertEquals(initialSize +2, postSize);
		
		def1.delete();
		def2.delete();
	}
	
	/**
	 * Test the validation when modifying the label
	 */
	@Test 
	public void testLabelValidation() {
		CustomActionDefinition def = settingRepo.createCustomActionDefinition("label").save();
		CustomActionDefinition test = settingRepo.createCustomActionDefinition("test").save();
		
		try {
			test.setLabel(null);
			fail("Able to change label to null");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setLabel("");
			fail("Able to change label to blank");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setLabel("label");
			test.save();
			fail("Able to modify object into duplicate.");
		} catch(RuntimeException re) {
			/* yay */
		}
		
		// Recover the transaction after a failure.
		JPA.em().getTransaction().rollback();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test the display order attribute.
	 */
	@Test
	public void testOrder() {
		
		CustomActionDefinition def4 = settingRepo.createCustomActionDefinition("label4").save();
		CustomActionDefinition def1 = settingRepo.createCustomActionDefinition("label1").save();
		CustomActionDefinition def3 = settingRepo.createCustomActionDefinition("label3").save();
		CustomActionDefinition def2 = settingRepo.createCustomActionDefinition("label2").save();
		
		def1.setDisplayOrder(0);
		def2.setDisplayOrder(1);
		def3.setDisplayOrder(3);
		def4.setDisplayOrder(4);
		
		def1.save();
		def2.save();
		def3.save();
		def4.save();
		
		List<CustomActionDefinition> definitions = settingRepo.findAllCustomActionDefinition();
		
		int index1 = definitions.indexOf(def1);
		int index2 = definitions.indexOf(def2);
		int index3 = definitions.indexOf(def3);
		int index4 = definitions.indexOf(def4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		def1.delete();
		def2.delete();
		def3.delete();
		def4.delete();
	}
	
	/**
	 * Test that the custom action definitions are persistent
	 */
	@Test
	public void testPersistance() {
		
		CustomActionDefinition def = settingRepo.createCustomActionDefinition("label").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		CustomActionDefinition retrieved = settingRepo.findCustomActionDefinition(def.getId());
		
		assertEquals(def.getId(),retrieved.getId());
		assertEquals(def.getLabel(),retrieved.getLabel());
		
		retrieved.delete();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that you can delete a custom action definition while there are still values asociated with them.
	 */
	@Test
	public void testDeletetion() {
		CustomActionDefinition def = settingRepo.createCustomActionDefinition("label").save();
		Person person = personRepo.createPerson("netid", "email@email.com", "first", "last", RoleType.NONE).save();
		Submission sub = subRepo.createSubmission(person);
		
		sub.addCustomAction(def, true);
		sub.save();
		
		// Clear out the indexer transaction.
		indexer.rollback();
		assertFalse(indexer.isUpdated(sub));

		
		def.delete();
		
		// Check that the submission was queued up in the indexer.
		assertTrue(indexer.isUpdated(sub));
		
		// check that the value associated with it was also deleted, once refreshed
		JPA.em().clear();
		sub = subRepo.findSubmission(sub.getId());
		
		assertNull(sub.getCustomAction(def));
		
		subRepo.findSubmission(sub.getId()).delete();
		personRepo.findPerson(person.getId()).delete();
	}
	
	/**
	 * Test that managers have access and other don't.
	 */
	@Test
	public void testAccess() {
		
		context.login(MockPerson.getManager());
		settingRepo.createCustomActionDefinition("label").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createCustomActionDefinition("label").save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
}
