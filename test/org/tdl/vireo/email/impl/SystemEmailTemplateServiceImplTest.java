package org.tdl.vireo.email.impl;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the system email template service implementation.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class SystemEmailTemplateServiceImplTest extends UnitTest {

	// Spring dependencies
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static SystemEmailTemplateServiceImpl systemEmailService = Spring.getBeanOfType(SystemEmailTemplateServiceImpl.class);
	
	
	/**
	 * Cleanup by deleting any preexisting email templates.
	 */
	@Before
	public void setup() {
		context.turnOffAuthorization();
		EmailTemplate template = settingRepo.findEmailTemplateByName("SYSTEM New User Registration");
		if (template != null) {
			template.setSystemRequired(false);
			template.delete();
		}
		
		template = settingRepo.findEmailTemplateByName("SYSTEM Verify Email Address");
		if (template != null) {
			template.setSystemRequired(false);
			template.delete();
		}
	}
	
	/**
	 * Restore all the email templates, and reset the context.
	 */
	@After
	public void cleanup() {
		
		systemEmailService.generateAllSystemEmailTemplates();	
		
		context.restoreAuthorization();
		context.logout(); // Resets the context incase of error.
	}
	
	/**
	 * Use the service to generate two email templates.
	 */
	@Test
	public void testGenerateSystemEmailTemplate() {

		// Check the two currently (at the time the test was written) email templates.
		
		// New User Registration
 		EmailTemplate template = systemEmailService.generateSystemEmailTemplate("SYSTEM New User Registration");
		
		assertEquals("SYSTEM New User Registration", template.getName());
		assertEquals("Vireo Account Registration",template.getSubject());
		assertTrue(template.getMessage().contains("To complete registration of your Vireo account, please click the link"));
		assertTrue(template.getMessage().contains("{REGISTRATION_URL}"));
		assertTrue(template.getMessage().contains("The Vireo Team"));

		template.setSystemRequired(false);
		template.delete();

		// Change Password Registration
		template = systemEmailService.generateSystemEmailTemplate("SYSTEM Verify Email Address");
		
		assertEquals("SYSTEM Verify Email Address", template.getName());
		assertEquals("Verify Email Address",template.getSubject());
		assertTrue(template.getMessage().contains("Please click on the link below to verify your email address with"));
		assertTrue(template.getMessage().contains("{REGISTRATION_URL}"));
		assertTrue(template.getMessage().contains("The Vireo Team"));

		template.setSystemRequired(false);
		template.delete();
	}
	
	/**
	 * Test that email templates over write existing version.
	 */
	@Test
	public void testOverwritingEmailTemplate() {
		
		// Create the template.
		settingRepo.createEmailTemplate("SYSTEM New User Registration", "subject", "message").save();

		// Generate it and check that it replaces the data.
		EmailTemplate template = systemEmailService.generateSystemEmailTemplate("SYSTEM New User Registration");
		
		assertEquals("SYSTEM New User Registration", template.getName());
		assertEquals("Vireo Account Registration",template.getSubject());
		assertTrue(template.isSystemRequired());
		assertTrue(template.getMessage().contains("To complete registration of your Vireo account, please click the link"));
		assertTrue(template.getMessage().contains("{REGISTRATION_URL}"));
		assertTrue(template.getMessage().contains("The Vireo Team"));

		template.setSystemRequired(false);
		template.delete();
	}
	
	/**
	 * Test getting a list of all the template names.
	 */
	@Test
	public void testGetTemplateNames() {
		
		List<String> names = systemEmailService.getAllSystemEmailTemplateNames();
		
		assertNotNull(names);
		assertTrue(names.contains("SYSTEM New User Registration"));
		assertTrue(names.contains("SYSTEM Verify Email Address"));
		assertTrue(names.contains("SYSTEM Email Test"));

		// There may be more added in the future.
		assertTrue(names.size() >= 3);

	}
	
	/**
	 * Test re-generating all the templates.
	 */
	@Test
	public void testCreateAllSystemEmailTemplates() {
		
		List<EmailTemplate> templates = systemEmailService.generateAllSystemEmailTemplates();
		
		assertNotNull(templates);
		assertTrue(templates.size() >= 2);
	}
	

	
}
