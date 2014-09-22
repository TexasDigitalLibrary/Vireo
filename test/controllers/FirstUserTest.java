package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.MockCommitteeMemberRoleType;
import org.tdl.vireo.model.MockConfiguration;
import org.tdl.vireo.model.MockEmbargoType;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.model.WorkflowEmailRule;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

public class FirstUserTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);

	/**
	 * Test that the create user page loads.
	 */
	@Test
	public void testPageLoad() {
		Application.personRepo = new MockPersonRepository();
		FirstUser.personRepo = new MockPersonRepository();
		FirstUser.settingRepo = new MockSettingsRepository();
		Application.firstUser = null;

		MockSettingsRepository.mockEmbargos.clear();
		MockSettingsRepository.mockConfigs.clear();
		MockSettingsRepository.mockRoleTypes.clear();

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
			assertNotNull(MockPersonRepository.lastPersonCreated);
			assertFalse(Application.firstUser);

			// Test that the default values were created.
			assertEquals(4, MockSettingsRepository.mockEmbargos.size());
			assertEquals(3, MockSettingsRepository.mockRoleTypes.size());

		} finally {
			Application.personRepo = personRepo;
			FirstUser.personRepo = personRepo;
			FirstUser.settingRepo = settingRepo;
		}
	}

	public static class MockPersonRepository implements PersonRepository {

		public static MockPerson lastPersonCreated;

		@Override
		public Person createPerson(String netId, String email, String firstName, String lastName, RoleType role) {

			MockPerson mockPerson = new MockPerson();
			mockPerson.netid = netId;
			mockPerson.email = email;
			mockPerson.firstName = firstName;
			mockPerson.lastName = lastName;
			mockPerson.role = role;

			lastPersonCreated = mockPerson;

			return mockPerson;
		}

		@Override
		public Person findPerson(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Person findPersonByEmail(String email) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Person findPersonByNetId(String netId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Person> findPersonsByRole(RoleType type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Person> searchPersons(String query, int offset, int limit) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Person> findAllPersons() {
			// TODO Auto-generated method stub
			return new ArrayList<Person>();
		}

		@Override
		public long findPersonsTotal() {
			return 0L;
		}

		@Override
		public Preference findPreference(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class MockSettingsRepository implements SettingsRepository {

		public static List<MockEmbargoType> mockEmbargos = new ArrayList<MockEmbargoType>();
		public static List<MockConfiguration> mockConfigs = new ArrayList<MockConfiguration>();
		public static List<MockCommitteeMemberRoleType> mockRoleTypes = new ArrayList<MockCommitteeMemberRoleType>();

		@Override
		public Degree createDegree(String name, DegreeLevel level) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Degree findDegree(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Degree findDegreeByName(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Degree> findAllDegrees() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Major createMajor(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Major findMajor(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Major> findAllMajors() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public College createCollege(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public College createCollege(String name, List<String> emails) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public College findCollege(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<College> findAllColleges() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Program createProgram(String name) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Program createProgram(String name, List<String> emails) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Program findProgram(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Program> findAllPrograms() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Department createDepartment(String name) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Department createDepartment(String name, List<String> emails) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Department findDepartment(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Department> findAllDepartments() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DocumentType createDocumentType(String name, DegreeLevel level) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DocumentType findDocumentType(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<DocumentType> findAllDocumentTypes(DegreeLevel level) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<DocumentType> findAllDocumentTypes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public EmbargoType createEmbargoType(String name, String description, Integer duration, boolean active) {

			MockEmbargoType mockEmbargoType = new MockEmbargoType();
			mockEmbargoType.name = name;
			mockEmbargoType.description = description;
			mockEmbargoType.duration = duration;
			mockEmbargoType.active = active;

			mockEmbargos.add(mockEmbargoType);

			return mockEmbargoType;
		}

		@Override
		public EmbargoType findEmbargoType(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<EmbargoType> findAllEmbargoTypes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<EmbargoType> findAllActiveEmbargoTypes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GraduationMonth createGraduationMonth(int month) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GraduationMonth findGraduationMonth(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<GraduationMonth> findAllGraduationMonths() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CommitteeMemberRoleType createCommitteeMemberRoleType(String name, DegreeLevel level) {

			MockCommitteeMemberRoleType roleType = new MockCommitteeMemberRoleType();
			roleType.name = name;
			roleType.level = level;

			mockRoleTypes.add(roleType);

			return roleType;
		}

		@Override
		public CommitteeMemberRoleType findCommitteeMemberRoleType(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<CommitteeMemberRoleType> findAllCommitteeMemberRoleTypes(DegreeLevel level) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<CommitteeMemberRoleType> findAllCommitteeMemberRoleTypes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public EmailTemplate createEmailTemplate(String name, String subject, String message) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public EmailTemplate findEmailTemplate(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public EmailTemplate findEmailTemplateByName(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<EmailTemplate> findAllEmailTemplates() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CustomActionDefinition createCustomActionDefinition(String label) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CustomActionDefinition findCustomActionDefinition(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<CustomActionDefinition> findAllCustomActionDefinition() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Configuration createConfiguration(String name, String value) {

			MockConfiguration mockConfig = new MockConfiguration();
			mockConfig.name = name;
			mockConfig.value = value;

			mockConfigs.add(mockConfig);

			return mockConfig;
		}

		@Override
		public Configuration findConfiguration(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Configuration findConfigurationByName(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getConfigValue(String name, String defaultValue) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getConfigValue(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getConfigBoolean(String name) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public List<Configuration> findAllConfigurations() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DepositLocation createDepositLocation(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DepositLocation findDepositLocation(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DepositLocation findDepositLocationByName(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<DepositLocation> findAllDepositLocations() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Language findLanguage(Long id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Language> findAllLanguages() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Language createLanguage(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Language findLanguageByName(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
        public WorkflowEmailRule createWorkflowEmailRule(State associatedState) {
	        // TODO Auto-generated method stub
	        return null;
        }

		@Override
        public WorkflowEmailRule findWorkflowEmailRule(Long id) {
	        // TODO Auto-generated method stub
	        return null;
        }

		@Override
        public List<WorkflowEmailRule> findWorkflowEmailRulesByState(State type) {
	        // TODO Auto-generated method stub
	        return null;
        }

		@Override
        public List<WorkflowEmailRule> findAllWorkflowEmailRules() {
	        // TODO Auto-generated method stub
	        return null;
        }
	}
}
