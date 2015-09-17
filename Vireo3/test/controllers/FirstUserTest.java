package controllers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.jpa.MockPersonRepository;
import org.tdl.vireo.model.jpa.MockSettingsRepository;

import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

public class FirstUserTest extends AbstractVireoFunctionalTest {

	private PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	private SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);

	/**
	 * Test that the create user page loads.
	 */
	@Test
	public void testPageLoad() {
		MockSettingsRepository mockSettingsRepository = new MockSettingsRepository();
		MockPersonRepository mockPersonRepository = new MockPersonRepository();
		Application.personRepo = mockPersonRepository;
		Application.settingRepo = mockSettingsRepository;
		FirstUser.personRepo = mockPersonRepository;
		FirstUser.settingRepo = mockSettingsRepository;
		Application.firstUser = null;

		try {
			assertEquals(0, Application.personRepo.findAllPersons().size());

			String URL = Router.reverse("Application.index").url;

			Response response = GET(URL);
			assertStatus(302, response);
			assertTrue(Application.firstUser);

			response = GET(URL);
			assertStatus(302, response);

			URL = Router.reverse("FirstUser.createUser").url;

			Map<String, String> params = new HashMap<String, String>();
			params.put("firstName", "John");
			params.put("lastName", "Doe");
			params.put("email", "john@email.com");
			params.put("password1", "password");
			params.put("password2", "password");
			params.put("netid1", "jdoe");
			params.put("netid2", "jdoe");
			params.put("createFirstUser", "true");

			response = POST(URL, params);
			assertStatus(302, response);
			assertEquals(1, mockPersonRepository.findPersonsTotal());
			assertFalse(Application.firstUser);

		} finally {
			Application.personRepo = personRepo;
			Application.settingRepo = settingRepo;
			FirstUser.personRepo = personRepo;
			FirstUser.settingRepo = settingRepo;
		}
	}
}
