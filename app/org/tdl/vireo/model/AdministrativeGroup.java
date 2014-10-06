package org.tdl.vireo.model;

import java.util.HashMap;

/**
 * This class represents an administrative group to be used with email workflow rules.
 * 
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 */
public interface AdministrativeGroup extends EmailGroup {

	/**
	 * @return The name of the administrative group
	 */
	public String getName();

	/**
	 * @param name
	 *            Set the new name of this administrative group.
	 */
	public void setName(String name);

}
