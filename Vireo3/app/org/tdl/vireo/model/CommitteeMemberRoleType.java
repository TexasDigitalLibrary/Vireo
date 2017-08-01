package org.tdl.vireo.model;


/**
 * This class represents roles committee members may have on a student's committee.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface CommitteeMemberRoleType extends AbstractOrderedModel {

	/**
	 * @return The name of this role.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of this role.
	 */
	public void setName(String name);

	/**
	 * @return The level of this role.
	 */
	public DegreeLevel getLevel();

	/**
	 * @param level
	 *            The new level of this role.
	 */
	public void setLevel(DegreeLevel level);

}
