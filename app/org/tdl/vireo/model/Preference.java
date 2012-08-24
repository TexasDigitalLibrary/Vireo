package org.tdl.vireo.model;

/**
 * A personal preference associated with a single person.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Preference extends AbstractModel {

	/**
	 * @return The person associated with this preference.
	 */
	public Person getPerson();

	/**
	 * @return The name of the preference.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of the preference.
	 */
	public void setName(String name);

	/**
	 * @return The value of the preference
	 */
	public String getValue();

	/**
	 * @param value
	 *            The new value of the preference.
	 */
	public void setValue(String value);
	
	
	
	/** Common preferences **/
	
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
