package org.tdl.vireo.model;

/**
 * This class represents the departments which may award degree status by Vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Department extends AbstractOrderedModel {
	
	/**
	 * @return The name of the department
	 */
	public String getName();
	
	/**
	 * @param name The new name of the department
	 */
	public void setName(String name);

}
