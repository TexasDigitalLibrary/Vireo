package org.tdl.vireo.constant;

/**
 * Common Application Preferences Names.
 * 
 * Stored with each person are preference objects consisting of name / value
 * pairs. These preferences can determine how particular parts of the
 * application respond during specific circumstances. The static values defined
 * here are the common definitions for those preferences parameters. This allows
 * these names to be defined in one location while being statically linked
 * throughout the application.
 * 
 * Future developers, feel free to add additional parameters to this class as
 * needed.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class AppPref {

	/** When sending any emails, CC the current user. **/
	public final static String CC_EMAILS = "cc_emails";
	
	/** When adding a note, should the student be emailed by default */
	public final static String NOTES_EMAIL_STUDENT = "notes_email_student_by_default";
	
	/** When adding a note, should the student's advisor be cc by default */
	public final static String NOTES_CC_ADVISOR = "notes_cc_student_advisor_by_default";
	
	/** When adding a note, should the submission be flagged as needs corrections by default */
	public final static String NOTES_FLAG_NEEDS_CORRECTIONS = "notes_flag_submission_as_needs_corrections_by_default";
	
	/** When adding a note, should it be marked as private by default */
	public final static String NOTES_MARK_PRIVATE = "notes_mark_comment_as_private_by_default";
	
	
	
	/** When adding an attachment, should the an email be send to the student by default */
	public final static String ATTACHMENT_EMAIL_STUDENT = "attachment_email_student_by_default";
	
	/** When adding an attachment, should the advisor be cc'ed by default */
	public final static String ATTACHMENT_CC_ADVISOR = "attachment_cc_student_advisor_by_default";
	
	/** When adding an attachment, should the submission be flagged as needs corrections by default */
	public final static String ATTACHMENT_FLAG_NEEDS_CORRECTIONS = "attachment_flag_submission_as_needs_corrections_by_default";
	
}
