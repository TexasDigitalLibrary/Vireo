package org.tdl.vireo.model.jpa;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the email template interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaEmailTemplateImplTest extends UnitTest {
	
	// Repositories
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static JpaSettingsRepositoryImpl settingRepo = Spring.getBeanOfType(JpaSettingsRepositoryImpl.class);
	
	@Before
	public void setup() {
		context.login(MockPerson.getAdministrator());
	}
	
	@After
	public void cleanup() {
		context.logout();
	}
	
	/**
	 * Test creating a template
	 */
	@Test
	public void testCreate() {
		
		EmailTemplate template = settingRepo.createEmailTemplate("subject", "body");
		
		assertNotNull(template);
		assertEquals("subject",template.getSubject());
		assertEquals("body",template.getMessage());

		template.delete();
	}
	
	/**
	 * Test creating the template without required parameters
	 */
	@Test
	public void testBadCreate() {
		
		try {
			settingRepo.createEmailTemplate(null,"body");
			fail("Able to create null subject");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createEmailTemplate("","body");
			fail("Able to create blank subject");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createEmailTemplate("subject",null);
			fail("Able to create null message");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createEmailTemplate("subject","");
			fail("Able to create blank message");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		
	}
	
	/**
	 * Test creating a duplicate template
	 */
	@Test
	public void testCreateDuplicate() {
		
		EmailTemplate template = settingRepo.createEmailTemplate("subject","body").save();
		
		try {
			settingRepo.createEmailTemplate("subject","other body").save();
			fail("Able to create duplicate email template");
		} catch (RuntimeException re) {
			/* yay */
		}
		JPA.em().clear();
		settingRepo.findEmailTemplate(template.getId()).delete();
	}
	
	/**
	 * Test the id.
	 */
	@Test
	public void testId() {
		
		EmailTemplate template = settingRepo.createEmailTemplate("subject","body").save();

		assertNotNull(template.getId());
		
		template.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		EmailTemplate template = settingRepo.createEmailTemplate("subject","body").save();

		
		EmailTemplate retrieved = settingRepo.findEmailTemplate(template.getId());
		
		assertEquals(template.getSubject(),retrieved.getSubject());
		assertEquals(template.getMessage(),retrieved.getMessage());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all templates
	 */
	@Test
	public void testFindAllColleges() {

		int initialSize = settingRepo.findAllEmailTemplates().size();
		
		EmailTemplate template1 = settingRepo.createEmailTemplate("subject1","body").save();
		EmailTemplate template2 = settingRepo.createEmailTemplate("subject2","body").save();

		int postSize = settingRepo.findAllEmailTemplates().size();
		
		assertEquals(initialSize +2, postSize);
		
		template1.delete();
		template2.delete();
	}
	
	/**
	 * Test the validation when modifying properties
	 */
	@Test 
	public void testModifyingValidation() {
		EmailTemplate template = settingRepo.createEmailTemplate("subject","body").save();
		EmailTemplate test = settingRepo.createEmailTemplate("test","body").save();
		
		try {
			test.setSubject(null);
			fail("Able to set subject to null");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setSubject("");
			fail("Able to set subject to blank");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setMessage(null);
			fail("Able to set message to null");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			test.setMessage("");
			fail("Able to set message to blank");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		
		try {
			test.setSubject("subject");
			test.save();
			fail("Able to modify object into duplicate.");
		} catch(RuntimeException re) {
			/* yay */
		}
		
		JPA.em().clear();
		settingRepo.findEmailTemplate(test.getId()).delete();
		settingRepo.findEmailTemplate(template.getId()).delete();
	}
	
	/**
	 * Test the display order attribute.
	 */
	@Test
	public void testOrder() {
		EmailTemplate template4 = settingRepo.createEmailTemplate("subject4","body").save();
		EmailTemplate template1 = settingRepo.createEmailTemplate("subject1","body").save();
		EmailTemplate template3 = settingRepo.createEmailTemplate("subject3","body").save();
		EmailTemplate template2 = settingRepo.createEmailTemplate("subject2","body").save();
		
		template1.setDisplayOrder(0);
		template2.setDisplayOrder(1);
		template3.setDisplayOrder(3);
		template4.setDisplayOrder(4);
		
		template1.save();
		template2.save();
		template3.save();
		template4.save();
		
		List<EmailTemplate> templates = settingRepo.findAllEmailTemplates();
		
		int index1 = templates.indexOf(template1);
		int index2 = templates.indexOf(template2);
		int index3 = templates.indexOf(template3);
		int index4 = templates.indexOf(template4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		template1.delete();
		template2.delete();
		template3.delete();
		template4.delete();
	}
	
	/**
	 * Test that the email template is persistence
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
		
		EmailTemplate template = settingRepo.createEmailTemplate("subject","body").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		EmailTemplate retrieved = settingRepo.findEmailTemplate(template.getId());
		
		assertEquals(template.getId(),retrieved.getId());
		assertEquals(template.getSubject(),retrieved.getSubject());
		assertEquals(template.getMessage(),retrieved.getMessage());
		
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
		settingRepo.createEmailTemplate("subject","body").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createEmailTemplate("subject","body").save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
