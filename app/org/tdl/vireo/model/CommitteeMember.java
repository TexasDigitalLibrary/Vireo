package org.tdl.vireo.model;

import java.util.List;

/**
 * A member of a student's degree committee.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface CommitteeMember extends AbstractOrderedModel {

	/**
	 * @return the submission this committee member belongs too.
	 */
	public Submission getSubmission();

	/**
	 * @return The first name of the committee member
	 */
	public String getFirstName();

	/**
	 * @param firstName
	 *            The new first name of the committee member
	 */
	public void setFirstName(String firstName);

	/**
	 * @return The last name of the committee member.
	 */
	public String getLastName();

	/**
	 * @param lastName
	 *            The new last name of the committee member.
	 */
	public void setLastName(String lastName);

	/**
	 * @return The middle name of the committee member.
	 */
	public String getMiddleName();

	/**
	 * @param middleName
	 *            The new middle name of the committee member.
	 */
	public void setMiddleName(String middleName);
	
	/**
	 * @return The list of all roles this committee member has. If no roles have
	 *         been assigned then an empty list is returned.
	 */
	public List<String> getRoles();

	/**
	 * Add a new role for this committee member. The new role will be appeneded
	 * to the list of roles.
	 * 
	 * @param role
	 *            The new role for the committee member.
	 */
	public void addRole(String role);

	/**
	 * Remove and old role from this committee member. The first occurrence of
	 * the role will be removed.
	 * 
	 * @param role
	 *            The role to add.
	 */
	public void removeRole(String role);
	
	/**
	 * Return true if the member has at least one of the listed roles.
	 * 
	 * @param role
	 *            A variable argument list of roles
	 * @return True if this member has one of the specified roles.
	 */
	public boolean hasRole(String... roles);

	/**
	 * Return true if this committee member has no special roles. Typical this
	 * indicates the person is just a committee member.
	 * 
	 * @return true if there are no roles.
	 */
	public boolean hasNoRole();
	
	/**
	 * 
	 * 
	 * @return a string containing all the roles associated with this member as
	 *         a comma separated list.
	 */
	public String getFormattedRoles();

	/**
	 * @param format
	 *            The format specifying how the name should be constructed.
	 * 
	 * @return The committee member's name according to the format specified.
	 */
	public String getFormattedName(NameFormat format);

}
