package org.tdl.vireo.model;

/**
 * This class represents majors which may award degree status by Vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 *
 */
public interface Major extends AbstractOrderedModel {
	
	/**
	 * @return The name of the major.
	 */
	public String getName();
	
	/**
	 * @param name The new name of the major.
	 */
	public void setName(String name);

}
