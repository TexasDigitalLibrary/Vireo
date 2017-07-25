/**
 * 
 */
package org.tdl.vireo.services;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.EmbargoType;
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
		mockSettingsRepository = new MockSettingsRepository();
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
	 * Test that the System data is loaded to a mock repository.
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
	 * Test that Sysetm email templates over write existing version.
	 */
	@Test
	public void testOverwritingSystemEmailTemplate() {
		// Create the template.

		EmailTemplate template = settingRepo.createEmailTemplate("SYSTEM Email Test", "subject", "message").save();
		assertEquals("SYSTEM Email Test", template.getName());
		assertEquals("subject", template.getSubject());
		assertEquals("message", template.getMessage());
		assertFalse(template.isSystemRequired());

		// Generate it and check that it replaces the data.
		template = systemDataLoader.loadSystemEmailTemplate("SYSTEM Email Test");
		template.save();

		assertEquals("SYSTEM Email Test", template.getName());
		assertEquals("Vireo Email Test", template.getSubject());
		assertTrue(template.isSystemRequired());
		assertTrue(template.getMessage().contains("A Vireo system administrator has sent this email address a test email. You may"));
		assertTrue(template.getMessage().contains("If you need assistance with your account, please email"));
		assertTrue(template.getMessage().contains("The Vireo Team"));
	}

	/**
	 * Test that the create user page loads.
	 */
	@Test
	public void testSystemDataUpgrade() {
		mockSettingsRepository = new MockSettingsRepository();
		Application.settingRepo = mockSettingsRepository;
		systemDataLoader.setSettingsRepository(mockSettingsRepository);

		// Create the template.
		EmailTemplate oldTemplate = mockSettingsRepository.createEmailTemplate("SYSTEM Email Test", "subject", "message").save();
		assertEquals("SYSTEM Email Test", oldTemplate.getName());
		assertEquals("subject", oldTemplate.getSubject());
		assertEquals("message", oldTemplate.getMessage());
		assertFalse(oldTemplate.isSystemRequired());

		// load all system templates, force upgdate/upgrade of data
		systemDataLoader.generateAllSystemEmailTemplates();

		// should give us our custom non-system one (after data upgrade)
		EmailTemplate customTemplate = mockSettingsRepository.findEmailTemplateByName("SYSTEM Email Test");
		assertEquals("SYSTEM Email Test", customTemplate.getName());
		assertEquals("subject", customTemplate.getSubject());
		assertEquals("message", customTemplate.getMessage());
		assertFalse(customTemplate.isSystemRequired());
		// make sure our Id's still match
		assertEquals(oldTemplate.getId(), customTemplate.getId());

		// should give us our system one (after data upgrade)
		EmailTemplate newSystemTemplate = mockSettingsRepository.findSystemEmailTemplateByName("SYSTEM Email Test");
		assertEquals("SYSTEM Email Test", newSystemTemplate.getName());
		assertEquals("Vireo Email Test", newSystemTemplate.getSubject());
		assertTrue(newSystemTemplate.isSystemRequired());
		assertTrue(newSystemTemplate.getMessage().contains("A Vireo system administrator has sent this email address a test email. You may"));
		assertTrue(newSystemTemplate.getMessage().contains("If you need assistance with your account, please email"));
		assertTrue(newSystemTemplate.getMessage().contains("The Vireo Team"));

		// Create the embargo.
		EmbargoType oldEmbargo = mockSettingsRepository.createEmbargoType("None", "Nonya", 60, false, EmbargoGuarantor.DEFAULT).save();
		assertEquals("None", oldEmbargo.getName());
		assertEquals("Nonya", oldEmbargo.getDescription());
		assertEquals((Integer)(60), oldEmbargo.getDuration());
		assertFalse(oldTemplate.isSystemRequired());

		// load all system templates, force upgdate/upgrade of data
		systemDataLoader.generateAllSystemEmbargos();

		// should give us our custom non-system one (after data upgrade)
		EmbargoType customEmbargo = mockSettingsRepository.findNonSystemEmbargoTypeByNameAndGuarantor("None", EmbargoGuarantor.DEFAULT);
		assertEquals("None", customEmbargo.getName());
		assertEquals("Nonya", customEmbargo.getDescription());
		assertEquals((Integer)(60), customEmbargo.getDuration());
		assertFalse(customEmbargo.isSystemRequired());
		// make sure our Id's still match
		assertEquals(oldEmbargo.getId(), customEmbargo.getId());

		// should give us our system one (after data upgrade)
		EmbargoType newSystemEmbargo = mockSettingsRepository.findSystemEmbargoTypeByNameAndGuarantor("None", EmbargoGuarantor.DEFAULT);
		assertEquals("None", newSystemEmbargo.getName());
		assertEquals("The work will be published after approval.", newSystemEmbargo.getDescription());
		assertEquals((Integer)(0), newSystemEmbargo.getDuration());
		assertTrue(newSystemEmbargo.isSystemRequired());

		systemDataLoader.setSettingsRepository(settingRepo);
	}
}
