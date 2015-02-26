/**
 * 
 */
package org.tdl.vireo.services;

import org.junit.Test;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.jpa.MockPersonRepository;
import org.tdl.vireo.model.jpa.MockSettingsRepository;

import play.modules.spring.Spring;
import controllers.AbstractVireoFunctionalTest;
import controllers.Application;
import controllers.FirstUser;

/**
 * @author gad
 *
 */
public class SystemDataLoaderTest extends AbstractVireoFunctionalTest {
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);

	/**
	 * Test that the create user page loads.
	 */
	@Test
	public void testSystemDataLoad() {
		MockSettingsRepository mockSettingsRepository = new MockSettingsRepository();
		Application.settingRepo = mockSettingsRepository;
		
		// these usually get called from @OnApplicationStart on the real DB, not on the Mock one.
		SystemDataLoader systemDataLoader = Spring.getBeanOfType(SystemDataLoader.class);
		systemDataLoader.setSettingsRepository(mockSettingsRepository);
		
		// generate system embargos
		systemDataLoader.generateAllSystemEmbargos();
		// generate system email templates
		systemDataLoader.generateAllSystemEmailTemplates();
		// generate system email workflow rules
		systemDataLoader.generateAllSystemEmailRules();
		
		systemDataLoader.setSettingsRepository(settingRepo);

		// Test that the system values were created.
		assertEquals(9, mockSettingsRepository.findAllEmbargoTypes().size());
		assertEquals(7, mockSettingsRepository.findAllEmailTemplates().size());
		assertEquals(2, mockSettingsRepository.findAllEmailWorkflowRules().size());
	}

	/**
	 * Test that the create user page loads.
	 */
	@Test
	public void testSystemDataUpgrade() {

	}
}
