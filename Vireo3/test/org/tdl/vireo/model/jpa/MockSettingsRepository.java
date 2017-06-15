/**
 * 
 */
package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.tdl.vireo.model.MockEmailTemplate;
import org.tdl.vireo.model.MockEmailWorkflowRule;
import org.tdl.vireo.model.MockEmbargoType;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.state.State;

/**
 * @author gad
 *
 */
public class MockSettingsRepository implements SettingsRepository {

	private List<CommitteeMemberRoleType> mockRoleTypes = new ArrayList<CommitteeMemberRoleType>();
	private List<Configuration> mockConfigs = new ArrayList<Configuration>();
	private List<EmbargoType> mockEmbargos = new ArrayList<EmbargoType>();
	private List<EmailTemplate> mockEmailTemplates = new ArrayList<EmailTemplate>();
	private List<EmailWorkflowRule> mockEmailWorkflowRules = new ArrayList<EmailWorkflowRule>();

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
		mockEmbargoType.id = System.currentTimeMillis();
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
		return mockEmbargos;
	}
	
	@Override
    public EmbargoType findSystemEmbargoTypeByNameAndGuarantor(String name, EmbargoGuarantor guarantor) {
	    EmbargoType ret = null;
	    for (EmbargoType mockEmbargo : mockEmbargos) {
	        if(mockEmbargo.getName().equals(name) && mockEmbargo.getGuarantor().equals(guarantor) && mockEmbargo.isSystemRequired()) {
	        	ret = mockEmbargo;
	        }
        }
	    return ret;
    }

	@Override
    public EmbargoType findNonSystemEmbargoTypeByNameAndGuarantor(String name, EmbargoGuarantor guarantor) {
		EmbargoType ret = null;
	    for (EmbargoType mockEmbargo : mockEmbargos) {
	        if(mockEmbargo.getName().equals(name) && mockEmbargo.getGuarantor().equals(guarantor) && !mockEmbargo.isSystemRequired()) {
	        	ret = mockEmbargo;
	        }
        }
	    return ret;
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
		return mockRoleTypes;
	}

	@Override
	public EmailTemplate createEmailTemplate(String name, String subject, String message) {
		MockEmailTemplate mockEmailTemplate = new MockEmailTemplate();
		mockEmailTemplate.id = System.currentTimeMillis();
		mockEmailTemplate.setName(name);
		mockEmailTemplate.setSubject(subject);
		mockEmailTemplate.setMessage(message);
		mockEmailTemplates.add(mockEmailTemplate);
		return mockEmailTemplate;
	}

	@Override
	public EmailTemplate findEmailTemplate(Long id) {
		// DO NOTHING
		return null;
	}

	@Override
	public EmailTemplate findEmailTemplateByName(String name) {
		EmailTemplate sysRet = null, custRet = null;
		
		for (EmailTemplate emailTemplate : mockEmailTemplates) {
			if (emailTemplate.getName().equals(name)) {
				if(emailTemplate.isSystemRequired())
					sysRet = emailTemplate;
				else
					custRet = emailTemplate;
			}
		}
		return (custRet != null ? custRet : sysRet);
	}
	
	@Override
	public EmailTemplate findNonSystemEmailTemplateByName(String name) {
		// DO NOTHING
	    return null;
	}
	
	@Override
	public EmailTemplate findSystemEmailTemplateByName(String name) {
		EmailTemplate sysRet = null;
		
		for (EmailTemplate emailTemplate : mockEmailTemplates) {
			if (emailTemplate.getName().equals(name)) {
				if(emailTemplate.isSystemRequired())
					sysRet = emailTemplate;
			}
		}
		return sysRet;
	}

	@Override
	public List<EmailTemplate> findAllEmailTemplates() {
		return mockEmailTemplates;
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
		return mockConfigs;
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
		MockEmailWorkflowRule mockEmailWorkflowRule = new MockEmailWorkflowRule();
		mockEmailWorkflowRule.setAssociatedState(associatedState);
		mockEmailWorkflowRules.add(mockEmailWorkflowRule);
		return mockEmailWorkflowRule;
	}

	@Override
	public EmailWorkflowRule findEmailWorkflowRule(Long id) {
		// DO NOTHING
		return null;
	}

	@Override
	public List<EmailWorkflowRule> findEmailWorkflowRulesByState(State type) {
		List<EmailWorkflowRule> ret = new ArrayList<EmailWorkflowRule>();
		for (EmailWorkflowRule rule : mockEmailWorkflowRules) {
			if (rule.getAssociatedState().equals(type)) {
				ret.add(rule);
			}
		}
		return ret;
	}

	@Override
	public List<EmailWorkflowRule> findAllEmailWorkflowRules() {
		return mockEmailWorkflowRules;
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