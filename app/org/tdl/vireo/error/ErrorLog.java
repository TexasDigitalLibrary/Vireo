package org.tdl.vireo.error;

import java.util.List;

import org.tdl.vireo.job.JobMetadata;

import play.mvc.Http.Request;

/**
 * The error report log keeps track of recent error reports for display with
 * Vireo. When an error is encountered and not able to be handled then it should
 * be reported to the ErrorLog. Later an administrator is able to retrieve
 * information about recent errors from the log. The log will only keep a
 * specific number of recent logs, they may be pushed out of the log at anytime.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */

public interface ErrorLog {

	/**
	 * Report an error.
	 * 
	 * @param exception
	 *            The exception
	 * @param request
	 *            The request generating the error (aka the where).
	 * @return The ErrorReport.
	 */
	public ErrorReport logError(Throwable exception, Request request);
	
	/**
	 * Report an error.
	 * 
	 * @param exception
	 *            The exception
	 * @param job
	 *            The job generating the error (aka the where)
	 * @return The ErrorReport.
	 */
	public ErrorReport logError(Throwable exception, JobMetadata job);
	
	/**
	 * Report an error.
	 * 
	 * @param exception
	 *            The exception
	 * @param where
	 *            A message indicating where the error occured.
	 * @return The ErrorReport.
	 */
	public ErrorReport logError(Throwable exception, String where);
	
	/**
	 * Report an error.
	 * 
	 * @param exception
	 *            The exception
	 * @return The ErrorReport.
	 */
	public ErrorReport logError(Throwable exception);	
	
	
	/**
	 * @return A list of recent error reports.
	 */
	public List<ErrorReport> getRecentErrorReports();
	
	/**
	 * Clear out the list of recent errors;
	 */
	public void clearRecentErrorReports();
	
	
}
