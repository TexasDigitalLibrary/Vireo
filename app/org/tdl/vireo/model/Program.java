package org.tdl.vireo.model;

/**
 * This class represents programs.
 * 
 * @author Micah Cooper
 */
public interface Program extends AbstractOrderedModel {

	/**
	 * @return The name of the program
	 */
	public String getName();

	/**
	 * @param name
	 *            Set the new name of this program.
	 */
	public void setName(String name);

}
