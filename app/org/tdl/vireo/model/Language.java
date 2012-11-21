package org.tdl.vireo.model;

/**
 * This class represents languages to be used with Proquest.
 * 
 * @author Micah Cooper
 */
public interface Language extends AbstractOrderedModel {
	
	/**
	 * @return The name of the language
	 */
	public String getName();
	
	/**
	 * @param name
	 * 			Set the name of this language
	 */
	public void setName(String name);

}
