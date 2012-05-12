package org.tdl.vireo.model;

/**
 * The possible months each year when degrees are awarded.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface GraduationMonth extends AbstractOrderedModel {
	
	/**
	 * @return the month, integer 0 ... 1 where 0 = January and 11 = December.
	 */
	public int getMonth();
	
	/**
	 * @return The english name of the month, i.e: January, February, ... December.
	 */
	public String getMonthName();
	
	/**
	 * @return the new month.
	 */
	public void setMonth(int month);

}
