package org.tdl.vireo.model.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the configuration interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaConfigurationImplTest extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	
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
	 * Test creating a configuration
	 */
	@Test
	public void testCreate() {
		
		Configuration config = settingRepo.createConfiguration("config","value");
		
		assertNotNull(config);
		assertEquals("config",config.getName());
		assertEquals("value",config.getValue());

		config.delete();
		
		// Test creating with null value
		
		config = settingRepo.createConfiguration("config",null);
		
		assertNotNull(config);
		assertEquals("config",config.getName());
		assertNull(config.getValue());

		config.delete();
	}
	
	/**
	 * Test creating the configuration without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createConfiguration(null,"value");
			fail("Able to create null configuration setting");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createConfiguration("","value");
			fail("Able to create blank configuration");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate config
	 */
	@Test
	public void testCreateDuplicate() {
		
		Configuration config = settingRepo.createConfiguration("config","value").save();
		
		try {
			settingRepo.createConfiguration("config","otherValue").save();
			fail("Able to create duplicate configuration");
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
		
		Configuration config = settingRepo.createConfiguration("config","value").save();

		assertNotNull(config.getId());
		
		config.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		Configuration config = settingRepo.createConfiguration("config","value").save();

		
		Configuration retrieved = settingRepo.findConfiguration(config.getId());
		
		assertEquals(config.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieval by name.
	 */
	@Test
	public void testFindByName() {
		Configuration config = settingRepo.createConfiguration("config","value").save();

		
		Configuration retrieved = settingRepo.findConfigurationByName("config");
		
		assertEquals(config.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieval of value by name.
	 */
	@Test
	public void testGetConfig() {
		Configuration config = settingRepo.createConfiguration("config","value").save();

		
		String retrieved = settingRepo.getConfigValue("config");
		
		assertEquals("value", retrieved);
		
		config.delete();
	}
	
	/**
	 * Test retrieval of value by name with default
	 */
	@Test
	public void testGetConfigWithDefault() {
		Configuration config = settingRepo.createConfiguration("config","value").save();

		// Search for one that exists.
		String retrieved = settingRepo.getConfigValue("config","default");
		assertEquals("value", retrieved);
		
		// Search for one that is missing.
		retrieved = settingRepo.getConfigValue("thisdoesnotexist","default");
		assertEquals("default",retrieved);
		
		config.delete();
	}
	
	/**
	 * Test retrieval of boolean value by name
	 */
	@Test
	public void testGetBooleanConfig() {
		Configuration config = settingRepo.createConfiguration("config","value").save();

		assertTrue(settingRepo.getConfigBoolean("config"));
		
		config.delete();
		
		assertFalse(settingRepo.getConfigBoolean("config"));
	}
	
	/**
	 * Test retrieving all configurations
	 */
	@Test
	public void testFindAllConfiguration() {

		int initialSize = settingRepo.findAllConfigurations().size();
		
		Configuration config1 = settingRepo.createConfiguration("config1","value").save();
		Configuration config2 = settingRepo.createConfiguration("config2","value").save();

		int postSize = settingRepo.findAllConfigurations().size();
		
		assertEquals(initialSize +2, postSize);
		
		config1.delete();
		config2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		Configuration config = settingRepo.createConfiguration("config","value").save();
		Configuration test = settingRepo.createConfiguration("test","value").save();
		
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
			test.setName("config");
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
	 * Test that the configuration is persistence
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
		
		Configuration config = settingRepo.createConfiguration("config","value").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Configuration retrieved = settingRepo.findConfiguration(config.getId());
		
		assertEquals(config.getId(),retrieved.getId());
		assertEquals(config.getName(),retrieved.getName());
		assertEquals(config.getValue(),retrieved.getValue());

		retrieved.delete();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Test that managers have access and other don't.
	 */
	@Test
	public void testAccess() {
		
		context.login(MockPerson.getManager());
		settingRepo.createConfiguration("config","value").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createConfiguration("config","value").save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
