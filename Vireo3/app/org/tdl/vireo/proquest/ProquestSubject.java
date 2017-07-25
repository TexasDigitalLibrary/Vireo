package org.tdl.vireo.proquest;

/**
 * A ProQuest authorized subject.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface ProquestSubject {
	
	/**
	 * @return The official code of this proquest subject.
	 */
	public String getCode();
	
	/**
	 * @return The official description of this proquest subject.
	 */
	public String getDescription();
	
}
