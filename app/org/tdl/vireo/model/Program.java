package org.tdl.vireo.model;

import java.util.HashMap;

/**
 * This class represents programs.
 * 
 * @author Micah Cooper
 * @author Jeremy Huff, huff@library.tamu.edu
 * 
 */
public interface Program extends EmailGroup {

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
