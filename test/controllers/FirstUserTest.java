package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.AdministrativeGroup;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.ConditionType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.EmbargoGuarantor;
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
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.services.SystemDataLoader;
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
		
		SystemDataLoader systemDataLoader = Spring.getBeanOfType(SystemDataLoader.class);
		systemDataLoader.setSettingsRepository(FirstUser.settingRepo);
		//systemDataLoader.generateAllSystemEmailTemplates();
		systemDataLoader.generateAllSystemEmbargos();
		systemDataLoader.setSettingsRepository(settingRepo);
		
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
			assertEquals(9, MockSettingsRepository.mockEmbargos.size());
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
			// DO NOTHING
			return null;
		}

		@Override
		public Person findPersonByEmail(String email) {
			// DO NOTHING
			return null;
		}

		@Override
		public Person findPersonByNetId(String netId) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<Person> findPersonsByRole(RoleType type) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<Person> searchPersons(String query, int offset, int limit) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<Person> findAllPersons() {
			// DO NOTHING
			return new ArrayList<Person>();
		}

		@Override
		public long findPersonsTotal() {
			return 0L;
		}

		@Override
		public Preference findPreference(Long id) {
			// DO NOTHING
			return null;
		}

	}

	public static class MockSettingsRepository implements SettingsRepository {

		public static List<MockEmbargoType> mockEmbargos = new ArrayList<MockEmbargoType>();
		public static List<MockEmbargoType> mockProQuestEmbargos = new ArrayList<MockEmbargoType>();
		public static List<MockConfiguration> mockConfigs = new ArrayList<MockConfiguration>();
		public static List<MockCommitteeMemberRoleType> mockRoleTypes = new ArrayList<MockCommitteeMemberRoleType>();

		@Override
		public Degree createDegree(String name, DegreeLevel level) {
			// DO NOTHING
			return null;
		}

		@Override
		public Degree findDegree(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public Degree findDegreeByName(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<Degree> findAllDegrees() {
			// DO NOTHING
			return null;
		}

		@Override
		public Major createMajor(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public Major findMajor(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<Major> findAllMajors() {
			// DO NOTHING
			return null;
		}

		@Override
		public College createCollege(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public College createCollege(String name, HashMap<Integer, String> emails) {
			// DO NOTHING
			return null;
		}

		@Override
		public College findCollege(Long id) {
			// DO NOTHING
			return null;
		}
		
		@Override
		public College findCollegeByName(String name) {
		    // DO NOTHING
		    return null;
		}

		@Override
		public List<College> findAllColleges() {
			// DO NOTHING
			return null;
		}

		@Override
		public Program createProgram(String name) {
			// DO NOTHING
			return null;
		}
		
		@Override
		public Program createProgram(String name, HashMap<Integer, String> emails) {
			// DO NOTHING
			return null;
		}

		@Override
		public Program findProgram(Long id) {
			// DO NOTHING
			return null;
		}
		
		@Override
		public Program findProgramByName(String name) {
		    // DO NOTHING
		    return null;
		}

		@Override
		public List<Program> findAllPrograms() {
			// DO NOTHING
			return null;
		}

		@Override
		public Department createDepartment(String name) {
			// DO NOTHING
			return null;
		}
		
		@Override
		public Department createDepartment(String name, HashMap<Integer, String> emails) {
			// DO NOTHING
			return null;
		}

		@Override
		public Department findDepartment(Long id) {
			// DO NOTHING
			return null;
		}
		
		@Override
		public Department findDepartmentByName(String name) {
		    // DO NOTHING
		    return null;
		}

		@Override
		public List<Department> findAllDepartments() {
			// DO NOTHING
			return null;
		}

		@Override
		public DocumentType createDocumentType(String name, DegreeLevel level) {
			// DO NOTHING
			return null;
		}

		@Override
		public DocumentType findDocumentType(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<DocumentType> findAllDocumentTypes(DegreeLevel level) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<DocumentType> findAllDocumentTypes() {
			// DO NOTHING
			return null;
		}

		@Override
		public EmbargoType createEmbargoType(String name, String description, Integer duration, boolean active) {

			MockEmbargoType mockEmbargoType = new MockEmbargoType();
			mockEmbargoType.name = name;
			mockEmbargoType.description = description;
			mockEmbargoType.duration = duration;
			mockEmbargoType.active = active;
			mockEmbargoType.guarantor = null;

			mockEmbargos.add(mockEmbargoType);

			return mockEmbargoType;
		}
		
		@Override
		public EmbargoType createEmbargoType(String name, String description, Integer duration, boolean active, EmbargoGuarantor guarantor) {

			MockEmbargoType mockEmbargoType = new MockEmbargoType();
			mockEmbargoType.name = name;
			mockEmbargoType.description = description;
			mockEmbargoType.duration = duration;
			mockEmbargoType.active = active;
			mockEmbargoType.guarantor = guarantor;

			mockEmbargos.add(mockEmbargoType);

			return mockEmbargoType;
		}

		@Override
		public EmbargoType findEmbargoType(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<EmbargoType> findAllEmbargoTypes() {
			return new ArrayList<EmbargoType>();
		}

		@Override
		public List<EmbargoType> findAllActiveEmbargoTypes() {
			// DO NOTHING
			return null;
		}

		@Override
		public GraduationMonth createGraduationMonth(int month) {
			// DO NOTHING
			return null;
		}

		@Override
		public GraduationMonth findGraduationMonth(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<GraduationMonth> findAllGraduationMonths() {
			// DO NOTHING
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
			// DO NOTHING
			return null;
		}

		@Override
		public List<CommitteeMemberRoleType> findAllCommitteeMemberRoleTypes(DegreeLevel level) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<CommitteeMemberRoleType> findAllCommitteeMemberRoleTypes() {
			// DO NOTHING
			return null;
		}

		@Override
		public EmailTemplate createEmailTemplate(String name, String subject, String message) {
			// DO NOTHING
			return null;
		}

		@Override
		public EmailTemplate findEmailTemplate(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public EmailTemplate findEmailTemplateByName(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<EmailTemplate> findAllEmailTemplates() {
			// DO NOTHING
			return null;
		}

		@Override
		public CustomActionDefinition createCustomActionDefinition(String label, Boolean isStudentVisible) {
			// DO NOTHING
			return null;
		}

		@Override
		public CustomActionDefinition findCustomActionDefinition(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<CustomActionDefinition> findAllCustomActionDefinition() {
			// DO NOTHING
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
			// DO NOTHING
			return null;
		}

		@Override
		public Configuration findConfigurationByName(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public String getConfigValue(String name, String defaultValue) {
			// DO NOTHING
			return null;
		}

		@Override
		public String getConfigValue(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public boolean getConfigBoolean(String name) {
			// DO NOTHING
			return false;
		}

		@Override
		public List<Configuration> findAllConfigurations() {
			// DO NOTHING
			return null;
		}

		@Override
		public DepositLocation createDepositLocation(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public DepositLocation findDepositLocation(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public DepositLocation findDepositLocationByName(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<DepositLocation> findAllDepositLocations() {
			// DO NOTHING
			return null;
		}

		@Override
		public Language findLanguage(Long id) {
			// DO NOTHING
			return null;
		}

		@Override
		public List<Language> findAllLanguages() {
			// DO NOTHING
			return null;
		}

		@Override
		public Language createLanguage(String name) {
			// DO NOTHING
			return null;
		}

		@Override
		public Language findLanguageByName(String name) {
			// DO NOTHING
			return null;
		}

		@Override
        public EmailWorkflowRule createEmailWorkflowRule(State associatedState) {
	        // DO NOTHING
	        return null;
        }

		@Override
        public EmailWorkflowRule findEmailWorkflowRule(Long id) {
	        // DO NOTHING
	        return null;
        }

		@Override
        public List<EmailWorkflowRule> findEmailWorkflowRulesByState(State type) {
	        // DO NOTHING
	        return null;
        }

		@Override
        public List<EmailWorkflowRule> findAllEmailWorkflowRules() {
	        // DO NOTHING
	        return null;
        }

		@Override
        public AdministrativeGroup createAdministrativeGroup(String name) {
	        // DO NOTHING
	        return null;
        }

		@Override
        public AdministrativeGroup createAdministrativeGroup(String name, HashMap<Integer, String> emails) {
	        // DO NOTHING
	        return null;
        }

		@Override
        public AdministrativeGroup findAdministrativeGroup(Long id) {
	        // DO NOTHING
	        return null;
        }

		@Override
        public AdministrativeGroup findAdministrativeGroupByName(String name) {
	        // DO NOTHING
	        return null;
        }

		@Override
        public List<AdministrativeGroup> findAllAdministrativeGroups() {
	        // DO NOTHING
	        return null;
        }

		@Override
        public AbstractWorkflowRuleCondition createEmailWorkflowRuleCondition(ConditionType condition) {
	        // DO NOTHING
	        return null;
        }

		@Override
        public List<AbstractWorkflowRuleCondition> findAllEmailWorkflowRuleConditions() {
	        // DO NOTHING
	        return null;
        }

		@Override
        public AbstractWorkflowRuleCondition findEmailWorkflowRuleCondition(Long id) {
	        // DO NOTHING
	        return null;
        }
		
	}
}
