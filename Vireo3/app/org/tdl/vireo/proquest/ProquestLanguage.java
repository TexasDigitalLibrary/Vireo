package org.tdl.vireo.proquest;

/**
 * 
 * A Proquest authorized language.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 *
 */
public interface ProquestLanguage {

	/**
	 * @return The official code of this proquest language.
	 */
	public String getCode();
	
	/**
	 * @return The official description of this proquest language.
	 */
	public String getDescription();
	
}
