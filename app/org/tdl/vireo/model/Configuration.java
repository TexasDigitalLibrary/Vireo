package org.tdl.vireo.model;

import java.util.HashMap;
import java.util.Map;

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
	
	// Theme settings
	/** Instructions to show on the front page of Vireo. */
	public final static String FRONT_PAGE_INSTRUCTIONS = "front_page_instructions";
	
	/** Instructions to show after completing a submission */
	public final static String SUBMIT_INSTRUCTIONS = "submit_instructions";

	/** Instructions to show after submitting corrections */
	public final static String CORRECTION_INSTRUCTIONS = "correction_instructions";
	
	/**
	 * Configuration defaults
	 * 
	 * Any component may register system-wide defaults for configuration
	 * parameters. Whenever a call to settingRepo.getConfigValue(name) is
	 * called, if that parameter is not defined then the value registered with
	 * DEFAULTS is returned.
	 */
	public static class DEFAULTS {

		// The singleton DEFAULTS map.
		protected static final Map<String, String> singleton = new HashMap<String,String>();

		private DEFAULTS() {
			/** We're a singleton instance **/
		}

		/**
		 * Return the default value for the provided configuration parameter.
		 * 
		 * @param name
		 *            The name of the configuration parameter.
		 * @return The value, or null if none registered.
		 */
		public static String get(String name) {
			return singleton.get(name);
		}

		/**
		 * Register a default value.
		 * 
		 * @param name
		 *            The name of the parameter.
		 * @param value
		 *            The default value of the parameter.
		 */
		public static void register(String name, String value) {
			singleton.put(name, value);
		}

		/**
		 * Un-Register a default value.
		 * 
		 * @param name
		 *            The name of the parameter to unregister.
		 */
		public static void unregister(String name) {
			singleton.remove(name);
		}
	} // DEFAULTS class
	
}
