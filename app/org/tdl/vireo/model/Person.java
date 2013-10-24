package org.tdl.vireo.model;

import java.util.List;

/**
 * The person object is used for both students, reviewers, and administrators
 * and is the central authentication object for Vireo. When one of these types
 * of users login to the system either an existing Person object is identified,
 * or a new person object is created. The one exception to this is for committee
 * chairs, they use a separate authentication system based upon an email hash. 
 * 
 * All of the student oriented attributes are optional, and will hopefully be
 * supplied by the originating institution either through Shibboleth, LDAP, or
 * another security mechanism that may be implemented. Since these values may
 * come from external sources a student's current attributes such as Department,
 * College, Major, etc may not be in the approved list of values for those
 * fields this is why the datatypes are uncontrolled string values. It is
 * intended when a student fills out their submission at that time if their
 * attributes differ from the approved list then they will have to select the
 * correct value from the list. 
 * 
 * Another important change from the previous version of Vireo is that the
 * students contact information has moved from the submission object onto the
 * person object. This will allow the student to keep this information
 * up-to-date across multiple submissions.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Person extends AbstractModel {

	/**
	 * @return The unique non-public identifier assigned by the institution for
	 *         this person.
	 */
	public String getNetId();

	/**
	 * @param netId
	 *            The new unique non-public identifier assigned by the
	 *            institution for this person.
	 */
	public void setNetId(String netId);

	/**
	 * @return The official email address assigned by the instutiton for this
	 *         person.
	 */
	public String getEmail();

	/**
	 * @param email
	 *            The new official email address assigned by the institution for
	 *            this person.
	 */
	public void setEmail(String email);
	
	/**
	 * It is highly recommended that all implementations store the password as a
	 * hash in any persistent storage mechanism.
	 * 
	 * @param password
	 *            Update this person's password to the new password. Or null to
	 *            remove a password from this person.
	 */
	public void setPassword(String password);

	/**
	 * @param password
	 *            The password to validate.
	 * @return True if the provided password matches the stored password for
	 *         this person. If the person's current password is null, then this
	 *         method will always return false.
	 */
	public boolean validatePassword(String password);
	
	/**
	 * @return The institutional identifier for this person.
	 */
	public String getInstitutionalIdentifier();
	
	/**
	 * @param identifier Set the institutional identifier for this person.
	 */
	public void setInstitutionalIdentifier(String identifier);

	/**
	 * @return The first name of the person.
	 */
	public String getFirstName();

	/**
	 * By the time this object is saved, either a first or a last name must
	 * exist on the person.
	 * 
	 * @param firstName
	 *            The new first name of the person.
	 */
	public void setFirstName(String firstName);

	/**
	 * @return The middle name of the person.
	 */
	public String getMiddleName();

	/**
	 * @param middleName
	 *            The new middle name of the person.
	 */
	public void setMiddleName(String middleName);

	/**
	 * By the time this object is saved, either a first or a last name must
	 * exist on the person.
	 * 
	 * @return The last name of the person.
	 */
	public String getLastName();

	/**
	 * 
	 * @param lastName
	 *            The new last name of the person.
	 */
	public void setLastName(String lastName);

	/**
	 * @param format
	 *            The format specifying how the name should be constructed.
	 * 
	 * @return The person's name according to the format specified.
	 */
	public String getFormattedName(NameFormat format);
	
	/**
	 * @return The preferred display name for the person. If no specific
	 *         displayName is specified then the results from getFullName() will
	 *         be returned instead.
	 */
	public String getDisplayName();

	/**
	 * 
	 * @param displayName
	 *            The new preferred display name for the person.
	 */
	public void setDisplayName(String displayName);

	/**
	 * 
	 * @return The birth year for the person.
	 */
	public Integer getBirthYear();

	/**
	 * @param year
	 *            The new birth year for the person.
	 */
	public void setBirthYear(Integer year);

	/**
	 * @return The list of affiliations this person has. Typically this is
	 *         things like "Student", "Staff", "Faculty", etc and are designated
	 *         by the authentication source.
	 */
	public List<String> getAffiliations();

	/**
	 * @param affiliation
	 *            The new affilation to add for this person.
	 */
	public void addAffiliation(String affiliation);

	/**
	 * 
	 * @param affiliation
	 *            Remove this affilation from this person.
	 */
	public void removeAffilation(String affiliation);

	/**
	 * @return The current phone number.
	 */
	public String getCurrentPhoneNumber();

	/**
	 * 
	 * @param phoneNumber
	 *            The new current phone number.
	 */
	public void setCurrentPhoneNumber(String phoneNumber);

	/**
	 * 
	 * @return The current postal address
	 */
	public String getCurrentPostalAddress();

	/**
	 * 
	 * @param postalAddress
	 *            The new current postal address.
	 */
	public void setCurrentPostalAddress(String postalAddress);

	/**
	 * 
	 * @return The current email address. If no specific email address is
	 *         specified here, then the user's primary email address (aka
	 *         getEmail()) will be returned.
	 */
	public String getCurrentEmailAddress();

	/**
	 * 
	 * @param email
	 *            The new current email address.
	 */
	public void setCurrentEmailAddress(String email);

	/**
	 * 
	 * @return The permanent phone number.
	 */
	public String getPermanentPhoneNumber();

	/**
	 * 
	 * @param phoneNumber
	 *            The new permanent phone number.
	 */
	public void setPermanentPhoneNumber(String phoneNumber);

	/**
	 * 
	 * @return The permanent postal address.
	 */
	public String getPermanentPostalAddress();

	/**
	 * 
	 * @param postalAddress
	 *            The new permanent postal address
	 */
	public void setPermanentPostalAddress(String postalAddress);

	/**
	 * 
	 * @return The permanent email address
	 */
	public String getPermanentEmailAddress();

	/**
	 * 
	 * @param email
	 *            The new permanent email address.
	 */
	public void setPermanentEmailAddress(String email);

	/**
	 * @return The current non-controlled degree.
	 */
	public String getCurrentDegree();

	/**
	 * 
	 * @param department
	 *            The new degree
	 */
	public void setCurrentDegree(String degree);
	
	/**
	 * @return The current non-controlled department.
	 */
	public String getCurrentDepartment();

	/**
	 * 
	 * @param department
	 *            The new department
	 */
	public void setCurrentDepartment(String department);

	/**
	 * @return The current non-controlled program.
	 */
	public String getCurrentProgram();
	
	/**
	 * @param program
	 * 			The new program
	 */
	public void setCurrentProgram(String program);
	
	/**
	 * 
	 * @return The current non-controlled college.
	 */
	public String getCurrentCollege();

	/**
	 * 
	 * @param college
	 *            The new college.
	 */
	public void setCurrentCollege(String college);

	/**
	 * 
	 * @return The current non-controlled major.
	 */
	public String getCurrentMajor();

	/**
	 * @param major
	 *            The new major.
	 */
	public void setCurrentMajor(String major);

	/**
	 * 
	 * @return The current graduation year.
	 */
	public Integer getCurrentGraduationYear();

	/**
	 * @param year
	 *            The new graduation year.
	 */
	public void setCurrentGraduationYear(Integer year);

	/**
	 * 
	 * @return The current non-controlled graduation month.
	 */
	public Integer getCurrentGraduationMonth();

	/**
	 * 
	 * @param month
	 *            The new graduation month.
	 */
	public void setCurrentGraduationMonth(Integer month);

	/**
	 * @return return all the preferences for this person.
	 */
	public List<Preference> getPreferences();
	
	/**
	 * Find the preference with the given name for this user.
	 * 
	 * @param name The name of the preference
	 * @return The preference
	 */
	public Preference getPreference(String name);
	
	/**
	 * Create a new preference for this person.
	 * @param name The name of the new preference.
	 * @param value The value of the new preference.
	 * @return The new preference object for convenience.
	 */
	public Preference addPreference(String name, String value);
	
	
	/**
	 * 
	 * @return Return the vireo role of this person.
	 */
	public RoleType getRole();

	/**
	 * 
	 * @param role
	 *            The new role of this person.
	 */
	public void setRole(RoleType role);

	
	/**
	 * @param orcid
	 * 			The orcid id of this person.
	 */
	public void setOrcid(String orcid);
	
	/**
	 * 
	 * @return Return the orcid id of this person.
	 */
	public String getOrcid();
}
