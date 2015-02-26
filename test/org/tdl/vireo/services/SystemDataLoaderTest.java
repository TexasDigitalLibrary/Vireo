/**
 * 
 */
package org.tdl.vireo.services;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.jpa.MockSettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.modules.spring.Spring;
import controllers.AbstractVireoFunctionalTest;
import controllers.Application;

/**
 * @author gad
 *
 */
public class SystemDataLoaderTest extends AbstractVireoFunctionalTest {
	private static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	private static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	private static SystemDataLoader systemDataLoader = Spring.getBeanOfType(SystemDataLoader.class);
	private static MockSettingsRepository mockSettingsRepository = new MockSettingsRepository();

	@Before
	public void setup() {
		context.turnOffAuthorization();
		Application.settingRepo = mockSettingsRepository;

		// these usually get called by @OnApplicationStart classes against real DB, not on the Mock one.
		SystemDataLoader systemDataLoader = Spring.getBeanOfType(SystemDataLoader.class);
		systemDataLoader.setSettingsRepository(mockSettingsRepository);

		// generate system embargos
		systemDataLoader.generateAllSystemEmbargos();
		// generate system email templates
		systemDataLoader.generateAllSystemEmailTemplates();
		// generate system email workflow rules
		systemDataLoader.generateAllSystemEmailRules();
	}

	@After
	public void cleanup() {
		context.restoreAuthorization();
		systemDataLoader.setSettingsRepository(settingRepo);
	}

	/**
	 * Test getting a list of all the template names.
	 */
	@Test
	public void testGetTemplateNames() {

		List<String> names = systemDataLoader.getAllSystemEmailTemplateNames();

		assertNotNull(names);
		assertTrue(names.contains("SYSTEM Advisor Review Request"));
		assertTrue(names.contains("SYSTEM Deposit Notification"));
		assertTrue(names.contains("SYSTEM Email Test"));
		assertTrue(names.contains("SYSTEM Initial Submission"));
		assertTrue(names.contains("SYSTEM Needs Corrections"));
		assertTrue(names.contains("SYSTEM New User Registration"));
		assertTrue(names.contains("SYSTEM Verify Email Address"));

		// There may be more added in the future.
		assertTrue(names.size() >= 7);
	}

	/**
	 * Test that the create user page loads.
	 */
	@Test
	public void testSystemDataLoaded() {

		// Test that the system values were created.
		assertEquals(9, mockSettingsRepository.findAllEmbargoTypes().size());
		assertEquals(7, mockSettingsRepository.findAllEmailTemplates().size());
		assertEquals(2, mockSettingsRepository.findAllEmailWorkflowRules().size());

		EmailTemplate template = mockSettingsRepository.findEmailTemplateByName("SYSTEM New User Registration");
		assertEquals("SYSTEM New User Registration", template.getName());
		assertEquals("Vireo Account Registration", template.getSubject());
		assertTrue(template.getMessage().contains("To complete registration of your Vireo account, please click the link"));
		assertTrue(template.getMessage().contains("{REGISTRATION_URL}"));
		assertTrue(template.getMessage().contains("The Vireo Team"));

		template = mockSettingsRepository.findEmailTemplateByName("SYSTEM Verify Email Address");
		assertEquals("SYSTEM Verify Email Address", template.getName());
		assertEquals("Verify Email Address", template.getSubject());
		assertTrue(template.getMessage().contains("Please click on the link below to verify your email address with"));
		assertTrue(template.getMessage().contains("{REGISTRATION_URL}"));
		assertTrue(template.getMessage().contains("The Vireo Team"));
	}

	/**
	 * Test that email templates over write existing version.
	 */
	@Test
	public void testOverwritingEmailTemplate() {
		// Create the template.
		settingRepo.createEmailTemplate("SYSTEM New User Registration", "subject", "message").save();

		// Generate it and check that it replaces the data.
		EmailTemplate template = systemDataLoader.loadSystemEmailTemplate("SYSTEM New User Registration");
		template.save();

		assertEquals("SYSTEM New User Registration", template.getName());
		assertEquals("Vireo Account Registration", template.getSubject());
		assertTrue(template.isSystemRequired());
		assertTrue(template.getMessage().contains("To complete registration of your Vireo account, please click the link"));
		assertTrue(template.getMessage().contains("{REGISTRATION_URL}"));
		assertTrue(template.getMessage().contains("The Vireo Team"));
	}

	/**
	 * Test that the create user page loads.
	 */
	@Test
	public void testSystemDataUpgrade() {

	}
}
