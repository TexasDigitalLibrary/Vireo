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
	
	/** The degree awarding institution.  */
	public final static String GRANTOR = "grantor";
	
	// Submission Settings
	/** If defined then the student's birth year will be requested */
	public final static String SUBMIT_REQUEST_BIRTH = "submit_request_birth";
	
	/** If defined then the college parameter will be requested */
	public final static String SUBMIT_REQUEST_COLLEGE = "submit_request_college";
	
	/** If defined then the UMI Release parameter will be requested */
	public final static String SUBMIT_REQUEST_UMI = "submit_request_umi";
	
	/** Instructions show to uses after completing their submission */
	public final static String SUBMIT_INSTRUCTIONS = "submit_instructions";

	/** The license students must agree to during submission  */
	public final static String SUBMIT_LICENSE = "submit_license";
	
	// Application Settings
	/** If defined then submissions are open, otherwise they are closed. **/
	public final static String SUBMISSIONS_OPEN = "submissions_open";
	
	/** Text string describing the current semester for which submissions are being accepted. **/
	public final static String CURRENT_SEMESTER = "current_semester";
	
	/** If defined then multiple submissions are allowed. */
	public final static String ALLOW_MULTIPLE_SUBMISSIONS = "allow_multiple_submissions";
	
	// Email settings
	/** Make administrative staff email address available to students. **/
	public final static String EMAIL_SHOW_ADDRESSES = "email_shown_addresses";
	
	/** CC the student's advisor as soon as student submits their work. **/
	public final static String EMAIL_CC_ADVISOR = "email_cc_advisor";
	
	/** CC the student whenever the system sent an email to someone else. **/
	public final static String EMAIL_CC_STUDENT = "email_cc_student";
	
	public final static String DEFAULT_SUBMIT_INSTRUCTIONS = 
			"The Thesis Office has received your electronic submittal. You will also receive an email confirmation. We will check your records as soon as possible to determine whether or not we have the signed Approval Form on file. Please be aware that your file is not complete and cannot be reviewed until we have both the electronic manuscript and the signed Approval Form. \n"+
			"\n" +
			"As soon as both items have been received, your manuscript will be placed in the queue and will be processed along with all other submissions for the semester in the order in which your completed file (manuscript and Approval Form) was received.\n"+
			"\n" +
			"The following are approximate turn-around times after the manuscript and the signed approval form have been submitted to the Thesis Office. Manuscripts are reviewed in the order received.\n"+
			"\n" +
			"Early in semester – 5 working days\n" +
			"Week before Deadline Day – 10 working days\n" +
			"Deadline Day – 15 working days\n" +
			"\n"+
			"If you have any questions about your submittal, feel free to contact our office. \n" +
			"\n" +
			"Thank you,\n" +
			"\n" +
			"Thesis Office\n";
	
	public final static String DEFAULT_SUBMIT_LICENSE = 
			"I grant the Texas Digital Library (hereafter called \"TDL\"), my home institution (hereafter called \"Institution\"), and my academic department (hereafter called \"Department\") the non-exclusive rights to copy, display, perform, distribute and publish the content I submit to this repository (hereafter called \"Work\") and to make the Work available in any format in perpetuity as part of a TDL, Institution or Department repository communication or distribution effort.\n" +
			"\n" +
			"I understand that once the Work is submitted, a bibliographic citation to the Work can remain visible in perpetuity, even if the Work is updated or removed.\n" +
			"\n" +
			"I understand that the Work's copyright owner(s) will continue to own copyright outside these non-exclusive granted rights.\n" +
			"\n" +
			"I warrant that:\n" +
			"\n" +
			"    1) I am the copyright owner of the Work, or\n" +
			"    2) I am one of the copyright owners and have permission from the other owners to submit the Work, or\n" +
			"    3) My Institution or Department is the copyright owner and I have permission to submit the Work, or\n" +
			"    4) Another party is the copyright owner and I have permission to submit the Work.\n" +
			"\n" +
			"Based on this, I further warrant to my knowledge:\n" +
			"\n" +
			"    1) The Work does not infringe any copyright, patent, or trade secrets of any third party,\n" +
			"    2) The Work does not contain any libelous matter, nor invade the privacy of any person or third party, and\n" +
			"    3) That no right in the Work has been sold, mortgaged, or otherwise disposed of, and is free from all claims.\n" +
			"\n" +
			"I agree to hold TDL, Institution, Department, and their agents harmless for any liability arising from any breach of the above warranties or any claim of intellectual property infringement arising from the exercise of these non-exclusive granted rights.\n"+
			"\n";
	
}
