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
	/** If defined then the student's birth year will be requested */
	@Deprecated
	public final static String SUBMIT_REQUEST_BIRTH = "submit_request_birth";
	
	/** If defined then the college parameter will be requested */
	@Deprecated
	public final static String SUBMIT_REQUEST_COLLEGE = "submit_request_college";
	
	/** If defined then the UMI Release parameter will be requested */
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
	/** Make administrative staff email address available to students. **/
	public final static String EMAIL_SHOW_ADDRESSES = "email_shown_addresses";
	
	/** CC the student's advisor as soon as student submits their work. **/
	public final static String EMAIL_CC_ADVISOR = "email_cc_advisor";
	
	/** CC the student whenever the system sent an email to someone else. **/
	public final static String EMAIL_CC_STUDENT = "email_cc_student";
	
	// Theme settings
	/** Instructions to show on the front page of Vireo. */
	public final static String FRONT_PAGE_INSTRUCTIONS = "front_page_instructions";
	
	/** Instructions to show after completing a submission */
	public final static String SUBMIT_INSTRUCTIONS = "submit_instructions";

	/** Instructions to show after submitting corrections */
	public final static String CORRECTION_INSTRUCTIONS = "correction_instructions";
	
	
	
	// Submission Sticky notes
	public final static String SUBMIT_PERSONAL_INFO_STICKIES = "submit_personal_info_stickyies";
	public final static String SUBMIT_DOCUMENT_INFO_STICKIES = "submit_document_info_stickyies";
	public final static String SUBMIT_UPLOAD_FILES_STICKIES = "submit_upload_files_stickyies";
	
}
