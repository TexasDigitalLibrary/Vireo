package org.tdl.vireo.model;

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
	 * @return the full name (first, middle, and last names) of this committee
	 *         member.
	 */
	public String getFullName();
	
	/**
	 * @return True if this committee member is a chair or co-chair of the
	 *         student's degree committee.
	 */
	public boolean isCommitteeChair();

	/**
	 * @param chair
	 *            Set weather this committee member is a chair or co-chair of the
	 *            student's degree committee.
	 */
	public void setCommitteeChair(boolean chair);

}
