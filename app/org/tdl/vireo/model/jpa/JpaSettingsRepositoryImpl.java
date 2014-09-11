package org.tdl.vireo.model.jpa;

import java.util.List;
import java.util.Map;

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
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.SettingsRepository;

/**
 * Jpa specific implementation of the Vireo Repository interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaSettingsRepositoryImpl implements SettingsRepository {
		
	// ///////////////////////////////////////////////
	// Degree, Major, College, and Department Models
	// ///////////////////////////////////////////////
	
	@Override
	public Degree createDegree(String name, DegreeLevel level) {
		return new JpaDegreeImpl(name, level);
	}

	@Override
	public Degree findDegree(Long id) {
		return (Degree) JpaDegreeImpl.findById(id);
	}

	@Override
	public Degree findDegreeByName(String name) {
		return JpaDegreeImpl.find("name = (?1)", name).first();
	}

	@Override
	public List<Degree> findAllDegrees() {
		return (List) JpaDegreeImpl.find("order by displayOrder").fetch();
	}

	@Override
	public Major createMajor(String name) {
		return new JpaMajorImpl(name);
	}

	@Override
	public Major findMajor(Long id) {
		return (Major) JpaMajorImpl.findById(id);
	}

	@Override
	public List<Major> findAllMajors() {
		return (List) JpaMajorImpl.find("order by displayOrder").fetch();
	}

	@Override
	public College createCollege(String name) {
		return new JpaCollegeImpl(name);
	}

	@Override
	public College createCollege(String name,List<String> emails) {
		return new JpaCollegeImpl(name,emails);
	}

	@Override
	public College findCollege(Long id) {
		return (College) JpaCollegeImpl.findById(id);
	}

	@Override
	public List<College> findAllColleges() {
		return (List) JpaCollegeImpl.find("order by displayOrder").fetch();
	}
	
	@Override
	public Program createProgram(String name) {
		return new JpaProgramImpl(name);
	}
	
	@Override
	public Program findProgram(Long id) {
		return (Program) JpaProgramImpl.findById(id);
	}
	
	@Override
	public List<Program> findAllPrograms() {
		return (List) JpaProgramImpl.find("order by displayOrder").fetch();
	}

	@Override
	public Department createDepartment(String name) {
		return new JpaDepartmentImpl(name);
	}

	@Override
	public Department findDepartment(Long id) {
		return (Department) JpaDepartmentImpl.findById(id);

	}

	@Override
	public List<Department> findAllDepartments() {
		return (List) JpaDepartmentImpl.find("order by displayOrder").fetch();
	}

	// /////////////////////
	// Document Type Model
	// /////////////////////
	
	@Override
	public DocumentType createDocumentType(String name, DegreeLevel level) {
		return new JpaDocumentTypeImpl(name, level);
	}

	@Override
	public DocumentType findDocumentType(Long id) {
		return (DocumentType) JpaDocumentTypeImpl.findById(id);
	}

	@Override
	public List<DocumentType> findAllDocumentTypes(DegreeLevel level) {
		if (level == null)
			return findAllDocumentTypes();
		
		return JpaDocumentTypeImpl.find("level = (?1)", level).fetch();
	}

	@Override
	public List<DocumentType> findAllDocumentTypes() {
		return (List) JpaDocumentTypeImpl.find("order by displayOrder").fetch();
	}

	// ////////////////////
	// Embargo Type Model
	// ////////////////////
	
	@Override
	public EmbargoType createEmbargoType(String name, String description,
			Integer duration, boolean active) {
		return new JpaEmbargoTypeImpl(name, description, duration, active);
	}

	@Override
	public EmbargoType findEmbargoType(Long id) {
		return (EmbargoType) JpaEmbargoTypeImpl.findById(id);
	}

	@Override
	public List<EmbargoType> findAllEmbargoTypes() {
		return (List) JpaEmbargoTypeImpl.find("order by displayOrder").fetch();
	}

	@Override
	public List<EmbargoType> findAllActiveEmbargoTypes() {
		return (List) JpaEmbargoTypeImpl.find("active = (?1) order by displayOrder", true).fetch();
	}

	// ////////////////////////
	// Graduation Month Model
	// ////////////////////////
	
	@Override
	public GraduationMonth createGraduationMonth(int month) {
		return new JpaGraduationMonthImpl(month);
	}
	
	@Override
	public GraduationMonth findGraduationMonth(Long id) {
		return (GraduationMonth) JpaGraduationMonthImpl.findById(id);
	}

	@Override
	public List<GraduationMonth> findAllGraduationMonths() {
		return (List) JpaGraduationMonthImpl.find("order by displayOrder").fetch();
	}

	// /////////////////////
	// Committee Member Role Type Model
	// /////////////////////
	
	@Override
	public CommitteeMemberRoleType createCommitteeMemberRoleType(String name, DegreeLevel level) {
		return new JpaCommitteeMemberRoleTypeImpl(name, level);
	}

	@Override
	public CommitteeMemberRoleType findCommitteeMemberRoleType(Long id) {
		return (CommitteeMemberRoleType) JpaCommitteeMemberRoleTypeImpl.findById(id);
	}

	@Override
	public List<CommitteeMemberRoleType> findAllCommitteeMemberRoleTypes(DegreeLevel level) {
		if (level == null)
			return findAllCommitteeMemberRoleTypes();
		
		return JpaCommitteeMemberRoleTypeImpl.find("level = (?1)", level).fetch();
	}

	@Override
	public List<CommitteeMemberRoleType> findAllCommitteeMemberRoleTypes() {
		return (List) JpaCommitteeMemberRoleTypeImpl.find("order by displayOrder").fetch();
	}
	
	// //////////////////////
	// Email Template Model
	// //////////////////////
	
	@Override
	public EmailTemplate createEmailTemplate(String name, String subject, String message) {
		return new JpaEmailTemplateImpl(name, subject, message);
	}

	@Override
	public EmailTemplate findEmailTemplate(Long id) {
		return (EmailTemplate) JpaEmailTemplateImpl.findById(id);
	}
	
	@Override
	public EmailTemplate findEmailTemplateByName(String name) {
		return JpaEmailTemplateImpl.find("name = (?1)", name).first();
	}

	@Override
	public List<EmailTemplate> findAllEmailTemplates() {
		return (List) JpaEmailTemplateImpl.find("order by displayOrder").fetch();
	}

	// ///////////////////////////
	// Custom action definitions
	// ///////////////////////////
	
	@Override
	public CustomActionDefinition createCustomActionDefinition(String label) {
		return new JpaCustomActionDefinitionImpl(label);
	}

	@Override
	public CustomActionDefinition findCustomActionDefinition(Long id) {
		return (CustomActionDefinition) JpaCustomActionDefinitionImpl.findById(id);
	}

	@Override
	public List<CustomActionDefinition> findAllCustomActionDefinition() {
		return (List) JpaCustomActionDefinitionImpl.find("order by displayOrder").fetch();
	}
	
	// ///////////////////////////
	// Language Model
	// ///////////////////////////
	
	@Override
	public Language createLanguage(String name) {
		return new JpaLanguageImpl(name);
	}
	
	@Override
	public Language findLanguage(Long id) {
		return (Language) JpaLanguageImpl.findById(id);
	}
	
	@Override
	public Language findLanguageByName(String name) {
		return (Language) JpaLanguageImpl.find("name = (?1)", name).first();
	}
	
	@Override
	public List<Language> findAllLanguages() {
		return (List) JpaLanguageImpl.find("order by displayOrder").fetch();
	}
	
	// ///////////////////////////
	// System wide configuration
	// ///////////////////////////
	
	@Override
	public Configuration createConfiguration(String name, String value) {
		return new JpaConfigurationImpl(name,value); 
	}

	@Override
	public Configuration findConfiguration(Long id) {
		return (Configuration) JpaConfigurationImpl.findById(id);
	}

	@Override
	public Configuration findConfigurationByName(String name) {
		return JpaConfigurationImpl.find("name = (?1)", name).first();
	}
	
	@Override
	public String getConfigValue(String name, String defaultValue) {
		
		Configuration config = findConfigurationByName(name);
		if (config == null || config.getValue() == null || config.getValue().trim().length() == 0)
			return defaultValue;
		else
			return config.getValue();
	}
	
	@Override
	public String getConfigValue(String name) {
		
		return getConfigValue(name,Configuration.DEFAULTS.get(name));
	}
	
	@Override
	public boolean getConfigBoolean(String name) {
		
		return getConfigValue(name) != null ? true : false;
	}

	@Override
	public List<Configuration> findAllConfigurations() {
		return (List) JpaConfigurationImpl.findAll();

	}

	/**
	 * Set default configuration parameters from spring.
	 * 
	 * @param defaults
	 *            A map of name value pairs for default configuration
	 *            paramaters.
	 */
	public void setConfigurationDefaults(Map<String, String> defaults) {
		for (String name : defaults.keySet()) {
			String value = defaults.get(name).trim();
			Configuration.DEFAULTS.register(name, value);
		}
	}

	// ///////////////////////////
	// Deposit Locations
	// ///////////////////////////
	
	@Override
	public DepositLocation createDepositLocation(String name) {
		return new JpaDepositLocationImpl(name); 
	}

	@Override
	public DepositLocation findDepositLocation(Long id) {
		return (DepositLocation) JpaDepositLocationImpl.findById(id);
	}

	@Override
	public DepositLocation findDepositLocationByName(String name) {
		return JpaDepositLocationImpl.find("name = (?1)", name).first();
	}

	@Override
	public List<DepositLocation> findAllDepositLocations() {
		return (List) JpaDepositLocationImpl.find("order by displayOrder").fetch();
	}
}
