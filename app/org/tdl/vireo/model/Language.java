package org.tdl.vireo.model;

import java.util.Locale;

/**
 * This class represents a language available for documents to be submitted under..
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
	 * 			Set the name/locale of this language
	 */
	public void setName(String name);
	
	
	/**
	 * @return the locale of the language.
	 */
	public Locale getLocale();

}
