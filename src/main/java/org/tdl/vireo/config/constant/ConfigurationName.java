package org.tdl.vireo.config.constant;

/**
 * Common Application Configuration Names.
 * 
 * Stored in the ConfigurationRepository there are global configuration objects
 * consisting of name / value pairs. Different parts of the application are
 * coded to look up specific configuration parameters to determine how the
 * application should respond in specific circumstances. The static values
 * defined here are the common definitions for those configuration parameters.
 * This allows these names to be defined in one location while being statically
 * linked throughout the application.
 * 
 * Future developers, feel free to add additional configuration parameters to
 * this class as needed.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ConfigurationName {
	
	// Application Settings
	/** If defined then submissions are open, otherwise they are closed. **/
	public final static String SUBMISSIONS_OPEN = "submissions_open";
	
	/** Text string describing the current semester for which submissions are being accepted. **/
	public final static String CURRENT_SEMESTER = "current_semester";
	
	/** If defined then multiple submissions are allowed. */
	public final static String ALLOW_MULTIPLE_SUBMISSIONS = "allow_multiple_submissions";
	
	/** The degree awarding institution.  */
	public final static String GRANTOR = "grantor";
	
	/** The license students must agree to during submission  */
	public final static String SUBMIT_LICENSE_TEXT = "submit_license";
	
	/**The from address attached to every email by default **/
	public final static String EMAIL_FROM = "email_from";
	
	/**The replyTo address attached to every email by default **/
	public final static String EMAIL_REPLY_TO = "email_reply_to";
	
	// Theme settings
	/** Background main color */
	public final static String BACKGROUND_MAIN_COLOR = "background_main_color";
	
	/** Background highlight color */
	public final static String BACKGROUND_HIGHLIGHT_COLOR = "background_highlight_color";
	
	/** Submission Step Button main color when in "on" state */
	public final static String BUTTON_MAIN_COLOR_ON = "button_main_color_on";
	
	/** Submission Step Button highlight color when in "on" state */
	public final static String BUTTON_HIGHLIGHT_COLOR_ON = "button_highlight_color_on";
	
	/** Submission Step Button main color when in "off" state */
	public final static String BUTTON_MAIN_COLOR_OFF = "button_main_color_off";
	
	/** Submission Step Button highlight color when in "off" state */
	public final static String BUTTON_HIGHLIGHT_COLOR_OFF = "button_highlight_color_off";
	
	/** Custom CSS */
    public final static String LEFT_LOGO = "left_logo";
    
    /** Custom CSS */
    public final static String RIGTH_LOGO = "right_logo";
	
	/** Custom CSS */
	public final static String CUSTOM_CSS = "custom_css";
	
	/** Instructions to show on the front page of Vireo. */
	public final static String FRONT_PAGE_INSTRUCTIONS = "front_page_instructions";
	
	/** Instructions to show after completing a submission */
	public final static String SUBMIT_INSTRUCTIONS = "submit_instructions";

	/** Instructions to show after submitting corrections */
	public final static String CORRECTION_INSTRUCTIONS = "correction_instructions";
	
	//ORCID settings
	/** Whether or not to validate ORCID IDs when changed */
	public final static String ORCID_VALIDATION = "orcid_validation";
	
	/** Whether or not to authenticate the Student against the ORCID ID. */
	public final static String ORCID_AUTHENTICATION = "orcid_authentication";
	
	//Proquest settings
	/** The proquest institution code visible during export. */
	public final static String PROQUEST_INSTITUTION_CODE = "proquest_institution_code";	
	
	/** Allow proquest indexing by search engines. */
	public final static String PROQUEST_INDEXING = "proquest_indexing";

	/** The ProQuest license students may have to agree to during submission */
	public final static String PROQUEST_LICENSE_TEXT = "proquest_license";

	// Submission Sticky notes
	public final static String SUBMIT_PERSONAL_INFO_STICKIES = "submit_personal_info_stickyies";
	public final static String SUBMIT_DOCUMENT_INFO_STICKIES = "submit_document_info_stickyies";
	public final static String SUBMIT_UPLOAD_FILES_STICKIES = "submit_upload_files_stickyies";
	
	// application.conf configuration options from Vireo 3 (Play Framework)
	public final static String APPLICATION_BASE_URL = "application.baseUrl";
	public final static String APPLICATION_ATTACHMENTS_PATH = "attachments_path";
	public final static String APPLICATION_INDEX_PATH = "index.path";
	public final static String APPLICATION_DEPOSITS_PATH = "deposits.path";
	public final static String APPLICATION_MAIL_HOST = "mail.host";
	public final static String APPLICATION_MAIL_PORT = "mail.port";
	public final static String APPLICATION_MAIL_USER = "mail.user";
	public final static String APPLICATION_MAIL_PASSWORD = "mail.pass";
	public final static String APPLICATION_MAIL_CHANNEL = "mail.channel";
	public final static String APPLICATION_MAIL_PROTOCOL = "mail.protocol";
	public final static String APPLICATION_AUTH_FORCE_SSL = "auth.forceSSL"; 
	public final static String APPLICATION_AUTH_PASS_ENABLED = "auth.pass.enabled";
	public final static String APPLICATION_AUTH_PASS_VISIBLE = "auth.pass.visible";
	public final static String APPLICATION_AUTH_PASS_NAME = "auth.pass.name";
	public final static String APPLICATION_AUTH_PASS_DESCRIPTION = "auth.pass.description";
	public final static String APPLICATION_AUTH_SHIB_ENABLED = "auth.shib.enabled";
	public final static String APPLICATION_AUTH_SHIB_VISIBLE = "auth.shib.visible";
	public final static String APPLICATION_AUTH_SHIB_NAME = "auth.shib.name";
	public final static String APPLICATION_AUTH_SHIB_DESCRIPTION = "auth.shib.description";
	public final static String APPLICATION_AUTH_SHIB_LOGIN_FORCE_SSL = "auth.shib.login.forceSSL";
	public final static String APPLICATION_AUTH_SHIB_LOGIN_URL = "auth.shib.login.url";
	public final static String APPLICATION_AUTH_SHIB_LOGOUT_URL = "auth.shib.logout.url";
	public final static String APPLICATION_AUTH_SHIB_LOGOUT_ENABLED = "auth.shib.logout.enabled";
	public final static String APPLICATION_AUTH_SHIB_PRIMARY_IDENTIFIER = "auth.shib.primaryIdentifier";
	public final static String APPLICATION_AUTH_SHIB_MOCK = "auth.shib.mock";
	public final static String APPLICATION_AUTH_SHIB_LOG = "auth.shib.log";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID = "auth.shib.attribute.netid";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL = "auth.shib.attribute.email";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME = "auth.shib.attribute.firstName";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME = "auth.shib.attribute.lastName";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER = "auth.shib.attribute.institutionalIdentifier";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_MIDDLE_NAME = "auth.shib.attribute.middleName";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_BIRTH_YEAR = "auth.shib.attribute.birthYear";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_AFFILIATIONS = "auth.shib.attribute.affiliations";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_PHONE_NUMBER = "auth.shib.attribute.currentPhoneNumber";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_POSTAL_ADDRESS = "auth.shib.attribute.currentPostalAddress";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_EMAIL_ADDRESS = "auth.shib.attribute.currentEmailAddress";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_PHONE_NUMBER = "auth.shib.attribute.permanentPhoneNumber";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_POSTAL_ADDRESS = "auth.shib.attribute.permanentPostalAddress";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_PERMANENT_EMAIL_ADDRESS = "auth.shib.attribute.permanentEmailAddress";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_DEGREE = "auth.shib.attribute.currentDegree";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_DEPARTMENT = "auth.shib.attribute.currentDepartment";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_COLLEGE = "auth.shib.attribute.currentCollege";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_MAJOR = "auth.shib.attribute.currentMajor";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_GRADUATION_YEAR = "auth.shib.attribute.currentGraduationYear";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_CURRENT_GRADUATION_MONTH = "auth.shib.attribute.currentGraduationMonth";
	public final static String APPLICATION_AUTH_SHIB_ATTRIBUTE_ORCID = "auth.shib.attribute.orcid";
	public final static String APPLICATION_ADVISOR_AFFILIATION_RESTRICT = "advisor.affiliation.restrict";
	public final static String APPLICATION_SUBMIT_FIELD_LOCK = "submit.field.lock";
	public final static String APPLICATION_VIREO_VERSION = "vireo.version";
	public final static String APPLICATION_NAME = "application.name";
	public final static String APPLICATION_MODE = "application.mode";
	public final static String APPLICATION_DATE_FORMAT = "date.format";
	public final static String APPLICATION_FILESIZE_VALIDATE = "fileSize.validate";
	public final static String APPLICATION_FILESIZE_MAXSIZE = "fileSize.maxFileSize";
	public final static String APPLICATION_FILESIZE_MAXSIZETOTAL = "fileSize.maxFileSizeTotal";
	
	// Vireo 4 new Configuration Names
	public final static String APPLICATION_INSTALL_DIRECTORY = "install.dir";
	public final static String APPLICATION_MAIL_FROM = "mail.from";
	public final static String APPLICATION_MAIL_REPLYTO = "mail.replyto";
}
