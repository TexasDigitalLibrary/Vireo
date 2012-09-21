package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the document type interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaDocumentTypeImplTest extends UnitTest {
	
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
	 * Test creating an document type
	 */
	@Test
	public void testCreate() {
		
		DocumentType type = settingRepo.createDocumentType("type",DegreeLevel.DOCTORAL);
		
		assertNotNull(type);
		assertEquals("type",type.getName());
		assertEquals(DegreeLevel.DOCTORAL,type.getLevel());
		
		type.delete();
	}
	
	/**
	 * Test creating the document type without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createDocumentType(null,DegreeLevel.MASTERS);
			fail("Able to create null document type");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createDocumentType("", DegreeLevel.UNDERGRADUATE);
			fail("Able to create blank document type");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createDocumentType("type", null);
			fail("Able to create document type with a null level");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate document type
	 */
	@Test
	public void testCreateDuplicate() {
		
		DocumentType type = settingRepo.createDocumentType("type",DegreeLevel.UNDERGRADUATE).save();
		
		try {
			settingRepo.createDocumentType("type",DegreeLevel.UNDERGRADUATE).save();
			fail("Able to create duplicate type");
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
		
		DocumentType type = settingRepo.createDocumentType("type",DegreeLevel.MASTERS).save();

		assertNotNull(type.getId());
		
		type.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		DocumentType type = settingRepo.createDocumentType("type",DegreeLevel.DOCTORAL).save();

		
		DocumentType retrieved = settingRepo.findDocumentType(type.getId());
		
		assertEquals(type.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all document types
	 */
	@Test
	public void testFindAllDocumentTypes() {

		int initialSize = settingRepo.findAllDocumentTypes().size();
		
		DocumentType type1 = settingRepo.createDocumentType("type1",DegreeLevel.MASTERS).save();
		DocumentType type2 = settingRepo.createDocumentType("type2",DegreeLevel.UNDERGRADUATE).save();

		int postSize = settingRepo.findAllDocumentTypes().size();
		
		assertEquals(initialSize +2, postSize);
		
		type1.delete();
		type2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		DocumentType type = settingRepo.createDocumentType("type",DegreeLevel.MASTERS).save();
		DocumentType test = settingRepo.createDocumentType("test",DegreeLevel.UNDERGRADUATE).save();
		
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
			test.setName("type");
			test.setLevel(DegreeLevel.MASTERS);
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
		DocumentType type4 = settingRepo.createDocumentType("type4",DegreeLevel.MASTERS).save();
		DocumentType type1 = settingRepo.createDocumentType("type1",DegreeLevel.MASTERS).save();
		DocumentType type3 = settingRepo.createDocumentType("type3",DegreeLevel.DOCTORAL).save();
		DocumentType type2 = settingRepo.createDocumentType("type2",DegreeLevel.MASTERS).save();
		
		type1.setDisplayOrder(0);
		type2.setDisplayOrder(1);
		type3.setDisplayOrder(3);
		type4.setDisplayOrder(4);
		
		type1.save();
		type2.save();
		type3.save();
		type4.save();
		
		List<DocumentType> types = settingRepo.findAllDocumentTypes();
		
		int index1 = types.indexOf(type1);
		int index2 = types.indexOf(type2);
		int index3 = types.indexOf(type3);
		int index4 = types.indexOf(type4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		type1.delete();
		type2.delete();
		type3.delete();
		type4.delete();
	}
	
	/**
	 * Test that the document type is persistence
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
		
		DocumentType type = settingRepo.createDocumentType("type",DegreeLevel.MASTERS).save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		DocumentType retrieved = settingRepo.findDocumentType(type.getId());
		
		assertEquals(type.getId(),retrieved.getId());
		assertEquals(type.getName(),retrieved.getName());
		assertEquals(type.getLevel(),retrieved.getLevel());
		
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
		settingRepo.createDocumentType("type",DegreeLevel.MASTERS).save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createDocumentType("type",DegreeLevel.MASTERS).save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
