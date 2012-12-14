package org.tdl.vireo.proquest;

/**
 * 
 * A Proquest authorized degree.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 *
 */
public interface ProquestDegree {

	/**
	 * @return The official code of this proquest degree.
	 */
	public String getCode();
	
	/**
	 * @return The official description of this proquest degree.
	 */
	public String getDescription();
	
}
