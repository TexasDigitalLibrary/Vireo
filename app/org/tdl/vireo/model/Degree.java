package org.tdl.vireo.model;


/**
 * This class represents degrees which may be awarded by Vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Degree extends AbstractOrderedModel {

	/**
	 * @return The name of this degree.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of this degree.
	 */
	public void setName(String name);

	/**
	 * @return The level of this degree.
	 */
	public DegreeLevel getLevel();

	/**
	 * @param level
	 *            The new level of this degree.
	 */
	public void setLevel(DegreeLevel level);

}