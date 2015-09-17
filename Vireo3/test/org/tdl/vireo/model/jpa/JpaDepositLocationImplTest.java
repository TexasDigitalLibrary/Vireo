package org.tdl.vireo.model.jpa;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the deposit location interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaDepositLocationImplTest extends UnitTest {
	
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
	 * Test creating an deposit location
	 */
	@Test
	public void testCreate() {
		
		DepositLocation location = settingRepo.createDepositLocation("location");
				
		assertNotNull(location);
		assertEquals("location",location.getName());
		
		location.delete();
	}
	
	/**
	 * Test creating the location without a name
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createDepositLocation(null);
			fail("Able to create null deposit location");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createDepositLocation("");
			fail("Able to create blank deposit location");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate deposit location
	 */
	@Test
	public void testCreateDuplicate() {
		
		settingRepo.createDepositLocation("location").save();
		
		try {
			settingRepo.createDepositLocation("location").save();
			fail("Able to create duplicate deposit location");
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
		
		DepositLocation location = settingRepo.createDepositLocation("location").save();

		assertNotNull(location.getId());
		
		location.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		DepositLocation location = settingRepo.createDepositLocation("location").save();

		
		DepositLocation retrieved = settingRepo.findDepositLocation(location.getId());
		
		assertEquals(location.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieval by name.
	 */
	@Test
	public void testFindByName() {
		DepositLocation location = settingRepo.createDepositLocation("location").save();

		
		DepositLocation retrieved = settingRepo.findDepositLocationByName("location");
		
		assertEquals(location.getId(), retrieved.getId());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all locations
	 */
	@Test
	public void testFindAllLocations() {

		int initialSize = settingRepo.findAllDepositLocations().size();
		
		DepositLocation location1 = settingRepo.createDepositLocation("location1").save();
		DepositLocation location2 = settingRepo.createDepositLocation("location2").save();

		int postSize = settingRepo.findAllDepositLocations().size();
		
		assertEquals(initialSize +2, postSize);
		
		location1.delete();
		location2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		DepositLocation location = settingRepo.createDepositLocation("location").save();
		DepositLocation test = settingRepo.createDepositLocation("test").save();
		
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
			test.setName("location");
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
		
		DepositLocation location4 = settingRepo.createDepositLocation("location4").save();
		DepositLocation location1 = settingRepo.createDepositLocation("location1").save();
		DepositLocation location3 = settingRepo.createDepositLocation("location3").save();
		DepositLocation location2 = settingRepo.createDepositLocation("location2").save();
		
		location1.setDisplayOrder(0);
		location2.setDisplayOrder(1);
		location3.setDisplayOrder(3);
		location4.setDisplayOrder(4);
		
		location1.save();
		location2.save();
		location3.save();
		location4.save();
		
		List<DepositLocation> locations = settingRepo.findAllDepositLocations();
		
		int index1 = locations.indexOf(location1);
		int index2 = locations.indexOf(location2);
		int index3 = locations.indexOf(location3);
		int index4 = locations.indexOf(location4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		location1.delete();
		location2.delete();
		location3.delete();
		location4.delete();
	}
	
	/**
	 * Test that the location is persistence
	 */
	@Test
	public void testPersistance() throws MalformedURLException {
		// Commit and reopen a new transaction because some of the other tests
		// may have caused exceptions which set the transaction to be rolled
		// back.
		if (JPA.em().getTransaction().getRollbackOnly())
			JPA.em().getTransaction().rollback();
		else
			JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		DepositLocation location = settingRepo.createDepositLocation("location");
		location.setRepository("http://example.com/");
		location.setCollection("http://example.com/collection");
		location.setUsername("username");
		location.setPassword("password");
		location.setOnBehalfOf("onBehalfOf");
		location.setPackager(null);
		location.setDepositor(null);
		location.save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		DepositLocation retrieved = settingRepo.findDepositLocation(location.getId());
		
		assertEquals(location.getId(),retrieved.getId());
		assertEquals(location.getName(),retrieved.getName());
		assertEquals(location.getRepository(),retrieved.getRepository());
		assertEquals(location.getCollection(),retrieved.getCollection());
		assertEquals(location.getUsername(),retrieved.getUsername());
		assertEquals(location.getPassword(),retrieved.getPassword());
		assertEquals(location.getOnBehalfOf(),retrieved.getOnBehalfOf());
		assertEquals(location.getPackager(),retrieved.getPackager());
		assertEquals(location.getDepositor(),retrieved.getDepositor());

		
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
		DepositLocation location = settingRepo.createDepositLocation("location").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createDepositLocation("location").save().delete();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
