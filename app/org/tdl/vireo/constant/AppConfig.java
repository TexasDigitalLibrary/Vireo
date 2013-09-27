package org.tdl.vireo.constant;

/**
 * Common Application Configuration Names.
 * 
 * Stored in the SettingsRepository there are global configuration objects
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
public class AppConfig {

	// Submission Settings
	/** If defined then the student's birth year will be requested. This field is no longer used, see FieldConfig */
	@Deprecated
	public final static String SUBMIT_REQUEST_BIRTH = "submit_request_birth";
	
	/** If defined then the college parameter will be requested. This field is no longer used, see FieldConfig */
	@Deprecated
	public final static String SUBMIT_REQUEST_COLLEGE = "submit_request_college";
	
	/** If defined then the UMI Release parameter will be requested. This field is no longer used, see FieldConfig */
	@Deprecated
	public final static String SUBMIT_REQUEST_UMI = "submit_request_umi";
	
	
	
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
		
	// Email settings
	public final static String EMAIL_DELAY_SENDING_ADVISOR_REQUEST = "email_delay_advisor_request";
	
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
	public final static String CUSTOM_CSS = "custom_css";
	
	/** Instructions to show on the front page of Vireo. */
	public final static String FRONT_PAGE_INSTRUCTIONS = "front_page_instructions";
	
	/** Instructions to show after completing a submission */
	public final static String SUBMIT_INSTRUCTIONS = "submit_instructions";

	/** Instructions to show after submitting corrections */
	public final static String CORRECTION_INSTRUCTIONS = "correction_instructions";
	
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
	
	
	// Degree code mapping
	/** The prefix underwhich all degree codes are stored under. Use the getDegreeCodeConfig() method to resolve the name for each degree */
	public final static String DEGREE_CODE_PREFIX = "degree_code";
	
	/**
	 * Return the configuration name which identifies the abbreviated degree code for a particular degree.
	 * 
	 * @param degree The full name of the degree (i.e. "Doctor of Philosophy")
	 * @return The configuration name of the code.
	 */
	public final static String getDegreeCodeConfig(String degree) {
		if (degree == null)
			return null;
		
		return DEGREE_CODE_PREFIX + "_" + degree.replaceAll(" ", "_").toLowerCase();
	}
	
}
