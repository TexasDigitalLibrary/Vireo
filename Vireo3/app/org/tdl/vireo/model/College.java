package org.tdl.vireo.model;

import java.util.HashMap;

/**
 * This class represents colleges which may award degree status by Vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface College extends EmailGroup {

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
