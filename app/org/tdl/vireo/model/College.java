package org.tdl.vireo.model;

/**
 * This class represents colleges which may award degree status by Vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface College extends AbstractOrderedModel {

	/**
	 * @return The name of the college
	 */
	public String getName();

	/**
	 * @param name
	 *            Set the new name of this college.
	 */
	public void setName(String name);

}
