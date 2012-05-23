package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.Test;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.Configuration;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the configuration interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaConfigurationImplTest extends UnitTest {
	
	JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	
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
		JPA.em().clear();
		settingRepo.findConfiguration(config.getId()).delete();
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
		
		JPA.em().clear();
		settingRepo.findConfiguration(test.getId()).delete();
		settingRepo.findConfiguration(config.getId()).delete();
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
	
}
