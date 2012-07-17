package org.tdl.vireo.model;

/**
 * A system wide configuration for vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Configuration extends AbstractModel {

	/**
	 * @return The name of the configuration.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of the configuration.
	 */
	public void setName(String name);

	/**
	 * @return The value of the configuration
	 */
	public String getValue();

	/**
	 * @param value
	 *            The new value of the configuration.
	 */
	public void setValue(String value);
	
	
	/** Common configuration **/
	
	// Application Settings
	/** If defined then submissions are open, otherwise they are closed. **/
	public final static String SUBMISSIONS_OPEN = "submissions_open";
	
	/** Text string describing the current semester for which submissions are being accepted. **/
	public final static String CURRENT_SEMESTER = "current_semester";
	
	/** If defined then multiple submissions are allowed. */
	public final static String ALLOW_MULTIPLE_SUBMISSIONS = "allow_multiple_submissions";
	
	/** If defined then the college parameter will be requested */
	public final static String REQUEST_COLLEGE = "request_college";
	
	/** If defined then the UMI Release parameter will be requested */
	public final static String REQUEST_UMI = "request_umi";
	
	/** Instructions show to uses after completing their submission */
	public final static String SUBMISSION_INSTRUCTIONS = "submission_instructions";
	
	
	// Email settings
	/** Make administrative staff email address available to students. **/
	public final static String EMAIL_SHOW_ADDRESSES = "email_shown_addresses";
	
	/** CC the student's advisor as soon as student submits their work. **/
	public final static String EMAIL_CC_ADVISOR = "email_cc_advisor";
	
	/** CC the student whenever the system sent an email to someone else. **/
	public final static String EMAIL_CC_STUDENT = "email_cc_student";
	
}
