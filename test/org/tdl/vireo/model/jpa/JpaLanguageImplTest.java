package org.tdl.vireo.model.jpa;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Jpa specific implementation of the language interface
 * 
 * @author Micah Cooper
 */
public class JpaLanguageImplTest extends UnitTest {
	
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
	 * Test creating an language
	 */
	@Test
	public void testCreate() {
		
		Language language = settingRepo.createLanguage("de");
		
		assertNotNull(language);
		assertEquals("de",language.getName());
		
		language.delete();
	}
	
	/**
	 * Test creating the language without required parameters
	 */
	@Test
	public void testBadCreate() {
		try {
			settingRepo.createLanguage(null);
			fail("Able to create null language");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
		
		try {
			settingRepo.createLanguage("");
			fail("Able to create blank language");
		} catch (IllegalArgumentException iae) {
			/* yay */
		}
	}
	
	/**
	 * Test creating a duplicate language
	 */
	@Test
	public void testCreateDuplicate() {
		
		settingRepo.createLanguage("de").save();
		
		try {
			settingRepo.createLanguage("de").save();
			fail("Able to create duplicate language");
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
		
		Language language = settingRepo.createLanguage("de").save();

		assertNotNull(language.getId());
		
		language.delete();
	}
	
	/**
	 * Test retrieval by id.
	 */
	@Test
	public void testFindById() {
		Language language = settingRepo.createLanguage("de").save();

		
		Language retrieved = settingRepo.findLanguage(language.getId());
		
		assertEquals(language.getName(), retrieved.getName());
		
		retrieved.delete();
	}
	
	/**
	 * Test retrieving all languages
	 */
	@Test
	public void testFindAllLanguages() {

		int initialSize = settingRepo.findAllLanguages().size();
		
		Language language1 = settingRepo.createLanguage("hu").save();
		Language language2 = settingRepo.createLanguage("de").save();

		int postSize = settingRepo.findAllLanguages().size();
		
		assertEquals(initialSize +2, postSize);
		
		language1.delete();
		language2.delete();
	}
	
	/**
	 * Test the validation when modifying the name
	 */
	@Test 
	public void testNameValidation() {
		Language language = settingRepo.createLanguage("de").save();
		Language test = settingRepo.createLanguage("hu").save();
		
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
			test.setName("de");
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
	 * Test converting into a locale
	 */
	@Test
	public void testGetLocale() {
		
		
		Language lang1 = settingRepo.createLanguage("de").save();
		Language lang2 = settingRepo.createLanguage("de_CH").save();
		
		Locale german = LocaleUtils.toLocale("de");
		Locale germanAustria = LocaleUtils.toLocale("de_CH");
		
		assertEquals(german, lang1.getLocale());
		assertEquals(germanAustria, lang2.getLocale());
	}
	
	/**
	 * Test the display order attribute.
	 */
	@Test
	public void testOrder() {
		Language language4 = settingRepo.createLanguage("aa").save();
		Language language1 = settingRepo.createLanguage("ab").save();
		Language language3 = settingRepo.createLanguage("af").save();
		Language language2 = settingRepo.createLanguage("ak").save();
		
		language1.setDisplayOrder(0);
		language2.setDisplayOrder(1);
		language3.setDisplayOrder(3);
		language4.setDisplayOrder(4);
		
		language1.save();
		language2.save();
		language3.save();
		language4.save();
		
		List<Language> languages = settingRepo.findAllLanguages();
		
		int index1 = languages.indexOf(language1);
		int index2 = languages.indexOf(language2);
		int index3 = languages.indexOf(language3);
		int index4 = languages.indexOf(language4);
		
		assertTrue(index4 > index3);
		assertTrue(index3 > index2);
		assertTrue(index2 > index1);

		language1.delete();
		language2.delete();
		language3.delete();
		language4.delete();
	}
	
	/**
	 * Test that the language is persistence
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
		
		Language language = settingRepo.createLanguage("de").save();
		
		// Commit and reopen a new transaction.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		Language retrieved = settingRepo.findLanguage(language.getId());
		
		assertEquals(language.getId(),retrieved.getId());
		assertEquals(language.getName(),retrieved.getName());
		
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
		settingRepo.createLanguage("de").save().delete();
		
		try {
			context.login(MockPerson.getReviewer());
			settingRepo.createLanguage("hu").save();
			fail("A reviewer was able to create a new object.");
		} catch (SecurityException se) {
			/* yay */
		}
		context.logout();
	}
	
}
