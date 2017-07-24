package org.tdl.vireo.error;

import java.util.Date;

/**
 * 
 * Interface recording an error that has been encountered. The report records
 * the user who encountered the error, a message about the exception, and the
 * stack trace of the exception.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface ErrorReport {

	/**
	 * @return When the error occured
	 */
	public Date getWhen();

	/**
	 * @return Who encountered the error
	 */
	public String getWho();

	/**
	 * @return Where the error was encountered
	 */
	public String getWhere();

	/**
	 * @return What the error is
	 */
	public String getWhat();

	/**
	 * @return The stack trace of the error
	 */
	public String getStackTrace();
}
