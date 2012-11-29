package org.tdl.vireo.model;

import java.util.List;

/**
 * The Vireo persistent repository for application-level settings. This object
 * follows the spring repository pattern, where this is the source for creating
 * and locating all persistent model objects. It is intended that this object
 * will be injected into all other spring beans that need access application
 * settings.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface SettingsRepository {

	// ///////////////////////////////////////////////
	// Degree, Major, College, and Department Models
	// ///////////////////////////////////////////////

	/**
	 * Create a new Degree object
	 * 
	 * @param name
	 *            Name of the degree.
	 * @param level
	 *            Level of the degree.
	 * @return A new degree object.
	 */
	public Degree createDegree(String name, DegreeLevel level);

	/**
	 * Find degree by unique id.
	 * 
	 * @param id
	 *            The degree's id.
	 * @return The degree object or null if not found.
	 */
	public Degree findDegree(Long id);

	/**
	 * Find degree by name.
	 *
	 * @param name
	 *			The name given to the degree.
	 *
	 * @return The degree object or null if not found.
	 */
	public Degree findDegreeByName(String name);

	/**
	 * Find all degree objects in order.
	 * 
	 * @return A list of all degrees, or an empty list if there are none.
	 */
	public List<Degree> findAllDegrees();

	/**
	 * Create a new Major object.
	 * 
	 * @param name
	 *            The name of the major.
	 * @return A new major object.
	 */
	public Major createMajor(String name);

	/**
	 * Find major by unique id.
	 * 
	 * @param id
	 *            The major's id.
	 * @return The major object or null if not found.
	 */
	public Major findMajor(Long id);

	/**
	 * Find all majors objects in order.
	 * 
	 * @return A list of all majors, or an empty list if there are none.
	 */
	public List<Major> findAllMajors();

	/**
	 * Create a new college object.
	 * 
	 * @param name
	 *            The name of the college.
	 * @return A new college object.
	 */
	public College createCollege(String name);

	/**
	 * Find a college by unique id.
	 * 
	 * @param id
	 *            The college's id.
	 * @return The college object or null if not found.
	 */
	public College findCollege(Long id);

	/**
	 * Find all college objects in order.
	 * 
	 * @return A list of all colleges, or an empty list if there are none.
	 */
	public List<College> findAllColleges();

	/**
	 * Create a new program object.
	 * 
	 * @param name
	 * 			The name of the program
	 * @return A new program object.
	 */
	public Program createProgram(String name);
	
	/**
	 * Find a program by unique id.
	 * 
	 * @param id
	 * 			The program's id
	 * @return The program object or null if not found.
	 */
	public Program findProgram(Long id);
	
	/**
	 * Find all program objects in order.
	 * 
	 * @return A list of all programs, or an empty list if there are none.
	 * 
	 */
	public List<Program> findAllPrograms();
	
	/**
	 * Create a new department object.
	 * 
	 * @param name
	 *            The name of the department.
	 * @return A new department object.
	 */
	public Department createDepartment(String name);

	/**
	 * Find department by unique id
	 * 
	 * @param id
	 *            The department's id.
	 * @return The department object or null if not found.
	 */
	public Department findDepartment(Long id);

	/**
	 * Find all department objects in order.
	 * 
	 * @return A list of all departments, or an empty list if there are none.
	 */
	public List<Department> findAllDepartments();

	// /////////////////////
	// Document Type Model
	// /////////////////////

	/**
	 * Create a new document type object.
	 * 
	 * @param name
	 *            The name of the document type.
	 * @param level
	 *            The level this document type is applicable for.
	 * @return A new document type object.
	 */
	public DocumentType createDocumentType(String name, DegreeLevel level);

	/**
	 * Find a document type by id.
	 * 
	 * @param id
	 *            The document type's id.
	 * @return The document type object, or null if not found.
	 */
	public DocumentType findDocumentType(Long id);

	/**
	 * Find all document type objects in order that are valid for a particular
	 * degree level.
	 * 
	 * @param level
	 *            The degree level to find.
	 * @return A list of all document types, or an empty list if there are none.
	 */
	public List<DocumentType> findAllDocumentTypes(DegreeLevel level);

	/**
	 * Find all document type objects in order.
	 * 
	 * @return A list of all document types, or an empty list if there are none.
	 */
	public List<DocumentType> findAllDocumentTypes();

	// //////////////////////
	// Language Model
	// //////////////////////
	
	/**
	 * Create a language
	 * 
	 * @param name
	 * 			The description provided by Proquest of the language.
	 */
	public Language createLanguage(String name);
	
	/**
	 * Find a language by id.
	 * 
	 * @param id
	 * 			The id of the language.
	 */
	public Language findLanguage(Long id);
	
	/**
	 * Find a language by the name
	 * 
	 * @param name
	 * 			The name of the language.
	 */
	public Language findLanguageByName(String name);
	
	/**
	 * Find all available languages.
	 * 
	 * @return A list of all available languages, or an empty list if
	 * 			there are none.
	 */
	public List<Language> findAllLanguages();
	
	// ////////////////////
	// Embargo Type Model
	// ////////////////////

	/**
	 * Create a new embargo type.
	 * 
	 * @param name
	 *            The unique name of the embargo type
	 * @param description
	 *            A description of the embargo.
	 * @param duration
	 *            How long the embargo should last messured in months. Null for
	 *            ideterminate, zero for no embargo, and negative numbers are
	 *            not allowed.
	 * @param active
	 *            Weather the embargo is currently active.
	 * @return A new embargo type.
	 */
	public EmbargoType createEmbargoType(String name, String description,
			Integer duration, boolean active );

	/**
	 * Find embargo type by id.
	 * 
	 * @param id
	 *            The embargo type's id.
	 * @return The embargo type, or null if not found.
	 */
	public EmbargoType findEmbargoType(Long id);

	/**
	 * Find all embargo types objects in order.
	 * 
	 * @return A list of all embargo types, or an empty list if there are none.
	 */
	public List<EmbargoType> findAllEmbargoTypes();

	/**
	 * Find all embargo types objects in order.
	 * 
	 * @return A list of all embargo types, or an empty list if there are none.
	 */
	public List<EmbargoType> findAllActiveEmbargoTypes();

	// ////////////////////////
	// Graduation Month Model
	// ////////////////////////

	/**
	 * Create a new graduation month.
	 * 
	 * @param month
	 *            The graduation month.
	 * @return A new graduation month object.
	 */
	public GraduationMonth createGraduationMonth(int month);

	/**
	 * Find a graduation month by id.
	 * 
	 * @param id
	 *            The month's id.
	 * @return The graduation month object, or null if not found.
	 */
	public GraduationMonth findGraduationMonth(Long id);

	/**
	 * Find all graduation months in order.
	 * 
	 * @return A list of all graduation months, or an empty list if there are
	 *         none.
	 */
	public List<GraduationMonth> findAllGraduationMonths();

	// /////////////////////
	// Committee Member Role Type Model
	// /////////////////////

	/**
	 * Create a new committee member role type
	 * 
	 * @param name
	 *            The name of the role.
	 * @param level
	 *            The level this role is available for.
	 * @return A role type object.
	 */
	public CommitteeMemberRoleType createCommitteeMemberRoleType(String name,
			DegreeLevel level);

	/**
	 * Find a committee member role type by id.
	 * 
	 * @param id
	 *            The type's id.
	 * @return The role type object, or null if not found.
	 */
	public CommitteeMemberRoleType findCommitteeMemberRoleType(Long id);

	/**
	 * Find all committee member role type objects in order that are valid for a
	 * particular degree level.
	 * 
	 * @param level
	 *            The degree level to find.
	 * @return A list of all role types, or an empty list if there are none.
	 */
	public List<CommitteeMemberRoleType> findAllCommitteeMemberRoleTypes(
			DegreeLevel level);

	/**
	 * Find all committee role type objects in order.
	 * 
	 * @return A list of all role types, or an empty list if there are none.
	 */
	public List<CommitteeMemberRoleType> findAllCommitteeMemberRoleTypes();

	// //////////////////////
	// Email Template Model
	// //////////////////////

	/**
	 * Create a new email template
	 * 
	 * @param name
	 *            The template's name
	 * @param subject
	 *            The template's subject
	 * @param message
	 *            The template's messages
	 * @return A new email template object.
	 */
	public EmailTemplate createEmailTemplate(String name, String subject, String message);

	/**
	 * Find an email template by id.
	 * 
	 * @param id
	 *            The email template's id.
	 * @return The email template object, or null if not found.
	 */
	public EmailTemplate findEmailTemplate(Long id);
	
	/**
	 * Find an email template by it's name.
	 * 
	 * @param name
	 *            The name of the template.
	 * @return The email template found, or null if not found.
	 */
	public EmailTemplate findEmailTemplateByName(String name);

	/**
	 * Find all email templates in order.
	 * 
	 * @return A list of all email templates, or an empty list if there are
	 *         none.
	 */
	public List<EmailTemplate> findAllEmailTemplates();

	// ///////////////////////////
	// Custom action definitions
	// ///////////////////////////

	/**
	 * Create a new custom action definition
	 * 
	 * @param label
	 *            The action's label
	 * @return A new custom action definition.
	 */
	public CustomActionDefinition createCustomActionDefinition(String label);

	/**
	 * Find custom action definition by id.
	 * 
	 * @param id
	 *            The action's id.
	 * @return The custom action definition, or null if not found.
	 */
	public CustomActionDefinition findCustomActionDefinition(Long id);

	/**
	 * Find all custom action definitions in order.
	 * 
	 * @return A list of all custom action definitions, or an empty list if
	 *         there are none.
	 */
	public List<CustomActionDefinition> findAllCustomActionDefinition();
	
	// ///////////////////////////
	// System wide configuration
	// ///////////////////////////	
	
	/**
	 * Create a new system wide configuration.
	 * 
	 * @param name
	 *            The name of the canfiguration parameter.
	 * @param value
	 *            The value.
	 * @return A new configuration object.
	 */
	public Configuration createConfiguration(String name, String value);

	/**
	 * Find a system wide configuration object by id.
	 * 
	 * @param id
	 *            The configuration's id.
	 * @return The configuration object, or null if not found.
	 */
	public Configuration findConfiguration(Long id);

	/**
	 * Find a system wide configuration object by name.
	 * 
	 * @param name
	 *            The configuration's name.
	 * @return The configuration object, or null if not found.
	 */
	public Configuration findConfigurationByName(String name);
	
	/**
	 * Find a system wide configuration and return it's value. If the
	 * configuration parameter is not set, then return the default value
	 * supplied.
	 * 
	 * @param name
	 *            The name of the configuration object.
	 * @param defaultValue
	 *            The default value for the configuration object if not found.
	 * @return The value
	 */
	public String getConfigValue(String name, String defaultValue);

	/**
	 * Find a system wide configuration and return it's value. If the
	 * configuration object does not exist the the Configuration.DEFAULTS
	 * registry will be consulted. If a default has been registered for the
	 * parameter then the default will be returned, otherwise null.
	 * 
	 * @param name
	 *            The name of the parameter.
	 * @return The value, or null.
	 */
	public String getConfigValue(String name);
	
	/**
	 * Find a system wide configuration and return it's boolean value. If the
	 * configuration parameter has any value at all it is considered true, if no
	 * configuration entry is defined for the parameter then it is considered
	 * false.
	 * 
	 * @param name
	 *            The name of the configuration object.
	 * @return The value
	 */
	public boolean getConfigBoolean(String name);

	/**
	 * Find all system wide configuration parameters.
	 * 
	 * @return A list of all system wide configuration parameters, or an empty
	 *         list if there are none.
	 */
	public List<Configuration> findAllConfigurations();
	
	
	// ///////////////////////////
	// Deposit Locations
	// ///////////////////////////

	/**
	 * Create a new deposit location
	 * 
	 * @param name
	 *            The name of the new deposit location.
	 * @return A new deposit location object.
	 */
	public DepositLocation createDepositLocation(String name);

	/**
	 * Find a deposit location object by id.
	 * 
	 * @param id
	 *            The location's unique id.
	 * @return The deposit location, or null if not found.
	 */
	public DepositLocation findDepositLocation(Long id);

	/**
	 * Find a deposit location by name.
	 * 
	 * @param name
	 *            The location's unique name.
	 * @return The deposit location, or null if not found.
	 */
	public DepositLocation findDepositLocationByName(String name);

	/**
	 * Find all defined deposit locations.
	 * 
	 * @return A list of all deposit locations.
	 */
	public List<DepositLocation> findAllDepositLocations();

}
